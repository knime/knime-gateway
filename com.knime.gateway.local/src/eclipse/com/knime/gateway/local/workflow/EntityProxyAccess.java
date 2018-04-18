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
package com.knime.gateway.local.workflow;

import static com.knime.gateway.local.service.ServiceManager.service;
import static com.knime.gateway.local.service.ServiceManager.workflowService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.util.Pair;

import com.knime.gateway.entity.GatewayEntity;
import com.knime.gateway.local.patch.EntityPatchApplierManager;
import com.knime.gateway.local.service.ServerServiceConfig;
import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowNodeEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotASubWorkflowException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotFoundException;

/**
 * Collection of methods helping to access (create/store) the entity-proxy classes (e.g.
 * {@link EntityProxyWorkflowManager}) from the respective entity classes (e.g. {@link WorkflowEnt}).
 *
 * It also provides helper methods to retrieve entity instances (e.g. {@link #getWorkflowEnt(WorkflowNodeEnt)}).
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityProxyAccess {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(EntityProxyAccess.class);

    private final Map<Pair<GatewayEntity, Class<EntityProxy>>, EntityProxy> m_entityProxyMap;

    private final Map<Pair<UUID, String>, AbstractEntityProxyWorkflowManager<? extends NodeEnt>> m_wfmMap;

    private final ServerServiceConfig m_serviceConfig;

    /**
     * Creates a new access instance.
     *
     * @param serviceConfig information how to connect to the server to retrieve entities
     */
    private EntityProxyAccess(final ServerServiceConfig serviceConfig) {
        m_entityProxyMap = new HashMap<Pair<GatewayEntity, Class<EntityProxy>>, EntityProxy>();
        m_wfmMap = new HashMap<Pair<UUID,String>, AbstractEntityProxyWorkflowManager<? extends NodeEnt>>();
        m_serviceConfig = serviceConfig;
    }

    /**
     * Creates a new entity proxy workflow manager.
     *
     * @param serviceConfig the server service config (e.g. uri etc.)
     * @param rootWorkflowID the id of the workflow to retrieve
     * @return the workflow manager
     */
    public static EntityProxyWorkflowManager createWorkflowManager(final ServerServiceConfig serviceConfig,
        final UUID rootWorkflowID) {
        return new EntityProxyAccess(serviceConfig).getWorkflowManager(rootWorkflowID, null);
    }

    /**
     * Retrieves for the given workflow ID the respective entities and returns a workflow manager to access it.
     *
     * The returned {@link EntityProxyWorkflowManager} is or will be cached.
     *
     * @param rootWorkflowID the ID of the workflow manager to retrieve
     * @param nodeID an optional node id to retrieve a sub workflow (i.e. metanode) - can be <code>null</code>
     * @return a newly created {@link EntityProxyWorkflowManager} or retrieved from the cache
     */
    EntityProxyWorkflowManager getWorkflowManager(final UUID rootWorkflowID, final String nodeID) {
        Pair<UUID, String> keyPair = Pair.create(rootWorkflowID, nodeID);
        if (m_wfmMap.containsKey(keyPair)) {
            return (EntityProxyWorkflowManager) m_wfmMap.get(keyPair);
        } else {
            NodeEnt node;
            try {
                if (nodeID == null) {
                    node = service(NodeService.class, m_serviceConfig).getRootNode(rootWorkflowID);
                } else {
                    node = service(NodeService.class, m_serviceConfig).getNode(rootWorkflowID, nodeID);
                }
            } catch (NodeNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            assert node instanceof WorkflowNodeEnt;
            EntityProxyWorkflowManager wfm = getOrCreate((WorkflowNodeEnt)node,
                n -> new EntityProxyWorkflowManager(n, this), EntityProxyWorkflowManager.class);
            m_wfmMap.put(keyPair, wfm);
            return wfm;
        }
    }

    /**
     * Returns the workflow manager for the respective node ent. With every call the same entity proxy instance will be
     * returned for the same rootWorkflowID- and nodeID-combination (taken from the wrapped worklfow node entity).
     *
     * @param rootWorkflowID
     * @param nodeID
     * @return the {@link EntityProxyWrappedWorkflowManager} - either the cached one or newly created
     */
    EntityProxyWrappedWorkflowManager getWrappedWorkflowManager(final WrappedWorkflowNodeEnt ent) {
        Pair<UUID, String> keyPair = Pair.create(ent.getRootWorkflowID(), ent.getNodeID());
        if (m_wfmMap.get(keyPair) != null) {
            EntityProxyWrappedWorkflowManager wfm = (EntityProxyWrappedWorkflowManager)m_wfmMap.get(keyPair);
            //put the workflow manager also into the entity map (in case it's a varying node entity
            getOrCreate(ent, e -> wfm, EntityProxyWrappedWorkflowManager.class);
            return wfm;
        } else {
            //put the workflow manager also into the entity map
            EntityProxyWrappedWorkflowManager wfm = getOrCreate(ent,
                n -> new EntityProxyWrappedWorkflowManager(n, this), EntityProxyWrappedWorkflowManager.class);
            m_wfmMap.put(keyPair, wfm);
            return wfm;
        }
    }

    /**
     * Returns the entity proxy node container for the given node entity.
     *
     * With every call the same entity proxy instance will be returned for the same entity.
     *
     * @param nodeEnt the node entity to be wrapped
     * @return the {@link EntityProxyNodeContainer} - either the cached one or newly created
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    EntityProxyNodeContainer getNodeContainer(final NodeEnt nodeEnt) {
        //return exactly the same node container instance for the same node entity
        return getOrCreate(nodeEnt, k -> {
            if (nodeEnt instanceof NativeNodeEnt) {
                return new EntityProxyNativeNodeContainer((NativeNodeEnt)nodeEnt, this);
            }
            //order of checking very important here, since WrappedWorkflowNode derived from WorkflowNode
            if (nodeEnt instanceof WrappedWorkflowNodeEnt) {
                return new EntityProxySubNodeContainer((WrappedWorkflowNodeEnt)nodeEnt, this);
            }
            if (nodeEnt instanceof WorkflowNodeEnt) {
                return new EntityProxyWorkflowManager((WorkflowNodeEnt)nodeEnt, this);
            }
            throw new IllegalStateException("Node entity type " + nodeEnt.getClass().getName() + " not supported.");
        }, EntityProxyNodeContainer.class);
    }

    /**
     * If an {@link EntityProxyNodeContainer} already exists for the 'oldNode', the entity will be replaced with
     * 'newNode'. Otherwise nothing happens.
     *
     * @param oldNode the entity to be replaced in an {@link EntityProxyNodeContainer}
     * @param newNode the entity to replace with
     */
    void updateNodeContainer(final NodeEnt oldNode, final NodeEnt newNode) {
        update(oldNode, newNode, EntityProxyNodeContainer.class);
    }

    /**
     * Returns the entity proxy for the given connection entity.
     *
     * With every call the same entity proxy instance will be returned for the same entity.
     *
     * @param c the entity to get the client-proxy wrapper for
     * @param objCache the cache that allows one to re-use class instances
     * @return the {@link EntityProxyConnectionContainer} - either the cached one or newly created
     */
    EntityProxyConnectionContainer getConnectionContainer(final ConnectionEnt c) {
        return getOrCreate(c, o -> new EntityProxyConnectionContainer(c, this), EntityProxyConnectionContainer.class);
    }

    /**
     * Returns the entity proxy for the given node port entity.
     *
     * With every call the same entity proxy instance will be returned for the same entity.
     *
     * @param p the entity to get the client-proxy wrapper for
     * @return the {@link EntityProxyNodeInPort} - either the cached one or newly created
     */
    EntityProxyNodeInPort getNodeInPort(final NodeInPortEnt p) {
        //possibly return the same node in port instance for the same index
        return getOrCreate(p, o -> new EntityProxyNodeInPort(o, this), EntityProxyNodeInPort.class);
    }

    /**
     * Returns the entity proxy for the given node port entity.
     *
     * With every call the same entity proxy instance will be returned for the same entity.
     *
     * @param p the entity to get the client-proxy wrapper for
     * @param node the node the passed port belongs to
     * @return the {@link EntityProxyNodeOutPort} - either the cached one or newly created
     */
    EntityProxyNodeOutPort getNodeOutPort(final NodeOutPortEnt p, final NodeEnt node) {
        //possibly return the same node out port instance for the same index
        return getOrCreate(p, o -> new EntityProxyNodeOutPort(o, node, this), EntityProxyNodeOutPort.class);
    }

    /**
     * Return the entity proxy for the given node port entity.
     *
     * With every call the same entity proxy instance will be returned for the same entity.
     *
     * @param p the entity to get the client-proxy wrapper for
     * @param underlyingPort
     * @param node
     * @return the {@link EntityProxyWorkflowInPort} - either the cached one or newly created
     */
    EntityProxyWorkflowInPort getWorkflowInPort(final NodeInPortEnt p, final NodeOutPortEnt underlyingPort,
        final NodeEnt node) {
        //possibly return the same node in port instance for the same index
        return getOrCreate(p, o -> new EntityProxyWorkflowInPort(o, underlyingPort, node, this),
            EntityProxyWorkflowInPort.class);
    }

    /**
     * With every call the same entity proxy instance will be returned for the same entity.
     *
     * @param p the entity to get the client-proxy wrapper for
     * @param node
     * @return the {@link EntityProxyWorkflowOutPort} - either the cached one or newly created
     */
    EntityProxyWorkflowOutPort getWorkflowOutPort(final NodeOutPortEnt p, final NodeEnt node) {
        //possibly return the same node out port instance for the same index
        return getOrCreate(p, o -> new EntityProxyWorkflowOutPort(o, node, this), EntityProxyWorkflowOutPort.class);
    }

    /**
     *
     *
     * @param wa the entity to get the workflow annotation for
     * @return the {@link WorkflowAnnotation}
     */
    EntityProxyWorkflowAnnotation getWorkflowAnnotation(final WorkflowAnnotationEnt wa) {
        return getOrCreate(wa, o -> {
            return new EntityProxyWorkflowAnnotation(wa, this);
        }, EntityProxyWorkflowAnnotation.class);
    }

    /**
     * Gets the actual {@link WorkflowSnapshotEnt} from the server for the given workflow node.
     *
     * @param workflowNodeEnt the entity to get the workflow for
     * @return the workflow entity
     */
    WorkflowSnapshotEnt getWorkflowSnapshotEnt(final WorkflowNodeEnt workflowNodeEnt) {
        if (workflowNodeEnt.getParentNodeID() != null) {
            //in case it's a sub-workflow
            try {
                return workflowService(m_serviceConfig)
                    .getSubWorkflow(workflowNodeEnt.getRootWorkflowID(), workflowNodeEnt.getNodeID());
            } catch (NotASubWorkflowException | NodeNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            //in case it's the root workflow
            return workflowService(m_serviceConfig).getWorkflow(workflowNodeEnt.getRootWorkflowID());
        }
    }

    /**
     * Updates the status of a workflow entity.
     *
     * @param rootWorkflowId the root workflow id of the workflow to update
     * @param nodeID the node id in case it's a sub-workflow, otherwise <code>null</code> (if it's the root workflow)
     * @param workflowEntToUpdate the actual entity to be updated
     * @param snapshotID the id of the currently available snapshot
     * @return the updated (new) entity or the very same entity if there are no changes (both accompanied with the
     *         (new) snapshot id)
     */
    Pair<WorkflowEnt, UUID> updateWorkflowEnt(final WorkflowNodeEnt workflowNodeEnt,
        final WorkflowEnt workflowEntToUpdate, final UUID snapshotID) {
        PatchEnt patch = null;
        if (workflowNodeEnt.getParentNodeID() == null) {
            //in case it's the root workflow
            try {
                patch = workflowService(m_serviceConfig).getWorkflowDiff(workflowNodeEnt.getRootWorkflowID(),
                    snapshotID);
            } catch (NotFoundException ex) {
                //no snapshot for the given id -> download entire workflow again
                LOGGER.debug("No patch available. Entire workflow downloaded again.");
            }
        } else {
            // in case it's a sub-workflow
            try {
                patch = workflowService(m_serviceConfig).getSubWorkflowDiff(workflowNodeEnt.getRootWorkflowID(),
                    workflowNodeEnt.getNodeID(), snapshotID);
            } catch (NotASubWorkflowException ex) {
                throw new RuntimeException(ex);
            } catch (NotFoundException ex) {
                //no snapshot for the given id -> downlaod entire workflow again
                LOGGER.debug("No patch available. Entire workflow downloaded again.");
            }
        }
        if (patch == null) {
            //no patch available -> retrieve entire workflow again
            WorkflowSnapshotEnt snapshot = getWorkflowSnapshotEnt(workflowNodeEnt);
            return Pair.create(snapshot.getWorkflow(), snapshot.getSnapshotID());
        } else {
            // apply patch and return new version
            if (!patch.getOps().isEmpty()) {
                return Pair.create(EntityPatchApplierManager.getPatchApplier().applyPatch(workflowEntToUpdate, patch),
                    patch.getSnapshotID());
            } else {
                return Pair.create(workflowEntToUpdate, null);
            }
        }
    }

    /**
     * Download node's settings as json.
     *
     * @param node the node to get the settings for
     * @return the settings formatted as json
     */
    String getSettingsAsJson(final NodeEnt node) {
        try {
            return service(NodeService.class, m_serviceConfig).getNodeSettings(node.getRootWorkflowID(),
                node.getNodeID());
        } catch (NodeNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates or returns a cached entity proxy.
     *
     * Its primary purpose is to workaround "1:1" wrappers. Within the eclipse UI very often instances are checked for
     * object equality. Consequently, there must be exactly one wrapper class instance for a certain object to be
     * wrapped. Two wrapper instances wrapping the same object should be avoided (this happens if, e.g. a getter method
     * is called twice and each time a new wrapper instance is created around the same object returned).
     *
     * This global map caches the proxy instances for look up with the actual entity as key (weak references).
     *
     * @param entity the key (a gateway entity) to look for in the internal map (and to store newly created objects
     *            with)
     * @param fct tells how to create the entity prox if not present in the internal map
     * @return
     *         <ul>
     *         <li><code>null</code> if the key is <code>null</code>
     *         <li>a newly created entity proxy,</li>
     *         <li>or the entity proxy stored in the internal map for the given entity</li>
     *         </ul>
     */
    private <E extends GatewayEntity, P extends EntityProxy<E>> P getOrCreate(final E entity, final Function<E, P> fct,
        final Class<P> proxyClass) {
        if (entity == null) {
            return null;
        } else {
            Pair<GatewayEntity, Class<EntityProxy>> key =
                new Pair<GatewayEntity, Class<EntityProxy>>(entity, (Class<EntityProxy>)proxyClass);
            P proxy = (P)m_entityProxyMap.get(key);
            if (proxy == null) {
                //create proxy entry
                proxy = fct.apply(entity);
                m_entityProxyMap.put(key, proxy);
            }
            return proxy;
        }
    }

    private <E extends GatewayEntity, P extends EntityProxy<E>> void update(final E oldEntity, final E newEntity, final Class<P> proxyClass) {
        Pair<GatewayEntity, Class<EntityProxy>> oldKey =
                new Pair<GatewayEntity, Class<EntityProxy>>(oldEntity, (Class<EntityProxy>)proxyClass);
        Pair<GatewayEntity, Class<EntityProxy>> newKey =
                new Pair<GatewayEntity, Class<EntityProxy>>(newEntity, (Class<EntityProxy>)proxyClass);
        EntityProxy entityProxy = m_entityProxyMap.get(oldKey);
        if (entityProxy != null) {
            entityProxy.update(newEntity);
            m_entityProxyMap.put(newKey, entityProxy);
            m_entityProxyMap.remove(oldKey);
        }
    }
}
