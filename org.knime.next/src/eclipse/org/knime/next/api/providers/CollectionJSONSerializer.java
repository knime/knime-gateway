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
 *   20.05.2014 (thor): created
 */
package org.knime.next.api.providers;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.output.CloseShieldOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knime.gateway.json.util.ObjectMapperUtil;

/**
 * REST message body de-/serializer for lists that uses Jackson's {@link ObjectMapper} and consimes/produces JSON.
 *
 * @author Thorsten Meinl, KNIME AG, Zurich, Switzerland
 */
@Produces({MediaType.APPLICATION_JSON, "application/*+json"})
@Provider
public class CollectionJSONSerializer implements MessageBodyWriter<Collection<?>> {
    private final ObjectMapper m_jsonMapper = ObjectMapperUtil.getInstance().getObjectMapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize(final Collection<?> value, final Class<?> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType) {
        return -1; // no idea how many byte we will write
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
        final MediaType mediaType) {
        if (!Collection.class.isAssignableFrom(type)) {
            return false;
        }
        return true;
        //TODO
        // only write parameterized lists (otherwise the de-serializer will choke because it doesn't know which
        // classes to create)
//        if (!(genericType instanceof ParameterizedType)) {
//            return false;
//        }

        // make sure we only write lists that are parameterized with a class and nothing fancy
//        Type[] typeParameters = ((ParameterizedType)genericType).getActualTypeArguments();
//        return (typeParameters.length == 1) && (typeParameters[0] instanceof Class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(final Collection<?> value, final Class<?> type, final Type genericType,
        final Annotation[] annotations, final MediaType mediaType,
        final MultivaluedMap<java.lang.String, java.lang.Object> httpHeaders, final OutputStream entityStream)
        throws IOException, WebApplicationException {
        //httpHeaders.putSingle(HttpHeaders.CONTENT_TYPE, mediaType + ";charset=UTF-8");

        m_jsonMapper.writeValue(new CloseShieldOutputStream(entityStream), value);
        entityStream.write('\n');
    }
}
