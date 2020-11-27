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

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.knime.gateway.api.entity.GatewayEntity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;

/**
 * Compares objects to a representation stored to files (i.e. snapshot testing).
 *
 * The objects (e.g. WorkflowEnt) are compared by turning them into a string via jackson and compare it to a static
 * string.
 *
 * A snapshot is stored into its own file, placed in a directory named after the test-class which carries out the test.
 *
 * Snapshot can be re-written by deleting the respective snapshot file (.snap). If a snapshot doesn't match, a debug
 * file (.snap.debug) will be created next to the original snapshot file what allows direct file comparison. The debug
 * file will be deleted automatically as soon as the respective snapshot matches again.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ResultChecker {

    /**
     * The for the serialization of entities to json-strings for comparison.
     */
    private final ObjectMapper m_objectMapper;

    private final PropertyExceptions m_propertyExceptions;

    private final File m_resultDirectory;

    /**
     * Creates a new instance of the result checker.
     *
     * @param propertyExceptions let one define exceptions of how to deal with certain properties for comparison
     * @param objectMapper the mapper for object serialization for comparison
     * @param resultDirectory the directory to read the result-snapshots from (and write them to, if not present), can
     *            be <code>null</code> if there are no exceptions
     */
    public ResultChecker(final PropertyExceptions propertyExceptions, final ObjectMapper objectMapper,
        final File resultDirectory) {
        m_propertyExceptions = propertyExceptions;
        m_resultDirectory = resultDirectory;
        m_objectMapper = objectMapper;
        // setup object mapper for entity-comparison with property exceptions
        SimpleModule module = new SimpleModule();
        module.setSerializerModifier(new PropertyExceptionSerializerModifier());
        m_objectMapper.registerModule(module);
    }

    /**
     * Checks an object by comparing it to a string referenced by a specific test name and a result key.
     *
     * @param testClass the class running the test
     * @param obj the object to snapshot-test
     * @param snapshotName the name for the snapshot
     * @throws AssertionError if the result check failed (e.g. if the entity differs from the representation referenced
     *             by the given key)
     */
    public void checkObject(final Class<?> testClass, final String snapshotName, final Object obj) {
        try {
            compareWithSnapshotFromFile(testClass, snapshotName, obj);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to compare with snapshot from file", ex); // NOSONAR
        }
    }

    private void compareWithSnapshotFromFile(final Class<?> testClass, final String snapshotName, final Object obj)
        throws IOException {
        String actual = (obj instanceof String) ? (String)obj
            : m_objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        Path snapFile = getSnapshotFile(testClass, snapshotName);
        if (Files.exists(snapFile)) {
            // load expected snapshot and compare
            String expected = new String(Files.readAllBytes(snapFile), StandardCharsets.UTF_8);
            Path debugFile = getSnapshotDebugFile(testClass, snapshotName);
            if (!actual.equals(expected)) {
                // write debug file if snapshot doesn't match
                Files.write(debugFile, actual.getBytes(StandardCharsets.UTF_8));
                assertEquals(snapshotName, testClass, expected, actual);
            } else if (Files.exists(debugFile)) {
                // if snapshot matches, delete debug file (might not exist)
                Files.delete(debugFile);
            } else {
                //
            }
        } else {
            // just write the snapshot
            Files.createDirectories(snapFile.getParent());
            Files.write(snapFile, actual.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        }
    }

    private static void assertEquals(final String snapshotName, final Class<?> testClass, final String expected,
        final String actual) {
        assertThat(String.format("Snapshot '%s' in test '%s' doesn't match", snapshotName, testClass.getSimpleName()),
            actual, compareWithDiff(expected));
    }

    private static Matcher<String> compareWithDiff(final String expected) {
        return new BaseMatcher<String>() {

            @Override
            public boolean matches(final Object item) {
                return item instanceof String && item.equals(expected);
            }

            @Override
            public void describeMismatch(final Object item, final Description description) {
                if (item instanceof String) {
                    Patch<String> diff = DiffUtils.diff((String)item, expected, null);
                    description.appendText("there are differences:\n"
                        + diff.getDeltas().stream().map(Object::toString).collect(Collectors.joining(",\n")));
                } else {
                    description.appendText("not a String");
                }
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Snapshot file content");
            }
        };
    }

    private Path getSnapshotFile(final Class<?> testClass, final String snapshotName) {
        return Paths.get(m_resultDirectory.getAbsolutePath(), getDirFromClass(testClass) + snapshotName + ".snap");
    }

    private Path getSnapshotDebugFile(final Class<?> testClass, final String snapshotName) {
        return Paths.get(m_resultDirectory.getAbsolutePath(),
            getDirFromClass(testClass) + snapshotName + ".snap.debug");
    }

    private static String getDirFromClass(final Class<?> testClass) {
        return "/" + testClass.getCanonicalName() + "/";
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
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public JsonSerializer<?> modifyCollectionSerializer(final SerializationConfig config,
            final CollectionType valueType, final BeanDescription beanDesc, final JsonSerializer<?> serializer) {
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