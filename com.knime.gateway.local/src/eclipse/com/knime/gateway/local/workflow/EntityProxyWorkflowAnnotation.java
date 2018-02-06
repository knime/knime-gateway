/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Nov 27, 2017 (hornm): created
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
