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

import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.PatchOpEnt.OpEnum;
import com.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;

/**
 * Helper class to help apply changes made to a {@link WorkflowEnt} represented by a {@link PatchEnt}.
 *
 * For each type of change the respective method in {@link WorkflowEntChangeListener} is called.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
class WorkflowEntChangeProcessor {

    private WorkflowEntChangeProcessor() {
        //utility class
    }

    /**
     * The name of the property of a workflow entity holding the connections map.
     */
    private static final String CONNECTIONS_PROPERTY = "connections";

    /**
     * The name of the property of a workflow entity holding the nodes map.
     */
    private static final String NODES_PROPERTY = "nodes";

    /**
     * The name of the property of a workflow entity holding the annotations map.
     */
    private static final String ANNOTATIONS_PROPERTY = "workflowAnnotations";

    /**
     * Process the changes given by the patch and calls the respective methods of the provided
     * {@link WorkflowEntChangeListener}.
     *
     * @param patch changes to process if there are no changes or patch is <code>null</code> - immediate return
     * @param oldEnt the old workflow before the given patch has been applied
     * @param newEnt the new workflow of the application of the given patch
     * @param l callbacks for the respective changes
     */
    static void processChanges(final PatchEnt patch, final WorkflowEnt oldEnt, final WorkflowEnt newEnt,
        final WorkflowEntChangeListener l) {
        if(patch == null || patch.getOps().size() == 0) {
            return;
        }

        //update removed connection container
        patch.getOps().stream()
            .filter(o -> o.getOp() == OpEnum.REMOVE && o.getPath().startsWith("/" + CONNECTIONS_PROPERTY))
            .forEach(o -> {
                String[] path = o.getPath().split("/");
                ConnectionEnt ent = oldEnt.getConnections().get(path[path.length - 1]);
                l.connectionEntRemoved(ent);
            });

        //update added connection container
        patch.getOps().stream()
            .filter(o -> o.getOp() == OpEnum.ADD && o.getPath().startsWith("/" + CONNECTIONS_PROPERTY)).forEach(o -> {
                String[] path = o.getPath().split("/");
                ConnectionEnt ent = newEnt.getConnections().get(path[path.length - 1]);
                l.connectionEntAdded(ent);
            });

        //update removed node container
        patch.getOps().stream().filter(o -> o.getOp() == OpEnum.REMOVE && o.getPath().startsWith("/" + NODES_PROPERTY))
            .forEach(o -> {
                String[] path = o.getPath().split("/");
                NodeEnt ent = oldEnt.getNodes().get(path[path.length - 1]);
                l.nodeEntRemoved(ent);
            });

        //update added node container
        patch.getOps().stream().filter(o -> o.getOp() == OpEnum.ADD && o.getPath().startsWith("/" + NODES_PROPERTY))
            .forEach(o -> {
                String[] path = o.getPath().split("/");
                NodeEnt ent = newEnt.getNodes().get(path[path.length - 1]);
                l.nodeEntAdded(ent);
            });

        //update removed worklfow annotations
        patch.getOps().stream()
            .filter(o -> o.getOp() == OpEnum.REMOVE && o.getPath().startsWith("/" + ANNOTATIONS_PROPERTY))
            .forEach(o -> {
                String[] path = o.getPath().split("/");
                WorkflowAnnotationEnt ent = oldEnt.getWorkflowAnnotations().get(path[path.length - 1]);
                l.annotationEntRemoved(ent);
            });

        //update added workflow annotations
        patch.getOps().stream()
            .filter(o -> o.getOp() == OpEnum.ADD && o.getPath().startsWith("/" + ANNOTATIONS_PROPERTY))
            .forEach(o -> {
                String[] path = o.getPath().split("/");
                WorkflowAnnotationEnt ent = newEnt.getWorkflowAnnotations().get(path[path.length - 1]);
                l.annotationEntAdded(ent);
            });


    }

    /**
     * Callsbacks for changes made to a {@link WorkflowEnt}.
     */
    interface WorkflowEntChangeListener {

        public void nodeEntAdded(NodeEnt newNode);

        public void nodeEntRemoved(NodeEnt removedNode);

        public void connectionEntAdded(ConnectionEnt newConnection);

        public void connectionEntRemoved(ConnectionEnt removedConnection);

        public void annotationEntAdded(WorkflowAnnotationEnt newAnno);

        public void annotationEntRemoved(WorkflowAnnotationEnt removedAnno);
    }
}
