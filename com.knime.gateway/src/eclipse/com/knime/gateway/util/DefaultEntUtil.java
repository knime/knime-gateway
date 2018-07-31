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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.node.workflow.ConnectionID;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.WorkflowAnnotationID;

/**
 * Utility methods used by the default entity implementations.
 *
 * @author Martin Horn, University of Konstanz
 * @noreference This class is not intended to be referenced by clients.
 */
public final class DefaultEntUtil {

    /**
     * Node id of the root node.
     */
    public static final String ROOT_NODE_ID = "root";

    private DefaultEntUtil() {
        // utility class
    }

    /**
     * Turns an object into an immutable one (if not already).
     *
     * @param obj the object to treat
     * @return the object itself or a immutable copy
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> T immutable(final T obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof Map) {
            return (T)Collections.unmodifiableMap(new HashMap((Map)obj));
        } else if (obj instanceof List) {
            return (T)Collections.unmodifiableList(new ArrayList((List)obj));
        } else {
            return obj;
        }
    }

    /**
     * Unifies the conversion from a node id to a string. The root id is either removed or replaced by 'root' if the
     * node id consist of the root id only.
     *
     * @param nodeID a node ID, must not be <code>null</code>
     * @return the node ID without the root as a string, or <tt>root</tt>
     */
    public static String nodeIDToString(final NodeID nodeID) {
        String s = nodeID.toString();
        int index = s.indexOf(":");
        return (index >= 0) ? s.substring(index + 1) : DefaultEntUtil.ROOT_NODE_ID;
    }


    /**
     * Unifies the conversion from a string to the node id.
     * A root node id is prepended.
     *
     * @param rootID the root node id to be prepended
     * @param nodeID the actual node id or 'root' if the root id
     * @return the node id as {@link NodeID} object with the root node id prepended
     */
    public static NodeID stringToNodeID(final String rootID, final String nodeID) {
        if (DefaultEntUtil.ROOT_NODE_ID.equals(nodeID)) {
            return NodeID.fromString(rootID);
        } else {
            return NodeIDSuffix.fromString(nodeID).prependParent(NodeID.fromString(rootID));
        }
    }

    /**
     * @param connectionId
     * @return the connection id as string formatted as '<dest-node-id>_<dest-port-idx>'
     */
    public static String connectionIDToString(final ConnectionID connectionId) {
        String destNodeId = nodeIDToString(connectionId.getDestinationNode());
        return connectionIDToString(destNodeId, connectionId.getDestinationPort());
    }

    /**
     * @param destNodeID
     * @param destPortIdx
     * @return the connection id as string formatted as '<dest-node-id>_<dest-port-idx>'
     */
    public static String connectionIDToString(final String destNodeID, final int destPortIdx) {
        return String.format("%1$s_%2$d", destNodeID, destPortIdx);
    }

    /**
     * @param id
     * @return the annotation id as string formatted as '<node-id>_<index>'
     */
    public static String annotationIDToString(final WorkflowAnnotationID id) {
        String nodeid = nodeIDToString(id.getNodeID());
        return String.format("%1$s_%2$d", nodeid, id.getIndex());
    }
}
