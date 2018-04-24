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
package com.knime.gateway.local.util;

import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;

import com.knime.gateway.util.DefaultEntUtil;

/**
 * Collection of entity proxy helper methods
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityProxyUtil {
    private EntityProxyUtil() {
        // utility class
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
}
