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

import static com.knime.gateway.entity.EntityBuilderManager.builder;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.FlowObjectStack;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.ui.node.workflow.SingleNodeContainerUI;
import org.knime.core.ui.node.workflow.async.CompletableFutureEx;

import com.knime.enterprise.utility.KnimeServerConstants;
import com.knime.gateway.local.workflow.EntityProxyNodeOutPort.UnsupportedPortObjectSpec;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodeSettingsEnt;
import com.knime.gateway.v0.entity.NodeSettingsEnt.NodeSettingsEntBuilder;
import com.knime.gateway.v0.service.util.ServiceExceptions;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;

/**
 * Entity-proxy class that proxies {@link NodeEnt} and implements {@link SingleNodeContainerUI}.
 *
 * @author Martin Horn, University of Konstanz
 *
 * @param <E> the type of the node entity
 */
abstract class AbstractEntityProxySingleNodeContainer<E extends NodeEnt> extends AbstractEntityProxyNodeContainer<E>
    implements SingleNodeContainerUI {

    private NodeDialogPane m_dialogPane;

    private NodeSettings m_nodeSettings;

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param node
     * @param access
     */
    AbstractEntityProxySingleNodeContainer(final E node, final EntityProxyAccess access) {
        super(node, access);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMemberOfScope() {
        // TODO
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void update(final NodeEnt entity) {
        //update port entities, too -> makes sure that still the very same entity proxy classes are used
        getAccess().updateNodeInPorts(getEntity(), entity);
        getAccess().updateNodeOutPorts(getEntity(), entity);

        //TODO update all other entity proxies referenced from the node container,
        //e.g. node annotation, node connections, etc.

        super.update((E)entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFutureEx<NodeDialogPane, NotConfigurableException> getDialogPaneWithSettingsAsync() {
        ExecutorService exec = Executors.newCachedThreadPool();
        final Future<NodeSettings> f1 = exec.submit(() -> getAccess().getNodeSettings(getEntity()));
        final Future<PortObjectSpec[]> f2 = exec.submit(() -> getAccess().getInputPortObjectSpecs(getEntity()));
        final Future<FlowObjectStack> f3 =
            exec.submit(() -> getAccess().getInputFlowVariableStack(getEntity(), getID()));
        return CompletableFutureEx.supplyAsync(() -> {
            try {
                Future<NodeDialogPane> p = getDialogPaneWithSettings(f1, f2, f3, m_dialogPane, exec);
                if (p != null) {
                    //wait for p to finish
                    m_dialogPane = p.get();
                }
                shutdownExecutorsAndWait(exec);

                NodeSettings nodeSettings = f1.get();
                PortObjectSpec[] portObjectSpecs = f2.get();
                FlowObjectStack flowObjectStack = f3.get();

                for (PortObjectSpec spec : portObjectSpecs) {
                    if (spec instanceof UnsupportedPortObjectSpec) {
                        throw new NotConfigurableException("Port object spec of type "
                            + ((UnsupportedPortObjectSpec)spec).getType().getName() + " not supported in job view.");
                    }
                }

                //cache the node settings
                m_nodeSettings = nodeSettings;

                //get (if not already) and cache the node dialog
                if (m_dialogPane == null) {
                    m_dialogPane =
                        getDialogPaneWithSettings(nodeSettings, portObjectSpecs, flowObjectStack, m_dialogPane);
                }
                return m_dialogPane;
            } catch (NotConfigurableException | InterruptedException | ExecutionException e) {
                throw new CompletionException(e);
            } finally {
                exec.shutdown();
            }
        }, NotConfigurableException.class);
    }

    @Override
    public void applySettingsFromDialog() throws InvalidSettingsException {
        CheckUtils.checkState(hasDialog(), "Node \"%s\" has no dialog", getName());
        // TODO do we need to reset the node first??
        NodeSettings sett = new NodeSettings("node settings");
        NodeContext.pushContext(this);
        try {
            m_dialogPane.finishEditingAndSaveSettingsTo(sett);
        } finally {
            NodeContext.removeLastContext();
        }

        //convert settings into a settings entity
        NodeSettingsEnt settingsEnt = builder(NodeSettingsEntBuilder.class)
            .setJsonContent(JSONConfig.toJSONString(sett, WriterConfig.PRETTY)).build();

        //transfer settings to server
        try {
            getAccess().nodeService().setNodeSettings(getEntity().getRootWorkflowID(), getEntity().getNodeID(),
                settingsEnt);
        } catch (ServiceExceptions.InvalidSettingsException ex) {
            throw new InvalidSettingsException(ex);
        } catch (ServiceExceptions.IllegalStateException | NodeNotFoundException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areDialogSettingsValid() {
        //always return true all the time and validate settings when applied to limit the number of requests
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areDialogAndNodeSettingsEqual() {
        if (m_nodeSettings == null) {
            return false;
        }
        NodeSettings dlgSettings = new NodeSettings("settings");
        try {
            m_dialogPane.finishEditingAndSaveSettingsTo(dlgSettings);
        } catch (InvalidSettingsException e) {
            return false;
        }
        return dlgSettings.equals(m_nodeSettings);
    }

    /**
     * Returns the dialog pane initialized with the node settings etc., just like
     * {@link #getDialogPaneWithSettings(NodeSettings, PortObjectSpec[], FlowObjectStack, NodeDialogPane)}. Allows the
     * parallel download of data required to initialize the dialog.
     *
     * @param nodeSettings the node settings
     * @param portObjectSpecs the node input port specs
     * @param flowObjectStack the node input flow variables
     * @param dialogPane the cached dialog pane (if not called for the first time), otherwise <code>null</code>
     * @param exec the execution service to submit other tasks to to be executed asynchronously
     * @return the initialized node dialog pane (as future!)
     * @throws NotConfigurableException if there are problems with loading the settings into the dialog
     * @throws InterruptedException
     * @throws ExecutionException
     */
    protected Future<NodeDialogPane> getDialogPaneWithSettings(final Future<NodeSettings> nodeSettings,
        final Future<PortObjectSpec[]> portObjectSpecs, final Future<FlowObjectStack> flowObjectStack,
        final NodeDialogPane dialogPane, final ExecutorService exec)
        throws NotConfigurableException, InterruptedException, ExecutionException {
        //optionally to be overridden by subclasses
        return null;
    }

    /**
     * Shuts down the passed executor service and waits for all threads for termination.
     *
     * @param exec the execution service
     * @throws InterruptedException in case of an interruption
     */
    private static void shutdownExecutorsAndWait(final ExecutorService exec) throws InterruptedException {
        exec.shutdown();
        //wait a bit longer than the timeouts of the individual requests
        exec.awaitTermination(KnimeServerConstants.GATEWAY_CLIENT_TIMEOUT + 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Returns the dialog pane initialized with the node settings etc.
     *
     * @param nodeSettings the node settings
     * @param portObjectSpecs the node input port specs
     * @param flowObjectStack the node input flow variables
     * @param dialogPane the cached dialog pane (if not called for the first time), otherwise <code>null</code>
     * @return the initialized node dialog pane
     * @throws NotConfigurableException if there are problems with loading the settings into the dialog
     */
    protected NodeDialogPane getDialogPaneWithSettings(final NodeSettings nodeSettings,
        final PortObjectSpec[] portObjectSpecs, final FlowObjectStack flowObjectStack, final NodeDialogPane dialogPane)
        throws NotConfigurableException {
        throw new IllegalStateException("Implementation error: Method needs to be overridden.");
    }

}
