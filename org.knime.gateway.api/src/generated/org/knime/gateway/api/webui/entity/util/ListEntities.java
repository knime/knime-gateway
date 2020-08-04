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
package org.knime.gateway.api.webui.entity.util;

import org.knime.gateway.api.webui.entity.AnnotationEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.GatewayExceptionEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.XYEnt;


import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>com.knime.gateway.api.entity</code>.
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
        res.add(AnnotationEnt.class);
        res.add(BoundsEnt.class);
        res.add(ComponentNodeEnt.class);
        res.add(ConnectionEnt.class);
        res.add(GatewayExceptionEnt.class);
        res.add(NativeNodeEnt.class);
        res.add(NodeAnnotationEnt.class);
        res.add(NodeEnt.class);
        res.add(NodePortEnt.class);
        res.add(WorkflowAnnotationEnt.class);
        res.add(WorkflowEnt.class);
        res.add(WorkflowNodeEnt.class);
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
        res.add(AnnotationEnt.AnnotationEntBuilder.class);
        res.add(BoundsEnt.BoundsEntBuilder.class);
        res.add(ComponentNodeEnt.ComponentNodeEntBuilder.class);
        res.add(ConnectionEnt.ConnectionEntBuilder.class);
        res.add(GatewayExceptionEnt.GatewayExceptionEntBuilder.class);
        res.add(NativeNodeEnt.NativeNodeEntBuilder.class);
        res.add(NodeAnnotationEnt.NodeAnnotationEntBuilder.class);
        res.add(NodeEnt.NodeEntBuilder.class);
        res.add(NodePortEnt.NodePortEntBuilder.class);
        res.add(WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder.class);
        res.add(WorkflowEnt.WorkflowEntBuilder.class);
        res.add(WorkflowNodeEnt.WorkflowNodeEntBuilder.class);
        res.add(WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder.class);
        res.add(XYEnt.XYEntBuilder.class);
        return res;
    }
}
