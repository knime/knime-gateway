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

import java.util.HashMap;
import java.util.Map;

import org.knime.gateway.api.service.GatewayService;

/**
 * Provides and manages specific object instances that are considered to be dependencies for the
 * implementation of {@link GatewayService}s.
 *
 * @author Kai Franze, KNIME GmbH
 */
public final class ServiceDependencies {

    private static final Map<Class<?>, Object> DEPENDENCIES = synchronizedMap(new HashMap<>());

    private ServiceDependencies() {
        // Utility class
    }

    /**
     * Return a dependency instance based on its class
     *
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
     * @param clazz The class characterizing the dependency
     * @param impl The implementation of `clazz` to be served as the dependency
     * @param <T> The concrete type of the dependency instance
     */
    public static <T> void setServiceDependency(final Class<T> clazz, final T impl) {
        if (ServiceInstances.areServicesInitialized()) {
            throw new IllegalStateException(
                "Some services are already initialized. Service dependencies can't be set anymore.");
        }
        if (DEPENDENCIES.containsKey(clazz)) {
            throw new IllegalStateException("Only one dependency can be registered at a time");
        }
        DEPENDENCIES.put(clazz, impl);
    }

    /**
     * Disposes all default service dependencies.
     */
    public static void disposeAllServicesDependencies() {
        DEPENDENCIES.clear();
    }

}
