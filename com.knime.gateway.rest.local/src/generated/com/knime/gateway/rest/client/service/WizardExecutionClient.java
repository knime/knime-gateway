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
package com.knime.gateway.rest.client.service;

import com.knime.gateway.v0.entity.WizardPageInputEnt;


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;

import com.knime.gateway.service.ServiceException;
import com.knime.gateway.v0.service.WizardExecutionService;
import com.knime.gateway.v0.service.util.ServiceExceptions;

import com.knime.enterprise.gateway.rest.api.WizardExecution;
import com.knime.enterprise.utility.ExecutorException;
import com.knime.enterprise.utility.PermissionException;
import com.knime.gateway.rest.client.AbstractGatewayClient;

/**
 * Client that provides access to a KNIME Gateway's <tt>WizardExecution</tt> resource. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.rest.local-config.json"})
public class WizardExecutionClient extends AbstractGatewayClient<WizardExecution> implements WizardExecutionService {

    /**
     * See {@link AbstractGatewayClient#AbstractGatewayClient(URI, String)}.
     *
     * @param restAddress
     * @param jwt
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public WizardExecutionClient(final URI restAddress, final String jwt)
        throws InstantiationException, IllegalAccessException, IOException {
        super(restAddress, jwt);
    }
    
    @Override
    public String executeToNextPage(java.util.UUID jobId, Boolean async, Long timeout, WizardPageInputEnt wizardPageInputEnt)  throws ServiceExceptions.InvalidSettingsException, ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException {
        try{
            return doRequest(c -> {
                try {
                    return c.executeToNextPage(jobId, async, timeout, toByteArray(wizardPageInputEnt));
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, String.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.v0.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex);
            if (gatewayException.getExceptionName().equals("InvalidSettingsException")) {
                throw new ServiceExceptions.InvalidSettingsException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("NoWizardPageException")) {
                throw new ServiceExceptions.NoWizardPageException(gatewayException.getExceptionMessage());
            }
            if (gatewayException.getExceptionName().equals("TimeoutException")) {
                throw new ServiceExceptions.TimeoutException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public String getCurrentPage(java.util.UUID jobId)  throws ServiceExceptions.NoWizardPageException {
        try{
            return doRequest(c -> {
                try {
                    return c.getCurrentPage(jobId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, String.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.v0.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex);
            if (gatewayException.getExceptionName().equals("NoWizardPageException")) {
                throw new ServiceExceptions.NoWizardPageException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
    @Override
    public String resetToPreviousPage(java.util.UUID jobId)  throws ServiceExceptions.NoWizardPageException {
        try{
            return doRequest(c -> {
                try {
                    return c.resetToPreviousPage(jobId);
                } catch (PermissionException | ExecutorException | IOException | TimeoutException ex) {
                    //server errors
                    throw new ServiceException("Internal server error.", ex);
                } catch (ProcessingException e) {
                    //in case the server cannot be reached (timeout, connection refused)
                    throw new ServiceException("Server doesn't seem to be reachable.",
                        e.getCause());
                }
            }, String.class);
        } catch (WebApplicationException ex) {
            //executor errors
            com.knime.gateway.v0.entity.GatewayExceptionEnt gatewayException = readAndParseGatewayExceptionResponse(ex);
            if (gatewayException.getExceptionName().equals("NoWizardPageException")) {
                throw new ServiceExceptions.NoWizardPageException(gatewayException.getExceptionMessage());
            }
            throw new ServiceException("Undefined service exception '" + gatewayException.getExceptionName()
                + "' with message: " + gatewayException.getExceptionMessage());
        }
    }
    
}
