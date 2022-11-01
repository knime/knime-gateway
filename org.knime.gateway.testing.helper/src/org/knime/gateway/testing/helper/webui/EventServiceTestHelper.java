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
 *   Mar 26, 2021 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.awaitility.Awaitility;
import org.knime.gateway.api.entity.EntityBuilderManager;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.testing.helper.EventSource;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests for {@link EventService}-endpoints and the events received when performing respective operations.
 *
 * More detailed (snapshot) tests for {@link WorkflowChangedEventEnt}s are carried out somewhere else.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EventServiceTestHelper extends WebUIGatewayServiceTestHelper {

    private final List<Object> m_events = Collections.synchronizedList(new ArrayList<>());
    
    private final String m_patchPatch = "/nodeTemplates/org.knime.base.node.mine.decisiontree2.learner2.DecisionTreeLearnerNodeFactory3";

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     * @param eventSource
     */
    protected EventServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor, final EventSource eventSource) {
        super(EventServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor,
            eventSource);
        setEventConsumer((name, event) -> m_events.add(event));
    }

    /**
     * Tests the patch created if the delete command is executed and un-done. Makes especially sure that<br>
     * - the respective node template is removed and added again on undo<br>
     * - that there are no 'add' patch operations for 'sub-objects' of be added objects (e.g. node annotation)
     *
     * @throws Exception
     */
    public void testExecuteAndUndoDeleteCommandPatches() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        WorkflowSnapshotEnt wf = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE);
        WorkflowChangedEventTypeEnt eventType = EntityBuilderManager.builder(WorkflowChangedEventTypeEntBuilder.class)
            .setProjectId(wfId).setWorkflowId(getRootID()).setSnapshotId(wf.getSnapshotId())
            .setTypeId("WorkflowChangedEventType").build();
        es().addEventListener(eventType);

        var command = WorkflowServiceTestHelper.createDeleteCommandEnt(asList(new NodeIDEnt(5)),
            Collections.emptyList(), Collections.emptyList());
        ws().executeWorkflowCommand(wfId, getRootID(), command);
        var patchPath = "/nodeTemplates/org.knime.base.node.mine.decisiontree2.learner2.DecisionTreeLearnerNodeFactory3";
        var patchOpEnt = waitAndFindPatchOpForPath(patchPath);
        m_events.clear();
        assertThat("unexpected operation", patchOpEnt.getOp(), is(OpEnum.REMOVE));

        ws().undoWorkflowCommand(wfId, getRootID());
        patchOpEnt = waitAndFindPatchOpForPath(patchPath);
        // make sure that 'sub-objects' of an object added as a patch op, aren't added again
        // (e.g. the node annotation of a to be added node)
        assertThatThereIsNoPathForPatchOp("/nodes/root:5/annotation");
        m_events.clear();
        assertThat("unexpected operation", patchOpEnt.getOp(), is(OpEnum.ADD));
        assertThat("patch op value expected", patchOpEnt.getValue(), is(notNullValue()));
        es().removeEventListener(eventType);
    }

    private PatchOpEnt waitAndFindPatchOpForPath(final String path) {
        AtomicReference<PatchOpEnt> res = new AtomicReference<>();
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(200, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            res.set(m_events.stream().flatMap(e -> ((WorkflowChangedEventEnt)e).getPatch().getOps().stream())
                .filter(op -> op.getPath().equals(path)).findFirst().orElse(null));
            assertThat("No patch op found for path " + path, res.get(), is(notNullValue()));
        });
        return res.get();
    }

    private void assertThatThereIsNoPathForPatchOp(final String path) {
        assertFalse("unexpected patch op for path " + path,
            m_events.stream().flatMap(e -> ((WorkflowChangedEventEnt)e).getPatch().getOps().stream())
                .anyMatch(op -> op.getPath().equals(path)));
    }

}
