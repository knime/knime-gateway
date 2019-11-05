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
package com.knime.gateway.entity.util;

import com.knime.gateway.entity.AnnotationEnt;
import com.knime.gateway.entity.BoundsEnt;
import com.knime.gateway.entity.ConnectionEnt;
import com.knime.gateway.entity.DataCellEnt;
import com.knime.gateway.entity.DataRowEnt;
import com.knime.gateway.entity.DataTableEnt;
import com.knime.gateway.entity.ExecutionStatisticsEnt;
import com.knime.gateway.entity.FlowVariableEnt;
import com.knime.gateway.entity.GatewayExceptionEnt;
import com.knime.gateway.entity.JavaObjectEnt;
import com.knime.gateway.entity.JobManagerEnt;
import com.knime.gateway.entity.MetaNodeDialogCompEnt;
import com.knime.gateway.entity.MetaNodeDialogEnt;
import com.knime.gateway.entity.MetaPortInfoEnt;
import com.knime.gateway.entity.NativeNodeEnt;
import com.knime.gateway.entity.NodeAnnotationEnt;
import com.knime.gateway.entity.NodeEnt;
import com.knime.gateway.entity.NodeExecutedStatisticsEnt;
import com.knime.gateway.entity.NodeExecutingStatisticsEnt;
import com.knime.gateway.entity.NodeFactoryKeyEnt;
import com.knime.gateway.entity.NodeInPortEnt;
import com.knime.gateway.entity.NodeMessageEnt;
import com.knime.gateway.entity.NodeOutPortEnt;
import com.knime.gateway.entity.NodePortEnt;
import com.knime.gateway.entity.NodeProgressEnt;
import com.knime.gateway.entity.NodeSettingsEnt;
import com.knime.gateway.entity.NodeStateEnt;
import com.knime.gateway.entity.NodeUIInfoEnt;
import com.knime.gateway.entity.PatchEnt;
import com.knime.gateway.entity.PatchOpEnt;
import com.knime.gateway.entity.PortObjectSpecEnt;
import com.knime.gateway.entity.PortTypeEnt;
import com.knime.gateway.entity.StyleRangeEnt;
import com.knime.gateway.entity.ViewDataEnt;
import com.knime.gateway.entity.WizardPageEnt;
import com.knime.gateway.entity.WizardPageInputEnt;
import com.knime.gateway.entity.WorkflowAnnotationEnt;
import com.knime.gateway.entity.WorkflowEnt;
import com.knime.gateway.entity.WorkflowNodeEnt;
import com.knime.gateway.entity.WorkflowPartsEnt;
import com.knime.gateway.entity.WorkflowSnapshotEnt;
import com.knime.gateway.entity.WorkflowUIInfoEnt;
import com.knime.gateway.entity.WrappedWorkflowNodeEnt;
import com.knime.gateway.entity.XYEnt;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>com.knime.gateway.entity</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway-interfaces-config.json"})
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
        res.add(AnnotationEnt.class);
        res.add(BoundsEnt.class);
        res.add(ConnectionEnt.class);
        res.add(DataCellEnt.class);
        res.add(DataRowEnt.class);
        res.add(DataTableEnt.class);
        res.add(ExecutionStatisticsEnt.class);
        res.add(FlowVariableEnt.class);
        res.add(GatewayExceptionEnt.class);
        res.add(JavaObjectEnt.class);
        res.add(JobManagerEnt.class);
        res.add(MetaNodeDialogCompEnt.class);
        res.add(MetaNodeDialogEnt.class);
        res.add(MetaPortInfoEnt.class);
        res.add(NativeNodeEnt.class);
        res.add(NodeAnnotationEnt.class);
        res.add(NodeEnt.class);
        res.add(NodeExecutedStatisticsEnt.class);
        res.add(NodeExecutingStatisticsEnt.class);
        res.add(NodeFactoryKeyEnt.class);
        res.add(NodeInPortEnt.class);
        res.add(NodeMessageEnt.class);
        res.add(NodeOutPortEnt.class);
        res.add(NodePortEnt.class);
        res.add(NodeProgressEnt.class);
        res.add(NodeSettingsEnt.class);
        res.add(NodeStateEnt.class);
        res.add(NodeUIInfoEnt.class);
        res.add(PatchEnt.class);
        res.add(PatchOpEnt.class);
        res.add(PortObjectSpecEnt.class);
        res.add(PortTypeEnt.class);
        res.add(StyleRangeEnt.class);
        res.add(ViewDataEnt.class);
        res.add(WizardPageEnt.class);
        res.add(WizardPageInputEnt.class);
        res.add(WorkflowAnnotationEnt.class);
        res.add(WorkflowEnt.class);
        res.add(WorkflowNodeEnt.class);
        res.add(WorkflowPartsEnt.class);
        res.add(WorkflowSnapshotEnt.class);
        res.add(WorkflowUIInfoEnt.class);
        res.add(WrappedWorkflowNodeEnt.class);
        res.add(XYEnt.class);
        return res;
    }
    
    /**
     * Lists all gateway entity builder classes of package <code>com.knime.gateway.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityBuilderClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AnnotationEnt.AnnotationEntBuilder.class);
        res.add(BoundsEnt.BoundsEntBuilder.class);
        res.add(ConnectionEnt.ConnectionEntBuilder.class);
        res.add(DataCellEnt.DataCellEntBuilder.class);
        res.add(DataRowEnt.DataRowEntBuilder.class);
        res.add(DataTableEnt.DataTableEntBuilder.class);
        res.add(ExecutionStatisticsEnt.ExecutionStatisticsEntBuilder.class);
        res.add(FlowVariableEnt.FlowVariableEntBuilder.class);
        res.add(GatewayExceptionEnt.GatewayExceptionEntBuilder.class);
        res.add(JavaObjectEnt.JavaObjectEntBuilder.class);
        res.add(JobManagerEnt.JobManagerEntBuilder.class);
        res.add(MetaNodeDialogCompEnt.MetaNodeDialogCompEntBuilder.class);
        res.add(MetaNodeDialogEnt.MetaNodeDialogEntBuilder.class);
        res.add(MetaPortInfoEnt.MetaPortInfoEntBuilder.class);
        res.add(NativeNodeEnt.NativeNodeEntBuilder.class);
        res.add(NodeAnnotationEnt.NodeAnnotationEntBuilder.class);
        res.add(NodeEnt.NodeEntBuilder.class);
        res.add(NodeExecutedStatisticsEnt.NodeExecutedStatisticsEntBuilder.class);
        res.add(NodeExecutingStatisticsEnt.NodeExecutingStatisticsEntBuilder.class);
        res.add(NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder.class);
        res.add(NodeInPortEnt.NodeInPortEntBuilder.class);
        res.add(NodeMessageEnt.NodeMessageEntBuilder.class);
        res.add(NodeOutPortEnt.NodeOutPortEntBuilder.class);
        res.add(NodePortEnt.NodePortEntBuilder.class);
        res.add(NodeProgressEnt.NodeProgressEntBuilder.class);
        res.add(NodeSettingsEnt.NodeSettingsEntBuilder.class);
        res.add(NodeStateEnt.NodeStateEntBuilder.class);
        res.add(NodeUIInfoEnt.NodeUIInfoEntBuilder.class);
        res.add(PatchEnt.PatchEntBuilder.class);
        res.add(PatchOpEnt.PatchOpEntBuilder.class);
        res.add(PortObjectSpecEnt.PortObjectSpecEntBuilder.class);
        res.add(PortTypeEnt.PortTypeEntBuilder.class);
        res.add(StyleRangeEnt.StyleRangeEntBuilder.class);
        res.add(ViewDataEnt.ViewDataEntBuilder.class);
        res.add(WizardPageEnt.WizardPageEntBuilder.class);
        res.add(WizardPageInputEnt.WizardPageInputEntBuilder.class);
        res.add(WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder.class);
        res.add(WorkflowEnt.WorkflowEntBuilder.class);
        res.add(WorkflowNodeEnt.WorkflowNodeEntBuilder.class);
        res.add(WorkflowPartsEnt.WorkflowPartsEntBuilder.class);
        res.add(WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder.class);
        res.add(WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder.class);
        res.add(WrappedWorkflowNodeEnt.WrappedWorkflowNodeEntBuilder.class);
        res.add(XYEnt.XYEntBuilder.class);
        return res;
    }
}
