/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 * History
 *   Nov 11, 2016 (hornm): created
 */
package com.knime.gateway.remote.service;

import static com.knime.gateway.remote.util.EntityBuilderUtil.buildWorkflowEnt;

import java.util.NoSuchElementException;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.v0.entity.WorkflowEnt;
import org.knime.gateway.v0.service.WorkflowService;

import com.knime.gateway.remote.endpoint.WorkflowProjectManager;
import com.knime.gateway.remote.util.EntityBuilderUtil;

/**
 * Default implementation of {@link WorkflowService} that delegates the operations to knime.core (e.g.
 * {@link WorkflowManager} etc.).

 * @author Martin Horn, University of Konstanz
 */
public class DefaultWorkflowService implements WorkflowService {

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowEnt getWorkflow(final String rootWorkflowID, final String nodeID) {
        //get the right IWorkflowManager for the given id and create a WorkflowEnt from it
        if (nodeID != null) {
            WorkflowManager rootWfm = WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID)
            .orElseThrow(
                () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
            NodeContainer metaNode =
                rootWfm.findNodeContainer(NodeIDSuffix.fromString(nodeID).prependParent(rootWfm.getID()));
            if(metaNode instanceof WorkflowManager) {
                WorkflowManager wfm = (WorkflowManager)metaNode;
                if (wfm.isEncrypted()) {
                    throw new IllegalStateException("Workflow is encrypted and cannot be accessed.");
                }
                return buildWorkflowEnt(wfm, rootWorkflowID);
            } else if(metaNode instanceof SubNodeContainer) {
                SubNodeContainer snc = (SubNodeContainer)metaNode;
                return EntityBuilderUtil.buildWorkflowEnt(snc.getWorkflowManager(), rootWorkflowID);
            } else {
                throw new IllegalArgumentException("Node for the given node id ('" + nodeID.toString() + "') is neither a metanode nor a wrapped metanode.");
            }
        } else {
            WorkflowManager wfm =
                WorkflowProjectManager.openAndCacheWorkflow(rootWorkflowID).orElseThrow(
                    () -> new NoSuchElementException("Workflow project for ID \"" + rootWorkflowID + "\" not found."));
            if (wfm.isEncrypted()) {
                throw new IllegalStateException("Workflow is encrypted and cannot be accessed.");
            }
            return buildWorkflowEnt(wfm, rootWorkflowID);
        }
    }
}
