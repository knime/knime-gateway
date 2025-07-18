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
 * ------------------------------------------------------------------------
 */

package org.knime.gateway.impl.webui.jsonrpc.service.util;

import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcSpaceServiceWrapper;
import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcCompositeViewServiceWrapper;
import org.knime.gateway.api.webui.service.CompositeViewService;
import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcKaiServiceWrapper;
import org.knime.gateway.api.webui.service.KaiService;
import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcNodeServiceWrapper;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcNodeRepositoryServiceWrapper;
import org.knime.gateway.api.webui.service.NodeRepositoryService;
import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcPortServiceWrapper;
import org.knime.gateway.api.webui.service.PortService;
import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcEventServiceWrapper;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcWorkflowServiceWrapper;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcComponentServiceWrapper;
import org.knime.gateway.api.webui.service.ComponentService;
import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcComponentEditorServiceWrapper;
import org.knime.gateway.api.webui.service.ComponentEditorService;
import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcApplicationServiceWrapper;
import org.knime.gateway.api.webui.service.ApplicationService;

import org.knime.gateway.api.service.GatewayService;

import java.lang.reflect.InvocationTargetException;

/**
 * Wraps the given gateway service with the appropriate json rpc service.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl.jsonrpc-config.json"})
public class WrapWithJsonRpcService {

    private WrapWithJsonRpcService() {
        //utility class
    }
    
    /**
     * Wraps a service instance with a JsonRpc-wrapper (that brings the json-rpc annotations).
     *
     * @param service the service-supplier to be wrapped
     * @param serviceInterface the service interface to select the right wrapper
     *
     * @return the service wrapper
     */
    @SuppressWarnings("unchecked")
    public static <S extends GatewayService> S wrap(final java.util.function.Supplier<S> service, final Class<S> serviceInterface) {
        try {
            if(serviceInterface == SpaceService.class) {
                return (S)JsonRpcSpaceServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
            if(serviceInterface == CompositeViewService.class) {
                return (S)JsonRpcCompositeViewServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
            if(serviceInterface == KaiService.class) {
                return (S)JsonRpcKaiServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
            if(serviceInterface == NodeService.class) {
                return (S)JsonRpcNodeServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
            if(serviceInterface == NodeRepositoryService.class) {
                return (S)JsonRpcNodeRepositoryServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
            if(serviceInterface == PortService.class) {
                return (S)JsonRpcPortServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
            if(serviceInterface == EventService.class) {
                return (S)JsonRpcEventServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
            if(serviceInterface == WorkflowService.class) {
                return (S)JsonRpcWorkflowServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
            if(serviceInterface == ComponentService.class) {
                return (S)JsonRpcComponentServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
            if(serviceInterface == ComponentEditorService.class) {
                return (S)JsonRpcComponentEditorServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
            if(serviceInterface == ApplicationService.class) {
                return (S)JsonRpcApplicationServiceWrapper.class.getConstructor(java.util.function.Supplier.class).newInstance(service);
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
        throw new IllegalArgumentException("No wrapper available!");
    }
}
