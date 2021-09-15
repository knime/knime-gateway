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
 *   Oct 23, 2020 (hornm): created
 */
package org.knime.gateway.impl.jsonrpc.ports;

import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.webui.data.rpc.NodePortRpcServerFactory;
import org.knime.core.webui.data.rpc.RpcServer;
import org.knime.core.webui.data.rpc.json.impl.JsonRpcSingleServer;
import org.knime.gateway.api.webui.util.BuildInWebPortViewType;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Abstract {@link NodePortRpcServerFactory}-implementation for build-in port types (see {@link BuildInWebPortViewType})
 * based on json-rpc.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
abstract class AbstractPortJsonRpcServerFactory<S> implements NodePortRpcServerFactory {

    private final BuildInWebPortViewType m_portType;

    protected AbstractPortJsonRpcServerFactory(final BuildInWebPortViewType portType) {
        m_portType = portType;
    }

    private static ObjectMapper objectMapper = createObjectMapper();

    /**
     * Publicly visible only for testing.
     *
     * @return a new object mapper instance
     */
    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        return mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCompatible(final PortType ptype) {
        return BuildInWebPortViewType.getPortViewTypeFor(ptype).map(t -> t == m_portType).orElse(Boolean.FALSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RpcServer createRpcServer(final NodeOutPort port) {
        return new JsonRpcSingleServer<S>(createService(port), objectMapper);
    }

    protected abstract S createService(NodeOutPort port);

}
