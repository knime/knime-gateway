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
package org.knime.gateway.v0.entity;

import org.knime.gateway.v0.entity.JobManagerEnt;
import org.knime.gateway.v0.entity.NodeAnnotationEnt;
import org.knime.gateway.v0.entity.NodeInPortEnt;
import org.knime.gateway.v0.entity.NodeMessageEnt;
import org.knime.gateway.v0.entity.NodeOutPortEnt;
import org.knime.gateway.v0.entity.NodeUIInfoEnt;

import org.knime.gateway.entity.GatewayEntityBuilder;


import org.knime.gateway.entity.GatewayEntity;

/**
 * A node.
 * 
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public interface NodeEnt extends GatewayEntity {

  /**
   * The type of the node.
   */
  public enum NodeTypeEnum {
    SOURCE("Source"),
    
    SINK("Sink"),
    
    LEARNER("Learner"),
    
    PREDICTOR("Predictor"),
    
    MANIPULATOR("Manipulator"),
    
    VISUALIZER("Visualizer"),
    
    META("Meta"),
    
    LOOPSTART("LoopStart"),
    
    LOOPEND("LoopEnd"),
    
    SCOPESTART("ScopeStart"),
    
    SCOPEEND("ScopeEnd"),
    
    QUICKFORM("QuickForm"),
    
    OTHER("Other"),
    
    MISSING("Missing"),
    
    UNKNOWN("Unknown"),
    
    SUBNODE("Subnode"),
    
    VIRTUALIN("VirtualIn"),
    
    VIRTUALOUT("VirtualOut");

    private String value;

    NodeTypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }

  /**
   * The state of the node.
   */
  public enum NodeStateEnum {
    IDLE("IDLE"),
    
    CONFIGURED("CONFIGURED"),
    
    UNCONFIGURED_MARKEDFOREXEC("UNCONFIGURED_MARKEDFOREXEC"),
    
    CONFIGURED_MARKEDFOREXEC("CONFIGURED_MARKEDFOREXEC"),
    
    EXECUTED_MARKEDFOREXEC("EXECUTED_MARKEDFOREXEC"),
    
    CONFIGURED_QUEUED("CONFIGURED_QUEUED"),
    
    EXECUTED_QUEUED("EXECUTED_QUEUED"),
    
    PREEXECUTE("PREEXECUTE"),
    
    EXECUTING("EXECUTING"),
    
    EXECUTINGREMOTELY("EXECUTINGREMOTELY"),
    
    POSTEXECUTE("POSTEXECUTE"),
    
    EXECUTED("EXECUTED");

    private String value;

    NodeStateEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * Discriminator for inheritance.
   * @return type
   **/
  public String getType();
  /**
   * The node&#39;s name.
   * @return name
   **/
  public String getName();
  /**
   * The ID of the node.
   * @return nodeID
   **/
  public String getNodeID();
  /**
   * The type of the node.
   * @return nodeType
   **/
  public NodeTypeEnum getNodeType();
  /**
   * The parent node id of the node or not present if it&#39;s the root node.
   * @return parentNodeID
   **/
  public String getParentNodeID();
  /**
   * The id of the root workflow this node is contained in or represents.
   * @return rootWorkflowID
   **/
  public String getRootWorkflowID();
  /**
   * The current node message (warning, error, none).
   * @return nodeMessage
   **/
  public NodeMessageEnt getNodeMessage();
  /**
   * The state of the node.
   * @return nodeState
   **/
  public NodeStateEnum getNodeState();
  /**
   * The list of inputs.
   * @return inPorts
   **/
  public java.util.List<NodeInPortEnt> getInPorts();
  /**
   * The list of outputs.
   * @return outPorts
   **/
  public java.util.List<NodeOutPortEnt> getOutPorts();
  /**
   * Whether the node is deletable.
   * @return deletable
   **/
  public Boolean isDeletable();
  /**
   * Whether the node has a configuration dialog / user settings.
   * @return hasDialog
   **/
  public Boolean isHasDialog();
  /**
   * The annotation below the node.
   * @return nodeAnnotation
   **/
  public NodeAnnotationEnt getNodeAnnotation();
  /**
   * The job manager (e.g. cluster or streaming).
   * @return jobManager
   **/
  public JobManagerEnt getJobManager();
  /**
   * Get uIInfo
   * @return uIInfo
   **/
  public NodeUIInfoEnt getUIInfo();

    /**
     * The builder for the entity.
     */
    public interface NodeEntBuilder extends GatewayEntityBuilder<NodeEnt> {

        NodeEntBuilder setType(String type);
        NodeEntBuilder setName(String name);
        NodeEntBuilder setNodeID(String nodeID);
        NodeEntBuilder setNodeType(NodeTypeEnum nodeType);
        NodeEntBuilder setParentNodeID(String parentNodeID);
        NodeEntBuilder setRootWorkflowID(String rootWorkflowID);
        NodeEntBuilder setNodeMessage(NodeMessageEnt nodeMessage);
        NodeEntBuilder setNodeState(NodeStateEnum nodeState);
        NodeEntBuilder setInPorts(java.util.List<NodeInPortEnt> inPorts);
        NodeEntBuilder setOutPorts(java.util.List<NodeOutPortEnt> outPorts);
        NodeEntBuilder setDeletable(Boolean deletable);
        NodeEntBuilder setHasDialog(Boolean hasDialog);
        NodeEntBuilder setNodeAnnotation(NodeAnnotationEnt nodeAnnotation);
        NodeEntBuilder setJobManager(JobManagerEnt jobManager);
        NodeEntBuilder setUIInfo(NodeUIInfoEnt uIInfo);
        
        NodeEnt build();
    
    }

}
