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
 *   Jul 30, 2025 (motacilla): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.function.FailableSupplier;
import org.junit.Assert;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.service.GatewayException;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt.ContainerTypeEnum;
import org.knime.gateway.api.webui.entity.CollapseResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ConvertContainerResultEnt;
import org.knime.gateway.api.webui.entity.ExpandCommandEnt;
import org.knime.gateway.api.webui.entity.ExpandResultEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Test {@link CollapseCommandEnt} and {@link ExpandCommandEnt} command implementations.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
@SuppressWarnings("javadoc")
public class CollapseExpandCommandsTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    public CollapseExpandCommandsTestHelper(final ResultChecker entityResultChecker,
        final ServiceProvider serviceProvider, final WorkflowLoader workflowLoader,
        final WorkflowExecutor workflowExecutor) {
        super(CollapseExpandCommandsTestHelper.class, entityResultChecker, serviceProvider, workflowLoader,
            workflowExecutor);
    }

    public void testCollapseConfiguredToMetanode() throws Exception {
        testCollapseConfigured(CollapseCommandEnt.ContainerTypeEnum.METANODE);
    }

    public void testCollapseConfiguredToComponent() throws Exception {
        testCollapseConfigured(CollapseCommandEnt.ContainerTypeEnum.COMPONENT);
    }

    public void testCollapseExecutingToMetanode() throws Exception {
        testCollapseExecuting(CollapseCommandEnt.ContainerTypeEnum.METANODE);
    }

    public void testCollapseExecutingToComponent() throws Exception {
        testCollapseExecuting(CollapseCommandEnt.ContainerTypeEnum.COMPONENT);
    }

    public void testCollapseResettableToMetanode() throws Exception {
        testCollapseResettable(CollapseCommandEnt.ContainerTypeEnum.METANODE);
    }

    public void testCollapseResettableToComponent() throws Exception {
        testCollapseResettable(CollapseCommandEnt.ContainerTypeEnum.COMPONENT);
    }

    public void testCollapseResultMetanode() throws Exception {
        testCollapseResult(CollapseCommandEnt.ContainerTypeEnum.METANODE);
    }

    public void testCollapseResultComponent() throws Exception {
        testCollapseResult(CollapseCommandEnt.ContainerTypeEnum.COMPONENT);
    }

    /**
     * Tests that the case of collapsing 'nothing' (i.e. no nodes nor workflow annotations given) into a metanode is
     * handled properly.
     */
    public void testCollapseNothingIntoMetanode() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        List<NodeIDEnt> nodesToCollapseEnts = Collections.emptyList();
        List<AnnotationIDEnt> annotsToCollapseEnts = Collections.emptyList();
        var commandEnt = buildCollapseCommandEnt(nodesToCollapseEnts, annotsToCollapseEnts, ContainerTypeEnum.METANODE);
        assertThrows(ServiceCallException.class, () -> ws().executeWorkflowCommand(wfId, getRootID(), commandEnt));
    }

    public void testExpandConfiguredMetanode() throws Exception {
        var configuredMetanode = 14;
        testExpandConfigured(configuredMetanode);
    }

    public void testExpandConfiguredComponent() throws Exception {
        var configuredComponent = 15;
        testExpandConfigured(configuredComponent);
    }

    public void testExpandResettableMetanode() throws Exception {
        var resettableMetanode = 13;
        testExpandResettable(resettableMetanode);
    }

    public void testExpandResettableComponent() throws Exception {
        var resettableComponent = 10;
        testExpandResettable(resettableComponent);
    }

    public void testExpandExecutingMetanode() throws Exception {
        var metanodeWithExecutingSuccessor = 20;
        var metanodeExecutingSuccessor = 19;
        testExpandExecuting(metanodeWithExecutingSuccessor, metanodeExecutingSuccessor);
    }

    public void testExpandExecutingComponent() throws Exception {
        var componentWithExecutingSuccessor = 22;
        var componentExecutingSuccessor = 21;
        testExpandExecuting(componentWithExecutingSuccessor, componentExecutingSuccessor);
    }

    public void testExpandResultMetanode() throws Exception {
        var configuredMetanode = 14;
        testExpandResult(configuredMetanode);
    }

    public void testExpandResultComponent() throws Exception {
        var configuredComponent = 15;
        testExpandResult(configuredComponent);
    }

    public void testExpandLockedMetanode() throws Exception {
        var lockedMetanode = 27;
        assertExpandLocked(lockedMetanode);
    }

    public void testExpandLockedComponent() throws Exception {
        var lockedComponent = 28;
        assertExpandLocked(lockedComponent);
    }

    private void testExpandResettable(final int nodeToExpand) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var nodeToExpandEnt = new NodeIDEnt(nodeToExpand);

        WorkflowEnt wfEnt = ws().getWorkflow(wfId, getRootID(), null, Boolean.TRUE).getWorkflow();
        assertTrue("Expect selected nodes to have allowed action for expand set to 'reset required'",
            getAllowedActionsOfNodes(List.of(nodeToExpandEnt), wfEnt).stream()
                .anyMatch(actions -> actions.getCanExpand() == AllowedNodeActionsEnt.CanExpandEnum.RESETREQUIRED));

        WorkflowEnt rootWfEnt = ws().getWorkflow(wfId, getRootID(), null, Boolean.TRUE).getWorkflow();
        assertNodesPresent("Expect container to be still be in root workflow", rootWfEnt, List.of(nodeToExpandEnt));

        var commandEnt = buildExpandCommandEnt(nodeToExpandEnt);
        ExpandResultEnt responseEnt = (ExpandResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), commandEnt);
        assertExpanded(wfId, getRootID(), commandEnt, responseEnt);
    }

    private void testExpandExecuting(final int container, final int successor) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var containerEnt = new NodeIDEnt(container);
        executeAndWaitUntilExecuting(wfId, successor);

        WorkflowEnt rootWfEnt = ws().getWorkflow(wfId, getRootID(), null, Boolean.TRUE).getWorkflow();

        assertTrue("Expect selected nodes to have allowed action for expand to be false",
            getAllowedActionsOfNodes(List.of(containerEnt), rootWfEnt).stream()
                .anyMatch(actions -> actions.getCanCollapse() == AllowedNodeActionsEnt.CanCollapseEnum.FALSE));

        assertNodesPresent("Expect nodes to still be in root workflow", rootWfEnt, List.of(containerEnt));
    }

    private void testExpandConfigured(final int nodeToExpand) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var nodeToExpandEnt = new NodeIDEnt(nodeToExpand);

        testExpandConfigured(wfId, getRootID(), nodeToExpandEnt);
    }

    private void testExpandConfigured(final String projectId, final NodeIDEnt wfId, final NodeIDEnt nodeToExpandEnt)
        throws Exception {
        WorkflowEnt unchangedWfEnt = ws().getWorkflow(projectId, wfId, null, true).getWorkflow();
        assertTrue("Expect selected nodes to have allowed action for collapse set to true",
            getAllowedActionsOfNodes(List.of(nodeToExpandEnt), unchangedWfEnt).stream()
                .anyMatch(actions -> actions.getCanExpand() == AllowedNodeActionsEnt.CanExpandEnum.TRUE));

        var commandEnt = buildExpandCommandEnt(nodeToExpandEnt);
        ExpandResultEnt commandResponseEnt = (ExpandResultEnt)ws().executeWorkflowCommand(projectId, wfId, commandEnt);
        assertExpanded(projectId, wfId, commandEnt, commandResponseEnt);

        ws().undoWorkflowCommand(projectId, wfId);
        WorkflowEnt parentWfAfterUndo = ws().getWorkflow(projectId, wfId, null, Boolean.TRUE).getWorkflow();
        assertNodesPresent("Container expected to be back in parent workflow after undo", parentWfAfterUndo,
            List.of(nodeToExpandEnt));
        assertNodesNotPresent("Expanded nodes assumed to no longer be in parent workflow", parentWfAfterUndo,
            commandResponseEnt.getExpandedNodeIds());

        ws().redoWorkflowCommand(projectId, wfId);
        assertExpanded(projectId, wfId, commandEnt, commandResponseEnt);
    }

    private void assertExpanded(final String projectId, final NodeIDEnt wfId, final ExpandCommandEnt commandEnt,
        final ExpandResultEnt responseEnt) throws Exception {
        assertExpanded(() -> ws().getWorkflow(projectId, wfId, null, true).getWorkflow(), commandEnt, responseEnt);
    }

    static void assertExpanded(final FailableSupplier<WorkflowEnt, GatewayException> workflowEntSupplier,
        final ExpandCommandEnt commandEnt, final ExpandResultEnt responseEnt) throws Exception {
        var workflowEnt = workflowEntSupplier.get();
        assertNodesNotPresent("Expanded node expected to have been removed", workflowEnt,
            List.of(commandEnt.getNodeId()));
        assertNodesPresent("Nodes from container expected to appear in parent workflow", workflowEnt,
            responseEnt.getExpandedNodeIds());
        assertAnnotationsPresent("Annotations from container expected to appear in parent workflow", workflowEnt,
            responseEnt.getExpandedAnnotationIds());
    }

    private void testCollapseExecuting(final CollapseCommandEnt.ContainerTypeEnum containerType) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var waitNode = 16;
        var nodesToCollapseInts = List.of(5, 3);
        var nodesToCollapseEnts = nodesToCollapseInts.stream().map(NodeIDEnt::new).toList();

        executeAndWaitUntilExecuting(wfId, waitNode);
        WorkflowEnt rootWfEnt = ws().getWorkflow(wfId, getRootID(), null, Boolean.TRUE).getWorkflow();

        assertTrue("Expect selected nodes to have allowed action for collapse to be false",
            getAllowedActionsOfNodes(nodesToCollapseEnts, rootWfEnt).stream()
                .anyMatch(actions -> actions.getCanCollapse() == AllowedNodeActionsEnt.CanCollapseEnum.FALSE));

        var commandEnt = buildCollapseCommandEnt(nodesToCollapseEnts, Collections.emptyList(), containerType);
        var exceptionMessage =
            assertThrows(ServiceCallException.class, () -> ws().executeWorkflowCommand(wfId, getRootID(), commandEnt))
                .getMessage();
        assertThat(exceptionMessage, containsString("Cannot move all selected nodes (successor executing?)."));
    }

    /**
     * Test that the command result of the collapse command contains the required fields. Does not test synchronisation
     * between workflow snapshots and command results or correctness of other contents of the result.
     *
     * @param containerType The kind of container node to test
     */
    private void testCollapseResult(final CollapseCommandEnt.ContainerTypeEnum containerType) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var nodesToCollapseInts = List.of(5, 3);
        var annotsToCollapseInts = List.of(0, 1);
        var nodesToCollapseEnts = nodesToCollapseInts.stream().map(NodeIDEnt::new).collect(Collectors.toList());
        var annotsToCollapseEnts =
            annotsToCollapseInts.stream().map(i -> new AnnotationIDEnt(getRootID(), i)).collect(Collectors.toList());
        var commandEnt = buildCollapseCommandEnt(nodesToCollapseEnts, annotsToCollapseEnts, containerType);

        // Call `getWorkflow` to trigger initialisation/update of latest snapshot ID.
        ws().getWorkflow(wfId, getRootID(), null, false).getWorkflow();
        var result0 = ws().executeWorkflowCommand(wfId, getRootID(), commandEnt);
        assertCollapseResult(result0, "0");

        ws().getWorkflow(wfId, getRootID(), null, false).getWorkflow();
        ws().undoWorkflowCommand(wfId, getRootID()); // no result to inspect

        ws().getWorkflow(wfId, getRootID(), null, false).getWorkflow();
        var result2 = ws().executeWorkflowCommand(wfId, getRootID(), commandEnt);
        assertCollapseResult(result2, "2");
    }

    private static void assertCollapseResult(final CommandResultEnt resultEnt, final String expectedSnapshotId)
        throws Exception {
        assertEquals(expectedSnapshotId, resultEnt.getSnapshotId());
        if (resultEnt instanceof CollapseResultEnt) {
            var collapseResultEnt = (CollapseResultEnt)resultEnt;
            assertNotNull(collapseResultEnt.getNewNodeId());
        } else if (resultEnt instanceof ConvertContainerResultEnt) {
            var convertResultEnt = (ConvertContainerResultEnt)resultEnt;
            assertNotNull(convertResultEnt.getConvertedNodeId());
        } else {
            throw new NoSuchElementException("Unexpected result entity");
        }
    }

    /**
     * Test that the command result of the expand command contains the required fields. Does not test synchronisation
     * between workflow snapshots and command results or correctness of other contents of the result.
     */
    private void testExpandResult(final int nodeToExpand) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var nodeToExpandEnt = new NodeIDEnt(nodeToExpand);
        var commandEnt = buildExpandCommandEnt(nodeToExpandEnt);

        // Call `getWorkflow` to trigger initialisation/update of latest snapshot ID.
        ws().getWorkflow(wfId, getRootID(), null, false).getWorkflow();
        var result0 = (ExpandResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), commandEnt);
        assertExpandResponse(result0, "0");

        ws().getWorkflow(wfId, getRootID(), null, false).getWorkflow();
        ws().undoWorkflowCommand(wfId, getRootID());

        ws().getWorkflow(wfId, getRootID(), null, false).getWorkflow();
        var result2 = (ExpandResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), commandEnt);
        assertExpandResponse(result2, "2");

    }

    private static void assertExpandResponse(final ExpandResultEnt resultEnt, final String expectedSnapshotId) {
        assertEquals(expectedSnapshotId, resultEnt.getSnapshotId());
        assertNotNull(resultEnt.getExpandedNodeIds());
        assertNotNull(resultEnt.getExpandedAnnotationIds());
    }

    private void testCollapseConfigured(final CollapseCommandEnt.ContainerTypeEnum containerType) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var nodesToCollapseInts = List.of(5, 3);
        var annotsToCollapseInts = List.of(0, 1);
        var nodesToCollapseEnts = nodesToCollapseInts.stream().map(NodeIDEnt::new).collect(Collectors.toList());
        var annotsToCollapseEnts =
            annotsToCollapseInts.stream().map(i -> new AnnotationIDEnt(getRootID(), i)).collect(Collectors.toList());

        WorkflowEnt unchangedWfEnt = ws().getWorkflow(wfId, getRootID(), null, true).getWorkflow();
        Set<String> annotationContents = unchangedWfEnt.getWorkflowAnnotations().stream()//
            .map(annotation -> annotation.getText().getValue())//
            .collect(Collectors.toSet());

        assertTrue("Expect selected nodes to have allowed action for collapse set to true",
            getAllowedActionsOfNodes(nodesToCollapseEnts, unchangedWfEnt).stream()
                .anyMatch(actions -> actions.getCanCollapse() == AllowedNodeActionsEnt.CanCollapseEnum.TRUE));

        var commandEnt = buildCollapseCommandEnt(nodesToCollapseEnts, annotsToCollapseEnts, containerType);
        var commandResponseEnt = ws().executeWorkflowCommand(wfId, getRootID(), commandEnt);
        var newNode = getNewNodeId(commandResponseEnt);

        assertCollapsed(wfId, commandEnt, commandResponseEnt, annotationContents);

        ws().undoWorkflowCommand(wfId, getRootID());

        WorkflowEnt parentWfEnt = ws().getWorkflow(wfId, getRootID(), null, true).getWorkflow();
        assertNodesPresent("Nodes expected to be back in parent workflow after undo of collapse", parentWfEnt,
            nodesToCollapseEnts);
        // after undo, annotations will re-appear with new ids -- instead compare contents
        assertAnnotationContentsPresent("Annotation contents expected to remain unchanged", parentWfEnt,
            annotationContents);
        assertNodesNotPresent("Previously created metanode expected to no longer be in parent workflow", parentWfEnt,
            List.of(newNode));

        ws().redoWorkflowCommand(wfId, getRootID());
        assertCollapsed(wfId, commandEnt, commandResponseEnt, annotationContents);
    }

    private static NodeIDEnt getNewNodeId(final CommandResultEnt commandResultEnt) {
        if (commandResultEnt instanceof CollapseResultEnt re) {
            return re.getNewNodeId();
        } else if (commandResultEnt instanceof ConvertContainerResultEnt re) {
            return re.getConvertedNodeId();
        } else {
            throw new NoSuchElementException("Unexpected response entity");
        }
    }

    static List<AllowedNodeActionsEnt> getAllowedActionsOfNodes(final List<NodeIDEnt> nodes,
        final WorkflowEnt wfEnt) {
        return nodes.stream() //
            .map(NodeIDEnt::toString) //
            .map(idStr -> wfEnt.getNodes().get(idStr)) //
            .map(NodeEnt::getAllowedActions) //
            .collect(Collectors.toList());
    }

    private void assertCollapsed(final String wfId, final CollapseCommandEnt commandEnt,
        final CommandResultEnt commandResultEnt, final Set<String> annotationContents) throws Exception {
        var newNode = getNewNodeId(commandResultEnt);
        var nodesToCollapseEnts = commandEnt.getNodeIds();
        var nodesToCollapseInts = nodesToCollapseEnts.stream().map(NodeIDEnt::getNodeIDs)
            .map(idArr -> idArr[idArr.length - 1]).collect(Collectors.toList());
        var annotsToCollapseEnts = commandEnt.getAnnotationIds();

        WorkflowEnt parentWfEnt = ws().getWorkflow(wfId, getRootID(), null, Boolean.TRUE).getWorkflow();

        assertNodesNotPresent("nodes expected to be removed from top-level workflow", parentWfEnt, nodesToCollapseEnts);
        assertAnnotationsNotPresent("annotations expected to be removed from top-level workflow", annotsToCollapseEnts,
            parentWfEnt);

        assertNodesPresent("node in command response expected to be in top-level workflow", parentWfEnt,
            List.of(newNode));

        WorkflowEnt childWfEnt = ws().getWorkflow(wfId, newNode, null, true).getWorkflow();

        var effectiveParentNodeEnt = getParentIdEnt(commandEnt.getContainerType(), newNode);
        assertNodesPresent("Collapsed nodes expected to be child of new node after collapse", childWfEnt,
            nodesToCollapseInts.stream().map(effectiveParentNodeEnt::appendNodeID).collect(Collectors.toList()));
        // annotation ids are not consistent -- check for contents only as a workaround.
        assertAnnotationContentsPresent("Annotation contents expected to be found in child workflow", childWfEnt,
            annotationContents);
    }

    private static NodeIDEnt getParentIdEnt(final CollapseCommandEnt.ContainerTypeEnum containerType,
        final NodeIDEnt parentEnt) {
        if (containerType == CollapseCommandEnt.ContainerTypeEnum.COMPONENT) {
            return parentEnt.appendNodeID(0);
        } else {
            return parentEnt;
        }
    }

    private void testCollapseResettable(final CollapseCommandEnt.ContainerTypeEnum containerType) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var nodesToCollapseInts = List.of(7, 6);
        var nodesToCollapseEnts = nodesToCollapseInts.stream().map(NodeIDEnt::new).collect(Collectors.toList());

        WorkflowEnt wfEnt = ws().getWorkflow(wfId, getRootID(), null, Boolean.TRUE).getWorkflow();
        assertTrue("Expect selected nodes to have allowed action for collapse set to 'reset required'",
            getAllowedActionsOfNodes(nodesToCollapseEnts, wfEnt).stream()
                .anyMatch(actions -> actions.getCanCollapse() == AllowedNodeActionsEnt.CanCollapseEnum.RESETREQUIRED));

        var commandEnt = buildCollapseCommandEnt(nodesToCollapseEnts, Collections.emptyList(), containerType);
        var commandResponseEnt = ws().executeWorkflowCommand(wfId, getRootID(), commandEnt);
        assertCollapsed(wfId, commandEnt, commandResponseEnt, Collections.emptySet());
    }

    private static void assertAnnotationsNotPresent(final String message, final List<AnnotationIDEnt> annots,
        final WorkflowEnt wfEnt) {
        if (annots.isEmpty()) {
            return;
        }
        assertThat(message,
            wfEnt.getWorkflowAnnotations().stream().map(WorkflowAnnotationEnt::getId).collect(Collectors.toList()),
            not(hasItems(annots.toArray(AnnotationIDEnt[]::new))));
    }

    private static void assertAnnotationContentsPresent(final String message, final WorkflowEnt wfEnt,
        final Set<String> annotationContents) {
        if (annotationContents.isEmpty()) {
            return;
        }
        assertEquals(message, annotationContents, wfEnt.getWorkflowAnnotations().stream()
            .map(annotation -> annotation.getText().getValue()).collect(Collectors.toSet()));
    }

    private static void assertAnnotationsPresent(final String message, final WorkflowEnt wfEnt,
        final List<AnnotationIDEnt> annots) {
        if (annots.isEmpty()) {
            return;
        }
        assertThat(message,
            wfEnt.getWorkflowAnnotations().stream().map(WorkflowAnnotationEnt::getId).collect(Collectors.toList()),
            hasItems(annots.toArray(AnnotationIDEnt[]::new)));
    }

    static void assertNodesPresent(final String message, final WorkflowEnt wfEnt, final List<NodeIDEnt> nodes) {
        if (nodes.isEmpty()) {
            return;
        }
        assertThat(message, wfEnt.getNodes().keySet(),
            hasItems(nodes.stream().map(NodeIDEnt::toString).toArray(String[]::new)));
    }

    static void assertNodesNotPresent(final String message, final WorkflowEnt wfEnt,
        final List<NodeIDEnt> nodes) {
        if (nodes.isEmpty()) {
            return;
        }
        assertThat(message, wfEnt.getNodes().keySet(),
            not(hasItems(nodes.stream().map(NodeIDEnt::toString).toArray(String[]::new))));
    }

    static CollapseCommandEnt buildCollapseCommandEnt(final List<NodeIDEnt> nodes,
        final List<AnnotationIDEnt> annotationIds, final CollapseCommandEnt.ContainerTypeEnum containerType) {
        return builder(CollapseCommandEnt.CollapseCommandEntBuilder.class) //
            .setKind(KindEnum.COLLAPSE) //
            .setContainerType(containerType) //
            .setNodeIds(nodes) //
            .setAnnotationIds(annotationIds) //
            .build();
    }

    static ExpandCommandEnt buildExpandCommandEnt(final NodeIDEnt node) {
        return builder(ExpandCommandEnt.ExpandCommandEntBuilder.class) //
            .setKind(KindEnum.EXPAND) //
            .setNodeId(node) //
            .build();
    }

    private void assertExpandLocked(final int nodeToExpand) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var nodeToExpandEnt = new NodeIDEnt(nodeToExpand);

        var expandCommand = buildExpandCommandEnt(nodeToExpandEnt);
        Assert.assertThrows(ServiceCallException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), expandCommand));
    }

}
