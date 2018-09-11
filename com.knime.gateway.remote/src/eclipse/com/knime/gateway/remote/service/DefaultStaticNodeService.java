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
package com.knime.gateway.remote.service;

import static com.knime.gateway.entity.EntityBuilderManager.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.workbench.repository.RepositoryManager;
import org.knime.workbench.repository.model.AbstractContainerObject;
import org.knime.workbench.repository.model.Category;
import org.knime.workbench.repository.model.IRepositoryObject;
import org.knime.workbench.repository.model.NodeTemplate;
import org.knime.workbench.repository.model.Root;

import com.knime.gateway.v0.entity.NodeCategoryEnt;
import com.knime.gateway.v0.entity.NodeCategoryEnt.NodeCategoryEntBuilder;
import com.knime.gateway.v0.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import com.knime.gateway.v0.entity.NodeTemplateEnt;
import com.knime.gateway.v0.entity.NodeTemplateEnt.NodeTemplateEntBuilder;
import com.knime.gateway.v0.service.StaticNodeService;

/**
 * Default implementation of {@link StaticNodeService} that delegates the operations to knime.core (e.g.
 * {@link WorkflowManager} etc.).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultStaticNodeService implements StaticNodeService {
    private static final DefaultStaticNodeService INSTANCE = new DefaultStaticNodeService();

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultStaticNodeService getInstance() {
       return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeCategoryEnt getAllNodes(final UUID rootWorkflowID, final String nodeType) {
        Root root = RepositoryManager.INSTANCE.getRoot();
        NodeCategoryEntBuilder catBuilder = builder(NodeCategoryEntBuilder.class);
        catBuilder.setName("root");
        addChildren(root, catBuilder, nodeType);
        return catBuilder.build();
    }

    private boolean addChildren(final AbstractContainerObject cat, final NodeCategoryEntBuilder catBuilder,
        final String nodeType) {
        List<NodeCategoryEnt> cats = new ArrayList<NodeCategoryEnt>();
        List<NodeTemplateEnt> facts = new ArrayList<NodeTemplateEnt>();
        for (IRepositoryObject child : cat.getChildren()) {
            if (child instanceof Category) {
                NodeCategoryEntBuilder childCatBuilder = builder(NodeCategoryEntBuilder.class);
                childCatBuilder.setName(child.getName());
                boolean hasChildren = addChildren((Category)child, childCatBuilder, nodeType);
                if (hasChildren) {
                    cats.add(childCatBuilder.build());
                }
            } else if (child instanceof NodeTemplate) {
                NodeTemplate t = (NodeTemplate)child;
                if (nodeType == null || nodeType.length() == 0 || nodeType.equals(t.getExecEnvNodeType())) {
                    NodeTemplateEntBuilder nodeBuilder = builder(NodeTemplateEntBuilder.class);
                    NodeFactoryKeyEntBuilder nodeFacBilder = builder(NodeFactoryKeyEntBuilder.class);
                    nodeFacBilder.setClassName(t.getFactory().getCanonicalName());

                    nodeBuilder.setName(t.getName()).setExecEnvNodeType(t.getExecEnvNodeType())
                        .setNodeFactory(nodeFacBilder.build());

                    facts.add(nodeBuilder.build());
                }
            }
        }
        catBuilder.setCategoryChildren(cats);
        catBuilder.setNodeTemplateChildren(facts);
        return cats.size() + facts.size() > 0;
    }
}
