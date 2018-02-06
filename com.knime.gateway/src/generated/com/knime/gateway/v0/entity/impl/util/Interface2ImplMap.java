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
package com.knime.gateway.v0.entity.impl.util;

import com.knime.gateway.v0.entity.AnnotationEnt.AnnotationEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultAnnotationEnt;
import com.knime.gateway.v0.entity.BoundsEnt.BoundsEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultBoundsEnt;
import com.knime.gateway.v0.entity.ConnectionEnt.ConnectionEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultConnectionEnt;
import com.knime.gateway.v0.entity.JobManagerEnt.JobManagerEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultJobManagerEnt;
import com.knime.gateway.v0.entity.MetaPortInfoEnt.MetaPortInfoEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultMetaPortInfoEnt;
import com.knime.gateway.v0.entity.NodeEnt.NodeEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNodeEnt;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNodeFactoryKeyEnt;
import com.knime.gateway.v0.entity.NodeMessageEnt.NodeMessageEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNodeMessageEnt;
import com.knime.gateway.v0.entity.NodePortEnt.NodePortEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNodePortEnt;
import com.knime.gateway.v0.entity.NodeUIInfoEnt.NodeUIInfoEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNodeUIInfoEnt;
import com.knime.gateway.v0.entity.PortTypeEnt.PortTypeEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultPortTypeEnt;
import com.knime.gateway.v0.entity.StyleRangeEnt.StyleRangeEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultStyleRangeEnt;
import com.knime.gateway.v0.entity.WorkflowEnt.WorkflowEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowUIInfoEnt;
import com.knime.gateway.v0.entity.XYEnt.XYEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultXYEnt;
import com.knime.gateway.v0.entity.NativeNodeEnt.NativeNodeEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNativeNodeEnt;
import com.knime.gateway.v0.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNodeAnnotationEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt.NodeInPortEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNodeInPortEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt.NodeOutPortEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultNodeOutPortEnt;
import com.knime.gateway.v0.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowAnnotationEnt;
import com.knime.gateway.v0.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultWorkflowNodeEnt;
import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt.WrappedWorkflowNodeEntBuilder;
import com.knime.gateway.v0.entity.impl.DefaultWrappedWorkflowNodeEnt;

import com.knime.gateway.entity.GatewayEntityBuilder;

/**
 * TODO
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class Interface2ImplMap {

    private Interface2ImplMap() {
        //utility class
    }

    public static GatewayEntityBuilder get(Class<? extends GatewayEntityBuilder> clazz) {
        if(clazz == AnnotationEntBuilder.class) {
            return new DefaultAnnotationEnt.DefaultAnnotationEntBuilder();
        }        
        if(clazz == BoundsEntBuilder.class) {
            return new DefaultBoundsEnt.DefaultBoundsEntBuilder();
        }        
        if(clazz == ConnectionEntBuilder.class) {
            return new DefaultConnectionEnt.DefaultConnectionEntBuilder();
        }        
        if(clazz == JobManagerEntBuilder.class) {
            return new DefaultJobManagerEnt.DefaultJobManagerEntBuilder();
        }        
        if(clazz == MetaPortInfoEntBuilder.class) {
            return new DefaultMetaPortInfoEnt.DefaultMetaPortInfoEntBuilder();
        }        
        if(clazz == NodeEntBuilder.class) {
            return new DefaultNodeEnt.DefaultNodeEntBuilder();
        }        
        if(clazz == NodeFactoryKeyEntBuilder.class) {
            return new DefaultNodeFactoryKeyEnt.DefaultNodeFactoryKeyEntBuilder();
        }        
        if(clazz == NodeMessageEntBuilder.class) {
            return new DefaultNodeMessageEnt.DefaultNodeMessageEntBuilder();
        }        
        if(clazz == NodePortEntBuilder.class) {
            return new DefaultNodePortEnt.DefaultNodePortEntBuilder();
        }        
        if(clazz == NodeUIInfoEntBuilder.class) {
            return new DefaultNodeUIInfoEnt.DefaultNodeUIInfoEntBuilder();
        }        
        if(clazz == PortTypeEntBuilder.class) {
            return new DefaultPortTypeEnt.DefaultPortTypeEntBuilder();
        }        
        if(clazz == StyleRangeEntBuilder.class) {
            return new DefaultStyleRangeEnt.DefaultStyleRangeEntBuilder();
        }        
        if(clazz == WorkflowEntBuilder.class) {
            return new DefaultWorkflowEnt.DefaultWorkflowEntBuilder();
        }        
        if(clazz == WorkflowUIInfoEntBuilder.class) {
            return new DefaultWorkflowUIInfoEnt.DefaultWorkflowUIInfoEntBuilder();
        }        
        if(clazz == XYEntBuilder.class) {
            return new DefaultXYEnt.DefaultXYEntBuilder();
        }        
        if(clazz == NativeNodeEntBuilder.class) {
            return new DefaultNativeNodeEnt.DefaultNativeNodeEntBuilder();
        }        
        if(clazz == NodeAnnotationEntBuilder.class) {
            return new DefaultNodeAnnotationEnt.DefaultNodeAnnotationEntBuilder();
        }        
        if(clazz == NodeInPortEntBuilder.class) {
            return new DefaultNodeInPortEnt.DefaultNodeInPortEntBuilder();
        }        
        if(clazz == NodeOutPortEntBuilder.class) {
            return new DefaultNodeOutPortEnt.DefaultNodeOutPortEntBuilder();
        }        
        if(clazz == WorkflowAnnotationEntBuilder.class) {
            return new DefaultWorkflowAnnotationEnt.DefaultWorkflowAnnotationEntBuilder();
        }        
        if(clazz == WorkflowNodeEntBuilder.class) {
            return new DefaultWorkflowNodeEnt.DefaultWorkflowNodeEntBuilder();
        }        
        if(clazz == WrappedWorkflowNodeEntBuilder.class) {
            return new DefaultWrappedWorkflowNodeEnt.DefaultWrappedWorkflowNodeEntBuilder();
        }        
        else {
            throw new IllegalArgumentException("No entity mapping.");
        }    
    }
}
