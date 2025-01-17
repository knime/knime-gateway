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
 *   Mar 17, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.NodeCategoryEnt;
import org.knime.gateway.api.webui.entity.NodeGroupsEnt;
import org.knime.gateway.api.webui.entity.NodeSearchResultEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.service.NodeRepositoryService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.webui.NodeRelation;
import org.knime.gateway.impl.webui.repo.NodeCategories;
import org.knime.gateway.impl.webui.repo.NodeCategoryExtensions;
import org.knime.gateway.impl.webui.repo.NodeGroups;
import org.knime.gateway.impl.webui.repo.NodeRecommendations;
import org.knime.gateway.impl.webui.repo.NodeRepository;
import org.knime.gateway.impl.webui.repo.NodeSearch;

/**
 * The default implementation of {@link NodeRepositoryService}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultNodeRepositoryService implements NodeRepositoryService {

    private final NodeRepository m_nodeRepo = ServiceDependencies.getServiceDependency(NodeRepository.class, true);

    private final NodeSearch m_nodeSearch;

    private final NodeGroups m_nodeGroups;

    private final NodeRecommendations m_nodeRecommendations;

    private final NodeCategories m_nodeCategories;

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultNodeRepositoryService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultNodeRepositoryService.class);
    }

    DefaultNodeRepositoryService() {
        var nodeCategories = ServiceDependencies.getServiceDependency(NodeCategoryExtensions.class, true);
        m_nodeSearch = new NodeSearch(m_nodeRepo);
        m_nodeGroups = new NodeGroups(m_nodeRepo, nodeCategories);
        m_nodeRecommendations = new NodeRecommendations(m_nodeRepo);
        m_nodeCategories = new NodeCategories(m_nodeRepo, nodeCategories);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public NodeGroupsEnt getNodesGroupedByTags(final Integer numNodesPerTag, final Integer tagsOffset,
        final Integer tagsLimit, final Boolean fullTemplateInfo) {
        return m_nodeGroups.getNodesGroupedByTags(numNodesPerTag, tagsOffset, tagsLimit, fullTemplateInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeSearchResultEnt searchNodes(final String q, final List<String> tags, final Boolean allTagsMatch,
        final Integer offset, final Integer limit, final Boolean fullTemplateInfo, final String portTypeId,
        final String nodeRelation) throws InvalidRequestException {
        return m_nodeSearch.searchNodes(q, tags, allTagsMatch, offset, limit, fullTemplateInfo, portTypeId,
            nodeRelation == null ? null : NodeRelation.valueOf(nodeRelation));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, NodeTemplateEnt> getNodeTemplates(final List<String> templateIds) {
        return m_nodeRepo.getNodeTemplates(templateIds, true);
    }

    @Override
    public NodeCategoryEnt getNodeCategory(final List<String> categoryPath)
        throws ServiceExceptions.NoSuchElementException {
        try {
            return m_nodeCategories.getCategoryEnt(categoryPath);
        } catch (NoSuchElementException e) {
            throw new ServiceExceptions.NoSuchElementException("The requested category could not be found.", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws OperationNotAllowedException
     */
    @Override
    public List<NodeTemplateEnt> getNodeRecommendations(final String projectId, final NodeIDEnt workflowId,
        final NodeIDEnt nodeId, final Integer portIdx, final Integer nodesLimit, final String nodeRelation,
        final Boolean fullTemplateInfo) throws OperationNotAllowedException {
        return m_nodeRecommendations.getNodeRecommendations(projectId, workflowId, nodeId, portIdx, nodesLimit,
            nodeRelation == null ? null : NodeRelation.valueOf(nodeRelation), fullTemplateInfo);
    }
}
