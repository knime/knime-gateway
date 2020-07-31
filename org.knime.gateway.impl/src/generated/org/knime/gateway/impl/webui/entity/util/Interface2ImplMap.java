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
package org.knime.gateway.impl.webui.entity.util;

import org.knime.gateway.api.webui.entity.AnnotationEnt.AnnotationEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultAnnotationEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt.ComponentNodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultConnectionEnt;
import org.knime.gateway.api.webui.entity.GatewayExceptionEnt.GatewayExceptionEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultGatewayExceptionEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeEnt.NodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeEnt;
import org.knime.gateway.api.webui.entity.NodeInPortEnt.NodeInPortEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeInPortEnt;
import org.knime.gateway.api.webui.entity.NodeMessageEnt.NodeMessageEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeMessageEnt;
import org.knime.gateway.api.webui.entity.NodeOutPortEnt.NodeOutPortEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeOutPortEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodePortEnt;
import org.knime.gateway.api.webui.entity.NodeProgressEnt.NodeProgressEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeProgressEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.NodeStateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeStateEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt.PortTypeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultPortTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowNodeEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowSnapshotEnt;

import org.knime.gateway.api.entity.GatewayEntityBuilder;

/**
 * Helper to create entity-builder instances.

 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public class Interface2ImplMap {

    private Interface2ImplMap() {
        //utility class
    }

    /**
     * Creates an entity-builder instance from the given interface class.
     * @param clazz
     * @return the builder instance or null if there is not match
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <B extends GatewayEntityBuilder> B create(final Class<B> clazz) {
        if(clazz == AnnotationEntBuilder.class) {
            return (B)new DefaultAnnotationEnt.DefaultAnnotationEntBuilder();
        }        
        if(clazz == ComponentNodeEntBuilder.class) {
            return (B)new DefaultComponentNodeEnt.DefaultComponentNodeEntBuilder();
        }        
        if(clazz == ConnectionEntBuilder.class) {
            return (B)new DefaultConnectionEnt.DefaultConnectionEntBuilder();
        }        
        if(clazz == GatewayExceptionEntBuilder.class) {
            return (B)new DefaultGatewayExceptionEnt.DefaultGatewayExceptionEntBuilder();
        }        
        if(clazz == NativeNodeEntBuilder.class) {
            return (B)new DefaultNativeNodeEnt.DefaultNativeNodeEntBuilder();
        }        
        if(clazz == NodeAnnotationEntBuilder.class) {
            return (B)new DefaultNodeAnnotationEnt.DefaultNodeAnnotationEntBuilder();
        }        
        if(clazz == NodeEntBuilder.class) {
            return (B)new DefaultNodeEnt.DefaultNodeEntBuilder();
        }        
        if(clazz == NodeInPortEntBuilder.class) {
            return (B)new DefaultNodeInPortEnt.DefaultNodeInPortEntBuilder();
        }        
        if(clazz == NodeMessageEntBuilder.class) {
            return (B)new DefaultNodeMessageEnt.DefaultNodeMessageEntBuilder();
        }        
        if(clazz == NodeOutPortEntBuilder.class) {
            return (B)new DefaultNodeOutPortEnt.DefaultNodeOutPortEntBuilder();
        }        
        if(clazz == NodePortEntBuilder.class) {
            return (B)new DefaultNodePortEnt.DefaultNodePortEntBuilder();
        }        
        if(clazz == NodeProgressEntBuilder.class) {
            return (B)new DefaultNodeProgressEnt.DefaultNodeProgressEntBuilder();
        }        
        if(clazz == NodeStateEntBuilder.class) {
            return (B)new DefaultNodeStateEnt.DefaultNodeStateEntBuilder();
        }        
        if(clazz == PortTypeEntBuilder.class) {
            return (B)new DefaultPortTypeEnt.DefaultPortTypeEntBuilder();
        }        
        if(clazz == WorkflowEntBuilder.class) {
            return (B)new DefaultWorkflowEnt.DefaultWorkflowEntBuilder();
        }        
        if(clazz == WorkflowNodeEntBuilder.class) {
            return (B)new DefaultWorkflowNodeEnt.DefaultWorkflowNodeEntBuilder();
        }        
        if(clazz == WorkflowSnapshotEntBuilder.class) {
            return (B)new DefaultWorkflowSnapshotEnt.DefaultWorkflowSnapshotEntBuilder();
        }        
        else {
            return null;
        }    
    }
}
