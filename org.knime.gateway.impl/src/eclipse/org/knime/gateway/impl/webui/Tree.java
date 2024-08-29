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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.knime.gateway.impl.util.ListFunctions;

/**
 * @implNote The tree is assumed to remain reasonably shallow, if this is used for deeper trees, methods should
 *           be reviewed w.r.t their complexity.
 * @param <K> The type used for lookups in this tree
 * @param <V> The type of values stored in this tree
 */
@SuppressWarnings({ //
    "java:S3242" // more general type for method parameter possible
})
class Tree<K, V extends Tree.TreeNode<K, V>> {

    private final V m_root;

    /**
     * Initialize a tree.
     */
    Tree(final V root) {
        m_root = root;
    }

    /**
     * A sequence of linked tree nodes.
     */
    static class Branch<K, V extends Tree.TreeNode<K,V>> {

        private final ArrayDeque<V> m_values;

        private final K m_firstKey;

        Branch(final List<K> keys, final List<V> values) {
            m_values = new ArrayDeque<>();
            m_firstKey = keys.get(0);
            ListFunctions.zip(keys, values)
                    .forEach(toInsert -> this.insert(toInsert.getKey(), toInsert.getValue())
            );
        }

        private void insert(final K key, final V value) {
            var previous = Optional.ofNullable(m_values.peekLast());
            previous.ifPresent(prev -> prev.addChild(key, value));
            m_values.addLast(value);
        }

        V firstValue() {
            return m_values.peekFirst();
        }

        K firstKey() {
            return m_firstKey;
        }

        V lastValue() {
            return  m_values.peekLast();
        }
    }

    /**
     * Split the given path into two parts:
     * <ul>
     * <li>The prefix that is contained in the tree</li>
     * <li>The suffix that is not contained in the tree</li>
     * </ul>
     */
    Difference<K> difference(final List<K> queryPath) {
        var queue = new ArrayDeque<>(queryPath);
        var contained = new ArrayList<K>();
        var currentValue = m_root;
        while (!queue.isEmpty()) {
            if (currentValue.children().containsKey(queue.peek())) {
                var key = queue.pop();
                contained.add(key);
                currentValue = currentValue.children().get(key);
            } else {
                break;
            }
        }
        return new Difference<>(contained, queue.stream().toList());
    }

    record Difference<V>(List<V> contained, List<V> notContained) {

    }

    /**
     * @param path List of {@code K}eys to traverse the tree along.
     * @return The {@code V}alue at the given {@code path}, or an empty optional if there is no value at that path in
     *         the tree.
     */

    Optional<V> get(final List<K> path) {
        var currentValue = m_root;
        for (var id : path) {
            if (!currentValue.children().containsKey(id)) {
                return Optional.empty();
            }
            currentValue = currentValue.children().get(id);
        }
        return Optional.of(currentValue);
    }

    V root() {
        return m_root;
    }

    interface TreeNode<K, V extends TreeNode<K, V>> {

        /**
         * @return A mutable (!) map of all the child tree nodes
         */
        Map<K, V> children();

        default void attach(final Branch<K,V> branch) {
            this.addChild(branch.firstKey(), branch.firstValue());
        }

        default void addChild(final K key, final V value) {
            this.children().put(key, value);
        }
    }

}
