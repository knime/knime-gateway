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
 *
 */
package com.knime.gateway.testing.helper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.junit.Assert;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.knime.gateway.entity.GatewayEntity;
import com.knime.gateway.entity.PortObjectSpecEnt;
import com.knime.gateway.entity.impl.DefaultJavaObjectEnt;
import com.knime.gateway.entity.impl.DefaultNodeEnt;
import com.knime.gateway.entity.impl.DefaultNodeMessageEnt;
import com.knime.gateway.entity.impl.DefaultPatchEnt;
import com.knime.gateway.entity.impl.DefaultPatchOpEnt;
import com.knime.gateway.entity.impl.DefaultWorkflowSnapshotEnt;
import com.knime.gateway.json.util.JsonUtil;

/**
 * Compares objects to a representation stored to files.
 *
 * The objects (e.g. WorkflowEnt) are compared by turning them into a string via jackson and compare it to a
 * static string.
 *
 * This class also allows one to newly collect and (re-)write the results (see
 * #{@link ResultChecker#ResultChecker(boolean)}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ResultChecker {

    /**
     * Collected exceptions for alternative property serialization.
     */
    private static final PropertyExceptions PROPERTY_EXCEPTIONS;

    static {
        PropertyExceptions pe = new PropertyExceptions();

        /**
         * Name of the field that holds the root workflow id (e.g. in NodeEnt). Since the id changes with every run it
         * cannot be compared.
         */
        pe.addException(DefaultNodeEnt.class, "rootWorkflowID", (v, gen) -> gen.writeString("PLACEHOLDER_FOR_WORKFLOW_ID"));

        /** Same as above but for the snapshot id. */
        pe.addException(DefaultWorkflowSnapshotEnt.class, "snapshotID", (v, gen) -> gen.writeString("PLACEHOLDER_FOR_SNAPSHOT_ID"));
        pe.addException(DefaultPatchEnt.class, "snapshotID", (v, gen) -> gen.writeString("PLACEHOLDER_FOR_SNAPSHOT_ID"));

        /**
         * Name of the field the holds the (node) message. It is treated a bit differently for comparison since wrapped
         * metanodes don't return deterministic node messages in case of an error (the node messages contain the
         * workflow root node id that varies depending on how many other workflows are loaded). Thus, only the first
         * line is used for comparison.
         */
        PropertyException firstLineOnly = (v, gen) -> {
            String s = v.toString();
            if (s.contains("\n")) {
                gen.writeString(s.split("\n")[0]);
            } else {
                gen.writeString(s);
            }
        };
        pe.addException(DefaultNodeMessageEnt.class, "message", firstLineOnly);

        /**
         * Same as with 'message' above, especially for the case when a patch contains a new value for a node message.
         */
        pe.addException(DefaultPatchOpEnt.class, "value", firstLineOnly);

        /**
         * Name of the field that holds json-objects as string. Since json-objects are regarded as the same although the
         * order of the fields varies, those fields are essentially ignored for comparison.
         */
        pe.addException(DefaultJavaObjectEnt.class, "jsonContent", (v, gen) -> gen.writeString("PLACEHOLDER_FOR_JSON_CONTENT"));

        /**
         * The representation-field of some entities varies with every test run (e.g. the serialized and base64-encoded port
         * object specs). Hence, representations-strings are ignored for comparison.
         */
        pe.addException(PortObjectSpecEnt.class, "representation", (v, gen) -> gen.writeString("PLACEHOLDER_FOR_REPRESENTATION"));

        PROPERTY_EXCEPTIONS = pe;
    }

    /**
     * The for the serialization of entities to json-strings for comparison.
     */
    private final ObjectMapper m_objectMapper;

    private final Map<String, Map<String, JsonNode>> m_resultMaps;

    private final boolean m_rewriteTestResults;

    /**
     * Creates a new instance of the result checker.
     *
     * The files required to check the results a read on demand.
     */
    public ResultChecker() {
        this(false);
    }

    /**
     * Creates a new instance of the result checker.
     *
     * @param rewriteTestResults if set to true, the expected test results (i.e. the retrieved workflow etc.) will be
     *            updated and written to the respective files on calling {@link #writeTestResultsToFiles()}
     */
    public ResultChecker(final boolean rewriteTestResults) {
        m_rewriteTestResults = rewriteTestResults;
        m_resultMaps = new HashMap<String, Map<String, JsonNode>>();

        // setup object mapper for entity-comparison
        m_objectMapper = new ObjectMapper();
        JsonUtil.addMixIns(m_objectMapper);
        m_objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        m_objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
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
                    try (FileWriterWithEncoding writer = new FileWriterWithEncoding(
                        TestUtil.resolveToFile(getResultFilePath(resultMap.getKey())), Charset.defaultCharset())) {
                        m_objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, resultMap.getValue());
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("Result map couldn't be written!");
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
        JsonNode objAsJson;
        try {
            objAsJson = objectToJson(obj);
        } catch (JsonProcessingException ex) {
            // should not happen
            Assert.fail("Problem turning an entity into a string for comparison");
            return;
        }
        if (m_rewriteTestResults) {
            Map<String, JsonNode> resultMap = m_resultMaps.computeIfAbsent(testName, k -> new HashMap<String, JsonNode>());
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
            } catch (JsonProcessingException ex) {
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
        return m_resultMaps.computeIfAbsent(testName, k -> readResultMap(k));
    }

    /**
     * Essentially reads the result map (string to compare to) from a file.
     */
    private Map<String, JsonNode> readResultMap(final String testName) {
        try {
            String json =
                IOUtils.toString(TestUtil.resolveToURL(getResultFilePath(testName)), Charset.defaultCharset());
            return m_objectMapper.readValue(json, new TypeReference<Map<String, JsonNode>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getResultFilePath(final String testName) {
        return "/files/testresults_" + testName + ".json";
    }

    /**
     * Turns an object into a json-string by using jackson.
     *
     * @param obj the object to get the json string from
     * @return the json representation
     * @throws JsonProcessingException
     */
    private final JsonNode objectToJson(final Object obj) throws JsonProcessingException {
        return m_objectMapper.valueToTree(obj);
    }

    /**
     * Guarantees a canonical sorting of the json properties within the serialized string and injects the
     * {@link PropertyExceptionSerializer}.
     */
    private static class PropertyExceptionSerializerModifier extends BeanSerializerModifier {

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
            beanProperties.sort((b1, b2) -> {
                return b1.getName().compareTo(b2.getName());
            });
            return super.orderProperties(config, beanDesc, beanProperties);
        }
    }

    /**
     * Uses in most cases the default serializer except for some fields with certain, predefined names.
     */
    private static class PropertyExceptionSerializer<T> extends JsonSerializer<T> {

        private JsonSerializer<T> m_defaultSerializer;

        PropertyExceptionSerializer(final JsonSerializer<T> defaultSerializer) {
            m_defaultSerializer = defaultSerializer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void serialize(final T value, final JsonGenerator gen, final SerializerProvider serializers)
            throws IOException, JsonProcessingException {
            String name = gen.getOutputContext().getCurrentName();
            if (name == null || !PROPERTY_EXCEPTIONS.alternativeSerialization(name, gen, value)) {
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
    private static class PropertyExceptions {

        private final List<Class<?>> m_classes = new ArrayList<>();

        private final List<String> m_propNames = new ArrayList<>();

        private final List<PropertyException> m_altFuncs = new ArrayList<>();

        /**
         * Adds a new exception.
         *
         * @param entityClass the entity to add the exception for
         * @param propName the actual property name to apply the exception to
         * @param altFunc how the property should be serialized alternatively
         */
        <E extends GatewayEntity> void addException(final Class<E> entityClass, final String propName,
            final PropertyException altFunc) {
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
        public boolean alternativeSerialization(final String propName, final JsonGenerator gen, final Object value)
            throws IOException {
            Class<?> parentEntity = gen.getCurrentValue().getClass();
            if (GatewayEntity.class.isAssignableFrom(parentEntity)) {
                for (int i = 0; i < m_classes.size(); i++) {
                    if (m_classes.get(i).isAssignableFrom(parentEntity) && propName.equals(m_propNames.get(i))) {
                        m_altFuncs.get(i).alternativeSerialization(value, gen);
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @FunctionalInterface
    private static interface PropertyException {
        void alternativeSerialization(Object value, JsonGenerator gen) throws IOException;
    }
}