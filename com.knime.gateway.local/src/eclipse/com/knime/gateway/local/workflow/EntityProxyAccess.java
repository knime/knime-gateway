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
import static com.knime.gateway.util.EntityUtil.stringToNodeID;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContent;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.port.AbstractSimplePortObjectSpec;
import org.knime.core.node.port.AbstractSimplePortObjectSpec.AbstractSimplePortObjectSpecSerializer;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObjectSpec;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.FlowVariable;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.util.Pair;

import com.google.common.collect.MapMaker;
import com.knime.gateway.entity.GatewayEntity;
import com.knime.gateway.local.service.ServerServiceConfig;
import com.knime.gateway.local.util.missing.MissingPortObject;
import com.knime.gateway.local.workflow.EntityProxyNodeOutPort.UnsupportedPortObjectSpec;
import com.knime.gateway.v0.entity.ConnectionEnt;
import com.knime.gateway.v0.entity.FlowVariableEnt;
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeInPortEnt;
import com.knime.gateway.v0.entity.NodeOutPortEnt;
import com.knime.gateway.v0.entity.NodePortEnt;
import com.knime.gateway.v0.entity.PatchEnt;
import com.knime.gateway.v0.entity.PortObjectSpecEnt;
import com.knime.gateway.v0.entity.PortTypeEnt;
import com.knime.gateway.v0.entity.WorkflowAnnotationEnt;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.entity.WorkflowNodeEnt;
import com.knime.gateway.v0.entity.WorkflowSnapshotEnt;
import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;
import com.knime.gateway.v0.service.AnnotationService;
import com.knime.gateway.v0.service.NodeService;
import com.knime.gateway.v0.service.WorkflowService;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotASubWorkflowException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotFoundException;

/**
 * Collection of methods helping to access (create/store) the entity-proxy classes (e.g.
 * {@link EntityProxyWorkflowManager}) from the respective entity classes (e.g. {@link WorkflowEnt}).
 *
 * @author Martin Horn, University of Konstanz
 */
public class EntityProxyAccess {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(EntityProxyAccess.class);

    /**
     * Map that keeps track of all root workflow ids and maps them to a unique node ids. It's the id the will be
     * prepended to the node's id (see {@link #getID()}).
     *
     * TODO: remove workflow id's from the list that aren't in memory anymore
     */
    private static final Map<UUID, String> ROOT_ID_MAP = new HashMap<UUID, String>();

    /* Maps the identity hash code of a entity to the entity proxy object */
    @SuppressWarnings("rawtypes")
    private final Map<Pair<Integer, Class<EntityProxy>>, EntityProxy> m_entityProxyMap;

    private final Map<Pair<UUID, String>, AbstractEntityProxyWorkflowManager<? extends NodeEnt>> m_wfmMap;

    private final ServerServiceConfig m_serviceConfig;

    //for debugging purposes
    private int m_numCreatedEntityProxies = 0;

    /**
     * Creates a new access instance.
     *
     * @param serviceConfig information how to connect to the server to retrieve entities
     */
    private EntityProxyAccess(final ServerServiceConfig serviceConfig) {
        m_entityProxyMap = new MapMaker().weakValues().makeMap();
        m_wfmMap = new MapMaker().weakValues().makeMap();
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
        ROOT_ID_MAP.computeIfAbsent(rootWorkflowID, s -> String.valueOf(ROOT_ID_MAP.size() + 1));
        return new EntityProxyAccess(serviceConfig).getRootWorkflowManager(rootWorkflowID);
    }

    /**
     * Retrieves the respective entities for the given root workflow id and returns a workflow manager to access it.
     *
     * @param rootWorkflowID the ID of the workflow manager to retrieve
     * @return an existing (i.e. cached) or newly created workflow manager instance
     */
    public EntityProxyWorkflowManager getRootWorkflowManager(final UUID rootWorkflowID) {
        Pair<UUID, String> keyPair = Pair.create(rootWorkflowID, null);
        if (m_wfmMap.containsKey(keyPair)) {
            return (EntityProxyWorkflowManager)m_wfmMap.get(keyPair);
        } else {
            NodeEnt node = service(NodeService.class, m_serviceConfig).getRootNode(rootWorkflowID);
            assert node instanceof WorkflowNodeEnt;
            EntityProxyWorkflowManager wfm = getOrCreate((WorkflowNodeEnt)node,
                n -> new EntityProxyWorkflowManager(n, this), EntityProxyWorkflowManager.class);
            m_wfmMap.put(keyPair, wfm);
            return wfm;
        }
    }

    /**
     * Retrieves for the given workflow ID the respective entities and returns a workflow manager to access it.
     *
     * The returned {@link AbstractEntityProxyWorkflowManager} is or will be cached.
     *
     * @param rootWorkflowID the ID of the workflow manager to retrieve
     * @param nodeID an optional node id to retrieve a sub workflow (i.e. metanode) - can be <code>null</code>
     * @return an existing workflow manager instance or <code>null</code>
     */
    AbstractEntityProxyWorkflowManager<? extends NodeEnt> getAbstractWorkflowManager(final UUID rootWorkflowID,
        final String nodeID) {
        return m_wfmMap.get(Pair.create(rootWorkflowID, nodeID));
    }

    /**
     * Returns the workflow manager for the respective node ent. With every call the same entity proxy instance will be
     * returned for the same rootWorkflowID- and nodeID-combination (taken from the wrapped workfow node entity).
     *
     * @param ent the workflow node referencing the workflow
     *
     * @return the {@link EntityProxyWrappedWorkflowManager} - either the cached one or newly created
     */
    EntityProxyWrappedWorkflowManager getWrappedWorkflowManager(final WrappedWorkflowNodeEnt ent) {
        //workflow ids of wrapped metanodes have a trailing ":0" - see line 309 in SubNodeContainer
        Pair<UUID, String> keyPair = Pair.create(ent.getRootWorkflowID(), ent.getNodeID() + ":0");
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
     * @return the {@link AbstractEntityProxyNodeContainer} - either the cached one or newly created
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    AbstractEntityProxyNodeContainer getNodeContainer(final NodeEnt nodeEnt) {
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
                EntityProxyWorkflowManager wfm = new EntityProxyWorkflowManager((WorkflowNodeEnt)nodeEnt, this);
                //also add this workflow manager to the global wfm map
                m_wfmMap.put(Pair.create(nodeEnt.getRootWorkflowID(), nodeEnt.getNodeID()), wfm);
                return wfm;
            }
            throw new IllegalStateException("Node entity type " + nodeEnt.getClass().getName() + " not supported.");
        }, AbstractEntityProxyNodeContainer.class);
    }

    /**
     * Gives the correct node id determined from a {@link NodeEnt}.
     *
     * @param ent the node entity to get the node id from
     * @return the {@link NodeID}
     */
    NodeID getNodeID(final NodeEnt ent) {
        return stringToNodeID(ROOT_ID_MAP.get(ent.getRootWorkflowID()), ent.getNodeID());
    }

    /**
     * Gives the correct node id determined from a root workflow id and a string node id
     * @param rootWorkflowID the id of the root workflow
     * @param nodeID the id string to parse
     * @return the {@link NodeID}
     */
    NodeID getNodeID(final UUID rootWorkflowID, final String nodeID) {
        return stringToNodeID(ROOT_ID_MAP.get(rootWorkflowID), nodeID);
    }

    /**
     * Turns a string into the respective {@link WorkflowAnnotationID}.
     * @param rootWorkflowID the id of the root workflow
     * @param annotationID the id-string to parse
     * @return the {@link WorkflowAnnotationID}
     */
    WorkflowAnnotationID getAnnotationID(final UUID rootWorkflowID, final String annotationID) {
        String[] id = annotationID.split("_");
        NodeID nodeID = getNodeID(rootWorkflowID, id[0]);
        return new WorkflowAnnotationID(nodeID, Integer.valueOf(id[1]));
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
    EntityProxyConnectionContainer getConnectionContainer(final ConnectionEnt c, final UUID rootWorkflowID) {
        return getOrCreate(c, o -> new EntityProxyConnectionContainer(c, rootWorkflowID, this), EntityProxyConnectionContainer.class);
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
     * @param node the node the passed port belongs to. Can be <code>null</code> if no new entity proxy instance is
     *            created (otherwise a {@link NullPointerException} will be thrown).
     * @return the {@link EntityProxyNodeOutPort} - either the cached one or newly created
     * @throws NullPointerException if node is <code>null</code> but a new proxy instance is created
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    EntityProxyNodeOutPort getNodeOutPort(final NodeOutPortEnt p, final NodeEnt node) {
        //possibly return the same node out port instance for the same index
        return getOrCreate(p, o -> {
            if (node == null) {
                throw new NullPointerException("Node must not be null.");
            }
            return new EntityProxyNodeOutPort(o, node, this);
        }, EntityProxyNodeOutPort.class);
    }

    /**
     * Return the entity proxy for the given node port entity.
     *
     * With every call the same entity proxy instance will be returned for the same entity.
     *
     * @param p the entity to get the client-proxy wrapper for
     * @return the {@link EntityProxyWorkflowInPort} - either the cached one or newly created
     * @throws NullPointerException if node or underlyingPort is <code>null</code> but a new proxy instance is created
     */
    EntityProxyWorkflowInPort getWorkflowInPort(final NodeInPortEnt p) {
        //possibly return the same node in port instance for the same index
        return getOrCreate(p, o -> {
           return new EntityProxyWorkflowInPort(o, this);
        }, EntityProxyWorkflowInPort.class);
    }

    /**
     * With every call the same entity proxy instance will be returned for the same entity.
     *
     * @param p the entity to get the client-proxy wrapper for
     * @param node the node the port belongs to. Can be <code>null</code> if no new entity proxy instance is created
     *            (otherwise a {@link NullPointerException} will be thrown).
     * @return the {@link EntityProxyWorkflowOutPort} - either the cached one or newly created
     * @throws NullPointerException if node is <code>null</code> but a new proxy instance is created
     */
    EntityProxyWorkflowOutPort getWorkflowOutPort(final NodeOutPortEnt p, final WorkflowNodeEnt node) {
        //possibly return the same node out port instance for the same index
        return getOrCreate(p, o -> {
            if (node == null) {
                throw new NullPointerException("Node must not be null.");
            }
            return new EntityProxyWorkflowOutPort(o, node, this);
        }, EntityProxyWorkflowOutPort.class);
    }

    /**
     * @param wa the entity to get the workflow annotation for
     * @param rootWorkflowID
     * @param parentNodeID
     * @return the {@link WorkflowAnnotation}
     */
    EntityProxyWorkflowAnnotation getWorkflowAnnotation(final WorkflowAnnotationEnt wa, final UUID rootWorkflowID,
        final String parentNodeID) {
        return getOrCreate(wa, o -> {
            return new EntityProxyWorkflowAnnotation(wa, rootWorkflowID, parentNodeID, this);
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
                return workflowService()
                    .getSubWorkflow(workflowNodeEnt.getRootWorkflowID(), workflowNodeEnt.getNodeID());
            } catch (NotASubWorkflowException | NodeNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            //in case it's the root workflow
            return workflowService().getWorkflow(workflowNodeEnt.getRootWorkflowID());
        }
    }

    /**
     * Requests the diff (i.e. patch) for a workflow with a snapshot id.
     *
     * @param workflowNodeEnt the workflow node referencing the workflow to update
     * @param snapshotID the id of the currently available snapshot
     * @return the patch
     * @throws NotFoundException thrown is there is no snapshot for the given snapshot ID (because, e.g., job has been
     *             job was swapped, deleted or snapshot has expired)
     */
    PatchEnt getWorkflowPatch(final WorkflowNodeEnt workflowNodeEnt, final UUID snapshotID) throws NotFoundException {
        if (workflowNodeEnt.getParentNodeID() == null) {
            //in case it's the root workflow
            return workflowService().getWorkflowDiff(workflowNodeEnt.getRootWorkflowID(), snapshotID);
        } else {
            // in case it's a sub-workflow
            try {
                return workflowService().getSubWorkflowDiff(workflowNodeEnt.getRootWorkflowID(),
                    workflowNodeEnt.getNodeID(), snapshotID);
            } catch (NotASubWorkflowException ex) {
                //should never happen
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Download node's settings as json.
     *
     * @param node the node to get the settings for
     * @return the settings formatted as json
     * @throws NodeNotFoundException if node wasn't found
     */
    NodeSettings getNodeSettings(final NodeEnt node) throws NodeNotFoundException {
        try {
            String json = service(NodeService.class, m_serviceConfig)
                .getNodeSettings(node.getRootWorkflowID(), node.getNodeID()).getJsonContent();
            return JSONConfig.readJSON(new NodeSettings("settings"), new StringReader(json));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read NodeSettings from JSON String", ex);
        }
    }

    /**
     * Retrieves the input port object specs. Entries can be <code>null</code>, if port object spec is not available. If
     * a requested spec is not supported by the gateway (because it cannot be serialized), a
     * {@link UnsupportedPortObjectSpec} instance will be returned instead.
     *
     * @param node node to retrieve the specs for
     * @return the specs for all ports (including the flow var port)
     * @throws NodeNotFoundException if the node wasn't found
     */
    PortObjectSpec[] getInputPortObjectSpecs(final NodeEnt node) throws NodeNotFoundException {
        //TODO cache the port object specs
        if (!node.getOutPorts().isEmpty()) {
            List<PortObjectSpecEnt> entList = service(NodeService.class, m_serviceConfig)
                .getInputPortSpecs(node.getRootWorkflowID(), node.getNodeID());
            return createPortObjectSpecsFromEntity(entList, node.getInPorts());
        } else {
            return new PortObjectSpec[0];
        }
    }

    /**
     * Retrieves the output port object specs. Entries can be <code>null</code>, if port object spec is not available.
     * If a requested spec is not supported by the gateway (because it cannot be serialized), a
     * {@link UnsupportedPortObjectSpec} instance will be returned instead.
     *
     * @param node node to retrieve the specs for
     * @return the specs for all ports (including the flow var port)
     * @throws NodeNotFoundException if the node wasn't found
     */
    PortObjectSpec[] getOutputPortObjectSpecs(final NodeEnt node)
        throws NodeNotFoundException {
        //TODO cache the port object specs
        if (!node.getOutPorts().isEmpty()) {
            List<PortObjectSpecEnt> entList = service(NodeService.class, m_serviceConfig)
                .getOutputPortSpecs(node.getRootWorkflowID(), node.getNodeID());
            return createPortObjectSpecsFromEntity(entList, node.getOutPorts());
        } else {
            return new PortObjectSpec[0];
        }
    }

    /**
     * Gives access to the outport data table for a particular node and it's node port.
     *
     * @param port the port to get the table for
     * @param node the node
     * @param spec the data table spec
     * @return either cached or a new entity proxy
     */
    EntityProxyDataTable getOutputDataTable(final NodeOutPortEnt port, final NodeEnt node, final DataTableSpec spec) {
        return getOrCreate(port, o -> new EntityProxyDataTable(port, node, spec, this), EntityProxyDataTable.class);
    }


    /**
     * Gets the matching port type for the given port type entity.
     *
     * @param ent the entity to convert into the actual PortType
     *
     * @return the port type
     */
    static PortType getPortType(final PortTypeEnt ent) {
        PortTypeRegistry ptr = PortTypeRegistry.getInstance();
        Class<? extends PortObject> portObjectClass =
            ptr.getObjectClass(ent.getPortObjectClassName()).orElseGet(() -> MissingPortObject.class);
        return ptr.getPortType(portObjectClass, ent.isOptional());
    }

    private static PortObjectSpec[] createPortObjectSpecsFromEntity(final List<PortObjectSpecEnt> entList,
        final List<? extends NodePortEnt> ports) {
        assert ports.size() == entList.size();
        PortObjectSpec[] res = new PortObjectSpec[entList.size()];
        for (int i = 0; i < res.length; i++) {
            PortObjectSpecEnt ent = entList.get(i);
            if (ent == null) {
                continue;
            }
            if (ent.isInactive()) {
                res[i] = InactiveBranchPortObjectSpec.INSTANCE;
                continue;
            }
            PortType ptype = getPortType(ent.getType());
            if (DataTableSpec.class.isAssignableFrom(ptype.getPortObjectSpecClass())) {
                try {
                    res[i] = DataTableSpec.load(
                        JSONConfig.readJSON(new ModelContent("model"), new StringReader(ent.getRepresentation())));
                } catch (InvalidSettingsException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (ptype.equals(FlowVariablePortObject.TYPE)) {
                res[i] = FlowVariablePortObjectSpec.INSTANCE;
            } else if (AbstractSimplePortObjectSpec.class.isAssignableFrom(ptype.getPortObjectSpecClass())) {
                ModelContent model = new ModelContent("model");
                try {
                    JSONConfig.readJSON(model, new StringReader(ent.getRepresentation()));
                    res[i] = AbstractSimplePortObjectSpecSerializer.loadPortObjectSpecFromModelSettings(model);
                } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                res[i] = new UnsupportedPortObjectSpec(ptype);
            }
        }
        return res;
    }

    /**
     * Retrieves the input flow variables for a node and returns them as a list.
     *
     * @param node the node to retrieve the flow variables for
     * @return the variables as a list
     */
    List<FlowVariable> getInputFlowVariableList(final NodeEnt node) {
        try {
            return flowVarEntToList(service(NodeService.class, m_serviceConfig)
                .getInputFlowVariables(node.getRootWorkflowID(), node.getNodeID()));
        } catch (NodeNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Retrieves the output flow variables for a node and returns them as a list.
     *
     * @param node the node to retrieve the flow variables for
     * @return the variables as a list
     */
    List<FlowVariable> getOutputFlowVariableList(final NodeEnt node) {
        try {
            return flowVarEntToList(service(NodeService.class, m_serviceConfig)
                .getOutputFlowVariables(node.getRootWorkflowID(), node.getNodeID()));
        } catch (NodeNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private List<FlowVariable> flowVarEntToList(final List<FlowVariableEnt> ents) {
        return ents.stream().map(e -> {
            switch (e.getType()) {
                case DOUBLE:
                    return new FlowVariable(e.getName(), Double.valueOf(e.getValue()));
                case INTEGER:
                    return new FlowVariable(e.getName(), Integer.valueOf(e.getValue()));
                case STRING:
                    return new FlowVariable(e.getName(), e.getValue());
                default:
                    throw new IllegalStateException();
            }
        }).collect(Collectors.toList());
    }

    /**
     * Retrieves the input flow variables for a node and returns them as a new {@link FlowObjectStack}.
     *
     * @param node see {@link #getFlowVariableList(NodeEnt, boolean)}
     * @return the variables as a stack
     */
    FlowObjectStack getInputFlowVariableStack(final NodeEnt node, final NodeID nodeId) {
        return FlowObjectStack.createFromFlowVariableList(getInputFlowVariableList(node), nodeId);
    }

    /**
     * Retrieves the output flow variables for a node and returns them as a new {@link FlowObjectStack}.
     *
     * @param node see {@link #getFlowVariableList(NodeEnt, boolean)}
     * @return the variables as a stack
     */
    FlowObjectStack getOutputFlowVariableStack(final NodeEnt node, final NodeID nodeId) {
        return FlowObjectStack.createFromFlowVariableList(getOutputFlowVariableList(node), nodeId);
    }

    /**
     * Return the interactive web views result entity proxy for the given node entity.
     *
     * With every call the same entity proxy instance will be returned for the same entity.
     *
     * @param node the entity to get the client-proxy wrapper for
     * @return the {@link EntityProxyInteractiveWebViewsResult} - either the cached one or newly created
     */
    EntityProxyInteractiveWebViewsResult getInteractiveWebViewsResult(final NativeNodeEnt node) {
        //possibly return the same node in port instance for the same index
        return getOrCreate(node, o -> new EntityProxyInteractiveWebViewsResult(o, this),
            EntityProxyInteractiveWebViewsResult.class);
    }

    /**
     * Returns a web view model entity proxy for the given node.
     *
     * @param node the node to get the web view model for
     * @param viewName the view name
     * @return the {@link EntityProxyWebViewModel} - either the cached one or newly created
     */
    EntityProxyWebViewModel getEntityProxyWebViewModel(final NativeNodeEnt node, final String viewName) {
        return getOrCreate(node, o -> new EntityProxyWebViewModel(o, viewName, this), EntityProxyWebViewModel.class);
    }

    /**
     * @return the current node service in use
     */
    NodeService nodeService() {
        return service(NodeService.class, m_serviceConfig);
    }

    /**
     * @return the current workflow service in use
     */
    WorkflowService workflowService() {
        return service(WorkflowService.class, m_serviceConfig);
    }

    /**
     * @return the current annotation service in use
     */
    AnnotationService annotationService() {
        return service(AnnotationService.class, m_serviceConfig);
    }

    /**
     * If an {@link AbstractEntityProxyNodeContainer} already exists for the 'oldNode', the entity will be replaced with
     * 'newNode'. Otherwise nothing happens.
     *
     * After the update is done, {@link EntityProxy#postUpdate()} will be called, too.
     *
     * @param oldNode the entity to be replaced in an {@link AbstractEntityProxyNodeContainer}
     * @param newNode the entity to replace with
     */
    @SuppressWarnings("unchecked")
    void updateNodeContainer(final NodeEnt oldNode, final NodeEnt newNode) {
        if(newNode == null || oldNode == null) {
            return;
        }
        if (update(oldNode, newNode, AbstractEntityProxyNodeContainer.class)) {
            postUpdate(newNode, AbstractEntityProxyNodeContainer.class);
        }
    }

    /**
     * If an {@link EntityProxyConnectionContainer} already exists for the 'oldConn', the entity will be replaced with
     * 'newConn'. Otherwise nothing happens.
     *
     * After the update is done, {@link EntityProxy#postUpdate()} will be called, too.
     *
     * @param oldNode the entity to be replaced in an {@link EntityProxyConnectionContainer}
     * @param newNode the entity to replace with
     */
    void updateConnectionContainer(final ConnectionEnt oldConn, final ConnectionEnt newConn) {
        if (newConn == null || oldConn == null) {
            return;
        }
        if (update(oldConn, newConn, EntityProxyConnectionContainer.class)) {
            postUpdate(newConn, EntityProxyConnectionContainer.class);
        }
    }

    /**
     * If an {@link EntityProxyWorkflowAnnotation} already exists for the 'oldAnno', the entity will be replaced with
     * 'newAnno'. Otherwise nothing happens.
     *
     * @param oldAnno the entity to be replaced in an {@link EntityProxyWorkflowAnnotation}
     * @param newAnno the entity to replace with
     * @return whether the entity has been updated
     *
     */
    boolean updateWorkflowAnnotation(final WorkflowAnnotationEnt oldAnno, final WorkflowAnnotationEnt newAnno) {
        if (oldAnno == null || newAnno == null) {
            return false;
        }
        return update(oldAnno, newAnno, EntityProxyWorkflowAnnotation.class);
    }

    /**
     * Usually called after all entites have been updated (via
     * {@link #updateWorkflowAnnotation(WorkflowAnnotationEnt, WorkflowAnnotationEnt)}). The post-update then, e.g.,
     * refreshes the visuals etc.
     *
     * @param newAnno the anno to do the post-update for
     */
    void postUpdateWorkflowAnnotation(final WorkflowAnnotationEnt newAnno) {
        postUpdate(newAnno, EntityProxyWorkflowAnnotation.class);
    }

    /**
     * Updates the node port entities and the referenced node entity of a EntityProxyNode*Port instance.
     *
     * @param oldNode the node entity whose port entities are to be replaced
     * @param newNode the node entity to take the new port entities from
     */
    @SuppressWarnings("unchecked")
    void updateNodeOutPorts(final NodeEnt oldNode, final NodeEnt newNode) {
        for (int i = 0; i < oldNode.getOutPorts().size(); i++) {
            if (update(oldNode.getOutPorts().get(i), newNode.getOutPorts().get(i), EntityProxyNodeOutPort.class)) {
                //also update the node entity referenced by the port
                getNodeOutPort(newNode.getOutPorts().get(i), null).updateNodeEnt(newNode);
            }
        }
    }

    /**
     * Same as {@link #updateNodeOutPorts(NodeEnt, NodeEnt)} but for workflow out ports.
     *
     * @param oldNode
     * @param newNode
     */
    void updateWorkflowNodeOutPorts(final WorkflowNodeEnt oldNode, final WorkflowNodeEnt newNode) {
        for (int i = 0; i < oldNode.getOutPorts().size(); i++) {
            if (update(oldNode.getOutPorts().get(i), newNode.getOutPorts().get(i), EntityProxyWorkflowOutPort.class)) {
                //also update the node entity referenced by the port
                getWorkflowOutPort(newNode.getOutPorts().get(i), null).updateNodeEnt(newNode);
            }
        }
    }

    /**
     * Updates the node port entities and the referenced node entity of a EntityProxyNode*Port instance.
     *
     * @param oldNode
     * @param newNode
     */
    void updateNodeInPorts(final NodeEnt oldNode, final NodeEnt newNode) {
        for (int i = 0; i < oldNode.getInPorts().size(); i++) {
            update(oldNode.getInPorts().get(i), newNode.getInPorts().get(i), EntityProxyNodeInPort.class);
        }
    }

    /**
     * Same as {@link #updateNodeInPorts(NodeEnt, NodeEnt)} but for workflow ports.
     *
     * @param oldNode
     * @param newNode
     */
    void updateWorkflowNodeInPorts(final WorkflowNodeEnt oldNode, final WorkflowNodeEnt newNode) {
        for (int i = 0; i < oldNode.getInPorts().size(); i++) {
            update(oldNode.getInPorts().get(i), newNode.getInPorts().get(i), EntityProxyWorkflowInPort.class);
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
     * @param obj an object whose identity hash code is used as the key to look for in the internal map (and to store
     *            newly created objects with)
     * @param fct tells how to create the entity prox if not present in the internal map
     * @return
     *         <ul>
     *         <li><code>null</code> if the key is <code>null</code>
     *         <li>a newly created entity proxy,</li>
     *         <li>or the entity proxy stored in the internal map for the given entity</li>
     *         </ul>
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private <E, P extends EntityProxy<E>> P getOrCreate(final E obj, final Function<E, P> fct,
        final Class<P> proxyClass) {
        if (obj == null) {
            return null;
        } else {
            Pair<Integer, Class<EntityProxy>> key =
                new Pair<Integer, Class<EntityProxy>>(System.identityHashCode(obj), (Class<EntityProxy>)proxyClass);
            P proxy = (P)m_entityProxyMap.get(key);
            if (proxy == null) {
                //create proxy entry
                proxy = fct.apply(obj);
                m_entityProxyMap.put(key, proxy);
                LOGGER
                    .debug("New entity proxy of type '" + proxy.getClass().getSimpleName() + "' created (total number: "
                        + ++m_numCreatedEntityProxies + ", cached: " + m_entityProxyMap.size() + ")");
            }
            return proxy;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <E extends GatewayEntity, P extends EntityProxy<E>> boolean update(final E oldEntity, final E newEntity,
        final Class<P> proxyClass) {
        if (oldEntity == newEntity) {
            return false;
        }
        Pair<Integer, Class<EntityProxy>> oldKey =
            new Pair<Integer, Class<EntityProxy>>(System.identityHashCode(oldEntity), (Class<EntityProxy>)proxyClass);
        Pair<Integer, Class<EntityProxy>> newKey =
            new Pair<Integer, Class<EntityProxy>>(System.identityHashCode(newEntity), (Class<EntityProxy>)proxyClass);
        EntityProxy entityProxy = m_entityProxyMap.get(oldKey);
        if (entityProxy != null) {
            entityProxy.update(newEntity);
            m_entityProxyMap.put(newKey, entityProxy);
            m_entityProxyMap.remove(oldKey);
            return true;
        }
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <E extends GatewayEntity, P extends EntityProxy<E>> void postUpdate(final E entity,
        final Class<P> proxyClass) {
        Pair<Integer, Class<EntityProxy>> key =
                new Pair<Integer, Class<EntityProxy>>(System.identityHashCode(entity), (Class<EntityProxy>)proxyClass);
        EntityProxy ep;
        if((ep = m_entityProxyMap.get(key)) != null) {
            ep.postUpdate();
        }
    }
}
