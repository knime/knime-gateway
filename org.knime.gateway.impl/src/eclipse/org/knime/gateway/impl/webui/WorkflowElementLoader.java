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
 *   Mar 13, 2025 (hornm): created
 */
package org.knime.gateway.impl.webui;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.NodePlaceholder;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;

/**
 * TODO
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
// TODO thread safety?
public final class WorkflowElementLoader {

    private final Map<NodeIDEnt, LoadingOperation> m_loadingOperations = new LinkedHashMap<>();

    private final WorkflowChangesListener m_workflowChangesListener;

    /**
     * TODO
     */
    WorkflowElementLoader(final WorkflowChangesListener workflowChangesListener) {
        m_workflowChangesListener = workflowChangesListener;
    }

    /**
     * TODO
     *
     * @param wfKey
     * @param nodeId
     * @param x
     * @param y
     * @param loadingOperation
     */
    public void addComponentLoader(final NodeIDEnt nodeId, final int x, final int y, final Runnable loadingOperation) {
        var future = CompletableFuture.runAsync(loadingOperation).handle((res, ex) -> {
            if (ex == null) {
                m_loadingOperations.remove(nodeId);
                m_workflowChangesListener.trigger(WorkflowChange.PLACERHOLDER_REMOVED);
            } else {
                // update placeholder with error
            }
            return null;
        });
        m_loadingOperations.put(nodeId,
            new LoadingOperation(future, new NodePlaceholder(nodeId, NodePlaceholder.Type.COMPONENT, "TODO", x, y)));
        m_workflowChangesListener.trigger(WorkflowChange.PLACERHOLDER_ADDED);
    }

    /**
     * TODO
     *
     * @return
     */
    public Collection<NodePlaceholder> getPlaceholders() {
        return m_loadingOperations.values().stream().map(LoadingOperation::placeholder).toList();
    }

    private record LoadingOperation(CompletableFuture<?> future, NodePlaceholder placeholder) {

    }

}
