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
package org.knime.gateway.json.webui.entity.util;

import org.knime.gateway.json.webui.entity.AnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.BoundsEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentNodeEntMixIn;
import org.knime.gateway.json.webui.entity.ConnectionEntMixIn;
import org.knime.gateway.json.webui.entity.EventEntMixIn;
import org.knime.gateway.json.webui.entity.EventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.GatewayExceptionEntMixIn;
import org.knime.gateway.json.webui.entity.NativeNodeEntMixIn;
import org.knime.gateway.json.webui.entity.NodeAnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.NodeEntMixIn;
import org.knime.gateway.json.webui.entity.NodeExecutionStateEntMixIn;
import org.knime.gateway.json.webui.entity.NodePortEntMixIn;
import org.knime.gateway.json.webui.entity.NodeTemplateEntMixIn;
import org.knime.gateway.json.webui.entity.PatchEntMixIn;
import org.knime.gateway.json.webui.entity.PatchOpEntMixIn;
import org.knime.gateway.json.webui.entity.StyleRangeEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowAnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowChangedEventEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowChangedEventTypeEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowNodeEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowSnapshotEntMixIn;
import org.knime.gateway.json.webui.entity.XYEntMixIn;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>com.knime.gateway.jsonrpc.entity</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.json-config.json"})
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
        res.add(ComponentNodeEntMixIn.class);
        res.add(ConnectionEntMixIn.class);
        res.add(EventEntMixIn.class);
        res.add(EventTypeEntMixIn.class);
        res.add(GatewayExceptionEntMixIn.class);
        res.add(NativeNodeEntMixIn.class);
        res.add(NodeAnnotationEntMixIn.class);
        res.add(NodeEntMixIn.class);
        res.add(NodeExecutionStateEntMixIn.class);
        res.add(NodePortEntMixIn.class);
        res.add(NodeTemplateEntMixIn.class);
        res.add(PatchEntMixIn.class);
        res.add(PatchOpEntMixIn.class);
        res.add(StyleRangeEntMixIn.class);
        res.add(WorkflowAnnotationEntMixIn.class);
        res.add(WorkflowChangedEventEntMixIn.class);
        res.add(WorkflowChangedEventTypeEntMixIn.class);
        res.add(WorkflowEntMixIn.class);
        res.add(WorkflowNodeEntMixIn.class);
        res.add(WorkflowSnapshotEntMixIn.class);
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
        res.add(ComponentNodeEntMixIn.ComponentNodeEntMixInBuilder.class);
        res.add(ConnectionEntMixIn.ConnectionEntMixInBuilder.class);
        res.add(EventEntMixIn.EventEntMixInBuilder.class);
        res.add(EventTypeEntMixIn.EventTypeEntMixInBuilder.class);
        res.add(GatewayExceptionEntMixIn.GatewayExceptionEntMixInBuilder.class);
        res.add(NativeNodeEntMixIn.NativeNodeEntMixInBuilder.class);
        res.add(NodeAnnotationEntMixIn.NodeAnnotationEntMixInBuilder.class);
        res.add(NodeEntMixIn.NodeEntMixInBuilder.class);
        res.add(NodeExecutionStateEntMixIn.NodeExecutionStateEntMixInBuilder.class);
        res.add(NodePortEntMixIn.NodePortEntMixInBuilder.class);
        res.add(NodeTemplateEntMixIn.NodeTemplateEntMixInBuilder.class);
        res.add(PatchEntMixIn.PatchEntMixInBuilder.class);
        res.add(PatchOpEntMixIn.PatchOpEntMixInBuilder.class);
        res.add(StyleRangeEntMixIn.StyleRangeEntMixInBuilder.class);
        res.add(WorkflowAnnotationEntMixIn.WorkflowAnnotationEntMixInBuilder.class);
        res.add(WorkflowChangedEventEntMixIn.WorkflowChangedEventEntMixInBuilder.class);
        res.add(WorkflowChangedEventTypeEntMixIn.WorkflowChangedEventTypeEntMixInBuilder.class);
        res.add(WorkflowEntMixIn.WorkflowEntMixInBuilder.class);
        res.add(WorkflowNodeEntMixIn.WorkflowNodeEntMixInBuilder.class);
        res.add(WorkflowSnapshotEntMixIn.WorkflowSnapshotEntMixInBuilder.class);
        res.add(XYEntMixIn.XYEntMixInBuilder.class);
        return res;
    }
}
