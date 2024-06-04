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
import org.knime.gateway.api.webui.entity.ComponentNodeAndDescriptionEnt;
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
        var startTimeDescending = Comparator //
            .<NodeContainer> comparingLong(nc -> nc.getNodeTimer().getStartTime()) //
            .thenComparing(NodeContainer::getID) //
            .reversed();

        var nodesWithMessages = new TreeSet<>(startTimeDescending);

        CoreUtil.iterateNodes(
                wfm, //
                nc -> collectMessages(nc, nodesWithMessages) //
        );

        var errors = new ArrayList<WorkflowMonitorMessageEnt>();
        var warnings = new ArrayList<WorkflowMonitorMessageEnt>();
        var hasComponentProjectParent = wfm.getProjectComponent().isPresent();
        nodesWithMessages.forEach(nc -> {
            var message = nc.getNodeMessage();
            if (message.getMessageType() == Type.ERROR) {
                errors.add(buildWorkflowMonitorMessageEnt(nc, hasComponentProjectParent));
            } else {
                warnings.add(buildWorkflowMonitorMessageEnt(nc, hasComponentProjectParent));
            }
        });

        return builder(WorkflowMonitorStateEntBuilder.class) //
            .setErrors(errors) //
            .setWarnings(warnings) //
            .build();
    }

    private static boolean collectMessages(final NodeContainer nc, final TreeSet<NodeContainer> nodesWithMessages) {
        var visit = checkNode(nc);
        if (visit.doReportMessages()) {
            var messageType = nc.getNodeMessage().getMessageType();
            if (messageType != Type.RESET) {
                nodesWithMessages.add(nc);
            }
        }
        return visit.doRecurse();
    }

    /**
     * @param doRecurse Whether to recurse into this node (if possible)
     * @param doReportMessages Whether to report messages for this node
     */
    private record NodeVisit(boolean doRecurse, boolean doReportMessages) {

    }

    private static NodeVisit checkNode(final NodeContainer nc) {
        if (nc instanceof WorkflowManager) {
            return new NodeVisit(true, false);
        }
        if ((nc instanceof NativeNodeContainer nnc) && IGNORED_NODES.contains(nnc.getNode().getFactory().getClass())) {
            return new NodeVisit(false, false);
        } else if (CoreUtil.isLocked(nc) || CoreUtil.isLinked(nc)) {
            return new NodeVisit(false, true);
        } else if (nc instanceof SubNodeContainer) {
            return new NodeVisit(true, false);
        }
        return new NodeVisit(true, true);
    }

    private static WorkflowMonitorMessageEnt buildWorkflowMonitorMessageEnt(final NodeContainer nc,
        final boolean hasComponentProjectParent) {
        NodeContainer parent = nc.getParent();
        if (parent.getDirectNCParent() instanceof SubNodeContainer snc) {
            parent = snc;
        }
        var builder = builder(WorkflowMonitorMessageEntBuilder.class) //
            .setNodeId(new NodeIDEnt(nc.getID(), hasComponentProjectParent)) //
            .setName(nc.getName()) //
            .setMessage(nc.getNodeMessage().getMessage()) //
            .setWorkflowId(new NodeIDEnt(parent.getID(), hasComponentProjectParent));
        if (nc instanceof NativeNodeContainer nnc) {
            builder.setTemplateId(nnc.getNode().getFactory().getFactoryId());
        }
        if (nc instanceof SubNodeContainer snc) {
            builder.setComponentInfo(
                    builder(ComponentNodeAndDescriptionEnt.ComponentNodeAndDescriptionEntBuilder.class) //
                            .setName(snc.getName()) //
                            .setType(WorkflowEntityFactory.buildComponentTypeEnt(snc)) //
                            .setIcon(WorkflowEntityFactory.buildComponentIconEnt(snc)) //
                            .build() //
            );
        }
        return builder.build();
    }

}
