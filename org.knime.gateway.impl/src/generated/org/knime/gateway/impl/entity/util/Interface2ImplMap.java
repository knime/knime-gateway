/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package org.knime.gateway.impl.entity.util;

import org.knime.gateway.api.entity.AnnotationEnt.AnnotationEntBuilder;
import org.knime.gateway.impl.entity.DefaultAnnotationEnt;
import org.knime.gateway.api.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.impl.entity.DefaultBoundsEnt;
import org.knime.gateway.api.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.impl.entity.DefaultConnectionEnt;
import org.knime.gateway.api.entity.DataCellEnt.DataCellEntBuilder;
import org.knime.gateway.impl.entity.DefaultDataCellEnt;
import org.knime.gateway.api.entity.DataRowEnt.DataRowEntBuilder;
import org.knime.gateway.impl.entity.DefaultDataRowEnt;
import org.knime.gateway.api.entity.DataTableEnt.DataTableEntBuilder;
import org.knime.gateway.impl.entity.DefaultDataTableEnt;
import org.knime.gateway.api.entity.ExecutionStatisticsEnt.ExecutionStatisticsEntBuilder;
import org.knime.gateway.impl.entity.DefaultExecutionStatisticsEnt;
import org.knime.gateway.api.entity.FlowVariableEnt.FlowVariableEntBuilder;
import org.knime.gateway.impl.entity.DefaultFlowVariableEnt;
import org.knime.gateway.api.entity.GatewayExceptionEnt.GatewayExceptionEntBuilder;
import org.knime.gateway.impl.entity.DefaultGatewayExceptionEnt;
import org.knime.gateway.api.entity.JavaObjectEnt.JavaObjectEntBuilder;
import org.knime.gateway.impl.entity.DefaultJavaObjectEnt;
import org.knime.gateway.api.entity.JobManagerEnt.JobManagerEntBuilder;
import org.knime.gateway.impl.entity.DefaultJobManagerEnt;
import org.knime.gateway.api.entity.MetaNodeDialogCompEnt.MetaNodeDialogCompEntBuilder;
import org.knime.gateway.impl.entity.DefaultMetaNodeDialogCompEnt;
import org.knime.gateway.api.entity.MetaNodeDialogEnt.MetaNodeDialogEntBuilder;
import org.knime.gateway.impl.entity.DefaultMetaNodeDialogEnt;
import org.knime.gateway.api.entity.MetaPortInfoEnt.MetaPortInfoEntBuilder;
import org.knime.gateway.impl.entity.DefaultMetaPortInfoEnt;
import org.knime.gateway.api.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.impl.entity.DefaultNativeNodeEnt;
import org.knime.gateway.api.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeAnnotationEnt;
import org.knime.gateway.api.entity.NodeEnt.NodeEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeEnt;
import org.knime.gateway.api.entity.NodeExecutedStatisticsEnt.NodeExecutedStatisticsEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeExecutedStatisticsEnt;
import org.knime.gateway.api.entity.NodeExecutingStatisticsEnt.NodeExecutingStatisticsEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeExecutingStatisticsEnt;
import org.knime.gateway.api.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeFactoryKeyEnt;
import org.knime.gateway.api.entity.NodeInPortEnt.NodeInPortEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeInPortEnt;
import org.knime.gateway.api.entity.NodeMessageEnt.NodeMessageEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeMessageEnt;
import org.knime.gateway.api.entity.NodeOutPortEnt.NodeOutPortEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeOutPortEnt;
import org.knime.gateway.api.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodePortEnt;
import org.knime.gateway.api.entity.NodeProgressEnt.NodeProgressEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeProgressEnt;
import org.knime.gateway.api.entity.NodeSettingsEnt.NodeSettingsEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeSettingsEnt;
import org.knime.gateway.api.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeStateEnt;
import org.knime.gateway.api.entity.NodeUIInfoEnt.NodeUIInfoEntBuilder;
import org.knime.gateway.impl.entity.DefaultNodeUIInfoEnt;
import org.knime.gateway.api.entity.PatchEnt.PatchEntBuilder;
import org.knime.gateway.impl.entity.DefaultPatchEnt;
import org.knime.gateway.api.entity.PatchOpEnt.PatchOpEntBuilder;
import org.knime.gateway.impl.entity.DefaultPatchOpEnt;
import org.knime.gateway.api.entity.PortObjectSpecEnt.PortObjectSpecEntBuilder;
import org.knime.gateway.impl.entity.DefaultPortObjectSpecEnt;
import org.knime.gateway.api.entity.PortTypeEnt.PortTypeEntBuilder;
import org.knime.gateway.impl.entity.DefaultPortTypeEnt;
import org.knime.gateway.api.entity.StyleRangeEnt.StyleRangeEntBuilder;
import org.knime.gateway.impl.entity.DefaultStyleRangeEnt;
import org.knime.gateway.api.entity.ViewDataEnt.ViewDataEntBuilder;
import org.knime.gateway.impl.entity.DefaultViewDataEnt;
import org.knime.gateway.api.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import org.knime.gateway.impl.entity.DefaultWorkflowAnnotationEnt;
import org.knime.gateway.api.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.impl.entity.DefaultWorkflowEnt;
import org.knime.gateway.api.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import org.knime.gateway.impl.entity.DefaultWorkflowNodeEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt.WorkflowPartsEntBuilder;
import org.knime.gateway.impl.entity.DefaultWorkflowPartsEnt;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.impl.entity.DefaultWorkflowSnapshotEnt;
import org.knime.gateway.api.entity.WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder;
import org.knime.gateway.impl.entity.DefaultWorkflowUIInfoEnt;
import org.knime.gateway.api.entity.WrappedWorkflowNodeEnt.WrappedWorkflowNodeEntBuilder;
import org.knime.gateway.impl.entity.DefaultWrappedWorkflowNodeEnt;
import org.knime.gateway.api.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.impl.entity.DefaultXYEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;

/**
 * TODO
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/java-ui/configs/com.knime.gateway.impl-config.json"})
public class Interface2ImplMap {

    private Interface2ImplMap() {
        //utility class
    }

    public static GatewayEntityBuilder get(Class<? extends GatewayEntityBuilder> clazz) {
        if(clazz == AnnotationEntBuilder.class) {
            return new DefaultAnnotationEnt.DefaultAnnotationEntBuilder();
        }        
        if(clazz == BoundsEntBuilder.class) {
            return new DefaultBoundsEnt.DefaultBoundsEntBuilder();
        }        
        if(clazz == ConnectionEntBuilder.class) {
            return new DefaultConnectionEnt.DefaultConnectionEntBuilder();
        }        
        if(clazz == DataCellEntBuilder.class) {
            return new DefaultDataCellEnt.DefaultDataCellEntBuilder();
        }        
        if(clazz == DataRowEntBuilder.class) {
            return new DefaultDataRowEnt.DefaultDataRowEntBuilder();
        }        
        if(clazz == DataTableEntBuilder.class) {
            return new DefaultDataTableEnt.DefaultDataTableEntBuilder();
        }        
        if(clazz == ExecutionStatisticsEntBuilder.class) {
            return new DefaultExecutionStatisticsEnt.DefaultExecutionStatisticsEntBuilder();
        }        
        if(clazz == FlowVariableEntBuilder.class) {
            return new DefaultFlowVariableEnt.DefaultFlowVariableEntBuilder();
        }        
        if(clazz == GatewayExceptionEntBuilder.class) {
            return new DefaultGatewayExceptionEnt.DefaultGatewayExceptionEntBuilder();
        }        
        if(clazz == JavaObjectEntBuilder.class) {
            return new DefaultJavaObjectEnt.DefaultJavaObjectEntBuilder();
        }        
        if(clazz == JobManagerEntBuilder.class) {
            return new DefaultJobManagerEnt.DefaultJobManagerEntBuilder();
        }        
        if(clazz == MetaNodeDialogCompEntBuilder.class) {
            return new DefaultMetaNodeDialogCompEnt.DefaultMetaNodeDialogCompEntBuilder();
        }        
        if(clazz == MetaNodeDialogEntBuilder.class) {
            return new DefaultMetaNodeDialogEnt.DefaultMetaNodeDialogEntBuilder();
        }        
        if(clazz == MetaPortInfoEntBuilder.class) {
            return new DefaultMetaPortInfoEnt.DefaultMetaPortInfoEntBuilder();
        }        
        if(clazz == NativeNodeEntBuilder.class) {
            return new DefaultNativeNodeEnt.DefaultNativeNodeEntBuilder();
        }        
        if(clazz == NodeAnnotationEntBuilder.class) {
            return new DefaultNodeAnnotationEnt.DefaultNodeAnnotationEntBuilder();
        }        
        if(clazz == NodeEntBuilder.class) {
            return new DefaultNodeEnt.DefaultNodeEntBuilder();
        }        
        if(clazz == NodeExecutedStatisticsEntBuilder.class) {
            return new DefaultNodeExecutedStatisticsEnt.DefaultNodeExecutedStatisticsEntBuilder();
        }        
        if(clazz == NodeExecutingStatisticsEntBuilder.class) {
            return new DefaultNodeExecutingStatisticsEnt.DefaultNodeExecutingStatisticsEntBuilder();
        }        
        if(clazz == NodeFactoryKeyEntBuilder.class) {
            return new DefaultNodeFactoryKeyEnt.DefaultNodeFactoryKeyEntBuilder();
        }        
        if(clazz == NodeInPortEntBuilder.class) {
            return new DefaultNodeInPortEnt.DefaultNodeInPortEntBuilder();
        }        
        if(clazz == NodeMessageEntBuilder.class) {
            return new DefaultNodeMessageEnt.DefaultNodeMessageEntBuilder();
        }        
        if(clazz == NodeOutPortEntBuilder.class) {
            return new DefaultNodeOutPortEnt.DefaultNodeOutPortEntBuilder();
        }        
        if(clazz == NodePortEntBuilder.class) {
            return new DefaultNodePortEnt.DefaultNodePortEntBuilder();
        }        
        if(clazz == NodeProgressEntBuilder.class) {
            return new DefaultNodeProgressEnt.DefaultNodeProgressEntBuilder();
        }        
        if(clazz == NodeSettingsEntBuilder.class) {
            return new DefaultNodeSettingsEnt.DefaultNodeSettingsEntBuilder();
        }        
        if(clazz == NodeStateEntBuilder.class) {
            return new DefaultNodeStateEnt.DefaultNodeStateEntBuilder();
        }        
        if(clazz == NodeUIInfoEntBuilder.class) {
            return new DefaultNodeUIInfoEnt.DefaultNodeUIInfoEntBuilder();
        }        
        if(clazz == PatchEntBuilder.class) {
            return new DefaultPatchEnt.DefaultPatchEntBuilder();
        }        
        if(clazz == PatchOpEntBuilder.class) {
            return new DefaultPatchOpEnt.DefaultPatchOpEntBuilder();
        }        
        if(clazz == PortObjectSpecEntBuilder.class) {
            return new DefaultPortObjectSpecEnt.DefaultPortObjectSpecEntBuilder();
        }        
        if(clazz == PortTypeEntBuilder.class) {
            return new DefaultPortTypeEnt.DefaultPortTypeEntBuilder();
        }        
        if(clazz == StyleRangeEntBuilder.class) {
            return new DefaultStyleRangeEnt.DefaultStyleRangeEntBuilder();
        }        
        if(clazz == ViewDataEntBuilder.class) {
            return new DefaultViewDataEnt.DefaultViewDataEntBuilder();
        }        
        if(clazz == WorkflowAnnotationEntBuilder.class) {
            return new DefaultWorkflowAnnotationEnt.DefaultWorkflowAnnotationEntBuilder();
        }        
        if(clazz == WorkflowEntBuilder.class) {
            return new DefaultWorkflowEnt.DefaultWorkflowEntBuilder();
        }        
        if(clazz == WorkflowNodeEntBuilder.class) {
            return new DefaultWorkflowNodeEnt.DefaultWorkflowNodeEntBuilder();
        }        
        if(clazz == WorkflowPartsEntBuilder.class) {
            return new DefaultWorkflowPartsEnt.DefaultWorkflowPartsEntBuilder();
        }        
        if(clazz == WorkflowSnapshotEntBuilder.class) {
            return new DefaultWorkflowSnapshotEnt.DefaultWorkflowSnapshotEntBuilder();
        }        
        if(clazz == WorkflowUIInfoEntBuilder.class) {
            return new DefaultWorkflowUIInfoEnt.DefaultWorkflowUIInfoEntBuilder();
        }        
        if(clazz == WrappedWorkflowNodeEntBuilder.class) {
            return new DefaultWrappedWorkflowNodeEnt.DefaultWrappedWorkflowNodeEntBuilder();
        }        
        if(clazz == XYEntBuilder.class) {
            return new DefaultXYEnt.DefaultXYEntBuilder();
        }        
        else {
            throw new IllegalArgumentException("No entity mapping.");
        }    
    }
}
