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
 *   Nov 10, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Collections;
import java.util.Set;

import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.TransformMetanodePortsBarCommandEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.service.util.WorkflowChangesTracker.WorkflowChange;

/**
 * Changes the bounds of a metanode ports bar.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class TransformMetanodePortsBar extends AbstractWorkflowCommand implements WithResult {

    private final TransformMetanodePortsBarCommandEnt m_command;

    private Runnable m_undo;

    TransformMetanodePortsBar(final TransformMetanodePortsBarCommandEnt command) {
        m_command = command;
    }

    @Override
    protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {
        var wfm = getWorkflowManager();
        if (CoreUtil.isComponentWFM(wfm)) {
            throw new ServiceCallException("Component don't have metanode ports bars. Can't be transformed.");
        }
        var bounds = m_command.getBounds();
        switch (m_command.getType()) {
            case IN -> {
                var uiInfo = wfm.getInPortsBarUIInfo();
                wfm.setInPortsBarUIInfo(updateBounds(uiInfo, bounds));
                m_undo = () -> wfm.setInPortsBarUIInfo(uiInfo);
            }
            case OUT -> {
                var uiInfo = wfm.getOutPortsBarUIInfo();
                wfm.setOutPortsBarUIInfo(updateBounds(uiInfo, bounds));
                m_undo = () -> wfm.setOutPortsBarUIInfo(uiInfo);
            }
        }
        return true;
    }

    private static NodeUIInformation updateBounds(final NodeUIInformation uiInfo, final BoundsEnt bounds) {
        var res = uiInfo == null ? NodeUIInformation.builder() : NodeUIInformation.builder(uiInfo);
        res.setNodeLocation(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        return res.build();
    }

    @Override
    public void undo() throws ServiceCallException {
        m_undo.run();
        m_undo = null;
    }

    @Override
    public CommandResultEnt buildEntity(final String snapshotId) {
        return builder(CommandResultEnt.CommandResultEntBuilder.class).setSnapshotId(snapshotId).build();
    }

    @Override
    public Set<WorkflowChange> getChangesToWaitFor() {
        return Collections.singleton(WorkflowChange.PORTS_BAR_MOVED);
    }

}
