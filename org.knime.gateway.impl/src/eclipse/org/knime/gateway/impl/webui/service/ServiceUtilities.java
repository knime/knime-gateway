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
 *   Mar 8, 2024 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;

import org.knime.core.data.RowKey;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.webui.node.NodePortWrapper;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.port.PortViewManager;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.core.webui.node.view.table.TableViewManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.SelectionEventEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.service.events.SelectionEventBus;

/**
 * Logic shared between web-ui default service implementations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class ServiceUtilities {

    private ServiceUtilities() {
        // utility
    }

    /**
     * Call {@link DefaultServiceContext#assertWorkflowProjectId(String)} and return the {@link NodeContainer}
     * associated with the given id.
     *
     * @param projectId -
     * @param workflowId -
     * @param nodeId -
     * @param versionId
     * @return -
     * @throws NodeNotFoundException if the node container couldn't be found
     * @throws IllegalStateException if the given project-id is not the expected one
     * @throws NoSuchElementException if there is no project for the given id
     */
    static NodeContainer assertProjectIdAndGetNodeContainer(final String projectId, final NodeIDEnt workflowId,
        final VersionId versionId, final NodeIDEnt nodeId) throws NodeNotFoundException {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        try {
            return org.knime.gateway.impl.service.util.DefaultServiceUtil.getNodeContainer(projectId, workflowId,
               versionId, nodeId);
        } catch (IllegalArgumentException e) {
            throw new NodeNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * Call {@link DefaultServiceContext#assertWorkflowProjectId(String)} and return the {@link WorkflowManager}
     * instance identified by the given {@link WorkflowKey}.
     *
     * @param wfKey -
     * @return -
     * @throws NodeNotFoundException -
     */
    static WorkflowManager assertProjectIdAndGetWorkflowManager(final WorkflowKey wfKey) throws NodeNotFoundException {
        DefaultServiceContext.assertWorkflowProjectId(wfKey.getProjectId());
        try {
            return org.knime.gateway.impl.service.util.DefaultServiceUtil.getWorkflowManager(wfKey);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw new NodeNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * Updates the data point selection (aka hiliting) for the given node/port.
     *
     * @param projectId -
     * @param workflowId -
     * @param nodeId -
     * @param mode -
     * @param selection -
     * @param getNodeWrapper -
     * @param <N> -
     * @param projectId
     * @param workflowId
     * @param nodeId
     * @param versionId
     * @param portIdx can be {@code null} if not a port view
     * @param viewIdx can be {@code null} if not a port view
     * @throws NodeNotFoundException if the node for the given id couldn't be found
     * @throws IllegalStateException If there was a problem with translating teh strings to row-keys
     */
    @SuppressWarnings("unchecked")
    static <N extends NodeWrapper> void updateDataPointSelection(final String projectId, final NodeIDEnt workflowId,
        final VersionId versionId, final NodeIDEnt nodeId, final String mode, final List<String> selection,
        final Function<NodeContainer, N> getNodeWrapper) throws NodeNotFoundException {
        var nc = assertProjectIdAndGetNodeContainer(projectId, workflowId, versionId, nodeId);
        var nodeWrapper = getNodeWrapper.apply(nc);
        TableViewManager<N> tableViewManager;
        if (nodeWrapper instanceof NodePortWrapper) {
            tableViewManager = (TableViewManager<N>)PortViewManager.getInstance().getTableViewManager();
        } else {
            tableViewManager = (TableViewManager<N>)NodeViewManager.getInstance().getTableViewManager();
        }

        Set<RowKey> rowKeys;
        try {
            rowKeys = tableViewManager.callSelectionTranslationService(nodeWrapper, selection);
        } catch (IOException ex) {
            throw new IllegalStateException("Problem translating selection to row keys", ex);
        }
        var hiLiteHandler = tableViewManager.getHiLiteHandler(nodeWrapper).orElseThrow();
        final var selectionEventMode = SelectionEventEnt.ModeEnum.valueOf(mode.toUpperCase(Locale.ROOT));
        SelectionEventBus.processSelectionEvent(hiLiteHandler, nc.getID(), selectionEventMode, true, rowKeys);
    }

}
