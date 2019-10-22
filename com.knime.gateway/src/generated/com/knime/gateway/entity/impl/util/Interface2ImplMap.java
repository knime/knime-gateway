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
package com.knime.gateway.entity.impl.util;

import com.knime.gateway.entity.AnnotationEnt.AnnotationEntBuilder;
import com.knime.gateway.entity.impl.DefaultAnnotationEnt;
import com.knime.gateway.entity.BoundsEnt.BoundsEntBuilder;
import com.knime.gateway.entity.impl.DefaultBoundsEnt;
import com.knime.gateway.entity.ConnectionEnt.ConnectionEntBuilder;
import com.knime.gateway.entity.impl.DefaultConnectionEnt;
import com.knime.gateway.entity.DataCellEnt.DataCellEntBuilder;
import com.knime.gateway.entity.impl.DefaultDataCellEnt;
import com.knime.gateway.entity.DataRowEnt.DataRowEntBuilder;
import com.knime.gateway.entity.impl.DefaultDataRowEnt;
import com.knime.gateway.entity.DataTableEnt.DataTableEntBuilder;
import com.knime.gateway.entity.impl.DefaultDataTableEnt;
import com.knime.gateway.entity.FlowVariableEnt.FlowVariableEntBuilder;
import com.knime.gateway.entity.impl.DefaultFlowVariableEnt;
import com.knime.gateway.entity.GatewayExceptionEnt.GatewayExceptionEntBuilder;
import com.knime.gateway.entity.impl.DefaultGatewayExceptionEnt;
import com.knime.gateway.entity.JavaObjectEnt.JavaObjectEntBuilder;
import com.knime.gateway.entity.impl.DefaultJavaObjectEnt;
import com.knime.gateway.entity.JobManagerEnt.JobManagerEntBuilder;
import com.knime.gateway.entity.impl.DefaultJobManagerEnt;
import com.knime.gateway.entity.MetaNodeDialogCompEnt.MetaNodeDialogCompEntBuilder;
import com.knime.gateway.entity.impl.DefaultMetaNodeDialogCompEnt;
import com.knime.gateway.entity.MetaNodeDialogEnt.MetaNodeDialogEntBuilder;
import com.knime.gateway.entity.impl.DefaultMetaNodeDialogEnt;
import com.knime.gateway.entity.MetaPortInfoEnt.MetaPortInfoEntBuilder;
import com.knime.gateway.entity.impl.DefaultMetaPortInfoEnt;
import com.knime.gateway.entity.NativeNodeEnt.NativeNodeEntBuilder;
import com.knime.gateway.entity.impl.DefaultNativeNodeEnt;
import com.knime.gateway.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodeAnnotationEnt;
import com.knime.gateway.entity.NodeEnt.NodeEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodeEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodeFactoryKeyEnt;
import com.knime.gateway.entity.NodeInPortEnt.NodeInPortEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodeInPortEnt;
import com.knime.gateway.entity.NodeMessageEnt.NodeMessageEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodeMessageEnt;
import com.knime.gateway.entity.NodeOutPortEnt.NodeOutPortEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodeOutPortEnt;
import com.knime.gateway.entity.NodePortEnt.NodePortEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodePortEnt;
import com.knime.gateway.entity.NodeProgressEnt.NodeProgressEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodeProgressEnt;
import com.knime.gateway.entity.NodeSettingsEnt.NodeSettingsEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodeSettingsEnt;
import com.knime.gateway.entity.NodeStateEnt.NodeStateEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodeStateEnt;
import com.knime.gateway.entity.NodeUIInfoEnt.NodeUIInfoEntBuilder;
import com.knime.gateway.entity.impl.DefaultNodeUIInfoEnt;
import com.knime.gateway.entity.PatchEnt.PatchEntBuilder;
import com.knime.gateway.entity.impl.DefaultPatchEnt;
import com.knime.gateway.entity.PatchOpEnt.PatchOpEntBuilder;
import com.knime.gateway.entity.impl.DefaultPatchOpEnt;
import com.knime.gateway.entity.PortObjectSpecEnt.PortObjectSpecEntBuilder;
import com.knime.gateway.entity.impl.DefaultPortObjectSpecEnt;
import com.knime.gateway.entity.PortTypeEnt.PortTypeEntBuilder;
import com.knime.gateway.entity.impl.DefaultPortTypeEnt;
import com.knime.gateway.entity.StyleRangeEnt.StyleRangeEntBuilder;
import com.knime.gateway.entity.impl.DefaultStyleRangeEnt;
import com.knime.gateway.entity.ViewDataEnt.ViewDataEntBuilder;
import com.knime.gateway.entity.impl.DefaultViewDataEnt;
import com.knime.gateway.entity.WizardPageEnt.WizardPageEntBuilder;
import com.knime.gateway.entity.impl.DefaultWizardPageEnt;
import com.knime.gateway.entity.WizardPageInputEnt.WizardPageInputEntBuilder;
import com.knime.gateway.entity.impl.DefaultWizardPageInputEnt;
import com.knime.gateway.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import com.knime.gateway.entity.impl.DefaultWorkflowAnnotationEnt;
import com.knime.gateway.entity.WorkflowEnt.WorkflowEntBuilder;
import com.knime.gateway.entity.impl.DefaultWorkflowEnt;
import com.knime.gateway.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import com.knime.gateway.entity.impl.DefaultWorkflowNodeEnt;
import com.knime.gateway.entity.WorkflowPartsEnt.WorkflowPartsEntBuilder;
import com.knime.gateway.entity.impl.DefaultWorkflowPartsEnt;
import com.knime.gateway.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import com.knime.gateway.entity.impl.DefaultWorkflowSnapshotEnt;
import com.knime.gateway.entity.WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder;
import com.knime.gateway.entity.impl.DefaultWorkflowUIInfoEnt;
import com.knime.gateway.entity.WrappedWorkflowNodeEnt.WrappedWorkflowNodeEntBuilder;
import com.knime.gateway.entity.impl.DefaultWrappedWorkflowNodeEnt;
import com.knime.gateway.entity.XYEnt.XYEntBuilder;
import com.knime.gateway.entity.impl.DefaultXYEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;

/**
 * TODO
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-implementations-config.json"})
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
        if(clazz == WizardPageEntBuilder.class) {
            return new DefaultWizardPageEnt.DefaultWizardPageEntBuilder();
        }        
        if(clazz == WizardPageInputEntBuilder.class) {
            return new DefaultWizardPageInputEnt.DefaultWizardPageInputEntBuilder();
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
