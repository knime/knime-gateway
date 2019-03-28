/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.local.workflow;

import static com.knime.gateway.entity.EntityBuilderManager.builder;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.eclipse.swt.widgets.Display;
import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.AnnotationData.TextAlignment;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.ui.node.workflow.async.AsyncNodeContainerUI;
import org.knime.core.ui.node.workflow.async.AsyncWorkflowAnnotationUI;

import com.knime.gateway.entity.AnnotationEnt;
import com.knime.gateway.entity.BoundsEnt;
import com.knime.gateway.entity.BoundsEnt.BoundsEntBuilder;
import com.knime.gateway.entity.StyleRangeEnt.FontStyleEnum;
import com.knime.gateway.entity.WorkflowAnnotationEnt;
import com.knime.gateway.service.util.ServiceExceptions.NotASubWorkflowException;
import com.knime.gateway.service.util.ServiceExceptions.NotFoundException;

/**
 * Entity-proxy class that proxies {@link WorkflowAnnotationEnt} and extends {@link WorkflowAnnotation}.
 *
 * @author Martin Horn, University of Konstanz
 */
class EntityProxyWorkflowAnnotation extends WorkflowAnnotation
    implements EntityProxy<WorkflowAnnotationEnt>, AsyncWorkflowAnnotationUI {

    private final EntityProxyAccess m_clientProxyAccess;

    private WorkflowAnnotationEnt m_entity;

    private UUID m_rootWorkflowID;

    private String m_parentNodeID;

    private WorkflowAnnotationEnt m_oldEntity;

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param entity
     * @param rootWorkflowID the id of the root workflow
     * @param clientProxyAccess
     */
    EntityProxyWorkflowAnnotation(final WorkflowAnnotationEnt entity, final UUID rootWorkflowID, final String parentNodeID,
        final EntityProxyAccess clientProxyAccess) {
        super(getAnnotationData(entity));
        m_entity = entity;
        m_rootWorkflowID = rootWorkflowID;
        m_parentNodeID = parentNodeID;
        m_clientProxyAccess = clientProxyAccess;
    }

    @Override
    public WorkflowAnnotationEnt getEntity() {
        return m_entity;
    }

    @Override
    public EntityProxyAccess getAccess() {
        return m_clientProxyAccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowAnnotationID getID() {
        //parse id from property in entity
        //TODO cache
        return getAccess().getAnnotationID(m_rootWorkflowID, getEntity().getAnnotationID());
    }

    @Override
    public void update(final WorkflowAnnotationEnt entity) {
        m_oldEntity = m_entity;
        m_entity = entity;
    }

    @Override
    public void postUpdate() {
        if(!Objects.equals(m_entity.getBounds(), m_oldEntity.getBounds())) {
            BoundsEnt b = m_entity.getBounds();
            Display.getDefault().syncExec(() -> {
                setDimension(b.getX(), b.getY(), b.getWidth(), b.getHeight());
            });
        }
    }

    static AnnotationData getAnnotationData(final AnnotationEnt annoEnt) {
        StyleRange[] styleRanges = annoEnt.getStyleRanges().stream().map(sr -> {
            StyleRange newSR = new StyleRange();
            newSR.setStart(sr.getStart());
            newSR.setLength(sr.getLength());
            newSR.setFontName(sr.getFontName());
            newSR.setFontSize(sr.getFontSize());
            newSR.setFgColor(sr.getForegroundColor());
            newSR.setFontStyle(getFontStyleIdx(sr.getFontStyle()));
            return newSR;
        }).toArray(StyleRange[]::new);
        AnnotationData ad = new AnnotationData();
        ad.setText(annoEnt.getText());
        ad.setX(annoEnt.getBounds().getX());
        ad.setY(annoEnt.getBounds().getY());
        ad.setBgColor(annoEnt.getBackgroundColor());
        ad.setBorderColor(annoEnt.getBorderColor());
        ad.setBorderSize(annoEnt.getBorderSize());
        ad.setDefaultFontSize(annoEnt.getDefaultFontSize());
        ad.setHeight(annoEnt.getBounds().getHeight());
        ad.setWidth(annoEnt.getBounds().getWidth());
        ad.setAlignment(TextAlignment.valueOf(annoEnt.getTextAlignment()));
        ad.setStyleRanges(styleRanges);
        return ad;
    }

    static int getFontStyleIdx(final FontStyleEnum fontStyle) {
        //indices from SWT-class
        switch (fontStyle) {
            case NORMAL:
                return 0;
            case BOLD:
                return 1;
            case ITALIC:
                return 2;
            default:
                //return normal style by default
                return 0;
        }
    }

    @Override
    public CompletableFuture<Void> setDimensionAsync(final int x, final int y, final int width, final int height) {
        BoundsEnt bounds = builder(BoundsEntBuilder.class).setX(x).setY(y).setWidth(width).setHeight(height).build();
        return AsyncNodeContainerUI.future(() -> {
            try {
                getAccess().annotationService().setAnnotationBoundsInSubWorkflow(m_rootWorkflowID, m_parentNodeID,
                    m_entity.getAnnotationID(), bounds);
                return null;
            } catch (NotFoundException | NotASubWorkflowException ex) {
                //should never happen
                throw new CompletionException(ex);
            }
        });
    }
}
