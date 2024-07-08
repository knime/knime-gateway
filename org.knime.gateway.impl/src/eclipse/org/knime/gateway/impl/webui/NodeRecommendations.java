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
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.knime.core.node.NodeFactory.NodeType;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.ui.workflowcoach.NodeRecommendationManager;
import org.knime.core.ui.workflowcoach.NodeRecommendationManager.NodeRecommendation;
import org.knime.core.ui.wrapper.NativeNodeContainerWrapper;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;

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

    private boolean m_nodeRecommendationManagerIsInitialized;

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
        final NodeIDEnt nodeId, final Integer portIdx, final Integer nodesLimit, final Boolean fullTemplateInfo)
        throws OperationNotAllowedException {
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
        var nc = nodeId == null ? null : DefaultServiceUtil.getNodeContainer(projectId, workflowId, nodeId);

        // This `null` is evaluated in `NodeRecommendations#getNodeTemplatesAndFilterByPortType(...)`
        var sourcePortType = nodeId == null ? null : determineSourcePortType(nc, portIdx);

        var recommendations = nc instanceof NativeNodeContainer nnc
            ? Stream.concat(getFlatStreamOfRecommendations(nnc), getFlatStreamOfMostFrequentlyUsedNodes())
            : getFlatStreamOfMostFrequentlyUsedNodes();
        return getNodeTemplatesAndFilter(recommendations, sourcePortType, limit, fullInfo);
    }

    /**
     * Initializes the {@link NodeRecommendationManager} using the {@link NodeRepository} to build the function needed
     *
     * @param nodeRepo The node repository
     * @return Exit status of initialization
     */
    private static boolean initializeNodeRecommendationManager(final NodeRepository nodeRepo) {
        Function<String, NodeType> getNodeType = id -> {
            var node = nodeRepo.getNodeIncludeFilteredNodes(id);
            return node == null ? null : node.nodeSpec.type();
        };
        return NodeRecommendationManager.getInstance().initialize(getNodeType);
    }

    private static PortType determineSourcePortType(final NodeContainer nc, final Integer portIdx)
        throws OperationNotAllowedException {
        if (portIdx + 1 > nc.getNrOutPorts() || portIdx < 0) {
            throw new OperationNotAllowedException("Cannot recommend nodes for non-existing port");
        }
        return nc.getOutPort(portIdx).getPortType();
    }

    private static Stream<NodeRecommendation> getFlatStreamOfRecommendations(final NativeNodeContainer nnc) {
        var recommendations = nnc == null //
            ? NodeRecommendationManager.getInstance().getNodeRecommendationFor() //
            : NodeRecommendationManager.getInstance().getNodeRecommendationFor(NativeNodeContainerWrapper.wrap(nnc));
        var recommendationsWithoutDups =
            NodeRecommendationManager.joinRecommendationsWithoutDuplications(recommendations);
        return recommendationsWithoutDups.stream() //
            .map(ObjectUtils::firstNonNull);
    }

    private static Stream<NodeRecommendation> getFlatStreamOfMostFrequentlyUsedNodes() {
        Supplier<List<NodeRecommendation[]>> supplier = () -> {
            var recommendations = NodeRecommendationManager.getInstance().getMostFrequentlyUsedNodes();
            return NodeRecommendationManager.joinRecommendationsWithoutDuplications(recommendations);
        };
        // This construct makes sure that the above supplier is only being called if the stream is
        // processed until this point (which is not the case, if there are sufficiently many node recommendations
        // such that the result doesn't need to be backfilled with the most frequently used nodes).
        return Stream.of(supplier).flatMap(s -> s.get().stream()).map(ObjectUtils::firstNonNull);
    }

    private List<NodeTemplateEnt> getNodeTemplatesAndFilter(final Stream<NodeRecommendation> recommendations,
        final PortType sourcePortType, final int limit, final boolean fullInfo) {
        return recommendations //
            .map(r -> m_nodeRepo.getNode(r.getFactoryId())) //
            .filter(Objects::nonNull) //
            .filter(n -> sourcePortType == null || n.isCompatibleWith(sourcePortType)) //
            .limit(limit) // Limit the number of results after filtering by port type compatibility
            .map(n -> m_nodeRepo.getNodeTemplate(n.templateId, fullInfo)) //
            .filter(Objects::nonNull) // `EntityBuilderUtil.buildNodeTemplateEnt(...)` could return null
            .toList();
    }
}
