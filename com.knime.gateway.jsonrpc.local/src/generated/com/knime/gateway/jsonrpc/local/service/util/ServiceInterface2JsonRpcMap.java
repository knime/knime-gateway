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

package com.knime.gateway.jsonrpc.local.service.util;

import com.knime.gateway.jsonrpc.local.service.JsonRpcNodeService;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.jsonrpc.local.service.JsonRpcWorkflowService;
import com.knime.gateway.v0.service.WorkflowService;

import com.knime.gateway.service.GatewayService;

/**
 * Maps a service interface (i.e. it's class) to the respective JsonRpc-service.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class ServiceInterface2JsonRpcMap {

    private ServiceInterface2JsonRpcMap() {
        //utility class
    }

    public static Class<?> get(Class<? extends GatewayService> clazz) {
        if(clazz == NodeService.class) {
            return JsonRpcNodeService.class;
        }        
        if(clazz == WorkflowService.class) {
            return JsonRpcWorkflowService.class;
        }        
        else {
            throw new IllegalArgumentException("No service mapping.");
        }    
    }
}
