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

import com.knime.gateway.entity.AnnotationIDEnt;
import com.knime.gateway.entity.ConnectionIDEnt;
import com.knime.gateway.entity.NodeIDEnt;

/**
 * Utility methods used by the default entity implementations, tests etc. and to deal with other entity related stuff.
 *
 * @author Martin Horn, University of Konstanz
 * @noreference This class is not intended to be referenced by clients.
 */
public final class EntityUtil {

    private EntityUtil() {
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
     * Helper to more conveniently create a list of {@link NodeIDEnt}s.
     *
     * @param ids 2-dim array where the first dim is the list dimension, the second the nested node ids
     * @return the new list of {@link NodeIDEnt}s
     */
    public static List<NodeIDEnt> createNodeIDEntList(final int[][] ids) {
        List<NodeIDEnt> res = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            res.add(new NodeIDEnt(ids[i]));
        }
        return res;
    }

    /**
     * Helper to more conveniently create a list of {@link AnnotationIDEnt}s.
     *
     * @param nodeIds all node ids
     * @param indices all annotation indices
     * @return the new list of {@link AnnotationIDEnt}s
     */
    public static List<AnnotationIDEnt> createAnnotationIDEntList(final int[][] nodeIds, final int... indices) {
        List<AnnotationIDEnt> res = new ArrayList<AnnotationIDEnt>();
        if (nodeIds.length != indices.length) {
            throw new IllegalArgumentException("array of node ids and indices must be of same length");
        }
        for (int i = 0; i < nodeIds.length; i++) {
            NodeIDEnt id = new NodeIDEnt(nodeIds[i]);
            res.add(new AnnotationIDEnt(id, indices[i]));
        }
        return res;
    }

    /**
     * Helper to more conveniently create a list of {@link ConnectionIDEnt}s.
     *
     * @param nodeIds all destination node ids
     * @param indices all destination port indices
     * @return the new list of {@link ConnectionIDEnt}s
     */
    public static List<ConnectionIDEnt> createConnectionIDEntList(final int[][] nodeIds, final int... indices) {
        List<ConnectionIDEnt> res = new ArrayList<>();
        if (nodeIds.length != indices.length) {
            throw new IllegalArgumentException("array of node ids and indices must be of same length");
        }
        for (int i = 0; i < nodeIds.length; i++) {
            NodeIDEnt id = new NodeIDEnt(nodeIds[i]);
            res.add(new ConnectionIDEnt(id, indices[i]));
        }
        return res;
    }

}
