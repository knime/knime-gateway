/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 */
package org.knime.gateway.api.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.knime.core.node.workflow.NodeContainerMetadata.Link;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.LinkEnt.LinkEntBuilder;

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
        List<AnnotationIDEnt> res = new ArrayList<>();
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

    /**
     * Converts a list of {@link Link} to a list of {@link LinkEnt}.
     *
     * @param links The list of {@link Link}
     * @return The list of {@link LinkEnt}
     */
    public static List<LinkEnt> toLinkEnts(final List<Link> links) {
        return links.stream()//
            .map(EntityUtil::toLinkEnt)//
            .collect(Collectors.toList());
    }

    private static LinkEnt toLinkEnt(final Link link) {
        return builder(LinkEntBuilder.class)//
            .setUrl(link.url())//
            .setText(link.text())//
            .build();
    }

}
