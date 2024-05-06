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
 *   Apr 30, 2024 (hornm): created
 */
package org.knime.gateway.api.webui.util;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeMessage.Type;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.virtual.subnode.VirtualSubNodeInputNodeFactory;
import org.knime.core.node.workflow.virtual.subnode.VirtualSubNodeOutputNodeFactory;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.WorkflowMonitorMessageEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorMessageEnt.WorkflowMonitorMessageEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateEnt.WorkflowMonitorStateEntBuilder;

/**
 * See {@link EntityFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("static-method")
public final class WorkflowMonitorStateEntityFactory {

    private static final Set<Class<?>> IGNORED_NODES =
        Set.of(VirtualSubNodeInputNodeFactory.class, VirtualSubNodeOutputNodeFactory.class);

    WorkflowMonitorStateEntityFactory() {
        //
    }

    /**
     * Builds {@link WorkflowMonitorStateEnt} instances.
     *
     * @param wfm
     * @return the new instance
     */
    public WorkflowMonitorStateEnt buildWorkflowMonitorStateEnt(final WorkflowManager wfm) {
        var startTimeDescending = Comparator.<NativeNodeContainer> comparingLong(nnc -> nnc.getNodeTimer().getStartTime())
            .thenComparing(NativeNodeContainer::getID).reversed();
        var nodesWithMessages = new TreeSet<>(startTimeDescending);

        CoreUtil.iterateNodes(wfm, nnc -> {
            if (!IGNORED_NODES.contains(nnc.getNode().getFactory().getClass())) {
                var messageType = nnc.getNodeMessage().getMessageType();
                if (messageType != Type.RESET) {
                    nodesWithMessages.add(nnc);
                }
            }
        }, subWfm -> !subWfm.isEncrypted());

        var errors = new ArrayList<WorkflowMonitorMessageEnt>();
        var warnings = new ArrayList<WorkflowMonitorMessageEnt>();
        var hasComponentProjectParent = wfm.getProjectComponent().isPresent();
        nodesWithMessages.forEach(nnc -> {
            var message = nnc.getNodeMessage();
            if (message.getMessageType() == Type.ERROR) {
                errors.add(buildWorkflowMonitorMessageEnt(nnc, hasComponentProjectParent));
            } else {
                warnings.add(buildWorkflowMonitorMessageEnt(nnc, hasComponentProjectParent));
            }
        });

        return builder(WorkflowMonitorStateEntBuilder.class) //
            .setErrors(errors) //
            .setWarnings(warnings) //
            .build();
    }

    private static WorkflowMonitorMessageEnt buildWorkflowMonitorMessageEnt(final NativeNodeContainer nnc,
        final boolean hasComponentProjectParent) {
        NodeContainer parent = nnc.getParent();
        if (parent.getDirectNCParent() instanceof SubNodeContainer snc) {
            parent = snc;
        }
        return builder(WorkflowMonitorMessageEntBuilder.class) //
            .setNodeId(new NodeIDEnt(nnc.getID(), hasComponentProjectParent)) //
            .setName(nnc.getName()) //
            .setTemplateId(nnc.getNode().getFactory().getFactoryId()) //
            .setMessage(nnc.getNodeMessage().getMessage()) //
            .setWorkflowId(new NodeIDEnt(parent.getID(), hasComponentProjectParent)) //
            .build();
    }

}
