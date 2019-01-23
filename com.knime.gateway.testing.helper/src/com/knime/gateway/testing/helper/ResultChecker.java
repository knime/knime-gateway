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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
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
 * #{@link ResultChecker#ResultChecker(boolean)}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ResultChecker {

    /**
     * Name of the field that holds the root workflow id (e.g. in NodeEnt). Since the id changes with every run it
     * cannot be compared.
     */
    private static final String ROOTWORKFLOWID_FIELDNAME = "rootWorkflowID";

    /**
     * Same as above but for the snapshot id.
     */
    private static final String SNAPSHOTID_FIELDNAME = "snapshotID";

    /**
     * Name of the field the holds the (node) message. It is treated a bit differently for comparison since wrapped
     * metanodes don't return deterministic node messages in case of an error (the node messages contain the workflow
     * root node id that varies depending on how many other workflows are loaded). Thus, only the first line is used for
     * comparison.
     */
    private static final String MASSAGE_FIELDNAME = "message";

    /**
     * Name of the field that holds json-objects as string. Since json-objects are regarded as the same although the
     * order of the fields varies, those fields need to be treated a bit different for comparison.
     */
    private static final String JSON_CONTENT_FIELDNAME = "jsonContent";

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
            name = name == null ? "" : name;
            if (name.equals(ROOTWORKFLOWID_FIELDNAME) || name.equals(SNAPSHOTID_FIELDNAME)) {
                gen.writeString("PLACEHOLDER_FOR_ID");
            } else if (name.equals(MASSAGE_FIELDNAME)) {
                String s = (String)value;
                //only take the first line of message fields
                if (s.contains("\n")) {
                    gen.writeString(s.split("\n")[0]);
                } else {
                    gen.writeString(s);
                }
            } else if (name.equals(JSON_CONTENT_FIELDNAME)) {
                //Json-content fields are ignored since they seem to vary.
                //The content of those json-objects should be tested somewhere else, anyway
                //(e.g. via the js-tests).
                gen.writeString("PLACEHOLDER_FOR_JSON_CONTENT");
            } else {
                m_defaultSerializer.serialize(value, gen, serializers);
            }
        }
    }
}