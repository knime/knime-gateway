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
 *   Aug 17, 2022 (Kai Franze, KNIME GmbH): created
 */
package org.knime.gateway.impl.webui;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.NodeInfo;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.ui.util.NodeTemplateId;
import org.knime.core.ui.workflowcoach.NodeRecommendationManager;
import org.knime.core.ui.workflowcoach.NodeRecommendationManager.NodeRecommendation;
import org.knime.core.ui.wrapper.NativeNodeContainerWrapper;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.webui.NodeRepository.Node;

/**
 * Logic to retrieve node recommendations, we might need the {@link NodeRepository} for it.
 *
 * @author Kai Franze, KNIME GmbH
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class NodeRecommendations {

    private static final int DEFAULT_NODES_LIMIT = 12;

    private static final boolean DEFAULT_FULL_TEMPLATE_INFO = false;

    private final NodeRepository m_nodeRepo;

    private boolean m_nodeRecommendationManagerIsInitialized = false;

    /**
     * Creates a new instance
     *
     * @param nodeRepo The node repository to retrieve the suggested nodes
     */
    public NodeRecommendations(final NodeRepository nodeRepo) {
        m_nodeRepo = nodeRepo;
    }

    /**
     * Gathers node recommendations and compiles the result accordingly.
     *
     * @param projectId The project of your node
     * @param workflowId The workflow of your node
     * @param nodeId The id of your node
     * @param portIdx The index of your port
     * @param nodesLimit The maximum number of node recommendations to return, 12 by default
     * @param fullTemplateInfo Whether to return complete result or not, true by default
     * @return The node recommendations
     * @throws OperationNotAllowedException
     */
    public List<NodeTemplateEnt> getNodeRecommendations(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId, final Integer portIdx, final Integer nodesLimit, final Boolean fullTemplateInfo) throws OperationNotAllowedException {
        if (!m_nodeRecommendationManagerIsInitialized) {
            m_nodeRecommendationManagerIsInitialized = initializeNodeRecommendationManager(m_nodeRepo);
        }
        if (!m_nodeRecommendationManagerIsInitialized) {
            throw new OperationNotAllowedException("Node recommendation manager was not initialized properly");
        }
        if (nodeId == null ^ portIdx == null) {
            throw new OperationNotAllowedException("<nodeId> and <portIdx> must either be both null or not null");
        }
        var limit = nodesLimit == null ? DEFAULT_NODES_LIMIT : nodesLimit;
        var fullInfo = fullTemplateInfo == null ? DEFAULT_FULL_TEMPLATE_INFO : fullTemplateInfo;

        // This `null` is evaluated in `NodeRecommandationManager#getNodeRecommendationFor(...)`
        var nnc = nodeId == null ? null : getNativeNodeContainer(projectId, workflowId, nodeId);

        // This `null` is evaluated in `NodeRecommendations#getNodeTemplatesAndFilterByPortType(...)`
        var sourcePortType = nodeId == null ? null : determineSourcePortType(nnc, portIdx);

        var recommendations = getFlatListOfRecommendations(nnc);
        return getNodeTemplatesAndFilter(recommendations, sourcePortType, limit, fullInfo);
    }

    /**
     * Initializes the {@link NodeRecommendationManager} using the {@link NodeRepository} to build the predicates needed
     *
     * @param nodeRepo The node repository
     * @return Exit status of initialization
     */
    private static boolean initializeNodeRecommendationManager(final NodeRepository nodeRepo) {
        Predicate<NodeInfo> isSourceNode = nodeInfo -> {
            var node = NodeTemplateId.callWithNodeTemplateIdVariants(nodeInfo.getFactory(), nodeInfo.getName(),
                nodeRepo::getNodeIncludeAdditionalNodes, true);
            return node != null && node.factory.getType() == NodeType.Source;
        };
        Predicate<NodeInfo> existsInRepository = nodeInfo -> {
            var node = NodeTemplateId.callWithNodeTemplateIdVariants(nodeInfo.getFactory(), nodeInfo.getName(),
                nodeRepo::getNodeIncludeAdditionalNodes, true);
            return node != null;
        };
        return NodeRecommendationManager.getInstance().initialize(isSourceNode, existsInRepository);
    }

    private static NativeNodeContainer getNativeNodeContainer(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId) throws OperationNotAllowedException {
        var nc = DefaultServiceUtil.getNodeContainer(projectId, workflowId, nodeId);
        if (nc instanceof NativeNodeContainer) {
            return (NativeNodeContainer)nc;
        } else {
            throw new OperationNotAllowedException(
                "Node recommendations for metanodes or components aren't supported yet");
        }
    }

    private static PortType determineSourcePortType(final NativeNodeContainer nnc, final Integer portIdx)
        throws OperationNotAllowedException {
        if (portIdx + 1 > nnc.getNrOutPorts() || portIdx < 0) {
            throw new OperationNotAllowedException("Cannot recommend nodes for non-existing port");
        }
        return nnc.getOutPort(portIdx).getPortType();
    }

    private static List<NodeRecommendation> getFlatListOfRecommendations(final NativeNodeContainer nnc) {
        var recommendations = nnc == null ? NodeRecommendationManager.getInstance().getNodeRecommendationFor()
            : NodeRecommendationManager.getInstance().getNodeRecommendationFor(NativeNodeContainerWrapper.wrap(nnc));
        var recommendationsWithoutDups =
            NodeRecommendationManager.joinRecommendationsWithoutDuplications(recommendations);
        return recommendationsWithoutDups.stream().map(ObjectUtils::firstNonNull).collect(Collectors.toList());
    }

    private List<NodeTemplateEnt> getNodeTemplatesAndFilter(final List<NodeRecommendation> recommendations,
        final PortType sourcePortType, final int limit, final boolean fullInfo) {
        return recommendations.stream()//
            .map(r -> NodeTemplateId.callWithNodeTemplateIdVariants(r.getNodeFactoryClassName(), r.getNodeName(),
                m_nodeRepo::getNode, true))//
            .filter(Objects::nonNull)// `NodeTemplateId.callWithNodeTemplateIdVariants(...)` could return null
            .map(n -> n.factory)//
            .filter(f -> sourcePortType == null || new Node(f).isCompatibleWith(sourcePortType))//
            .limit(limit)// Limit the number of results after filtering by port type compatibility
            .map(f -> fullInfo ? EntityFactory.NodeTemplateAndDescription.buildNodeTemplateEnt(f)
                : EntityFactory.NodeTemplateAndDescription.buildMinimalNodeTemplateEnt(f))//
            .filter(Objects::nonNull)// `EntityBuilderUtil.buildNodeTemplateEnt(...)` could return null
            .collect(Collectors.toList());
    }
}
