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
package org.knime.gateway.impl.webui.jsonrpc.service;

import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;

import com.googlecode.jsonrpc4j.JsonRpcError;
import com.googlecode.jsonrpc4j.JsonRpcErrors;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import org.knime.gateway.api.webui.service.util.ServiceExceptions;

import org.knime.gateway.api.webui.service.SpaceService;

/**
 * Json rpc annotated class that wraps another service and delegates the method calls. 
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@JsonRpcService(value = "SpaceService")
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl.jsonrpc-config.json"})
public class JsonRpcSpaceServiceWrapper implements SpaceService {

    private final java.util.function.Supplier<SpaceService> m_service;
    
    public JsonRpcSpaceServiceWrapper(java.util.function.Supplier<SpaceService> service) {
        m_service = service;
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "createSpace")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.IOException.class, code = -32600,
            data = "IOException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public SpaceEnt createSpace(@JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="spaceGroupName") String spaceGroupName)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException {
        return m_service.get().createSpace(spaceProviderId, spaceGroupName);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "createWorkflow")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.IOException.class, code = -32600,
            data = "IOException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public SpaceItemEnt createWorkflow(@JsonRpcParam(value="spaceId") String spaceId, @JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="itemId") String itemId, @JsonRpcParam(value="itemName") String itemName)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException {
        return m_service.get().createWorkflow(spaceId, spaceProviderId, itemId, itemName);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "createWorkflowGroup")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.IOException.class, code = -32600,
            data = "IOException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public SpaceItemEnt createWorkflowGroup(@JsonRpcParam(value="spaceId") String spaceId, @JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="itemId") String itemId)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException {
        return m_service.get().createWorkflowGroup(spaceId, spaceProviderId, itemId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "deleteItems")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.IOException.class, code = -32600,
            data = "IOException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public void deleteItems(@JsonRpcParam(value="spaceId") String spaceId, @JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="itemIds") java.util.List<String> itemIds)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException {
        m_service.get().deleteItems(spaceId, spaceProviderId, itemIds);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "deleteJobsForWorkflow")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.IOException.class, code = -32600,
            data = "IOException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public void deleteJobsForWorkflow(@JsonRpcParam(value="spaceId") String spaceId, @JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="itemId") String itemId, @JsonRpcParam(value="jobId") String jobId)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException {
        m_service.get().deleteJobsForWorkflow(spaceId, spaceProviderId, itemId, jobId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "deleteSchedulesForWorkflow")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.IOException.class, code = -32600,
            data = "IOException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public void deleteSchedulesForWorkflow(@JsonRpcParam(value="spaceId") String spaceId, @JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="itemId") String itemId, @JsonRpcParam(value="scheduleId") String scheduleId)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException {
        m_service.get().deleteSchedulesForWorkflow(spaceId, spaceProviderId, itemId, scheduleId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "getSpaceProvider")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public SpaceProviderEnt getSpaceProvider(@JsonRpcParam(value="spaceProviderId") String spaceProviderId)  throws ServiceExceptions.InvalidRequestException {
        return m_service.get().getSpaceProvider(spaceProviderId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "listJobsForWorkflow")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public java.util.List<Object> listJobsForWorkflow(@JsonRpcParam(value="spaceId") String spaceId, @JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="itemId") String itemId)  throws ServiceExceptions.InvalidRequestException {
        return m_service.get().listJobsForWorkflow(spaceId, spaceProviderId, itemId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "listSchedulesForWorkflow")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public java.util.List<Object> listSchedulesForWorkflow(@JsonRpcParam(value="spaceId") String spaceId, @JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="itemId") String itemId)  throws ServiceExceptions.InvalidRequestException {
        return m_service.get().listSchedulesForWorkflow(spaceId, spaceProviderId, itemId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "listWorkflowGroup")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.IOException.class, code = -32600,
            data = "IOException" /*per convention the data property contains the exception name*/)
    })
    public WorkflowGroupContentEnt listWorkflowGroup(@JsonRpcParam(value="spaceId") String spaceId, @JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="itemId") String itemId)  throws ServiceExceptions.InvalidRequestException, ServiceExceptions.IOException {
        return m_service.get().listWorkflowGroup(spaceId, spaceProviderId, itemId);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "moveOrCopyItems")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.IOException.class, code = -32600,
            data = "IOException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/)
    })
    public void moveOrCopyItems(@JsonRpcParam(value="spaceId") String spaceId, @JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="itemIds") java.util.List<String> itemIds, @JsonRpcParam(value="destWorkflowGroupItemId") String destWorkflowGroupItemId, @JsonRpcParam(value="collisionHandling") String collisionHandling, @JsonRpcParam(value="copy") Boolean copy)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException {
        m_service.get().moveOrCopyItems(spaceId, spaceProviderId, itemIds, destWorkflowGroupItemId, collisionHandling, copy);    
    }

	/**
     * {@inheritDoc}
     */
    @Override
    @JsonRpcMethod(value = "renameItem")
    @JsonRpcErrors(value = {
        @JsonRpcError(exception = ServiceExceptions.IOException.class, code = -32600,
            data = "IOException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.InvalidRequestException.class, code = -32600,
            data = "InvalidRequestException" /*per convention the data property contains the exception name*/),
        @JsonRpcError(exception = ServiceExceptions.OperationNotAllowedException.class, code = -32600,
            data = "OperationNotAllowedException" /*per convention the data property contains the exception name*/)
    })
    public SpaceItemEnt renameItem(@JsonRpcParam(value="spaceProviderId") String spaceProviderId, @JsonRpcParam(value="spaceId") String spaceId, @JsonRpcParam(value="itemId") String itemId, @JsonRpcParam(value="itemName") String itemName)  throws ServiceExceptions.IOException, ServiceExceptions.InvalidRequestException, ServiceExceptions.OperationNotAllowedException {
        return m_service.get().renameItem(spaceProviderId, spaceId, itemId, itemName);    
    }

}
