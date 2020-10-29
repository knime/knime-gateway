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

import org.knime.gateway.api.webui.entity.AllowedActionsEnt;
import org.knime.gateway.api.webui.entity.AnnotationEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeAndTemplateEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeTemplateEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.EventEnt;
import org.knime.gateway.api.webui.entity.EventTypeEnt;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.MetaNodeEnt;
import org.knime.gateway.api.webui.entity.MetaNodePortEnt;
import org.knime.gateway.api.webui.entity.MetaNodeStateEnt;
import org.knime.gateway.api.webui.entity.MetaPortsEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptionsEnt;
import org.knime.gateway.api.webui.entity.NodeDialogOptions_fieldsEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodePortAndTemplateEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeViewDescriptionEnt;
import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt;
import org.knime.gateway.api.webui.entity.PortViewEnt;
import org.knime.gateway.api.webui.entity.ProjectMetadataEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowInfoEnt;
import org.knime.gateway.api.webui.entity.WorkflowProjectEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.XYEnt;


import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>org.knime.gateway.api.entity</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
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
        res.add(AllowedActionsEnt.class);
        res.add(AnnotationEnt.class);
        res.add(AppStateEnt.class);
        res.add(BoundsEnt.class);
        res.add(ComponentNodeAndTemplateEnt.class);
        res.add(ComponentNodeEnt.class);
        res.add(ComponentNodeTemplateEnt.class);
        res.add(ConnectionEnt.class);
        res.add(EventEnt.class);
        res.add(EventTypeEnt.class);
        res.add(LinkEnt.class);
        res.add(MetaNodeEnt.class);
        res.add(MetaNodePortEnt.class);
        res.add(MetaNodeStateEnt.class);
        res.add(MetaPortsEnt.class);
        res.add(NativeNodeEnt.class);
        res.add(NativeNodeTemplateEnt.class);
        res.add(NodeAnnotationEnt.class);
        res.add(NodeDialogOptionsEnt.class);
        res.add(NodeDialogOptions_fieldsEnt.class);
        res.add(NodeEnt.class);
        res.add(NodePortAndTemplateEnt.class);
        res.add(NodePortEnt.class);
        res.add(NodePortTemplateEnt.class);
        res.add(NodeStateEnt.class);
        res.add(NodeViewDescriptionEnt.class);
        res.add(PatchEnt.class);
        res.add(PatchOpEnt.class);
        res.add(PortViewEnt.class);
        res.add(ProjectMetadataEnt.class);
        res.add(StyleRangeEnt.class);
        res.add(WorkflowAnnotationEnt.class);
        res.add(WorkflowChangedEventEnt.class);
        res.add(WorkflowChangedEventTypeEnt.class);
        res.add(WorkflowEnt.class);
        res.add(WorkflowInfoEnt.class);
        res.add(WorkflowProjectEnt.class);
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
        res.add(AllowedActionsEnt.AllowedActionsEntBuilder.class);
        res.add(AnnotationEnt.AnnotationEntBuilder.class);
        res.add(AppStateEnt.AppStateEntBuilder.class);
        res.add(BoundsEnt.BoundsEntBuilder.class);
        res.add(ComponentNodeAndTemplateEnt.ComponentNodeAndTemplateEntBuilder.class);
        res.add(ComponentNodeEnt.ComponentNodeEntBuilder.class);
        res.add(ComponentNodeTemplateEnt.ComponentNodeTemplateEntBuilder.class);
        res.add(ConnectionEnt.ConnectionEntBuilder.class);
        res.add(EventEnt.EventEntBuilder.class);
        res.add(EventTypeEnt.EventTypeEntBuilder.class);
        res.add(LinkEnt.LinkEntBuilder.class);
        res.add(MetaNodeEnt.MetaNodeEntBuilder.class);
        res.add(MetaNodePortEnt.MetaNodePortEntBuilder.class);
        res.add(MetaNodeStateEnt.MetaNodeStateEntBuilder.class);
        res.add(MetaPortsEnt.MetaPortsEntBuilder.class);
        res.add(NativeNodeEnt.NativeNodeEntBuilder.class);
        res.add(NativeNodeTemplateEnt.NativeNodeTemplateEntBuilder.class);
        res.add(NodeAnnotationEnt.NodeAnnotationEntBuilder.class);
        res.add(NodeDialogOptionsEnt.NodeDialogOptionsEntBuilder.class);
        res.add(NodeDialogOptions_fieldsEnt.NodeDialogOptions_fieldsEntBuilder.class);
        res.add(NodeEnt.NodeEntBuilder.class);
        res.add(NodePortAndTemplateEnt.NodePortAndTemplateEntBuilder.class);
        res.add(NodePortEnt.NodePortEntBuilder.class);
        res.add(NodePortTemplateEnt.NodePortTemplateEntBuilder.class);
        res.add(NodeStateEnt.NodeStateEntBuilder.class);
        res.add(NodeViewDescriptionEnt.NodeViewDescriptionEntBuilder.class);
        res.add(PatchEnt.PatchEntBuilder.class);
        res.add(PatchOpEnt.PatchOpEntBuilder.class);
        res.add(PortViewEnt.PortViewEntBuilder.class);
        res.add(ProjectMetadataEnt.ProjectMetadataEntBuilder.class);
        res.add(StyleRangeEnt.StyleRangeEntBuilder.class);
        res.add(WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder.class);
        res.add(WorkflowChangedEventEnt.WorkflowChangedEventEntBuilder.class);
        res.add(WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder.class);
        res.add(WorkflowEnt.WorkflowEntBuilder.class);
        res.add(WorkflowInfoEnt.WorkflowInfoEntBuilder.class);
        res.add(WorkflowProjectEnt.WorkflowProjectEntBuilder.class);
        res.add(WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder.class);
        res.add(XYEnt.XYEntBuilder.class);
        return res;
    }
}
