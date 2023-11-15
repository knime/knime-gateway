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
package org.knime.gateway.testing.helper.webui;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.CopyCommandEnt;
import org.knime.gateway.api.webui.entity.CopyResultEnt;
import org.knime.gateway.api.webui.entity.CutCommandEnt;
import org.knime.gateway.api.webui.entity.PasteCommandEnt;
import org.knime.gateway.api.webui.entity.PasteResultEnt;
import org.knime.gateway.api.webui.entity.TypedTextEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;
import org.knime.shared.workflow.storage.clipboard.InvalidDefClipboardContentVersionException;
import org.knime.shared.workflow.storage.clipboard.SystemClipboardFormat;
import org.knime.shared.workflow.storage.clipboard.SystemClipboardFormat.ObfuscatorException;
import org.knime.shared.workflow.storage.text.util.ObjectMapperUtil;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Tests the implementations of {@link org.knime.gateway.impl.webui.service.commands.Cut},
 * {@link org.knime.gateway.impl.webui.service.commands.Copy} and
 * {@link org.knime.gateway.impl.webui.service.commands.Paste}.
 */
@SuppressWarnings("javadoc")
public class CutCopyPasteCommandsTestHelper extends WebUIGatewayServiceTestHelper {

    public CutCopyPasteCommandsTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(CutCopyPasteCommandsTestHelper.class, entityResultChecker, serviceProvider, workflowLoader,
            workflowExecutor);
    }

    /**
     * Test Copy command
     *
     * @throws Exception
     */
    public void testExecuteCopyCommand() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var command = buildCopyCommand(asList(new NodeIDEnt(1), new NodeIDEnt(2)),
            asList(new AnnotationIDEnt("root_0"), new AnnotationIDEnt("root_1")));
        // execute command
        var commandResult = (CopyResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), command);
        assertCopyResultValid(commandResult);
    }

    private static CopyCommandEnt buildCopyCommand(final List<NodeIDEnt> nodeIds,
        final List<AnnotationIDEnt> annotationIds) {
        return builder(CopyCommandEnt.CopyCommandEntBuilder.class)//
            .setKind(WorkflowCommandEnt.KindEnum.COPY)//
            .setNodeIds(nodeIds)//
            .setAnnotationIds(annotationIds)//
            .build();
    }

    private static void assertCopyResultValid(final CopyResultEnt copyResult) throws JsonProcessingException,
        IllegalArgumentException, InvalidDefClipboardContentVersionException, ObfuscatorException {
        var clipboardContent = copyResult.getContent();
        var mapper = ObjectMapperUtil.getInstance().getObjectMapper();
        var systemClipboardContent = mapper.readValue(clipboardContent, String.class);
        var defClipboardContent = SystemClipboardFormat.deserialize(systemClipboardContent);
        assertThat("The DefClipboardContent could not be read", defClipboardContent != null);
    }

    /**
     * Test Cut command
     *
     * @throws Exception
     */
    public void testExecuteCutCommand() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var command = buildCutCommand(asList(new NodeIDEnt(1), new NodeIDEnt(2)),
            asList(new AnnotationIDEnt("root_0"), new AnnotationIDEnt("root_1")));
        var nodeKeysBefore = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
        var annKeysBefore = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
        // execute command
        var commandResult = (CopyResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), command);
        var nodeKeysAfterExecution =
            ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
        var annKeysAfterExecution = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
        assertCopyResultValid(commandResult);
        assertThat("We should have less nodes in the workflow after cutting",
            nodeKeysAfterExecution.size() < nodeKeysBefore.size());
        assertThat("We should have less annotations in the workflow after cutting",
            annKeysAfterExecution.size() < annKeysBefore.size());
        assertThat("We should not have more nodes in the workflow after cutting",
            nodeKeysBefore.containsAll(nodeKeysAfterExecution));
        assertThat("We should not have more annotations in the workflow after cutting",
            annKeysBefore.containsAll(annKeysAfterExecution));
        // undo command
        ws().undoWorkflowCommand(wfId, getRootID());
        var nodeKeysAfterUndo = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
        var annKeysAfterUndo = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
        assertEquals("We should have the same nodes as before execution", nodeKeysBefore, nodeKeysAfterUndo);
        assertEquals("We should have the same annotations as before execution", annKeysBefore, annKeysAfterUndo);
        // redo command
        ws().redoWorkflowCommand(wfId, getRootID());
        var nodeKeysAfterRedo = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
        var annKeysAfterRedo = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
        assertEquals("We should have the same nodes as after execution", nodeKeysAfterExecution, nodeKeysAfterRedo);
        assertEquals("We should have the same annotations as after execution", annKeysAfterExecution, annKeysAfterRedo);
    }

    private static CutCommandEnt buildCutCommand(final List<NodeIDEnt> nodeIds,
        final List<AnnotationIDEnt> annotationIds) {
        return builder(CutCommandEnt.CutCommandEntBuilder.class)//
            .setKind(WorkflowCommandEnt.KindEnum.CUT)//
            .setNodeIds(nodeIds)//
            .setAnnotationIds(annotationIds)//
            .build();
    }

    private static Set<String> getAnnotationsKeysFromWorkflow(final WorkflowSnapshotEnt workflow) {
        return workflow.getWorkflow().getWorkflowAnnotations().stream().map(a -> a.getId().toString())
            .collect(Collectors.toSet());
    }

    /**
     * Test that paste command with a target position properly translates inserted elements after translation
     *
     * @throws Exception
     */
    public void testPasteCommandWithTranslation() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.BENDPOINTS);
        var copyCommand = buildCopyCommand(asList(new NodeIDEnt(189), new NodeIDEnt(187)), List.of());
        var clipboardContent =
            ((CopyResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), copyCommand)).getContent();
        var posX = 16;
        var posY = 32;
        var pasteCommand = buildPasteCommand(clipboardContent, List.of(posX, posY));
        ws().executeWorkflowCommand(wfId, getRootID(), pasteCommand);
        var modifiedWorkflow = ws().getWorkflow(wfId, getRootID(), false);
        // only pasting nodes, do not have to check for annotations
        var nodes = modifiedWorkflow.getWorkflow().getNodes().values();
        var minNodeX = nodes.stream().map(nodeEnt -> nodeEnt.getPosition().getX()).min(Comparator.comparingInt(a -> a))
            .orElseThrow();
        var minNodeY = nodes.stream().map(nodeEnt -> nodeEnt.getPosition().getY()).min(Comparator.comparingInt(a -> a))
            .orElseThrow();
        var bendpoints =
            modifiedWorkflow.getWorkflow().getConnections().values().stream().map(ConnectionEnt::getBendpoints) //
                .filter(Objects::nonNull) // null if no bendpoints on connection
                .flatMap(Collection::stream) // flatten
                .collect(Collectors.toSet());
        var minBendpointX = bendpoints.stream().map(XYEnt::getX).min(Comparator.comparingInt(x -> x)).orElseThrow();
        var minBendpointY = bendpoints.stream().map(XYEnt::getY).min(Comparator.comparingInt(y -> y)).orElseThrow();
        var minX = Math.min(minNodeX, minBendpointX);
        var minY = Math.min(minNodeY, minBendpointY);
        assertEquals(posX, minX);
        assertEquals(posY, minY);
    }

    /**
     * Test Paste command
     *
     * @throws Exception
     */
    public void testExecutePasteCommand() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var copyCommand = buildCopyCommand(asList(new NodeIDEnt(1), new NodeIDEnt(2)),
            asList(new AnnotationIDEnt("root_0"), new AnnotationIDEnt("root_1")));
        var clipboardContent =
            ((CopyResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), copyCommand)).getContent();
        // test paste commands
        var pasteCommands =
            List.of(buildPasteCommand(clipboardContent, null), buildPasteCommand(clipboardContent, List.of(16, 32)));
        for (var pasteCommand : pasteCommands) {
            var nodeKeysBefore = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
            var annKeysBefore = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
            // execute command
            var commandResult = ws().executeWorkflowCommand(wfId, getRootID(), pasteCommand);
            assertThat(commandResult.getSnapshotId(), notNullValue());
            assertThat(commandResult.getKind().toString(), is("paste_result"));
            var pasteResult = (PasteResultEnt)commandResult;
            assertThat(pasteResult.getNodeIds(), hasSize(2));
            assertThat(pasteResult.getAnnotationIds(), hasSize(2));
            var nodeKeysAfterExecution =
                ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
            var annKeysAfterExecution =
                getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
            assertThat("We should have more nodes in the workflow after pasting",
                nodeKeysAfterExecution.size() > nodeKeysBefore.size());
            assertThat("We shouldn't have lost any nodes while pasting",
                nodeKeysAfterExecution.containsAll(nodeKeysBefore));
            assertThat("We should have more annotations in the workflow after pasting",
                annKeysAfterExecution.size() > annKeysBefore.size());
            assertThat("We shouldn't have lost any annotations while pasting",
                annKeysAfterExecution.containsAll(annKeysBefore));
            // undo command
            ws().undoWorkflowCommand(wfId, getRootID());
            var nodeKeysAfterUndo = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
            var annKeysAfterUndo = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
            assertEquals("We should have the same nodes as before execution", nodeKeysBefore, nodeKeysAfterUndo);
            assertEquals("We should have the same annotations as before execution", annKeysBefore, annKeysAfterUndo);
            // redo command
            ws().redoWorkflowCommand(wfId, getRootID());
            var nodeKeysAfterRedo = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
            var annKeysAfterRedo = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
            assertEquals("We should have the same nodes as after execution", nodeKeysAfterExecution, nodeKeysAfterRedo);
            assertEquals("We should have the same annotations as after execution", annKeysAfterExecution,
                annKeysAfterRedo);
        }
    }

    /**
     * Test {@link PasteCommandEnt} for annotations especially.
     *
     * @throws Exception
     */
    public void testExecutePasteCommandForAnnotations() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.ANNOTATIONS);
        var workflowId = getRootID();
        var annotationEnts = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getWorkflowAnnotations();
        var annotationIdEnts = annotationEnts.stream().map(WorkflowAnnotationEnt::getId).collect(Collectors.toList());

        assertContentTypesForWorkflowAnnotations(annotationEnts);

        var copyCommand = buildCopyCommand(Collections.emptyList(), annotationIdEnts);
        var clipboardContent =
            ((CopyResultEnt)ws().executeWorkflowCommand(projectId, workflowId, copyCommand)).getContent();
        var pasteCommand = buildPasteCommand(clipboardContent, List.of(32, 64));
        var pasteResult = ((PasteResultEnt)ws().executeWorkflowCommand(projectId, workflowId, pasteCommand));

        var pastedAnnotationIdEnts = pasteResult.getAnnotationIds();
        var annotationEntsAfterPaste =
            ws().getWorkflow(projectId, workflowId, false).getWorkflow().getWorkflowAnnotations();
        var pastedAnnotationEnts = annotationEntsAfterPaste.stream()//
            .filter(annotation -> pastedAnnotationIdEnts.contains(annotation.getId()))//
            .collect(Collectors.toList());

        assertContentTypesForWorkflowAnnotations(pastedAnnotationEnts);
    }

    private static void assertContentTypesForWorkflowAnnotations(final List<WorkflowAnnotationEnt> annotationEnts) {
        var contentTypes = annotationEnts.stream()//
            .map(annotation -> annotation.getText().getContentType())//
            .collect(Collectors.toList());
        assertThat("There should exist at least on 'text/plain' workflow annotation",
            contentTypes.stream().anyMatch(type -> type == TypedTextEnt.ContentTypeEnum.PLAIN), is(true));
        assertThat("There should exist at least on 'text/html' workflow annotation",
            contentTypes.stream().anyMatch(type -> type == TypedTextEnt.ContentTypeEnum.HTML), is(true));
    }

    private static PasteCommandEnt buildPasteCommand(final String clipboardContent, final List<Integer> position) {
        return builder(PasteCommandEnt.PasteCommandEntBuilder.class)//
            .setKind(WorkflowCommandEnt.KindEnum.PASTE)//
            .setContent(clipboardContent)//
            .setPosition(position != null
                ? builder(XYEnt.XYEntBuilder.class).setX(position.get(0)).setY(position.get(1)).build() : null)
            .build();
    }

}
