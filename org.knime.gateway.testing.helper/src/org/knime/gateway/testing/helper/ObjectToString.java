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
 *
 * History
 *   Mar 11, 2021 (hornm): created
 */
package org.knime.gateway.testing.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.CollectionType;

/**
 * Turns an object into a string.
 *
 * This implementation serializes it in to a json string using jackson.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ObjectToString {

    private PropertyExceptions m_propertyExceptions;

    private ObjectMapper m_mapper;

    /**
     * @param mapper the object mapper used to turn an object into a string
     */
    public ObjectToString(final ObjectMapper mapper) {
        m_mapper = mapper;
        if (m_mapper != null) {
            // setup object mapper for to-string conversion with property exceptions
            SimpleModule module = new SimpleModule();
            module.setSerializerModifier(new PropertyExceptionSerializerModifier());
            m_mapper.registerModule(module);
        }
    }

    /**
     * Turns an object into a string.
     *
     * If the object is already a string, the very same object is returned.
     *
     * @param obj
     * @return the object as string (if not already)
     * @throws JsonProcessingException
     */
    public String toString(final Object obj) throws JsonProcessingException {
        if (obj instanceof String) {
            return (String)obj;
        } else {
            ObjectWriter objectWriter = m_mapper
                .writer(new DefaultPrettyPrinter().withObjectIndenter(new DefaultIndenter().withLinefeed("\n")));
            return objectWriter.writeValueAsString(obj);
        }
    }

    /**
     * Adds a new exception.
     *
     * @param entityClass the entity to add the exception for
     * @param propName the actual property name to apply the exception to
     * @param altFunc how the property should be serialized alternatively
     */
    public <E> void addException(final Class<E> entityClass, final String propName,
        final PropertyException<E> altFunc) {
        if (m_propertyExceptions == null) {
            m_propertyExceptions = new PropertyExceptions();
        }
        m_propertyExceptions.addException(entityClass, propName, altFunc);
    }

    /**
     * Guarantees a canonical sorting of the json properties within the serialized string and injects the
     * {@link PropertyExceptionSerializer}.
     */
    private class PropertyExceptionSerializerModifier extends BeanSerializerModifier {

        /**
         * {@inheritDoc}
         */
        @Override
        public JsonSerializer<?> modifySerializer(final SerializationConfig config, final BeanDescription beanDesc,
            final JsonSerializer<?> serializer) {
            return new PropertyExceptionSerializer<>(serializer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public JsonSerializer<?> modifyCollectionSerializer(final SerializationConfig config,
            final CollectionType valueType, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
            return new PropertyExceptionSerializer<>(serializer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<BeanPropertyWriter> orderProperties(final SerializationConfig config,
            final BeanDescription beanDesc, final List<BeanPropertyWriter> beanProperties) {
            //order properties alphabetically
            beanProperties.sort((b1, b2) -> b1.getName().compareTo(b2.getName()));
            return super.orderProperties(config, beanDesc, beanProperties);
        }
    }

    /**
     * Uses in most cases the default serializer except for some fields with certain, predefined names.
     */
    private class PropertyExceptionSerializer<T> extends JsonSerializer<T> {

        private final JsonSerializer<T> m_defaultSerializer;

        PropertyExceptionSerializer(final JsonSerializer<T> defaultSerializer) {
            m_defaultSerializer = defaultSerializer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void serialize(final T value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException {
            String name = gen.getOutputContext().getCurrentName();
            if (name == null || m_propertyExceptions == null
                || !m_propertyExceptions.alternativeSerialization(name, gen, value)) {
                m_defaultSerializer.serialize(value, gen, serializers);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void serializeWithType(final T value, final JsonGenerator gen, final SerializerProvider serializers,
            final TypeSerializer typeSer) throws IOException {
            serialize(value, gen, serializers);
        }
    }

    /**
     * Represents a alternative way of serializing a property specified by a gateway entity class and the property name.
     */
    private static class PropertyExceptions {

        private final List<Class<?>> m_classes = new ArrayList<>();

        private final List<String> m_propNames = new ArrayList<>();

        private final List<PropertyException<?>> m_altFuncs = new ArrayList<>();

        private <E> void addException(final Class<E> entityClass, final String propName,
            final PropertyException<E> altFunc) {
            m_classes.add(entityClass);
            m_propNames.add(propName);
            m_altFuncs.add(altFunc);
        }

        /**
         * @param propName
         * @param gen
         * @param value
         * @return <code>true</code> if property has been serialized, <code>false</code> if no serialization has been
         *         carried out
         * @throws IOException
         */
        @SuppressWarnings("unchecked")
        private <E> boolean alternativeSerialization(final String propName, final JsonGenerator gen, final Object value)
            throws IOException {
            Class<?> parentEntity = gen.getCurrentValue().getClass();
            for (int i = 0; i < m_classes.size(); i++) {
                if (m_classes.get(i).isAssignableFrom(parentEntity) && propName.equals(m_propNames.get(i))) {
                    PropertyException<E> pe = (PropertyException<E>)m_altFuncs.get(i);
                    pe.alternativeSerialization(value, gen, (E)gen.getCurrentValue());
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Single serialization exception for an entity property.
     *
     * @param <E> the type of the entity
     */
    @FunctionalInterface
    public static interface PropertyException<E> {
        /**
         * Overwrite to provide an alternative serialization of the specific property.
         *
         * @param value
         * @param gen
         * @param entity
         * @throws IOException
         */
        void alternativeSerialization(Object value, JsonGenerator gen, E entity) throws IOException;
    }

}
