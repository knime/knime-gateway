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
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultBoundsEnt;
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
import org.knime.gateway.api.webui.entity.NodeExecutionStateEnt.NodeExecutionStateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeExecutionStateEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodePortEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt.NodeTemplateEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultNodeTemplateEnt;
import org.knime.gateway.api.webui.entity.StyleRangeEnt.StyleRangeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultStyleRangeEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowNodeEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt.WorkflowSnapshotEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.impl.webui.entity.DefaultXYEnt;

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
        if(clazz == BoundsEntBuilder.class) {
            return (B)new DefaultBoundsEnt.DefaultBoundsEntBuilder();
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
        if(clazz == NodeExecutionStateEntBuilder.class) {
            return (B)new DefaultNodeExecutionStateEnt.DefaultNodeExecutionStateEntBuilder();
        }        
        if(clazz == NodePortEntBuilder.class) {
            return (B)new DefaultNodePortEnt.DefaultNodePortEntBuilder();
        }        
        if(clazz == NodeTemplateEntBuilder.class) {
            return (B)new DefaultNodeTemplateEnt.DefaultNodeTemplateEntBuilder();
        }        
        if(clazz == StyleRangeEntBuilder.class) {
            return (B)new DefaultStyleRangeEnt.DefaultStyleRangeEntBuilder();
        }        
        if(clazz == WorkflowAnnotationEntBuilder.class) {
            return (B)new DefaultWorkflowAnnotationEnt.DefaultWorkflowAnnotationEntBuilder();
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
        if(clazz == XYEntBuilder.class) {
            return (B)new DefaultXYEnt.DefaultXYEntBuilder();
        }        
        else {
            return null;
        }    
    }
}
