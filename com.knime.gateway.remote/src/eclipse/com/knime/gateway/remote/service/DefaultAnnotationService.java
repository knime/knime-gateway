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
package com.knime.gateway.remote.service;

import static com.knime.gateway.remote.service.util.DefaultServiceUtil.entityToAnnotationID;
import static com.knime.gateway.remote.service.util.DefaultServiceUtil.getWorkflowManager;

import java.util.UUID;

import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;

import com.knime.gateway.entity.AnnotationIDEnt;
import com.knime.gateway.entity.BoundsEnt;
import com.knime.gateway.service.AnnotationService;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.service.util.ServiceExceptions.NotASubWorkflowException;
import com.knime.gateway.service.util.ServiceExceptions.NotFoundException;

/**
 * Default implementation of {@link AnnotationService} that delegates the operations to knime.core (e.g.
 * {@link WorkflowAnnotation} etc.).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultAnnotationService implements AnnotationService {
    private static final DefaultAnnotationService INSTANCE = new DefaultAnnotationService();

    private DefaultAnnotationService() {
        //private constructor since it's a singleton
    }

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultAnnotationService getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAnnotationBounds(final UUID rootWorkflowID, final AnnotationIDEnt annoID,
        final BoundsEnt bounds) throws NotFoundException, NotASubWorkflowException {
        try {
            WorkflowManager wfm = getWorkflowManager(rootWorkflowID, annoID.getNodeIDEnt());
            WorkflowAnnotation workflowAnnotation =
                wfm.getWorkflowAnnotations(entityToAnnotationID(rootWorkflowID, annoID))[0];
            if (workflowAnnotation == null) {
                throw new NotFoundException("Annotation for id '" + annoID + "' not found");
            }
            workflowAnnotation.setDimension(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());

        } catch (NodeNotFoundException ex) {
            throw new NotFoundException("Workflow for id '" + annoID.getNodeIDEnt() + "' not found", ex);
        }
    }
}