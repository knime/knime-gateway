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
package org.knime.gateway.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.commons.lang3.function.FailableBiFunction;

public final class Functional {

    private Functional() {

    }

    /**
     * Apply a mapping function {@code S -> T} to the list. The mapping function additionally receives the result of its
     * application to the previous element in the list. The mapping function may throw exceptions of type {@code E}.
     */
    public static <S, T, E extends Throwable> List<T> mapWithPrevious(List<S> list,
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

    public static <E> Optional<E> getOptional(List<E> list, int index) {
        try {
            return Optional.of(list.get(index));
        } catch (IndexOutOfBoundsException e) { // NOSONAR
            return Optional.empty();
        }
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
    public static <E> List<List<E>> foldAppend(List<E> identity, Iterable<E> list) {
        var result = new ArrayList<List<E>>();
        var accumulator = new ArrayList<>(identity);
        for (var element : list) {
            accumulator.add(element);
            result.add(accumulator.stream().toList());
        }
        return result;
    }

    /**
     * Given two lists [ x1, x2, x3 ] and [ y1, y2, y3 ], yields
     * [ {x1, y1}, {x2, y2}, {x3, y3} ]
     */
    public static <S, T> List<Entry<S, T>> zip(List<S> list, List<T> otherList) {
        return IntStream.range(0, Math.min(list.size(), otherList.size()))
            .mapToObj(i -> new Entry<>(list.get(i), otherList.get(i))).toList();
    }

    public record Entry<S, T>(S key, T value) {

    }
}
