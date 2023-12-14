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
 */
package org.knime.gateway.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LockFailedException;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public class CoreUtilTest {

    private static final String BASE_PATH = "/files/testflows/";

    private static final String CONTAINER_NODES_WF = "ContainerNodes";

    private WorkflowManager getWorkflowManager(final String fileName) throws IOException, InvalidSettingsException,
        CanceledExecutionException, UnsupportedWorkflowVersionException, LockFailedException {
        return WorkflowManagerUtil.loadWorkflow(CoreUtil.resolveToFile(BASE_PATH + fileName, this.getClass()));
    }

    /**
     * @see CoreUtil#getContainerType(NodeID, WorkflowManager)
     * @throws Exception
     */
    @Test
    public void testGetContainerType() throws Exception {
        var wfm = getWorkflowManager(CONTAINER_NODES_WF);
        var emptyMetanodeId = wfm.getID().createChild(2);
        var emptyComponentId = wfm.getID().createChild(3);
        var someNativeNode = wfm.getID().createChild(4);

        assertThat(CoreUtil.getContainerType(emptyMetanodeId, wfm).orElseThrow())
            .isEqualTo(CoreUtil.ContainerType.METANODE);

        assertThat(CoreUtil.getContainerType(emptyComponentId, wfm).orElseThrow())
            .isEqualTo(CoreUtil.ContainerType.COMPONENT);

        assertThat(CoreUtil.getContainerType(someNativeNode, wfm)).isPresent();

        CoreUtil.cancelAndCloseLoadedWorkflow(wfm);
    }

    /**
     * @see CoreUtil#getContainedWfm(NodeID, WorkflowManager)
     * @throws Exception
     */
    @Test
    public void testGetContainedWfm() throws Exception {
        var wfm = getWorkflowManager(CONTAINER_NODES_WF);
        var emptyMetanodeId = wfm.getID().createChild(2);
        var emptyComponentId = wfm.getID().createChild(3);
        var someNativeNode = wfm.getID().createChild(4);

        assertThat(CoreUtil.getContainedWfm(emptyMetanodeId, wfm)).isPresent();
        assertThat(CoreUtil.getContainedWfm(emptyComponentId, wfm)).isPresent();
        assertThat(CoreUtil.getContainedWfm(someNativeNode, wfm)).isPresent();

        CoreUtil.cancelAndCloseLoadedWorkflow(wfm);
    }

}
