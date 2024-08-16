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
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import org.apache.commons.lang3.function.FailableBiFunction;
import org.apache.commons.lang3.function.FailableFunction;

/**
 * @implNote The tree is assumed to remain reasonably small, if this is used for larger amounts of data, methods should
 *           be reviewed w.r.t their complexity.
 * @param <K> The type used for lookups in this tree
 * @param <V> The type of values stored in this tree
 */
@SuppressWarnings({ //
    "java:S3242", // more general type for method parameter possible
    "java:S1602" // curly braces around lambdas
})
class Tree<K, V extends Tree.TreeNode<K, V>> {

    private final V m_root;

    /**
     * Create node for this tree.
     *
     * @implNote Not an abstract method to enable testing this class independently.
     */
    interface TreeNodeCreator<K, V> extends FailableFunction<TreeInsertionContext<K, V>, V, TreeNodeCreationException> {
        @Override
        V apply(TreeInsertionContext<K, V> context) throws TreeNodeCreationException;
    }

    record TreeInsertionContext<K, V>(Optional<V> parent, List<K> path) {

    }

    /**
     * Initialise a tree.
     */
    public Tree(final V root) {
        m_root = root;
    }

    /**
     * Traverse the tree along the {@code path}, inserting child nodes until {@code path} is fully contained in the
     * tree. If the path is already fully contained in the tree, this corresponds to a simple lookup. If creation of any
     * new child nodes fails, this method throws and does not modify the tree.
     *
     * @param path List of {@link K}eys to traverse the tree along.
     * @param createTreeNode Supplier to create new nodes
     * @return The {@link V}alues at the given {@code path} (possibly newly created).
     * @throws TreeNodeCreationException if a child node could not be created
     */
    @SuppressWarnings({"java:S1941"}) // early declaration of local variables
    public V getOrGrowBranchAlong(final List<K> path, final TreeNodeCreator<K, V> createTreeNode)
        throws TreeNodeCreationException {
        // Suppose tree contains path [x1, x2]
        // path = [x1, x2, x3, x4, x5]
        var difference = difference(path);
        // difference.contained() = [x1, x2]
        // difference.notContained = [x3, x4, x5]
        // foldAppend(difference.notContained()) = [ [x3], [x3, x4], [x3, x4, x5] ]
        // pathsToInsert = [ [x1, x2, x3], [x1, x2, x3, x4], [x1, x2, x3, x4, x5] ]
        var pathsToInsert = foldAppend(difference.contained(), difference.notContained());
        var keysToInsert = difference.notContained();
        // try creating all values to insert beforehand. If any fails, this method throws.
        List<V> valuesToInsert = mapWithPrevious( //
            pathsToInsert, //
            (previouslyCreatedValue, pathToInsert) -> { //
                return createTreeNode.apply(new TreeInsertionContext<>(previouslyCreatedValue, pathToInsert)); // throws
            } //
        );
        // need to keep the keys in order to link values
        var toInsert = zipToEntry(keysToInsert, valuesToInsert);
        // link the created values s.t. the next is a child of the previous, producing a new branch
        var newBranch = mapWithNext(toInsert, (current, maybeNext) -> {
            maybeNext.ifPresent(next -> current.value().children().put(next.key(), next.value()));
            return current;
        });
        // attach the new branch to the tree
        var leafInTree = get(difference.contained()).orElseThrow(); // contained in tree by definition of `difference`
        if (!newBranch.isEmpty()) {
            // if `path` is already fully contained in tree, all operations above collapse to maps on empty lists
            var firstOfNewBranch = newBranch.get(0);
            leafInTree.children().put(firstOfNewBranch.key(), firstOfNewBranch.value());
        }
        // return the leaf of the new branch
        return get(path).orElseThrow(); // now fully contained in tree
    }

    /**
     * Apply a mapping function S -> T to the list. The mapping function additionally receives the result of its
     * application to the previous element in the list. The mapping function may throw.
     */
    private static <S, T, E extends Throwable> List<T> mapWithPrevious(List<S> list,
        FailableBiFunction<Optional<T>, S, T, E> mapper) throws E {
        var result = new ArrayList<T>();
        for (var index = 0; index < list.size(); index++) {
            var previous = getOptional(result, index);
            var current = list.get(index);
            var mapped = mapper.apply(previous, current); // potentially throws and exits
            result.add(mapped);
        }
        return result;
    }

    private static <E> List<E> mapWithNext(List<E> list, BiFunction<E, Optional<E>, E> mapper) {
        return IntStream.range(0, list.size()).mapToObj(i -> {
            var current = list.get(i);
            var next = getOptional(list, i + 1);
            return mapper.apply(current, next);
        }).toList();
    }

    private static <E> Optional<E> getOptional(List<E> list, int index) {
        try {
            return Optional.of(list.get(index));
        } catch (IndexOutOfBoundsException e) { // NOSONAR
            return Optional.empty();
        }
    }

    /**
     * Split the given path into two parts:
     * <ul>
     * <li>The prefix that is contained in the tree</li>
     * <li>The suffix that is not contained in the tree</li>
     * </ul>
     */
    private Difference<K> difference(List<K> queryPath) {
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

    private record Difference<V>(List<V> contained, List<V> notContained) {

    }

    private static <S, T> List<Entry<S, T>> zipToEntry(List<S> keys, List<T> values) {
        return IntStream.range(0, Math.min(keys.size(), values.size()))
            .mapToObj(i -> new Entry<>(keys.get(i), values.get(i))).toList();
    }

    private record Entry<S, T>(S key, T value) {

    }

    /**
     * Left fold the append operator over a list. Given a list of <code>[x1, x2, x3, ...]</code> and identity value of
     * <code>[y1, y2]</code>, the result is a list of lists <code>
     * [
     *  [y1, y2, x1],
     *  [y1, y2, x1, x2],
     *  [y1, y2, x1, x2, x3],
     *  ...
     * ]
     * </code>
     */
    private List<List<K>> foldAppend(List<K> identity, List<K> list) {
        var result = new ArrayList<List<K>>();
        var accumulator = new ArrayList<>(identity);
        for (var element : list) {
            accumulator.add(element);
            result.add(accumulator.stream().toList());
        }
        return result;
    }

    /**
     * @param path List of {@link K}eys to traverse the tree along.
     * @return The {@link V}alue at the given {@code path}, or an empty optional if there is no value at that path in
     *         the tree.
     */

    public Optional<V> get(final List<K> path) {
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

    static class TreeNodeCreationException extends Exception {
    }
}
