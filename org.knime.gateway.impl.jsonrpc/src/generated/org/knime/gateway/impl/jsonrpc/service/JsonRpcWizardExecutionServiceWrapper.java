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
package org.knime.gateway.impl.jsonrpc.service;

import org.knime.gateway.api.entity.ExecutionStatisticsEnt;
import org.knime.gateway.api.entity.WizardPageEnt;
import org.knime.gateway.api.entity.WizardPageInputEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.api.service.util.ServiceExceptions;

import org.knime.gateway.api.service.WizardExecutionService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "WizardExecutionService")
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.impl.jsonrpc-config.json"})
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
            data = "InvalidSettingsException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.NoWizardPageException.class, code = -32600,
            data = "NoWizardPageException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.TimeoutException.class, code = -32600,
            data = "TimeoutException" /*per convention the data property contains the exception name*/)
    })
    public WizardPageEnt executeToNextPage(@JsonRpcParam(value="jobId") java.util.UUID jobId, Boolean async, Long timeout, @JsonRpcParam(value="wizardPageInputEnt") WizardPageInputEnt wizardPageInputEnt)  throws ServiceExceptions.InvalidSettingsException, ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException {
        return m_service.executeToNextPage(jobId, async, timeout, wizardPageInputEnt);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getCurrentPage")
    public WizardPageEnt getCurrentPage(@JsonRpcParam(value="jobId") java.util.UUID jobId)  {
        return m_service.getCurrentPage(jobId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getExecutionStatistics")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "NotFoundException" /*per convention the data property contains the exception name*/)
    })
    public ExecutionStatisticsEnt getExecutionStatistics(@JsonRpcParam(value="jobId") java.util.UUID jobId)  throws ServiceExceptions.NotFoundException {
        return m_service.getExecutionStatistics(jobId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getWebResource")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NotFoundException.class, code = -32600,
            data = "NotFoundException" /*per convention the data property contains the exception name*/)
    })
    public byte[] getWebResource(@JsonRpcParam(value="jobId") java.util.UUID jobId, @JsonRpcParam(value="resourceId") String resourceId)  throws ServiceExceptions.NotFoundException {
        return m_service.getWebResource(jobId, resourceId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "listWebResources")
    public java.util.List<String> listWebResources(@JsonRpcParam(value="jobId") java.util.UUID jobId)  {
        return m_service.listWebResources(jobId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "renderReport")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.TimeoutException.class, code = -32600,
            data = "TimeoutException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public byte[] renderReport(@JsonRpcParam(value="jobId") java.util.UUID jobId, String format)  throws ServiceExceptions.TimeoutException, ServiceExceptions.InvalidRequestException {
        return m_service.renderReport(jobId, format);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "resetToPreviousPage")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.NoWizardPageException.class, code = -32600,
            data = "NoWizardPageException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.TimeoutException.class, code = -32600,
            data = "TimeoutException" /*per convention the data property contains the exception name*/)
    })
    public WizardPageEnt resetToPreviousPage(@JsonRpcParam(value="jobId") java.util.UUID jobId, Long timeout)  throws ServiceExceptions.NoWizardPageException, ServiceExceptions.TimeoutException {
        return m_service.resetToPreviousPage(jobId, timeout);    
    }

}
