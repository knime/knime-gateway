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
package org.knime.gateway.json.webui.entity.util;

import org.knime.gateway.json.webui.entity.AnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentNodeEntMixIn;
import org.knime.gateway.json.webui.entity.ConnectionEntMixIn;
import org.knime.gateway.json.webui.entity.GatewayExceptionEntMixIn;
import org.knime.gateway.json.webui.entity.NativeNodeEntMixIn;
import org.knime.gateway.json.webui.entity.NodeAnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.NodeEntMixIn;
import org.knime.gateway.json.webui.entity.NodeInPortEntMixIn;
import org.knime.gateway.json.webui.entity.NodeMessageEntMixIn;
import org.knime.gateway.json.webui.entity.NodeOutPortEntMixIn;
import org.knime.gateway.json.webui.entity.NodePortEntMixIn;
import org.knime.gateway.json.webui.entity.NodeProgressEntMixIn;
import org.knime.gateway.json.webui.entity.NodeStateEntMixIn;
import org.knime.gateway.json.webui.entity.PortTypeEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowNodeEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowSnapshotEntMixIn;

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
        res.add(ComponentNodeEntMixIn.class);
        res.add(ConnectionEntMixIn.class);
        res.add(GatewayExceptionEntMixIn.class);
        res.add(NativeNodeEntMixIn.class);
        res.add(NodeAnnotationEntMixIn.class);
        res.add(NodeEntMixIn.class);
        res.add(NodeInPortEntMixIn.class);
        res.add(NodeMessageEntMixIn.class);
        res.add(NodeOutPortEntMixIn.class);
        res.add(NodePortEntMixIn.class);
        res.add(NodeProgressEntMixIn.class);
        res.add(NodeStateEntMixIn.class);
        res.add(PortTypeEntMixIn.class);
        res.add(WorkflowEntMixIn.class);
        res.add(WorkflowNodeEntMixIn.class);
        res.add(WorkflowSnapshotEntMixIn.class);
        return res;
    }
    
    /**
     * Lists all gateway entity builder classes of package <code>com.knime.gateway.jsonrpc.entity</code>.
     * @return the class list
     */
    public static List<Class<?>> listEntityBuilderClasses() {
        List<Class<?>> res = new ArrayList<>();
        res.add(AnnotationEntMixIn.AnnotationEntMixInBuilder.class);
        res.add(ComponentNodeEntMixIn.ComponentNodeEntMixInBuilder.class);
        res.add(ConnectionEntMixIn.ConnectionEntMixInBuilder.class);
        res.add(GatewayExceptionEntMixIn.GatewayExceptionEntMixInBuilder.class);
        res.add(NativeNodeEntMixIn.NativeNodeEntMixInBuilder.class);
        res.add(NodeAnnotationEntMixIn.NodeAnnotationEntMixInBuilder.class);
        res.add(NodeEntMixIn.NodeEntMixInBuilder.class);
        res.add(NodeInPortEntMixIn.NodeInPortEntMixInBuilder.class);
        res.add(NodeMessageEntMixIn.NodeMessageEntMixInBuilder.class);
        res.add(NodeOutPortEntMixIn.NodeOutPortEntMixInBuilder.class);
        res.add(NodePortEntMixIn.NodePortEntMixInBuilder.class);
        res.add(NodeProgressEntMixIn.NodeProgressEntMixInBuilder.class);
        res.add(NodeStateEntMixIn.NodeStateEntMixInBuilder.class);
        res.add(PortTypeEntMixIn.PortTypeEntMixInBuilder.class);
        res.add(WorkflowEntMixIn.WorkflowEntMixInBuilder.class);
        res.add(WorkflowNodeEntMixIn.WorkflowNodeEntMixInBuilder.class);
        res.add(WorkflowSnapshotEntMixIn.WorkflowSnapshotEntMixInBuilder.class);
        return res;
    }
}
