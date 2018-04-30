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
package com.knime.gateway.json.entity.util;

import com.knime.gateway.json.entity.AnnotationEntMixIn;
import com.knime.gateway.json.entity.BoundsEntMixIn;
import com.knime.gateway.json.entity.ConnectionEntMixIn;
import com.knime.gateway.json.entity.JobManagerEntMixIn;
import com.knime.gateway.json.entity.MetaPortInfoEntMixIn;
import com.knime.gateway.json.entity.NodeEntMixIn;
import com.knime.gateway.json.entity.NodeFactoryKeyEntMixIn;
import com.knime.gateway.json.entity.NodeMessageEntMixIn;
import com.knime.gateway.json.entity.NodePortEntMixIn;
import com.knime.gateway.json.entity.NodeStateEntMixIn;
import com.knime.gateway.json.entity.NodeUIInfoEntMixIn;
import com.knime.gateway.json.entity.PatchEntMixIn;
import com.knime.gateway.json.entity.PatchOpEntMixIn;
import com.knime.gateway.json.entity.PortTypeEntMixIn;
import com.knime.gateway.json.entity.StyleRangeEntMixIn;
import com.knime.gateway.json.entity.WorkflowEntMixIn;
import com.knime.gateway.json.entity.WorkflowSnapshotEntMixIn;
import com.knime.gateway.json.entity.WorkflowUIInfoEntMixIn;
import com.knime.gateway.json.entity.XYEntMixIn;
import com.knime.gateway.json.entity.NativeNodeEntMixIn;
import com.knime.gateway.json.entity.NodeAnnotationEntMixIn;
import com.knime.gateway.json.entity.NodeInPortEntMixIn;
import com.knime.gateway.json.entity.NodeOutPortEntMixIn;
import com.knime.gateway.json.entity.WorkflowAnnotationEntMixIn;
import com.knime.gateway.json.entity.WorkflowNodeEntMixIn;
import com.knime.gateway.json.entity.WrappedWorkflowNodeEntMixIn;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway entities of package <code>com.knime.gateway.jsonrpc.entity</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
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
        res.add(JobManagerEntMixIn.class);
        res.add(MetaPortInfoEntMixIn.class);
        res.add(NodeEntMixIn.class);
        res.add(NodeFactoryKeyEntMixIn.class);
        res.add(NodeMessageEntMixIn.class);
        res.add(NodePortEntMixIn.class);
        res.add(NodeStateEntMixIn.class);
        res.add(NodeUIInfoEntMixIn.class);
        res.add(PatchEntMixIn.class);
        res.add(PatchOpEntMixIn.class);
        res.add(PortTypeEntMixIn.class);
        res.add(StyleRangeEntMixIn.class);
        res.add(WorkflowEntMixIn.class);
        res.add(WorkflowSnapshotEntMixIn.class);
        res.add(WorkflowUIInfoEntMixIn.class);
        res.add(XYEntMixIn.class);
        res.add(NativeNodeEntMixIn.class);
        res.add(NodeAnnotationEntMixIn.class);
        res.add(NodeInPortEntMixIn.class);
        res.add(NodeOutPortEntMixIn.class);
        res.add(WorkflowAnnotationEntMixIn.class);
        res.add(WorkflowNodeEntMixIn.class);
        res.add(WrappedWorkflowNodeEntMixIn.class);
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
        res.add(JobManagerEntMixIn.JobManagerEntMixInBuilder.class);
        res.add(MetaPortInfoEntMixIn.MetaPortInfoEntMixInBuilder.class);
        res.add(NodeEntMixIn.NodeEntMixInBuilder.class);
        res.add(NodeFactoryKeyEntMixIn.NodeFactoryKeyEntMixInBuilder.class);
        res.add(NodeMessageEntMixIn.NodeMessageEntMixInBuilder.class);
        res.add(NodePortEntMixIn.NodePortEntMixInBuilder.class);
        res.add(NodeStateEntMixIn.NodeStateEntMixInBuilder.class);
        res.add(NodeUIInfoEntMixIn.NodeUIInfoEntMixInBuilder.class);
        res.add(PatchEntMixIn.PatchEntMixInBuilder.class);
        res.add(PatchOpEntMixIn.PatchOpEntMixInBuilder.class);
        res.add(PortTypeEntMixIn.PortTypeEntMixInBuilder.class);
        res.add(StyleRangeEntMixIn.StyleRangeEntMixInBuilder.class);
        res.add(WorkflowEntMixIn.WorkflowEntMixInBuilder.class);
        res.add(WorkflowSnapshotEntMixIn.WorkflowSnapshotEntMixInBuilder.class);
        res.add(WorkflowUIInfoEntMixIn.WorkflowUIInfoEntMixInBuilder.class);
        res.add(XYEntMixIn.XYEntMixInBuilder.class);
        res.add(NativeNodeEntMixIn.NativeNodeEntMixInBuilder.class);
        res.add(NodeAnnotationEntMixIn.NodeAnnotationEntMixInBuilder.class);
        res.add(NodeInPortEntMixIn.NodeInPortEntMixInBuilder.class);
        res.add(NodeOutPortEntMixIn.NodeOutPortEntMixInBuilder.class);
        res.add(WorkflowAnnotationEntMixIn.WorkflowAnnotationEntMixInBuilder.class);
        res.add(WorkflowNodeEntMixIn.WorkflowNodeEntMixInBuilder.class);
        res.add(WrappedWorkflowNodeEntMixIn.WrappedWorkflowNodeEntMixInBuilder.class);
        return res;
    }
}
