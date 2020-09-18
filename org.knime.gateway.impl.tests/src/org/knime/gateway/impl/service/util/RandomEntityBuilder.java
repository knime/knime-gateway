/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
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
package org.knime.gateway.impl.service.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.GatewayEntity;
import org.knime.gateway.api.entity.GatewayEntityBuilder;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.util.ListEntities;
import org.knime.gateway.impl.webui.entity.DefaultEntityBuilderFactory;

/**
 * Helper class to create random entities (mainly for unit tests).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class RandomEntityBuilder {

    private static final Random RANDOM = new Random();

    /**
     * Creates new entity builder with all properties assigned with random values.
     *
     * @param entityBuilderInterface the builder interface to create a random instance for
     * @return the entity builder interface with random values
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("rawtypes")
    public static <E extends GatewayEntityBuilder> E buildRandomEntityBuilder(final Class<E> entityBuilderInterface)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        @SuppressWarnings("unchecked")
        E builder = (E)new DefaultEntityBuilderFactory().createEntityBuilder(entityBuilderInterface).get();

        //provide random values to all setter methods via reflection
        Method[] methods = builder.getClass().getInterfaces()[0].getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set") && method.getParameterCount() == 1) {
                Type paramType = method.getGenericParameterTypes()[0];
                Object randValue = getRandomValue(paramType);
                if (randValue != null) {
                    method.invoke(builder, randValue);
                }
            }
        }
        return builder;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object getRandomValue(final Type type)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)type;
            Class<?> rclazz = (Class<?>)ptype.getRawType();
            if (List.class.isAssignableFrom(rclazz)) {
                List l = new ArrayList();
                for (int i = 0; i < RANDOM.nextInt(10); i++) {
                    l.add(getRandomValue(ptype.getActualTypeArguments()[0]));
                }
                return l;
            } else if (Map.class.isAssignableFrom(rclazz)) {
                Map m = new HashMap();
                for (int i = 0; i < RANDOM.nextInt(10); i++) {
                    m.put(getRandomValue(String.class), getRandomValue(ptype.getActualTypeArguments()[1]));
                }
                return m;
            } else {
                return null;
            }
        } else if (type instanceof Class) {
            Class<?> clazz = (Class<?>)type;
            if (clazz.equals(Integer.class)) {
                return RANDOM.nextInt();
            } else if (clazz.equals(Boolean.class)) {
                return RANDOM.nextBoolean();
            } else if (clazz.equals(String.class)) {
                return UUID.randomUUID().toString();
            } else if (clazz.equals(UUID.class)) {
                return UUID.randomUUID();
            } else if (Enum.class.isAssignableFrom(clazz)) {
                Object[] enumVals = clazz.getEnumConstants();
                return enumVals[RANDOM.nextInt(enumVals.length)];
            } else if(clazz.equals(NodeIDEnt.class)) {
                return new NodeIDEnt(RANDOM.nextInt());
            } else if (clazz.equals(AnnotationIDEnt.class)) {
                return new AnnotationIDEnt(new NodeIDEnt(RANDOM.nextInt()), RANDOM.nextInt());
            } else if (GatewayEntity.class.isAssignableFrom(clazz)) {
                Class<GatewayEntityBuilder> builderInterface =
                    getBuilderInterfaceForEntity((Class<GatewayEntity>)clazz);
                return buildRandomEntityBuilder(builderInterface).build();
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Class<GatewayEntityBuilder>
        getBuilderInterfaceForEntity(final Class<GatewayEntity> entityInterface) {
        return (Class<GatewayEntityBuilder>)ListEntities.listEntityBuilderClasses()
            .get(ListEntities.listEntityClasses().indexOf(entityInterface));
    }

}
