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
 *   Oct 26, 2020 (hornm): created
 */
package org.knime.gateway.impl.rpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.port.PortType;
import org.knime.core.node.rpc.NodeRpcServerFactory;
import org.knime.core.node.rpc.RpcServer;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeOutPort;

/**
 * Manages and forwards rpc-requests to respective rpc servers provided, e.g., by/for nodes or ports.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 *
 * @noreference This class is not intended to be referenced by clients.
 * @since 4.3
 */
public final class RpcServerManager {

    private static final String NODE_PORT_RPC_SERVER_FACTORY_EXT_ID = "org.knime.gateway.impl.NodePortRpcServerFactory";

    private static List<NodePortRpcServerFactory> nodePortRpcServerFactories;

    private static RpcServerManager instance;

    private final Map<NativeNodeContainer, RpcServer> m_nodeRpcServerCache =
        Collections.synchronizedMap(new WeakHashMap<>());

    private final Map<NodeOutPort, RpcServer> m_portRpcServerCache = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static RpcServerManager getInstance() {
        if (instance == null) {
            instance = new RpcServerManager();
        }
        return instance;
    }

    private RpcServerManager() {
        // singleton
    }

    /**
     * Carries out a remote procedure call by calling the rpc server provided by a node.
     *
     * @param nnc the node which is addressed by the rpc
     * @param remoteProcedureCall the actual remote procedure call encoded in some textual format
     * @return the rpc response
     * @throws IllegalStateException if the referenced node doesn't provide a rpc server
     * @throws IOException if the rpc server can't process the rpc request properly
     */
    public String doRpc(final NativeNodeContainer nnc, final String remoteProcedureCall) throws IOException {
        return doRpc(getRpcServer(nnc), remoteProcedureCall);
    }

    /**
     * Carries out a remote procedure call by calling the rpc server provided by a node port.
     *
     * @param nc the node which port is being addressed
     * @param portIdx the index of the port being addressed
     * @param remoteProcedureCall the actual remote procedure call encoded in some textual format
     * @return the rpc response
     * @throws IllegalStateException thrown if the referenced node port doesn't provide a rpc server
     * @throws IOException if the rpc server can't process the rpc request properly
     */
    public String doRpc(final NodeContainer nc, final int portIdx, final String remoteProcedureCall)
        throws IOException {
        return doRpc(getRpcServer(nc.getOutPort(portIdx)), remoteProcedureCall);
    }

    private static Optional<NodePortRpcServerFactory> getRpcServerFactoryForPort(final PortType ptype) {
        if (nodePortRpcServerFactories == null) {
            nodePortRpcServerFactories = collectNodePortRpcServerFactoriesFromExtension();
        }
        return nodePortRpcServerFactories.stream().filter(f -> f.isCompatible(ptype)).findFirst();
    }

    private static Optional<NodeRpcServerFactory> getRpcServerFactoryForNode(final NodeContainer nc) {
        NativeNodeContainer nativeNodeContainer = (NativeNodeContainer)nc;
        NodeFactory<NodeModel> factory = nativeNodeContainer.getNode().getFactory();
        // TODO can a single and multi rpc server can be used in parallel? (e.g. for backwards-compatibility) //NOSONAR
        if (factory instanceof NodeRpcServerFactory) {
            return Optional.of((NodeRpcServerFactory)factory);
        } else {
            return Optional.empty();
        }
    }

    private static String doRpc(final RpcServer rpcServer, final String remoteProcedureCall) throws IOException {
        try (ByteArrayInputStream request =
            new ByteArrayInputStream(remoteProcedureCall.getBytes(StandardCharsets.UTF_8));
                ByteArrayOutputStream response = new ByteArrayOutputStream()) {

            rpcServer.handleRequest(request, response);

            // if the invoked method returns void, the output stream is empty (unless an error occurs, in which
            // case we want to return a response containing the error message).
            return new String(response.toByteArray(), StandardCharsets.UTF_8.name());
        }
    }

    private RpcServer getRpcServer(final NativeNodeContainer nc) {
        NodeRpcServerFactory factory = getRpcServerFactoryForNode(nc).orElse(null);
        if (factory != null) {
            return m_nodeRpcServerCache.computeIfAbsent(nc,
                nnc -> factory.createRpcServer(nnc.getNode().getNodeModel()));
        } else {
            throw new IllegalStateException("The node with id '" + nc.getID() + "' does not provide a rpc server.");
        }
    }

    private RpcServer getRpcServer(final NodeOutPort port) {
        NodePortRpcServerFactory factory = getRpcServerFactoryForPort(port.getPortType()).orElse(null);
        if (factory != null) {
            return m_portRpcServerCache.computeIfAbsent(port, factory::createRpcServer);
        } else {
            throw new IllegalStateException(
                "The port of type '" + port.getPortType().getName() + "' does not provide a rpc server.");
        }
    }

    private static List<NodePortRpcServerFactory> collectNodePortRpcServerFactoriesFromExtension() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(NODE_PORT_RPC_SERVER_FACTORY_EXT_ID);
        return Stream.of(point.getExtensions()).flatMap(ext -> Stream.of(ext.getConfigurationElements()))
            .map(RpcServerManager::getNodePortRpcServerFactory).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private static NodePortRpcServerFactory getNodePortRpcServerFactory(final IConfigurationElement cfe) {
        try {
            NodePortRpcServerFactory ext = (NodePortRpcServerFactory)cfe.createExecutableExtension("impl");
            NodeLogger.getLogger(RpcServerManager.class).debugWithFormat(
                "Added NodePortRpcServerFactory '%s' from '%s'", ext.getClass().getName(),
                cfe.getContributor().getName());
            return ext;
        } catch (CoreException ex) {
            NodeLogger.getLogger(RpcServerManager.class)
                .error(String.format("Looking for an implementation of the NodePortRpcServerFactory extension point,\n"
                    + "but could not process extension %s: %s", cfe.getContributor().getName(), ex.getMessage()), ex);
        }
        return null;
    }

}
