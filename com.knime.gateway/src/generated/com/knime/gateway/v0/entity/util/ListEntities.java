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
package com.knime.gateway.v0.entity.util;

import com.knime.gateway.v0.entity.AnnotationEnt;
import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.DataCellEnt;
import com.knime.gateway.v0.entity.DataRowEnt;
import com.knime.gateway.v0.entity.DataTableEnt;
import com.knime.gateway.v0.entity.FlowVariableEnt;
import com.knime.gateway.v0.entity.JavaObjectEnt;
import com.knime.gateway.v0.entity.JobManagerEnt;
import com.knime.gateway.v0.entity.MetaNodeDialogCompEnt;
import com.knime.gateway.v0.entity.MetaNodeDialogEnt;
import com.knime.gateway.v0.entity.MetaPortInfoEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt;
import com.knime.gateway.v0.entity.NodePortEnt;
import com.knime.gateway.v0.entity.NodeProgressEnt;
import com.knime.gateway.v0.entity.NodeSettingsEnt;
import com.knime.gateway.v0.entity.NodeStateEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.PatchOpEnt;
import com.knime.gateway.v0.entity.PortObjectSpecEnt;
import com.knime.gateway.v0.entity.PortTypeEnt;
import com.knime.gateway.v0.entity.StyleRangeEnt;
import com.knime.gateway.v0.entity.ViewDataEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.entity.WorkflowUIInfoEnt;
import com.knime.gateway.v0.entity.XYEnt;
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import com.knime.gateway.v0.entity.WorkflowNodeEnt;
import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>com.knime.gateway.v0.entity</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class ListEntities {

    private ListEntities() {
        //utility class
    }

    /**
     * Lists all gateway entity classes of package <code>com.knime.gateway.v0.entity</code>.
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
        res.add(FlowVariableEnt.class);
        res.add(JavaObjectEnt.class);
        res.add(JobManagerEnt.class);
        res.add(MetaNodeDialogCompEnt.class);
        res.add(MetaNodeDialogEnt.class);
        res.add(MetaPortInfoEnt.class);
        res.add(NodeEnt.class);
        res.add(NodeFactoryKeyEnt.class);
        res.add(NodeMessageEnt.class);
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
        res.add(WorkflowEnt.class);
        res.add(WorkflowSnapshotEnt.class);
        res.add(WorkflowUIInfoEnt.class);
        res.add(XYEnt.class);
        res.add(NativeNodeEnt.class);
        res.add(NodeAnnotationEnt.class);
        res.add(NodeInPortEnt.class);
        res.add(NodeOutPortEnt.class);
        res.add(WorkflowAnnotationEnt.class);
        res.add(WorkflowNodeEnt.class);
        res.add(WrappedWorkflowNodeEnt.class);
        return res;
    }
    
    /**
     * Lists all gateway entity builder classes of package <code>com.knime.gateway.v0.entity</code>.
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
        res.add(FlowVariableEnt.FlowVariableEntBuilder.class);
        res.add(JavaObjectEnt.JavaObjectEntBuilder.class);
        res.add(JobManagerEnt.JobManagerEntBuilder.class);
        res.add(MetaNodeDialogCompEnt.MetaNodeDialogCompEntBuilder.class);
        res.add(MetaNodeDialogEnt.MetaNodeDialogEntBuilder.class);
        res.add(MetaPortInfoEnt.MetaPortInfoEntBuilder.class);
        res.add(NodeEnt.NodeEntBuilder.class);
        res.add(NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder.class);
        res.add(NodeMessageEnt.NodeMessageEntBuilder.class);
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
        res.add(WorkflowEnt.WorkflowEntBuilder.class);
        res.add(WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder.class);
        res.add(WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder.class);
        res.add(XYEnt.XYEntBuilder.class);
        res.add(NativeNodeEnt.NativeNodeEntBuilder.class);
        res.add(NodeAnnotationEnt.NodeAnnotationEntBuilder.class);
        res.add(NodeInPortEnt.NodeInPortEntBuilder.class);
        res.add(NodeOutPortEnt.NodeOutPortEntBuilder.class);
        res.add(WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder.class);
        res.add(WorkflowNodeEnt.WorkflowNodeEntBuilder.class);
        res.add(WrappedWorkflowNodeEnt.WrappedWorkflowNodeEntBuilder.class);
        return res;
    }
}
