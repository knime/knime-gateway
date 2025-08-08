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
 *   Aug 17, 2022 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import static org.knime.gateway.impl.webui.service.DefaultNodeService.createInitialSelectionSupplier;
import static org.knime.gateway.impl.webui.service.ServiceUtilities.assertProjectIdAndGetNodeContainer;

import java.util.List;
import java.util.NoSuchElementException;

import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObject;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.webui.node.DataValueWrapper;
import org.knime.core.webui.node.NodePortWrapper;
import org.knime.core.webui.node.port.PortSpecViewFactory;
import org.knime.core.webui.node.port.PortViewManager;
import org.knime.core.webui.node.port.PortViewManager.PortViewDescriptor;
import org.knime.core.webui.node.view.table.datavalue.DataValueViewManager;
import org.knime.gateway.api.entity.DataValueViewEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.PortViewEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.service.PortService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.impl.webui.service.events.SelectionEventBus;

/**
 * Default implementation of the {@link PortService}-interface.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultPortService implements PortService {

    private final SelectionEventBus m_selectionEventBus =
        ServiceDependencies.getServiceDependency(SelectionEventBus.class, false);

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultPortService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultPortService.class);
    }

    DefaultPortService() {
        // singleton
    }

    @Override
    public PortViewEnt getPortView(final String projectId, final NodeIDEnt workflowId, final String versionId,
        final NodeIDEnt nodeId, final Integer portIdx, final Integer viewIdx)
        throws NodeNotFoundException, InvalidRequestException {

        var version = VersionId.parse(versionId);
        var nc = assertProjectIdAndGetNodeContainer(projectId, workflowId, version, nodeId);
        var outPort = nc.getOutPort(portIdx);

        final var errorTitle = "Failed to get port view";
        if (outPort.getPortObject() == InactiveBranchPortObject.INSTANCE) {
            throw InvalidRequestException.builder() //
                .withTitle(errorTitle) //
                .withDetails(
                    String.format("No port view available because the port at index %d for node %s is inactive.",
                        portIdx, nc.getNameWithID())) //
                .canCopy(true) //
                .build();
        }

        var viewDescriptor = PortViewManager.getPortViewDescriptor(outPort.getPortType(), viewIdx)
            .orElseThrow(() -> InvalidRequestException.builder() //
                .withTitle(errorTitle) //
                .withDetails(String.format("Port %d for node %s doesn't provide a view at index %d", portIdx,
                    nc.getNameWithID(), viewIdx)) //
                .canCopy(false) //
                .build());
        var isFlowVariablePort = outPort.getPortType().equals(FlowVariablePortObject.TYPE);

        if (!isFlowVariablePort && !isExecutionStateValid(viewDescriptor, nc, portIdx)) {
            throw InvalidRequestException.builder() //
                .withTitle(errorTitle) //
                .withDetails(String.format("No port view available at index %d for current state of node %s.", portIdx,
                    nc.getNameWithID())) //
                .canCopy(true) //
                .build();
        }

        if (!isFlowVariablePort && (viewDescriptor.viewFactory() instanceof PortSpecViewFactory)
            && outPort.getPortObjectSpec() == null) {
            throw InvalidRequestException.builder() //
                .withTitle(errorTitle) //
                .withDetails(String.format("No port object spec available at index %d for node %s.", portIdx,
                    nc.getNameWithID())) //
                .canCopy(false) //
                .build();
        }

        var wrapper = NodePortWrapper.of(nc, portIdx, viewIdx);
        var portViewManager = PortViewManager.getInstance();
        return new PortViewEnt(wrapper, portViewManager, createInitialSelectionSupplier(wrapper, projectId,
            portViewManager.getTableViewManager(), m_selectionEventBus));
    }

    @Override
    public Object getDataValueView(final String projectId, final NodeIDEnt workflowId, final String versionId,
        final NodeIDEnt nodeId, final Integer portIdx, final Integer rowIdx, final Integer colIdx)
        throws NodeNotFoundException {
        var version = VersionId.parse(versionId);
        var nc = assertProjectIdAndGetNodeContainer(projectId, workflowId, version, nodeId);
        return new DataValueViewEnt(DataValueWrapper.of(nc, portIdx, rowIdx, colIdx),
            DataValueViewManager.getInstance());
    }

    private static boolean isExecutionStateValid(final PortViewDescriptor viewDescriptor, final NodeContainer nc,
        final Integer portIdx) {
        var nodeState = (nc instanceof SingleNodeContainer) ? nc.getNodeContainerState()
            : ((WorkflowManager)nc).getOutPort(portIdx).getNodeContainerState();
        return viewDescriptor.viewFactory() instanceof PortSpecViewFactory ? nodeState.isConfigured()
            : nodeState.isExecuted();
    }

    @Override
    public String callPortDataService(final String projectId, final NodeIDEnt workflowId, final String versionId,
        final NodeIDEnt nodeId, final Integer portIdx, final Integer viewIdx, final String serviceType,
        final String body) throws NodeNotFoundException, InvalidRequestException {

        var version = VersionId.parse(versionId);
        var nc = assertProjectIdAndGetNodeContainer(projectId, workflowId, version, nodeId);
        var portViewManager = PortViewManager.getInstance();

        if ("initial_data".equals(serviceType)) {
            return portViewManager.getDataServiceManager()
                .callInitialDataService(NodePortWrapper.of(nc, portIdx, viewIdx));
        } else if ("data".equals(serviceType)) {
            return portViewManager.getDataServiceManager().callRpcDataService(NodePortWrapper.of(nc, portIdx, viewIdx),
                body);
        } else {
            throw InvalidRequestException.builder() //
                .withTitle("Failed to call port data service") //
                .withDetails("Unknown service type '" + serviceType + "'.") //
                .canCopy(true) //
                .build();
        }
    }

    @Override
    public void deactivatePortDataServices(final String projectId, final NodeIDEnt workflowId, final String versionId,
        final NodeIDEnt nodeId, final Integer portIdx, final Integer viewIdx)
        throws NodeNotFoundException, InvalidRequestException {

        var version = VersionId.parse(versionId);
        NodeContainer nc;
        try {
            nc = assertProjectIdAndGetNodeContainer(projectId, workflowId, version, nodeId);
        } catch (NoSuchElementException e) {
            // in case there is no project for the given id
            throw InvalidRequestException.builder() //
                .withTitle("Failed to deactivate port data services") //
                .withDetails(e.getMessage()) //
                .canCopy(true) //
                .withCause(e) //
                .build();
        }

        var outPort = nc.getOutPort(portIdx);
        var viewDescriptor = PortViewManager.getPortViewDescriptor(outPort.getPortType(), viewIdx).orElse(null);

        if (viewDescriptor != null && isExecutionStateValid(viewDescriptor, nc, portIdx)) {
            PortViewManager.getInstance().getDataServiceManager()
                .deactivateDataServices(NodePortWrapper.of(nc, portIdx, viewIdx));
        }
    }

    @Override
    public void updateDataPointSelection(final String projectId, final NodeIDEnt workflowId, final String versionId,
        final NodeIDEnt nodeId, final Integer portIdx, final Integer viewIdx, final String mode,
        final List<String> selection) throws NodeNotFoundException {

        var version = VersionId.parse(versionId);
        ServiceUtilities.updateDataPointSelection(projectId, workflowId, version, nodeId, mode, selection,
            nc -> NodePortWrapper.of(nc, portIdx, viewIdx));
    }

}
