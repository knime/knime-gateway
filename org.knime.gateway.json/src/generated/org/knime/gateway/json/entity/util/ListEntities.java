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
package org.knime.gateway.json.entity.util;

import org.knime.gateway.json.entity.AnnotationEntMixIn;
import org.knime.gateway.json.entity.BoundsEntMixIn;
import org.knime.gateway.json.entity.ConnectionEntMixIn;
import org.knime.gateway.json.entity.DataCellEntMixIn;
import org.knime.gateway.json.entity.DataRowEntMixIn;
import org.knime.gateway.json.entity.DataTableEntMixIn;
import org.knime.gateway.json.entity.ExecutionStatisticsEntMixIn;
import org.knime.gateway.json.entity.FlowVariableEntMixIn;
import org.knime.gateway.json.entity.GatewayExceptionEntMixIn;
import org.knime.gateway.json.entity.JavaObjectEntMixIn;
import org.knime.gateway.json.entity.JobManagerEntMixIn;
import org.knime.gateway.json.entity.MetaNodeDialogCompEntMixIn;
import org.knime.gateway.json.entity.MetaNodeDialogEntMixIn;
import org.knime.gateway.json.entity.MetaPortInfoEntMixIn;
import org.knime.gateway.json.entity.NativeNodeEntMixIn;
import org.knime.gateway.json.entity.NodeAnnotationEntMixIn;
import org.knime.gateway.json.entity.NodeEntMixIn;
import org.knime.gateway.json.entity.NodeExecutedStatisticsEntMixIn;
import org.knime.gateway.json.entity.NodeExecutingStatisticsEntMixIn;
import org.knime.gateway.json.entity.NodeFactoryKeyEntMixIn;
import org.knime.gateway.json.entity.NodeInPortEntMixIn;
import org.knime.gateway.json.entity.NodeMessageEntMixIn;
import org.knime.gateway.json.entity.NodeOutPortEntMixIn;
import org.knime.gateway.json.entity.NodePortEntMixIn;
import org.knime.gateway.json.entity.NodeProgressEntMixIn;
import org.knime.gateway.json.entity.NodeSettingsEntMixIn;
import org.knime.gateway.json.entity.NodeStateEntMixIn;
import org.knime.gateway.json.entity.NodeUIInfoEntMixIn;
import org.knime.gateway.json.entity.PatchEntMixIn;
import org.knime.gateway.json.entity.PatchOpEntMixIn;
import org.knime.gateway.json.entity.PortObjectSpecEntMixIn;
import org.knime.gateway.json.entity.PortTypeEntMixIn;
import org.knime.gateway.json.entity.StyleRangeEntMixIn;
import org.knime.gateway.json.entity.ViewDataEntMixIn;
import org.knime.gateway.json.entity.WizardPageEntMixIn;
import org.knime.gateway.json.entity.WizardPageInputEntMixIn;
import org.knime.gateway.json.entity.WorkflowAnnotationEntMixIn;
import org.knime.gateway.json.entity.WorkflowEntMixIn;
import org.knime.gateway.json.entity.WorkflowNodeEntMixIn;
import org.knime.gateway.json.entity.WorkflowPartsEntMixIn;
import org.knime.gateway.json.entity.WorkflowSnapshotEntMixIn;
import org.knime.gateway.json.entity.WorkflowUIInfoEntMixIn;
import org.knime.gateway.json.entity.WrappedWorkflowNodeEntMixIn;
import org.knime.gateway.json.entity.XYEntMixIn;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>com.knime.gateway.jsonrpc.entity</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.json-config.json"})
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
        res.add(AnnotationEntMixIn.class);
        res.add(BoundsEntMixIn.class);
        res.add(ConnectionEntMixIn.class);
        res.add(DataCellEntMixIn.class);
        res.add(DataRowEntMixIn.class);
        res.add(DataTableEntMixIn.class);
        res.add(ExecutionStatisticsEntMixIn.class);
        res.add(FlowVariableEntMixIn.class);
        res.add(GatewayExceptionEntMixIn.class);
        res.add(JavaObjectEntMixIn.class);
        res.add(JobManagerEntMixIn.class);
        res.add(MetaNodeDialogCompEntMixIn.class);
        res.add(MetaNodeDialogEntMixIn.class);
        res.add(MetaPortInfoEntMixIn.class);
        res.add(NativeNodeEntMixIn.class);
        res.add(NodeAnnotationEntMixIn.class);
        res.add(NodeEntMixIn.class);
        res.add(NodeExecutedStatisticsEntMixIn.class);
        res.add(NodeExecutingStatisticsEntMixIn.class);
        res.add(NodeFactoryKeyEntMixIn.class);
        res.add(NodeInPortEntMixIn.class);
        res.add(NodeMessageEntMixIn.class);
        res.add(NodeOutPortEntMixIn.class);
        res.add(NodePortEntMixIn.class);
        res.add(NodeProgressEntMixIn.class);
        res.add(NodeSettingsEntMixIn.class);
        res.add(NodeStateEntMixIn.class);
        res.add(NodeUIInfoEntMixIn.class);
        res.add(PatchEntMixIn.class);
        res.add(PatchOpEntMixIn.class);
        res.add(PortObjectSpecEntMixIn.class);
        res.add(PortTypeEntMixIn.class);
        res.add(StyleRangeEntMixIn.class);
        res.add(ViewDataEntMixIn.class);
        res.add(WizardPageEntMixIn.class);
        res.add(WizardPageInputEntMixIn.class);
        res.add(WorkflowAnnotationEntMixIn.class);
        res.add(WorkflowEntMixIn.class);
        res.add(WorkflowNodeEntMixIn.class);
        res.add(WorkflowPartsEntMixIn.class);
        res.add(WorkflowSnapshotEntMixIn.class);
        res.add(WorkflowUIInfoEntMixIn.class);
        res.add(WrappedWorkflowNodeEntMixIn.class);
        res.add(XYEntMixIn.class);
        return res;
    }
    
    /**
     * Lists all gateway entity builder classes of package <code>com.knime.gateway.jsonrpc.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityBuilderClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AnnotationEntMixIn.AnnotationEntMixInBuilder.class);
        res.add(BoundsEntMixIn.BoundsEntMixInBuilder.class);
        res.add(ConnectionEntMixIn.ConnectionEntMixInBuilder.class);
        res.add(DataCellEntMixIn.DataCellEntMixInBuilder.class);
        res.add(DataRowEntMixIn.DataRowEntMixInBuilder.class);
        res.add(DataTableEntMixIn.DataTableEntMixInBuilder.class);
        res.add(ExecutionStatisticsEntMixIn.ExecutionStatisticsEntMixInBuilder.class);
        res.add(FlowVariableEntMixIn.FlowVariableEntMixInBuilder.class);
        res.add(GatewayExceptionEntMixIn.GatewayExceptionEntMixInBuilder.class);
        res.add(JavaObjectEntMixIn.JavaObjectEntMixInBuilder.class);
        res.add(JobManagerEntMixIn.JobManagerEntMixInBuilder.class);
        res.add(MetaNodeDialogCompEntMixIn.MetaNodeDialogCompEntMixInBuilder.class);
        res.add(MetaNodeDialogEntMixIn.MetaNodeDialogEntMixInBuilder.class);
        res.add(MetaPortInfoEntMixIn.MetaPortInfoEntMixInBuilder.class);
        res.add(NativeNodeEntMixIn.NativeNodeEntMixInBuilder.class);
        res.add(NodeAnnotationEntMixIn.NodeAnnotationEntMixInBuilder.class);
        res.add(NodeEntMixIn.NodeEntMixInBuilder.class);
        res.add(NodeExecutedStatisticsEntMixIn.NodeExecutedStatisticsEntMixInBuilder.class);
        res.add(NodeExecutingStatisticsEntMixIn.NodeExecutingStatisticsEntMixInBuilder.class);
        res.add(NodeFactoryKeyEntMixIn.NodeFactoryKeyEntMixInBuilder.class);
        res.add(NodeInPortEntMixIn.NodeInPortEntMixInBuilder.class);
        res.add(NodeMessageEntMixIn.NodeMessageEntMixInBuilder.class);
        res.add(NodeOutPortEntMixIn.NodeOutPortEntMixInBuilder.class);
        res.add(NodePortEntMixIn.NodePortEntMixInBuilder.class);
        res.add(NodeProgressEntMixIn.NodeProgressEntMixInBuilder.class);
        res.add(NodeSettingsEntMixIn.NodeSettingsEntMixInBuilder.class);
        res.add(NodeStateEntMixIn.NodeStateEntMixInBuilder.class);
        res.add(NodeUIInfoEntMixIn.NodeUIInfoEntMixInBuilder.class);
        res.add(PatchEntMixIn.PatchEntMixInBuilder.class);
        res.add(PatchOpEntMixIn.PatchOpEntMixInBuilder.class);
        res.add(PortObjectSpecEntMixIn.PortObjectSpecEntMixInBuilder.class);
        res.add(PortTypeEntMixIn.PortTypeEntMixInBuilder.class);
        res.add(StyleRangeEntMixIn.StyleRangeEntMixInBuilder.class);
        res.add(ViewDataEntMixIn.ViewDataEntMixInBuilder.class);
        res.add(WizardPageEntMixIn.WizardPageEntMixInBuilder.class);
        res.add(WizardPageInputEntMixIn.WizardPageInputEntMixInBuilder.class);
        res.add(WorkflowAnnotationEntMixIn.WorkflowAnnotationEntMixInBuilder.class);
        res.add(WorkflowEntMixIn.WorkflowEntMixInBuilder.class);
        res.add(WorkflowNodeEntMixIn.WorkflowNodeEntMixInBuilder.class);
        res.add(WorkflowPartsEntMixIn.WorkflowPartsEntMixInBuilder.class);
        res.add(WorkflowSnapshotEntMixIn.WorkflowSnapshotEntMixInBuilder.class);
        res.add(WorkflowUIInfoEntMixIn.WorkflowUIInfoEntMixInBuilder.class);
        res.add(WrappedWorkflowNodeEntMixIn.WrappedWorkflowNodeEntMixInBuilder.class);
        res.add(XYEntMixIn.XYEntMixInBuilder.class);
        return res;
    }
}
