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
package com.knime.gateway.local.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.node.KNIMEConstants;
import org.knime.core.node.NodeLogger;
import org.knime.core.util.Pair;

import com.knime.gateway.service.GatewayService;
import com.knime.gateway.util.ExtPointUtil;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.v0.service.WorkflowService;

/**
 * Manages services (i.e. {@link GatewayService}s) and gives access to service interface implementations.
 *
 * @author Martin Horn, University of Konstanz
 */
public class ServiceManager {
    private static final NodeLogger LOGGER = NodeLogger.getLogger(ServiceManager.class);

    private static ServiceFactory SERVICE_FACTORY;

    /* SERVICES SINGLEON INSTANCES */
    private static Map<Pair<Class<? extends GatewayService>, ServiceConfig>, GatewayService> SERVICES = new HashMap<>();

    private ServiceManager() {
        //private 'utility' class
    }

    /**
     * Delivers implementations for service interfaces (see {@link GatewayService}. Implementations are injected via
     * {@link ServiceFactory} extension point.
     *
     * In order to be sure that the right service is delivered at any time and stateful services are handled correctly,
     * always use this method to access the desired service and never keep a service reference (unless you know what you
     * are doing).
     *
     * TODO add more shortcuts for services (e.g. ServiceManager.nodeService())
     *
     * @param serviceInterface the service interface the implementation is requested for
     * @param serviceConfig the service configuration required to instantiate a service, e.g. server information etc.
     * @return an implementation of the requested service interface (it returns the same instance with every method
     *         call)
     */
    public static synchronized <S extends GatewayService> S service(final Class<S> serviceInterface,
        final ServiceConfig serviceConfig) {
        Pair<Class<? extends GatewayService>, ServiceConfig> pair = Pair.create(serviceInterface, serviceConfig);
        S service = (S)SERVICES.get(pair);
        if (service == null) {
            if (SERVICE_FACTORY == null) {
                SERVICE_FACTORY = createServiceFactory();
            }
            service = createLogDelegateService(serviceInterface,
                SERVICE_FACTORY.createService(serviceInterface, serviceConfig));
            SERVICES.put(pair, service);
        }
        return service;
    }


    /**
     * Wrap the service with a proxy class that logs the method calls and subsequently delegates them.
     *
     * @param serviceInterface the service interface to proxy
     * @param delegate the service to delegate the method calls to
     * @return the wrapped service instance
     */
    @SuppressWarnings("unchecked")
    private static <S extends GatewayService> S createLogDelegateService(final Class<S> serviceInterface,
        final S delegate) {
        return (S)Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[]{serviceInterface},
            new InvocationHandler() {
                @Override
                public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                    if (KNIMEConstants.ASSERTIONS_ENABLED) {
                        // this produces lots of message therefore only enabled for development and tests
                        LOGGER.debug("Gateway service call: " + serviceInterface.getSimpleName() + "."
                            + method.getName() + "(" + Arrays.deepToString(args) + ")");
                    }
                    try {
                        return method.invoke(delegate, args);
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                }
            });
    }

    /**
     * Shortcut for {@link #service(WorkflowService.class, ServiceConfig)}.
     *
     * @param serviceConfig see {@link #service(Class, ServiceConfig)}
     * @return the workflow service implementation
     */
    public static WorkflowService workflowService(final ServiceConfig serviceConfig) {
        return service(WorkflowService.class, serviceConfig);
    }

    /**
     * Shortcut for {@link #service(NodeService.class, ServiceConfig)}.
     *
     * @param serviceConfig see {@link #service(Class, ServiceConfig)}
     * @return the node service implementation
     */
    public static NodeService nodeService(final ServiceConfig serviceConfig) {
        return service(NodeService.class, serviceConfig);
    }

    private static ServiceFactory createServiceFactory() {
        List<ServiceFactory> instances =
            ExtPointUtil.collectExecutableExtensions(ServiceFactory.EXT_POINT_ID, ServiceFactory.EXT_POINT_ATTR);

        if (instances.size() == 0) {
            throw new IllegalStateException("No service factory registered!");
        } else if (instances.size() > 1) {
            NodeLogger.getLogger(ServiceManager.class)
                .debug("Multiple service factories registered! The one with the highest priority is used.");
            Collections.sort(instances, (o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));
        }
        return instances.get(0);
    }
}
