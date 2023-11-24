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
 *   Oct 5, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.List;

import org.junit.Test;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.extension.NodeFactoryProvider;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt.TranslateCommandEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.testing.helper.webui.TranslateCommandTestHelper;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests some aspects of the {@link Translate}-command which can't be covered by the {@link TranslateCommandTestHelper}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class TranslateTest {

    /**
     * Makes sure that nodes aren't marked dirty after the translate operation. Because it's not considered a direct
     * manipulation of the node which requires to save the node's data again (NXT-2080).
     *
     * @throws Exception
     */
    @Test
    public void testThatNodeAreNotDirtyAfterTranslate() throws Exception {
        var wp = WorkflowCommandsTest.createEmptyWorkflowProject();
        var wfm = wp.loadWorkflowManager();
        var nc = WorkflowManagerUtil.createAndAddNode(wfm, NodeFactoryProvider.getInstance() //
            .getNodeFactory("org.knime.base.node.util.sampledata.SampleDataNodeFactory").get());
        nc.setUIInformation(NodeUIInformation.builder().setNodeLocation(0, 0, 0, 0).build());
        var path = wfm.getContextV2().getExecutorInfo().getLocalWorkflowPath();
        wfm.save(path.toFile(), new ExecutionMonitor(), true);
        assertThat(nc.isDirty(), is(false));

        TranslateCommandEnt translate = builder(TranslateCommandEntBuilder.class)
            .setNodeIds(List.of(new NodeIDEnt(nc.getID()))).setKind(KindEnum.TRANSLATE)
            .setTranslation(builder(XYEntBuilder.class).setX(10).setY(10).build()).build();
        var commands = new WorkflowCommands(5);
        commands.execute(new WorkflowKey(wp.getID(), NodeIDEnt.getRootID()), translate);
        assertThat(nc.getUIInformation().getBounds(), is(new int[]{10, 10, 0, 0}));
        assertThat(nc.isDirty(), is(false));

        WorkflowCommandsTest.disposeWorkflowProject(wp);
    }

}
