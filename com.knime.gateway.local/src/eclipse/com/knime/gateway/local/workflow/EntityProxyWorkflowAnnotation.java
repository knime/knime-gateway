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

import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.AnnotationData.StyleRange;
import org.knime.core.node.workflow.AnnotationData.TextAlignment;
import org.knime.core.node.workflow.WorkflowAnnotation;

import com.knime.gateway.v0.entity.AnnotationEnt;
import com.knime.gateway.v0.entity.StyleRangeEnt.FontStyleEnum;
import com.knime.gateway.v0.entity.WorkflowAnnotationEnt;

/**
 * Entity-proxy class that proxies {@link WorkflowAnnotationEnt} and extends {@link WorkflowAnnotation}.
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityProxyWorkflowAnnotation extends WorkflowAnnotation implements EntityProxy<WorkflowAnnotationEnt> {

    private final EntityProxyAccess m_clientProxyAccess;

    private WorkflowAnnotationEnt m_entity;

    /**
     * Creates a new entity proxy class. Deriving classes MUST never be instantiated directly but use the
     * {@link EntityProxyAccess} in order to get/create instances.
     *
     * @param entity the entity instance
     * @param clientProxyAccess the entity proxy access to access/create (new) entity proxy instances
     */
    EntityProxyWorkflowAnnotation(final WorkflowAnnotationEnt entity, final EntityProxyAccess clientProxyAccess) {
        super(getAnnotationData(entity));
        m_entity = entity;
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

    @Override
    public void update(final WorkflowAnnotationEnt entity) {
        m_entity = entity;
    }

    @Override
    public void postUpdate() {
        //nothing to be done here
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

}
