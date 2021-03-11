/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *   Oct 27, 2020 (hornm): created
 */
package org.knime.gateway.impl.jsonrpc.ports;

import static org.knime.gateway.testing.helper.GatewayServiceTestHelper.resolveToFile;

import java.io.IOException;

import org.junit.Before;
import org.knime.core.node.util.CheckUtils;
import org.knime.gateway.impl.rpc.table.TableService;
import org.knime.gateway.testing.helper.ResultChecker;

/**
 * Abstracts some details of port rpc service tests (such as the {@link TableService}).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class AbstractJsonRpcSerializationTest<S> {

    private ResultChecker m_resultChecker;

    private final Class<S> m_serviceClass;

    private TestJsonRpcClient<S> m_rpcClient;

    /**
     * New test instance.
     *
     * @param serviceClass the service interface class
     */
    protected AbstractJsonRpcSerializationTest(final Class<S> serviceClass) {
        m_serviceClass = serviceClass;
    }

    /**
     * Inits the result checker for snapshot testing.
     *
     * @throws IOException
     */
    @Before
    public void initResultChecker() throws IOException {
        m_resultChecker = new ResultChecker(null, resolveToFile("/files/test_snapshots", this.getClass()));
    }

    /**
     * Creates a new service client instance.
     *
     * @param serviceImpl
     * @return the new service client instance
     */
    protected S createServiceClient(final S serviceImpl) {
        m_rpcClient = new TestJsonRpcClient<>(m_serviceClass, serviceImpl);
        return m_rpcClient.getService();
    }

    /**
     * Checks the last response from the rpc server by comparing it to the snapshot of the provided name
     *
     * @param snapshotName the snapshot to compare the last server response to
     */
    protected void checkLastServerResponse(final String snapshotName) {
        CheckUtils.checkNotNull(m_rpcClient, "No rpc client initialized. Call 'createServiceClient' first.");
        m_resultChecker.checkObject(AbstractJsonRpcSerializationTest.class, snapshotName,
            m_rpcClient.getLastServerResponse());
    }

    /**
     * @return the last server response as json-rpc string
     */
    protected String getLastServerResponse() {
        return m_rpcClient.getLastServerResponse();
    }

}
