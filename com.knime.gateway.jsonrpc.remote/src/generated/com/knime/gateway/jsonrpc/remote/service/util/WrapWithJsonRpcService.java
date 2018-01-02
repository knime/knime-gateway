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

package com.knime.gateway.jsonrpc.remote.service.util;

import com.knime.gateway.jsonrpc.remote.service.JsonRpcNodeServiceWrapper;
import org.knime.gateway.v0.service.NodeService;
import com.knime.gateway.jsonrpc.remote.service.JsonRpcWorkflowServiceWrapper;
import org.knime.gateway.v0.service.WorkflowService;

import org.knime.gateway.service.GatewayService;

import java.lang.reflect.InvocationTargetException;

/**
 * Wraps the given gateway service with the appropriate json rpc service.
 *
 * @author Martin Horn, University of Konstanz
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen", date = "2018-01-02T16:29:34.776+01:00")
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
        
            if(serviceInterface == NodeService.class) {
                return JsonRpcNodeServiceWrapper.class.getConstructor(serviceInterface).newInstance(service);
            }
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
