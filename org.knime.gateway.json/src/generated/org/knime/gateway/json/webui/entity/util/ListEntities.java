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

import org.knime.gateway.json.webui.entity.AddAnnotationResultEntMixIn;
import org.knime.gateway.json.webui.entity.AddBendpointCommandEntMixIn;
import org.knime.gateway.json.webui.entity.AddComponentCommandEntMixIn;
import org.knime.gateway.json.webui.entity.AddComponentPlaceholderResultEntMixIn;
import org.knime.gateway.json.webui.entity.AddNodeCommandEntMixIn;
import org.knime.gateway.json.webui.entity.AddNodeResultEntMixIn;
import org.knime.gateway.json.webui.entity.AddPortCommandEntMixIn;
import org.knime.gateway.json.webui.entity.AddPortResultEntMixIn;
import org.knime.gateway.json.webui.entity.AddWorkflowAnnotationCommandEntMixIn;
import org.knime.gateway.json.webui.entity.AlignNodesCommandEntMixIn;
import org.knime.gateway.json.webui.entity.AllowedActionsEntMixIn;
import org.knime.gateway.json.webui.entity.AllowedConnectionActionsEntMixIn;
import org.knime.gateway.json.webui.entity.AllowedLoopActionsEntMixIn;
import org.knime.gateway.json.webui.entity.AllowedNodeActionsEntMixIn;
import org.knime.gateway.json.webui.entity.AllowedWorkflowActionsEntMixIn;
import org.knime.gateway.json.webui.entity.AnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.AppStateChangedEventEntMixIn;
import org.knime.gateway.json.webui.entity.AppStateChangedEventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.AppStateEntMixIn;
import org.knime.gateway.json.webui.entity.AutoConnectCommandEntMixIn;
import org.knime.gateway.json.webui.entity.AutoDisconnectCommandEntMixIn;
import org.knime.gateway.json.webui.entity.BoundsEntMixIn;
import org.knime.gateway.json.webui.entity.CategoryMetadataEntMixIn;
import org.knime.gateway.json.webui.entity.CollapseCommandEntMixIn;
import org.knime.gateway.json.webui.entity.CollapseResultEntMixIn;
import org.knime.gateway.json.webui.entity.CommandResultEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentNodeAndDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentNodeDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentNodeEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentPlaceholderEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentPortDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.CompositeEventEntMixIn;
import org.knime.gateway.json.webui.entity.ConnectCommandEntMixIn;
import org.knime.gateway.json.webui.entity.ConnectablesBasedCommandEntMixIn;
import org.knime.gateway.json.webui.entity.ConnectionEntMixIn;
import org.knime.gateway.json.webui.entity.ConvertContainerResultEntMixIn;
import org.knime.gateway.json.webui.entity.CopyCommandEntMixIn;
import org.knime.gateway.json.webui.entity.CopyResultEntMixIn;
import org.knime.gateway.json.webui.entity.CustomJobManagerEntMixIn;
import org.knime.gateway.json.webui.entity.CutCommandEntMixIn;
import org.knime.gateway.json.webui.entity.DeleteCommandEntMixIn;
import org.knime.gateway.json.webui.entity.DeleteComponentPlaceholderCommandEntMixIn;
import org.knime.gateway.json.webui.entity.DynamicPortGroupDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.EditableMetadataEntMixIn;
import org.knime.gateway.json.webui.entity.EventEntMixIn;
import org.knime.gateway.json.webui.entity.EventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.ExpandCommandEntMixIn;
import org.knime.gateway.json.webui.entity.ExpandResultEntMixIn;
import org.knime.gateway.json.webui.entity.ExtensionEntMixIn;
import org.knime.gateway.json.webui.entity.GatewayProblemDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.InsertNodeCommandEntMixIn;
import org.knime.gateway.json.webui.entity.JobManagerEntMixIn;
import org.knime.gateway.json.webui.entity.KaiFeedbackEntMixIn;
import org.knime.gateway.json.webui.entity.KaiMessageEntMixIn;
import org.knime.gateway.json.webui.entity.KaiRequestEntMixIn;
import org.knime.gateway.json.webui.entity.KaiUiStringsEntMixIn;
import org.knime.gateway.json.webui.entity.KaiWelcomeMessagesEntMixIn;
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
import org.knime.gateway.json.webui.entity.NodeCategoryEntMixIn;
import org.knime.gateway.json.webui.entity.NodeDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.NodeDialogOptionDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.NodeDialogOptionGroupEntMixIn;
import org.knime.gateway.json.webui.entity.NodeEntMixIn;
import org.knime.gateway.json.webui.entity.NodeExecutionInfoEntMixIn;
import org.knime.gateway.json.webui.entity.NodeFactoryKeyEntMixIn;
import org.knime.gateway.json.webui.entity.NodeGroupEntMixIn;
import org.knime.gateway.json.webui.entity.NodeGroupsEntMixIn;
import org.knime.gateway.json.webui.entity.NodeIdAndIsExecutedEntMixIn;
import org.knime.gateway.json.webui.entity.NodePortDescriptionEntMixIn;
import org.knime.gateway.json.webui.entity.NodePortEntMixIn;
import org.knime.gateway.json.webui.entity.NodePortTemplateEntMixIn;
import org.knime.gateway.json.webui.entity.NodeRepositoryLoadingProgressEventEntMixIn;
import org.knime.gateway.json.webui.entity.NodeRepositoryLoadingProgressEventTypeEntMixIn;
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
import org.knime.gateway.json.webui.entity.PortViewDescriptorEntMixIn;
import org.knime.gateway.json.webui.entity.PortViewDescriptorMappingEntMixIn;
import org.knime.gateway.json.webui.entity.PortViewsEntMixIn;
import org.knime.gateway.json.webui.entity.ProjectDirtyStateEventEntMixIn;
import org.knime.gateway.json.webui.entity.ProjectDisposedEventEntMixIn;
import org.knime.gateway.json.webui.entity.ProjectDisposedEventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.ProjectEntMixIn;
import org.knime.gateway.json.webui.entity.ProjectMetadataEntMixIn;
import org.knime.gateway.json.webui.entity.RemovePortCommandEntMixIn;
import org.knime.gateway.json.webui.entity.ReorderWorkflowAnnotationsCommandEntMixIn;
import org.knime.gateway.json.webui.entity.ReplaceNodeCommandEntMixIn;
import org.knime.gateway.json.webui.entity.SelectionEventEntMixIn;
import org.knime.gateway.json.webui.entity.ShowToastEventEntMixIn;
import org.knime.gateway.json.webui.entity.SpaceEntMixIn;
import org.knime.gateway.json.webui.entity.SpaceGroupEntMixIn;
import org.knime.gateway.json.webui.entity.SpaceItemChangedEventEntMixIn;
import org.knime.gateway.json.webui.entity.SpaceItemChangedEventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.SpaceItemEntMixIn;
import org.knime.gateway.json.webui.entity.SpaceItemReferenceEntMixIn;
import org.knime.gateway.json.webui.entity.SpaceItemVersionEntMixIn;
import org.knime.gateway.json.webui.entity.SpacePathSegmentEntMixIn;
import org.knime.gateway.json.webui.entity.SpaceProviderEntMixIn;
import org.knime.gateway.json.webui.entity.StyleRangeEntMixIn;
import org.knime.gateway.json.webui.entity.TemplateLinkEntMixIn;
import org.knime.gateway.json.webui.entity.TransformMetanodePortsBarCommandEntMixIn;
import org.knime.gateway.json.webui.entity.TransformWorkflowAnnotationCommandEntMixIn;
import org.knime.gateway.json.webui.entity.TranslateCommandEntMixIn;
import org.knime.gateway.json.webui.entity.TypedTextEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateAvailableEventEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateAvailableEventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateComponentLinkInformationCommandEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateComponentMetadataCommandEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateComponentOrMetanodeNameCommandEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateInfoEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateLinkedComponentsCommandEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateLinkedComponentsResultEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateNodeLabelCommandEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateProjectMetadataCommandEntMixIn;
import org.knime.gateway.json.webui.entity.UpdateWorkflowAnnotationCommandEntMixIn;
import org.knime.gateway.json.webui.entity.VendorEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowAnnotationCommandEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowAnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowChangedEventEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowChangedEventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowCommandEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowGroupContentEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowInfoEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowMonitorMessageEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowMonitorStateChangeEventEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowMonitorStateChangeEventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowMonitorStateEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowMonitorStateSnapshotEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowSnapshotEntMixIn;
import org.knime.gateway.json.webui.entity.XYEntMixIn;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>com.knime.gateway.jsonrpc.entity</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
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
        res.add(AddAnnotationResultEntMixIn.class);
        res.add(AddBendpointCommandEntMixIn.class);
        res.add(AddComponentCommandEntMixIn.class);
        res.add(AddComponentPlaceholderResultEntMixIn.class);
        res.add(AddNodeCommandEntMixIn.class);
        res.add(AddNodeResultEntMixIn.class);
        res.add(AddPortCommandEntMixIn.class);
        res.add(AddPortResultEntMixIn.class);
        res.add(AddWorkflowAnnotationCommandEntMixIn.class);
        res.add(AlignNodesCommandEntMixIn.class);
        res.add(AllowedActionsEntMixIn.class);
        res.add(AllowedConnectionActionsEntMixIn.class);
        res.add(AllowedLoopActionsEntMixIn.class);
        res.add(AllowedNodeActionsEntMixIn.class);
        res.add(AllowedWorkflowActionsEntMixIn.class);
        res.add(AnnotationEntMixIn.class);
        res.add(AppStateChangedEventEntMixIn.class);
        res.add(AppStateChangedEventTypeEntMixIn.class);
        res.add(AppStateEntMixIn.class);
        res.add(AutoConnectCommandEntMixIn.class);
        res.add(AutoDisconnectCommandEntMixIn.class);
        res.add(BoundsEntMixIn.class);
        res.add(CategoryMetadataEntMixIn.class);
        res.add(CollapseCommandEntMixIn.class);
        res.add(CollapseResultEntMixIn.class);
        res.add(CommandResultEntMixIn.class);
        res.add(ComponentNodeAndDescriptionEntMixIn.class);
        res.add(ComponentNodeDescriptionEntMixIn.class);
        res.add(ComponentNodeEntMixIn.class);
        res.add(ComponentPlaceholderEntMixIn.class);
        res.add(ComponentPortDescriptionEntMixIn.class);
        res.add(CompositeEventEntMixIn.class);
        res.add(ConnectCommandEntMixIn.class);
        res.add(ConnectablesBasedCommandEntMixIn.class);
        res.add(ConnectionEntMixIn.class);
        res.add(ConvertContainerResultEntMixIn.class);
        res.add(CopyCommandEntMixIn.class);
        res.add(CopyResultEntMixIn.class);
        res.add(CustomJobManagerEntMixIn.class);
        res.add(CutCommandEntMixIn.class);
        res.add(DeleteCommandEntMixIn.class);
        res.add(DeleteComponentPlaceholderCommandEntMixIn.class);
        res.add(DynamicPortGroupDescriptionEntMixIn.class);
        res.add(EditableMetadataEntMixIn.class);
        res.add(EventEntMixIn.class);
        res.add(EventTypeEntMixIn.class);
        res.add(ExpandCommandEntMixIn.class);
        res.add(ExpandResultEntMixIn.class);
        res.add(ExtensionEntMixIn.class);
        res.add(GatewayProblemDescriptionEntMixIn.class);
        res.add(InsertNodeCommandEntMixIn.class);
        res.add(JobManagerEntMixIn.class);
        res.add(KaiFeedbackEntMixIn.class);
        res.add(KaiMessageEntMixIn.class);
        res.add(KaiRequestEntMixIn.class);
        res.add(KaiUiStringsEntMixIn.class);
        res.add(KaiWelcomeMessagesEntMixIn.class);
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
        res.add(NodeCategoryEntMixIn.class);
        res.add(NodeDescriptionEntMixIn.class);
        res.add(NodeDialogOptionDescriptionEntMixIn.class);
        res.add(NodeDialogOptionGroupEntMixIn.class);
        res.add(NodeEntMixIn.class);
        res.add(NodeExecutionInfoEntMixIn.class);
        res.add(NodeFactoryKeyEntMixIn.class);
        res.add(NodeGroupEntMixIn.class);
        res.add(NodeGroupsEntMixIn.class);
        res.add(NodeIdAndIsExecutedEntMixIn.class);
        res.add(NodePortDescriptionEntMixIn.class);
        res.add(NodePortEntMixIn.class);
        res.add(NodePortTemplateEntMixIn.class);
        res.add(NodeRepositoryLoadingProgressEventEntMixIn.class);
        res.add(NodeRepositoryLoadingProgressEventTypeEntMixIn.class);
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
        res.add(PortViewDescriptorEntMixIn.class);
        res.add(PortViewDescriptorMappingEntMixIn.class);
        res.add(PortViewsEntMixIn.class);
        res.add(ProjectDirtyStateEventEntMixIn.class);
        res.add(ProjectDisposedEventEntMixIn.class);
        res.add(ProjectDisposedEventTypeEntMixIn.class);
        res.add(ProjectEntMixIn.class);
        res.add(ProjectMetadataEntMixIn.class);
        res.add(RemovePortCommandEntMixIn.class);
        res.add(ReorderWorkflowAnnotationsCommandEntMixIn.class);
        res.add(ReplaceNodeCommandEntMixIn.class);
        res.add(SelectionEventEntMixIn.class);
        res.add(ShowToastEventEntMixIn.class);
        res.add(SpaceEntMixIn.class);
        res.add(SpaceGroupEntMixIn.class);
        res.add(SpaceItemChangedEventEntMixIn.class);
        res.add(SpaceItemChangedEventTypeEntMixIn.class);
        res.add(SpaceItemEntMixIn.class);
        res.add(SpaceItemReferenceEntMixIn.class);
        res.add(SpaceItemVersionEntMixIn.class);
        res.add(SpacePathSegmentEntMixIn.class);
        res.add(SpaceProviderEntMixIn.class);
        res.add(StyleRangeEntMixIn.class);
        res.add(TemplateLinkEntMixIn.class);
        res.add(TransformMetanodePortsBarCommandEntMixIn.class);
        res.add(TransformWorkflowAnnotationCommandEntMixIn.class);
        res.add(TranslateCommandEntMixIn.class);
        res.add(TypedTextEntMixIn.class);
        res.add(UpdateAvailableEventEntMixIn.class);
        res.add(UpdateAvailableEventTypeEntMixIn.class);
        res.add(UpdateComponentLinkInformationCommandEntMixIn.class);
        res.add(UpdateComponentMetadataCommandEntMixIn.class);
        res.add(UpdateComponentOrMetanodeNameCommandEntMixIn.class);
        res.add(UpdateInfoEntMixIn.class);
        res.add(UpdateLinkedComponentsCommandEntMixIn.class);
        res.add(UpdateLinkedComponentsResultEntMixIn.class);
        res.add(UpdateNodeLabelCommandEntMixIn.class);
        res.add(UpdateProjectMetadataCommandEntMixIn.class);
        res.add(UpdateWorkflowAnnotationCommandEntMixIn.class);
        res.add(VendorEntMixIn.class);
        res.add(WorkflowAnnotationCommandEntMixIn.class);
        res.add(WorkflowAnnotationEntMixIn.class);
        res.add(WorkflowChangedEventEntMixIn.class);
        res.add(WorkflowChangedEventTypeEntMixIn.class);
        res.add(WorkflowCommandEntMixIn.class);
        res.add(WorkflowEntMixIn.class);
        res.add(WorkflowGroupContentEntMixIn.class);
        res.add(WorkflowInfoEntMixIn.class);
        res.add(WorkflowMonitorMessageEntMixIn.class);
        res.add(WorkflowMonitorStateChangeEventEntMixIn.class);
        res.add(WorkflowMonitorStateChangeEventTypeEntMixIn.class);
        res.add(WorkflowMonitorStateEntMixIn.class);
        res.add(WorkflowMonitorStateSnapshotEntMixIn.class);
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
        res.add(AddAnnotationResultEntMixIn.AddAnnotationResultEntMixInBuilder.class);
        res.add(AddBendpointCommandEntMixIn.AddBendpointCommandEntMixInBuilder.class);
        res.add(AddComponentCommandEntMixIn.AddComponentCommandEntMixInBuilder.class);
        res.add(AddComponentPlaceholderResultEntMixIn.AddComponentPlaceholderResultEntMixInBuilder.class);
        res.add(AddNodeCommandEntMixIn.AddNodeCommandEntMixInBuilder.class);
        res.add(AddNodeResultEntMixIn.AddNodeResultEntMixInBuilder.class);
        res.add(AddPortCommandEntMixIn.AddPortCommandEntMixInBuilder.class);
        res.add(AddPortResultEntMixIn.AddPortResultEntMixInBuilder.class);
        res.add(AddWorkflowAnnotationCommandEntMixIn.AddWorkflowAnnotationCommandEntMixInBuilder.class);
        res.add(AlignNodesCommandEntMixIn.AlignNodesCommandEntMixInBuilder.class);
        res.add(AllowedActionsEntMixIn.AllowedActionsEntMixInBuilder.class);
        res.add(AllowedConnectionActionsEntMixIn.AllowedConnectionActionsEntMixInBuilder.class);
        res.add(AllowedLoopActionsEntMixIn.AllowedLoopActionsEntMixInBuilder.class);
        res.add(AllowedNodeActionsEntMixIn.AllowedNodeActionsEntMixInBuilder.class);
        res.add(AllowedWorkflowActionsEntMixIn.AllowedWorkflowActionsEntMixInBuilder.class);
        res.add(AnnotationEntMixIn.AnnotationEntMixInBuilder.class);
        res.add(AppStateChangedEventEntMixIn.AppStateChangedEventEntMixInBuilder.class);
        res.add(AppStateChangedEventTypeEntMixIn.AppStateChangedEventTypeEntMixInBuilder.class);
        res.add(AppStateEntMixIn.AppStateEntMixInBuilder.class);
        res.add(AutoConnectCommandEntMixIn.AutoConnectCommandEntMixInBuilder.class);
        res.add(AutoDisconnectCommandEntMixIn.AutoDisconnectCommandEntMixInBuilder.class);
        res.add(BoundsEntMixIn.BoundsEntMixInBuilder.class);
        res.add(CategoryMetadataEntMixIn.CategoryMetadataEntMixInBuilder.class);
        res.add(CollapseCommandEntMixIn.CollapseCommandEntMixInBuilder.class);
        res.add(CollapseResultEntMixIn.CollapseResultEntMixInBuilder.class);
        res.add(CommandResultEntMixIn.CommandResultEntMixInBuilder.class);
        res.add(ComponentNodeAndDescriptionEntMixIn.ComponentNodeAndDescriptionEntMixInBuilder.class);
        res.add(ComponentNodeDescriptionEntMixIn.ComponentNodeDescriptionEntMixInBuilder.class);
        res.add(ComponentNodeEntMixIn.ComponentNodeEntMixInBuilder.class);
        res.add(ComponentPlaceholderEntMixIn.ComponentPlaceholderEntMixInBuilder.class);
        res.add(ComponentPortDescriptionEntMixIn.ComponentPortDescriptionEntMixInBuilder.class);
        res.add(CompositeEventEntMixIn.CompositeEventEntMixInBuilder.class);
        res.add(ConnectCommandEntMixIn.ConnectCommandEntMixInBuilder.class);
        res.add(ConnectablesBasedCommandEntMixIn.ConnectablesBasedCommandEntMixInBuilder.class);
        res.add(ConnectionEntMixIn.ConnectionEntMixInBuilder.class);
        res.add(ConvertContainerResultEntMixIn.ConvertContainerResultEntMixInBuilder.class);
        res.add(CopyCommandEntMixIn.CopyCommandEntMixInBuilder.class);
        res.add(CopyResultEntMixIn.CopyResultEntMixInBuilder.class);
        res.add(CustomJobManagerEntMixIn.CustomJobManagerEntMixInBuilder.class);
        res.add(CutCommandEntMixIn.CutCommandEntMixInBuilder.class);
        res.add(DeleteCommandEntMixIn.DeleteCommandEntMixInBuilder.class);
        res.add(DeleteComponentPlaceholderCommandEntMixIn.DeleteComponentPlaceholderCommandEntMixInBuilder.class);
        res.add(DynamicPortGroupDescriptionEntMixIn.DynamicPortGroupDescriptionEntMixInBuilder.class);
        res.add(EditableMetadataEntMixIn.EditableMetadataEntMixInBuilder.class);
        res.add(EventEntMixIn.EventEntMixInBuilder.class);
        res.add(EventTypeEntMixIn.EventTypeEntMixInBuilder.class);
        res.add(ExpandCommandEntMixIn.ExpandCommandEntMixInBuilder.class);
        res.add(ExpandResultEntMixIn.ExpandResultEntMixInBuilder.class);
        res.add(ExtensionEntMixIn.ExtensionEntMixInBuilder.class);
        res.add(GatewayProblemDescriptionEntMixIn.GatewayProblemDescriptionEntMixInBuilder.class);
        res.add(InsertNodeCommandEntMixIn.InsertNodeCommandEntMixInBuilder.class);
        res.add(JobManagerEntMixIn.JobManagerEntMixInBuilder.class);
        res.add(KaiFeedbackEntMixIn.KaiFeedbackEntMixInBuilder.class);
        res.add(KaiMessageEntMixIn.KaiMessageEntMixInBuilder.class);
        res.add(KaiRequestEntMixIn.KaiRequestEntMixInBuilder.class);
        res.add(KaiUiStringsEntMixIn.KaiUiStringsEntMixInBuilder.class);
        res.add(KaiWelcomeMessagesEntMixIn.KaiWelcomeMessagesEntMixInBuilder.class);
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
        res.add(NodeCategoryEntMixIn.NodeCategoryEntMixInBuilder.class);
        res.add(NodeDescriptionEntMixIn.NodeDescriptionEntMixInBuilder.class);
        res.add(NodeDialogOptionDescriptionEntMixIn.NodeDialogOptionDescriptionEntMixInBuilder.class);
        res.add(NodeDialogOptionGroupEntMixIn.NodeDialogOptionGroupEntMixInBuilder.class);
        res.add(NodeEntMixIn.NodeEntMixInBuilder.class);
        res.add(NodeExecutionInfoEntMixIn.NodeExecutionInfoEntMixInBuilder.class);
        res.add(NodeFactoryKeyEntMixIn.NodeFactoryKeyEntMixInBuilder.class);
        res.add(NodeGroupEntMixIn.NodeGroupEntMixInBuilder.class);
        res.add(NodeGroupsEntMixIn.NodeGroupsEntMixInBuilder.class);
        res.add(NodeIdAndIsExecutedEntMixIn.NodeIdAndIsExecutedEntMixInBuilder.class);
        res.add(NodePortDescriptionEntMixIn.NodePortDescriptionEntMixInBuilder.class);
        res.add(NodePortEntMixIn.NodePortEntMixInBuilder.class);
        res.add(NodePortTemplateEntMixIn.NodePortTemplateEntMixInBuilder.class);
        res.add(NodeRepositoryLoadingProgressEventEntMixIn.NodeRepositoryLoadingProgressEventEntMixInBuilder.class);
        res.add(NodeRepositoryLoadingProgressEventTypeEntMixIn.NodeRepositoryLoadingProgressEventTypeEntMixInBuilder.class);
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
        res.add(PortViewDescriptorEntMixIn.PortViewDescriptorEntMixInBuilder.class);
        res.add(PortViewDescriptorMappingEntMixIn.PortViewDescriptorMappingEntMixInBuilder.class);
        res.add(PortViewsEntMixIn.PortViewsEntMixInBuilder.class);
        res.add(ProjectDirtyStateEventEntMixIn.ProjectDirtyStateEventEntMixInBuilder.class);
        res.add(ProjectDisposedEventEntMixIn.ProjectDisposedEventEntMixInBuilder.class);
        res.add(ProjectDisposedEventTypeEntMixIn.ProjectDisposedEventTypeEntMixInBuilder.class);
        res.add(ProjectEntMixIn.ProjectEntMixInBuilder.class);
        res.add(ProjectMetadataEntMixIn.ProjectMetadataEntMixInBuilder.class);
        res.add(RemovePortCommandEntMixIn.RemovePortCommandEntMixInBuilder.class);
        res.add(ReorderWorkflowAnnotationsCommandEntMixIn.ReorderWorkflowAnnotationsCommandEntMixInBuilder.class);
        res.add(ReplaceNodeCommandEntMixIn.ReplaceNodeCommandEntMixInBuilder.class);
        res.add(SelectionEventEntMixIn.SelectionEventEntMixInBuilder.class);
        res.add(ShowToastEventEntMixIn.ShowToastEventEntMixInBuilder.class);
        res.add(SpaceEntMixIn.SpaceEntMixInBuilder.class);
        res.add(SpaceGroupEntMixIn.SpaceGroupEntMixInBuilder.class);
        res.add(SpaceItemChangedEventEntMixIn.SpaceItemChangedEventEntMixInBuilder.class);
        res.add(SpaceItemChangedEventTypeEntMixIn.SpaceItemChangedEventTypeEntMixInBuilder.class);
        res.add(SpaceItemEntMixIn.SpaceItemEntMixInBuilder.class);
        res.add(SpaceItemReferenceEntMixIn.SpaceItemReferenceEntMixInBuilder.class);
        res.add(SpaceItemVersionEntMixIn.SpaceItemVersionEntMixInBuilder.class);
        res.add(SpacePathSegmentEntMixIn.SpacePathSegmentEntMixInBuilder.class);
        res.add(SpaceProviderEntMixIn.SpaceProviderEntMixInBuilder.class);
        res.add(StyleRangeEntMixIn.StyleRangeEntMixInBuilder.class);
        res.add(TemplateLinkEntMixIn.TemplateLinkEntMixInBuilder.class);
        res.add(TransformMetanodePortsBarCommandEntMixIn.TransformMetanodePortsBarCommandEntMixInBuilder.class);
        res.add(TransformWorkflowAnnotationCommandEntMixIn.TransformWorkflowAnnotationCommandEntMixInBuilder.class);
        res.add(TranslateCommandEntMixIn.TranslateCommandEntMixInBuilder.class);
        res.add(TypedTextEntMixIn.TypedTextEntMixInBuilder.class);
        res.add(UpdateAvailableEventEntMixIn.UpdateAvailableEventEntMixInBuilder.class);
        res.add(UpdateAvailableEventTypeEntMixIn.UpdateAvailableEventTypeEntMixInBuilder.class);
        res.add(UpdateComponentLinkInformationCommandEntMixIn.UpdateComponentLinkInformationCommandEntMixInBuilder.class);
        res.add(UpdateComponentMetadataCommandEntMixIn.UpdateComponentMetadataCommandEntMixInBuilder.class);
        res.add(UpdateComponentOrMetanodeNameCommandEntMixIn.UpdateComponentOrMetanodeNameCommandEntMixInBuilder.class);
        res.add(UpdateInfoEntMixIn.UpdateInfoEntMixInBuilder.class);
        res.add(UpdateLinkedComponentsCommandEntMixIn.UpdateLinkedComponentsCommandEntMixInBuilder.class);
        res.add(UpdateLinkedComponentsResultEntMixIn.UpdateLinkedComponentsResultEntMixInBuilder.class);
        res.add(UpdateNodeLabelCommandEntMixIn.UpdateNodeLabelCommandEntMixInBuilder.class);
        res.add(UpdateProjectMetadataCommandEntMixIn.UpdateProjectMetadataCommandEntMixInBuilder.class);
        res.add(UpdateWorkflowAnnotationCommandEntMixIn.UpdateWorkflowAnnotationCommandEntMixInBuilder.class);
        res.add(VendorEntMixIn.VendorEntMixInBuilder.class);
        res.add(WorkflowAnnotationCommandEntMixIn.WorkflowAnnotationCommandEntMixInBuilder.class);
        res.add(WorkflowAnnotationEntMixIn.WorkflowAnnotationEntMixInBuilder.class);
        res.add(WorkflowChangedEventEntMixIn.WorkflowChangedEventEntMixInBuilder.class);
        res.add(WorkflowChangedEventTypeEntMixIn.WorkflowChangedEventTypeEntMixInBuilder.class);
        res.add(WorkflowCommandEntMixIn.WorkflowCommandEntMixInBuilder.class);
        res.add(WorkflowEntMixIn.WorkflowEntMixInBuilder.class);
        res.add(WorkflowGroupContentEntMixIn.WorkflowGroupContentEntMixInBuilder.class);
        res.add(WorkflowInfoEntMixIn.WorkflowInfoEntMixInBuilder.class);
        res.add(WorkflowMonitorMessageEntMixIn.WorkflowMonitorMessageEntMixInBuilder.class);
        res.add(WorkflowMonitorStateChangeEventEntMixIn.WorkflowMonitorStateChangeEventEntMixInBuilder.class);
        res.add(WorkflowMonitorStateChangeEventTypeEntMixIn.WorkflowMonitorStateChangeEventTypeEntMixInBuilder.class);
        res.add(WorkflowMonitorStateEntMixIn.WorkflowMonitorStateEntMixInBuilder.class);
        res.add(WorkflowMonitorStateSnapshotEntMixIn.WorkflowMonitorStateSnapshotEntMixInBuilder.class);
        res.add(WorkflowSnapshotEntMixIn.WorkflowSnapshotEntMixInBuilder.class);
        res.add(XYEntMixIn.XYEntMixInBuilder.class);
        return res;
    }
}
