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
package org.knime.gateway.impl.service.util;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Utilities around performing operations lazily.
 */
@SuppressWarnings("java:S119")
public final class Lazy {

    private Lazy() {

    }

    /**
     * A lazy initializer with the capability to reset it.
     * 
     * @implNote Not thread-safe.
     * @param <V> The type of the provided value.
     */
    public static final class Init<V> {
        private final Supplier<V> m_supplier;

        private V m_value;

        public Init(Supplier<V> supplier) {
            this.m_supplier = supplier;
        }

        /**
         * Obtain the value. Initialise it if not already done, otherwise return the previously initialised cached
         * value.
         *
         * @return the value
         */
        public V initialised() {
            if (m_value == null) {
                m_value = m_supplier.get();
            }
            return m_value;
        }

        /**
         * Clear the cached value. The next call to {@link #initialised()} will trigger a new initialisation.
         */
        public void clear() {
            m_value = null;
        }
    }

    /**
     * Lazy transformation of the wrapped value.
     * 
     * @implNote Not thread-safe.
     * @param <V> The type of the contained value.
     */
    public static class Transform<V> {
        private final UnaryOperator<V> m_transformation;

        private final V m_value;

        private final Init<V> m_transformed;

        public Transform(V value, UnaryOperator<V> transformation) {
            this.m_transformation = transformation;
            this.m_value = value;
            this.m_transformed = new Init<>(() -> m_transformation.apply(m_value));
        }

        public V original() {
            return m_value;
        }

        public V transformed() {
            return m_transformed.initialised();
        }

    }
}
