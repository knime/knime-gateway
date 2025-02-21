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

package org.knime.gateway.api.util;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.util.ClassUtils;

/**
 * Value type that can hold either of two type instances.
 */
public final class Either<L, R> {

    L m_left;

    R m_right;

    private Either(final L left, final R right) {
        m_left = left;
        m_right = right;
    }

    /**
     *
     * @param left
     * @return An instance containing value of type {@link L}
     * @param <L>
     * @param <R>
     */
    public static <L, R> Either<L, R> left(final L left) {
        return new Either<>(left, null);
    }

    /**
     *
     * @param right
     * @return An instance containing value of type {@link R}
     * @param <L>
     * @param <R>
     */
    public static <L, R> Either<L, R> right(final R right) {
        return new Either<>(null, right);
    }

    /**
     *
     * @return an instance containing no value
     * @param <L>
     * @param <R>
     */
    public static <L, R> Either<L, R> empty() {
        return new Either<>(null, null);
    }

    /**
     *
     * @param query
     * @return the value of type {@code Z} if it is set, otherwise an empty optional.
     * @param <Z>
     */
    public <Z> Optional<Z> get(final Class<Z> query) {
        return Stream.of(m_left, m_right) //
            .filter(query::isInstance) //
            .map(e -> ClassUtils.castOptional(query, e)) //
            .flatMap(Optional::stream) //
            .findFirst();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        var either = (Either<?, ?>)other;
        return new EqualsBuilder().append(m_left, either.m_left).append(m_right, either.m_right).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(m_left).append(m_right).toHashCode();
    }

    @Override
    public String toString() {
        var value = m_left != null ? m_left : m_right;
        if (value == null) {
            return "Either[empty]";
        }
        return "Either[%s]".formatted(value);
    }
}
