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
package com.knime.gateway.v0.entity.impl;

import static com.knime.gateway.util.DefaultEntUtil.immutable;

import com.knime.gateway.v0.entity.PortTypeEnt;
import com.knime.gateway.v0.entity.impl.DefaultNodeOutPortEnt;

import com.knime.gateway.v0.entity.WorkflowNodeOutPortEnt;

/**
 * The output port of a workflow node.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public class DefaultWorkflowNodeOutPortEnt extends DefaultNodeOutPortEnt implements WorkflowNodeOutPortEnt {

  protected String m_nodeState;
  
  protected DefaultWorkflowNodeOutPortEnt() {
    //for sub-classes
  }
  
  @Override
  public String getTypeID() {
    return "WorkflowNodeOutPort";
  }
  
  private DefaultWorkflowNodeOutPortEnt(DefaultWorkflowNodeOutPortEntBuilder builder) {
    super();
    if(builder.m_type == null) {
        throw new IllegalArgumentException("type must not be null.");
    }
    m_type = immutable(builder.m_type);
    if(builder.m_portIndex == null) {
        throw new IllegalArgumentException("portIndex must not be null.");
    }
    m_portIndex = immutable(builder.m_portIndex);
    if(builder.m_portType == null) {
        throw new IllegalArgumentException("portType must not be null.");
    }
    m_portType = immutable(builder.m_portType);
    m_portName = immutable(builder.m_portName);
    m_inactive = immutable(builder.m_inactive);
    m_nodeState = immutable(builder.m_nodeState);
  }


  @Override
  public String getNodeState() {
        return m_nodeState;
    }
    
  
    public static class DefaultWorkflowNodeOutPortEntBuilder implements WorkflowNodeOutPortEntBuilder {
    
        public DefaultWorkflowNodeOutPortEntBuilder(){
            super();
        }
    
        private String m_type = null;
        private Integer m_portIndex = null;
        private PortTypeEnt m_portType;
        private String m_portName = null;
        private Boolean m_inactive = null;
        private String m_nodeState = null;

        @Override
        public DefaultWorkflowNodeOutPortEntBuilder setType(String type) {
             if(type == null) {
                 throw new IllegalArgumentException("type must not be null.");
             }
             m_type = type;
             return this;
        }

        @Override
        public DefaultWorkflowNodeOutPortEntBuilder setPortIndex(Integer portIndex) {
             if(portIndex == null) {
                 throw new IllegalArgumentException("portIndex must not be null.");
             }
             m_portIndex = portIndex;
             return this;
        }

        @Override
        public DefaultWorkflowNodeOutPortEntBuilder setPortType(PortTypeEnt portType) {
             if(portType == null) {
                 throw new IllegalArgumentException("portType must not be null.");
             }
             m_portType = portType;
             return this;
        }

        @Override
        public DefaultWorkflowNodeOutPortEntBuilder setPortName(String portName) {
             m_portName = portName;
             return this;
        }

        @Override
        public DefaultWorkflowNodeOutPortEntBuilder setInactive(Boolean inactive) {
             m_inactive = inactive;
             return this;
        }

        @Override
        public DefaultWorkflowNodeOutPortEntBuilder setNodeState(String nodeState) {
             m_nodeState = nodeState;
             return this;
        }

        
        @Override
        public DefaultWorkflowNodeOutPortEnt build() {
            return new DefaultWorkflowNodeOutPortEnt(this);
        }
    
    }

}
