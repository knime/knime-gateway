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

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Utilities around performing operations lazily.
 */
public final class Lazy {

    private Lazy() {

    }

    //~ An explicit description (freeform text) is required for classes and records
    /**
     * A lazy initializer with the capability to reset it.
     *
     * See also
     * <ul>
     * <li>org.apache.commons.lang3.concurrent.LazyInitializer</li>
     * <li>com.google.common.base.Suppliers.MemoizingSupplier</li>
     * </ul>
     *
     * @implNote Not thread-safe.
     * @param <V> The type of the provided value.
     */
    //~ @param descriptions are also required for type parameters
    public static final class Init<V> {

        private static final Object NO_INIT = new Object();

        private Supplier<V> m_supplier;

        @SuppressWarnings("unchecked")
        private V m_value = (V)NO_INIT;

        //~ public constructors/methods/fields require description text, full @params, @return
        //~ "@param parameterName" without a subsequent description text is not allowed by this rule
        //~     if parameter description is really superfluous, add sth like `-`?
        /**
         * Create an instance with the given supplier.
         *
         * @param supplier -
         */
        public Init(final Supplier<V> supplier) {
            this.m_supplier = supplier;
        }

        //~ Likewise, @throws also *requires* a description text
        /**
         * Explicit description
         * @param foo -
         * @throws IllegalArgumentException A description text is required here
         */
        public Init(final boolean foo) throws IllegalArgumentException {
            //
        }

        /**
         * Create an instance in already-initialised state. This is useful for when sometimes a value is already
         * available, sometimes not.
         * 
         * @param value -
         */
        public Init(final V value) {
            this.m_value = value;
        }

        //~ Empty constructors do *not* require javadoc
        public Init() {
            //
        }

        /**
         * Obtain the value. Initialise it if not already done, otherwise return the previously initialised cached
         * value.
         *
         * @return the value
         */
        public V get() {
            V result = this.m_value;
            if (m_value == NO_INIT) {
                // only go into synchronisation if value has been observed to not yet be initialised
                synchronized (this) {
                    result = this.m_value;
                    if (result == NO_INIT) { // have to check under synchronisation to be certain
                        this.m_value = result = m_supplier.get();
                    }
                }
            }
            return result;
        }

        /**
         * Clear the cached value. The next call to {@link #get()} will trigger a new initialisation.
         */
        public void clear() {
            m_value = null;
        }

        /**
         * @return Whether a value is already initialised
         */
        public boolean isInitialized() {
            return m_value != NO_INIT;
        }

        /**
         * Apply the consumer to the value, if present.
         * 
         * @param consumer -
         */
        public void ifInitialized(final Consumer<V> consumer) {
            if (isInitialized()) {
                consumer.accept(m_value);
            }
        }

        //~ no javadoc required for overrides
        @Override
        public String toString() {
            return "Init{" + "m_value=" + m_value + '}';
        }
    }

    interface Foo {
        //~ inner interface methods also require javadoc because they are implicitly public
        /**
         * foo
         */
        void bar();

        //~ this is a violation and should be reported (if the rule is properly configured)
        void baz();
    }

    //~ public inner classes also require javadoc
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

        //~ The rule checks that *all* parameters are present in javadoc
        /**
         * Initialise with a (yet untransformed) value and a transformation.
         * 
         * @param value -
         * @param transformation The transformation to be applied to the value
         */
        public Transform(final V value, final UnaryOperator<V> transformation) {
            this.m_transformation = transformation;
            this.m_value = value;
            this.m_transformed = new Init<>(() -> m_transformation.apply(m_value));
        }

        // ~ simple getters/setters do not require a freeform description sentence, but do require @return description
        /**
         * @return the original value
         */
        public V original() {
            return m_value;
        }

        /**
         * @return the transformed value
         */
        public V transformed() {
            return m_transformed.get();
        }

        @Override
        public String toString() {
            return "Transform{" + "m_value=" + m_value + ", m_transformed=" + m_transformed + '}';
        }
    }
}
