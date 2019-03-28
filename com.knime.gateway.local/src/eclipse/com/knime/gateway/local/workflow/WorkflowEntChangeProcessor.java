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

import java.util.Arrays;
import java.util.List;

import com.knime.gateway.entity.ConnectionEnt;
import com.knime.gateway.entity.NodeEnt;
import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.entity.PatchOpEnt;
import com.knime.gateway.entity.WorkflowAnnotationEnt;
import com.knime.gateway.entity.WorkflowEnt;

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
    static synchronized void processChanges(final PatchEnt patch, final WorkflowEnt oldEnt, final WorkflowEnt newEnt,
        final WorkflowEntChangeListener l) {
        if (patch == null || patch.getOps().size() == 0) {
            return;
        }

        patch.getOps().forEach(o -> {
            switch (o.getOp()) {
                case ADD:
                    ADD_PROCESSORS.forEach(p -> p.process(o.getPath(), oldEnt, newEnt, l));
                    break;
                case REMOVE:
                    REMOVE_PROCESSORS.forEach(p -> p.process(o.getPath(), oldEnt, newEnt, l));
                    break;
                case REPLACE:
                    REPLACE_PROCESSORS.forEach(p -> p.process(o.getPath(), oldEnt, newEnt, l));
                default:
                    break;
            }
        });
    }

    private static List<PatchOpProcessor> ADD_PROCESSORS = Arrays.asList((p, oldEnt, newEnt, l) -> {
        if (p.startsWith("/" + CONNECTIONS_PROPERTY)) {
            String[] path = p.split("/");
            ConnectionEnt ent = newEnt.getConnections().get(path[path.length - 1]);
            l.connectionEntAdded(ent);
        }
    }, (p, oldEnt, newEnt, l) -> {
        if (p.startsWith("/" + NODES_PROPERTY)) {
            String[] path = p.split("/");
            NodeEnt ent = newEnt.getNodes().get(path[path.length - 1]);
            l.nodeEntAdded(ent);
        }
    }, (p, oldEnt, newEnt, l) -> {
        if (p.startsWith("/" + ANNOTATIONS_PROPERTY)) {
            String[] path = p.split("/");
            WorkflowAnnotationEnt ent = newEnt.getWorkflowAnnotations().get(path[path.length - 1]);
            l.annotationEntAdded(ent);
        }
    });

    private static List<PatchOpProcessor> REMOVE_PROCESSORS = Arrays.asList((p, oldEnt, newEnt, l) -> {
        if (p.startsWith("/" + CONNECTIONS_PROPERTY)) {
            String[] path = p.split("/");
            ConnectionEnt ent = oldEnt.getConnections().get(path[path.length - 1]);
            l.connectionEntRemoved(ent);
        }
    }, (p, oldEnt, newEnt, l) -> {
        if (p.startsWith("/" + NODES_PROPERTY)) {
            String[] path = p.split("/");
            NodeEnt ent = oldEnt.getNodes().get(path[path.length - 1]);
            l.nodeEntRemoved(ent);
        }
    }, (p, oldEnt, newEnt, l) -> {
        if (p.startsWith("/" + ANNOTATIONS_PROPERTY)) {
            String[] path = p.split("/");
            WorkflowAnnotationEnt ent = oldEnt.getWorkflowAnnotations().get(path[path.length - 1]);
            l.annotationEntRemoved(ent);
        }
    });

    private static List<PatchOpProcessor> REPLACE_PROCESSORS = Arrays.asList((p, oldEnt, newEnt, l) -> {
        if (p.startsWith("/" + CONNECTIONS_PROPERTY)) {
            String[] path = p.split("/");
            ConnectionEnt newConn = newEnt.getConnections().get(path[path.length - 2]);
            ConnectionEnt oldConn = oldEnt.getConnections().get(path[path.length - 2]);
            l.connectionEntReplaced(oldConn, newConn);
        }
    });

    @FunctionalInterface
    interface PatchOpProcessor {
        /**
         * Processes one {@link PatchOpEnt}
         *
         * @param path the patch-op path
         * @param oldEnt the old workflow entity before applying the patch
         * @param newEnt the new workflow entity after the patch has been applied
         * @param l the callbacks
         */
        void process(String path, WorkflowEnt oldEnt, WorkflowEnt newEnt, WorkflowEntChangeListener l);
    }

    /**
     * Callbacks for changes made to a {@link WorkflowEnt}.
     */
    interface WorkflowEntChangeListener {

        public void nodeEntAdded(NodeEnt newNode);

        public void nodeEntRemoved(NodeEnt removedNode);

        public void connectionEntAdded(ConnectionEnt newConnection);

        public void connectionEntRemoved(ConnectionEnt removedConnection);

        public void connectionEntReplaced(ConnectionEnt oldConnection, ConnectionEnt newConnection);

        public void annotationEntAdded(WorkflowAnnotationEnt newAnno);

        public void annotationEntRemoved(WorkflowAnnotationEnt removedAnno);
    }
}
