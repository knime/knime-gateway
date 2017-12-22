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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
package org.knime.gateway.v0.entity.impl.util;

import org.knime.gateway.v0.entity.AnnotationEnt.AnnotationEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultAnnotationEnt;
import org.knime.gateway.v0.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultBoundsEnt;
import org.knime.gateway.v0.entity.ConnectionEnt.ConnectionEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultConnectionEnt;
import org.knime.gateway.v0.entity.JobManagerEnt.JobManagerEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultJobManagerEnt;
import org.knime.gateway.v0.entity.MetaPortInfoEnt.MetaPortInfoEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultMetaPortInfoEnt;
import org.knime.gateway.v0.entity.NodeEnt.NodeEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultNodeEnt;
import org.knime.gateway.v0.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultNodeFactoryKeyEnt;
import org.knime.gateway.v0.entity.NodeMessageEnt.NodeMessageEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultNodeMessageEnt;
import org.knime.gateway.v0.entity.NodePortEnt.NodePortEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultNodePortEnt;
import org.knime.gateway.v0.entity.NodeUIInfoEnt.NodeUIInfoEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultNodeUIInfoEnt;
import org.knime.gateway.v0.entity.PortTypeEnt.PortTypeEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultPortTypeEnt;
import org.knime.gateway.v0.entity.StyleRangeEnt.StyleRangeEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultStyleRangeEnt;
import org.knime.gateway.v0.entity.WorkflowEnt.WorkflowEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultWorkflowEnt;
import org.knime.gateway.v0.entity.WorkflowUIInfoEnt.WorkflowUIInfoEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultWorkflowUIInfoEnt;
import org.knime.gateway.v0.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultXYEnt;
import org.knime.gateway.v0.entity.NativeNodeEnt.NativeNodeEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultNativeNodeEnt;
import org.knime.gateway.v0.entity.NodeAnnotationEnt.NodeAnnotationEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultNodeAnnotationEnt;
import org.knime.gateway.v0.entity.NodeInPortEnt.NodeInPortEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultNodeInPortEnt;
import org.knime.gateway.v0.entity.NodeOutPortEnt.NodeOutPortEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultNodeOutPortEnt;
import org.knime.gateway.v0.entity.WorkflowAnnotationEnt.WorkflowAnnotationEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultWorkflowAnnotationEnt;
import org.knime.gateway.v0.entity.WorkflowNodeEnt.WorkflowNodeEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultWorkflowNodeEnt;
import org.knime.gateway.v0.entity.WrappedWorkflowNodeEnt.WrappedWorkflowNodeEntBuilder;
import org.knime.gateway.v0.entity.impl.DefaultWrappedWorkflowNodeEnt;

import org.knime.gateway.entity.GatewayEntityBuilder;

/**
 * TODO
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
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
