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
 *   Apr 17, 2020 (hornm): created
 */
package com.knime.gateway.remote.service.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.knime.core.node.workflow.WorkflowManager;

import com.knime.gateway.entity.ExecutionStatisticsEnt;
import com.knime.gateway.service.AbstractGatewayServiceTestHelper.TestWorkflow;
import com.knime.gateway.testing.helper.TestUtil;

/**
 * Tests {@link WizardExecutionStatistics}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WizardExecutionStatisticsTest {

    /**
     * Tests the expected node executions count of the {@link ExecutionStatisticsEnt#getTotalNodeExecutionsCount()} as
     * returned by {@link WizardExecutionStatistics#getUpdatedStatistics(WorkflowManager)}.
     *
     * @throws Exception if something goes wrong
     */
    @Test
    public void testTotalNodeExecutionsCount() throws Exception {
        WorkflowManager wfm = TestUtil.loadWorkflow(TestWorkflow.NODE_EXECUTIONS_COUNT.getUrlFolder());
        WizardExecutionStatistics wes = new WizardExecutionStatistics();
        wes.resetStatisticsToWizardPage(null, wfm);
        ExecutionStatisticsEnt stats = wes.getUpdatedStatistics(wfm);
        assertThat("wrong node executions count", stats.getTotalNodeExecutionsCount(), is(10));

        wes.resetStatisticsToWizardPage(wfm.getID().createChild(9), wfm);
        stats = wes.getUpdatedStatistics(wfm);
        assertThat("wrong node executions count", stats.getTotalNodeExecutionsCount(), is(23));
        assertThat("wrong executed node count", stats.getNodesExecuted().size(), is(0));

        wfm.executeUpToHere(wfm.getID().createChild(17));
        wfm.executeUpToHere(wfm.getID().createChild(19));
        wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        stats = wes.getUpdatedStatistics(wfm);
        assertThat("wrong node executions count", stats.getTotalNodeExecutionsCount(), is(16));
        assertThat("wrong executed node count", stats.getNodesExecuted().size(), is(13));

        wfm.resetAndConfigureNode(wfm.getID().createChild(17));
        wes.resetStatisticsToWizardPage(wfm.getID().createChild(17), wfm);
        wfm.executeUpToHere(wfm.getID().createChild(26));
        wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        stats = wes.getUpdatedStatistics(wfm);
        assertThat("wrong node executions count", stats.getTotalNodeExecutionsCount(), is(16));
        assertThat("wrong executed node count", stats.getNodesExecuted().size(), is(16));

        // wizard page in nested loop (2x)
        // if loop is not finished, yet
        wes.resetStatisticsToWizardPage(wfm.getID().createChild(26), wfm);
        stats = wes.getUpdatedStatistics(wfm);
        assertThat("wrong node executions count", stats.getTotalNodeExecutionsCount(), is(13));
        // if inner loop is finished (same as above since nested loops are not supported, yet)
        wfm.executeUpToHere(wfm.getID().createChild(34));
        wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        stats = wes.getUpdatedStatistics(wfm);
        assertThat("wrong node executions count", stats.getTotalNodeExecutionsCount(), is(13));
        // if outer loop is finished
        wfm.executeUpToHere(wfm.getID().createChild(39));
        wfm.waitWhileInExecution(5, TimeUnit.SECONDS);
        stats = wes.getUpdatedStatistics(wfm);
        assertThat("wrong node executions count", stats.getTotalNodeExecutionsCount(), is(10));

        // last nodes
        wes.resetStatisticsToWizardPage(wfm.getID().createChild(35), wfm);
        stats = wes.getUpdatedStatistics(wfm);
        assertThat("wrong node executions count", stats.getTotalNodeExecutionsCount(), is(7));
    }
}
