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
 */
package org.knime.gateway.impl.webui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @param <K> The type used for lookups in this tree
 * @param <V> The type of values stored in this tree
 */
@SuppressWarnings({"java:S119", "javadoc"})
class Tree<K, V extends Tree.TreeNode<K, V>> {

    private final V m_root;

    private final TreeNodeCreator<K, V> m_createNode;

    /**
     * Create node for this tree.
     *
     * @implNote Not an abstract method to enable testing this class independently.
     */
    interface TreeNodeCreator<K, V> {
        V get(List<K> path) throws NodeCreationException;
    }

    /**
     * Initialise a tree.
     */
    public Tree(final V root, final TreeNodeCreator<K, V> createNode) {
        m_root = root;
        m_createNode = createNode;
    }

    /**
     * Traverse the tree along the {@code path}, inserting child nodes until {@code path} is fully contained in the
     * tree.
     *
     * @param path List of {@link K}eys to traverse the tree along.
     * @return The {@link V}alues at the given {@code path} (possibly newly created).
     * @throws NodeCreationException if a child node could not be created
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    public V getOrInsert(final Iterable<K> path) throws NodeCreationException {
        var currentValue = m_root;
        var currentFullPath = new ArrayList<K>();
        for (var id : path) {
            currentFullPath.add(id);
            if (currentValue.children().containsKey(id)) {
                currentValue = currentValue.children().get(id);
            } else {
                var newValue = m_createNode.get(currentFullPath); // might throw
                currentValue.children().put(id, newValue);
                currentValue = newValue;
            }
        }
        return currentValue;
    }

    /**
     * @param path List of {@link K}eys to traverse the tree along.
     * @return The {@link V}alue at the given {@code path}, or an empty optional if there is no value at that path in
     *         the tree.
     */

    public Optional<V> get(final Iterable<K> path) {
        var currentValue = m_root;
        for (var id : path) {
            if (!currentValue.children().containsKey(id)) {
                return Optional.empty();
            }
            currentValue = currentValue.children().get(id);
        }
        return Optional.of(currentValue);
    }

    public V root() {
        return m_root;
    }

    interface TreeNode<K, V> {

        /**
         * Has to be mutable
         */
        Map<K, V> children();
    }

    static class NodeCreationException extends Exception {
    }
}
