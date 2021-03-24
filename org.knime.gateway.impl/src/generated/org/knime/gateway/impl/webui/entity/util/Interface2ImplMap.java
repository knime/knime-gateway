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

import org.knime.gateway.api.webui.entity.AllowedActionsEnt.AllowedActionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAllowedActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedLoopActionsEnt.AllowedLoopActionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAllowedLoopActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt.AllowedNodeActionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.AllowedWorkflowActionsEnt.AllowedWorkflowActionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAllowedWorkflowActionsEnt;
import org.knime.gateway.api.webui.entity.AnnotationEnt.AnnotationEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAnnotationEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppStateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAppStateEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultBoundsEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeAndDescriptionEnt.ComponentNodeAndDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeAndDescriptionEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt.ComponentNodeDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt.ComponentNodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt.ConnectCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultConnectCommandEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultConnectionEnt;
import org.knime.gateway.api.webui.entity.CustomJobManagerEnt.CustomJobManagerEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultCustomJobManagerEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt.DeleteCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultDeleteCommandEnt;
import org.knime.gateway.api.webui.entity.EventEnt.EventEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultEventEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt.EventTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultEventTypeEnt;
import org.knime.gateway.api.webui.entity.JobManagerEnt.JobManagerEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultJobManagerEnt;
import org.knime.gateway.api.webui.entity.LinkEnt.LinkEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultLinkEnt;
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
import org.knime.gateway.api.webui.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNativeNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt.NativeNodeInvariantsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionsEnt.NodeDialogOptionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeDialogOptionsEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptions_fieldsEnt.NodeDialogOptions_fieldsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeDialogOptions_fieldsEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.NodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeEnt;
import org.knime.gateway.api.webui.entity.NodeExecutionInfoEnt.NodeExecutionInfoEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeExecutionInfoEnt;
import org.knime.gateway.api.webui.entity.NodePortAndDescriptionEnt.NodePortAndDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodePortAndDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt.NodePortDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortInvariantsEnt.NodePortInvariantsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodePortInvariantsEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt.NodeSearchResultEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeSelectionEnt.NodeSelectionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeSelectionEnt;
import org.knime.gateway.api.webui.entity.NodeSelectionsEnt.NodeSelectionsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeSelectionsEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt.NodeTemplateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt.NodeViewDescriptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeViewDescriptionEnt;
import org.knime.gateway.api.webui.entity.PatchEnt.PatchEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPatchEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt.PatchOpEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPatchOpEnt;
import org.knime.gateway.api.webui.entity.PortViewEnt.PortViewEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPortViewEnt;
import org.knime.gateway.api.webui.entity.ProjectMetadataEnt.ProjectMetadataEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultProjectMetadataEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt.StyleRangeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultStyleRangeEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt.TranslateCommandEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultTranslateCommandEnt;
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
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt.WorkflowInfoEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowInfoEnt;
import org.knime.gateway.api.webui.entity.WorkflowProjectEnt.WorkflowProjectEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowProjectEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultXYEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;

/**
 * Helper to create entity-builder instances.

 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
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
        if(clazz == AllowedActionsEntBuilder.class) {
            return (B)new DefaultAllowedActionsEnt.DefaultAllowedActionsEntBuilder();
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
        if(clazz == AnnotationEntBuilder.class) {
            return (B)new DefaultAnnotationEnt.DefaultAnnotationEntBuilder();
        }        
        if(clazz == AppStateEntBuilder.class) {
            return (B)new DefaultAppStateEnt.DefaultAppStateEntBuilder();
        }        
        if(clazz == BoundsEntBuilder.class) {
            return (B)new DefaultBoundsEnt.DefaultBoundsEntBuilder();
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
        if(clazz == ConnectCommandEntBuilder.class) {
            return (B)new DefaultConnectCommandEnt.DefaultConnectCommandEntBuilder();
        }        
        if(clazz == ConnectionEntBuilder.class) {
            return (B)new DefaultConnectionEnt.DefaultConnectionEntBuilder();
        }        
        if(clazz == CustomJobManagerEntBuilder.class) {
            return (B)new DefaultCustomJobManagerEnt.DefaultCustomJobManagerEntBuilder();
        }        
        if(clazz == DeleteCommandEntBuilder.class) {
            return (B)new DefaultDeleteCommandEnt.DefaultDeleteCommandEntBuilder();
        }        
        if(clazz == EventEntBuilder.class) {
            return (B)new DefaultEventEnt.DefaultEventEntBuilder();
        }        
        if(clazz == EventTypeEntBuilder.class) {
            return (B)new DefaultEventTypeEnt.DefaultEventTypeEntBuilder();
        }        
        if(clazz == JobManagerEntBuilder.class) {
            return (B)new DefaultJobManagerEnt.DefaultJobManagerEntBuilder();
        }        
        if(clazz == LinkEntBuilder.class) {
            return (B)new DefaultLinkEnt.DefaultLinkEntBuilder();
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
        if(clazz == NativeNodeEntBuilder.class) {
            return (B)new DefaultNativeNodeEnt.DefaultNativeNodeEntBuilder();
        }        
        if(clazz == NativeNodeInvariantsEntBuilder.class) {
            return (B)new DefaultNativeNodeInvariantsEnt.DefaultNativeNodeInvariantsEntBuilder();
        }        
        if(clazz == NodeAnnotationEntBuilder.class) {
            return (B)new DefaultNodeAnnotationEnt.DefaultNodeAnnotationEntBuilder();
        }        
        if(clazz == NodeDialogOptionsEntBuilder.class) {
            return (B)new DefaultNodeDialogOptionsEnt.DefaultNodeDialogOptionsEntBuilder();
        }        
        if(clazz == NodeDialogOptions_fieldsEntBuilder.class) {
            return (B)new DefaultNodeDialogOptions_fieldsEnt.DefaultNodeDialogOptions_fieldsEntBuilder();
        }        
        if(clazz == NodeEntBuilder.class) {
            return (B)new DefaultNodeEnt.DefaultNodeEntBuilder();
        }        
        if(clazz == NodeExecutionInfoEntBuilder.class) {
            return (B)new DefaultNodeExecutionInfoEnt.DefaultNodeExecutionInfoEntBuilder();
        }        
        if(clazz == NodePortAndDescriptionEntBuilder.class) {
            return (B)new DefaultNodePortAndDescriptionEnt.DefaultNodePortAndDescriptionEntBuilder();
        }        
        if(clazz == NodePortDescriptionEntBuilder.class) {
            return (B)new DefaultNodePortDescriptionEnt.DefaultNodePortDescriptionEntBuilder();
        }        
        if(clazz == NodePortEntBuilder.class) {
            return (B)new DefaultNodePortEnt.DefaultNodePortEntBuilder();
        }        
        if(clazz == NodePortInvariantsEntBuilder.class) {
            return (B)new DefaultNodePortInvariantsEnt.DefaultNodePortInvariantsEntBuilder();
        }        
        if(clazz == NodeSearchResultEntBuilder.class) {
            return (B)new DefaultNodeSearchResultEnt.DefaultNodeSearchResultEntBuilder();
        }        
        if(clazz == NodeSelectionEntBuilder.class) {
            return (B)new DefaultNodeSelectionEnt.DefaultNodeSelectionEntBuilder();
        }        
        if(clazz == NodeSelectionsEntBuilder.class) {
            return (B)new DefaultNodeSelectionsEnt.DefaultNodeSelectionsEntBuilder();
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
        if(clazz == PatchEntBuilder.class) {
            return (B)new DefaultPatchEnt.DefaultPatchEntBuilder();
        }        
        if(clazz == PatchOpEntBuilder.class) {
            return (B)new DefaultPatchOpEnt.DefaultPatchOpEntBuilder();
        }        
        if(clazz == PortViewEntBuilder.class) {
            return (B)new DefaultPortViewEnt.DefaultPortViewEntBuilder();
        }        
        if(clazz == ProjectMetadataEntBuilder.class) {
            return (B)new DefaultProjectMetadataEnt.DefaultProjectMetadataEntBuilder();
        }        
        if(clazz == StyleRangeEntBuilder.class) {
            return (B)new DefaultStyleRangeEnt.DefaultStyleRangeEntBuilder();
        }        
        if(clazz == TranslateCommandEntBuilder.class) {
            return (B)new DefaultTranslateCommandEnt.DefaultTranslateCommandEntBuilder();
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
        if(clazz == WorkflowInfoEntBuilder.class) {
            return (B)new DefaultWorkflowInfoEnt.DefaultWorkflowInfoEntBuilder();
        }        
        if(clazz == WorkflowProjectEntBuilder.class) {
            return (B)new DefaultWorkflowProjectEnt.DefaultWorkflowProjectEntBuilder();
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
