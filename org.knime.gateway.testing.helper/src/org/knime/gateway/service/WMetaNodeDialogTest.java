/*
 * ------------------------------------------------------------------------
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
 */
package org.knime.gateway.service;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.knime.gateway.api.entity.GatewayEntity;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.service.NodeService;
import org.knime.gateway.api.service.util.ServiceExceptions;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests the necessary parts for the configuration of components.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WMetaNodeDialogTest extends AbstractGatewayServiceTestHelper {

	/**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param serviceProvider
     * @param entityResultChecker
     */
    public WMetaNodeDialogTest(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader) {
        super("wmetanodedialog", serviceProvider, entityResultChecker, workflowLoader);
    }

    /**
     * Tests essentially the {@link NodeService#getWMetaNodeDialog(java.util.UUID, String)} endpoint.
     *
     * @throws Exception if an error occurs
     */
    public void testGetWMetaNodeDialog() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.QUICKFORMS);

        GatewayEntity entity = ns().getWMetaNodeDialog(wfId, new NodeIDEnt(19));
        cr(entity, "wmetanodedialog_19");

        //what if the node to get the meta node dialog for is not a component?
        try {
            ns().getWMetaNodeDialog(wfId, new NodeIDEnt(1));
            fail("Expected a ServiceException to be thrown");
        } catch (ServiceExceptions.InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("The node the dialog is requested for is not a component"));
        }
    }
}
