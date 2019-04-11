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
package com.knime.gateway.json.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.knime.gateway.entity.AnnotationIDEnt;
import com.knime.gateway.entity.ConnectionIDEnt;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.util.ListEntities;

/**
 * Utility class for json rpc stuff.
 *
 * @author Martin Horn, University of Konstanz
 */
public class JsonUtil {

    private JsonUtil() {
        //utility class
    }

    /**
     * Adds entity and entity builder mixin classes to the passed mapper in order to add jackson-annotations to the
     * respective entity (and entity builder) interface for de-/serialization.
     *
     * @param mapper the object mapper to add the mixins to
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final void addMixIns(final ObjectMapper mapper) {
        List<Class<?>> entityClasses = ListEntities.listEntityClasses();
        List<Class<?>> entityBuilderClasses = ListEntities.listEntityBuilderClasses();
        List<Class<?>> entityMixInClasses = com.knime.gateway.json.entity.util.ListEntities.listEntityClasses();
        List<Class<?>> entityBuilderMixInClasses =
            com.knime.gateway.json.entity.util.ListEntities.listEntityBuilderClasses();

        for (int i = 0; i < entityClasses.size(); i++) {
            mapper.addMixIn(entityClasses.get(i), entityMixInClasses.get(i));
            mapper.addMixIn(entityBuilderClasses.get(i), entityBuilderMixInClasses.get(i));
        }


        for (Class clazz : Arrays.asList(NodeIDEnt.class, ConnectionIDEnt.class, AnnotationIDEnt.class)) {
            SimpleModule nodeIdMod = new SimpleModule();
            nodeIdMod.addSerializer(clazz, createToStringSerializer(clazz));
            nodeIdMod.addDeserializer(clazz, createFromStringSerializer(clazz));
            mapper.registerModule(nodeIdMod);
        }
    }

    @SuppressWarnings("serial")
    private static <C> JsonSerializer<C> createToStringSerializer(final Class<C> clazz) {
        return new StdSerializer<C>(clazz) {

            @Override
            public void serialize(final C value, final JsonGenerator gen, final SerializerProvider provider)
                throws IOException {
                gen.writeString(value.toString());
            }
        };
    }

    @SuppressWarnings("serial")
    private static <C> JsonDeserializer<C> createFromStringSerializer(final Class<C> clazz) {
        return new StdDeserializer<C>(clazz) {

            @Override
            public C deserialize(final JsonParser p, final DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
                try {
                    return clazz.getConstructor(String.class).newInstance(p.getText());
                } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                    throw new IOException(ex);
                }
            }
        };
    }
}
