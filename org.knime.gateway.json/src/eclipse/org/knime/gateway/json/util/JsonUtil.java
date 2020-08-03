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
package org.knime.gateway.json.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;

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
     * Only the mixins for the web-ui entities are added.
     *
     * @param mapper the object mapper to add the mixins to
     */
    public static final void addWebUIMixIns(final ObjectMapper mapper) {
        List<Class<?>> entityClasses = org.knime.gateway.api.webui.entity.util.ListEntities.listEntityClasses();
        List<Class<?>> entityBuilderClasses = org.knime.gateway.api.webui.entity.util.ListEntities.listEntityBuilderClasses();
        List<Class<?>> entityMixInClasses = org.knime.gateway.json.webui.entity.util.ListEntities.listEntityClasses();
        List<Class<?>> entityBuilderMixInClasses =
            org.knime.gateway.json.webui.entity.util.ListEntities.listEntityBuilderClasses();

        for (int i = 0; i < entityClasses.size(); i++) {
            mapper.addMixIn(entityClasses.get(i), entityMixInClasses.get(i));
            mapper.addMixIn(entityBuilderClasses.get(i), entityBuilderMixInClasses.get(i));
        }
    }

    /**
     * Adds the de-/serializer for {@link NodeIDEnt}, {@link ConnectionIDEnt} and {@link AnnotationIDEnt}.
     *
     * @param mapper the mapper to add the de-/serializer to
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void addIDEntitySerializer(final ObjectMapper mapper) {
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
