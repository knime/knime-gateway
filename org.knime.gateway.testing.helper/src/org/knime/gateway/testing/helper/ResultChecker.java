/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 */
package org.knime.gateway.testing.helper;

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
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.PortObjectSpecEnt;
import org.knime.gateway.api.entity.WorkflowNodeEnt;
import org.knime.gateway.impl.entity.DefaultJavaObjectEnt;
import org.knime.gateway.impl.entity.DefaultNodeEnt;
import org.knime.gateway.impl.entity.DefaultNodeMessageEnt;
import org.knime.gateway.impl.entity.DefaultPatchEnt;
import org.knime.gateway.impl.entity.DefaultPatchOpEnt;
import org.knime.gateway.impl.entity.DefaultWorkflowSnapshotEnt;
import org.knime.gateway.json.util.JsonUtil;

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
        pe.addException(DefaultNodeEnt.class, "rootWorkflowID", (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_WORKFLOW_ID"));

        /** Same as above but for the snapshot id. */
        pe.addException(DefaultWorkflowSnapshotEnt.class, "snapshotID", (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_SNAPSHOT_ID"));
        pe.addException(DefaultPatchEnt.class, "snapshotID", (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_SNAPSHOT_ID"));

        /**
         * Name of the field the holds the (node) message. It is treated a bit differently for comparison since wrapped
         * metanodes don't return deterministic node messages in case of an error (the node messages contain the
         * workflow root node id that varies depending on how many other workflows are loaded). Thus, only the first
         * line is used for comparison.
         */
        PropertyException firstLineOnlyAndIgnoreRootIDs = (v, gen, e) -> {
            String s = v.toString();
            String firstLine = s.contains("\n") ? s.split("\n")[0] : s;
            firstLine = firstLine.replaceAll(" \\d+:", " ROOT:");
            gen.writeString(firstLine);
        };

        pe.addException(DefaultNodeMessageEnt.class, "message", firstLineOnlyAndIgnoreRootIDs);

        /**
         * Same as with 'message' above, especially for the case when a patch contains a new value for a node message.
         */
        pe.addException(DefaultPatchOpEnt.class, "value", firstLineOnlyAndIgnoreRootIDs);

        /**
         * Name of the field that holds json-objects as string. Since json-objects are regarded as the same although the
         * order of the fields varies, those fields are essentially ignored for comparison.
         */
        pe.addException(DefaultJavaObjectEnt.class, "jsonContent", (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_JSON_CONTENT"));

        /**
         * The representation-field of some entities varies with every test run (e.g. the serialized and base64-encoded port
         * object specs). Hence, representations-strings are ignored for comparison.
         */
        pe.addException(PortObjectSpecEnt.class, "representation", (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_REPRESENTATION"));

        /**
         * The name-field of a workflow varies if the test is executed as part of an it-test or unit-test.
         */
        pe.addException(WorkflowNodeEnt.class, "name", (v, gen, e) -> {
            if (e.getNodeID().equals(NodeIDEnt.getRootID())) {
                gen.writeString("PLACEHOLDER_FOR_NAME");
            } else {
                gen.writeString(e.getName());
            }
        });

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
        public boolean alternativeSerialization(final String propName, final JsonGenerator gen, final Object value)
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

    @FunctionalInterface
    private static interface PropertyException<E> {
        void alternativeSerialization(Object value, JsonGenerator gen, E entity) throws IOException;
    }
}