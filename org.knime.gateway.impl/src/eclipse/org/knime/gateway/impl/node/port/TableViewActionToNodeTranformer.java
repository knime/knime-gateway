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
 *   8 Nov 2024 (baqueroj): created
 */
package org.knime.gateway.impl.node.port;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.AddNodeCommandEntBuilder;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.NodeRelationEnum;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.webui.service.DefaultWorkflowService;

/**
 *
 * @author baqueroj
 */
public abstract class TableViewActionToNodeTranformer {

    String className;
    String projectId;
    NodeIDEnt workflowId;
    WorkflowManager wfm;

    /**
     * @param className
     */
    public TableViewActionToNodeTranformer(final String className, final String projectId, final NodeIDEnt workflowId,
        final WorkflowManager wfm) {
        this.className = className;
        this.projectId = projectId;
        this.workflowId = workflowId;
        this.wfm = wfm;
    }

    public final SingleNodeContainer toNode(final SingleNodeContainer sourceNode) {
        var command = toAddNodeCommand(new NodeIDEnt(sourceNode.getID()));

        AddNodeResultEnt addCommandResult;
        try {
            addCommandResult = (AddNodeResultEnt)DefaultWorkflowService.getInstance().executeWorkflowCommand(projectId,
                workflowId, command);
            var newNodeId = addCommandResult.getNewNodeId();
            var newNode = wfm.getNodeContainer(newNodeId.toNodeID(sourceNode));
            toConfigure((SingleNodeContainer)newNode, wfm);
            return (SingleNodeContainer) newNode;
        } catch (NotASubWorkflowException | NodeNotFoundException | OperationNotAllowedException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        }
    }

    private final AddNodeCommandEnt toAddNodeCommand(final NodeIDEnt sourceNode) {
        final var addCommandBuilder = builder(AddNodeCommandEntBuilder.class).setNodeFactory(//
            builder(NodeFactoryKeyEntBuilder.class)//
                // .setClassName("org.knime.base.node.preproc.filter.row3.RowFilterNodeFactory")//
                // .setClassName("org.knime.base.node.preproc.sorter.SorterNodeFactory")//
                //.setClassName("org.knime.base.node.preproc.filter.hilite.HiliteFilterNodeFactory")//
                .setClassName(className)//
                .build()//
        )//
             .setPosition(builder(XYEntBuilder.class)//
                    .setX(100)//
                    .setY(100)//
                    .build())
            .setNodeRelation(NodeRelationEnum.SUCCESSORS)
            .setKind(KindEnum.ADD_NODE).setSourceNodeId(sourceNode)
            .build();
        return addCommandBuilder;
    }

    /**
     * @param nc
     * @param wfm
     */
    protected void toConfigure(final SingleNodeContainer nc, final WorkflowManager wfm) {
        // Empty by default, for nodes that require no configuration
    }
}
