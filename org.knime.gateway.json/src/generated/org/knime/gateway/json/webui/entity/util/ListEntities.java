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
import org.knime.gateway.json.webui.entity.BoundsEntMixIn;
import org.knime.gateway.json.webui.entity.ComponentNodeEntMixIn;
import org.knime.gateway.json.webui.entity.ConnectionEntMixIn;
import org.knime.gateway.json.webui.entity.GatewayExceptionEntMixIn;
import org.knime.gateway.json.webui.entity.NativeNodeEntMixIn;
import org.knime.gateway.json.webui.entity.NodeAnnotationEntMixIn;
import org.knime.gateway.json.webui.entity.NodeEntMixIn;
import org.knime.gateway.json.webui.entity.NodePortEntMixIn;
import org.knime.gateway.json.webui.entity.WorkflowAnnotationEntMixIn;
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
        res.add(GatewayExceptionEntMixIn.class);
        res.add(NativeNodeEntMixIn.class);
        res.add(NodeAnnotationEntMixIn.class);
        res.add(NodeEntMixIn.class);
        res.add(NodePortEntMixIn.class);
        res.add(WorkflowAnnotationEntMixIn.class);
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
        res.add(GatewayExceptionEntMixIn.GatewayExceptionEntMixInBuilder.class);
        res.add(NativeNodeEntMixIn.NativeNodeEntMixInBuilder.class);
        res.add(NodeAnnotationEntMixIn.NodeAnnotationEntMixInBuilder.class);
        res.add(NodeEntMixIn.NodeEntMixInBuilder.class);
        res.add(NodePortEntMixIn.NodePortEntMixInBuilder.class);
        res.add(WorkflowAnnotationEntMixIn.WorkflowAnnotationEntMixInBuilder.class);
        res.add(WorkflowEntMixIn.WorkflowEntMixInBuilder.class);
        res.add(WorkflowNodeEntMixIn.WorkflowNodeEntMixInBuilder.class);
        res.add(WorkflowSnapshotEntMixIn.WorkflowSnapshotEntMixInBuilder.class);
        res.add(XYEntMixIn.XYEntMixInBuilder.class);
        return res;
    }
}
