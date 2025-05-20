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
 * History
 *   Mar 30, 2022 (Kai Franze, KNIME GmbH): created
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
import org.knime.gateway.api.webui.service.ComponentService;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.KaiService;
import org.knime.gateway.api.webui.service.NodeRepositoryService;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.api.webui.service.PortService;
import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.api.webui.service.TaskService;
import org.knime.gateway.api.webui.service.WorkflowService;

/**
 * Provides the default service implementations for gateway services and utility methods to dispose the service
 * instances.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
public final class ServiceInstances {

    // TODO auto-generate?
    private static final Map<Class<?>, Class<?>> INTERFACE_TO_IMPLEMENTATION_MAP = synchronizedMap(Map.of(//
        WorkflowService.class, DefaultWorkflowService.class, //
        NodeService.class, DefaultNodeService.class, //
        PortService.class, DefaultPortService.class, //
        EventService.class, DefaultEventService.class, //
        ApplicationService.class, DefaultApplicationService.class, //
        NodeRepositoryService.class, DefaultNodeRepositoryService.class, //
        SpaceService.class, DefaultSpaceService.class, //
        KaiService.class, DefaultKaiService.class, //
        ComponentService.class, DefaultComponentService.class, //
        TaskService.class, DefaultTaskService.class //
    ) //
    );

    private static final Map<Class<? extends GatewayService>, LazyInitializer<? extends GatewayService>> SERVICE_INITIALIZERS =
        synchronizedMap(new HashMap<>());

    private ServiceInstances() {
        // Utility class
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
     *         ({@link ServiceDependencies#setServiceDependency(Class, Object)}) shouldn't be set anymore.
     */
    public static boolean areServicesInitialized() {
        return !SERVICE_INITIALIZERS.isEmpty();
    }

    /**
     * Disposes all default service instances.
     */
    public static void disposeAllServiceInstancesAndDependencies() {
        SERVICE_INITIALIZERS.values().forEach(s -> {
            try {
                s.get().dispose();
            } catch (ConcurrentException ex) {
                throw new IllegalStateException(ex);
            }
        });
        SERVICE_INITIALIZERS.clear();
        ServiceDependencies.disposeAllServicesDependencies();
    }

}
