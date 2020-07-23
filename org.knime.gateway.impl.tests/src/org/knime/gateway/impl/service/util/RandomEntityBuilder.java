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
import org.knime.gateway.api.entity.util.ListEntities;
import org.knime.gateway.impl.entity.util.Interface2ImplMap;

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
    public static <E extends GatewayEntityBuilder> E buildRandomEntityBuilder(final Class<E> entityBuilderInterface)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        E builder = (E)Interface2ImplMap.get(entityBuilderInterface);

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

    private static Class<GatewayEntityBuilder>
        getBuilderInterfaceForEntity(final Class<GatewayEntity> entityInterface) {
        return (Class<GatewayEntityBuilder>)ListEntities.listEntityBuilderClasses()
            .get(ListEntities.listEntityClasses().indexOf(entityInterface));
    }

}
