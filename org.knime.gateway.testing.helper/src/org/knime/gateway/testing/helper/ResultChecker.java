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
 */
package org.knime.gateway.testing.helper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.junit.Assert;
import org.knime.gateway.api.entity.GatewayEntity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * Compares objects to a representation stored to files.
 *
 * The objects (e.g. WorkflowEnt) are compared by turning them into a string via jackson and compare it to a
 * static string.
 *
 * This class also allows one to newly collect and (re-)write the results (see
 * #{@link ResultChecker#ResultChecker(boolean, PropertyExceptions, ObjectMapper, File))}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ResultChecker {

    /**
     * The for the serialization of entities to json-strings for comparison.
     */
    private final ObjectMapper m_objectMapper;

    private final Map<String, Map<String, JsonNode>> m_resultMaps;

    private final boolean m_rewriteTestResults;

    private final PropertyExceptions m_propertyExceptions;

    private final File m_resultDirectory;

    /**
     * Creates a new instance of the result checker.
     *
     * @param propertyExceptions let one define exceptions of how to deal with certain properties for comparison
     * @param rewriteTestResults if set to true, the expected test results (i.e. the retrieved workflow etc.) will be
     *            updated and written to the respective files on calling {@link #writeTestResultsToFiles()}
     * @param objectMapper the mapper for object serialization for comparison
     * @param resultDirectory the directory to read the result-snapshots from (and write them to, if desired)
     */
    public ResultChecker(final boolean rewriteTestResults, final PropertyExceptions propertyExceptions,
        final ObjectMapper objectMapper, final File resultDirectory) {
        m_rewriteTestResults = rewriteTestResults;
        m_propertyExceptions = propertyExceptions;
        m_resultDirectory = resultDirectory;
        m_resultMaps = new HashMap<>();

        m_objectMapper = objectMapper;
        // setup object mapper for entity-comparison with property exceptions
        SimpleModule module = new SimpleModule();
        module.setSerializerModifier(new PropertyExceptionSerializerModifier());
        m_objectMapper.registerModule(module);
    }

    /**
     * Writes the newly created result maps to files, one file for each test, with the test name as file name.
     *
     * @throws IllegalStateException if the result checker wasn't configured to rewrite the test results
     */
    public void writeTestResultsToFiles() {
        if (m_rewriteTestResults) {
            m_resultMaps.entrySet().forEach(resultMap -> {
                try {
                    try (FileWriterWithEncoding writer =
                        new FileWriterWithEncoding(getResultFilePath(resultMap.getKey()), Charset.defaultCharset())) {
                        m_objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, resultMap.getValue());
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("Result map couldn't be written!", e);
                }
            });
        } else {
            throw new IllegalStateException("Result checker wasn't configured to rewrite the test results.");
        }
    }

    /**
     * Checks an object by comparing it to a string referenced by a specific test name and a result key.
     *
     * @param testName the name of the test
     * @param obj the object to test
     * @param resultKey the key in the result map
     * @throws AssertionError if the result check failed (e.g. if the entity differs from the representation referenced
     *             by the given key)
     */
    public void checkObject(final String testName, final Object obj, final String resultKey) {
        JsonNode objAsJson = objectToJson(obj);
        if (m_rewriteTestResults) {
            Map<String, JsonNode> resultMap =
                m_resultMaps.computeIfAbsent(testName, k -> new HashMap<String, JsonNode>());
            resultMap.put(resultKey, objAsJson);
            //result map will be written to file in the writeTestResultsToFiles()-method
        } else {
            JsonNode expectedResult = getResultMap(testName).get(resultKey);
            try {
                String expectedResultAsString =
                    m_objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expectedResult);
                String objAsString = m_objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objAsJson);

                // Still using assetEquals on strings(!) here since a nice result comparison can be opened
                // with double-click on the test-error
                // assertThat("Unexpected result returned", result, is(expectedResult));
                // TODO alternatively https://github.com/skyscreamer/JSONassert could be used
                // here eventually or the JsonNode-object itself for comparison
                Assert.assertEquals(expectedResultAsString, objAsString);
            } catch (JsonProcessingException ex) { //NOSONAR
                Assert.fail("Problem comparing objects");
            }
        }
    }

    /**
     * Returns the result map for a particular test.
     *
     * @param testName the test to get the result map for
     * @return
     */
    private Map<String, JsonNode> getResultMap(final String testName) {
        return m_resultMaps.computeIfAbsent(testName, this::readResultMap);
    }

    /**
     * Essentially reads the result map (string to compare to) from a file.
     */
    private Map<String, JsonNode> readResultMap(final String testName) {
        try {
            String json = IOUtils.toString(getResultFilePath(testName).toURI().toURL(), Charset.defaultCharset());
            return m_objectMapper.readValue(json, new TypeReference<Map<String, JsonNode>>() {
            });
        } catch (IOException e) {
            // should never happen
            throw new RuntimeException(e); // NOSONAR
        }
    }

    private File getResultFilePath(final String testName) {
        return new File(m_resultDirectory, "testresults_" + testName + ".json");
    }

    /**
     * Turns an object into a json-string by using jackson.
     *
     * @param obj the object to get the json string from
     * @return the json representation
     */
    private final JsonNode objectToJson(final Object obj) {
        return m_objectMapper.valueToTree(obj);
    }

    /**
     * Guarantees a canonical sorting of the json properties within the serialized string and injects the
     * {@link PropertyExceptionSerializer}.
     */
    private class PropertyExceptionSerializerModifier extends BeanSerializerModifier {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        public JsonSerializer<?> modifySerializer(final SerializationConfig config, final BeanDescription beanDesc,
            final JsonSerializer<?> serializer) {
            return new PropertyExceptionSerializer(serializer);
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
            if (name == null || !m_propertyExceptions.alternativeSerialization(name, gen, value)) {
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
     * Represents a alternative way of serializing a property specified by a gateway entity and the property name.
     */
    public static class PropertyExceptions {

        private final List<Class<?>> m_classes = new ArrayList<>();

        private final List<String> m_propNames = new ArrayList<>();

        @SuppressWarnings("rawtypes")
        private final List<PropertyException> m_altFuncs = new ArrayList<>();

        /**
         * Adds a new exception.
         *
         * @param entityClass the entity to add the exception for
         * @param propName the actual property name to apply the exception to
         * @param altFunc how the property should be serialized alternatively
         */
        public <E extends GatewayEntity> void addException(final Class<E> entityClass, final String propName,
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
        private boolean alternativeSerialization(final String propName, final JsonGenerator gen, final Object value)
            throws IOException {
            Class<?> parentEntity = gen.getCurrentValue().getClass();
            if (GatewayEntity.class.isAssignableFrom(parentEntity)) {
                for (int i = 0; i < m_classes.size(); i++) {
                    if (m_classes.get(i).isAssignableFrom(parentEntity) && propName.equals(m_propNames.get(i))) {
                        m_altFuncs.get(i).alternativeSerialization(value, gen, gen.getCurrentValue());
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * Single serialization exception for an entity property.
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