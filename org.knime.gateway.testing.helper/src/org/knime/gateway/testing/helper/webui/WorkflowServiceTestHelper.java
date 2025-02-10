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
 *   Aug 3, 2020 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.NodeAnnotationData;
import org.knime.core.node.workflow.capture.WorkflowPortObject;
import org.knime.core.util.Pair;
import org.knime.core.util.pathresolve.URIToFileResolve;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.util.EntityUtil;
import org.knime.gateway.api.webui.entity.AddAnnotationResultEnt;
import org.knime.gateway.api.webui.entity.AddBendpointCommandEnt.AddBendpointCommandEntBuilder;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.AddNodeCommandEntBuilder;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.NodeRelationEnum;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt;
import org.knime.gateway.api.webui.entity.AddPortResultEnt;
import org.knime.gateway.api.webui.entity.AddWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.AddWorkflowAnnotationCommandEnt.AddWorkflowAnnotationCommandEntBuilder;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.BoundsEnt.BoundsEntBuilder;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt.ContainerTypeEnum;
import org.knime.gateway.api.webui.entity.CollapseResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ComponentPortDescriptionEnt;
import org.knime.gateway.api.webui.entity.ConvertContainerResultEnt;
import org.knime.gateway.api.webui.entity.ExpandCommandEnt;
import org.knime.gateway.api.webui.entity.ExpandResultEnt;
import org.knime.gateway.api.webui.entity.InsertNodeCommandEnt;
import org.knime.gateway.api.webui.entity.InsertNodeCommandEnt.InsertNodeCommandEntBuilder;
import org.knime.gateway.api.webui.entity.LinkEnt;
import org.knime.gateway.api.webui.entity.LinkEnt.LinkEntBuilder;
import org.knime.gateway.api.webui.entity.MetaNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.NodeIdAndIsExecutedEnt;
import org.knime.gateway.api.webui.entity.NodePortDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.PortCommandEnt;
import org.knime.gateway.api.webui.entity.ProjectMetadataEnt;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;
import org.knime.gateway.api.webui.entity.ReorderWorkflowAnnotationsCommandEnt;
import org.knime.gateway.api.webui.entity.ReorderWorkflowAnnotationsCommandEnt.ActionEnum;
import org.knime.gateway.api.webui.entity.ReorderWorkflowAnnotationsCommandEnt.ReorderWorkflowAnnotationsCommandEntBuilder;
import org.knime.gateway.api.webui.entity.ReplaceNodeCommandEnt;
import org.knime.gateway.api.webui.entity.ReplaceNodeCommandEnt.ReplaceNodeCommandEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt.SpaceItemReferenceEntBuilder;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.TransformMetanodePortsBarCommandEnt.TransformMetanodePortsBarCommandEntBuilder;
import org.knime.gateway.api.webui.entity.TransformMetanodePortsBarCommandEnt.TypeEnum;
import org.knime.gateway.api.webui.entity.TransformWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.TransformWorkflowAnnotationCommandEnt.TransformWorkflowAnnotationCommandEntBuilder;
import org.knime.gateway.api.webui.entity.TypedTextEnt;
import org.knime.gateway.api.webui.entity.TypedTextEnt.ContentTypeEnum;
import org.knime.gateway.api.webui.entity.UpdateComponentLinkInformationCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentLinkInformationCommandEnt.UpdateComponentLinkInformationCommandEntBuilder;
import org.knime.gateway.api.webui.entity.UpdateComponentMetadataCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentOrMetanodeNameCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentOrMetanodeNameCommandEnt.UpdateComponentOrMetanodeNameCommandEntBuilder;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsCommandEnt.UpdateLinkedComponentsCommandEntBuilder;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt;
import org.knime.gateway.api.webui.entity.UpdateLinkedComponentsResultEnt.StatusEnum;
import org.knime.gateway.api.webui.entity.UpdateNodeLabelCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateProjectMetadataCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateProjectMetadataCommandEnt.UpdateProjectMetadataCommandEntBuilder;
import org.knime.gateway.api.webui.entity.UpdateWorkflowAnnotationCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateWorkflowAnnotationCommandEnt.UpdateWorkflowAnnotationCommandEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowAnnotationEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.entity.XYEnt;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.OperationNotAllowedException;
import org.knime.gateway.api.webui.util.WorkflowEntityFactory;
import org.knime.gateway.impl.webui.NodeFactoryProvider;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;
import org.knime.testing.util.URIToFileResolveTestUtil;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test for the endpoints to view/render a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@SuppressWarnings({"java:S112", "java:S1192", "java:S3655", "java:S1874", "javadoc"})
// generic exceptions, repeated string literals, Optional#get without presence check, deprecated classes
public class WorkflowServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    public WorkflowServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(WorkflowServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests to get the workflow.
     */
    public void testGetWorkflow() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        // check un-executed
        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.TRUE).getWorkflow();
        cr(workflow, "workflowent_root");

        // get a metanode's workflow
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(6), Boolean.TRUE).getWorkflow();
        cr(workflow, "workflowent_6");

        // get a component's workflow
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(23), Boolean.FALSE).getWorkflow();
        cr(workflow, "workflowent_23");

        // check executed
        executeWorkflow(wfId);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.TRUE).getWorkflow();
        cr(workflow, "workflowent_root_executed");

        // get a workflow of a linked component
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(183), Boolean.FALSE).getWorkflow();
        cr(workflow, "workflowent_183_linked_component");
    }

    /**
     * Tests to get a workflow of a component workflow.
     */
    public void testGetComponentProjectWorkflow() throws Exception {
        String wfId = loadComponent(TestWorkflowCollection.COMPONENT_PROJECT);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.TRUE).getWorkflow();
        cr(workflow, "component_project");

        workflow = ws().getWorkflow(wfId, new NodeIDEnt(5), Boolean.TRUE).getWorkflow();
        cr(workflow, "component_in_component_project_l1");

        workflow = ws().getWorkflow(wfId, new NodeIDEnt(5, 0, 7), Boolean.TRUE).getWorkflow();
        cr(workflow, "component_in_component_project_l2");
    }

    /**
     * Test construction of the workflow entity of a project containing nested linked components
     */
    public void testGetNestedLinkedComponentsProject() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.NESTED_LINKED_COMPONENT_PROJECT);
        WorkflowEnt workflow = ws().getWorkflow(wfId, new NodeIDEnt(2), Boolean.TRUE).getWorkflow();
        cr(workflow, "nested_linked_component");
    }

    /**
     * Tests the correct mapping of the node execution states.
     */
    public void testNodeExecutionStates() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.EXECUTION_STATES);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow();
        cr(getNodeStates(workflow), "node_states");

        executeWorkflowAsync(wfId);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            WorkflowEnt w = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow();
            assertThat(((NativeNodeEnt)w.getNodes().get("root:4")).getState().getExecutionState(),
                is(ExecutionStateEnum.EXECUTED));
        });
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow();
        cr(getNodeStates(workflow), "node_states_execution");
    }

    private static Map<String, ExecutionStateEnum> getNodeStates(final WorkflowEnt w) {
        return w.getNodes().entrySet().stream().map(e -> { // NOSONAR
            ExecutionStateEnum state = null;
            NodeEnt n = e.getValue();
            if (n instanceof NativeNodeEnt nativeEnt) {
                state = nativeEnt.getState().getExecutionState();
            } else if (n instanceof ComponentNodeEnt componentEnt) {
                state = componentEnt.getState().getExecutionState();
            } else {
                //
            }
            return Pair.create(e.getKey(), state);
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * Tests the {@link WorkflowEnt#getAllowedActions()} property in partcular.
     */
    public void testGetAllowedActionsInfo() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.TRUE).getWorkflow();

        // check the allowed actions on the workflow itself
        cr(workflow.getAllowedActions(), "allowedactions_root");

        // check for component, node and metanode
        cr(workflow.getAllowedActions(), "allowedactions_8");
        cr(workflow.getAllowedActions(), "allowedactions_12");
        cr(workflow.getAllowedActions(), "allowedactions_6");
    }

    /**
     * Tests the metadata of the project workflow and components.
     */
    public void testWorkflowAndComponentMetadata() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.METADATA);

        // checks the metadata of the project workflow
        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow();
        cr(workflow.getProjectMetadata(), "projectmetadataent");
        assertNull(workflow.getComponentMetadata());

        // checks the metadata of a component
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(4), Boolean.FALSE).getWorkflow();
        cr(workflow.getComponentMetadata(), "componentmetadataent_4");
        assertNull(workflow.getProjectMetadata());

        // makes sure that no metadata is returned for a metanode
        workflow = ws().getWorkflow(wfId, new NodeIDEnt(2), Boolean.FALSE).getWorkflow();
        assertNull(workflow.getProjectMetadata());
        assertNull(workflow.getComponentMetadata());
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
        assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), commandEnt));
    }

    @SuppressWarnings("javadoc")
    public void testExpandConfiguredMetanode() throws Exception {
        var configuredMetanode = 14;
        testExpandConfigured(configuredMetanode);
    }

    @SuppressWarnings("javadoc")
    public void testExpandConfiguredComponent() throws Exception {
        var configuredComponent = 15;
        testExpandConfigured(configuredComponent);
    }

    @SuppressWarnings("javadoc")
    public void testExpandResettableMetanode() throws Exception {
        var resettableMetanode = 13;
        testExpandResettable(resettableMetanode);
    }

    @SuppressWarnings("javadoc")
    public void testExpandResettableComponent() throws Exception {
        var resettableComponent = 10;
        testExpandResettable(resettableComponent);
    }

    @SuppressWarnings("javadoc")
    public void testExpandExecutingMetanode() throws Exception {
        var metanodeWithExecutingSuccessor = 20;
        var metanodeExecutingSuccessor = 19;
        testExpandExecuting(metanodeWithExecutingSuccessor, metanodeExecutingSuccessor);
    }

    @SuppressWarnings("javadoc")
    public void testExpandExecutingComponent() throws Exception {
        var componentWithExecutingSuccessor = 22;
        var componentExecutingSuccessor = 21;
        testExpandExecuting(componentWithExecutingSuccessor, componentExecutingSuccessor);
    }

    @SuppressWarnings("javadoc")
    public void testExpandResultMetanode() throws Exception {
        var configuredMetanode = 14;
        testExpandResult(configuredMetanode);
    }

    @SuppressWarnings("javadoc")
    public void testExpandResultComponent() throws Exception {
        var configuredComponent = 15;
        testExpandResult(configuredComponent);
    }

    @SuppressWarnings("javadoc")
    public void testExpandLockedMetanode() throws Exception {
        var lockedMetanode = 27;
        assertExpandLocked(lockedMetanode);
    }

    @SuppressWarnings("javadoc")
    public void testExpandLockedComponent() throws Exception {
        var lockedComponent = 28;
        assertExpandLocked(lockedComponent);
    }

    private void testExpandResettable(final int nodeToExpand) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var nodeToExpandEnt = new NodeIDEnt(nodeToExpand);

        WorkflowEnt wfEnt = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        assertTrue("Expect selected nodes to have allowed action for expand set to 'reset required'",
            getAllowedActionsOfNodes(List.of(nodeToExpandEnt), wfEnt).stream()
                .anyMatch(actions -> actions.getCanExpand() == AllowedNodeActionsEnt.CanExpandEnum.RESETREQUIRED));

        WorkflowEnt rootWfEnt = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        assertNodesPresent("Expect container to be still be in root workflow", rootWfEnt, List.of(nodeToExpandEnt));

        var commandEnt = buildExpandCommandEnt(nodeToExpandEnt);
        ExpandResultEnt responseEnt = (ExpandResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), commandEnt);
        assertExpanded(wfId, getRootID(), commandEnt, responseEnt);
    }

    private void testExpandExecuting(final int container, final int successor) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var containerEnt = new NodeIDEnt(container);
        executeAndWaitUntilExecuting(wfId, successor);

        WorkflowEnt rootWfEnt = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();

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
        WorkflowEnt unchangedWfEnt = ws().getWorkflow(projectId, wfId, true).getWorkflow();
        assertTrue("Expect selected nodes to have allowed action for collapse set to true",
            getAllowedActionsOfNodes(List.of(nodeToExpandEnt), unchangedWfEnt).stream()
                .anyMatch(actions -> actions.getCanExpand() == AllowedNodeActionsEnt.CanExpandEnum.TRUE));

        var commandEnt = buildExpandCommandEnt(nodeToExpandEnt);
        ExpandResultEnt commandResponseEnt = (ExpandResultEnt)ws().executeWorkflowCommand(projectId, wfId, commandEnt);
        assertExpanded(projectId, wfId, commandEnt, commandResponseEnt);

        ws().undoWorkflowCommand(projectId, wfId);
        WorkflowEnt parentWfAfterUndo = ws().getWorkflow(projectId, wfId, Boolean.TRUE).getWorkflow();
        assertNodesPresent("Container expected to be back in parent workflow after undo", parentWfAfterUndo,
            List.of(nodeToExpandEnt));
        assertNodesNotPresent("Expanded nodes assumed to no longer be in parent workflow", parentWfAfterUndo,
            commandResponseEnt.getExpandedNodeIds());

        ws().redoWorkflowCommand(projectId, wfId);
        assertExpanded(projectId, wfId, commandEnt, commandResponseEnt);
    }

    private void assertExpanded(final String projectId, final NodeIDEnt wfId, final ExpandCommandEnt commandEnt,
        final ExpandResultEnt responseEnt) throws Exception {
        var parentWfEnt = ws().getWorkflow(projectId, wfId, true).getWorkflow();
        assertNodesNotPresent("Expanded node expected to have been removed", parentWfEnt,
            List.of(commandEnt.getNodeId()));
        assertNodesPresent("Nodes from container expected to appear in parent workflow", parentWfEnt,
            responseEnt.getExpandedNodeIds());
        assertAnnotationsPresent("Annotations from container expected to appear in parent workflow", parentWfEnt,
            responseEnt.getExpandedAnnotationIds());
    }

    private void testCollapseExecuting(final CollapseCommandEnt.ContainerTypeEnum containerType) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var waitNode = 16;
        var nodesToCollapseInts = List.of(5, 3);
        var nodesToCollapseEnts = nodesToCollapseInts.stream().map(NodeIDEnt::new).collect(Collectors.toList());

        executeAndWaitUntilExecuting(wfId, waitNode);
        WorkflowEnt rootWfEnt = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();

        assertTrue("Expect selected nodes to have allowed action for collapse to be false",
            getAllowedActionsOfNodes(nodesToCollapseEnts, rootWfEnt).stream()
                .anyMatch(actions -> actions.getCanCollapse() == AllowedNodeActionsEnt.CanCollapseEnum.FALSE));

        var commandEnt = buildCollapseCommandEnt(nodesToCollapseEnts, Collections.emptyList(), containerType);
        var exceptionMessage = assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), commandEnt)).getMessage();
        assertThat(exceptionMessage, containsString("Cannot move all selected nodes (successor executing?)."));
    }

    private static List<AllowedNodeActionsEnt> getAllowedActionsOfNodes(final List<NodeIDEnt> nodes,
        final WorkflowEnt wfEnt) {
        return nodes.stream() //
            .map(NodeIDEnt::toString) //
            .map(idStr -> wfEnt.getNodes().get(idStr)) //
            .map(NodeEnt::getAllowedActions) //
            .collect(Collectors.toList());
    }

    /**
     * Execute a node and block until we can confirm that it is executing (currently only for nodes in root workflow).
     *
     * @param wfId The workflow to operate in
     * @param toWaitFor the node to wait it's executing
     */
    private void executeAndWaitUntilExecuting(final String wfId, final int toWaitFor) throws Exception {
        executeWorkflowAsync(wfId);
        Awaitility.await().atMost(15, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            WorkflowEnt wfEnt = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.FALSE).getWorkflow();
            assertThat(((NativeNodeEnt)wfEnt.getNodes().get(new NodeIDEnt(toWaitFor).toString())).getState()
                .getExecutionState(), is(ExecutionStateEnum.EXECUTING));
        });
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
        ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
        var result0 = ws().executeWorkflowCommand(wfId, getRootID(), commandEnt);
        assertCollapseResult(result0, "0");

        ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
        ws().undoWorkflowCommand(wfId, getRootID()); // no result to inspect

        ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
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
        ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
        var result0 = (ExpandResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), commandEnt);
        assertExpandResponse(result0, "0");

        ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
        ws().undoWorkflowCommand(wfId, getRootID());

        ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
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

        WorkflowEnt unchangedWfEnt = ws().getWorkflow(wfId, getRootID(), true).getWorkflow();
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

        WorkflowEnt parentWfEnt = ws().getWorkflow(wfId, getRootID(), true).getWorkflow();
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

    private void assertCollapsed(final String wfId, final CollapseCommandEnt commandEnt,
        final CommandResultEnt commandResultEnt, final Set<String> annotationContents) throws Exception {
        var newNode = getNewNodeId(commandResultEnt);
        var nodesToCollapseEnts = commandEnt.getNodeIds();
        var nodesToCollapseInts = nodesToCollapseEnts.stream().map(NodeIDEnt::getNodeIDs)
            .map(idArr -> idArr[idArr.length - 1]).collect(Collectors.toList());
        var annotsToCollapseEnts = commandEnt.getAnnotationIds();

        WorkflowEnt parentWfEnt = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();

        assertNodesNotPresent("nodes expected to be removed from top-level workflow", parentWfEnt, nodesToCollapseEnts);
        assertAnnotationsNotPresent("annotations expected to be removed from top-level workflow", annotsToCollapseEnts,
            parentWfEnt);

        assertNodesPresent("node in command response expected to be in top-level workflow", parentWfEnt,
            List.of(newNode));

        WorkflowEnt childWfEnt = ws().getWorkflow(wfId, newNode, true).getWorkflow();

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

        WorkflowEnt wfEnt = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
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

    private static void assertNodesPresent(final String message, final WorkflowEnt wfEnt, final List<NodeIDEnt> nodes) {
        if (nodes.isEmpty()) {
            return;
        }
        assertThat(message, wfEnt.getNodes().keySet(),
            hasItems(nodes.stream().map(NodeIDEnt::toString).toArray(String[]::new)));
    }

    private static void assertNodesNotPresent(final String message, final WorkflowEnt wfEnt,
        final List<NodeIDEnt> nodes) {
        if (nodes.isEmpty()) {
            return;
        }
        assertThat(message, wfEnt.getNodes().keySet(),
            not(hasItems(nodes.stream().map(NodeIDEnt::toString).toArray(String[]::new))));
    }

    private static CollapseCommandEnt buildCollapseCommandEnt(final List<NodeIDEnt> nodes,
        final List<AnnotationIDEnt> annotationIds, final CollapseCommandEnt.ContainerTypeEnum containerType) {
        return builder(CollapseCommandEnt.CollapseCommandEntBuilder.class) //
            .setKind(KindEnum.COLLAPSE) //
            .setContainerType(containerType) //
            .setNodeIds(nodes) //
            .setAnnotationIds(annotationIds) //
            .build();
    }

    private static ExpandCommandEnt buildExpandCommandEnt(final NodeIDEnt node) {
        return builder(ExpandCommandEnt.ExpandCommandEntBuilder.class) //
            .setKind(KindEnum.EXPAND) //
            .setNodeId(node) //
            .build();
    }

    private void assertExpandLocked(final int nodeToExpand) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var nodeToExpandEnt = new NodeIDEnt(nodeToExpand);

        var expandCommand = buildExpandCommandEnt(nodeToExpandEnt);
        Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), expandCommand));
    }


    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link AddNodeCommandEnt}.
     */
    public void testExecuteAddNodeCommand() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var rowFilterFactory = "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory";
        var metanode = new NodeIDEnt(1);
        var component = new NodeIDEnt(2);

        // add a node on root-level
        var result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 12, 13, null, null, null));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), rowFilterFactory, 12, 13, result);

        // undo
        // NOTE: for some reason the undo (i.e. delete node) seems to be carried out asynchronously by the
        // WorkflowManager
        ws().undoWorkflowCommand(wfId, getRootID());
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> assertTrue(
                ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().isEmpty()));

        // add node to metanode
        result = ws().executeWorkflowCommand(wfId, metanode,
            buildAddNodeCommand(rowFilterFactory, null, 13, 14, null, null, null));
        checkForNode(ws().getWorkflow(wfId, metanode, Boolean.FALSE), rowFilterFactory, 13, 14, result);

        // undo
        ws().undoWorkflowCommand(wfId, metanode);
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> assertTrue(
                ws().getWorkflow(wfId, metanode, Boolean.FALSE).getWorkflow().getNodeTemplates().isEmpty()));

        // add node to component
        result = ws().executeWorkflowCommand(wfId, component,
            buildAddNodeCommand(rowFilterFactory, null, 14, 15, null, null, null));
        checkForNode(ws().getWorkflow(wfId, component, Boolean.FALSE), rowFilterFactory, 14, 15, result);

        // undo
        ws().undoWorkflowCommand(wfId, component);
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(
            () -> assertThat(ws().getWorkflow(wfId, component, Boolean.FALSE).getWorkflow().getNodeTemplates().size(),
                is(2)));

        // add a dynamic node (i.e. with factory settings)
        var jsNodeFactory = "org.knime.dynamic.js.v30.DynamicJSNodeFactory";
        var factorySettings =
            "{\"name\":\"settings\",\"value\":{\"nodeDir\":{\"type\":\"string\",\"value\":\"org.knime.dynamic.js.base:nodes/:boxplot_v2\"}}}";
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(jsNodeFactory, factorySettings, 15, 16, null, null, null));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), jsNodeFactory + "#Box Plot (JavaScript)",
            15, 16, result);

        // add a node that doesn't exists
        Exception ex = assertThrows(OperationNotAllowedException.class, () -> ws().executeWorkflowCommand(wfId,
            getRootID(), buildAddNodeCommand("non-sense-factory", null, 0, 0, null, null, null)));
        assertThat(ex.getMessage(), is("No node found for factory key non-sense-factory"));

        // add a dynamic node with non-sense settings
        ex = assertThrows(OperationNotAllowedException.class, () -> ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(jsNodeFactory, "blub", 0, 0, null, null, null)));
        assertThat(ex.getMessage(), startsWith("Problem reading factory settings while trying to create node from"));
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link AddNodeCommandEnt} that also automatically connects a node.
     */
    public void testExecuteAddAndConnectCommand() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var rowFilterFactory = "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory";

        // add and connect a node
        var normalizerFactory = "org.knime.base.node.preproc.pmml.normalize.NormalizerPMMLNodeFactory2";
        var sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(normalizerFactory, null, 32, 64, null, null, null))).getNewNodeId();
        var result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 64, 128, sourceNodeId, 1, NodeRelationEnum.SUCCESSORS));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), rowFilterFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 1, result, true);

        // undo adding both of the nodes
        ws().undoWorkflowCommand(wfId, getRootID()); // to remove row filter node
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(
            () -> assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(),
                is(1)));
        ws().undoWorkflowCommand(wfId, getRootID()); // to remove normalizer node
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(
            () -> assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(),
                is(0)));

        // redo adding the normalizer
        ws().redoWorkflowCommand(wfId, getRootID());
        assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(), is(1));

        // try to connect to an incompatible port
        var ex = assertThrows(OperationNotAllowedException.class, () -> ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 64, 128, sourceNodeId, 2, NodeRelationEnum.SUCCESSORS)));
        assertThat(ex.getMessage(), is("Node couldn't be created because a connection couldn't be added."));

        // redo adding the row filter
        ws().redoWorkflowCommand(wfId, getRootID());
        assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(), is(2));

        // try to connect to a port that is already used
        var rowSplitterFactory = "org.knime.base.node.preproc.filter.row2.RowSplitterNodeFactory";
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowSplitterFactory, null, 128, 256, sourceNodeId, 1, NodeRelationEnum.SUCCESSORS));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), rowSplitterFactory, 128, 256, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 1, result, true); // this extra connection is allowed

        // undo adding row splitter
        ws().undoWorkflowCommand(wfId, getRootID());
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link AddNodeCommandEnt} that also auto connects nodes without a given source port.
     */
    public void testExecuteAddAndConnectCommandAutoGuessSourcePorts() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var rowSplitterFactory = "org.knime.base.node.preproc.filter.row2.RowSplitterNodeFactory";
        var columnAppenderFactory = "org.knime.base.node.preproc.columnappend.ColumnAppenderNodeFactory";
        var normalizerFactory = "org.knime.base.node.preproc.pmml.normalize.NormalizerPMMLNodeFactory2";
        var columnFilter = "org.knime.base.node.preproc.filter.column.DataColumnSpecFilterNodeFactory";

        // add a node and auto-connect all compatible ports
        var sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowSplitterFactory, null, 32, 64, null, null, null))).getNewNodeId();
        var result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(columnAppenderFactory, null, 64, 128, sourceNodeId, null, NodeRelationEnum.SUCCESSORS));
        var snapshot = ws().getWorkflow(wfId, getRootID(), Boolean.FALSE);
        checkForNode(snapshot, columnAppenderFactory, 64, 128, result);
        checkForConnection(snapshot, sourceNodeId, 1, result, true); // got auto-connected
        checkForConnection(snapshot, sourceNodeId, 2, result, true); // got auto-connected

        // add a another node and try to auto-connect auto-guessed ports
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(normalizerFactory, null, 128, 256, sourceNodeId, null, NodeRelationEnum.SUCCESSORS));
        snapshot = ws().getWorkflow(wfId, getRootID(), Boolean.FALSE);
        checkForNode(snapshot, normalizerFactory, 128, 256, result);
        checkForConnection(snapshot, sourceNodeId, 1, result, true); // got auto-connected, port with 2 connections
        checkForConnection(snapshot, sourceNodeId, 2, result, false); // not auto-connected

        // add a node as a predecessor and auto-connect auto-guessed ports
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(columnFilter, null, 128, 256, sourceNodeId, null, NodeRelationEnum.PREDECESSORS));
        snapshot = ws().getWorkflow(wfId, getRootID(), Boolean.FALSE);
        checkForNode(snapshot, columnFilter, 128, 256, result);
        var predecessorId = ((AddNodeResultEnt)result).getNewNodeId();
        checkForConnection(snapshot, predecessorId, 1, sourceNodeId, true); // got auto-connected

    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link AddNodeCommandEnt} that also automatically connects a dynamic node.
     */
    public void testExecuteAddAndConnectCommandDynamicNode() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var rowFilterFactory = "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory";
        var caseSwitchStartFactory = "org.knime.base.node.switches.caseswitch.any.CaseStartAnyNodeFactory";
        var tableColToFlowVariableFactory =
            "org.knime.base.node.flowvariable.tablecoltovariable4.TableColumnToVariable4NodeFactory";

        // add and connect a node
        var sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 32, 64, null, null, null))).getNewNodeId();
        var result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(caseSwitchStartFactory, null, 64, 128, sourceNodeId, 1, NodeRelationEnum.SUCCESSORS));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), caseSwitchStartFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 1, result, true);

        // undo adding the case switch
        ws().undoWorkflowCommand(wfId, getRootID());
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(
            () -> assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(),
                is(1)));

        // try the same thing for a flow variable connection
        sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(tableColToFlowVariableFactory, null, 256, 512, null, null, null))).getNewNodeId();
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(caseSwitchStartFactory, null, 64, 128, sourceNodeId, 1, NodeRelationEnum.SUCCESSORS));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), caseSwitchStartFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 1, result, true);

        // TODO:
        // * Find another way to test this for database ports
        // * Test for exchangeable ports, like excel reader and file system reader
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link AddNodeCommandEnt} that also automatically connects with flow variable ports.
     */
    public void testExecuteAddAndConnectCommandFlowVariables() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var tableColToVariableFactory =
            "org.knime.base.node.flowvariable.tablecoltovariable4.TableColumnToVariable4NodeFactory";
        var variableToTableRowFactory =
            "org.knime.base.node.flowvariable.variabletotablerow4.VariableToTable4NodeFactory";
        var rowFilterFactory = "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory";
        var imageToTableFactory = "org.knime.base.node.image.ImageToTableNodeFactory";
        var tableRowToImageFactory = "org.knime.base.node.image.tablerowtoimage.TableRowToImageNodeFactory";

        // add and connect native nodes with flow variables on port index 1
        var sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(tableColToVariableFactory, null, 32, 64, null, null, null))).getNewNodeId();
        var result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(variableToTableRowFactory, null, 64, 128, sourceNodeId, 1, NodeRelationEnum.SUCCESSORS));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), variableToTableRowFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 1, result, true);

        // undo variable to table row
        ws().undoWorkflowCommand(wfId, getRootID());
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(
            () -> assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(),
                is(1)));

        // add and connect two incompatible nodes from source port 0 to destination port 1
        sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 128, 256, null, null, null))).getNewNodeId();
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(variableToTableRowFactory, null, 64, 128, sourceNodeId, 0, NodeRelationEnum.SUCCESSORS));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), variableToTableRowFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 0, result, true);

        // undo variable to table row
        ws().undoWorkflowCommand(wfId, getRootID());
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(
            () -> assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(),
                is(2)));

        // connect two incompatible nodes via their flow default variable ports
        sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(imageToTableFactory, null, 256, 512, null, null, null))).getNewNodeId();
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(tableRowToImageFactory, null, 64, 128, sourceNodeId, 0, NodeRelationEnum.SUCCESSORS));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), tableRowToImageFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 0, result, true);
    }

    private static AddNodeCommandEnt buildAddNodeCommand(final String factoryClassName, final String factorySettings,
        final int x, final int y, final NodeIDEnt quickAddNodeId, final Integer quickAddPortIdx,
        final NodeRelationEnum nodeRelation) {
        return builder(AddNodeCommandEntBuilder.class).setKind(KindEnum.ADD_NODE)//
            .setNodeFactory(builder(NodeFactoryKeyEntBuilder.class).setClassName(factoryClassName)
                .setSettings(factorySettings).build())//
            .setPosition(builder(XYEntBuilder.class).setX(x).setY(y).build())//
            .setSourceNodeId(quickAddNodeId)//
            .setSourcePortIdx(quickAddPortIdx)//
            .setNodeRelation(nodeRelation).build();
    }

    /**
     * Check for the existence of a node with the given nodeFactory in the given x and y coordinates, throws exception
     * if nothing is found
     */
    private static NodeEnt checkForNode(final String message, final WorkflowSnapshotEnt wf, final String nodeFactory,
        final int x, final int y) {
        assertThat(message, wf.getWorkflow().getNodeTemplates().keySet(), Matchers.hasItems(nodeFactory));
        var nodeEnt = wf.getWorkflow().getNodes().values().stream()
            .filter(n -> n instanceof NativeNodeEnt && ((NativeNodeEnt)n).getTemplateId().equals(nodeFactory))
            .findFirst().orElseThrow();
        assertThat(message, nodeEnt.getPosition().getX(), is(x));
        assertThat(message, nodeEnt.getPosition().getY(), is(y));
        return nodeEnt;
    }

    private static void checkForNode(final WorkflowSnapshotEnt wf, final String nodeFactory, final int x, final int y,
        final CommandResultEnt result) {
        var nodeEnt = checkForNode("", wf, nodeFactory, x, y);
        var newNodeId = ((AddNodeResultEnt)result).getNewNodeId();
        assertThat(newNodeId, equalTo(nodeEnt.getId()));
    }

    private static void checkForConnection(final WorkflowSnapshotEnt wf, final NodeIDEnt sourceNodeId,
        final Integer sourcePortIdx, final NodeIDEnt destNodeId, final boolean isPresent) {
        var connections = wf.getWorkflow().getConnections();
        var numConnections = connections.values().stream()//
            .filter(c -> c.getSourceNode().equals(sourceNodeId))//
            .filter(c -> c.getSourcePort().equals(sourcePortIdx))//
            .filter(c -> c.getDestNode().equals(destNodeId))//
            .count();
        assertThat(numConnections, is(isPresent ? 1L : 0L));
    }

    private static void checkForConnection(final WorkflowSnapshotEnt wf, final NodeIDEnt sourceNodeId,
        final Integer sourcePortIdx, final CommandResultEnt result, final boolean isPresent) {
        var destNodeId = ((AddNodeResultEnt)result).getNewNodeId();
        checkForConnection(wf, sourceNodeId, sourcePortIdx, destNodeId, isPresent);
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called {@link UpdateComponentOrMetanodeNameCommandEnt}
     */
    public void testExecuteUpdateComponentOrMetanodeNameCommand() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        final var newName = "New Name";
        final var nativeNode = new NodeIDEnt(2);
        final var metanode = new NodeIDEnt(6);
        final var component = new NodeIDEnt(23);

        // successfully rename metanode
        WorkflowEnt workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        final String oldMetaNodeName = ((MetaNodeEnt)workflow.getNodes().get(metanode.toString())).getName();
        final var command1 = buildUpdateComponentOrMetanodeNameCommandEnt(metanode, newName);
        ws().executeWorkflowCommand(wfId, getRootID(), command1);
        workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        assertThat(((MetaNodeEnt)workflow.getNodes().get(metanode.toString())).getName(), is(newName));
        // undo
        ws().undoWorkflowCommand(wfId, getRootID());
        workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        assertThat(((MetaNodeEnt)workflow.getNodes().get(metanode.toString())).getName(), is(oldMetaNodeName));

        // successfully rename component
        workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        final String oldComponentName = ((ComponentNodeEnt)workflow.getNodes().get(component.toString())).getName();
        final var command2 = buildUpdateComponentOrMetanodeNameCommandEnt(component, newName);
        ws().executeWorkflowCommand(wfId, getRootID(), command2);
        workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        assertThat(((ComponentNodeEnt)workflow.getNodes().get(component.toString())).getName(), is(newName));
        // undo
        ws().undoWorkflowCommand(wfId, getRootID());
        workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        assertThat(((ComponentNodeEnt)workflow.getNodes().get(component.toString())).getName(), is(oldComponentName));

        // fail to rename metanode or component
        final List<NodeIDEnt> nodeIds = Arrays.asList(metanode, component);
        final List<String> emptyNames = Arrays.asList("", " ", "   ");
        emptyNames.forEach(name -> nodeIds.forEach(nodeId -> {
            final var command3 = buildUpdateComponentOrMetanodeNameCommandEnt(nodeId, name);
            Exception exception = assertThrows(OperationNotAllowedException.class,
                () -> ws().executeWorkflowCommand(wfId, getRootID(), command3));
            assertThat(exception.getMessage(), containsString("Illegal new name"));
        }));

        // fail to rename native node
        final var command4 = buildUpdateComponentOrMetanodeNameCommandEnt(nativeNode, newName);
        Exception exception = assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command4));
        assertThat(exception.getMessage(), containsString("cannot be renamed"));

    }

    private static UpdateComponentOrMetanodeNameCommandEnt
        buildUpdateComponentOrMetanodeNameCommandEnt(final NodeIDEnt nodeId, final String name) {
        return builder(UpdateComponentOrMetanodeNameCommandEntBuilder.class)
            .setKind(KindEnum.UPDATE_COMPONENT_OR_METANODE_NAME)//
            .setName(name)//
            .setNodeId(nodeId)//
            .build();
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called {@link UpdateLabelCommandEnt}
     */
    public void testExecuteUpdateNodeLabelCommand() throws Exception {
        final var wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        final var metanodeId = new NodeIDEnt(1);
        final var componentId = new NodeIDEnt(2);

        // add a native node
        final var rowFilterFactory = "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory";
        final var nativeNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 32, 64, null, null, null))).getNewNodeId();

        // do tests for metanode, component and native node
        for (var nodeId : List.of(metanodeId, componentId, nativeNodeId)) {
            var oldLabel =
                getLabelFromNodeInWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow(), nodeId);
            var newLabel = "The new label";

            // update node label
            ws().executeWorkflowCommand(wfId, getRootID(), buildUpdateNodeLabelCommandEnt(nodeId, newLabel));
            var retrievedLabel =
                getLabelFromNodeInWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow(), nodeId);
            assertThat("Retrieved label must equal new label", retrievedLabel, is(newLabel));

            // undo command
            ws().undoWorkflowCommand(wfId, getRootID());
            retrievedLabel =
                getLabelFromNodeInWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow(), nodeId);
            assertThat("Retrieved label must equal old label", retrievedLabel, is(oldLabel));
        }
    }

    private static UpdateNodeLabelCommandEnt buildUpdateNodeLabelCommandEnt(final NodeIDEnt nodeId,
        final String label) {
        return builder(UpdateNodeLabelCommandEnt.UpdateNodeLabelCommandEntBuilder.class)//
            .setKind(KindEnum.UPDATE_NODE_LABEL)//
            .setNodeId(nodeId)//
            .setLabel(label)//
            .build();
    }

    /**
     * When a node has its default {@link NodeAnnotationData}, the {@link NodeAnnotationEnt} will be set to
     * {@code null}, see {@link WorkflowEntityFactory#buildNodeAnnotationEnt}. Setting the node label to something else
     * and undoing this operation will yield to a non-empty {@link NodeAnnotationEnt}, since the
     * {@link NodeAnnotationData} is no longer considered the default. Instead, the node annotation text will be set to
     * the empty string, see {@link WorkflowEntityFactory#buildNodeAnnotationEnt}
     */
    private static String getLabelFromNodeInWorkflow(final WorkflowEnt wf, final NodeIDEnt nodeId) {
        var annotation = wf.getNodes().get(nodeId.toString()).getAnnotation();
        return annotation == null ? "" : annotation.getText().getValue();
    }

    /**
     * Test whether removing ports from native nodes is (not) allowed.
     */
    public void testCanRemovePortFromNative() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.PORTS);
        var workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();

        // node with two input port groups, both containing one fixed port
        // the first input port group has an additionally added port ("configured" port)
        // all input ports are connected.
        final var recursiveLoopEnd = new NodeIDEnt(190);
        assertPortTypeIDs(recursiveLoopEnd, workflow, 0, Boolean.TRUE, FlowVariablePortObject.TYPE);
        assertThat( //
            "Do not allow removal of fixed flow variable port", //
            !portRemovalAllowed(recursiveLoopEnd, workflow, 0) //
        );
        assertPortTypeIDs(recursiveLoopEnd, workflow, 1, Boolean.TRUE, BufferedDataTable.TYPE);
        assertThat( //
            "Do not allow removal of first static port in first port group", //
            !portRemovalAllowed(recursiveLoopEnd, workflow, 1) //
        );
        assertThat( //
            "Allow removal of dynamic port in first port group", //
            portRemovalAllowed(recursiveLoopEnd, workflow, 2) //
        );
        assertThat( //
            "Do not allow removal of first static port in second port group", //
            !portRemovalAllowed(recursiveLoopEnd, workflow, 3) //
        );
        // each additional input port also adds an output port
        assertThat("Do not allow removal of static output port",
            !portRemovalAllowed(recursiveLoopEnd, workflow, 1, Boolean.FALSE));
        assertThat("Allow removal of additional output port",
            portRemovalAllowed(recursiveLoopEnd, workflow, 2, Boolean.FALSE));

        // example for static nodes (only one port group, nothing to be modified)
        final var columnFilter = new NodeIDEnt(197);
        assertThat( //
            "Do not allow removal of port from node without dynamic port groups", //
            !portRemovalAllowed(columnFilter, workflow, 1) //
        );

        var workflowWithoutInteractionInfo = ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow();
        assertNull( //
            "Expect CAN_REMOVE property to not be present if interaction info is not included", //
            workflowWithoutInteractionInfo.getNodes().get(recursiveLoopEnd.toString()).getInPorts().get(1).isCanRemove()//
        );

        // test that ports can not be removed if node cannot be replaced (updating ports of native node means
        //   replacing the node with a modified version).
        // Node with one input group containing two fixed and one additionally added port.
        final var concatenateNode = new NodeIDEnt(187);
        var successor = 189;
        executeAndWaitUntilExecuting(wfId, successor);
        assertThat( //
            "Do not allow removing port if executing successor", //
            !portRemovalAllowed(concatenateNode, workflow, 1) //
        );
    }

    /**
     * Test whether removing ports from container nodes is (not) allowed.
     */
    public void testCanRemovePortFromContainer() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.PORTS);
        var workflow = ws().getWorkflow(wfId, getRootID(), true).getWorkflow();
        final var metanode = new NodeIDEnt(192);
        final var component = new NodeIDEnt(193);
        assertThat( //
            "Expect: Can not remove connected port", //
            !portRemovalAllowed(metanode, workflow, 0) //
        );
        assertThat( //
            "Expect: Can remove unconnected input port", //
            portRemovalAllowed(metanode, workflow, 1) //
        );
        assertThat( //
            "Expect: Can remove unconnected output port", //
            portRemovalAllowed(metanode, workflow, 0, false) //
        );
        // components have a fixed flow variable port, metanodes do not
        var flowVarPortOffset = 1;
        assertThat( //
            "Expect: Can not remove connected port", //
            !portRemovalAllowed(component, workflow, 0 + flowVarPortOffset) //
        );
        assertThat( //
            "Expect: Can remove unconnected input port", //
            portRemovalAllowed(component, workflow, 1 + flowVarPortOffset) //
        );
        assertThat( //
            "Expect: Can remove unconnected output port", //
            portRemovalAllowed(component, workflow, 1 + flowVarPortOffset, false) //
        );
    }

    private static boolean portRemovalAllowed(final NodeIDEnt targetNode, final WorkflowEnt workflow,
        final int portIndex) {
        return portRemovalAllowed(targetNode, workflow, portIndex, true);
    }

    private static boolean portRemovalAllowed(final NodeIDEnt targetNode, final WorkflowEnt workflow,
        final int portIndex, final boolean inPort) {
        var nodeEnt = workflow.getNodes().get(targetNode.toString());
        if (nodeEnt == null) {
            throw new IllegalArgumentException("Node not found in workflow entity");
        }
        var portList = inPort ? nodeEnt.getInPorts() : nodeEnt.getOutPorts();
        if (portList == null) {
            throw new IllegalArgumentException("No in-/output ports present");
        }
        return portList.get(portIndex).isCanRemove();
    }

    private static void assertPortTypeIDs(final NodeIDEnt targetNode, final WorkflowEnt workflow, final int portIndex,
        final boolean inPort, final PortType expectedPortType) {
        var nodeEnt = workflow.getNodes().get(targetNode.toString());
        var portList = inPort ? nodeEnt.getInPorts() : nodeEnt.getOutPorts();
        var type = portList.get(portIndex).getTypeId();
        assertThat("Expect port type kinds to match", type.equals(CoreUtil.getPortTypeId(expectedPortType)));
    }

    /**
     * Test whether adding ports to native nodes is (not) allowed
     */
    public void testCanAddPortToNative() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.PORTS);
        var workflow = ws().getWorkflow(wfId, getRootID(), true).getWorkflow();
        final var concatenateNode = new NodeIDEnt(187);
        var successor = 189;
        var compatiblePortType = BufferedDataTable.TYPE;
        var compatiblePortTypeId = CoreUtil.getPortTypeId(compatiblePortType);
        var incompatiblePortType = DatabasePortObject.TYPE;
        var incompatiblePortTypeId = CoreUtil.getPortTypeId(incompatiblePortType);
        var targetPortGroup = "input";

        assertThat( //
            "Allow adding port of compatible type", //
            portAddingAllowed(concatenateNode, workflow, compatiblePortTypeId, targetPortGroup) //
        );
        assertThat("Do not allow adding port of incompatible type",
            !portAddingAllowed(concatenateNode, workflow, incompatiblePortTypeId, targetPortGroup) //
        );

        executeAndWaitUntilExecuting(wfId, successor);
        workflow = ws().getWorkflow(wfId, getRootID(), true).getWorkflow();
        var addingNotAllowed = !portAddingAllowed(concatenateNode, workflow, compatiblePortTypeId, targetPortGroup);
        assertThat( //
            "Do not allow adding port if executing successor", //
            addingNotAllowed);
    }

    /**
     * Test whether adding ports to native nodes with dynamic ports is (not) allowed.
     */
    public void testCanAddPortToNativeIsInteractive() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var nodeFactory = "org.knime.gateway.testing.helper.webui.node.DummyNodeDynamicPortsInteractiveFactory";
        var compatiblePortTypeId = CoreUtil.getPortTypeId(BufferedDataTable.TYPE);
        var node = new NodeIDEnt(3);

        // Add node and get workflow
        ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(nodeFactory, null, 32, 64, null, null, null));
        var workflow = ws().getWorkflow(wfId, getRootID(), true).getWorkflow();

        // Can add input ports
        assertThat( //
            "Allow adding input ports", //
            portAddingAllowed(node, workflow, compatiblePortTypeId, "Inputs") //
        );

        // Cannot add output ports
        assertThat( //
            "Don't allow adding output ports", //
            !portAddingAllowed(node, workflow, compatiblePortTypeId, "Outputs") //
        );
    }

    private static boolean portAddingAllowed(final NodeIDEnt targetNode, final WorkflowEnt workflow,
        final String targetPortTypeId, final String targetPortGroup) {
        var node = workflow.getNodes().get(targetNode.toString());
        if (!(node instanceof NativeNodeEnt)) {
            return Boolean.FALSE;
        }
        var portGroups = ((NativeNodeEnt)node).getPortGroups();
        if (portGroups == null) {
            return Boolean.FALSE;
        }
        var portGroup = portGroups.get(targetPortGroup);
        if (portGroup == null) {
            return Boolean.FALSE;
        }
        var canAddInputPort = portGroup.isCanAddInPort() != null ? portGroup.isCanAddInPort() : Boolean.FALSE;
        var canAddOutputPort = portGroup.isCanAddOutPort() != null ? portGroup.isCanAddOutPort() : Boolean.FALSE;
        var supportsType =
            portGroup.getSupportedPortTypeIds().stream().filter(ent -> ent.equals(targetPortTypeId)).count() > 0;
        return (canAddInputPort || canAddOutputPort) && supportsType;
    }

    /**
     * Execute, undo and redo of adding an input port to a metanode. Add output port.
     */
    public void testAddPortToMetanode() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var someConfiguredMetanode = new NodeIDEnt(14);
        assertAddUndoRedoContainerPorts(wfId, someConfiguredMetanode);
    }

    /**
     * Execute, undo and redo of adding an input port to a component. Add output port.
     */
    public void testAddPortToComponent() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var someConfiguredComponent = new NodeIDEnt(15);
        assertAddUndoRedoContainerPorts(wfId, someConfiguredComponent);
    }

    private void assertAddUndoRedoContainerPorts(final String wfId, final NodeIDEnt node) throws Exception {
        assertAddUndoRedoContainerPorts(wfId, getRootID(), node);
    }

    private void assertAddUndoRedoContainerPorts(final String projectId, final NodeIDEnt wfId, final NodeIDEnt node)
        throws Exception {
        var portType = WorkflowPortObject.TYPE;

        var unchangedWfEnt = ws().getWorkflow(projectId, wfId, false).getWorkflow();

        var addInputPortCommandEnt = builder(AddPortCommandEnt.AddPortCommandEntBuilder.class) //
            .setNodeId(node) //
            .setSide(PortCommandEnt.SideEnum.INPUT) //
            .setPortTypeId(CoreUtil.getPortTypeId(portType)) //
            .setKind(KindEnum.ADD_PORT) //
            .build();

        var commandResult = ws().executeWorkflowCommand(projectId, wfId, addInputPortCommandEnt);
        assertPortAdded(node, true, projectId, wfId, unchangedWfEnt, commandResult);

        ws().undoWorkflowCommand(projectId, wfId);
        assertPortsUnchanged(projectId, wfId, node, unchangedWfEnt);

        ws().redoWorkflowCommand(projectId, wfId);
        assertPortAdded(node, true, projectId, wfId, unchangedWfEnt, commandResult);

        var addOutputPortCommandEnt = builder(AddPortCommandEnt.AddPortCommandEntBuilder.class) //
            .setNodeId(node) //
            .setSide(PortCommandEnt.SideEnum.OUTPUT) //
            .setPortTypeId(CoreUtil.getPortTypeId(portType)) //
            .setKind(KindEnum.ADD_PORT) //
            .build();
        commandResult = ws().executeWorkflowCommand(projectId, wfId, addOutputPortCommandEnt);
        assertPortAdded(node, false, projectId, wfId, unchangedWfEnt, commandResult);
    }

    private void assertPortsUnchanged(final String projectId, final NodeIDEnt wfId, final NodeIDEnt node,
        final WorkflowEnt originalWfEnt) throws ServiceExceptions.NotASubWorkflowException, NodeNotFoundException {
        var currentWfEnt = ws().getWorkflow(projectId, wfId, false).getWorkflow();
        var unchangedInports = originalWfEnt.getNodes().get(node.toString()).getInPorts();
        var changedInPorts = currentWfEnt.getNodes().get(node.toString()).getInPorts();
        assertPortListUnchanged(unchangedInports, changedInPorts);
        var unchangedOutports = originalWfEnt.getNodes().get(node.toString()).getOutPorts();
        var changedOutports = currentWfEnt.getNodes().get(node.toString()).getOutPorts();
        assertPortListUnchanged(unchangedOutports, changedOutports);
    }

    private static void assertPortListUnchanged(final List<? extends NodePortEnt> originalPorts,
        final List<? extends NodePortEnt> currentPorts) {
        var originalNumInPorts = originalPorts.size();
        assertThat("Expect to back to original number of in-ports after undo",
            currentPorts.size() == originalNumInPorts);
        var originalNames = originalPorts.stream().map(NodePortTemplateEnt::getName).collect(Collectors.toList());
        var currentNames = currentPorts.stream().map(NodePortTemplateEnt::getName).collect(Collectors.toList());
        assertEquals("Expect port names to not have changed", originalNames, currentNames);
    }

    private void assertPortAdded(final NodeIDEnt node, final boolean isInPort, final String projectId,
        final NodeIDEnt wfId, final WorkflowEnt originalWfEnt, final CommandResultEnt commandResult) throws Exception {
        var originalNumInPorts = getPortList(originalWfEnt, isInPort, node).size();
        var newWorkflowEnt = ws().getWorkflow(projectId, wfId, Boolean.TRUE).getWorkflow();
        var newPortList = getPortList(newWorkflowEnt, isInPort, node);
        var newNumPorts = newPortList.size();
        assertThat("Expect number of ports to have increased by one", newNumPorts == originalNumInPorts + 1);
        assertThat("Command result is not an `AddPortResultEnt`", commandResult, instanceOf(AddPortResultEnt.class));
        assertThat("New port index returned is wrong", ((AddPortResultEnt)commandResult).getNewPortIdx(),
            is(newNumPorts - 1));
    }

    private void assertPortRemoved(final NodeIDEnt node, final boolean isInPort, final String wfId,
        final WorkflowEnt originalWfEnt) throws Exception {
        var originalNumInPorts = getPortList(originalWfEnt, isInPort, node).size();
        var newWorkflowEnt = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        var newPortList = getPortList(newWorkflowEnt, isInPort, node);
        var newNumPorts = newPortList.size();
        assertThat("Expect number of ports to have decreased by one", newNumPorts == originalNumInPorts - 1);
    }

    /**
     * Execute, undo and redo of removing port from metanode.
     */
    public void testRemovePortFromMetanode() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var metanodeWithPorts = new NodeIDEnt(24);
        assertRemoveUndoRedoContainerPorts(wfId, metanodeWithPorts);
    }

    /**
     * Execute, undo and redo of removing port from container.
     */
    public void testRemovePortFromComponent() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var componentWithPorts = new NodeIDEnt(25);

        var unchangedWfEnt = ws().getWorkflow(wfId, getRootID(), false).getWorkflow();

        var deleteFixedFlowVarPort = buildDeletePortCommandEnt(componentWithPorts, PortCommandEnt.SideEnum.INPUT, 0);
        assertThrows("Expect exception on removing port with index 0 from component (fixed flow variable port)",
            OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), deleteFixedFlowVarPort));
        assertPortsUnchanged(wfId, getRootID(), componentWithPorts, unchangedWfEnt);

        assertRemoveUndoRedoContainerPorts(wfId, componentWithPorts);
    }

    private void assertRemoveUndoRedoContainerPorts(final String wfId, final NodeIDEnt node) throws Exception {
        var unchangedWfEnt = ws().getWorkflow(wfId, getRootID(), false).getWorkflow();

        var deleteImpossiblePort = buildDeletePortCommandEnt(node, PortCommandEnt.SideEnum.INPUT, -3);
        assertThrows("Expect exception on removing port with invalid index", OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), deleteImpossiblePort));

        var deleteFirst = buildDeletePortCommandEnt(node, PortCommandEnt.SideEnum.INPUT, 1);
        ws().executeWorkflowCommand(wfId, getRootID(), deleteFirst);
        assertPortRemoved(node, true, wfId, unchangedWfEnt);

        ws().undoWorkflowCommand(wfId, getRootID());
        assertPortsUnchanged(wfId, getRootID(), node, unchangedWfEnt);

        ws().redoWorkflowCommand(wfId, getRootID());
        assertPortRemoved(node, true, wfId, unchangedWfEnt);
    }

    private static RemovePortCommandEnt buildDeletePortCommandEnt(final NodeIDEnt targetNode,
        final PortCommandEnt.SideEnum side, final int portIndex) {
        return builder(RemovePortCommandEnt.RemovePortCommandEntBuilder.class) //
            .setNodeId(targetNode) //
            .setSide(side) //
            .setPortIndex(portIndex) //
            .setKind(KindEnum.REMOVE_PORT) //
            .build();
    }

    private static List<? extends NodePortEnt> getPortList(final WorkflowEnt wfEnt, final boolean isInPort,
        final NodeIDEnt node) {
        var nodeEnt = wfEnt.getNodes().get(node.toString());
        return isInPort ? nodeEnt.getInPorts() : nodeEnt.getOutPorts();
    }

    private List<? extends NodePortEnt> getPortList(final String wfId, final boolean isInPort, final NodeIDEnt node)
        throws Exception {
        var wfEnt = ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
        var nodeEnt = wfEnt.getNodes().get(node.toString());
        return isInPort ? nodeEnt.getInPorts() : nodeEnt.getOutPorts();
    }

    /**
     * Add ports to different port groups of native node, undo and redo.
     */
    public void testAddPortToNativeNode() throws Exception {

        final String wfId = loadWorkflow(TestWorkflowCollection.PORTS);
        var recursiveLoopEnd = new NodeIDEnt(190);

        var portType = BufferedDataTable.TYPE;

        var addToFirstGroupCommand = builder(AddPortCommandEnt.AddPortCommandEntBuilder.class) //
            .setNodeId(recursiveLoopEnd) //
            .setSide(PortCommandEnt.SideEnum.INPUT) //
            .setPortGroup("Collector") //
            .setPortTypeId(CoreUtil.getPortTypeId(portType)) //
            .setKind(KindEnum.ADD_PORT) //
            .build();

        var portResult = ws().executeWorkflowCommand(wfId, getRootID(), addToFirstGroupCommand);
        assertThat(portResult, instanceOf(AddPortResultEnt.class));
        assertThat(((AddPortResultEnt)portResult).getNewPortIdx(), is(3));
        cr(getPortList(wfId, true, recursiveLoopEnd), "native_add_port_addToFirstPortGroup");

        var addToSecondGroupCommand = builder(AddPortCommandEnt.AddPortCommandEntBuilder.class) //
            .setNodeId(recursiveLoopEnd) //
            .setSide(PortCommandEnt.SideEnum.INPUT) //
            .setPortGroup("Recursion") //
            .setPortTypeId(CoreUtil.getPortTypeId(portType)) //
            .setKind(KindEnum.ADD_PORT) //
            .build();
        portResult = ws().executeWorkflowCommand(wfId, getRootID(), addToSecondGroupCommand);
        assertThat(portResult, instanceOf(AddPortResultEnt.class));
        assertThat(((AddPortResultEnt)portResult).getNewPortIdx(), is(6));
        cr(getPortList(wfId, true, recursiveLoopEnd), "native_add_port_addToSecondPortGroup");

        ws().undoWorkflowCommand(wfId, getRootID());
        cr(getPortList(wfId, true, recursiveLoopEnd), "native_add_port_undo");

        ws().redoWorkflowCommand(wfId, getRootID());
        cr(getPortList(wfId, true, recursiveLoopEnd), "native_add_port_redo");
    }

    /**
     * Remove ports from different port groups of native node, undo and redo.
     */
    public void testRemovePortFromNative() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.PORTS);
        var recursiveLoopEnd = new NodeIDEnt(190);

        var removeFromFirstGroupCommand = builder(RemovePortCommandEnt.RemovePortCommandEntBuilder.class) //
            .setNodeId(recursiveLoopEnd) //
            .setSide(PortCommandEnt.SideEnum.INPUT) //
            .setPortGroup("Collector") //
            .setKind(KindEnum.REMOVE_PORT) //
            .build();

        ws().executeWorkflowCommand(wfId, getRootID(), removeFromFirstGroupCommand);
        cr(getPortList(wfId, true, recursiveLoopEnd), "native_remove_port_removeFromFirstPortGroup");

        var removeFromSecondGroupCommand = builder(RemovePortCommandEnt.RemovePortCommandEntBuilder.class) //
            .setNodeId(recursiveLoopEnd) //
            .setSide(PortCommandEnt.SideEnum.INPUT) //
            .setPortGroup("Recursion") //
            .setKind(KindEnum.REMOVE_PORT) //
            .build();
        ws().executeWorkflowCommand(wfId, getRootID(), removeFromSecondGroupCommand);
        cr(getPortList(wfId, true, recursiveLoopEnd), "native_remove_port_removeFromSecondPortGroup");

        ws().undoWorkflowCommand(wfId, getRootID());
        cr(getPortList(wfId, true, recursiveLoopEnd), "native_remove_port_undo");

        ws().redoWorkflowCommand(wfId, getRootID());
        cr(getPortList(wfId, true, recursiveLoopEnd), "native_remove_port_redo");
    }

    /**
     * Some commands weren't working from within a component and metanode (NXT-1141). This is the test for it.
     */
    public void testExecuteCommandsWithinMetanode() throws Exception {
        testExecuteCommandsWithinComponentAndMetanode(ContainerTypeEnum.METANODE);
    }

    /**
     * Some commands weren't working from within a component and metanode (NXT-1141). This is the test for it.
     */
    public void testExecuteCommandsWithinComponent() throws Exception {
        testExecuteCommandsWithinComponentAndMetanode(ContainerTypeEnum.COMPONENT);
    }

    private void testExecuteCommandsWithinComponentAndMetanode(final ContainerTypeEnum containerType) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);

        // collapse metanode and component into a component/metanode and add ports to metanode and component
        var collapse = buildCollapseCommandEnt(List.of(new NodeIDEnt(14), new NodeIDEnt(15)), Collections.emptyList(),
            containerType);
        var commandRes = ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), collapse);
        var newNodeID = getNewNodeId(commandRes);
        assertAddUndoRedoContainerPorts(wfId, newNodeID, appendNodeID(newNodeID, 14, containerType));
        assertAddUndoRedoContainerPorts(wfId, newNodeID, appendNodeID(newNodeID, 15, containerType));

        // expand again
        var expand = buildExpandCommandEnt(newNodeID);
        ws().executeWorkflowCommand(wfId, getRootID(), expand);

        // collapse metanode and component into component/metanode and expand those within the component
        collapse = buildCollapseCommandEnt(List.of(new NodeIDEnt(14), new NodeIDEnt(15)), Collections.emptyList(),
            containerType);
        commandRes = ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), collapse);
        newNodeID = getNewNodeId(commandRes);
        testExpandConfigured(wfId, newNodeID, appendNodeID(newNodeID, 14, containerType));
        testExpandConfigured(wfId, newNodeID, appendNodeID(newNodeID, 15, containerType));
    }

    private static NodeIDEnt appendNodeID(final NodeIDEnt idEnt, final int id, final ContainerTypeEnum containerType) {
        if (containerType == ContainerTypeEnum.COMPONENT) {
            return idEnt.appendNodeID(0).appendNodeID(id);
        } else {
            return idEnt.appendNodeID(id);
        }
    }

    /**
     * Test Replace Node command with a node from repository
     */
    public void testExecuteReplaceNodeCommandFromRepo() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var rowFilterFactory = "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory";
        var workflow = ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
        var nodes = workflow.getNodes();
        // Replace a normal node
        var targetNode = nodes.get("root:1");
        var command = buildReplaceNodeCommand(targetNode.getId(),
            builder(NodeFactoryKeyEntBuilder.class).setClassName(rowFilterFactory).setSettings(null).build(), null);
        // execute command
        ws().executeWorkflowCommand(wfId, getRootID(), command);

        var targetNodePosition = targetNode.getPosition();
        checkForNode("Create new node in the location of the target node", ws().getWorkflow(wfId, getRootID(), false),
            rowFilterFactory, targetNodePosition.getX(), targetNodePosition.getY());
        var connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        assertThat("connection still exists", connections.get("root:10_1").getSourceNode().toString(), is("root:1"));

        // undo
        ws().undoWorkflowCommand(wfId, getRootID());
        checkForNode("Should restore old node", ws().getWorkflow(wfId, getRootID(), false),
            "org.knime.base.node.util.sampledata.SampleDataNodeFactory", targetNodePosition.getX(),
            targetNodePosition.getY());
        connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        assertThat("connection still exists", connections.get("root:10_1").getSourceNode().toString(), is("root:1"));
    }

    /**
     * Test Replace metanode command with a node from repository (metanodes have a slightly different port mapping)
     */
    public void testExecuteReplaceNodeCommandFromRepoOnMetanode() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var rowAgreggatorFactory = "org.knime.base.node.preproc.rowagg.RowAggregatorNodeFactory";
        var workflow = ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
        var nodes = workflow.getNodes();
        var connection = workflow.getConnections().get("root:5_1");
        var position = builder(XYEnt.XYEntBuilder.class).setX(5).setY(6).build();
        var addBendpointCmd = builder(AddBendpointCommandEntBuilder.class).setConnectionId(connection.getId())
            .setKind(KindEnum.ADD_BENDPOINT).setPosition(position).setIndex(BigDecimal.valueOf(0)).build();
        ws().executeWorkflowCommand(wfId, getRootID(), addBendpointCmd);

        // Replace a metanode
        var targetNode = nodes.get("root:6");
        var command = buildReplaceNodeCommand(targetNode.getId(),
            builder(NodeFactoryKeyEntBuilder.class).setClassName(rowAgreggatorFactory).setSettings(null).build(), null);
        // execute command
        ws().executeWorkflowCommand(wfId, getRootID(), command);

        var targetNodePosition = targetNode.getPosition();
        var newNode = checkForNode("Create new node in the location of the target node",
            ws().getWorkflow(wfId, getRootID(), false), rowAgreggatorFactory, targetNodePosition.getX(),
            targetNodePosition.getY());
        var connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        var newId = newNode.getId();
        assertThat("Metanode is reconnected",
            connections.get(String.format("%s_1", newId.toString())).getSourceNode().toString(), is("root:2"));
        assertThat("Metanode is reconnected", connections.get("root:4_1").getSourceNode().toString(),
            is(newId.toString()));
        assertThat("Metanode is reconnected",
            connections.get("root:5_1").getSourceNode().toString(), is(newId.toString()));
        assertThat("Metanode connection has bendpoints",
            connections.get("root:5_1").getBendpoints(), equalTo(List.of(position)));

        // undo
        ws().undoWorkflowCommand(wfId, getRootID());
        connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        assertThat("Should restore all connections", connections.get("root:6_0").getSourceNode().toString(),
            is("root:2"));
        assertThat("Should restore all connections", connections.get("root:4_1").getSourceNode().toString(),
            is("root:6"));
        assertThat("Should restore all connections", connections.get("root:5_1").getSourceNode().toString(),
            is("root:6"));
        assertThat("Bendpoints are restored",
            connections.get("root:5_1").getBendpoints(), equalTo(List.of(position)));
    }

    /**
     * Test Replace Node command with an existing node
     */
    public void testExecuteReplaceNodeCommandFromExistingNode() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        // delete Existing connection on Component
        var deleteCommand =
            DeleteCommandTestHelper.createDeleteCommandEnt(asList(), asList(new ConnectionIDEnt(new NodeIDEnt(23), 1)), asList());
        ws().executeWorkflowCommand(wfId, getRootID(), deleteCommand);

        var command = buildReplaceNodeCommand(new NodeIDEnt(2), null, new NodeIDEnt(23));
        var workflow = ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
        var nodes = workflow.getNodes();
        var targetNode = nodes.get("root:2").getPosition();
        var connections = workflow.getConnections();
        // execute command
        ws().executeWorkflowCommand(wfId, getRootID(), command);

        workflow = ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
        var replacementNodePos = workflow.getNodes().get("root:23").getPosition();
        assertThat("Move Metanode to the x location of target node", replacementNodePos.getX(), is(targetNode.getX()));
        assertThat("Move Metanode to the y location of target node", replacementNodePos.getY(), is(targetNode.getY()));
        connections = workflow.getConnections();
        assertThat("Metanode is reconnected", connections.get("root:23_1").getSourceNode().toString(), is("root:1"));
        assertThat("Metanode is reconnected", connections.get("root:6_0").getSourceNode().toString(), is("root:23"));
        assertThat("Metanode is reconnected", connections.get("root:11_1").getSourceNode().toString(), is("root:23"));

        // undo
        ws().undoWorkflowCommand(wfId, getRootID());
        var columnFilterFactory = "org.knime.base.node.preproc.filter.column.DataColumnSpecFilterNodeFactory";
        checkForNode("Should restore old target node", ws().getWorkflow(wfId, getRootID(), false),
            columnFilterFactory, targetNode.getX(), targetNode.getY());
        connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        assertThat("Should restore all connections", connections.get("root:2_1").getSourceNode().toString(), is("root:1"));
        assertThat("Should restore all connections", connections.get("root:6_0").getSourceNode().toString(), is("root:2"));
        assertThat("Should restore all connections", connections.get("root:11_1").getSourceNode().toString(), is("root:2"));
    }

    private static ReplaceNodeCommandEnt buildReplaceNodeCommand(final NodeIDEnt targetNodeId,
        final NodeFactoryKeyEnt nodeFactory, final NodeIDEnt replacementNodeId) {
        return builder(ReplaceNodeCommandEntBuilder.class)//
            .setKind(KindEnum.REPLACE_NODE)//
            .setTargetNodeId(targetNodeId)//
            .setReplacementNodeId(replacementNodeId)//
            .setNodeFactory(nodeFactory).build();
    }

    /**
     * Test Insert Node command with existing Node
     */
    public void testExecuteInsertNodeCommand() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var command = buildInsertNodeCommand(new ConnectionIDEnt(new NodeIDEnt(2), 1),
            builder(XYEntBuilder.class).setX(0).setY(0).build(), null, new NodeIDEnt(183));

        // execute command
        ws().executeWorkflowCommand(wfId, getRootID(), command);

        var connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        var ingoingConnection = connections.get("root:183_1");
        assertThat("connection between src and node exists", ingoingConnection.getSourceNode().toString(), is("root:1"));

        var outgoingConnection = connections.get("root:2_1");
        assertThat("connection between node and dest exists", outgoingConnection.getSourceNode().toString(),
            is("root:183"));

        // undo
        ws().undoWorkflowCommand(wfId, getRootID());

        connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        var connection = connections.get("root:2_1");
        assertThat("source should be original", connection.getSourceNode().toString(), is("root:1"));
    }

    /**
     * Test Insert Node command with node from repository
     */
    public void testExecuteInsertNodeCommandRepository() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var columnAppenderFactory = "org.knime.base.node.preproc.columnappend.ColumnAppenderNodeFactory";
        var command = buildInsertNodeCommand(new ConnectionIDEnt(new NodeIDEnt(2), 1),
            builder(XYEntBuilder.class).setX(0).setY(0).build(),
            builder(NodeFactoryKeyEntBuilder.class).setClassName(columnAppenderFactory).setSettings(null).build(),
            null);

        // execute command
        ws().executeWorkflowCommand(wfId, getRootID(), command);

        var workflow = ws().getWorkflow(wfId, getRootID(), false).getWorkflow();
        var nodes = workflow.getNodes();
        var newNode = nodes.entrySet().stream()
            .filter(entry -> entry.getValue() instanceof NativeNodeEnt && ((NativeNodeEnt)entry.getValue()).getTemplateId().equals(columnAppenderFactory))
            .findFirst().get().getValue();
        var connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        var ingoingConnection = connections.get(newNode.getId().toString() + "_1");
        assertThat(
            "connection between src and node exists", ingoingConnection.getSourceNode().toString(), is("root:1"));

        var outgoingConnection = connections.get("root:2_1");
        assertThat("connection between node and dest exists", outgoingConnection.getSourceNode().toString(),
            is(newNode.getId().toString()));

        // undo
        ws().undoWorkflowCommand(wfId, getRootID());

        connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        var connection = connections.get("root:2_1");
        assertThat("source should be original", connection.getSourceNode().toString(), is("root:1"));
    }

    /**
     * Test Insert Node command inside metanode
     */
    public void testExecuteInsertNodeMetanode() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = new NodeIDEnt(6);

        var columnAppenderFactory = "org.knime.base.node.preproc.columnappend.ColumnAppenderNodeFactory";
        var command = buildInsertNodeCommand(new ConnectionIDEnt(new NodeIDEnt(6), 0),
            builder(XYEntBuilder.class).setX(0).setY(0).build(),
            builder(NodeFactoryKeyEntBuilder.class).setClassName(columnAppenderFactory).setSettings(null).build(),
            null);

        // execute command
        ws().executeWorkflowCommand(wfId, workflowId, command);

        var nodes = ws().getWorkflow(wfId, workflowId, false).getWorkflow().getNodes();
        var newNode = nodes.entrySet().stream()
            .filter(entry -> entry.getValue() instanceof NativeNodeEnt && ((NativeNodeEnt)entry.getValue()).getTemplateId().equals(columnAppenderFactory))
            .findFirst().get().getValue();
        var connections = ws().getWorkflow(wfId, workflowId, false).getWorkflow().getConnections();
        var ingoingConnection = connections.get(newNode.getId().toString() + "_1");
        assertThat(
            "connection between src and node exists", ingoingConnection.getSourceNode().toString(), is("root:6:3"));

        var outgoingConnection = connections.get("root:6_0");
        assertThat("connection between node and dest exists", outgoingConnection.getSourceNode().toString(),
            is(newNode.getId().toString()));

        // undo
        ws().undoWorkflowCommand(wfId, workflowId);

        connections = ws().getWorkflow(wfId, workflowId, false).getWorkflow().getConnections();
        var connection = connections.get("root:6_1");
        assertThat("source should be original", connection.getSourceNode().toString(), is("root:6:3"));
    }

    private static InsertNodeCommandEnt buildInsertNodeCommand(final ConnectionIDEnt connection, final XYEnt position,
        final NodeFactoryKeyEnt nodeFactory, final NodeIDEnt nodeId) {
        return builder(InsertNodeCommandEntBuilder.class)//
            .setKind(KindEnum.INSERT_NODE)//
            .setConnectionId(connection).setPosition(position).setNodeId(nodeId).setNodeFactory(nodeFactory)
            .setNodeId(nodeId).build();
    }

    /**
     * Tests the {@link AddNodeCommandEnt} with {@link AddNodeCommandEnt#getUrl()} set.
     */
    public void testAddNodeCommandFromURI() throws Exception {
        var nodeFactoryProvider = mock(NodeFactoryProvider.class);
        Class nodeFactoryClass = org.knime.core.node.extension.NodeFactoryProvider.getInstance() // NOSONAR
            .getNodeFactory("org.knime.base.node.io.filehandling.csv.reader.CSVTableReaderNodeFactory").orElseThrow()
            .getClass();
        Mockito.when(nodeFactoryProvider.fromFileExtension(ArgumentMatchers.endsWith("file.csv")))
            .thenReturn(nodeFactoryClass);
        ServiceDependencies.setServiceDependency(NodeFactoryProvider.class, nodeFactoryProvider);
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var addNodeCommand = builder(AddNodeCommandEntBuilder.class).setKind(KindEnum.ADD_NODE)//
            .setUrl("file:/file.csv").setPosition(builder(XYEntBuilder.class).setX(0).setY(0).build())//
            .build();
        AddNodeResultEnt res = (AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), addNodeCommand);
        var nodes = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getNodes();
        assertThat(((NativeNodeEnt)nodes.get(res.getNewNodeId().toString())).getTemplateId(),
            is("org.knime.base.node.io.filehandling.csv.reader.CSVTableReaderNodeFactory"));
    }

    /**
     * Tests the {@link AddNodeCommandEnt} with {@link AddNodeCommandEnt#getSpaceItemId()} set.
     */
    public void testAddNodeCommandFromSpaceItemId() throws Exception {
        var nodeFactoryProvider = mock(NodeFactoryProvider.class);
        var spaceProvider = mock(SpaceProvider.class);
        var space = mock(Space.class);
        when(spaceProvider.getSpace(eq("spaceId"))).thenReturn(space);
        when(spaceProvider.getId()).thenReturn("providerId");
        when(spaceProvider.getType()).thenReturn(SpaceProviderEnt.TypeEnum.HUB);
        when(space.toPathBasedKnimeUrl(eq("itemId"))).thenReturn(URI.create("knime://LOCAL/test.csv"));
        var spaceProvidersManager = SpaceServiceTestHelper.createSpaceProvidersManager(spaceProvider);

        Class nodeFactoryClass = org.knime.core.node.extension.NodeFactoryProvider.getInstance() // NOSONAR
            .getNodeFactory("org.knime.base.node.io.filehandling.csv.reader.CSVTableReaderNodeFactory").orElseThrow()
            .getClass();
        Mockito.when(nodeFactoryProvider.fromFileExtension(ArgumentMatchers.endsWith("test.csv")))
            .thenReturn(nodeFactoryClass);
        ServiceDependencies.setServiceDependency(NodeFactoryProvider.class, nodeFactoryProvider);
        ServiceDependencies.setServiceDependency(SpaceProvidersManager.class, spaceProvidersManager);
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);

        var addNodeCommand = builder(AddNodeCommandEntBuilder.class).setKind(KindEnum.ADD_NODE)//
            .setSpaceItemReference(builder(SpaceItemReferenceEntBuilder.class).setItemId("itemId").setSpaceId("spaceId")
                .setProviderId("providerId").build())
            .setPosition(builder(XYEntBuilder.class).setX(0).setY(0).build())//
            .build();
        AddNodeResultEnt res = (AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), addNodeCommand);
        var nodes = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getNodes();
        assertThat(((NativeNodeEnt)nodes.get(res.getNewNodeId().toString())).getTemplateId(),
            is("org.knime.base.node.io.filehandling.csv.reader.CSVTableReaderNodeFactory"));
    }

    /**
     * Tests {@link TransformWorkflowAnnotationCommandEnt}.
     */
    public void testTransformWorkflowAnnotationCommand() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var annotationEnt = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(0);
        var boundsEnt = annotationEnt.getBounds();
        assertThat(boundsEnt.getX(), is(20));
        assertThat(boundsEnt.getY(), is(20));
        assertThat(boundsEnt.getWidth(), is(250));
        assertThat(boundsEnt.getHeight(), is(140));

        var newBounds = builder(BoundsEntBuilder.class).setX(4).setY(5).setWidth(10).setHeight(15).build();
        var command = builder(TransformWorkflowAnnotationCommandEntBuilder.class)//
            .setKind(KindEnum.TRANSFORM_WORKFLOW_ANNOTATION)//
            .setAnnotationId(annotationEnt.getId())//
            .setBounds(newBounds)//
            .build();
        ws().executeWorkflowCommand(wfId, getRootID(), command);

        annotationEnt = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(0);
        boundsEnt = annotationEnt.getBounds();
        assertThat(boundsEnt.getX(), is(4));
        assertThat(boundsEnt.getY(), is(5));
        assertThat(boundsEnt.getWidth(), is(10));
        assertThat(boundsEnt.getHeight(), is(15));

        ws().undoWorkflowCommand(wfId, getRootID());

        annotationEnt = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(0);
        boundsEnt = annotationEnt.getBounds();
        assertThat(boundsEnt.getX(), is(20));
        assertThat(boundsEnt.getY(), is(20));
        assertThat(boundsEnt.getWidth(), is(250));
        assertThat(boundsEnt.getHeight(), is(140));

        // test invalid annotation id
        var command2 = builder(TransformWorkflowAnnotationCommandEntBuilder.class)//
            .setKind(KindEnum.TRANSFORM_WORKFLOW_ANNOTATION)//
            .setAnnotationId(new AnnotationIDEnt("root_9999"))//
            .setBounds(newBounds)//
            .build();
        assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command2));

        // test command without change
        var command3 = builder(TransformWorkflowAnnotationCommandEntBuilder.class)//
            .setKind(KindEnum.TRANSFORM_WORKFLOW_ANNOTATION)//
            .setAnnotationId(annotationEnt.getId())//
            .setBounds(boundsEnt)//
            .build();
        ws().executeWorkflowCommand(wfId, getRootID(), command3);
        assertThrows("No command to undo", OperationNotAllowedException.class,
            () -> ws().undoWorkflowCommand(wfId, getRootID()));

    }

    /**
     * Tests {@link ReorderWorkflowAnnotationsCommandEnt} with a single annotation selected.
     */
    public void testReorderWorkflowAnnotationsCommandWithSingleAnnotation() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = getRootID();
        var annotationEnts = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getWorkflowAnnotations();
        var annotationCount = annotationEnts.size();
        assertThat("Could not perform test since there are less than 2 workflow annotations present", annotationCount,
            greaterThanOrEqualTo(2));

        // Bring bottom annotation forward
        assertReorderWorkflowAnnotationsCommand(projectId, ActionEnum.BRING_FORWARD, 0, 1);
        // Bring bottom annotation to front
        assertReorderWorkflowAnnotationsCommand(projectId, ActionEnum.BRING_TO_FRONT, 0,
            annotationCount - 1);
        // Send top annotation backward
        assertReorderWorkflowAnnotationsCommand(projectId, ActionEnum.SEND_BACKWARD, annotationCount - 1,
            annotationCount - 2);
        // Send top annotation to back
        assertReorderWorkflowAnnotationsCommand(projectId, ActionEnum.SEND_TO_BACK, annotationCount - 1, 0);

        // Invalid annotation ID
        var command = builder(ReorderWorkflowAnnotationsCommandEntBuilder.class)//
            .setKind(KindEnum.REORDER_WORKFLOW_ANNOTATIONS)//
            .setAnnotationIds(List.of(new AnnotationIDEnt("root_123456789")))//
            .setAction(ActionEnum.BRING_FORWARD)//
            .build();
        assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, getRootID(), command));
    }

    /**
     * Tests {@link ReorderWorkflowAnnotationsCommandEnt} within a metanode. This test doesn't cover all the cases, its
     * purpose is to assert the command logic also works within workflows that aren't the root workflow.
     */
    public void testReorderWorkflowAnnotationsCommandWithinMetanode() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var workflowId = new NodeIDEnt(6);
        var annotationEnts = ws().getWorkflow(projectId, workflowId, false).getWorkflow().getWorkflowAnnotations();
        var annotationCount = annotationEnts.size();
        assertThat("Could not perform test since there are less than 2 workflow annotations present", annotationCount,
            greaterThanOrEqualTo(2));
        assertReorderWorkflowAnnotationsCommand(projectId, ActionEnum.BRING_FORWARD, 0, 1);
    }

    private void assertReorderWorkflowAnnotationsCommand(final String projectId, final ActionEnum action,
        final int initialIndex, final int finalIndex) throws Exception {
        var annotationEnt =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(initialIndex);
        var command = builder(ReorderWorkflowAnnotationsCommandEntBuilder.class)//
            .setKind(KindEnum.REORDER_WORKFLOW_ANNOTATIONS)//
            .setAnnotationIds(List.of(annotationEnt.getId()))//
            .setAction(action)//
            .build();

        ws().executeWorkflowCommand(projectId, getRootID(), command);
        var annotationEntAfterCommandExecution =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(finalIndex);
        assertThat(annotationEnt, is(annotationEntAfterCommandExecution));

        ws().undoWorkflowCommand(projectId, getRootID());
        var annotationEntAfterUndoCommand =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(initialIndex);
        assertThat(annotationEnt, is(annotationEntAfterUndoCommand));
    }

    /**
     * Tests {@link ReorderWorkflowAnnotationCommandEnt} with a multiple annotation selected.
     */
    public void testReorderWorkflowAnnotationsCommandWithMultipleAnnotations() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var annotationEnts = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations();
        var annotationCount = annotationEnts.size();
        assertThat("Could not perform test since there are less than 5 workflow annotations present", annotationCount,
            greaterThanOrEqualTo(5));

        // Bring two bottom annotations forward twice
        assertReorderWorkflowAnnotationsCommandSequence(projectId, ActionEnum.BRING_FORWARD, Pair.create(0, 2),
            Pair.create(2, 4));
        // Send two top annotations backward twice
        assertReorderWorkflowAnnotationsCommandSequence(projectId, ActionEnum.SEND_BACKWARD,
            Pair.create(annotationCount - 1, annotationCount - 3),
            Pair.create(annotationCount - 3, annotationCount - 5));
        // Bring two bottom annotations to front twice
        assertReorderWorkflowAnnotationsCommandSequence(projectId, ActionEnum.BRING_TO_FRONT, Pair.create(0, 2),
            Pair.create(annotationCount - 2, annotationCount - 1));
        // Send two top annotations to back twice
        assertReorderWorkflowAnnotationsCommandSequence(projectId, ActionEnum.SEND_TO_BACK,
            Pair.create(annotationCount - 1, annotationCount - 3), Pair.create(1, 0));
    }

    private void assertReorderWorkflowAnnotationsCommandSequence(final String projectId, final ActionEnum action,
        final Pair<Integer, Integer> initialIndices, final Pair<Integer, Integer> finalIndices) throws Exception {
        var annotationEnt1 = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations()
            .get(initialIndices.getFirst());
        var annotationEnt2 = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations()
            .get(initialIndices.getSecond());
        var command = builder(ReorderWorkflowAnnotationsCommandEntBuilder.class)//
            .setKind(KindEnum.REORDER_WORKFLOW_ANNOTATIONS)//
            .setAnnotationIds(List.of(annotationEnt1.getId(), annotationEnt2.getId()))//
            .setAction(action)//
            .build();

        ws().executeWorkflowCommand(projectId, getRootID(), command);
        ws().executeWorkflowCommand(projectId, getRootID(), command);
        var annotationEnt1AfterCommandExecution = ws().getWorkflow(projectId, getRootID(), false).getWorkflow()
            .getWorkflowAnnotations().get(finalIndices.getFirst());
        var annotationEnt2AfterCommandExecution = ws().getWorkflow(projectId, getRootID(), false).getWorkflow()
            .getWorkflowAnnotations().get(finalIndices.getSecond());
        assertThat(annotationEnt1, is(annotationEnt1AfterCommandExecution));
        assertThat(annotationEnt2, is(annotationEnt2AfterCommandExecution));

        ws().undoWorkflowCommand(projectId, getRootID());
        ws().undoWorkflowCommand(projectId, getRootID());
        var annotationEnt1AfterUndoCommand = ws().getWorkflow(projectId, getRootID(), false).getWorkflow()
            .getWorkflowAnnotations().get(initialIndices.getFirst());
        var annotationEnt2AfterUndoCommand = ws().getWorkflow(projectId, getRootID(), false).getWorkflow()
            .getWorkflowAnnotations().get(initialIndices.getSecond());
        assertThat(annotationEnt1, is(annotationEnt1AfterUndoCommand));
        assertThat(annotationEnt2, is(annotationEnt2AfterUndoCommand));
    }

    /**
     * Tests {@link UpdateWorkflowAnnotationCommandEnt} with only text to update.
     */
    public void testUpdateWorkflowAnnotationText() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var annotationIdx = 4; // Using that specific annotation for the test
        var annotationEnt =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(annotationIdx);
        var previousText = annotationEnt.getText().getValue();
        var previousStyleRanges = annotationEnt.getStyleRanges();
        var previousTextAlignment = annotationEnt.getTextAlign();
        var formattedText =
            """
            <p>this is a text with <strong>bold</strong>,<em>italic</em> <strong><em>bolditalic</em></strong>, <u>underline</u></p>
            <ul>
                <li><p><u>list item</u></p></li>
                <li><p><u>list item</u></p></li>
            </ul>
            <p></p>
            <p style="text-align: right">and right aligned text</p><p style="text-align: right"></p>
            """;

        assertThat("Content type isn't plain text prior to execution", annotationEnt.getText().getContentType(),
            is(ContentTypeEnum.PLAIN));

        var command = buildUpdateWorkflowAnnotationCommand(annotationEnt, formattedText, null);
        ws().executeWorkflowCommand(projectId, getRootID(), command);
        var annotationEntAfterExecution =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(annotationIdx);

        assertThat("Text field wasn't updated", annotationEntAfterExecution.getText().getValue(), is(formattedText));
        assertThat("Content type isn't HTML text", annotationEntAfterExecution.getText().getContentType(),
            is(ContentTypeEnum.HTML));
        assertThat("Style ranges aren't gone", annotationEntAfterExecution.getStyleRanges(), nullValue());
        assertThat("Text alignment isn't gone", annotationEntAfterExecution.getTextAlign(), nullValue());

        ws().undoWorkflowCommand(projectId, getRootID());
        var annotationEntAfterUndo =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(annotationIdx);

        assertThat("Regular text field wasn't reset", annotationEntAfterUndo.getText().getValue(), is(previousText));
        assertThat("Content type isn't plain text again", annotationEnt.getText().getContentType(),
            is(ContentTypeEnum.PLAIN));
        assertThat("Not exactly 18 style ranges are back", annotationEntAfterUndo.getStyleRanges().size(), is(18));
        assertThat("Not the same style ranges were brought back", annotationEntAfterUndo.getStyleRanges(),
            is(previousStyleRanges));
        assertThat("Text alignment isn't back", annotationEntAfterUndo.getTextAlign(), is(previousTextAlignment));
    }

    private static UpdateWorkflowAnnotationCommandEnt buildUpdateWorkflowAnnotationCommand(
        final WorkflowAnnotationEnt annotationEnt, final String text, final String borderColor) {
        return builder(UpdateWorkflowAnnotationCommandEntBuilder.class)//
            .setKind(KindEnum.UPDATE_WORKFLOW_ANNOTATION)//
            .setAnnotationId(annotationEnt.getId())//
            .setText(text)//
            .setBorderColor(borderColor)//
            .build();
    }

    /**
     * Tests {@link AddWorkflowAnnotationCommandEnt}.
     */
    public void testAddWorkflowAnnotation() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.HOLLOW);

        var annotationsBefore = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations();
        assertThat("There should not be any annotations before command execution", annotationsBefore.size(), is(0));

        var bounds = builder(BoundsEntBuilder.class)//
            .setX(32)//
            .setY(64)//
            .setWidth(128)//
            .setHeight(256)//
            .build();
        var command = builder(AddWorkflowAnnotationCommandEntBuilder.class)//
            .setKind(KindEnum.ADD_WORKFLOW_ANNOTATION)//
            .setBounds(bounds)//
            .setBorderColor("#C0C4C6")//
            .build();

        var result = ws().executeWorkflowCommand(projectId, getRootID(), command);
        var annotationsAfterExecution =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations();

        assertThat("There should be exactly one annotation after command execution", annotationsAfterExecution.size(),
            is(1));
        var annotation = annotationsAfterExecution.get(0);
        assertThat("Bounds don't match the bounds passed", annotation.getBounds(), is(bounds));

        if (result instanceof AddAnnotationResultEnt addAnnotationResult) {
            assertThat("Unexpected annotation ID returned", addAnnotationResult.getNewAnnotationId().toString(),
                is("root_0"));
        } else {
            throw new Exception("Unexpected command result returned");
        }

        assertThat("Content type must isn't text/html", annotation.getText().getContentType(),
            is(ContentTypeEnum.HTML));
        assertThat("The text field wasn't empty.", annotation.getText().getValue(), is(""));

        ws().undoWorkflowCommand(projectId, getRootID());
        var annotationsAfterUndo =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations();

        assertThat("There should not be any annotatin after command undo", annotationsAfterUndo.size(), is(0));
    }

    /**
     * Tests {@link UpdateWorkflowAnnotationCommandEnt} with only the color to update.
     */
    public void testUpdateWorkflowAnnotationColor() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var initialBorderColor = "#FFD800";
        var newBorderColor = "#000000";
        var annotationEnt =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(0);
        assertThat("Unexpected previous border color", annotationEnt.getBorderColor(), is(initialBorderColor));

        // Test normal command
        var command = buildUpdateWorkflowAnnotationCommand(annotationEnt, null, newBorderColor);
        ws().executeWorkflowCommand(projectId, getRootID(), command);
        var annotationEntAfterExecution =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(0);
        assertThat("Unexpected border color after command execution", annotationEntAfterExecution.getBorderColor(),
            is(newBorderColor));

        ws().undoWorkflowCommand(projectId, getRootID());
        var annotationEntAfterUndo =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations().get(0);
        assertThat("Border color should be reset to previous one", annotationEntAfterUndo.getBorderColor(),
            is(initialBorderColor));

        // Test command without change
        var commandWithoutChange =
            buildUpdateWorkflowAnnotationCommand(annotationEntAfterUndo, null, initialBorderColor);
        ws().executeWorkflowCommand(projectId, getRootID(), commandWithoutChange);
        assertThrows("No command to undo", OperationNotAllowedException.class,
            () -> ws().undoWorkflowCommand(projectId, getRootID()));

        // Test both arguments are missing case
        var commandMissing = buildUpdateWorkflowAnnotationCommand(annotationEntAfterUndo, null, null);
        assertThrows("Cannot update a workflow annotation with neither a border color nor a text provided.",
            OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, getRootID(), commandMissing));
    }

    /**
     * Tests {@link UpdateWorkflowAnnotationCommandEnt} with text and color to update.
     */
    public void testUpdateWorkflowAnnotationTextAndColor() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.ANNOTATIONS);
        var annotationEnts = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations();
        assertThat("There should be exactly 2 annotations before execution", annotationEnts.size(), is(2));

        var newText = "New text";
        var newBorderColor = "#000000";
        for (var annotationEnt : annotationEnts) {
            var command = buildUpdateWorkflowAnnotationCommand(annotationEnt, newText, newBorderColor);
            ws().executeWorkflowCommand(projectId, getRootID(), command);
        }

        var annotationEntsAfterExecution =
            ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getWorkflowAnnotations();
        annotationEntsAfterExecution.forEach(annotationEnt -> {
            assertThat("The content type was not updated", annotationEnt.getText().getContentType(),
                is(ContentTypeEnum.HTML));
            assertThat("The annotation text was not updated", annotationEnt.getText().getValue(), is(newText));
            assertThat("The border color was not updated", annotationEnt.getBorderColor(), is(newBorderColor));
        });
    }

    /**
     * Test execute-undo-redo for updating component metadata from within a component workflow.
     */
    public void testUpdateComponentMetadataFromWithin() throws Exception {
        // we need to load a workflow corresponding to a Container/Subnode
        var projectId = loadWorkflow(TestWorkflowCollection.METADATA);
        var componentId = getRootID().appendNodeID(4);
        // extract original properties to check against them later
        var originalMetadata = ws().getWorkflow(projectId, componentId, false).getWorkflow().getComponentMetadata();
        // new properties to be sent with the command
        var newDescription = EntityUtil.toTypedTextEnt("<p>bla bla bla</p>", ContentTypeEnum.HTML);
        var newTags = List.of("foo", "bar");
        var newLinks = List.of(buildLinkEnt("https://yeah.com", "sure thing"));
        var newIcon = "data:image/png;base64,"
            + "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAPVJREFUOI2dk1FxwzAQRHddAoZgCmGgMCgEB0HCYCUGZZBAKIOMERRCHAZB4MuPPCPLkibT+zztrfaeRl+IJWl0zvXTNM1oVK7r1gOSInmV1LcMcl0XXT1JHwXnxu07HSUNJB8k3bIsT5J3MzuGEOZsuKjrSAoAzOxC8g/AQPJaiv6JbgdMkmtputbhJ2CrBpI8gCFGrYJlZXgg+UhaLzM75GCrCSKwHwC32OprwHYGkkYAI4CX9/5kZicAMwBXArozWJ9rrRDCzcyOAH5LQDcGCbi018dE3ygAZSLMwXkzm+LuqekG6OYzZdtcSN7zRGgA/Ve9ASTnd38pmtoyAAAAAElFTkSuQmCC";
        var newType = UpdateComponentMetadataCommandEnt.TypeEnum.OTHER;
        var newInPorts = List.of(builder(ComponentPortDescriptionEnt.ComponentPortDescriptionEntBuilder.class)
            .setName("new in port 1 name").setDescription("new port 1 description").build());
        var newOutPorts = List.of(builder(ComponentPortDescriptionEnt.ComponentPortDescriptionEntBuilder.class)
            .setName("new out port 1 name").setDescription("new out port 1 description").build());
        var command = buildUpdateComponentMetadataCommand(newDescription, newTags, newLinks, newIcon, newType,
            newInPorts, newOutPorts);
        // execute
        ws().executeWorkflowCommand(projectId, componentId, command);
        var modifiedMetadata = ws().getWorkflow(projectId, componentId, false).getWorkflow().getComponentMetadata();
        // assert result
        assertProjectMetadata(modifiedMetadata, newDescription, newTags, newLinks);
        assertComponentMetadata(modifiedMetadata, newIcon, newType, newInPorts, newOutPorts);
        // undo
        ws().undoWorkflowCommand(projectId, componentId);
        var metadataUndo = ws().getWorkflow(projectId, componentId, false).getWorkflow().getComponentMetadata();
        assertProjectMetadata(metadataUndo, originalMetadata);
        assertComponentMetadata(metadataUndo, originalMetadata);
        // redo
        ws().redoWorkflowCommand(projectId, componentId);
        var metadataRedo = ws().getWorkflow(projectId, componentId, false).getWorkflow().getComponentMetadata();
        assertProjectMetadata(metadataRedo, newDescription, newTags, newLinks);
        assertComponentMetadata(metadataRedo, newIcon, newType, newInPorts, newOutPorts);
    }

    /**
     * Test that workflow was not modified if a component metadata update command requesting the exact same values is
     * handled.
     */
    public void testComponentMetadataNotUpdatedIfNoChange() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.METADATA);
        var componentId = getRootID().appendNodeID(4);
        var originalMetadata = ws().getWorkflow(projectId, componentId, false).getWorkflow().getComponentMetadata();
        var originalType = UpdateComponentMetadataCommandEnt.TypeEnum.valueOf(originalMetadata.getType().name());
        var originalInPorts = toComponentPortDescription(originalMetadata.getInPorts());
        var originalOutPorts = toComponentPortDescription(originalMetadata.getOutPorts());
        var command = buildUpdateComponentMetadataCommand(originalMetadata.getDescription(), originalMetadata.getTags(),
            originalMetadata.getLinks(), originalMetadata.getIcon(), originalType, originalInPorts, originalOutPorts);
        // execute
        ws().executeWorkflowCommand(projectId, componentId, command);
        WorkflowEnt workflow = ws().getWorkflow(projectId, componentId, false).getWorkflow();
        assertThat("Workflow should not be dirty", !workflow.isDirty());
    }

    private List<ComponentPortDescriptionEnt>
        toComponentPortDescription(final List<NodePortDescriptionEnt> nodePortDescriptions) {
        return nodePortDescriptions.stream().map(nodePortDescription -> {
            return builder(ComponentPortDescriptionEnt.ComponentPortDescriptionEntBuilder.class)
                .setName(nodePortDescription.getName()).setDescription(nodePortDescription.getDescription()).build();
        }).toList();
    }

    /**
     * Tests {@link UpdateProjectMetadataCommandEnt} using legacy workflow metadata format.
     */
    public void testUpdateProjectMetadata() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.METADATA); // This uses the legacy workflow metadata format
        var metadataBefore = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getProjectMetadata();
        var oldDescription =
            EntityUtil.toTypedTextEnt("Workflow with metadata\n\nThe workflow description", ContentTypeEnum.PLAIN);
        var oldTags = List.of("tag1", "tag2");
        var oldLinks = List.of(buildLinkEnt("http://blub.com", "BLUB"));
        assertProjectMetadata(metadataBefore, oldDescription, oldTags, oldLinks);

        // Test successful case
        var description = EntityUtil.toTypedTextEnt("<p>bla bla bla</p>", ContentTypeEnum.HTML);
        var tags = List.of("foo", "bar");
        var links = List.of(buildLinkEnt("https://yeah.com", "sure thing"));
        var command = buildUpdateProjectMetadataCommand(description, tags, links);
        ws().executeWorkflowCommand(projectId, getRootID(), command);
        var metadataAfter = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getProjectMetadata();
        assertProjectMetadata(metadataAfter, description, tags, links);

        // Undo
        ws().undoWorkflowCommand(projectId, getRootID());
        var metadataUndo = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getProjectMetadata();
        assertProjectMetadata(metadataUndo, oldDescription, oldTags, oldLinks);

        // Redo
        ws().redoWorkflowCommand(projectId, getRootID());
        var metadataRedo = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getProjectMetadata();
        assertProjectMetadata(metadataRedo, description, tags, links);

        // No undo is possible
        ws().undoWorkflowCommand(projectId, getRootID());
        var voidCommand = buildUpdateProjectMetadataCommand(oldDescription, oldTags, oldLinks);
        ws().executeWorkflowCommand(projectId, getRootID(), voidCommand);
        assertThrows("No command to undo", OperationNotAllowedException.class,
            () -> ws().undoWorkflowCommand(projectId, getRootID()));
    }

    /**
     * Tests {@link UpdateProjectMetadataCommandEnt} using new workflow metadata format.
     */
    public void testUpdateProjectMetadataNewFormat() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.METADATA2); // This uses the new workflow metadata format
        var metadata = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getProjectMetadata();
        var oldDescription = EntityUtil.toTypedTextEnt("My new description...", ContentTypeEnum.PLAIN);
        var oldTags = List.of("tag1", "tag2", "tag3", "tag4");
        var oldLinks = List.of(buildLinkEnt("http://www.knime.com", "The KNIME website"),
            buildLinkEnt("http://www.yeah.com", "The yeah website"));
        var oldLastEdit = "2023-06-19T17:09:46.601+02:00";
        assertProjectMetadata(metadata, oldDescription, oldTags, oldLinks);
        assertThat("Unexpected last edit", metadata.getLastEdit().toString(), is(oldLastEdit));

        // Test successful case
        var description = EntityUtil.toTypedTextEnt("<p>bla bla bla</p>", ContentTypeEnum.HTML);
        var tags = List.of("foo", "bar");
        var links = List.of(buildLinkEnt("https://yeah.com", "sure thing"));
        var command = buildUpdateProjectMetadataCommand(description, tags, links);
        ws().executeWorkflowCommand(projectId, getRootID(), command);
        var metadataAfter = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getProjectMetadata();
        assertProjectMetadata(metadataAfter, description, tags, links);
        assertThat("Unexpected last edit", metadataAfter.getLastEdit().toString(), is(not(oldLastEdit)));

        // Undo
        ws().undoWorkflowCommand(projectId, getRootID());
        var metadataUndo = ws().getWorkflow(projectId, getRootID(), false).getWorkflow().getProjectMetadata();
        assertProjectMetadata(metadataUndo, oldDescription, oldTags, oldLinks);
    }

    private static void assertProjectMetadata(final ProjectMetadataEnt metadata, final TypedTextEnt description,
        final List<String> tags, final List<LinkEnt> links) {
        assertThat("Unexpected workflow description", metadata.getDescription(), is(description));
        assertThat("Unexpected links", metadata.getLinks(), is(links));
        assertThat("Unexpected tags", metadata.getTags(), is(tags));
    }

    private static void assertProjectMetadata(final ProjectMetadataEnt actualMetadata,
        final ProjectMetadataEnt expected) {
        assertProjectMetadata(actualMetadata, expected.getDescription(), expected.getTags(), expected.getLinks());
    }

    private void assertComponentMetadata(final ComponentNodeDescriptionEnt modifiedMetadata, final String newIcon,
        final UpdateComponentMetadataCommandEnt.TypeEnum newType, final List<ComponentPortDescriptionEnt> newInPorts,
        final List<ComponentPortDescriptionEnt> newOutPorts) {
        assertThat("Unexpected icon", modifiedMetadata.getIcon(), is(newIcon));
        assertThat("Unexpected type",
            UpdateComponentMetadataCommandEnt.TypeEnum.valueOf(modifiedMetadata.getType().name()), is(newType));

        Consumer<Pair<NodePortDescriptionEnt, ComponentPortDescriptionEnt>> compareTitleAndDescription = p -> {
            var actual = p.getFirst();
            var expected = p.getSecond();
            assertThat("Unexpected title", actual.getName(), is(expected.getName()));
            assertThat("Unexpected description", actual.getDescription(), is(expected.getDescription()));
        };
        zip(modifiedMetadata.getInPorts(), newInPorts).forEach(compareTitleAndDescription);
        zip(modifiedMetadata.getOutPorts(), newOutPorts).forEach(compareTitleAndDescription);
    }

    private void assertComponentMetadata(final ComponentNodeDescriptionEnt actualMetadata,
        final ComponentNodeDescriptionEnt expectedMetadata) {
        var type = UpdateComponentMetadataCommandEnt.TypeEnum.valueOf(actualMetadata.getType().name());
        var inPorts = toComponentPortDescription(actualMetadata.getInPorts());
        var outPorts = toComponentPortDescription(actualMetadata.getOutPorts());
        assertComponentMetadata(actualMetadata, expectedMetadata.getIcon(), type, inPorts, outPorts);
    }

    private <A, B> Stream<Pair<A, B>> zip(final List<A> a, final List<B> b) {
        assertThat("Lists of different length", a.size(), is(b.size()));
        return IntStream.range(0, Math.min(a.size(), b.size())).mapToObj(i -> Pair.create(a.get(i), b.get(i)));
    }

    private static LinkEnt buildLinkEnt(final String url, final String text) {
        return builder(LinkEntBuilder.class)//
            .setUrl(url)//
            .setText(text).build();
    }

    private static UpdateProjectMetadataCommandEnt buildUpdateProjectMetadataCommand(final TypedTextEnt description,
        final List<String> tags, final List<LinkEnt> links) {
        return builder(UpdateProjectMetadataCommandEntBuilder.class)//
            .setKind(KindEnum.UPDATE_PROJECT_METADATA)//
            .setDescription(description)//
            .setTags(tags)//
            .setLinks(links)//
            .build();
    }

    private static UpdateComponentMetadataCommandEnt buildUpdateComponentMetadataCommand(final TypedTextEnt description,
        final List<String> tags, final List<LinkEnt> links, final String icon,
        final UpdateComponentMetadataCommandEnt.TypeEnum type, final List<ComponentPortDescriptionEnt> inPorts,
        final List<ComponentPortDescriptionEnt> outPorts) {
        return builder(UpdateComponentMetadataCommandEnt.UpdateComponentMetadataCommandEntBuilder.class)
            .setKind(KindEnum.UPDATE_COMPONENT_METADATA).setDescription(description).setTags(tags).setLinks(links)
            .setIcon(icon).setType(type).setInPorts(inPorts) //
            .setOutPorts(outPorts) //
            .build();
    }

    /**
     * Tests {@link UpdateComponentOrMetanodeNameCommandEnt}.
     */
    public void testUpdateComponentLinkInformation() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var linkedComponent = new NodeIDEnt(1);
        var notLinkedComponent = new NodeIDEnt(10);
        var oldLink = "knime://LOCAL/Component/";
        var newLink = "newUrl";

        // Test happy path
        var command1 = buildUpdateComponentLinkInformationCommand(linkedComponent, newLink);
        var nodeBefore = getNodeEntFromWorkflowSnapshotEnt(
            ws().getWorkflow(projectId, NodeIDEnt.getRootID(), Boolean.FALSE), linkedComponent);
        assertComponentWithLink(nodeBefore, oldLink);

        ws().executeWorkflowCommand(projectId, getRootID(), command1);
        var nodeAfter = getNodeEntFromWorkflowSnapshotEnt(
            ws().getWorkflow(projectId, NodeIDEnt.getRootID(), Boolean.FALSE), linkedComponent);
        assertComponentWithLink(nodeAfter, newLink);

        // Test undo command
        ws().undoWorkflowCommand(projectId, getRootID());
        var nodeUndone = getNodeEntFromWorkflowSnapshotEnt(
            ws().getWorkflow(projectId, NodeIDEnt.getRootID(), Boolean.FALSE), linkedComponent);
        assertComponentWithLink(nodeUndone, oldLink);

        // Test not a component
        var command2 = buildUpdateComponentLinkInformationCommand(new NodeIDEnt(99), newLink);
        assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, getRootID(), command2));

        // Test not a linked component
        var command3 = buildUpdateComponentLinkInformationCommand(notLinkedComponent, newLink);
        assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, getRootID(), command3));

        // Test unlink a component
        var command4 = buildUpdateComponentLinkInformationCommand(linkedComponent, null);
        ws().executeWorkflowCommand(projectId, getRootID(), command4);
        var nodeUnlinked = getNodeEntFromWorkflowSnapshotEnt(
            ws().getWorkflow(projectId, NodeIDEnt.getRootID(), Boolean.FALSE), linkedComponent);
        assertComponentWithLink(nodeUnlinked, null);
    }

    private static UpdateComponentLinkInformationCommandEnt
        buildUpdateComponentLinkInformationCommand(final NodeIDEnt nodeIdEnt, final String newUrl) {
        return builder(UpdateComponentLinkInformationCommandEntBuilder.class)//
            .setKind(KindEnum.UPDATE_COMPONENT_LINK_INFORMATION)//
            .setNodeId(nodeIdEnt)//
            .setNewUrl(newUrl)//
            .build();
    }

    private static NodeEnt getNodeEntFromWorkflowSnapshotEnt(final WorkflowSnapshotEnt workflowSnapshotEnt,
        final NodeIDEnt nodeIdEnt) {
        return workflowSnapshotEnt//
            .getWorkflow()//
            .getNodes()//
            .entrySet()//
            .stream()//
            .filter(entry -> entry.getKey().equals(nodeIdEnt.toString()))//
            .findFirst()//
            .map(Entry::getValue)//
            .orElseThrow();
    }

    private static void assertComponentWithLink(final NodeEnt nodeEnt, final String expectedUrl) {
        assertThat("The node is not a component", nodeEnt, new IsInstanceOf(ComponentNodeEnt.class));
        var link = ((ComponentNodeEnt)nodeEnt).getLink();
        if (expectedUrl == null) {
            assertThat("There should not be a link", link, nullValue());
        } else {
            var actualUrl = ((ComponentNodeEnt)nodeEnt).getLink().getUrl();
            assertThat("The links do not match", actualUrl, equalTo(expectedUrl));
        }
    }

    public void testTransformMetanodePortsBarCommand() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var metanodeId = new NodeIDEnt(6);
        var originalMetaOutPortsBounds =
            ws().getWorkflow(projectId, metanodeId, Boolean.FALSE).getWorkflow().getMetaOutPorts().getBounds();

        var bounds = builder(BoundsEntBuilder.class).setX(4).setY(5).setWidth(10).setHeight(11).build();

        // metanode _in_ ports bar
        var command1 = builder(TransformMetanodePortsBarCommandEntBuilder.class)
            .setKind(KindEnum.TRANSFORM_METANODE_PORTS_BAR).setType(TypeEnum.IN).setBounds(bounds).build();
        ws().executeWorkflowCommand(projectId, metanodeId, command1);
        var newBounds =
            ws().getWorkflow(projectId, metanodeId, Boolean.FALSE).getWorkflow().getMetaInPorts().getBounds();
        assertThat(newBounds, is(bounds));
        ws().undoWorkflowCommand(projectId, metanodeId);
        var undoneBounds =
            ws().getWorkflow(projectId, metanodeId, Boolean.FALSE).getWorkflow().getMetaInPorts().getBounds();
        assertThat(undoneBounds, is(nullValue()));

        // metanode _out_ ports bar
        var command2 = builder(TransformMetanodePortsBarCommandEntBuilder.class)
            .setKind(KindEnum.TRANSFORM_METANODE_PORTS_BAR).setType(TypeEnum.OUT).setBounds(bounds).build();
        ws().executeWorkflowCommand(projectId, metanodeId, command2);
        newBounds = ws().getWorkflow(projectId, metanodeId, Boolean.FALSE).getWorkflow().getMetaOutPorts().getBounds();
        assertThat(newBounds, is(bounds));
        ws().undoWorkflowCommand(projectId, metanodeId);
        undoneBounds =
            ws().getWorkflow(projectId, metanodeId, Boolean.FALSE).getWorkflow().getMetaOutPorts().getBounds();
        assertThat(undoneBounds, is(originalMetaOutPortsBounds));

        // try to execute the command for a component
        var message = assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, new NodeIDEnt(12), command1)).getMessage();
        assertThat(message, is("Component don't have metanode ports bars. Can't be transformed."));
    }

    /**
     * Tests {@link WorkflowService#getUpdatableLinkedComponents(String, NodeIDEnt)} and
     * {@link UpdateLinkedComponentsCommandEnt} command execution.
     *
     * @throws Exception
     */
    public void testUpdateLinkedComponents() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.UPDATE_LINKED_COMPONENTS);
        var nativeNode = new NodeIDEnt(4);
        var updatableComponent1 = new NodeIDEnt(7); // workflow relative link
        var updatableComponent2 = new NodeIDEnt(10); // mount point relative link
        var updatableComponent3 = new NodeIDEnt(11); // absolute link
        var notLinkedComponent = new NodeIDEnt(9);
        var updatableComponents = Set.of(updatableComponent1, updatableComponent2, updatableComponent3);

        // Try to update a component with a broken link
        var command = buildUpdateLinkedComponentsCommand(List.of(new NodeIDEnt(13)));
        var result = (UpdateLinkedComponentsResultEnt)ws().executeWorkflowCommand(projectId, getRootID(), command);
        assertThat(result.getStatus(), is(UpdateLinkedComponentsResultEnt.StatusEnum.ERROR));
        assertThat(result.getDetails().get(0),
            Matchers.startsWith("Could not update <Linked Component with broken Link"));

        // Mock URI to file resolver
        var mockedResolver = Mockito.mock(URIToFileResolve.class);
        var oldResolver = URIToFileResolveTestUtil.replaceURIToFileResolveService(mockedResolver);
        when(mockedResolver.resolveToLocalOrTempFileConditional(any(), any(), any()))
            .thenReturn(Optional.of(TestWorkflowCollection.LINKED_COMPONENT.getWorkflowDir()));

        // Get updatable nodes
        var componentsAndState = ws().getUpdatableLinkedComponents(projectId, getRootID());
        var cc1 = componentsAndState.stream().map(NodeIdAndIsExecutedEnt::getId).toList();
        assertThat("List of updatable nodes unexpected", Set.copyOf(cc1), is(updatableComponents));

        // Update the nodes
        command = buildUpdateLinkedComponentsCommand(cc1);
        result = (UpdateLinkedComponentsResultEnt)ws().executeWorkflowCommand(projectId, getRootID(), command);
        assertThat("Component update status unexpected", result.getStatus(), is(StatusEnum.SUCCESS));
        var componentsAndStateAfterUpdate = ws().getUpdatableLinkedComponents(projectId, getRootID());
        assertThat("There shouldn't be any updatable components", Set.copyOf(componentsAndStateAfterUpdate),
            is(Collections.emptySet()));

        // Undo
        ws().undoWorkflowCommand(projectId, getRootID());
        var componentsAndStateAfterUndo = ws().getUpdatableLinkedComponents(projectId, getRootID());
        var cc2 = componentsAndStateAfterUndo.stream().map(NodeIdAndIsExecutedEnt::getId).toList();
        assertThat("List of updatable nodes unexpected", Set.copyOf(cc2), is(updatableComponents));

        // Redo
        ws().redoWorkflowCommand(projectId, getRootID());
        var componentsAndStateAfterRedo = ws().getUpdatableLinkedComponents(projectId, getRootID());
        assertThat("There shouldn't be any updatable components", Set.copyOf(componentsAndStateAfterRedo),
            is(Collections.emptySet()));

        // Try updating again, shouldn't change anything
        var resultAfterSecondUpdate =
            (UpdateLinkedComponentsResultEnt)ws().executeWorkflowCommand(projectId, getRootID(), command);
        assertThat("Nothing should have changed", resultAfterSecondUpdate.getStatus(), is(StatusEnum.UNCHANGED));

        // Operation not allowed
        var commandEmpty = buildUpdateLinkedComponentsCommand(List.of());
        assertThrows("Should not be allowed for emtpy lists", OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, getRootID(), commandEmpty));
        var commandWithNativeNode = buildUpdateLinkedComponentsCommand(List.of(nativeNode, updatableComponent1));
        assertThrows("Should not be allowed if list contains native nodes", OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, getRootID(), commandWithNativeNode));
        var commandWithNotLinked = buildUpdateLinkedComponentsCommand(List.of(notLinkedComponent, updatableComponent1));
        assertThrows("Should not be allowed if list contains components that are not links",
            OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(projectId, getRootID(), commandWithNotLinked));

        // Reset mocked resolver
        URIToFileResolveTestUtil.replaceURIToFileResolveService(oldResolver);
    }

    private static UpdateLinkedComponentsCommandEnt buildUpdateLinkedComponentsCommand(final List<NodeIDEnt> nodeIds) {
        return builder(UpdateLinkedComponentsCommandEntBuilder.class)//
            .setKind(KindEnum.UPDATE_LINKED_COMPONENTS)//
            .setNodeIds(nodeIds)//
            .build();
    }

    public void testTryCatchNoFailure() throws Exception {
        var nodes = executeWorkflowAndGetNodes(TestWorkflowCollection.TRY_CATCH);
        var tryCatchStart = "root:31";
        var tryCatchEnd = "root:32";
        var nodeInTry = "root:28";
        Stream.of(tryCatchStart, tryCatchEnd, nodeInTry) //
            .map(id -> getNativeNode(nodes, id)) //
            .forEach(node -> assertThat( //
                "Node is executed", //
                node.getState().getExecutionState() == NodeStateEnt.ExecutionStateEnum.EXECUTED //
            ));
    }

    public void testTryCatchFailureInTry() throws Exception {
        var nodes = executeWorkflowAndGetNodes(TestWorkflowCollection.TRY_CATCH);
        var nodeBeforeFailure = getNativeNode(nodes, "root:26");
        assertThat(nodeBeforeFailure.getState().getExecutionState(), is(NodeStateEnt.ExecutionStateEnum.EXECUTED));
        var nodeFailing = getNativeNode(nodes, "root:3");
        assertNotNull(nodeFailing.getState().getError());
        var nodeAfterFailure = getNativeNode(nodes, "root:39");
        assertNull(nodeAfterFailure.getState());
        var failingNodeAfterFailure = getNativeNode(nodes, "root:40");
        assertNull(failingNodeAfterFailure.getState());
    }

    public void testTryCatchFailureInCatch() throws Exception {
        var nodes = executeWorkflowAndGetNodes(TestWorkflowCollection.TRY_CATCH);
        var tryCatchEnd = getNativeNode(nodes, "root:49");
        assertThat(tryCatchEnd.getState().getExecutionState(), is(NodeStateEnt.ExecutionStateEnum.CONFIGURED));
        var failingInTry = getNativeNode(nodes, "root:47");
        assertNotNull(failingInTry.getState().getError());
        var failingInCatch = getNativeNode(nodes, "root:46");
        assertThat(failingInCatch.getState().getExecutionState(), is(NodeStateEnt.ExecutionStateEnum.CONFIGURED));
    }

    public void testFailureInInactiveBranch() throws Exception {
        var nodes = executeWorkflowAndGetNodes(TestWorkflowCollection.TRY_CATCH);
        var failingOnInactiveElseBranch = getNativeNode(nodes, "root:9");
        assertNull(failingOnInactiveElseBranch.getState());
    }

    public void testFailureInActiveBranch() throws Exception {
        var nodes = executeWorkflowAndGetNodes(TestWorkflowCollection.TRY_CATCH);
        var failingNodeOnActiveElseBranch = getNativeNode(nodes, "root:38");
        assertThat(failingNodeOnActiveElseBranch.getState().getExecutionState(), is(NodeStateEnt.ExecutionStateEnum.CONFIGURED));
        assertNotNull(failingNodeOnActiveElseBranch.getState().getError());
    }

    /**
     * Test reported state of a failing node in a try catch scope, which itself is fully contained in an inactive
     * branch.
     */
    public void testFailureInTryCatchOnInactiveBranch() throws Exception {
        var nodes = executeWorkflowAndGetNodes(TestWorkflowCollection.TRY_CATCH);
        var failingInTryCatchOnInactive = getNativeNode(nodes, "root:14");
        assertNull(failingInTryCatchOnInactive.getState());
        var failingOnInActive = getNativeNode(nodes, "root:17");
        assertNull(failingOnInActive.getState());
    }

    public void testFailureInNestedTryCatch() throws Exception {
        var nodes = executeWorkflowAndGetNodes(TestWorkflowCollection.TRY_CATCH);
        var failingNode = getNativeNode(nodes, "root:22");
        assertNotNull(failingNode.getState().getError());
        var innerTryCatchStart = getNativeNode(nodes, "root:23");
        assertThat(innerTryCatchStart.getState().getExecutionState(), is(NodeStateEnt.ExecutionStateEnum.EXECUTED));
        var innerTryCatchEnd = getNativeNode(nodes, "root:24");
        assertThat(innerTryCatchEnd.getState().getExecutionState(), is(NodeStateEnt.ExecutionStateEnum.EXECUTED));
    }

    private static NativeNodeEnt getNativeNode(final Map<String, NodeEnt> nodes, final String id) {
        return (NativeNodeEnt)nodes.get(id);
    }

    private Map<String, NodeEnt> executeWorkflowAndGetNodes(final TestWorkflowCollection workflow) throws Exception {
        var projectId = loadWorkflow(workflow);
        executeWorkflow(projectId);
        var project = ws().getWorkflow(projectId, NodeIDEnt.getRootID(), false).getWorkflow();
        return project.getNodes();
    }

}
