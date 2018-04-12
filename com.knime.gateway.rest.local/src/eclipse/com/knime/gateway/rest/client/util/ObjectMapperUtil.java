package com.knime.gateway.rest.client.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.knime.gateway.json.JsonUtil;

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
        m_mapper.registerModule(new Jdk8Module());

        m_mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        m_mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

        JsonUtil.addMixIns(m_mapper);
    }

    /**
     * Returns the shared object mapper.
     *
     * @return an object mapper
     */
    public ObjectMapper getObjectMapper() {
        return m_mapper;
    }
}
