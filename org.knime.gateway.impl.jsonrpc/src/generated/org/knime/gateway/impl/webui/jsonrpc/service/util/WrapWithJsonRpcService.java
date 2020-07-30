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

package org.knime.gateway.impl.webui.jsonrpc.service.util;

import org.knime.gateway.impl.webui.jsonrpc.service.JsonRpcWorkflowServiceWrapper;
import org.knime.gateway.api.webui.service.WorkflowService;

import org.knime.gateway.api.service.GatewayService;

import java.lang.reflect.InvocationTargetException;

/**
 * Wraps the given gateway service with the appropriate json rpc service.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl.jsonrpc-config.json"})
public class WrapWithJsonRpcService {

    private WrapWithJsonRpcService() {
        //utility class
    }
    
    /**
     * Wraps a service instance with a JsonRpc-wrapper (that brings the json-rpc annotations).
     *
     * @param service the service to be wrapped
     * @param serviceInterface the service interface to select the right wrapper
     *
     * @return the service wrapper
     */
    public static GatewayService wrap(final GatewayService service, final Class<?> serviceInterface) {
        try {
        
            if(serviceInterface == WorkflowService.class) {
                return JsonRpcWorkflowServiceWrapper.class.getConstructor(serviceInterface).newInstance(service);
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
        throw new IllegalArgumentException("No wrapper available!");
    }
}
