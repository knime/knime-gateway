/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by
 * KNIME AG, Zurich, Switzerland
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
 * History
 *   08.02.2015 (thor): created
 */
package com.knime.gateway.rest.client.providers.json;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.knime.enterprise.server.mason.NamespaceRegistry;
import com.knime.gateway.rest.client.util.ObjectMapperUtil;

/**
 * Deserializer for collections in JSON (or Mason) format.
 *
 * (mostly copied from {@link com.knime.enterprise.server.rest.providers.json.CollectionJSONDeserializer} but with
 * another jackson object mapper and other '@Consumes' types)
 *
 * @author Thorsten Meinl, KNIME AG, Zurich, Switzerland
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@Consumes({MediaType.APPLICATION_JSON})
@Provider
public class CollectionJSONDeserializer implements MessageBodyReader<Collection<?>> {
    private final ObjectMapper m_jsonMapper = ObjectMapperUtil.getInstance().getObjectMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
        final MediaType mediaType) {
        if (!Collection.class.isAssignableFrom(type)) {
            return false;
        }
        // only accept parameterized lists otherwise the ObjectMapper doesn't know which classes to create
        if (!(genericType instanceof ParameterizedType)) {
            return false;
        }
        Type[] typeParameters = ((ParameterizedType)genericType).getActualTypeArguments();
        return (typeParameters.length == 1) && (typeParameters[0] instanceof Class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<?> readFrom(final Class<Collection<?>> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders,
        final InputStream entityStream) throws IOException, WebApplicationException {
        InjectableValues inject = new InjectableValues.Std().addValue(NamespaceRegistry.class, new NamespaceRegistry());

        Type[] typeParameters = ((ParameterizedType)genericType).getActualTypeArguments();

        ObjectReader reader =
            m_jsonMapper.readerFor(TypeFactory.defaultInstance()
                .constructCollectionType(type, (Class<?>)typeParameters[0])).with(inject);

        JsonParser jp = reader.getFactory().createParser(entityStream);
        jp.disable(Feature.AUTO_CLOSE_SOURCE);
        if (jp.nextToken() == null) {
            return Collections.EMPTY_LIST;
        } else {
            return reader.readValue(jp);
        }
    }
}
