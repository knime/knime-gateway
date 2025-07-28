package org.knime.gateway.json.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import de.undercouch.bson4jackson.BsonFactory;

/**
 * Utility class around Jackson's {@link ObjectMapper}. It sets up an {@link ObjectMapper}.
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

    private ObjectMapper m_mapper = null;
    private ObjectMapper m_binaryMapper = null;

    private ObjectMapperUtil() {
        //utility class
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

    /**
     * Returns the shared object mapper.
     *
     * @return an object mapper
     */
    public ObjectMapper getObjectMapper() {
        if (m_mapper == null) {
            m_mapper = createObjectMapper();
        }
        return m_mapper;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        configureObjectMapper(mapper);
        return mapper;
    }

    /**
     * @return object mapper that reads/writes bson (binary json)
     */
    public ObjectMapper getBinaryObjectMapper() {
        if (m_binaryMapper == null) {
            m_binaryMapper = createBinaryObjectMapper();
        }
        return m_binaryMapper;
    }

    private static ObjectMapper createBinaryObjectMapper() {
        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        configureObjectMapper(mapper);
        return mapper;
    }
}
