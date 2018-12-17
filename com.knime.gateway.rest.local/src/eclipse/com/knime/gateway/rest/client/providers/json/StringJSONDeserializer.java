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
package com.knime.gateway.rest.client.providers.json;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knime.gateway.json.util.ObjectMapperUtil;

/**
 * Deserializes strings from json.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@Consumes(MediaType.APPLICATION_JSON)
@Provider
public class StringJSONDeserializer implements MessageBodyReader<String> {

    ObjectMapper m_objectMapper;

    /**
     * Creates a new deserializer.
     */
    public StringJSONDeserializer() {
        m_objectMapper = ObjectMapperUtil.getInstance().getObjectMapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
        final MediaType mediaType) {
        return String.class.isAssignableFrom(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readFrom(final Class<String> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders,
        final InputStream entityStream) throws IOException, WebApplicationException {
        return m_objectMapper.readValue(entityStream, String.class);
    }

}
