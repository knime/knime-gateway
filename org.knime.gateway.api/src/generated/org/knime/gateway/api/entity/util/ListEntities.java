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
package org.knime.gateway.api.entity.util;

import org.knime.gateway.api.entity.AnnotationEnt;
import org.knime.gateway.api.entity.BoundsEnt;
import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.DataCellEnt;
import org.knime.gateway.api.entity.DataRowEnt;
import org.knime.gateway.api.entity.DataTableEnt;
import org.knime.gateway.api.entity.ExecutionStatisticsEnt;
import org.knime.gateway.api.entity.FlowVariableEnt;
import org.knime.gateway.api.entity.GatewayExceptionEnt;
import org.knime.gateway.api.entity.JavaObjectEnt;
import org.knime.gateway.api.entity.JobManagerEnt;
import org.knime.gateway.api.entity.MetaNodeDialogCompEnt;
import org.knime.gateway.api.entity.MetaNodeDialogEnt;
import org.knime.gateway.api.entity.MetaPortInfoEnt;
import org.knime.gateway.api.entity.NativeNodeEnt;
import org.knime.gateway.api.entity.NodeAnnotationEnt;
import org.knime.gateway.api.entity.NodeEnt;
import org.knime.gateway.api.entity.NodeExecutedStatisticsEnt;
import org.knime.gateway.api.entity.NodeExecutingStatisticsEnt;
import org.knime.gateway.api.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.entity.NodeInPortEnt;
import org.knime.gateway.api.entity.NodeMessageEnt;
import org.knime.gateway.api.entity.NodeOutPortEnt;
import org.knime.gateway.api.entity.NodePortEnt;
import org.knime.gateway.api.entity.NodeProgressEnt;
import org.knime.gateway.api.entity.NodeSettingsEnt;
import org.knime.gateway.api.entity.NodeStateEnt;
import org.knime.gateway.api.entity.NodeUIInfoEnt;
import org.knime.gateway.api.entity.PatchEnt;
import org.knime.gateway.api.entity.PatchOpEnt;
import org.knime.gateway.api.entity.PortObjectSpecEnt;
import org.knime.gateway.api.entity.PortTypeEnt;
import org.knime.gateway.api.entity.StyleRangeEnt;
import org.knime.gateway.api.entity.ViewDataEnt;
import org.knime.gateway.api.entity.WizardPageEnt;
import org.knime.gateway.api.entity.WizardPageInputEnt;
import org.knime.gateway.api.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.entity.WorkflowEnt;
import org.knime.gateway.api.entity.WorkflowNodeEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt;
import org.knime.gateway.api.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.entity.WorkflowUIInfoEnt;
import org.knime.gateway.api.entity.WrappedWorkflowNodeEnt;
import org.knime.gateway.api.entity.XYEnt;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>com.knime.gateway.entity</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
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
