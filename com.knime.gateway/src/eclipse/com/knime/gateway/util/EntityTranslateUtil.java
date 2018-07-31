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
package com.knime.gateway.util;

import java.util.function.Function;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;

import com.knime.gateway.v0.entity.WorkflowPartsEnt;

/**
 * Helper methods to translate gateway entities into KNIME-core objects.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityTranslateUtil {

    private EntityTranslateUtil() {
        //utility class
    }

    /**
     * Translates {@link WorkflowPartsEnt} into {@link WorkflowCopyContent}.
     *
     * @param ent the entity
     * @param string2NodeID function that translates a string into a {@link NodeID}-instance
     * @param string2AnnotationID function that tranlates a string into a {@link WorkflowAnnotationID}-instance.
     * @return the newly created translation result
     */
    public static WorkflowCopyContent translateWorkflowPartsEnt(final WorkflowPartsEnt ent,
        final Function<String, NodeID> string2NodeID,
        final Function<String, WorkflowAnnotationID> string2AnnotationID) {
        return WorkflowCopyContent.builder()
            .setNodeIDs(ent.getNodeIDs().stream().map(s -> string2NodeID.apply(s)).toArray(size -> new NodeID[size]))
            .setAnnotationIDs(ent.getAnnotationIDs().stream().map(s -> string2AnnotationID.apply(s))
                .toArray(size -> new WorkflowAnnotationID[size]))
            .build();
    }
}
