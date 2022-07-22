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
package org.knime.gateway.json.webui.entity.util;

import org.knime.gateway.json.webui.entity.AddNodeCommandEntMixIn;
import org.knime.gateway.json.webui.entity.AddPortCommandEntMixIn;
import org.knime.gateway.json.webui.entity.AllowedActionsEntMixIn;
import org.knime.gateway.json.webui.entity.AllowedConnectionActionsEntMixIn;
import org.knime.gateway.json.webui.entity.AllowedLoopActionsEntMixIn;
import org.knime.gateway.json.webui.entity.AllowedNodeActionsEntMixIn;
import org.knime.gateway.json.webui.entity.AllowedWorkflowActionsEntMixIn;
import org.knime.gateway.json.webui.entity.AnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.AppStateChangedEventEntMixIn;
import org.knime.gateway.json.webui.entity.AppStateChangedEventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.AppStateEntMixIn;
import org.knime.gateway.json.webui.entity.BoundsEntMixIn;
import org.knime.gateway.json.webui.entity.CollapseCommandEntMixIn;
import org.knime.gateway.json.webui.entity.CollapseResultEntMixIn;
import org.knime.gateway.json.webui.entity.CommandResultEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentNodeAndDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentNodeDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentNodeEntMixIn;
import org.knime.gateway.json.webui.entity.ConnectCommandEntMixIn;
import org.knime.gateway.json.webui.entity.ConnectionEntMixIn;
import org.knime.gateway.json.webui.entity.ConvertContainerResultEntMixIn;
import org.knime.gateway.json.webui.entity.CopyCommandEntMixIn;
import org.knime.gateway.json.webui.entity.CopyResultEntMixIn;
import org.knime.gateway.json.webui.entity.CustomJobManagerEntMixIn;
import org.knime.gateway.json.webui.entity.CutCommandEntMixIn;
import org.knime.gateway.json.webui.entity.DeleteCommandEntMixIn;
import org.knime.gateway.json.webui.entity.DynamicPortGroupDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.EventEntMixIn;
import org.knime.gateway.json.webui.entity.EventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.ExpandCommandEntMixIn;
import org.knime.gateway.json.webui.entity.ExpandResultEntMixIn;
import org.knime.gateway.json.webui.entity.JobManagerEntMixIn;
import org.knime.gateway.json.webui.entity.LinkEntMixIn;
import org.knime.gateway.json.webui.entity.LoopInfoEntMixIn;
import org.knime.gateway.json.webui.entity.MetaNodeEntMixIn;
import org.knime.gateway.json.webui.entity.MetaNodePortEntMixIn;
import org.knime.gateway.json.webui.entity.MetaNodeStateEntMixIn;
import org.knime.gateway.json.webui.entity.MetaPortsEntMixIn;
import org.knime.gateway.json.webui.entity.NativeNodeDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.NativeNodeEntMixIn;
import org.knime.gateway.json.webui.entity.NativeNodeInvariantsEntMixIn;
import org.knime.gateway.json.webui.entity.NodeAnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.NodeDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.NodeDialogOptionDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.NodeDialogOptionGroupEntMixIn;
import org.knime.gateway.json.webui.entity.NodeEntMixIn;
import org.knime.gateway.json.webui.entity.NodeExecutionInfoEntMixIn;
import org.knime.gateway.json.webui.entity.NodeFactoryKeyEntMixIn;
import org.knime.gateway.json.webui.entity.NodeGroupEntMixIn;
import org.knime.gateway.json.webui.entity.NodeGroupsEntMixIn;
import org.knime.gateway.json.webui.entity.NodePortDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.NodePortEntMixIn;
import org.knime.gateway.json.webui.entity.NodePortTemplateEntMixIn;
import org.knime.gateway.json.webui.entity.NodeSearchResultEntMixIn;
import org.knime.gateway.json.webui.entity.NodeStateEntMixIn;
import org.knime.gateway.json.webui.entity.NodeTemplateEntMixIn;
import org.knime.gateway.json.webui.entity.NodeViewDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.PartBasedCommandEntMixIn;
import org.knime.gateway.json.webui.entity.PasteCommandEntMixIn;
import org.knime.gateway.json.webui.entity.PasteResultEntMixIn;
import org.knime.gateway.json.webui.entity.PatchEntMixIn;
import org.knime.gateway.json.webui.entity.PatchOpEntMixIn;
import org.knime.gateway.json.webui.entity.PortCommandEntMixIn;
import org.knime.gateway.json.webui.entity.PortGroupEntMixIn;
import org.knime.gateway.json.webui.entity.PortTypeEntMixIn;
import org.knime.gateway.json.webui.entity.ProjectMetadataEntMixIn;
import org.knime.gateway.json.webui.entity.RemovePortCommandEntMixIn;
import org.knime.gateway.json.webui.entity.StyleRangeEntMixIn;
import org.knime.gateway.json.webui.entity.TranslateCommandEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateComponentOrMetanodeNameCommandEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowAnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowChangedEventEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowChangedEventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowCommandEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowInfoEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowProjectEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowSnapshotEntMixIn;
import org.knime.gateway.json.webui.entity.XYEntMixIn;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>com.knime.gateway.jsonrpc.entity</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
public class ListEntities {

    private ListEntities() {
        //utility class
    }

    /**
     * Lists all gateway entity classes of package <code>com.knime.gateway.jsonrpc.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AddNodeCommandEntMixIn.class);
        res.add(AddPortCommandEntMixIn.class);
        res.add(AllowedActionsEntMixIn.class);
        res.add(AllowedConnectionActionsEntMixIn.class);
        res.add(AllowedLoopActionsEntMixIn.class);
        res.add(AllowedNodeActionsEntMixIn.class);
        res.add(AllowedWorkflowActionsEntMixIn.class);
        res.add(AnnotationEntMixIn.class);
        res.add(AppStateChangedEventEntMixIn.class);
        res.add(AppStateChangedEventTypeEntMixIn.class);
        res.add(AppStateEntMixIn.class);
        res.add(BoundsEntMixIn.class);
        res.add(CollapseCommandEntMixIn.class);
        res.add(CollapseResultEntMixIn.class);
        res.add(CommandResultEntMixIn.class);
        res.add(ComponentNodeAndDescriptionEntMixIn.class);
        res.add(ComponentNodeDescriptionEntMixIn.class);
        res.add(ComponentNodeEntMixIn.class);
        res.add(ConnectCommandEntMixIn.class);
        res.add(ConnectionEntMixIn.class);
        res.add(ConvertContainerResultEntMixIn.class);
        res.add(CopyCommandEntMixIn.class);
        res.add(CopyResultEntMixIn.class);
        res.add(CustomJobManagerEntMixIn.class);
        res.add(CutCommandEntMixIn.class);
        res.add(DeleteCommandEntMixIn.class);
        res.add(DynamicPortGroupDescriptionEntMixIn.class);
        res.add(EventEntMixIn.class);
        res.add(EventTypeEntMixIn.class);
        res.add(ExpandCommandEntMixIn.class);
        res.add(ExpandResultEntMixIn.class);
        res.add(JobManagerEntMixIn.class);
        res.add(LinkEntMixIn.class);
        res.add(LoopInfoEntMixIn.class);
        res.add(MetaNodeEntMixIn.class);
        res.add(MetaNodePortEntMixIn.class);
        res.add(MetaNodeStateEntMixIn.class);
        res.add(MetaPortsEntMixIn.class);
        res.add(NativeNodeDescriptionEntMixIn.class);
        res.add(NativeNodeEntMixIn.class);
        res.add(NativeNodeInvariantsEntMixIn.class);
        res.add(NodeAnnotationEntMixIn.class);
        res.add(NodeDescriptionEntMixIn.class);
        res.add(NodeDialogOptionDescriptionEntMixIn.class);
        res.add(NodeDialogOptionGroupEntMixIn.class);
        res.add(NodeEntMixIn.class);
        res.add(NodeExecutionInfoEntMixIn.class);
        res.add(NodeFactoryKeyEntMixIn.class);
        res.add(NodeGroupEntMixIn.class);
        res.add(NodeGroupsEntMixIn.class);
        res.add(NodePortDescriptionEntMixIn.class);
        res.add(NodePortEntMixIn.class);
        res.add(NodePortTemplateEntMixIn.class);
        res.add(NodeSearchResultEntMixIn.class);
        res.add(NodeStateEntMixIn.class);
        res.add(NodeTemplateEntMixIn.class);
        res.add(NodeViewDescriptionEntMixIn.class);
        res.add(PartBasedCommandEntMixIn.class);
        res.add(PasteCommandEntMixIn.class);
        res.add(PasteResultEntMixIn.class);
        res.add(PatchEntMixIn.class);
        res.add(PatchOpEntMixIn.class);
        res.add(PortCommandEntMixIn.class);
        res.add(PortGroupEntMixIn.class);
        res.add(PortTypeEntMixIn.class);
        res.add(ProjectMetadataEntMixIn.class);
        res.add(RemovePortCommandEntMixIn.class);
        res.add(StyleRangeEntMixIn.class);
        res.add(TranslateCommandEntMixIn.class);
        res.add(UpdateComponentOrMetanodeNameCommandEntMixIn.class);
        res.add(WorkflowAnnotationEntMixIn.class);
        res.add(WorkflowChangedEventEntMixIn.class);
        res.add(WorkflowChangedEventTypeEntMixIn.class);
        res.add(WorkflowCommandEntMixIn.class);
        res.add(WorkflowEntMixIn.class);
        res.add(WorkflowInfoEntMixIn.class);
        res.add(WorkflowProjectEntMixIn.class);
        res.add(WorkflowSnapshotEntMixIn.class);
        res.add(XYEntMixIn.class);
        return res;
    }
    
    /**
     * Lists all gateway entity builder classes of package <code>com.knime.gateway.jsonrpc.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityBuilderClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AddNodeCommandEntMixIn.AddNodeCommandEntMixInBuilder.class);
        res.add(AddPortCommandEntMixIn.AddPortCommandEntMixInBuilder.class);
        res.add(AllowedActionsEntMixIn.AllowedActionsEntMixInBuilder.class);
        res.add(AllowedConnectionActionsEntMixIn.AllowedConnectionActionsEntMixInBuilder.class);
        res.add(AllowedLoopActionsEntMixIn.AllowedLoopActionsEntMixInBuilder.class);
        res.add(AllowedNodeActionsEntMixIn.AllowedNodeActionsEntMixInBuilder.class);
        res.add(AllowedWorkflowActionsEntMixIn.AllowedWorkflowActionsEntMixInBuilder.class);
        res.add(AnnotationEntMixIn.AnnotationEntMixInBuilder.class);
        res.add(AppStateChangedEventEntMixIn.AppStateChangedEventEntMixInBuilder.class);
        res.add(AppStateChangedEventTypeEntMixIn.AppStateChangedEventTypeEntMixInBuilder.class);
        res.add(AppStateEntMixIn.AppStateEntMixInBuilder.class);
        res.add(BoundsEntMixIn.BoundsEntMixInBuilder.class);
        res.add(CollapseCommandEntMixIn.CollapseCommandEntMixInBuilder.class);
        res.add(CollapseResultEntMixIn.CollapseResultEntMixInBuilder.class);
        res.add(CommandResultEntMixIn.CommandResultEntMixInBuilder.class);
        res.add(ComponentNodeAndDescriptionEntMixIn.ComponentNodeAndDescriptionEntMixInBuilder.class);
        res.add(ComponentNodeDescriptionEntMixIn.ComponentNodeDescriptionEntMixInBuilder.class);
        res.add(ComponentNodeEntMixIn.ComponentNodeEntMixInBuilder.class);
        res.add(ConnectCommandEntMixIn.ConnectCommandEntMixInBuilder.class);
        res.add(ConnectionEntMixIn.ConnectionEntMixInBuilder.class);
        res.add(ConvertContainerResultEntMixIn.ConvertContainerResultEntMixInBuilder.class);
        res.add(CopyCommandEntMixIn.CopyCommandEntMixInBuilder.class);
        res.add(CopyResultEntMixIn.CopyResultEntMixInBuilder.class);
        res.add(CustomJobManagerEntMixIn.CustomJobManagerEntMixInBuilder.class);
        res.add(CutCommandEntMixIn.CutCommandEntMixInBuilder.class);
        res.add(DeleteCommandEntMixIn.DeleteCommandEntMixInBuilder.class);
        res.add(DynamicPortGroupDescriptionEntMixIn.DynamicPortGroupDescriptionEntMixInBuilder.class);
        res.add(EventEntMixIn.EventEntMixInBuilder.class);
        res.add(EventTypeEntMixIn.EventTypeEntMixInBuilder.class);
        res.add(ExpandCommandEntMixIn.ExpandCommandEntMixInBuilder.class);
        res.add(ExpandResultEntMixIn.ExpandResultEntMixInBuilder.class);
        res.add(JobManagerEntMixIn.JobManagerEntMixInBuilder.class);
        res.add(LinkEntMixIn.LinkEntMixInBuilder.class);
        res.add(LoopInfoEntMixIn.LoopInfoEntMixInBuilder.class);
        res.add(MetaNodeEntMixIn.MetaNodeEntMixInBuilder.class);
        res.add(MetaNodePortEntMixIn.MetaNodePortEntMixInBuilder.class);
        res.add(MetaNodeStateEntMixIn.MetaNodeStateEntMixInBuilder.class);
        res.add(MetaPortsEntMixIn.MetaPortsEntMixInBuilder.class);
        res.add(NativeNodeDescriptionEntMixIn.NativeNodeDescriptionEntMixInBuilder.class);
        res.add(NativeNodeEntMixIn.NativeNodeEntMixInBuilder.class);
        res.add(NativeNodeInvariantsEntMixIn.NativeNodeInvariantsEntMixInBuilder.class);
        res.add(NodeAnnotationEntMixIn.NodeAnnotationEntMixInBuilder.class);
        res.add(NodeDescriptionEntMixIn.NodeDescriptionEntMixInBuilder.class);
        res.add(NodeDialogOptionDescriptionEntMixIn.NodeDialogOptionDescriptionEntMixInBuilder.class);
        res.add(NodeDialogOptionGroupEntMixIn.NodeDialogOptionGroupEntMixInBuilder.class);
        res.add(NodeEntMixIn.NodeEntMixInBuilder.class);
        res.add(NodeExecutionInfoEntMixIn.NodeExecutionInfoEntMixInBuilder.class);
        res.add(NodeFactoryKeyEntMixIn.NodeFactoryKeyEntMixInBuilder.class);
        res.add(NodeGroupEntMixIn.NodeGroupEntMixInBuilder.class);
        res.add(NodeGroupsEntMixIn.NodeGroupsEntMixInBuilder.class);
        res.add(NodePortDescriptionEntMixIn.NodePortDescriptionEntMixInBuilder.class);
        res.add(NodePortEntMixIn.NodePortEntMixInBuilder.class);
        res.add(NodePortTemplateEntMixIn.NodePortTemplateEntMixInBuilder.class);
        res.add(NodeSearchResultEntMixIn.NodeSearchResultEntMixInBuilder.class);
        res.add(NodeStateEntMixIn.NodeStateEntMixInBuilder.class);
        res.add(NodeTemplateEntMixIn.NodeTemplateEntMixInBuilder.class);
        res.add(NodeViewDescriptionEntMixIn.NodeViewDescriptionEntMixInBuilder.class);
        res.add(PartBasedCommandEntMixIn.PartBasedCommandEntMixInBuilder.class);
        res.add(PasteCommandEntMixIn.PasteCommandEntMixInBuilder.class);
        res.add(PasteResultEntMixIn.PasteResultEntMixInBuilder.class);
        res.add(PatchEntMixIn.PatchEntMixInBuilder.class);
        res.add(PatchOpEntMixIn.PatchOpEntMixInBuilder.class);
        res.add(PortCommandEntMixIn.PortCommandEntMixInBuilder.class);
        res.add(PortGroupEntMixIn.PortGroupEntMixInBuilder.class);
        res.add(PortTypeEntMixIn.PortTypeEntMixInBuilder.class);
        res.add(ProjectMetadataEntMixIn.ProjectMetadataEntMixInBuilder.class);
        res.add(RemovePortCommandEntMixIn.RemovePortCommandEntMixInBuilder.class);
        res.add(StyleRangeEntMixIn.StyleRangeEntMixInBuilder.class);
        res.add(TranslateCommandEntMixIn.TranslateCommandEntMixInBuilder.class);
        res.add(UpdateComponentOrMetanodeNameCommandEntMixIn.UpdateComponentOrMetanodeNameCommandEntMixInBuilder.class);
        res.add(WorkflowAnnotationEntMixIn.WorkflowAnnotationEntMixInBuilder.class);
        res.add(WorkflowChangedEventEntMixIn.WorkflowChangedEventEntMixInBuilder.class);
        res.add(WorkflowChangedEventTypeEntMixIn.WorkflowChangedEventTypeEntMixInBuilder.class);
        res.add(WorkflowCommandEntMixIn.WorkflowCommandEntMixInBuilder.class);
        res.add(WorkflowEntMixIn.WorkflowEntMixInBuilder.class);
        res.add(WorkflowInfoEntMixIn.WorkflowInfoEntMixInBuilder.class);
        res.add(WorkflowProjectEntMixIn.WorkflowProjectEntMixInBuilder.class);
        res.add(WorkflowSnapshotEntMixIn.WorkflowSnapshotEntMixInBuilder.class);
        res.add(XYEntMixIn.XYEntMixInBuilder.class);
        return res;
    }
}
