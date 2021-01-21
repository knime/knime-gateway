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
 *   Jan 20, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui.service.operations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.knime.core.node.workflow.WorkflowContext;
import org.knime.core.node.workflow.WorkflowCreationHelper;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowPersistor;
import org.knime.core.util.FileUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.TranslateOperationEnt;
import org.knime.gateway.api.webui.entity.TranslateOperationEnt.TranslateOperationEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowOperationEnt.KindEnum;
import org.knime.gateway.api.webui.entity.WorkflowOperationEnt.WorkflowOperationEntBuilder;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.impl.webui.service.WorkflowKey;

/**
 * Tests {@link WorkflowOperations}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowOperationsTest {

    /**
     * Mainly tests the expected sizes of the undo- and redo-stacks after calling apply, undo, redo or
     * disposeUndoAndRedoStacks.
     */
    @Test
    public void testUndoAndRedoStackSizes() throws Exception {
        WorkflowProject wp = createEmptyWorkflowProject();

        WorkflowOperations ops = new WorkflowOperations(5);
        TranslateOperationEnt operationEntity = builder(TranslateOperationEntBuilder.class).setKind(KindEnum.TRANSLATE)
            .setPosition(builder(XYEntBuilder.class).setX(0).setY(0).build()).build();
        WorkflowKey wfKey = new WorkflowKey(wp.getID(), NodeIDEnt.getRootID());

        assertThrows(OperationNotAllowedException.class, () -> ops.undo(wfKey));
        assertThrows(OperationNotAllowedException.class, () -> ops.redo(wfKey));

        ops.apply(wfKey, operationEntity);
        ops.apply(wfKey, operationEntity);
        ops.apply(wfKey, operationEntity);
        ops.apply(wfKey, operationEntity);
        ops.apply(wfKey, operationEntity);
        ops.apply(wfKey, operationEntity);
        assertThat(ops.getUndoStackSize(wfKey), is(5));
        assertThat(ops.getRedoStackSize(wfKey), is(-1));
        assertThat(ops.canUndo(wfKey), is(true));
        assertThat(ops.canRedo(wfKey), is(false));
        assertThrows(OperationNotAllowedException.class, () -> ops.redo(wfKey));

        ops.undo(wfKey);
        ops.undo(wfKey);
        assertThat(ops.getUndoStackSize(wfKey), is(3));
        assertThat(ops.getRedoStackSize(wfKey), is(2));
        assertThat(ops.canUndo(wfKey), is(true));
        assertThat(ops.canRedo(wfKey), is(true));

        ops.redo(wfKey);
        assertThat(ops.getUndoStackSize(wfKey), is(4));
        assertThat(ops.getRedoStackSize(wfKey), is(1));
        assertThat(ops.canUndo(wfKey), is(true));
        assertThat(ops.canRedo(wfKey), is(true));

        ops.undo(wfKey);
        ops.undo(wfKey);
        ops.undo(wfKey);
        ops.undo(wfKey);
        assertThat(ops.getUndoStackSize(wfKey), is(0));
        assertThat(ops.getRedoStackSize(wfKey), is(5));
        assertThat(ops.canUndo(wfKey), is(false));
        assertThat(ops.canRedo(wfKey), is(true));
        assertThrows(OperationNotAllowedException.class, () -> ops.undo(wfKey));

        assertThrows(OperationNotAllowedException.class,
            () -> ops.apply(null, builder(WorkflowOperationEntBuilder.class).setKind(KindEnum.TRANSLATE).build()));

        ops.redo(wfKey);
        assertThat(ops.getUndoStackSize(wfKey), is(1));
        assertThat(ops.getRedoStackSize(wfKey), is(4));
        ops.disposeUndoAndRedoStacks(wfKey.getProjectId());
        assertThat(ops.getUndoStackSize(wfKey), is(-1));
        assertThat(ops.getRedoStackSize(wfKey), is(-1));

        WorkflowProjectManager.removeWorkflowProject(wp.getID());
        WorkflowManager.ROOT.removeProject(wp.openProject().getID());
    }

    private static WorkflowProject createEmptyWorkflowProject() throws IOException {
        File dir = FileUtil.createTempDir("workflow");
        File workflowFile = new File(dir, WorkflowPersistor.WORKFLOW_FILE);
        if (workflowFile.createNewFile()) {
            WorkflowCreationHelper creationHelper = new WorkflowCreationHelper();
            WorkflowContext.Factory fac = new WorkflowContext.Factory(workflowFile.getParentFile());
            creationHelper.setWorkflowContext(fac.createContext());

            WorkflowManager wfm = WorkflowManager.ROOT.createAndAddProject("workflow", creationHelper);
            String id = "wfId";
            WorkflowProject workflowProject = new WorkflowProject() {

                @Override
                public String getName() {
                    return "workflow";
                }

                @Override
                public String getID() {
                    return id;
                }

                @Override
                public WorkflowManager openProject() {
                    return wfm;
                }

            };
            WorkflowProjectManager.addWorkflowProject(id, workflowProject);
            return workflowProject;
        } else {
            throw new IllegalStateException("Creating empty workflow failed");
        }
    }

}
