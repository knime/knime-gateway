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
package org.knime.gateway.api.webui.entity.util;

import org.knime.gateway.api.webui.entity.AddAnnotationResultEnt;
import org.knime.gateway.api.webui.entity.AddBendpointCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentPlaceholderResultEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt;
import org.knime.gateway.api.webui.entity.AddPortResultEnt;
import org.knime.gateway.api.webui.entity.AddWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.AlignNodesCommandEnt;
import org.knime.gateway.api.webui.entity.AllowedActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedConnectionActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedLoopActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedWorkflowActionsEnt;
import org.knime.gateway.api.webui.entity.AnnotationEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.api.webui.entity.AutoConnectCommandEnt;
import org.knime.gateway.api.webui.entity.AutoDisconnectCommandEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.CategoryMetadataEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt;
import org.knime.gateway.api.webui.entity.CollapseResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ComponentEditorConfigEnt;
import org.knime.gateway.api.webui.entity.ComponentEditorStateEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeAndDescriptionEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt;
import org.knime.gateway.api.webui.entity.ComponentPortDescriptionEnt;
import org.knime.gateway.api.webui.entity.CompositeEventEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt;
import org.knime.gateway.api.webui.entity.ConnectablesBasedCommandEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.ConvertContainerResultEnt;
import org.knime.gateway.api.webui.entity.CopyCommandEnt;
import org.knime.gateway.api.webui.entity.CopyResultEnt;
import org.knime.gateway.api.webui.entity.CustomJobManagerEnt;
import org.knime.gateway.api.webui.entity.CutCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteComponentPlaceholderCommandEnt;
import org.knime.gateway.api.webui.entity.DynamicPortGroupDescriptionEnt;
import org.knime.gateway.api.webui.entity.EditableMetadataEnt;
import org.knime.gateway.api.webui.entity.EventEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt;
import org.knime.gateway.api.webui.entity.ExpandCommandEnt;
import org.knime.gateway.api.webui.entity.ExpandResultEnt;
import org.knime.gateway.api.webui.entity.ExtensionEnt;
import org.knime.gateway.api.webui.entity.GatewayProblemDescriptionEnt;
import org.knime.gateway.api.webui.entity.InsertNodeCommandEnt;
import org.knime.gateway.api.webui.entity.JobManagerEnt;
import org.knime.gateway.api.webui.entity.KaiFeedbackEnt;
import org.knime.gateway.api.webui.entity.KaiMessageEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionContextEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionErrorEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionGenerateAnnotationContextEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionGenerateAnnotationRequestEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionGenerateAnnotationResponseEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionGenerateAnnotationResultEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionRequestEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionResponseEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionResultEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionsAvailableEnt;
import org.knime.gateway.api.webui.entity.KaiRequestEnt;
import org.knime.gateway.api.webui.entity.KaiUiStringsEnt;
import org.knime.gateway.api.webui.entity.KaiUsageEnt;
import org.knime.gateway.api.webui.entity.KaiWelcomeMessagesEnt;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.LoopInfoEnt;
import org.knime.gateway.api.webui.entity.MetaNodeEnt;
import org.knime.gateway.api.webui.entity.MetaNodePortEnt;
import org.knime.gateway.api.webui.entity.MetaNodeStateEnt;
import org.knime.gateway.api.webui.entity.MetaPortsEnt;
import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeCategoryEnt;
import org.knime.gateway.api.webui.entity.NodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodeGroupEnt;
import org.knime.gateway.api.webui.entity.NodeGroupsEnt;
import org.knime.gateway.api.webui.entity.NodeIdAndIsExecutedEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeRepositoryLoadingProgressEventEnt;
import org.knime.gateway.api.webui.entity.NodeRepositoryLoadingProgressEventTypeEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;
import org.knime.gateway.api.webui.entity.PartBasedCommandEnt;
import org.knime.gateway.api.webui.entity.PasteCommandEnt;
import org.knime.gateway.api.webui.entity.PasteResultEnt;
import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt;
import org.knime.gateway.api.webui.entity.PortCommandEnt;
import org.knime.gateway.api.webui.entity.PortGroupEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.PortViewDescriptorEnt;
import org.knime.gateway.api.webui.entity.PortViewDescriptorMappingEnt;
import org.knime.gateway.api.webui.entity.PortViewsEnt;
import org.knime.gateway.api.webui.entity.ProjectDirtyStateEventEnt;
import org.knime.gateway.api.webui.entity.ProjectDisposedEventEnt;
import org.knime.gateway.api.webui.entity.ProjectDisposedEventTypeEnt;
import org.knime.gateway.api.webui.entity.ProjectEnt;
import org.knime.gateway.api.webui.entity.ProjectMetadataEnt;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;
import org.knime.gateway.api.webui.entity.ReorderWorkflowAnnotationsCommandEnt;
import org.knime.gateway.api.webui.entity.ReplaceNodeCommandEnt;
import org.knime.gateway.api.webui.entity.SelectionEventEnt;
import org.knime.gateway.api.webui.entity.ShowToastEventEnt;
import org.knime.gateway.api.webui.entity.SpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt;
import org.knime.gateway.api.webui.entity.SpaceItemChangedEventEnt;
import org.knime.gateway.api.webui.entity.SpaceItemChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemVersionEnt;
import org.knime.gateway.api.webui.entity.SpacePathSegmentEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt;
import org.knime.gateway.api.webui.entity.TemplateLinkEnt;
import org.knime.gateway.api.webui.entity.TransformMetanodePortsBarCommandEnt;
import org.knime.gateway.api.webui.entity.TransformWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.TypedTextEnt;
import org.knime.gateway.api.webui.entity.UpdateAvailableEventEnt;
import org.knime.gateway.api.webui.entity.UpdateAvailableEventTypeEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentLinkInformationCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentMetadataCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentOrMetanodeNameCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateInfoEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt;
import org.knime.gateway.api.webui.entity.UpdateNodeLabelCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateProjectMetadataCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.VendorEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorMessageEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.XYEnt;


import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>org.knime.gateway.api.entity</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public class ListEntities {

    private ListEntities() {
        //utility class
    }

    /**
     * Lists all gateway entity classes of package <code>com.knime.gateway.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AddAnnotationResultEnt.class);
        res.add(AddBendpointCommandEnt.class);
        res.add(AddComponentCommandEnt.class);
        res.add(AddComponentPlaceholderResultEnt.class);
        res.add(AddNodeCommandEnt.class);
        res.add(AddNodeResultEnt.class);
        res.add(AddPortCommandEnt.class);
        res.add(AddPortResultEnt.class);
        res.add(AddWorkflowAnnotationCommandEnt.class);
        res.add(AlignNodesCommandEnt.class);
        res.add(AllowedActionsEnt.class);
        res.add(AllowedConnectionActionsEnt.class);
        res.add(AllowedLoopActionsEnt.class);
        res.add(AllowedNodeActionsEnt.class);
        res.add(AllowedWorkflowActionsEnt.class);
        res.add(AnnotationEnt.class);
        res.add(AppStateChangedEventEnt.class);
        res.add(AppStateChangedEventTypeEnt.class);
        res.add(AppStateEnt.class);
        res.add(AutoConnectCommandEnt.class);
        res.add(AutoDisconnectCommandEnt.class);
        res.add(BoundsEnt.class);
        res.add(CategoryMetadataEnt.class);
        res.add(CollapseCommandEnt.class);
        res.add(CollapseResultEnt.class);
        res.add(CommandResultEnt.class);
        res.add(ComponentEditorConfigEnt.class);
        res.add(ComponentEditorStateEnt.class);
        res.add(ComponentNodeAndDescriptionEnt.class);
        res.add(ComponentNodeDescriptionEnt.class);
        res.add(ComponentNodeEnt.class);
        res.add(ComponentPlaceholderEnt.class);
        res.add(ComponentPortDescriptionEnt.class);
        res.add(CompositeEventEnt.class);
        res.add(ConnectCommandEnt.class);
        res.add(ConnectablesBasedCommandEnt.class);
        res.add(ConnectionEnt.class);
        res.add(ConvertContainerResultEnt.class);
        res.add(CopyCommandEnt.class);
        res.add(CopyResultEnt.class);
        res.add(CustomJobManagerEnt.class);
        res.add(CutCommandEnt.class);
        res.add(DeleteCommandEnt.class);
        res.add(DeleteComponentPlaceholderCommandEnt.class);
        res.add(DynamicPortGroupDescriptionEnt.class);
        res.add(EditableMetadataEnt.class);
        res.add(EventEnt.class);
        res.add(EventTypeEnt.class);
        res.add(ExpandCommandEnt.class);
        res.add(ExpandResultEnt.class);
        res.add(ExtensionEnt.class);
        res.add(GatewayProblemDescriptionEnt.class);
        res.add(InsertNodeCommandEnt.class);
        res.add(JobManagerEnt.class);
        res.add(KaiFeedbackEnt.class);
        res.add(KaiMessageEnt.class);
        res.add(KaiQuickActionContextEnt.class);
        res.add(KaiQuickActionErrorEnt.class);
        res.add(KaiQuickActionGenerateAnnotationContextEnt.class);
        res.add(KaiQuickActionGenerateAnnotationRequestEnt.class);
        res.add(KaiQuickActionGenerateAnnotationResponseEnt.class);
        res.add(KaiQuickActionGenerateAnnotationResultEnt.class);
        res.add(KaiQuickActionRequestEnt.class);
        res.add(KaiQuickActionResponseEnt.class);
        res.add(KaiQuickActionResultEnt.class);
        res.add(KaiQuickActionsAvailableEnt.class);
        res.add(KaiRequestEnt.class);
        res.add(KaiUiStringsEnt.class);
        res.add(KaiUsageEnt.class);
        res.add(KaiWelcomeMessagesEnt.class);
        res.add(LinkEnt.class);
        res.add(LoopInfoEnt.class);
        res.add(MetaNodeEnt.class);
        res.add(MetaNodePortEnt.class);
        res.add(MetaNodeStateEnt.class);
        res.add(MetaPortsEnt.class);
        res.add(NativeNodeDescriptionEnt.class);
        res.add(NativeNodeEnt.class);
        res.add(NativeNodeInvariantsEnt.class);
        res.add(NodeAnnotationEnt.class);
        res.add(NodeCategoryEnt.class);
        res.add(NodeDescriptionEnt.class);
        res.add(NodeDialogOptionDescriptionEnt.class);
        res.add(NodeDialogOptionGroupEnt.class);
        res.add(NodeEnt.class);
        res.add(NodeExecutionInfoEnt.class);
        res.add(NodeFactoryKeyEnt.class);
        res.add(NodeGroupEnt.class);
        res.add(NodeGroupsEnt.class);
        res.add(NodeIdAndIsExecutedEnt.class);
        res.add(NodePortDescriptionEnt.class);
        res.add(NodePortEnt.class);
        res.add(NodePortTemplateEnt.class);
        res.add(NodeRepositoryLoadingProgressEventEnt.class);
        res.add(NodeRepositoryLoadingProgressEventTypeEnt.class);
        res.add(NodeSearchResultEnt.class);
        res.add(NodeStateEnt.class);
        res.add(NodeTemplateEnt.class);
        res.add(NodeViewDescriptionEnt.class);
        res.add(PartBasedCommandEnt.class);
        res.add(PasteCommandEnt.class);
        res.add(PasteResultEnt.class);
        res.add(PatchEnt.class);
        res.add(PatchOpEnt.class);
        res.add(PortCommandEnt.class);
        res.add(PortGroupEnt.class);
        res.add(PortTypeEnt.class);
        res.add(PortViewDescriptorEnt.class);
        res.add(PortViewDescriptorMappingEnt.class);
        res.add(PortViewsEnt.class);
        res.add(ProjectDirtyStateEventEnt.class);
        res.add(ProjectDisposedEventEnt.class);
        res.add(ProjectDisposedEventTypeEnt.class);
        res.add(ProjectEnt.class);
        res.add(ProjectMetadataEnt.class);
        res.add(RemovePortCommandEnt.class);
        res.add(ReorderWorkflowAnnotationsCommandEnt.class);
        res.add(ReplaceNodeCommandEnt.class);
        res.add(SelectionEventEnt.class);
        res.add(ShowToastEventEnt.class);
        res.add(SpaceEnt.class);
        res.add(SpaceGroupEnt.class);
        res.add(SpaceItemChangedEventEnt.class);
        res.add(SpaceItemChangedEventTypeEnt.class);
        res.add(SpaceItemEnt.class);
        res.add(SpaceItemReferenceEnt.class);
        res.add(SpaceItemVersionEnt.class);
        res.add(SpacePathSegmentEnt.class);
        res.add(SpaceProviderEnt.class);
        res.add(StyleRangeEnt.class);
        res.add(TemplateLinkEnt.class);
        res.add(TransformMetanodePortsBarCommandEnt.class);
        res.add(TransformWorkflowAnnotationCommandEnt.class);
        res.add(TranslateCommandEnt.class);
        res.add(TypedTextEnt.class);
        res.add(UpdateAvailableEventEnt.class);
        res.add(UpdateAvailableEventTypeEnt.class);
        res.add(UpdateComponentLinkInformationCommandEnt.class);
        res.add(UpdateComponentMetadataCommandEnt.class);
        res.add(UpdateComponentOrMetanodeNameCommandEnt.class);
        res.add(UpdateInfoEnt.class);
        res.add(UpdateLinkedComponentsCommandEnt.class);
        res.add(UpdateLinkedComponentsResultEnt.class);
        res.add(UpdateNodeLabelCommandEnt.class);
        res.add(UpdateProjectMetadataCommandEnt.class);
        res.add(UpdateWorkflowAnnotationCommandEnt.class);
        res.add(VendorEnt.class);
        res.add(WorkflowAnnotationCommandEnt.class);
        res.add(WorkflowAnnotationEnt.class);
        res.add(WorkflowChangedEventEnt.class);
        res.add(WorkflowChangedEventTypeEnt.class);
        res.add(WorkflowCommandEnt.class);
        res.add(WorkflowEnt.class);
        res.add(WorkflowGroupContentEnt.class);
        res.add(WorkflowInfoEnt.class);
        res.add(WorkflowMonitorMessageEnt.class);
        res.add(WorkflowMonitorStateChangeEventEnt.class);
        res.add(WorkflowMonitorStateChangeEventTypeEnt.class);
        res.add(WorkflowMonitorStateEnt.class);
        res.add(WorkflowMonitorStateSnapshotEnt.class);
        res.add(WorkflowSnapshotEnt.class);
        res.add(XYEnt.class);
        return res;
    }
    
    /**
     * Lists all gateway entity builder classes of package <code>com.knime.gateway.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityBuilderClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AddAnnotationResultEnt.AddAnnotationResultEntBuilder.class);
        res.add(AddBendpointCommandEnt.AddBendpointCommandEntBuilder.class);
        res.add(AddComponentCommandEnt.AddComponentCommandEntBuilder.class);
        res.add(AddComponentPlaceholderResultEnt.AddComponentPlaceholderResultEntBuilder.class);
        res.add(AddNodeCommandEnt.AddNodeCommandEntBuilder.class);
        res.add(AddNodeResultEnt.AddNodeResultEntBuilder.class);
        res.add(AddPortCommandEnt.AddPortCommandEntBuilder.class);
        res.add(AddPortResultEnt.AddPortResultEntBuilder.class);
        res.add(AddWorkflowAnnotationCommandEnt.AddWorkflowAnnotationCommandEntBuilder.class);
        res.add(AlignNodesCommandEnt.AlignNodesCommandEntBuilder.class);
        res.add(AllowedActionsEnt.AllowedActionsEntBuilder.class);
        res.add(AllowedConnectionActionsEnt.AllowedConnectionActionsEntBuilder.class);
        res.add(AllowedLoopActionsEnt.AllowedLoopActionsEntBuilder.class);
        res.add(AllowedNodeActionsEnt.AllowedNodeActionsEntBuilder.class);
        res.add(AllowedWorkflowActionsEnt.AllowedWorkflowActionsEntBuilder.class);
        res.add(AnnotationEnt.AnnotationEntBuilder.class);
        res.add(AppStateChangedEventEnt.AppStateChangedEventEntBuilder.class);
        res.add(AppStateChangedEventTypeEnt.AppStateChangedEventTypeEntBuilder.class);
        res.add(AppStateEnt.AppStateEntBuilder.class);
        res.add(AutoConnectCommandEnt.AutoConnectCommandEntBuilder.class);
        res.add(AutoDisconnectCommandEnt.AutoDisconnectCommandEntBuilder.class);
        res.add(BoundsEnt.BoundsEntBuilder.class);
        res.add(CategoryMetadataEnt.CategoryMetadataEntBuilder.class);
        res.add(CollapseCommandEnt.CollapseCommandEntBuilder.class);
        res.add(CollapseResultEnt.CollapseResultEntBuilder.class);
        res.add(CommandResultEnt.CommandResultEntBuilder.class);
        res.add(ComponentEditorConfigEnt.ComponentEditorConfigEntBuilder.class);
        res.add(ComponentEditorStateEnt.ComponentEditorStateEntBuilder.class);
        res.add(ComponentNodeAndDescriptionEnt.ComponentNodeAndDescriptionEntBuilder.class);
        res.add(ComponentNodeDescriptionEnt.ComponentNodeDescriptionEntBuilder.class);
        res.add(ComponentNodeEnt.ComponentNodeEntBuilder.class);
        res.add(ComponentPlaceholderEnt.ComponentPlaceholderEntBuilder.class);
        res.add(ComponentPortDescriptionEnt.ComponentPortDescriptionEntBuilder.class);
        res.add(CompositeEventEnt.CompositeEventEntBuilder.class);
        res.add(ConnectCommandEnt.ConnectCommandEntBuilder.class);
        res.add(ConnectablesBasedCommandEnt.ConnectablesBasedCommandEntBuilder.class);
        res.add(ConnectionEnt.ConnectionEntBuilder.class);
        res.add(ConvertContainerResultEnt.ConvertContainerResultEntBuilder.class);
        res.add(CopyCommandEnt.CopyCommandEntBuilder.class);
        res.add(CopyResultEnt.CopyResultEntBuilder.class);
        res.add(CustomJobManagerEnt.CustomJobManagerEntBuilder.class);
        res.add(CutCommandEnt.CutCommandEntBuilder.class);
        res.add(DeleteCommandEnt.DeleteCommandEntBuilder.class);
        res.add(DeleteComponentPlaceholderCommandEnt.DeleteComponentPlaceholderCommandEntBuilder.class);
        res.add(DynamicPortGroupDescriptionEnt.DynamicPortGroupDescriptionEntBuilder.class);
        res.add(EditableMetadataEnt.EditableMetadataEntBuilder.class);
        res.add(EventEnt.EventEntBuilder.class);
        res.add(EventTypeEnt.EventTypeEntBuilder.class);
        res.add(ExpandCommandEnt.ExpandCommandEntBuilder.class);
        res.add(ExpandResultEnt.ExpandResultEntBuilder.class);
        res.add(ExtensionEnt.ExtensionEntBuilder.class);
        res.add(GatewayProblemDescriptionEnt.GatewayProblemDescriptionEntBuilder.class);
        res.add(InsertNodeCommandEnt.InsertNodeCommandEntBuilder.class);
        res.add(JobManagerEnt.JobManagerEntBuilder.class);
        res.add(KaiFeedbackEnt.KaiFeedbackEntBuilder.class);
        res.add(KaiMessageEnt.KaiMessageEntBuilder.class);
        res.add(KaiQuickActionContextEnt.KaiQuickActionContextEntBuilder.class);
        res.add(KaiQuickActionErrorEnt.KaiQuickActionErrorEntBuilder.class);
        res.add(KaiQuickActionGenerateAnnotationContextEnt.KaiQuickActionGenerateAnnotationContextEntBuilder.class);
        res.add(KaiQuickActionGenerateAnnotationRequestEnt.KaiQuickActionGenerateAnnotationRequestEntBuilder.class);
        res.add(KaiQuickActionGenerateAnnotationResponseEnt.KaiQuickActionGenerateAnnotationResponseEntBuilder.class);
        res.add(KaiQuickActionGenerateAnnotationResultEnt.KaiQuickActionGenerateAnnotationResultEntBuilder.class);
        res.add(KaiQuickActionRequestEnt.KaiQuickActionRequestEntBuilder.class);
        res.add(KaiQuickActionResponseEnt.KaiQuickActionResponseEntBuilder.class);
        res.add(KaiQuickActionResultEnt.KaiQuickActionResultEntBuilder.class);
        res.add(KaiQuickActionsAvailableEnt.KaiQuickActionsAvailableEntBuilder.class);
        res.add(KaiRequestEnt.KaiRequestEntBuilder.class);
        res.add(KaiUiStringsEnt.KaiUiStringsEntBuilder.class);
        res.add(KaiUsageEnt.KaiUsageEntBuilder.class);
        res.add(KaiWelcomeMessagesEnt.KaiWelcomeMessagesEntBuilder.class);
        res.add(LinkEnt.LinkEntBuilder.class);
        res.add(LoopInfoEnt.LoopInfoEntBuilder.class);
        res.add(MetaNodeEnt.MetaNodeEntBuilder.class);
        res.add(MetaNodePortEnt.MetaNodePortEntBuilder.class);
        res.add(MetaNodeStateEnt.MetaNodeStateEntBuilder.class);
        res.add(MetaPortsEnt.MetaPortsEntBuilder.class);
        res.add(NativeNodeDescriptionEnt.NativeNodeDescriptionEntBuilder.class);
        res.add(NativeNodeEnt.NativeNodeEntBuilder.class);
        res.add(NativeNodeInvariantsEnt.NativeNodeInvariantsEntBuilder.class);
        res.add(NodeAnnotationEnt.NodeAnnotationEntBuilder.class);
        res.add(NodeCategoryEnt.NodeCategoryEntBuilder.class);
        res.add(NodeDescriptionEnt.NodeDescriptionEntBuilder.class);
        res.add(NodeDialogOptionDescriptionEnt.NodeDialogOptionDescriptionEntBuilder.class);
        res.add(NodeDialogOptionGroupEnt.NodeDialogOptionGroupEntBuilder.class);
        res.add(NodeEnt.NodeEntBuilder.class);
        res.add(NodeExecutionInfoEnt.NodeExecutionInfoEntBuilder.class);
        res.add(NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder.class);
        res.add(NodeGroupEnt.NodeGroupEntBuilder.class);
        res.add(NodeGroupsEnt.NodeGroupsEntBuilder.class);
        res.add(NodeIdAndIsExecutedEnt.NodeIdAndIsExecutedEntBuilder.class);
        res.add(NodePortDescriptionEnt.NodePortDescriptionEntBuilder.class);
        res.add(NodePortEnt.NodePortEntBuilder.class);
        res.add(NodePortTemplateEnt.NodePortTemplateEntBuilder.class);
        res.add(NodeRepositoryLoadingProgressEventEnt.NodeRepositoryLoadingProgressEventEntBuilder.class);
        res.add(NodeRepositoryLoadingProgressEventTypeEnt.NodeRepositoryLoadingProgressEventTypeEntBuilder.class);
        res.add(NodeSearchResultEnt.NodeSearchResultEntBuilder.class);
        res.add(NodeStateEnt.NodeStateEntBuilder.class);
        res.add(NodeTemplateEnt.NodeTemplateEntBuilder.class);
        res.add(NodeViewDescriptionEnt.NodeViewDescriptionEntBuilder.class);
        res.add(PartBasedCommandEnt.PartBasedCommandEntBuilder.class);
        res.add(PasteCommandEnt.PasteCommandEntBuilder.class);
        res.add(PasteResultEnt.PasteResultEntBuilder.class);
        res.add(PatchEnt.PatchEntBuilder.class);
        res.add(PatchOpEnt.PatchOpEntBuilder.class);
        res.add(PortCommandEnt.PortCommandEntBuilder.class);
        res.add(PortGroupEnt.PortGroupEntBuilder.class);
        res.add(PortTypeEnt.PortTypeEntBuilder.class);
        res.add(PortViewDescriptorEnt.PortViewDescriptorEntBuilder.class);
        res.add(PortViewDescriptorMappingEnt.PortViewDescriptorMappingEntBuilder.class);
        res.add(PortViewsEnt.PortViewsEntBuilder.class);
        res.add(ProjectDirtyStateEventEnt.ProjectDirtyStateEventEntBuilder.class);
        res.add(ProjectDisposedEventEnt.ProjectDisposedEventEntBuilder.class);
        res.add(ProjectDisposedEventTypeEnt.ProjectDisposedEventTypeEntBuilder.class);
        res.add(ProjectEnt.ProjectEntBuilder.class);
        res.add(ProjectMetadataEnt.ProjectMetadataEntBuilder.class);
        res.add(RemovePortCommandEnt.RemovePortCommandEntBuilder.class);
        res.add(ReorderWorkflowAnnotationsCommandEnt.ReorderWorkflowAnnotationsCommandEntBuilder.class);
        res.add(ReplaceNodeCommandEnt.ReplaceNodeCommandEntBuilder.class);
        res.add(SelectionEventEnt.SelectionEventEntBuilder.class);
        res.add(ShowToastEventEnt.ShowToastEventEntBuilder.class);
        res.add(SpaceEnt.SpaceEntBuilder.class);
        res.add(SpaceGroupEnt.SpaceGroupEntBuilder.class);
        res.add(SpaceItemChangedEventEnt.SpaceItemChangedEventEntBuilder.class);
        res.add(SpaceItemChangedEventTypeEnt.SpaceItemChangedEventTypeEntBuilder.class);
        res.add(SpaceItemEnt.SpaceItemEntBuilder.class);
        res.add(SpaceItemReferenceEnt.SpaceItemReferenceEntBuilder.class);
        res.add(SpaceItemVersionEnt.SpaceItemVersionEntBuilder.class);
        res.add(SpacePathSegmentEnt.SpacePathSegmentEntBuilder.class);
        res.add(SpaceProviderEnt.SpaceProviderEntBuilder.class);
        res.add(StyleRangeEnt.StyleRangeEntBuilder.class);
        res.add(TemplateLinkEnt.TemplateLinkEntBuilder.class);
        res.add(TransformMetanodePortsBarCommandEnt.TransformMetanodePortsBarCommandEntBuilder.class);
        res.add(TransformWorkflowAnnotationCommandEnt.TransformWorkflowAnnotationCommandEntBuilder.class);
        res.add(TranslateCommandEnt.TranslateCommandEntBuilder.class);
        res.add(TypedTextEnt.TypedTextEntBuilder.class);
        res.add(UpdateAvailableEventEnt.UpdateAvailableEventEntBuilder.class);
        res.add(UpdateAvailableEventTypeEnt.UpdateAvailableEventTypeEntBuilder.class);
        res.add(UpdateComponentLinkInformationCommandEnt.UpdateComponentLinkInformationCommandEntBuilder.class);
        res.add(UpdateComponentMetadataCommandEnt.UpdateComponentMetadataCommandEntBuilder.class);
        res.add(UpdateComponentOrMetanodeNameCommandEnt.UpdateComponentOrMetanodeNameCommandEntBuilder.class);
        res.add(UpdateInfoEnt.UpdateInfoEntBuilder.class);
        res.add(UpdateLinkedComponentsCommandEnt.UpdateLinkedComponentsCommandEntBuilder.class);
        res.add(UpdateLinkedComponentsResultEnt.UpdateLinkedComponentsResultEntBuilder.class);
        res.add(UpdateNodeLabelCommandEnt.UpdateNodeLabelCommandEntBuilder.class);
        res.add(UpdateProjectMetadataCommandEnt.UpdateProjectMetadataCommandEntBuilder.class);
        res.add(UpdateWorkflowAnnotationCommandEnt.UpdateWorkflowAnnotationCommandEntBuilder.class);
        res.add(VendorEnt.VendorEntBuilder.class);
        res.add(WorkflowAnnotationCommandEnt.WorkflowAnnotationCommandEntBuilder.class);
        res.add(WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder.class);
        res.add(WorkflowChangedEventEnt.WorkflowChangedEventEntBuilder.class);
        res.add(WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder.class);
        res.add(WorkflowCommandEnt.WorkflowCommandEntBuilder.class);
        res.add(WorkflowEnt.WorkflowEntBuilder.class);
        res.add(WorkflowGroupContentEnt.WorkflowGroupContentEntBuilder.class);
        res.add(WorkflowInfoEnt.WorkflowInfoEntBuilder.class);
        res.add(WorkflowMonitorMessageEnt.WorkflowMonitorMessageEntBuilder.class);
        res.add(WorkflowMonitorStateChangeEventEnt.WorkflowMonitorStateChangeEventEntBuilder.class);
        res.add(WorkflowMonitorStateChangeEventTypeEnt.WorkflowMonitorStateChangeEventTypeEntBuilder.class);
        res.add(WorkflowMonitorStateEnt.WorkflowMonitorStateEntBuilder.class);
        res.add(WorkflowMonitorStateSnapshotEnt.WorkflowMonitorStateSnapshotEntBuilder.class);
        res.add(WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder.class);
        res.add(XYEnt.XYEntBuilder.class);
        return res;
    }
}
