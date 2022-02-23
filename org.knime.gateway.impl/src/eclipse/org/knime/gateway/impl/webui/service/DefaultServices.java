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
 */
package org.knime.gateway.impl.webui.service;

import static java.util.Collections.synchronizedMap;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.api.webui.service.ApplicationService;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.NodeRepositoryService;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.api.webui.service.WorkflowService;

/**
 * Provides the default service implementations for gateway services and utility methods to register respective service
 * dependencies and dispose the service instances.
 *
 * This is the service instance life cycle:
 * <ul>
 * <li>setting all necessary service dependencies via {@link #setServiceDependency(Class, Object)}</li>
 * <li>accessing the service instances as needed, via {@link #getDefaultService(Class)} or directly through the
 * default service implementation, e.g., {@link DefaultWorkflowService#getInstance()}</li>
 * <li>disposing all service instances when not needed anymore via {@link #disposeAllServicesInstances()}</li>
 * </ul>
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultServices {

    // TODO auto-generate?
    private static final Map<Class<?>, Class<?>> INTERFACE_TO_IMPLEMENTATION_MAP = synchronizedMap(Map.of(//
        WorkflowService.class, DefaultWorkflowService.class, //
        NodeService.class, DefaultNodeService.class, //
        EventService.class, DefaultEventService.class, //
        ApplicationService.class, DefaultApplicationService.class, //
        NodeRepositoryService.class, DefaultNodeRepositoryService.class));

    private static final Map<Class<?>, Object> DEPENDENCIES = synchronizedMap(new HashMap<>());

    private static final Map<Class<? extends GatewayService>, LazyInitializer<? extends GatewayService>> SERVICE_INITIALIZERS =
        synchronizedMap(new HashMap<>());

    private DefaultServices() {
        //utility class
    }

    /**
     * Maps a service interface class to its default service implementation.
     *
     * @param serviceInterface the service interface the default implementation is requested for
     * @return the default implementation for the provided service interface (supplied lazily)
     * @throws NoSuchElementException if no default service implementation has been found
     */
    @SuppressWarnings("unchecked")
    public static Supplier<GatewayService> getDefaultService(final Class<?> serviceInterface) {
        if (!INTERFACE_TO_IMPLEMENTATION_MAP.containsKey(serviceInterface)) {
            throw new NoSuchElementException(
                "No default service implementation found for " + serviceInterface.getSimpleName());
        }
        Class<? extends GatewayService> defaultServiceClass =
            (Class<? extends GatewayService>)INTERFACE_TO_IMPLEMENTATION_MAP.get(serviceInterface);
        return () -> getDefaultServiceInstance(defaultServiceClass);
    }

    @SuppressWarnings("unchecked")
    static <S extends GatewayService> S getDefaultServiceInstance(final Class<S> defaultServiceClass) {
        try {
            return (S)SERVICE_INITIALIZERS.computeIfAbsent(defaultServiceClass, k -> { // NOSONAR
                return new LazyInitializer<S>() {

                    @Override
                    protected S initialize() throws ConcurrentException {
                        try {
                            return defaultServiceClass.getDeclaredConstructor().newInstance();
                        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                                | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                            throw new IllegalStateException(
                                "A default service couldn't be initialized: " + defaultServiceClass.getName(), ex);
                        }
                    }
                };
            }).get();
        } catch (ConcurrentException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * @return {@code true} if some or all services have already been initialized; if so, services dependencies
     *         ({@link #setServiceDependency(Class, Object)}) shouldn't be set anymore.
     */
    public static boolean areServicesInitialized() {
        return !SERVICE_INITIALIZERS.isEmpty();
    }

    /**
     * Disposes all default service instances and their dependencies.
     */
    public static void disposeAllServicesInstances() {
        SERVICE_INITIALIZERS.values().forEach(s -> {
            try {
                s.get().dispose();
            } catch (ConcurrentException ex) {
                throw new IllegalStateException(ex);
            }
        });
        SERVICE_INITIALIZERS.clear();
        DEPENDENCIES.clear();
    }

    /**
     * @param <T>
     * @param key the service dependency-class requested
     * @param isRequired whether the dependency is required
     * @return an instance of the given class or {@code null} if it's not present and not required
     * @throws IllegalStateException if there is no implementation registered
     */
    @SuppressWarnings("unchecked")
    static <T> T getServiceDependency(final Class<T> key, final boolean isRequired) {
        if (isRequired && !DEPENDENCIES.containsKey(key)) {
            throw new IllegalStateException(
                "No instance for class " + key.getName() + " given but required as a service dependency");
        }
        return (T)DEPENDENCIES.get(key);
    }

    /**
     * Make a dependency available
     *
     * @param clazz The class characterising the dependency
     * @param impl The implementation of `clazz` to be served as the dependency
     * @param <T> The concrete type of the dependency instance
     */
    public static <T> void setServiceDependency(final Class<T> clazz, final T impl) {
        if (areServicesInitialized()) {
            throw new IllegalStateException(
                "Some services are already initialized. Service dependencies can't be set anymore.");
        }
        if (DEPENDENCIES.containsKey(clazz)) {
            throw new IllegalStateException("Only one dependency can be registered at a time");
        }
        DEPENDENCIES.put(clazz, impl);
    }

}
