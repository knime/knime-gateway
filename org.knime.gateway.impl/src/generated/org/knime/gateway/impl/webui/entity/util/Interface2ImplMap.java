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
package org.knime.gateway.impl.webui.entity.util;

import org.knime.gateway.api.webui.entity.AddAnnotationResultEnt.AddAnnotationResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAddAnnotationResultEnt;
import org.knime.gateway.api.webui.entity.AddBendpointCommandEnt.AddBendpointCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAddBendpointCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt.AddComponentCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAddComponentCommandEnt;
import org.knime.gateway.api.webui.entity.AddComponentPlaceholderResultEnt.AddComponentPlaceholderResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAddComponentPlaceholderResultEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.AddNodeCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt.AddNodeResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAddNodeResultEnt;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt.AddPortCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAddPortCommandEnt;
import org.knime.gateway.api.webui.entity.AddPortResultEnt.AddPortResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAddPortResultEnt;
import org.knime.gateway.api.webui.entity.AddWorkflowAnnotationCommandEnt.AddWorkflowAnnotationCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAddWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.AlignNodesCommandEnt.AlignNodesCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAlignNodesCommandEnt;
import org.knime.gateway.api.webui.entity.AllowedActionsEnt.AllowedActionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAllowedActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedConnectionActionsEnt.AllowedConnectionActionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAllowedConnectionActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedLoopActionsEnt.AllowedLoopActionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAllowedLoopActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt.AllowedNodeActionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedWorkflowActionsEnt.AllowedWorkflowActionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAllowedWorkflowActionsEnt;
import org.knime.gateway.api.webui.entity.AncestorInfoEnt.AncestorInfoEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAncestorInfoEnt;
import org.knime.gateway.api.webui.entity.AnnotationEnt.AnnotationEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAnnotationEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventEnt.AppStateChangedEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAppStateChangedEventEnt;
import org.knime.gateway.api.webui.entity.AppStateChangedEventTypeEnt.AppStateChangedEventTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAppStateChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppStateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAppStateEnt;
import org.knime.gateway.api.webui.entity.AutoConnectCommandEnt.AutoConnectCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAutoConnectCommandEnt;
import org.knime.gateway.api.webui.entity.AutoDisconnectCommandEnt.AutoDisconnectCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAutoDisconnectCommandEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultBoundsEnt;
import org.knime.gateway.api.webui.entity.CategoryMetadataEnt.CategoryMetadataEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCategoryMetadataEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt.CollapseCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCollapseCommandEnt;
import org.knime.gateway.api.webui.entity.CollapseResultEnt.CollapseResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCollapseResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt.CommandResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCommandResultEnt;
import org.knime.gateway.api.webui.entity.ComponentEditorConfigEnt.ComponentEditorConfigEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentEditorConfigEnt;
import org.knime.gateway.api.webui.entity.ComponentEditorStateEnt.ComponentEditorStateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentEditorStateEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeAndDescriptionEnt.ComponentNodeAndDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeAndDescriptionEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt.ComponentNodeDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt.ComponentNodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.ComponentPlaceholderEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentPlaceholderEnt;
import org.knime.gateway.api.webui.entity.ComponentPortDescriptionEnt.ComponentPortDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentPortDescriptionEnt;
import org.knime.gateway.api.webui.entity.CompositeEventEnt.CompositeEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCompositeEventEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt.ConnectCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultConnectCommandEnt;
import org.knime.gateway.api.webui.entity.ConnectablesBasedCommandEnt.ConnectablesBasedCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultConnectablesBasedCommandEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultConnectionEnt;
import org.knime.gateway.api.webui.entity.ConvertContainerResultEnt.ConvertContainerResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultConvertContainerResultEnt;
import org.knime.gateway.api.webui.entity.CopyCommandEnt.CopyCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCopyCommandEnt;
import org.knime.gateway.api.webui.entity.CopyResultEnt.CopyResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCopyResultEnt;
import org.knime.gateway.api.webui.entity.CustomJobManagerEnt.CustomJobManagerEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCustomJobManagerEnt;
import org.knime.gateway.api.webui.entity.CutCommandEnt.CutCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCutCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt.DeleteCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultDeleteCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteComponentPlaceholderCommandEnt.DeleteComponentPlaceholderCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultDeleteComponentPlaceholderCommandEnt;
import org.knime.gateway.api.webui.entity.DynamicPortGroupDescriptionEnt.DynamicPortGroupDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultDynamicPortGroupDescriptionEnt;
import org.knime.gateway.api.webui.entity.EditableMetadataEnt.EditableMetadataEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultEditableMetadataEnt;
import org.knime.gateway.api.webui.entity.EventEnt.EventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultEventEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt.EventTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultEventTypeEnt;
import org.knime.gateway.api.webui.entity.ExpandCommandEnt.ExpandCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultExpandCommandEnt;
import org.knime.gateway.api.webui.entity.ExpandResultEnt.ExpandResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultExpandResultEnt;
import org.knime.gateway.api.webui.entity.ExtensionEnt.ExtensionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultExtensionEnt;
import org.knime.gateway.api.webui.entity.GatewayProblemDescriptionEnt.GatewayProblemDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultGatewayProblemDescriptionEnt;
import org.knime.gateway.api.webui.entity.InsertNodeCommandEnt.InsertNodeCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultInsertNodeCommandEnt;
import org.knime.gateway.api.webui.entity.JobManagerEnt.JobManagerEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultJobManagerEnt;
import org.knime.gateway.api.webui.entity.KaiFeedbackEnt.KaiFeedbackEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiFeedbackEnt;
import org.knime.gateway.api.webui.entity.KaiMessageEnt.KaiMessageEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiMessageEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionContextEnt.KaiQuickActionContextEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiQuickActionContextEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionErrorEnt.KaiQuickActionErrorEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiQuickActionErrorEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionGenerateAnnotationContextEnt.KaiQuickActionGenerateAnnotationContextEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiQuickActionGenerateAnnotationContextEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionGenerateAnnotationRequestEnt.KaiQuickActionGenerateAnnotationRequestEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiQuickActionGenerateAnnotationRequestEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionGenerateAnnotationResponseEnt.KaiQuickActionGenerateAnnotationResponseEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiQuickActionGenerateAnnotationResponseEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionGenerateAnnotationResultEnt.KaiQuickActionGenerateAnnotationResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiQuickActionGenerateAnnotationResultEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionRequestEnt.KaiQuickActionRequestEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiQuickActionRequestEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionResponseEnt.KaiQuickActionResponseEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiQuickActionResponseEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionResultEnt.KaiQuickActionResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiQuickActionResultEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionsAvailableEnt.KaiQuickActionsAvailableEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiQuickActionsAvailableEnt;
import org.knime.gateway.api.webui.entity.KaiRequestEnt.KaiRequestEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiRequestEnt;
import org.knime.gateway.api.webui.entity.KaiUiStringsEnt.KaiUiStringsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiUiStringsEnt;
import org.knime.gateway.api.webui.entity.KaiUsageEnt.KaiUsageEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiUsageEnt;
import org.knime.gateway.api.webui.entity.KaiWelcomeMessagesEnt.KaiWelcomeMessagesEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultKaiWelcomeMessagesEnt;
import org.knime.gateway.api.webui.entity.LegacyViewNodeConfigEnt.LegacyViewNodeConfigEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultLegacyViewNodeConfigEnt;
import org.knime.gateway.api.webui.entity.LinkEnt.LinkEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultLinkEnt;
import org.knime.gateway.api.webui.entity.LinkVariantEnt.LinkVariantEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultLinkVariantEnt;
import org.knime.gateway.api.webui.entity.LinkVariantInfoEnt.LinkVariantInfoEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultLinkVariantInfoEnt;
import org.knime.gateway.api.webui.entity.LoopInfoEnt.LoopInfoEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultLoopInfoEnt;
import org.knime.gateway.api.webui.entity.MetaNodeEnt.MetaNodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultMetaNodeEnt;
import org.knime.gateway.api.webui.entity.MetaNodePortEnt.MetaNodePortEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultMetaNodePortEnt;
import org.knime.gateway.api.webui.entity.MetaNodeStateEnt.MetaNodeStateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultMetaNodeStateEnt;
import org.knime.gateway.api.webui.entity.MetaPortsEnt.MetaPortsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultMetaPortsEnt;
import org.knime.gateway.api.webui.entity.NativeNodeDescriptionEnt.NativeNodeDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNativeNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNativeNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt.NativeNodeInvariantsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeCategoryEnt.NodeCategoryEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeCategoryEnt;
import org.knime.gateway.api.webui.entity.NodeDescriptionEnt.NodeDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionDescriptionEnt.NodeDialogOptionDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeDialogOptionDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionGroupEnt.NodeDialogOptionGroupEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeDialogOptionGroupEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.NodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt.NodeExecutionInfoEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodeGroupEnt.NodeGroupEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeGroupEnt;
import org.knime.gateway.api.webui.entity.NodeGroupsEnt.NodeGroupsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeGroupsEnt;
import org.knime.gateway.api.webui.entity.NodeIdAndIsExecutedEnt.NodeIdAndIsExecutedEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeIdAndIsExecutedEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt.NodePortDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt.NodePortTemplateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodePortTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeRepositoryLoadingProgressEventEnt.NodeRepositoryLoadingProgressEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeRepositoryLoadingProgressEventEnt;
import org.knime.gateway.api.webui.entity.NodeRepositoryLoadingProgressEventTypeEnt.NodeRepositoryLoadingProgressEventTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeRepositoryLoadingProgressEventTypeEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt.NodeSearchResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt.NodeTemplateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt.NodeViewDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeViewDescriptionEnt;
import org.knime.gateway.api.webui.entity.PartBasedCommandEnt.PartBasedCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPartBasedCommandEnt;
import org.knime.gateway.api.webui.entity.PasteCommandEnt.PasteCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPasteCommandEnt;
import org.knime.gateway.api.webui.entity.PasteResultEnt.PasteResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPasteResultEnt;
import org.knime.gateway.api.webui.entity.PatchEnt.PatchEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPatchEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt.PatchOpEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPatchOpEnt;
import org.knime.gateway.api.webui.entity.PortCommandEnt.PortCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPortCommandEnt;
import org.knime.gateway.api.webui.entity.PortGroupEnt.PortGroupEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPortGroupEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt.PortTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPortTypeEnt;
import org.knime.gateway.api.webui.entity.PortViewDescriptorEnt.PortViewDescriptorEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPortViewDescriptorEnt;
import org.knime.gateway.api.webui.entity.PortViewDescriptorMappingEnt.PortViewDescriptorMappingEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPortViewDescriptorMappingEnt;
import org.knime.gateway.api.webui.entity.PortViewsEnt.PortViewsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPortViewsEnt;
import org.knime.gateway.api.webui.entity.ProjectDirtyStateEventEnt.ProjectDirtyStateEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultProjectDirtyStateEventEnt;
import org.knime.gateway.api.webui.entity.ProjectDisposedEventEnt.ProjectDisposedEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultProjectDisposedEventEnt;
import org.knime.gateway.api.webui.entity.ProjectDisposedEventTypeEnt.ProjectDisposedEventTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultProjectDisposedEventTypeEnt;
import org.knime.gateway.api.webui.entity.ProjectEnt.ProjectEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultProjectEnt;
import org.knime.gateway.api.webui.entity.ProjectMetadataEnt.ProjectMetadataEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultProjectMetadataEnt;
import org.knime.gateway.api.webui.entity.ProjectSyncStateEnt.ProjectSyncStateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultProjectSyncStateEnt;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt.RemovePortCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultRemovePortCommandEnt;
import org.knime.gateway.api.webui.entity.ReorderWorkflowAnnotationsCommandEnt.ReorderWorkflowAnnotationsCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultReorderWorkflowAnnotationsCommandEnt;
import org.knime.gateway.api.webui.entity.ReplaceNodeCommandEnt.ReplaceNodeCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultReplaceNodeCommandEnt;
import org.knime.gateway.api.webui.entity.SelectionEventEnt.SelectionEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSelectionEventEnt;
import org.knime.gateway.api.webui.entity.ShareComponentCommandEnt.ShareComponentCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultShareComponentCommandEnt;
import org.knime.gateway.api.webui.entity.ShareComponentResultEnt.ShareComponentResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultShareComponentResultEnt;
import org.knime.gateway.api.webui.entity.ShowToastEventEnt.ShowToastEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultShowToastEventEnt;
import org.knime.gateway.api.webui.entity.SpaceEnt.SpaceEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSpaceEnt;
import org.knime.gateway.api.webui.entity.SpaceGroupEnt.SpaceGroupEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSpaceGroupEnt;
import org.knime.gateway.api.webui.entity.SpaceItemChangedEventEnt.SpaceItemChangedEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSpaceItemChangedEventEnt;
import org.knime.gateway.api.webui.entity.SpaceItemChangedEventTypeEnt.SpaceItemChangedEventTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSpaceItemChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt.SpaceItemEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.SpaceItemReferenceEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSpaceItemReferenceEnt;
import org.knime.gateway.api.webui.entity.SpaceItemVersionEnt.SpaceItemVersionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSpaceItemVersionEnt;
import org.knime.gateway.api.webui.entity.SpacePathSegmentEnt.SpacePathSegmentEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSpacePathSegmentEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.SpaceProviderEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSpaceProviderEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt.StyleRangeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultStyleRangeEnt;
import org.knime.gateway.api.webui.entity.SyncStateErrorEnt.SyncStateErrorEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultSyncStateErrorEnt;
import org.knime.gateway.api.webui.entity.TemplateLinkEnt.TemplateLinkEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultTemplateLinkEnt;
import org.knime.gateway.api.webui.entity.TransformMetanodePortsBarCommandEnt.TransformMetanodePortsBarCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultTransformMetanodePortsBarCommandEnt;
import org.knime.gateway.api.webui.entity.TransformWorkflowAnnotationCommandEnt.TransformWorkflowAnnotationCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultTransformWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt.TranslateCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultTranslateCommandEnt;
import org.knime.gateway.api.webui.entity.TypedTextEnt.TypedTextEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultTypedTextEnt;
import org.knime.gateway.api.webui.entity.UpdateAvailableEventEnt.UpdateAvailableEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateAvailableEventEnt;
import org.knime.gateway.api.webui.entity.UpdateAvailableEventTypeEnt.UpdateAvailableEventTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateAvailableEventTypeEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentLinkInformationCommandEnt.UpdateComponentLinkInformationCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateComponentLinkInformationCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentMetadataCommandEnt.UpdateComponentMetadataCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateComponentMetadataCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentOrMetanodeNameCommandEnt.UpdateComponentOrMetanodeNameCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateComponentOrMetanodeNameCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateInfoEnt.UpdateInfoEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateInfoEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsCommandEnt.UpdateLinkedComponentsCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateLinkedComponentsCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt.UpdateLinkedComponentsResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateLinkedComponentsResultEnt;
import org.knime.gateway.api.webui.entity.UpdateNodeLabelCommandEnt.UpdateNodeLabelCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateNodeLabelCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateProjectMetadataCommandEnt.UpdateProjectMetadataCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateProjectMetadataCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateWorkflowAnnotationCommandEnt.UpdateWorkflowAnnotationCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultUpdateWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.VendorEnt.VendorEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultVendorEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationCommandEnt.WorkflowAnnotationCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt.WorkflowChangedEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.WorkflowCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt.WorkflowGroupContentEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowGroupContentEnt;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.WorkflowInfoEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowInfoEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorMessageEnt.WorkflowMonitorMessageEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowMonitorMessageEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventEnt.WorkflowMonitorStateChangeEventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowMonitorStateChangeEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateChangeEventTypeEnt.WorkflowMonitorStateChangeEventTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowMonitorStateChangeEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateEnt.WorkflowMonitorStateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowMonitorStateEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateSnapshotEnt.WorkflowMonitorStateSnapshotEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowMonitorStateSnapshotEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultXYEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;

/**
 * Helper to create entity-builder instances.

 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class Interface2ImplMap {

    private Interface2ImplMap() {
        //utility class
    }

    /**
     * Creates an entity-builder instance from the given interface class.
     * @param clazz
     * @return the builder instance or null if there is not match
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <B extends GatewayEntityBuilder> B create(final Class<B> clazz) {
        if(clazz == AddAnnotationResultEntBuilder.class) {
            return (B)new DefaultAddAnnotationResultEnt.DefaultAddAnnotationResultEntBuilder();
        }        
        if(clazz == AddBendpointCommandEntBuilder.class) {
            return (B)new DefaultAddBendpointCommandEnt.DefaultAddBendpointCommandEntBuilder();
        }        
        if(clazz == AddComponentCommandEntBuilder.class) {
            return (B)new DefaultAddComponentCommandEnt.DefaultAddComponentCommandEntBuilder();
        }        
        if(clazz == AddComponentPlaceholderResultEntBuilder.class) {
            return (B)new DefaultAddComponentPlaceholderResultEnt.DefaultAddComponentPlaceholderResultEntBuilder();
        }        
        if(clazz == AddNodeCommandEntBuilder.class) {
            return (B)new DefaultAddNodeCommandEnt.DefaultAddNodeCommandEntBuilder();
        }        
        if(clazz == AddNodeResultEntBuilder.class) {
            return (B)new DefaultAddNodeResultEnt.DefaultAddNodeResultEntBuilder();
        }        
        if(clazz == AddPortCommandEntBuilder.class) {
            return (B)new DefaultAddPortCommandEnt.DefaultAddPortCommandEntBuilder();
        }        
        if(clazz == AddPortResultEntBuilder.class) {
            return (B)new DefaultAddPortResultEnt.DefaultAddPortResultEntBuilder();
        }        
        if(clazz == AddWorkflowAnnotationCommandEntBuilder.class) {
            return (B)new DefaultAddWorkflowAnnotationCommandEnt.DefaultAddWorkflowAnnotationCommandEntBuilder();
        }        
        if(clazz == AlignNodesCommandEntBuilder.class) {
            return (B)new DefaultAlignNodesCommandEnt.DefaultAlignNodesCommandEntBuilder();
        }        
        if(clazz == AllowedActionsEntBuilder.class) {
            return (B)new DefaultAllowedActionsEnt.DefaultAllowedActionsEntBuilder();
        }        
        if(clazz == AllowedConnectionActionsEntBuilder.class) {
            return (B)new DefaultAllowedConnectionActionsEnt.DefaultAllowedConnectionActionsEntBuilder();
        }        
        if(clazz == AllowedLoopActionsEntBuilder.class) {
            return (B)new DefaultAllowedLoopActionsEnt.DefaultAllowedLoopActionsEntBuilder();
        }        
        if(clazz == AllowedNodeActionsEntBuilder.class) {
            return (B)new DefaultAllowedNodeActionsEnt.DefaultAllowedNodeActionsEntBuilder();
        }        
        if(clazz == AllowedWorkflowActionsEntBuilder.class) {
            return (B)new DefaultAllowedWorkflowActionsEnt.DefaultAllowedWorkflowActionsEntBuilder();
        }        
        if(clazz == AncestorInfoEntBuilder.class) {
            return (B)new DefaultAncestorInfoEnt.DefaultAncestorInfoEntBuilder();
        }        
        if(clazz == AnnotationEntBuilder.class) {
            return (B)new DefaultAnnotationEnt.DefaultAnnotationEntBuilder();
        }        
        if(clazz == AppStateChangedEventEntBuilder.class) {
            return (B)new DefaultAppStateChangedEventEnt.DefaultAppStateChangedEventEntBuilder();
        }        
        if(clazz == AppStateChangedEventTypeEntBuilder.class) {
            return (B)new DefaultAppStateChangedEventTypeEnt.DefaultAppStateChangedEventTypeEntBuilder();
        }        
        if(clazz == AppStateEntBuilder.class) {
            return (B)new DefaultAppStateEnt.DefaultAppStateEntBuilder();
        }        
        if(clazz == AutoConnectCommandEntBuilder.class) {
            return (B)new DefaultAutoConnectCommandEnt.DefaultAutoConnectCommandEntBuilder();
        }        
        if(clazz == AutoDisconnectCommandEntBuilder.class) {
            return (B)new DefaultAutoDisconnectCommandEnt.DefaultAutoDisconnectCommandEntBuilder();
        }        
        if(clazz == BoundsEntBuilder.class) {
            return (B)new DefaultBoundsEnt.DefaultBoundsEntBuilder();
        }        
        if(clazz == CategoryMetadataEntBuilder.class) {
            return (B)new DefaultCategoryMetadataEnt.DefaultCategoryMetadataEntBuilder();
        }        
        if(clazz == CollapseCommandEntBuilder.class) {
            return (B)new DefaultCollapseCommandEnt.DefaultCollapseCommandEntBuilder();
        }        
        if(clazz == CollapseResultEntBuilder.class) {
            return (B)new DefaultCollapseResultEnt.DefaultCollapseResultEntBuilder();
        }        
        if(clazz == CommandResultEntBuilder.class) {
            return (B)new DefaultCommandResultEnt.DefaultCommandResultEntBuilder();
        }        
        if(clazz == ComponentEditorConfigEntBuilder.class) {
            return (B)new DefaultComponentEditorConfigEnt.DefaultComponentEditorConfigEntBuilder();
        }        
        if(clazz == ComponentEditorStateEntBuilder.class) {
            return (B)new DefaultComponentEditorStateEnt.DefaultComponentEditorStateEntBuilder();
        }        
        if(clazz == ComponentNodeAndDescriptionEntBuilder.class) {
            return (B)new DefaultComponentNodeAndDescriptionEnt.DefaultComponentNodeAndDescriptionEntBuilder();
        }        
        if(clazz == ComponentNodeDescriptionEntBuilder.class) {
            return (B)new DefaultComponentNodeDescriptionEnt.DefaultComponentNodeDescriptionEntBuilder();
        }        
        if(clazz == ComponentNodeEntBuilder.class) {
            return (B)new DefaultComponentNodeEnt.DefaultComponentNodeEntBuilder();
        }        
        if(clazz == ComponentPlaceholderEntBuilder.class) {
            return (B)new DefaultComponentPlaceholderEnt.DefaultComponentPlaceholderEntBuilder();
        }        
        if(clazz == ComponentPortDescriptionEntBuilder.class) {
            return (B)new DefaultComponentPortDescriptionEnt.DefaultComponentPortDescriptionEntBuilder();
        }        
        if(clazz == CompositeEventEntBuilder.class) {
            return (B)new DefaultCompositeEventEnt.DefaultCompositeEventEntBuilder();
        }        
        if(clazz == ConnectCommandEntBuilder.class) {
            return (B)new DefaultConnectCommandEnt.DefaultConnectCommandEntBuilder();
        }        
        if(clazz == ConnectablesBasedCommandEntBuilder.class) {
            return (B)new DefaultConnectablesBasedCommandEnt.DefaultConnectablesBasedCommandEntBuilder();
        }        
        if(clazz == ConnectionEntBuilder.class) {
            return (B)new DefaultConnectionEnt.DefaultConnectionEntBuilder();
        }        
        if(clazz == ConvertContainerResultEntBuilder.class) {
            return (B)new DefaultConvertContainerResultEnt.DefaultConvertContainerResultEntBuilder();
        }        
        if(clazz == CopyCommandEntBuilder.class) {
            return (B)new DefaultCopyCommandEnt.DefaultCopyCommandEntBuilder();
        }        
        if(clazz == CopyResultEntBuilder.class) {
            return (B)new DefaultCopyResultEnt.DefaultCopyResultEntBuilder();
        }        
        if(clazz == CustomJobManagerEntBuilder.class) {
            return (B)new DefaultCustomJobManagerEnt.DefaultCustomJobManagerEntBuilder();
        }        
        if(clazz == CutCommandEntBuilder.class) {
            return (B)new DefaultCutCommandEnt.DefaultCutCommandEntBuilder();
        }        
        if(clazz == DeleteCommandEntBuilder.class) {
            return (B)new DefaultDeleteCommandEnt.DefaultDeleteCommandEntBuilder();
        }        
        if(clazz == DeleteComponentPlaceholderCommandEntBuilder.class) {
            return (B)new DefaultDeleteComponentPlaceholderCommandEnt.DefaultDeleteComponentPlaceholderCommandEntBuilder();
        }        
        if(clazz == DynamicPortGroupDescriptionEntBuilder.class) {
            return (B)new DefaultDynamicPortGroupDescriptionEnt.DefaultDynamicPortGroupDescriptionEntBuilder();
        }        
        if(clazz == EditableMetadataEntBuilder.class) {
            return (B)new DefaultEditableMetadataEnt.DefaultEditableMetadataEntBuilder();
        }        
        if(clazz == EventEntBuilder.class) {
            return (B)new DefaultEventEnt.DefaultEventEntBuilder();
        }        
        if(clazz == EventTypeEntBuilder.class) {
            return (B)new DefaultEventTypeEnt.DefaultEventTypeEntBuilder();
        }        
        if(clazz == ExpandCommandEntBuilder.class) {
            return (B)new DefaultExpandCommandEnt.DefaultExpandCommandEntBuilder();
        }        
        if(clazz == ExpandResultEntBuilder.class) {
            return (B)new DefaultExpandResultEnt.DefaultExpandResultEntBuilder();
        }        
        if(clazz == ExtensionEntBuilder.class) {
            return (B)new DefaultExtensionEnt.DefaultExtensionEntBuilder();
        }        
        if(clazz == GatewayProblemDescriptionEntBuilder.class) {
            return (B)new DefaultGatewayProblemDescriptionEnt.DefaultGatewayProblemDescriptionEntBuilder();
        }        
        if(clazz == InsertNodeCommandEntBuilder.class) {
            return (B)new DefaultInsertNodeCommandEnt.DefaultInsertNodeCommandEntBuilder();
        }        
        if(clazz == JobManagerEntBuilder.class) {
            return (B)new DefaultJobManagerEnt.DefaultJobManagerEntBuilder();
        }        
        if(clazz == KaiFeedbackEntBuilder.class) {
            return (B)new DefaultKaiFeedbackEnt.DefaultKaiFeedbackEntBuilder();
        }        
        if(clazz == KaiMessageEntBuilder.class) {
            return (B)new DefaultKaiMessageEnt.DefaultKaiMessageEntBuilder();
        }        
        if(clazz == KaiQuickActionContextEntBuilder.class) {
            return (B)new DefaultKaiQuickActionContextEnt.DefaultKaiQuickActionContextEntBuilder();
        }        
        if(clazz == KaiQuickActionErrorEntBuilder.class) {
            return (B)new DefaultKaiQuickActionErrorEnt.DefaultKaiQuickActionErrorEntBuilder();
        }        
        if(clazz == KaiQuickActionGenerateAnnotationContextEntBuilder.class) {
            return (B)new DefaultKaiQuickActionGenerateAnnotationContextEnt.DefaultKaiQuickActionGenerateAnnotationContextEntBuilder();
        }        
        if(clazz == KaiQuickActionGenerateAnnotationRequestEntBuilder.class) {
            return (B)new DefaultKaiQuickActionGenerateAnnotationRequestEnt.DefaultKaiQuickActionGenerateAnnotationRequestEntBuilder();
        }        
        if(clazz == KaiQuickActionGenerateAnnotationResponseEntBuilder.class) {
            return (B)new DefaultKaiQuickActionGenerateAnnotationResponseEnt.DefaultKaiQuickActionGenerateAnnotationResponseEntBuilder();
        }        
        if(clazz == KaiQuickActionGenerateAnnotationResultEntBuilder.class) {
            return (B)new DefaultKaiQuickActionGenerateAnnotationResultEnt.DefaultKaiQuickActionGenerateAnnotationResultEntBuilder();
        }        
        if(clazz == KaiQuickActionRequestEntBuilder.class) {
            return (B)new DefaultKaiQuickActionRequestEnt.DefaultKaiQuickActionRequestEntBuilder();
        }        
        if(clazz == KaiQuickActionResponseEntBuilder.class) {
            return (B)new DefaultKaiQuickActionResponseEnt.DefaultKaiQuickActionResponseEntBuilder();
        }        
        if(clazz == KaiQuickActionResultEntBuilder.class) {
            return (B)new DefaultKaiQuickActionResultEnt.DefaultKaiQuickActionResultEntBuilder();
        }        
        if(clazz == KaiQuickActionsAvailableEntBuilder.class) {
            return (B)new DefaultKaiQuickActionsAvailableEnt.DefaultKaiQuickActionsAvailableEntBuilder();
        }        
        if(clazz == KaiRequestEntBuilder.class) {
            return (B)new DefaultKaiRequestEnt.DefaultKaiRequestEntBuilder();
        }        
        if(clazz == KaiUiStringsEntBuilder.class) {
            return (B)new DefaultKaiUiStringsEnt.DefaultKaiUiStringsEntBuilder();
        }        
        if(clazz == KaiUsageEntBuilder.class) {
            return (B)new DefaultKaiUsageEnt.DefaultKaiUsageEntBuilder();
        }        
        if(clazz == KaiWelcomeMessagesEntBuilder.class) {
            return (B)new DefaultKaiWelcomeMessagesEnt.DefaultKaiWelcomeMessagesEntBuilder();
        }        
        if(clazz == LegacyViewNodeConfigEntBuilder.class) {
            return (B)new DefaultLegacyViewNodeConfigEnt.DefaultLegacyViewNodeConfigEntBuilder();
        }        
        if(clazz == LinkEntBuilder.class) {
            return (B)new DefaultLinkEnt.DefaultLinkEntBuilder();
        }        
        if(clazz == LinkVariantEntBuilder.class) {
            return (B)new DefaultLinkVariantEnt.DefaultLinkVariantEntBuilder();
        }        
        if(clazz == LinkVariantInfoEntBuilder.class) {
            return (B)new DefaultLinkVariantInfoEnt.DefaultLinkVariantInfoEntBuilder();
        }        
        if(clazz == LoopInfoEntBuilder.class) {
            return (B)new DefaultLoopInfoEnt.DefaultLoopInfoEntBuilder();
        }        
        if(clazz == MetaNodeEntBuilder.class) {
            return (B)new DefaultMetaNodeEnt.DefaultMetaNodeEntBuilder();
        }        
        if(clazz == MetaNodePortEntBuilder.class) {
            return (B)new DefaultMetaNodePortEnt.DefaultMetaNodePortEntBuilder();
        }        
        if(clazz == MetaNodeStateEntBuilder.class) {
            return (B)new DefaultMetaNodeStateEnt.DefaultMetaNodeStateEntBuilder();
        }        
        if(clazz == MetaPortsEntBuilder.class) {
            return (B)new DefaultMetaPortsEnt.DefaultMetaPortsEntBuilder();
        }        
        if(clazz == NativeNodeDescriptionEntBuilder.class) {
            return (B)new DefaultNativeNodeDescriptionEnt.DefaultNativeNodeDescriptionEntBuilder();
        }        
        if(clazz == NativeNodeEntBuilder.class) {
            return (B)new DefaultNativeNodeEnt.DefaultNativeNodeEntBuilder();
        }        
        if(clazz == NativeNodeInvariantsEntBuilder.class) {
            return (B)new DefaultNativeNodeInvariantsEnt.DefaultNativeNodeInvariantsEntBuilder();
        }        
        if(clazz == NodeAnnotationEntBuilder.class) {
            return (B)new DefaultNodeAnnotationEnt.DefaultNodeAnnotationEntBuilder();
        }        
        if(clazz == NodeCategoryEntBuilder.class) {
            return (B)new DefaultNodeCategoryEnt.DefaultNodeCategoryEntBuilder();
        }        
        if(clazz == NodeDescriptionEntBuilder.class) {
            return (B)new DefaultNodeDescriptionEnt.DefaultNodeDescriptionEntBuilder();
        }        
        if(clazz == NodeDialogOptionDescriptionEntBuilder.class) {
            return (B)new DefaultNodeDialogOptionDescriptionEnt.DefaultNodeDialogOptionDescriptionEntBuilder();
        }        
        if(clazz == NodeDialogOptionGroupEntBuilder.class) {
            return (B)new DefaultNodeDialogOptionGroupEnt.DefaultNodeDialogOptionGroupEntBuilder();
        }        
        if(clazz == NodeEntBuilder.class) {
            return (B)new DefaultNodeEnt.DefaultNodeEntBuilder();
        }        
        if(clazz == NodeExecutionInfoEntBuilder.class) {
            return (B)new DefaultNodeExecutionInfoEnt.DefaultNodeExecutionInfoEntBuilder();
        }        
        if(clazz == NodeFactoryKeyEntBuilder.class) {
            return (B)new DefaultNodeFactoryKeyEnt.DefaultNodeFactoryKeyEntBuilder();
        }        
        if(clazz == NodeGroupEntBuilder.class) {
            return (B)new DefaultNodeGroupEnt.DefaultNodeGroupEntBuilder();
        }        
        if(clazz == NodeGroupsEntBuilder.class) {
            return (B)new DefaultNodeGroupsEnt.DefaultNodeGroupsEntBuilder();
        }        
        if(clazz == NodeIdAndIsExecutedEntBuilder.class) {
            return (B)new DefaultNodeIdAndIsExecutedEnt.DefaultNodeIdAndIsExecutedEntBuilder();
        }        
        if(clazz == NodePortDescriptionEntBuilder.class) {
            return (B)new DefaultNodePortDescriptionEnt.DefaultNodePortDescriptionEntBuilder();
        }        
        if(clazz == NodePortEntBuilder.class) {
            return (B)new DefaultNodePortEnt.DefaultNodePortEntBuilder();
        }        
        if(clazz == NodePortTemplateEntBuilder.class) {
            return (B)new DefaultNodePortTemplateEnt.DefaultNodePortTemplateEntBuilder();
        }        
        if(clazz == NodeRepositoryLoadingProgressEventEntBuilder.class) {
            return (B)new DefaultNodeRepositoryLoadingProgressEventEnt.DefaultNodeRepositoryLoadingProgressEventEntBuilder();
        }        
        if(clazz == NodeRepositoryLoadingProgressEventTypeEntBuilder.class) {
            return (B)new DefaultNodeRepositoryLoadingProgressEventTypeEnt.DefaultNodeRepositoryLoadingProgressEventTypeEntBuilder();
        }        
        if(clazz == NodeSearchResultEntBuilder.class) {
            return (B)new DefaultNodeSearchResultEnt.DefaultNodeSearchResultEntBuilder();
        }        
        if(clazz == NodeStateEntBuilder.class) {
            return (B)new DefaultNodeStateEnt.DefaultNodeStateEntBuilder();
        }        
        if(clazz == NodeTemplateEntBuilder.class) {
            return (B)new DefaultNodeTemplateEnt.DefaultNodeTemplateEntBuilder();
        }        
        if(clazz == NodeViewDescriptionEntBuilder.class) {
            return (B)new DefaultNodeViewDescriptionEnt.DefaultNodeViewDescriptionEntBuilder();
        }        
        if(clazz == PartBasedCommandEntBuilder.class) {
            return (B)new DefaultPartBasedCommandEnt.DefaultPartBasedCommandEntBuilder();
        }        
        if(clazz == PasteCommandEntBuilder.class) {
            return (B)new DefaultPasteCommandEnt.DefaultPasteCommandEntBuilder();
        }        
        if(clazz == PasteResultEntBuilder.class) {
            return (B)new DefaultPasteResultEnt.DefaultPasteResultEntBuilder();
        }        
        if(clazz == PatchEntBuilder.class) {
            return (B)new DefaultPatchEnt.DefaultPatchEntBuilder();
        }        
        if(clazz == PatchOpEntBuilder.class) {
            return (B)new DefaultPatchOpEnt.DefaultPatchOpEntBuilder();
        }        
        if(clazz == PortCommandEntBuilder.class) {
            return (B)new DefaultPortCommandEnt.DefaultPortCommandEntBuilder();
        }        
        if(clazz == PortGroupEntBuilder.class) {
            return (B)new DefaultPortGroupEnt.DefaultPortGroupEntBuilder();
        }        
        if(clazz == PortTypeEntBuilder.class) {
            return (B)new DefaultPortTypeEnt.DefaultPortTypeEntBuilder();
        }        
        if(clazz == PortViewDescriptorEntBuilder.class) {
            return (B)new DefaultPortViewDescriptorEnt.DefaultPortViewDescriptorEntBuilder();
        }        
        if(clazz == PortViewDescriptorMappingEntBuilder.class) {
            return (B)new DefaultPortViewDescriptorMappingEnt.DefaultPortViewDescriptorMappingEntBuilder();
        }        
        if(clazz == PortViewsEntBuilder.class) {
            return (B)new DefaultPortViewsEnt.DefaultPortViewsEntBuilder();
        }        
        if(clazz == ProjectDirtyStateEventEntBuilder.class) {
            return (B)new DefaultProjectDirtyStateEventEnt.DefaultProjectDirtyStateEventEntBuilder();
        }        
        if(clazz == ProjectDisposedEventEntBuilder.class) {
            return (B)new DefaultProjectDisposedEventEnt.DefaultProjectDisposedEventEntBuilder();
        }        
        if(clazz == ProjectDisposedEventTypeEntBuilder.class) {
            return (B)new DefaultProjectDisposedEventTypeEnt.DefaultProjectDisposedEventTypeEntBuilder();
        }        
        if(clazz == ProjectEntBuilder.class) {
            return (B)new DefaultProjectEnt.DefaultProjectEntBuilder();
        }        
        if(clazz == ProjectMetadataEntBuilder.class) {
            return (B)new DefaultProjectMetadataEnt.DefaultProjectMetadataEntBuilder();
        }        
        if(clazz == ProjectSyncStateEntBuilder.class) {
            return (B)new DefaultProjectSyncStateEnt.DefaultProjectSyncStateEntBuilder();
        }        
        if(clazz == RemovePortCommandEntBuilder.class) {
            return (B)new DefaultRemovePortCommandEnt.DefaultRemovePortCommandEntBuilder();
        }        
        if(clazz == ReorderWorkflowAnnotationsCommandEntBuilder.class) {
            return (B)new DefaultReorderWorkflowAnnotationsCommandEnt.DefaultReorderWorkflowAnnotationsCommandEntBuilder();
        }        
        if(clazz == ReplaceNodeCommandEntBuilder.class) {
            return (B)new DefaultReplaceNodeCommandEnt.DefaultReplaceNodeCommandEntBuilder();
        }        
        if(clazz == SelectionEventEntBuilder.class) {
            return (B)new DefaultSelectionEventEnt.DefaultSelectionEventEntBuilder();
        }        
        if(clazz == ShareComponentCommandEntBuilder.class) {
            return (B)new DefaultShareComponentCommandEnt.DefaultShareComponentCommandEntBuilder();
        }        
        if(clazz == ShareComponentResultEntBuilder.class) {
            return (B)new DefaultShareComponentResultEnt.DefaultShareComponentResultEntBuilder();
        }        
        if(clazz == ShowToastEventEntBuilder.class) {
            return (B)new DefaultShowToastEventEnt.DefaultShowToastEventEntBuilder();
        }        
        if(clazz == SpaceEntBuilder.class) {
            return (B)new DefaultSpaceEnt.DefaultSpaceEntBuilder();
        }        
        if(clazz == SpaceGroupEntBuilder.class) {
            return (B)new DefaultSpaceGroupEnt.DefaultSpaceGroupEntBuilder();
        }        
        if(clazz == SpaceItemChangedEventEntBuilder.class) {
            return (B)new DefaultSpaceItemChangedEventEnt.DefaultSpaceItemChangedEventEntBuilder();
        }        
        if(clazz == SpaceItemChangedEventTypeEntBuilder.class) {
            return (B)new DefaultSpaceItemChangedEventTypeEnt.DefaultSpaceItemChangedEventTypeEntBuilder();
        }        
        if(clazz == SpaceItemEntBuilder.class) {
            return (B)new DefaultSpaceItemEnt.DefaultSpaceItemEntBuilder();
        }        
        if(clazz == SpaceItemReferenceEntBuilder.class) {
            return (B)new DefaultSpaceItemReferenceEnt.DefaultSpaceItemReferenceEntBuilder();
        }        
        if(clazz == SpaceItemVersionEntBuilder.class) {
            return (B)new DefaultSpaceItemVersionEnt.DefaultSpaceItemVersionEntBuilder();
        }        
        if(clazz == SpacePathSegmentEntBuilder.class) {
            return (B)new DefaultSpacePathSegmentEnt.DefaultSpacePathSegmentEntBuilder();
        }        
        if(clazz == SpaceProviderEntBuilder.class) {
            return (B)new DefaultSpaceProviderEnt.DefaultSpaceProviderEntBuilder();
        }        
        if(clazz == StyleRangeEntBuilder.class) {
            return (B)new DefaultStyleRangeEnt.DefaultStyleRangeEntBuilder();
        }        
        if(clazz == SyncStateErrorEntBuilder.class) {
            return (B)new DefaultSyncStateErrorEnt.DefaultSyncStateErrorEntBuilder();
        }        
        if(clazz == TemplateLinkEntBuilder.class) {
            return (B)new DefaultTemplateLinkEnt.DefaultTemplateLinkEntBuilder();
        }        
        if(clazz == TransformMetanodePortsBarCommandEntBuilder.class) {
            return (B)new DefaultTransformMetanodePortsBarCommandEnt.DefaultTransformMetanodePortsBarCommandEntBuilder();
        }        
        if(clazz == TransformWorkflowAnnotationCommandEntBuilder.class) {
            return (B)new DefaultTransformWorkflowAnnotationCommandEnt.DefaultTransformWorkflowAnnotationCommandEntBuilder();
        }        
        if(clazz == TranslateCommandEntBuilder.class) {
            return (B)new DefaultTranslateCommandEnt.DefaultTranslateCommandEntBuilder();
        }        
        if(clazz == TypedTextEntBuilder.class) {
            return (B)new DefaultTypedTextEnt.DefaultTypedTextEntBuilder();
        }        
        if(clazz == UpdateAvailableEventEntBuilder.class) {
            return (B)new DefaultUpdateAvailableEventEnt.DefaultUpdateAvailableEventEntBuilder();
        }        
        if(clazz == UpdateAvailableEventTypeEntBuilder.class) {
            return (B)new DefaultUpdateAvailableEventTypeEnt.DefaultUpdateAvailableEventTypeEntBuilder();
        }        
        if(clazz == UpdateComponentLinkInformationCommandEntBuilder.class) {
            return (B)new DefaultUpdateComponentLinkInformationCommandEnt.DefaultUpdateComponentLinkInformationCommandEntBuilder();
        }        
        if(clazz == UpdateComponentMetadataCommandEntBuilder.class) {
            return (B)new DefaultUpdateComponentMetadataCommandEnt.DefaultUpdateComponentMetadataCommandEntBuilder();
        }        
        if(clazz == UpdateComponentOrMetanodeNameCommandEntBuilder.class) {
            return (B)new DefaultUpdateComponentOrMetanodeNameCommandEnt.DefaultUpdateComponentOrMetanodeNameCommandEntBuilder();
        }        
        if(clazz == UpdateInfoEntBuilder.class) {
            return (B)new DefaultUpdateInfoEnt.DefaultUpdateInfoEntBuilder();
        }        
        if(clazz == UpdateLinkedComponentsCommandEntBuilder.class) {
            return (B)new DefaultUpdateLinkedComponentsCommandEnt.DefaultUpdateLinkedComponentsCommandEntBuilder();
        }        
        if(clazz == UpdateLinkedComponentsResultEntBuilder.class) {
            return (B)new DefaultUpdateLinkedComponentsResultEnt.DefaultUpdateLinkedComponentsResultEntBuilder();
        }        
        if(clazz == UpdateNodeLabelCommandEntBuilder.class) {
            return (B)new DefaultUpdateNodeLabelCommandEnt.DefaultUpdateNodeLabelCommandEntBuilder();
        }        
        if(clazz == UpdateProjectMetadataCommandEntBuilder.class) {
            return (B)new DefaultUpdateProjectMetadataCommandEnt.DefaultUpdateProjectMetadataCommandEntBuilder();
        }        
        if(clazz == UpdateWorkflowAnnotationCommandEntBuilder.class) {
            return (B)new DefaultUpdateWorkflowAnnotationCommandEnt.DefaultUpdateWorkflowAnnotationCommandEntBuilder();
        }        
        if(clazz == VendorEntBuilder.class) {
            return (B)new DefaultVendorEnt.DefaultVendorEntBuilder();
        }        
        if(clazz == WorkflowAnnotationCommandEntBuilder.class) {
            return (B)new DefaultWorkflowAnnotationCommandEnt.DefaultWorkflowAnnotationCommandEntBuilder();
        }        
        if(clazz == WorkflowAnnotationEntBuilder.class) {
            return (B)new DefaultWorkflowAnnotationEnt.DefaultWorkflowAnnotationEntBuilder();
        }        
        if(clazz == WorkflowChangedEventEntBuilder.class) {
            return (B)new DefaultWorkflowChangedEventEnt.DefaultWorkflowChangedEventEntBuilder();
        }        
        if(clazz == WorkflowChangedEventTypeEntBuilder.class) {
            return (B)new DefaultWorkflowChangedEventTypeEnt.DefaultWorkflowChangedEventTypeEntBuilder();
        }        
        if(clazz == WorkflowCommandEntBuilder.class) {
            return (B)new DefaultWorkflowCommandEnt.DefaultWorkflowCommandEntBuilder();
        }        
        if(clazz == WorkflowEntBuilder.class) {
            return (B)new DefaultWorkflowEnt.DefaultWorkflowEntBuilder();
        }        
        if(clazz == WorkflowGroupContentEntBuilder.class) {
            return (B)new DefaultWorkflowGroupContentEnt.DefaultWorkflowGroupContentEntBuilder();
        }        
        if(clazz == WorkflowInfoEntBuilder.class) {
            return (B)new DefaultWorkflowInfoEnt.DefaultWorkflowInfoEntBuilder();
        }        
        if(clazz == WorkflowMonitorMessageEntBuilder.class) {
            return (B)new DefaultWorkflowMonitorMessageEnt.DefaultWorkflowMonitorMessageEntBuilder();
        }        
        if(clazz == WorkflowMonitorStateChangeEventEntBuilder.class) {
            return (B)new DefaultWorkflowMonitorStateChangeEventEnt.DefaultWorkflowMonitorStateChangeEventEntBuilder();
        }        
        if(clazz == WorkflowMonitorStateChangeEventTypeEntBuilder.class) {
            return (B)new DefaultWorkflowMonitorStateChangeEventTypeEnt.DefaultWorkflowMonitorStateChangeEventTypeEntBuilder();
        }        
        if(clazz == WorkflowMonitorStateEntBuilder.class) {
            return (B)new DefaultWorkflowMonitorStateEnt.DefaultWorkflowMonitorStateEntBuilder();
        }        
        if(clazz == WorkflowMonitorStateSnapshotEntBuilder.class) {
            return (B)new DefaultWorkflowMonitorStateSnapshotEnt.DefaultWorkflowMonitorStateSnapshotEntBuilder();
        }        
        if(clazz == WorkflowSnapshotEntBuilder.class) {
            return (B)new DefaultWorkflowSnapshotEnt.DefaultWorkflowSnapshotEntBuilder();
        }        
        if(clazz == XYEntBuilder.class) {
            return (B)new DefaultXYEnt.DefaultXYEntBuilder();
        }        
        else {
            return null;
        }    
    }
}
