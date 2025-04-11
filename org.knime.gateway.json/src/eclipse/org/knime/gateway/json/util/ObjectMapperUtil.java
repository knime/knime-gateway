package org.knime.gateway.json.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.undercouch.bson4jackson.BsonFactory;

/**
 * Utility class around Jackson's {@link ObjectMapper}. It set's up an {@link ObjectMapper}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH, Germany
 */
public final class ObjectMapperUtil {
    private static final ObjectMapperUtil INSTANCE = new ObjectMapperUtil();

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance
     */
    public static ObjectMapperUtil getInstance() {
        return INSTANCE;
    }

    private ObjectMapper m_mapper = null;
    private ObjectMapper m_binaryMapper = null;

    private ObjectMapperUtil() {
        // Singleton
    }

    /**
     * Returns the shared object mapper.
     *
     * @return an object mapper
     */
    public ObjectMapper getObjectMapper() {
        if (m_mapper == null) {
            m_mapper = createObjectMapper(new JsonFactory());
        }
        return m_mapper;
    }

    /**
     * @return object mapper that reads/writes bson (binary json)
     */
    public ObjectMapper getBinaryObjectMapper() {
        if (m_binaryMapper == null) {
            m_binaryMapper = createObjectMapper(new BsonFactory());
        }
        return m_binaryMapper;
    }

    private static ObjectMapper createObjectMapper(final JsonFactory jsonFactory) {
        configureJsonFactory(jsonFactory);
        final var mapper = new ObjectMapper(jsonFactory);
        configureObjectMapper(mapper);
        return mapper;
    }

    private static void configureJsonFactory(final JsonFactory factory) {
        final var readConstraints = getStreamReadConstraints();
        factory.setStreamReadConstraints(readConstraints);
        final var writeConstraints = getStreamWriteConstraints();
        factory.setStreamWriteConstraints(writeConstraints);
    }

    /**
     * Default settings:
     *
     * - Maximum Number value length: default 1000 (see DEFAULT_MAX_NUM_LEN)
     * - Maximum String value length: default 20_000_000 (see DEFAULT_MAX_STRING_LEN)
     * - Maximum Property name length: default 50_000 (see DEFAULT_MAX_NAME_LEN)
     * - Maximum Nesting depth: default 1000 (see DEFAULT_MAX_DEPTH)
     * - Maximum Document length: default unlimited (coded as -1, (see DEFAULT_MAX_DOC_LEN))
     */
    private static StreamReadConstraints getStreamReadConstraints() {
        // TODO: Set reasonable limits
        return StreamReadConstraints.defaults()//
            .rebuild()//
            .maxNumberLength(1000)//
            .maxStringLength(20_000_000)//
            .maxNameLength(50_000)//
            .maxNestingDepth(1000)//
            .maxDocumentLength(-1)//
            .build();
    }

    /**
     * Default settings:
     *
     * - Maximum Nesting depth: default 1000 (see DEFAULT_MAX_DEPTH)
     */
    private static StreamWriteConstraints getStreamWriteConstraints() {
        // TODO: Set reasonable limits
        return StreamWriteConstraints.defaults()//
            .rebuild()//
            .maxNestingDepth(1000)//
            .build();
    }

    private static void configureObjectMapper(final ObjectMapper mapper) {
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());

        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        mapper.setSerializationInclusion(Include.NON_NULL);

        JsonUtil.addWebUIMixIns(mapper);
        JsonUtil.addIDEntityDeSerializer(mapper);
        JsonUtil.addDateTimeDeSerializer(mapper);
        JsonUtil.addBitSetDeSerializer(mapper);
    }

}
