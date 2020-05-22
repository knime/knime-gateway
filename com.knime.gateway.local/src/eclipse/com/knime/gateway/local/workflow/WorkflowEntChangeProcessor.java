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

import static java.util.regex.Pattern.compile;
import static org.knime.core.util.Pair.create;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.knime.core.util.Pair;

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
     * Pattern to match the property of a workflow entity holding the connections map.
     */
    private static final Pattern CONNECTIONS = compile("/connections/.*");

    /**
     * Pattern to match the property of a workflow entity holding the nodes map.
     */
    private static final Pattern NODES = compile("/nodes/root:[:|\\d]+");

    /**
     * Pattern to match the property of a in port.
     */
    private static final Pattern PORTS = compile("/nodes/root[:|\\d]*/(inPorts|outPorts)/\\d+");

    /**
     * Pattern to match the property of a workflow entity holding the annotations map.
     */
    private static final Pattern ANNOTATIONS = compile("/workflowAnnotations/.*");

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
                    process(ADD_PROCESSORS, o.getPath(), oldEnt, newEnt, l);
                    break;
                case REMOVE:
                    process(REMOVE_PROCESSORS, o.getPath(), oldEnt, newEnt, l);
                    break;
                case REPLACE:
                    process(REPLACE_PROCESSORS, o.getPath(), oldEnt, newEnt, l);
                    break;
                default:
                    break;
            }
        });
    }

    private static void process(final List<Pair<Pattern, PatchOpProcessor>> processors, final String path,
        final WorkflowEnt oldEnt, final WorkflowEnt newEnt, final WorkflowEntChangeListener l) {
        for (Pair<Pattern, PatchOpProcessor> p : processors) {
            if (p.getFirst().matcher(path).matches()) {
                p.getSecond().process(path, oldEnt, newEnt, l);
                return;
            }
        }
    }

    private static List<Pair<Pattern, PatchOpProcessor>> ADD_PROCESSORS =
        Arrays.asList(create(CONNECTIONS, (p, oldEnt, newEnt, l) -> {
            String[] path = p.split("/");
            ConnectionEnt ent = newEnt.getConnections().get(path[path.length - 1]);
            l.connectionEntAdded(ent);
        }), create(NODES, (p, oldEnt, newEnt, l) -> {
            String[] path = p.split("/");
            NodeEnt ent = newEnt.getNodes().get(path[path.length - 1]);
            l.nodeEntAdded(ent);
        }), create(ANNOTATIONS, (p, oldEnt, newEnt, l) -> {
            String[] path = p.split("/");
            WorkflowAnnotationEnt ent = newEnt.getWorkflowAnnotations().get(path[path.length - 1]);
            l.annotationEntAdded(ent);
        }), create(PORTS, (p, oldEnt, newEnt, l) -> {
            String[] path = p.split("/");
            NodeEnt node = newEnt.getNodes().get(path[2]);
            l.nodePortsChanged(node);
        }));

    private static List<Pair<Pattern, PatchOpProcessor>> REMOVE_PROCESSORS =
        Arrays.asList(create(CONNECTIONS, (p, oldEnt, newEnt, l) -> {
            String[] path = p.split("/");
            ConnectionEnt ent = oldEnt.getConnections().get(path[path.length - 1]);
            l.connectionEntRemoved(ent);
        }), create(NODES, (p, oldEnt, newEnt, l) -> {
            String[] path = p.split("/");
            NodeEnt ent = oldEnt.getNodes().get(path[path.length - 1]);
            l.nodeEntRemoved(ent);
        }), create(ANNOTATIONS, (p, oldEnt, newEnt, l) -> {
            String[] path = p.split("/");
            WorkflowAnnotationEnt ent = oldEnt.getWorkflowAnnotations().get(path[path.length - 1]);
            l.annotationEntRemoved(ent);
        }), create(PORTS, (p, oldEnt, newEnt, l) -> {
            String[] path = p.split("/");
            NodeEnt node = newEnt.getNodes().get(path[2]);
            l.nodePortsChanged(node);
        }));

    private static List<Pair<Pattern, PatchOpProcessor>> REPLACE_PROCESSORS =
        Arrays.asList(create(CONNECTIONS, (p, oldEnt, newEnt, l) -> {
            String[] path = p.split("/");
            ConnectionEnt newConn = newEnt.getConnections().get(path[path.length - 2]);
            ConnectionEnt oldConn = oldEnt.getConnections().get(path[path.length - 2]);
            l.connectionEntReplaced(oldConn, newConn);
        }));

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

        void nodeEntAdded(NodeEnt newNode);

        void nodeEntRemoved(NodeEnt removedNode);

        void connectionEntAdded(ConnectionEnt newConnection);

        void connectionEntRemoved(ConnectionEnt removedConnection);

        void connectionEntReplaced(ConnectionEnt oldConnection, ConnectionEnt newConnection);

        void annotationEntAdded(WorkflowAnnotationEnt newAnno);

        void annotationEntRemoved(WorkflowAnnotationEnt removedAnno);

        void nodePortsChanged(NodeEnt node);

    }
}
