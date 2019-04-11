package com.knime.gateway.json.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import de.undercouch.bson4jackson.BsonFactory;

/**
 * Utility class around Jackson's {@link ObjectMapper}. It set's up an {@link ObjectMapper}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
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

    private final ObjectMapper m_mapper;

    private ObjectMapperUtil() {
        m_mapper = new ObjectMapper();
        configureObjectMapper(m_mapper);
    }

    private static void configureObjectMapper(final ObjectMapper mapper) {
        mapper.registerModule(new Jdk8Module());

        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        JsonUtil.addMixIns(mapper);
    }

    /**
     * Returns the shared object mapper.
     *
     * @return an object mapper
     */
    public ObjectMapper getObjectMapper() {
        return m_mapper;
    }

    public ObjectMapper getBinaryObjectMapper() {
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        configureObjectMapper(mapper);
        return mapper;
    }
}
