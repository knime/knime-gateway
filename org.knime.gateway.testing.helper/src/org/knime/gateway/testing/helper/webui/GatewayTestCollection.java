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
package org.knime.gateway.testing.helper.webui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.knime.core.util.Pair;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Gives programmatic access to all gateway test (helpers) that test the web-ui API services. Provided in that way such
 * that they can be re-used in different test settings (e.g. integration tests).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class GatewayTestCollection {

    private static final List<Class<?>> CONTRIBUTING_CLASSES = List.of( //
        AddComponentCommandTestHelper.class, //
        AlignNodesCommandTestHelper.class, //
        BendpointsTestHelper.class, //
        CollapseExpandCommandsTestHelper.class, //
        ComponentServiceTestHelper.class, //
        ConnectCommandsTestHelper.class, //
        CutCopyPasteCommandsTestHelper.class, //
        DeleteCommandTestHelper.class, //
        DeleteComponentPlaceholderCommandTestHelper.class, //
        EditPortsTestHelper.class, //
        EventServiceTestHelper.class, //
        NodeRecommendationsTestHelper.class, //
        NodeServiceTestHelper.class, //
        PortServiceTestHelper.class, //
        ShareComponentCommandTestHelper.class, //
        SpaceServiceTestHelper.class, //
        StreamingExecutionTestHelper.class, //
        TranslateCommandTestHelper.class, //
        UpdateComponentLinkCommandTestHelper.class, //
        WorkflowCommandTestHelper.class, //
        WorkflowServiceTestHelper.class //
    );

    private GatewayTestCollection() {
        // utility class
    }

    /**
     * Collects and initializes all available gateway tests.
     *
     * Any non-static, public method declared by the 'contributing classes' is considered a test.
     *
     * @return map from the individual test names to a function that allows one to run the test
     */
    public static Map<String, GatewayTestRunner> collectAllGatewayTests() {
        var classFilter = parseFilters(System.getProperty("org.knime.gateway.testing.helper.test_class"));
        return CONTRIBUTING_CLASSES.stream() //
            .filter(helper -> classFilter.test(helper.getSimpleName())) //
            .flatMap(helper -> Arrays.stream(helper.getDeclaredMethods())) //
            .filter(method -> !Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) //
            .map(method -> {
                final var declaringClass = method.getDeclaringClass();
                return Pair.create(declaringClass.getSimpleName() + "::" + method.getName(),
                    createGatewayTestRunner(declaringClass, method));
            }) //
            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private static Predicate<String> parseFilters(final String property) {
        if (property == null || property.isEmpty() || property.isBlank()) {
            return s -> true;
        }
        var filter = Arrays.stream(property.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
        return filter::contains;
    }

    private static boolean hasConstructor(final Class<?> clazz, final Class<?>... parameterTypes) {
        for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), parameterTypes)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether we need to {@link ProjectManager} dependency or not, we choose the matching constructor and instantiate
     * the class.
     *
     */
    private static Object getInstanceFromInferredConstructor(final Class<?> declaringClass, final ResultChecker rc,
        final ServiceProvider sp, final WorkflowLoader wl, final WorkflowExecutor we, final ProjectManager pm)
        throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {
        // Use the constructor including the project manager
        if (hasConstructor(declaringClass, ResultChecker.class, ServiceProvider.class, WorkflowLoader.class,
            WorkflowExecutor.class, ProjectManager.class)) {
            return declaringClass.getConstructor(ResultChecker.class, ServiceProvider.class, WorkflowLoader.class,
                WorkflowExecutor.class, ProjectManager.class).newInstance(rc, sp, wl, we, pm);
        }

        // Use the constructor without the project manager
        if (hasConstructor(declaringClass, ResultChecker.class, ServiceProvider.class, WorkflowLoader.class,
            WorkflowExecutor.class)) {
            return declaringClass.getConstructor(ResultChecker.class, ServiceProvider.class, WorkflowLoader.class,
                WorkflowExecutor.class).newInstance(rc, sp, wl, we);
        }

        throw new IllegalStateException("No suitable constructor found for " + declaringClass.getName());
    }

    private static GatewayTestRunner createGatewayTestRunner(final Class<?> declaringClass, final Method method) {
        return (rc, sp, wl, we, pm) -> { // NOSONAR
            final var instance = getInstanceFromInferredConstructor(declaringClass, rc, sp, wl, we, pm);
            try {
                method.invoke(instance);
            } catch (InvocationTargetException ex) {
                var cause = ex.getCause();
                if (cause instanceof Exception e) {
                    throw e;
                } else if (cause instanceof Error e) {
                    throw e;
                } else {
                    throw ex;
                }
            }
        };
    }
}
