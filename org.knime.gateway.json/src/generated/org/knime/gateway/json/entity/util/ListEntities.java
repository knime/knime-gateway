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
