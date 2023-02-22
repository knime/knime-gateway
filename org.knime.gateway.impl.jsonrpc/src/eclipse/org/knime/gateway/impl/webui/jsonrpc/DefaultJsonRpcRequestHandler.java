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
 *   Oct 12, 2020 (hornm): created
 */
package org.knime.gateway.impl.webui.jsonrpc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.impl.jsonrpc.DefaultExceptionToJsonRpcErrorTranslator;
import org.knime.gateway.impl.jsonrpc.JsonRpcRequestHandler;
import org.knime.gateway.impl.webui.service.DefaultWorkflowService;
import org.knime.gateway.impl.webui.service.ServiceInstances;
import org.knime.gateway.json.util.ObjectMapperUtil;

/**
 * A {@link JsonRpcRequestHandler} that delegates the json-requests to the default service implementations of the web-ui
 * (e.g. {@link DefaultWorkflowService}) using the {@link DefaultExceptionToJsonRpcErrorTranslator} in case of thrown
 * exceptions.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultJsonRpcRequestHandler extends JsonRpcRequestHandler {

    /**
     * New instance.
     */
    public DefaultJsonRpcRequestHandler() {
        super(ObjectMapperUtil.getInstance().getObjectMapper(), wrapWithJsonRpcServices(getDefaultServiceImpls()),
            new DefaultExceptionToJsonRpcErrorTranslator());
    }

    /**
     * For testing purposes only.
     */
    DefaultJsonRpcRequestHandler(
        final Map<Class<? extends GatewayService>, Supplier<? extends GatewayService>> serviceImpls) {
        super(ObjectMapperUtil.getInstance().getObjectMapper(), wrapWithJsonRpcServices(serviceImpls),
            new DefaultExceptionToJsonRpcErrorTranslator());
    }

    private static Map<Class<? extends GatewayService>, Supplier<? extends GatewayService>> getDefaultServiceImpls() {
        // default web-ui service implementations
        List<Class<? extends GatewayService>> serviceInterfaces =
            org.knime.gateway.api.webui.service.util.ListServices.listServiceInterfaces();
        return serviceInterfaces.stream() //
                .collect(Collectors.toMap(Function.identity(), ServiceInstances::getDefaultServiceSupplier));
    }

    private static Map<String, GatewayService>
        wrapWithJsonRpcServices(
            final Map<Class<? extends GatewayService>, Supplier<? extends GatewayService>> serviceImpls) {
        Map<String, GatewayService> wrappedServices = new HashMap<>();

        for (Entry<Class<? extends GatewayService>, Supplier<? extends GatewayService>> entry : serviceImpls
            .entrySet()) { // NOSONAR
            @SuppressWarnings("rawtypes")
            Class key = entry.getKey(); // NOSONAR
            @SuppressWarnings("unchecked")
            GatewayService wrappedService =
                org.knime.gateway.impl.webui.jsonrpc.service.util.WrapWithJsonRpcService.wrap(entry.getValue(), key);
            wrappedServices.put(key.getSimpleName(), wrappedService);
        }
        return wrappedServices;
    }
}
