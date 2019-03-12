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
package com.knime.gateway.jsonrpc.remote.service;


import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.gateway.v0.service.WizardExecutionService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "WizardExecutionService")
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class JsonRpcWizardExecutionServiceWrapper implements WizardExecutionService {

    private final WizardExecutionService m_service;
    
    public JsonRpcWizardExecutionServiceWrapper(WizardExecutionService service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "executeToNextPage")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.InvalidSettingsException.class, code = -32600,
            data = "400" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.NoWizardPageException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/),
        @JsonRpcError(exception = ServiceExceptions.TimeoutException.class, code = -32600,
            data = "502" /*per convention the data property contains the status code*/)
    })
    public String executeToNextPage(@JsonRpcParam(value="jobId") java.util.UUID jobId, Boolean async, Long timeout, @JsonRpcParam(value="requestBody") java.util.Map<String, String> requestBody)  throws ServiceExceptions.InvalidSettingsException, ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException {
        return m_service.executeToNextPage(jobId, async, timeout, requestBody);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getCurrentPage")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NoWizardPageException.class, code = -32600,
            data = "404" /*per convention the data property contains the status code*/)
    })
    public String getCurrentPage(@JsonRpcParam(value="jobId") java.util.UUID jobId)  throws ServiceExceptions.NoWizardPageException {
        return m_service.getCurrentPage(jobId);    
    }

}
