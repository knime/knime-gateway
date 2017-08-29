/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 * ---------------------------------------------------------------------
 *
 */
package org.knime.gateway.v0.workflow.entity;

import org.knime.gateway.workflow.entity.GatewayEntity;
import java.util.List;
import java.util.Optional;

/**
 * A node containing (referencing) a workflow (also referred to it as metanode)
 *
 * @author Martin Horn, University of Konstanz
 */
// AUTO-GENERATED CODE; DO NOT MODIFY
public interface WorkflowNodeEnt extends GatewayEntity, NodeEnt  {

    /**
     * @return List of all incoming workflow ports.
     */
 	List<NodeOutPortEnt> getWorkflowIncomingPorts();
 	
    /**
     * @return List of all outgoing workflow ports.
     */
 	List<NodeInPortEnt> getWorkflowOutgoingPorts();
 	
    /**
     * @return Whether the referenced workflow is encrypted is required to be unlocked before it can be accessed.
     */
 	boolean getIsEncrypted();
 	
    /**
     * @return The parent node id of the node or not present if it's the root node.
     */
 	Optional<String> getParentNodeID();
 	
    /**
     * @return The id of the root workflow this node is contained in or represents.
     */
 	String getRootWorkflowID();
 	
    /**
     * @return The job manager (e.g. cluster or streaming).
     */
 	Optional<JobManagerEnt> getJobManager();
 	
    /**
     * @return The current node message (warning, error, none).
     */
 	NodeMessageEnt getNodeMessage();
 	
    /**
     * @return The list of inputs.
     */
 	List<NodeInPortEnt> getInPorts();
 	
    /**
     * @return The list of outputs.
     */
 	List<NodeOutPortEnt> getOutPorts();
 	
    /**
     * @return The name.
     */
 	String getName();
 	
    /**
     * @return The ID of the node.
     */
 	String getNodeID();
 	
    /**
     * @return The type of the node as string.
     */
 	String getNodeType();
 	
    /**
     * @return The bounds / rectangle on screen of the node.
     */
 	BoundsEnt getBounds();
 	
    /**
     * @return Whether node is deletable.
     */
 	boolean getIsDeletable();
 	
    /**
     * @return The state of the node.
     */
 	String getNodeState();
 	
    /**
     * @return Whether the node has a configuration dialog / user settings.
     */
 	boolean getHasDialog();
 	
    /**
     * @return The annotation underneath the node.
     */
 	NodeAnnotationEnt getNodeAnnotation();
 	
}
