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
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.node.workflow.NodeAnnotationData;
import org.knime.core.node.workflow.capture.WorkflowPortObject;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.ConnectionIDEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt;
import org.knime.gateway.api.webui.entity.AddNodeCommandEnt.AddNodeCommandEntBuilder;
import org.knime.gateway.api.webui.entity.AddNodeResultEnt;
import org.knime.gateway.api.webui.entity.AddPortCommandEnt;
import org.knime.gateway.api.webui.entity.AddPortResultEnt;
import org.knime.gateway.api.webui.entity.AllowedNodeActionsEnt;
import org.knime.gateway.api.webui.entity.AnnotationEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt;
import org.knime.gateway.api.webui.entity.CollapseCommandEnt.ContainerTypeEnum;
import org.knime.gateway.api.webui.entity.CollapseResultEnt;
import org.knime.gateway.api.webui.entity.CommandResultEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt;
import org.knime.gateway.api.webui.entity.ConnectCommandEnt.ConnectCommandEntBuilder;
import org.knime.gateway.api.webui.entity.ConnectionEnt;
import org.knime.gateway.api.webui.entity.ConvertContainerResultEnt;
import org.knime.gateway.api.webui.entity.CopyCommandEnt;
import org.knime.gateway.api.webui.entity.CopyCommandEnt.CopyCommandEntBuilder;
import org.knime.gateway.api.webui.entity.CopyResultEnt;
import org.knime.gateway.api.webui.entity.CutCommandEnt;
import org.knime.gateway.api.webui.entity.CutCommandEnt.CutCommandEntBuilder;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt;
import org.knime.gateway.api.webui.entity.DeleteCommandEnt.DeleteCommandEntBuilder;
import org.knime.gateway.api.webui.entity.ExpandCommandEnt;
import org.knime.gateway.api.webui.entity.ExpandResultEnt;
import org.knime.gateway.api.webui.entity.MetaNodeEnt;
import org.knime.gateway.api.webui.entity.NativeNodeEnt;
import org.knime.gateway.api.webui.entity.NodeAnnotationEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt.NodeFactoryKeyEntBuilder;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodePortTemplateEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt.ExecutionStateEnum;
import org.knime.gateway.api.webui.entity.PasteCommandEnt;
import org.knime.gateway.api.webui.entity.PasteCommandEnt.PasteCommandEntBuilder;
import org.knime.gateway.api.webui.entity.PasteResultEnt;
import org.knime.gateway.api.webui.entity.PortCommandEnt;
import org.knime.gateway.api.webui.entity.RemovePortCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt;
import org.knime.gateway.api.webui.entity.TranslateCommandEnt.TranslateCommandEntBuilder;
import org.knime.gateway.api.webui.entity.UpdateComponentOrMetanodeNameCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentOrMetanodeNameCommandEnt.UpdateComponentOrMetanodeNameCommandEntBuilder;
import org.knime.gateway.api.webui.entity.UpdateNodeLabelCommandEnt;
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
import org.knime.gateway.api.webui.util.WorkflowEntityBuilder;
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
 * Test for the endpoints to view/render a workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH
 */
@SuppressWarnings("java:S112") // generic exceptions
public class WorkflowServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    protected WorkflowServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(WorkflowServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests to get the workflow.
     *
     * @throws Exception
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
     *
     * @throws Exception
     */
    public void testGetComponentProjectWorkflow() throws Exception {
        String wfId = loadComponent(TestWorkflowCollection.COMPONENT_PROJECT);

        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), Boolean.TRUE).getWorkflow();
        cr(workflow, "component_project");

        workflow = ws().getWorkflow(wfId, new NodeIDEnt(5), Boolean.TRUE).getWorkflow();
        cr(workflow, "component_in_component_project_l1");

        workflow = ws().getWorkflow(wfId, new NodeIDEnt(5,0,7), Boolean.TRUE).getWorkflow();
        cr(workflow, "component_in_component_project_l2");
    }

    /**
     * Tests the correct mapping of the node execution states.
     *
     * @throws Exception
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
            if (n instanceof NativeNodeEnt) {
                state = ((NativeNodeEnt)n).getState().getExecutionState();
            } else if (n instanceof ComponentNodeEnt) {
                state = ((ComponentNodeEnt)n).getState().getExecutionState();
            } else {
                //
            }
            return Pair.create(e.getKey(), state);
        }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    /**
     * Tests the {@link WorkflowEnt#getAllowedActions()} property in partcular.
     *
     * @throws Exception
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
     *
     * @throws Exception
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

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link TranslateCommandEnt}.
     *
     * @throws Exception
     */
    public void testExecuteTranslateCommand() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        var node15 = new NodeIDEnt(15);
        var node16 = new NodeIDEnt(16);
        var node18 = new NodeIDEnt(18);
        WorkflowEnt workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();
        Map<String, NodeEnt> nodes = workflow.getNodes();
        XYEnt orgPosNode15 = nodes.get(node15.toString()).getPosition();
        XYEnt orgPosNode16 = nodes.get(node16.toString()).getPosition();
        XYEnt orgPosNode18 = nodes.get(node18.toString()).getPosition();

        assertThat(workflow.getAllowedActions().isCanUndo(), is(false));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // node and annotation translation
        var anno3 = new AnnotationIDEnt("root_3");
        TranslateCommandEnt command = builder(TranslateCommandEntBuilder.class).setKind(KindEnum.TRANSLATE)
            .setNodeIds(asList(node15, node16, node18)).setAnnotationIds(singletonList(anno3))
            .setTranslation(builder(XYEntBuilder.class).setX(-224).setY(-763).build()).build();
        ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();
        nodes = workflow.getNodes();
        XYEnt pos = nodes.get(node15.toString()).getPosition();
        assertThat(pos.getX(), is(0));
        assertThat(pos.getY(), is(0));
        pos = nodes.get(node16.toString()).getPosition();
        assertThat(pos.getX(), is(120));
        assertThat(pos.getY(), is(0));
        pos = nodes.get(node18.toString()).getPosition();
        assertThat(pos.getX(), is(240));
        assertThat(pos.getY(), is(0));
        WorkflowAnnotationEnt wa =
            workflow.getWorkflowAnnotations().stream().filter(a -> a.getId().equals(anno3)).findFirst().orElse(null);
        assertThat(wa.getBounds().getX(), is(116)); // NOSONAR wa guaranteed to be non-null
        assertThat(wa.getBounds().getY(), is(123));
        assertThat(workflow.getAllowedActions().isCanUndo(), is(true));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // undo
        ws().undoWorkflowCommand(wfId, NodeIDEnt.getRootID());
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();
        nodes = workflow.getNodes();
        pos = nodes.get(node15.toString()).getPosition();
        assertThat(pos.getX(), is(orgPosNode15.getX()));
        assertThat(pos.getY(), is(orgPosNode15.getY()));
        pos = nodes.get(node16.toString()).getPosition();
        assertThat(pos.getX(), is(orgPosNode16.getX()));
        assertThat(pos.getY(), is(orgPosNode16.getY()));
        pos = nodes.get(node18.toString()).getPosition();
        assertThat(pos.getX(), is(orgPosNode18.getX()));
        assertThat(pos.getY(), is(orgPosNode18.getY()));
        assertThat(workflow.getAllowedActions().isCanUndo(), is(false));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(true));

        // redo
        ws().redoWorkflowCommand(wfId, NodeIDEnt.getRootID());
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), true).getWorkflow();
        nodes = workflow.getNodes();
        pos = nodes.get(node15.toString()).getPosition();
        assertThat(pos.getX(), is(0));
        assertThat(pos.getY(), is(0));
        pos = nodes.get(node16.toString()).getPosition();
        assertThat(pos.getX(), is(120));
        assertThat(pos.getY(), is(0));
        pos = nodes.get(node18.toString()).getPosition();
        assertThat(pos.getX(), is(240));
        assertThat(pos.getY(), is(0));
        assertThat(workflow.getAllowedActions().isCanUndo(), is(true));
        assertThat(workflow.getAllowedActions().isCanRedo(), is(false));

        // annotation translation alone
        AnnotationIDEnt anno1 = new AnnotationIDEnt("root_1");
        TranslateCommandEnt command2 =
            builder(TranslateCommandEntBuilder.class).setKind(KindEnum.TRANSLATE).setAnnotationIds(singletonList(anno1))
                .setTranslation(builder(XYEntBuilder.class).setX(-880).setY(-26).build()).build();
        ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command2);
        workflow = ws().getWorkflow(wfId, NodeIDEnt.getRootID(), false).getWorkflow();
        wa = workflow.getWorkflowAnnotations().stream().filter(a -> a.getId().equals(anno1)).findFirst().orElse(null);
        assertThat(wa.getBounds().getX(), is(0)); // NOSONAR wa guaranteed to be non-null
        assertThat(wa.getBounds().getY(), is(0));

        // move a node within a component
        TranslateCommandEnt command3 = builder(TranslateCommandEntBuilder.class).setKind(KindEnum.TRANSLATE)
            .setNodeIds(asList(new NodeIDEnt(12, 0, 10))).setAnnotationIds(emptyList())
            .setTranslation(builder(XYEntBuilder.class).setX(10).setY(10).build()).build();
        NodeEnt nodeBefore =
            ws().getWorkflow(wfId, new NodeIDEnt(12), true).getWorkflow().getNodes().get("root:12:0:10");
        ws().executeWorkflowCommand(wfId, new NodeIDEnt(12), command3);
        NodeEnt nodeAfter =
            ws().getWorkflow(wfId, new NodeIDEnt(12), true).getWorkflow().getNodes().get("root:12:0:10");
        assertThat(nodeAfter.getPosition().getX(), is(nodeBefore.getPosition().getX() + 10));
        assertThat(nodeAfter.getPosition().getY(), is(nodeBefore.getPosition().getY() + 10));

        // move a node within a metanode
        TranslateCommandEnt command4 = builder(TranslateCommandEntBuilder.class).setKind(KindEnum.TRANSLATE)
            .setNodeIds(asList(new NodeIDEnt(6, 3))).setAnnotationIds(emptyList())
            .setTranslation(builder(XYEntBuilder.class).setX(10).setY(10).build()).build();
        nodeBefore = ws().getWorkflow(wfId, new NodeIDEnt(6), true).getWorkflow().getNodes().get("root:6:3");
        ws().executeWorkflowCommand(wfId, new NodeIDEnt(6), command4);
        nodeAfter = ws().getWorkflow(wfId, new NodeIDEnt(6), true).getWorkflow().getNodes().get("root:6:3");
        assertThat(nodeAfter.getPosition().getX(), is(nodeBefore.getPosition().getX() + 10));
        assertThat(nodeAfter.getPosition().getY(), is(nodeBefore.getPosition().getY() + 10));

        // exceptions
        TranslateCommandEnt command5 = builder(TranslateCommandEntBuilder.class).setKind(KindEnum.TRANSLATE)
            .setNodeIds(singletonList(new NodeIDEnt(9999)))
            .setAnnotationIds(singletonList(new AnnotationIDEnt("root_12345")))
            .setTranslation(builder(XYEntBuilder.class).setX(0).setY(0).build()).build();
        assertThrows(NodeNotFoundException.class,
            () -> ws().executeWorkflowCommand(wfId, new NodeIDEnt(999999), command5));
        try {
            ws().executeWorkflowCommand(wfId, NodeIDEnt.getRootID(), command5);
        } catch (Exception e) { // NOSONAR
            assertThat("unexpected exception class", e, Matchers.instanceOf(OperationNotAllowedException.class));
            assertThat("unexpected exception message", e.getMessage(),
                is("Failed to execute command. Workflow parts not found: "
                    + "nodes (0:9999), workflow-annotations (0:12345)"));
        }

    }

    @SuppressWarnings("javadoc")
    public void testCollapseConfiguredToMetanode() throws Exception {
        testCollapseConfigured(CollapseCommandEnt.ContainerTypeEnum.METANODE);
    }

    @SuppressWarnings("javadoc")
    public void testCollapseConfiguredToComponent() throws Exception {
        testCollapseConfigured(CollapseCommandEnt.ContainerTypeEnum.COMPONENT);
    }

    @SuppressWarnings("javadoc")
    public void testCollapseExecutingToMetanode() throws Exception {
        testCollapseExecuting(CollapseCommandEnt.ContainerTypeEnum.METANODE);
    }

    @SuppressWarnings("javadoc")
    public void testCollapseExecutingToComponent() throws Exception {
        testCollapseExecuting(CollapseCommandEnt.ContainerTypeEnum.COMPONENT);
    }

    @SuppressWarnings("javadoc")
    public void testCollapseResettableToMetanode() throws Exception {
        testCollapseResettable(CollapseCommandEnt.ContainerTypeEnum.METANODE);
    }

    @SuppressWarnings("javadoc")
    public void testCollapseResettableToComponent() throws Exception {
        testCollapseResettable(CollapseCommandEnt.ContainerTypeEnum.COMPONENT);
    }

    @SuppressWarnings("javadoc")
    public void testCollapseResultMetanode() throws Exception {
        testCollapseResult(CollapseCommandEnt.ContainerTypeEnum.METANODE);
    }

    @SuppressWarnings("javadoc")
    public void testCollapseResultComponent() throws Exception {
        testCollapseResult(CollapseCommandEnt.ContainerTypeEnum.COMPONENT);
    }

    /**
     * Tests that the case of collapsing 'nothing' (i.e. no nodes nor workflow annotations given) into a metanode is
     * handled properly.
     *
     * @throws Exception
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

    private void testExpandExecuting(final int container, final int successor) throws Exception{
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
        ExpandResultEnt commandResponseEnt =
            (ExpandResultEnt)ws().executeWorkflowCommand(projectId, wfId, commandEnt);
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

        var commandEnt =
            buildCollapseCommandEnt(nodesToCollapseEnts, Collections.emptyList(), containerType);
        var exceptionMessage = assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), commandEnt)).getMessage();
        assertThat(exceptionMessage, containsString("Cannot move executed nodes"));
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
     * @throws Exception
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
     * @throws Exception
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
     *
     * @param containerType
     * @throws Exception
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
        var annotsToCollapseEnts = annotsToCollapseInts.stream().map(i -> new AnnotationIDEnt(getRootID(), i)).collect(Collectors.toList());

        WorkflowEnt unchangedWfEnt = ws().getWorkflow(wfId, getRootID(), true).getWorkflow();
        Set<String> annotationContents = unchangedWfEnt.getWorkflowAnnotations().stream().map(AnnotationEnt::getText).collect(
                Collectors.toSet());

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
        if (commandResultEnt instanceof CollapseResultEnt) {
            var collapseResponseEnt = (CollapseResultEnt)commandResultEnt;
            return collapseResponseEnt.getNewNodeId();
        } else if (commandResultEnt instanceof ConvertContainerResultEnt) {
            var convertResponseEnt = (ConvertContainerResultEnt)commandResultEnt;
            return convertResponseEnt.getConvertedNodeId();
        } else {
            throw new NoSuchElementException("Unexpected response entity");
        }
    }

    private void assertCollapsed(final String wfId, final CollapseCommandEnt commandEnt,
            final CommandResultEnt commandResultEnt, final Set<String> annotationContents) throws Exception {
        var newNode = getNewNodeId(commandResultEnt);
        var nodesToCollapseEnts = commandEnt.getNodeIds();
        var nodesToCollapseInts = nodesToCollapseEnts.stream()
                .map(NodeIDEnt::getNodeIDs)
                .map(idArr -> idArr[idArr.length-1])
                .collect(Collectors.toList());
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

    private static NodeIDEnt getParentIdEnt(final CollapseCommandEnt.ContainerTypeEnum containerType, final NodeIDEnt parentEnt) {
        if (containerType == CollapseCommandEnt.ContainerTypeEnum.COMPONENT) {
            return parentEnt.appendNodeID(0);
        } else {
            return parentEnt;
        }
    }


    private void testCollapseResettable(final CollapseCommandEnt.ContainerTypeEnum containerType) throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var nodesToCollapseInts = List.of(7,6);
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
        assertEquals(message, annotationContents,
            wfEnt.getWorkflowAnnotations().stream().map(AnnotationEnt::getText).collect(Collectors.toSet()));
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
     * when called with {@link DeleteCommandEnt}.
     *
     * @throws Exception
     */
    public void testExecuteDeleteCommand() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);

        // successful deletion
        var command = createDeleteCommandEnt(asList(new NodeIDEnt(1), new NodeIDEnt(4)),
            asList(new ConnectionIDEnt(new NodeIDEnt(26), 1)), asList(new AnnotationIDEnt(getRootID(), 1)));
        ws().executeWorkflowCommand(wfId, getRootID(), command);
        WorkflowEnt workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        cr(workflow, "delete_command");
        assertThat(workflow.getNodes().keySet(), not(hasItems("root:1", "root:4")));
        assertThat(
            workflow.getWorkflowAnnotations().stream().map(a -> a.getId().toString()).collect(Collectors.toList()),
            not(hasItems("root_1")));
        assertThat(workflow.getWorkflowAnnotations().size(), is(6));
        assertThat(workflow.getConnections().keySet(), not(hasItems("root:26_1")));

        // undo deletion
        ws().undoWorkflowCommand(wfId, getRootID());
        workflow = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow();
        assertThat(workflow.getNodes().keySet(), hasItems("root:1", "root:4"));
        assertThat(
            workflow.getWorkflowAnnotations().stream().map(a -> a.getId().toString()).collect(Collectors.toList()),
            hasItems("root_1"));
        assertThat(workflow.getWorkflowAnnotations().size(), is(7));
        assertThat(workflow.getConnections().keySet(), hasItems("root:26_1"));

        // delete a node within a component
        assertThat("node expected to be present",
            ws().getWorkflow(wfId, new NodeIDEnt(12), true).getWorkflow().getNodes().get("root:12:0:10"),
            is(notNullValue()));
        ws().executeWorkflowCommand(wfId, new NodeIDEnt(12),
            createDeleteCommandEnt(asList(new NodeIDEnt(12, 0, 10)), emptyList(), emptyList()));
        assertThat("node expected to be deleted",
            ws().getWorkflow(wfId, new NodeIDEnt(12), true).getWorkflow().getNodes().get("root:12:0:10"),
            is(nullValue()));

        // deletion a connection between a node and a port leaving a metanode
        assertThat(ws().getWorkflow(wfId, new NodeIDEnt(6), false).getWorkflow().getConnections().get("root:6_1"),
            is(notNullValue()));
        ws().executeWorkflowCommand(wfId, new NodeIDEnt(6),
            createDeleteCommandEnt(emptyList(), asList(new ConnectionIDEnt("root:6_1")), emptyList()));
        assertThat(ws().getWorkflow(wfId, new NodeIDEnt(6), false).getWorkflow().getConnections().get("root:6_1"),
            is(nullValue()));

        // delete a node within a metanode
        assertThat("node expected to be present",
            ws().getWorkflow(wfId, new NodeIDEnt(6), true).getWorkflow().getNodes().get("root:6:3"),
            is(notNullValue()));
        ws().executeWorkflowCommand(wfId, new NodeIDEnt(6),
            createDeleteCommandEnt(asList(new NodeIDEnt(6, 3)), emptyList(), emptyList()));
        assertThat("node expected to be deleted",
            ws().getWorkflow(wfId, new NodeIDEnt(6), true).getWorkflow().getNodes().get("root:6:3"), is(nullValue()));

        // deletion fails because a node doesn't exist
        var command2 = createDeleteCommandEnt(asList(new NodeIDEnt(99999999)), emptyList(), emptyList());
        Exception ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command2));
        assertThat(ex.getMessage(), is("Some nodes can't be deleted or don't exist. Delete operation aborted."));

        // deletion fails because a connection doesn't exist
        var command3 =
            createDeleteCommandEnt(emptyList(), asList(new ConnectionIDEnt(new NodeIDEnt(99999999), 0)), emptyList());
        ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command3));
        assertThat(ex.getMessage(), is("Some connections don't exist. Delete operation aborted."));

        // deletion fails because a workflow annotation doesn't exist
        var command4 =
            createDeleteCommandEnt(emptyList(), emptyList(), asList(new AnnotationIDEnt(getRootID(), 999999999)));
        ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command4));
        assertThat(ex.getMessage(), is("Some workflow annotations don't exist. Delete operation aborted."));

        // deletion fails because a node cannot be deleted due to a delete lock
        var command5 = createDeleteCommandEnt(asList(new NodeIDEnt(23, 0, 8)), emptyList(), emptyList());
        ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, new NodeIDEnt(23), command5));
        assertThat(ex.getMessage(), is("Some nodes can't be deleted or don't exist. Delete operation aborted."));

        /* checks for a workflow in execution */
        String wfId2 = loadWorkflow(TestWorkflowCollection.EXECUTION_STATES);
        executeWorkflowAsync(wfId2);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            WorkflowEnt w = ws().getWorkflow(wfId2, NodeIDEnt.getRootID(), Boolean.TRUE).getWorkflow();
            assertThat(((NativeNodeEnt)w.getNodes().get("root:4")).getState().getExecutionState(),
                is(ExecutionStateEnum.EXECUTED));
        });
        cr(ws().getWorkflow(wfId2, getRootID(), Boolean.TRUE).getWorkflow(), "can_delete_executing");

        // deletion fails because of a node that cannot be deleted due to executing successors
        var command6 = createDeleteCommandEnt(asList(new NodeIDEnt(3)), emptyList(), emptyList());
        ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId2, getRootID(), command6));
        assertThat(ex.getMessage(), is("Some nodes can't be deleted or don't exist. Delete operation aborted."));

        // deletion of a connection fails because because it's connected to an executing node
        var command7 =
            createDeleteCommandEnt(emptyList(), asList(new ConnectionIDEnt(new NodeIDEnt(7), 0)), emptyList());
        ex = Assert.assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId2, getRootID(), command7));
        assertThat(ex.getMessage(), is("Some connections can't be deleted. Delete operation aborted."));

    }

    static DeleteCommandEnt createDeleteCommandEnt(final List<NodeIDEnt> nodeIds,
        final List<ConnectionIDEnt> connectionIds, final List<AnnotationIDEnt> annotationIds) {
        return builder(DeleteCommandEntBuilder.class).setKind(KindEnum.DELETE)//
            .setNodeIds(nodeIds)//
            .setConnectionIds(connectionIds)//
            .setAnnotationIds(annotationIds)//
            .build();
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link ConnectCommandEnt}.
     *
     * @throws Exception
     */
    public void testExecuteConnectCommand() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        Map<String, ConnectionEnt> connections =
            ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        var originalNumConnections = connections.size();
        assertThat(connections.get("root:10_1").getSourceNode().toString(), is("root:1"));

        // replace existing connection
        var command = buildConnectCommandEnt(new NodeIDEnt(27), 1, new NodeIDEnt(10), 1);
        ws().executeWorkflowCommand(wfId, getRootID(), command);
        connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections));
        assertThat(connections.get("root:10_1").getSourceNode().toString(), is("root:27"));

        // undo
        ws().undoWorkflowCommand(wfId, getRootID());
        connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections));
        assertThat(connections.get("root:10_1").getSourceNode().toString(), is("root:1"));

        // new connection
        command = buildConnectCommandEnt(new NodeIDEnt(27), 1, new NodeIDEnt(21), 2);
        ws().executeWorkflowCommand(wfId, getRootID(), command);
        connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections + 1));
        assertThat(connections.get("root:21_2").getSourceNode().toString(), is("root:27"));

        // undo
        ws().undoWorkflowCommand(wfId, getRootID());
        connections = ws().getWorkflow(wfId, getRootID(), false).getWorkflow().getConnections();
        assertThat(connections.size(), is(originalNumConnections));
        assertNull(connections.get("root:21_2"));

        // add already existing connection (command is not added to the undo stack)
        command = buildConnectCommandEnt(new NodeIDEnt(1), 1, new NodeIDEnt(10), 1);
        ws().executeWorkflowCommand(wfId, getRootID(), command);
        Exception exception =
            assertThrows(OperationNotAllowedException.class, () -> ws().undoWorkflowCommand(wfId, getRootID()));
        assertThat(exception.getMessage(), is("No command to undo"));

        // add a connection to a node that doesn't exist
        var command2 = buildConnectCommandEnt(new NodeIDEnt(1), 1, new NodeIDEnt(9999999), 1);
        exception = assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command2));
        assertThat(exception.getMessage(),
            containsString("9999999\" not contained in workflow, nor it's the workflow itself"));

        // add a connection that can't be added (here: because it creates a cycle)
        var command3 = buildConnectCommandEnt(new NodeIDEnt(27), 0, new NodeIDEnt(1), 0);
        exception = assertThrows(OperationNotAllowedException.class,
            () -> ws().executeWorkflowCommand(wfId, getRootID(), command3));
        assertThat(exception.getMessage(), containsString("Connection can't be added"));

        // add a connection within a sub-workflow (e.g. within a component)
        var component23Id = new NodeIDEnt(23);
        var deleteCommand =
            createDeleteCommandEnt(emptyList(), List.of(new ConnectionIDEnt(new NodeIDEnt(23, 0, 9), 1)), emptyList());
        ws().executeWorkflowCommand(wfId, component23Id, deleteCommand);
        var component23ConnectionRemoved = ws().getWorkflow(wfId, component23Id, false);
        assertThat(component23ConnectionRemoved.getWorkflow().getConnections().size(), is(1));
        var command4 = buildConnectCommandEnt(new NodeIDEnt(23, 0, 10), 1, new NodeIDEnt(23, 0, 9), 1);
        ws().executeWorkflowCommand(wfId, component23Id, command4);
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            var component23ConnectionAdded = ws().getWorkflow(wfId, component23Id, false);
            assertThat(component23ConnectionAdded.getWorkflow().getConnections().size(), is(2));
        });
    }

    private static ConnectCommandEnt buildConnectCommandEnt(final NodeIDEnt source, final Integer sourcePort, final NodeIDEnt dest,
        final Integer destPort) {
        return builder(ConnectCommandEntBuilder.class).setKind(KindEnum.CONNECT).setSourceNodeId(source)
            .setSourcePortIdx(sourcePort).setDestinationNodeId(dest).setDestinationPortIdx(destPort).build();
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link AddNodeCommandEnt}.
     *
     * @throws Exception
     */
    public void testExecuteAddNodeCommand() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var rowFilterFactory = "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory";
        var metanode = new NodeIDEnt(1);
        var component = new NodeIDEnt(2);

        // add a node on root-level
        var result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 12, 13, null, null));
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
            buildAddNodeCommand(rowFilterFactory, null, 13, 14, null, null));
        checkForNode(ws().getWorkflow(wfId, metanode, Boolean.FALSE), rowFilterFactory, 13, 14, result);

        // undo
        ws().undoWorkflowCommand(wfId, metanode);
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> assertTrue(
                ws().getWorkflow(wfId, metanode, Boolean.FALSE).getWorkflow().getNodeTemplates().isEmpty()));

        // add node to component
        result = ws().executeWorkflowCommand(wfId, component,
            buildAddNodeCommand(rowFilterFactory, null, 14, 15, null, null));
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
            buildAddNodeCommand(jsNodeFactory, factorySettings, 15, 16, null, null));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), jsNodeFactory  + "#Box+Plot", 15, 16, result);

        // add a node that doesn't exists
        Exception ex = assertThrows(OperationNotAllowedException.class, () -> ws().executeWorkflowCommand(wfId,
            getRootID(), buildAddNodeCommand("non-sense-factory", null, 0, 0, null, null)));
        assertThat(ex.getMessage(), is("No node found for factory key non-sense-factory"));

        // add a dynamic node with non-sense settings
        ex = assertThrows(OperationNotAllowedException.class, () -> ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(jsNodeFactory, "blub", 0, 0, null, null)));
        assertThat(ex.getMessage(), startsWith("Problem reading factory settings while trying to create node from"));
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link AddNodeCommandEnt} that also automatically connects a node.
     *
     * @throws Exception
     */
    public void testExecuteAddAndConnectCommand()throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var rowFilterFactory = "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory";

        // add and connect a node
        var normalizerFactory = "org.knime.base.node.preproc.pmml.normalize.NormalizerPMMLNodeFactory2";
        var sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(normalizerFactory, null, 32, 64, null, null))).getNewNodeId();
        var result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 64, 128, sourceNodeId, 1));
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
            buildAddNodeCommand(rowFilterFactory, null, 64, 128, sourceNodeId, 2)));
        assertThat(ex.getMessage(), is("Destination port index could not be inferred"));

        // redo adding the row filter
        ws().redoWorkflowCommand(wfId, getRootID());
        assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(), is(2));

        // try to connect to a port that is already used
        var rowSplitterFactory = "org.knime.base.node.preproc.filter.row2.RowSplitterNodeFactory";
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowSplitterFactory, null, 128, 256, sourceNodeId, 1));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), rowSplitterFactory, 128, 256, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 1, result, true); // this extra connection is allowed

        // undo adding row splitter
        ws().undoWorkflowCommand(wfId, getRootID());
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link AddNodeCommandEnt} that also auto connects nodes without a given source port.
     *
     * @throws Exception
     */
    public void testExecuteAddAndConnectCommandAutoGuessSourcePorts() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var rowSplitterFactory = "org.knime.base.node.preproc.filter.row2.RowSplitterNodeFactory";
        var columnAppenderFactory = "org.knime.base.node.preproc.columnappend.ColumnAppenderNodeFactory";
        var normalizerFactory = "org.knime.base.node.preproc.pmml.normalize.NormalizerPMMLNodeFactory2";

        // add a node and auto-connect all compatible ports
        var sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowSplitterFactory, null, 32, 64, null, null))).getNewNodeId();
        var result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(columnAppenderFactory, null, 64, 128, sourceNodeId, null));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), columnAppenderFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 1, result, true); // got auto-connected
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 2, result, true); // got auto-connected

        // add a another node and try to auto-connect auto-guessed ports
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(normalizerFactory, null, 128, 256, sourceNodeId, null));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), normalizerFactory, 128, 256, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 1, result, false); // not auto-connected
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 2, result, false); // not auto-connected

    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called with {@link AddNodeCommandEnt} that also automatically connects a dynamic node.
     *
     * @throws Exception
     */
    public void testExecuteAddAndConnectCommandDynamicNode() throws Exception {
        String wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var rowFilterFactory = "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory";
        var caseSwitchStartFactory = "org.knime.base.node.switches.caseswitch.any.CaseStartAnyNodeFactory";
        var tableColToFlowVariableFactory =
            "org.knime.base.node.flowvariable.tablecoltovariable4.TableColumnToVariable4NodeFactory";

        // add and connect a node
        var sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 32, 64, null, null))).getNewNodeId();
        var result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(caseSwitchStartFactory, null, 64, 128, sourceNodeId, 1));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), caseSwitchStartFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 1, result, true);

        // undo adding the case switch
        ws().undoWorkflowCommand(wfId, getRootID());
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(
            () -> assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(),
                is(1)));

        // try the same thing for a flow variable connection
        sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(tableColToFlowVariableFactory, null, 256, 512, null, null))).getNewNodeId();
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(caseSwitchStartFactory, null, 64, 128, sourceNodeId, 1));
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
     *
     * @throws Exception
     */
    public void testExecuteAddAndConnectCommandFlowVariables()throws Exception {
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
            buildAddNodeCommand(tableColToVariableFactory, null, 32, 64, null, null))).getNewNodeId();
        var result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(variableToTableRowFactory, null, 64, 128, sourceNodeId, 1));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), variableToTableRowFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 1, result, true);

        // undo variable to table row
        ws().undoWorkflowCommand(wfId, getRootID());
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(
            () -> assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(),
                is(1)));

        // add and connect two incompatible nodes from source port 0 to destination port 1
        sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 128, 256, null, null))).getNewNodeId();
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(variableToTableRowFactory, null, 64, 128, sourceNodeId, 0));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), variableToTableRowFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 0, result, true);

        // undo variable to table row
        ws().undoWorkflowCommand(wfId, getRootID());
        Awaitility.await().atMost(2, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS).untilAsserted(
            () -> assertThat(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE).getWorkflow().getNodeTemplates().size(),
                is(2)));

        // connect two incompatible nodes via their flow default variable ports
        sourceNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(imageToTableFactory, null, 256, 512, null, null))).getNewNodeId();
        result = ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(tableRowToImageFactory, null, 64, 128, sourceNodeId, 0));
        checkForNode(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), tableRowToImageFactory, 64, 128, result);
        checkForConnection(ws().getWorkflow(wfId, getRootID(), Boolean.FALSE), sourceNodeId, 0, result, true);
    }

    private static AddNodeCommandEnt buildAddNodeCommand(final String factoryClassName, final String factorySettings,
        final int x, final int y, final NodeIDEnt sourceNodeId, final Integer sourcePortIdx) {
        return builder(AddNodeCommandEntBuilder.class).setKind(KindEnum.ADD_NODE)//
            .setNodeFactory(builder(NodeFactoryKeyEntBuilder.class).setClassName(factoryClassName)
                .setSettings(factorySettings).build())//
            .setPosition(builder(XYEntBuilder.class).setX(x).setY(y).build())//
            .setSourceNodeId(sourceNodeId)//
            .setSourcePortIdx(sourcePortIdx)//
            .build();
    }

    private static void checkForNode(final WorkflowSnapshotEnt wf, final String nodeFactory, final int x, final int y, final CommandResultEnt result) {
        assertThat(wf.getWorkflow().getNodeTemplates().keySet(), Matchers.hasItems(nodeFactory));
        var nodeEnt = wf.getWorkflow().getNodes().values().stream()
            .filter(n -> n instanceof NativeNodeEnt && ((NativeNodeEnt)n).getTemplateId().equals(nodeFactory))
            .findFirst().orElseThrow();
        assertThat(nodeEnt.getPosition().getX(), is(x));
        assertThat(nodeEnt.getPosition().getY(), is(y));
        var newNodeId = ((AddNodeResultEnt)result).getNewNodeId();
        assertThat(newNodeId, equalTo(nodeEnt.getId()));
    }

    private static void checkForConnection(final WorkflowSnapshotEnt wf, final NodeIDEnt sourceNodeId,
        final Integer sourcePortIdx, final CommandResultEnt result, final boolean isPresent) {
        var destNodeId = ((AddNodeResultEnt)result).getNewNodeId();
        var connections = wf.getWorkflow().getConnections();
        var numConnections = connections.values().stream()//
            .filter(c -> c.getSourceNode().equals(sourceNodeId))//
            .filter(c -> c.getSourcePort().equals(sourcePortIdx))//
            .filter(c -> c.getDestNode().equals(destNodeId))//
            .count();
        assertThat(numConnections, is(isPresent ? 1L : 0L));
    }

    /**
     * Tests
     * {@link WorkflowService#executeWorkflowCommand(String, NodeIDEnt, org.knime.gateway.api.webui.entity.WorkflowCommandEnt)}
     * when called {@link UpdateComponentOrMetanodeNameCommandEnt}
     *
     * @throws Exception
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
     *
     * @throws Exception
     */
    public void testExecuteUpdateNodeLabelCommand() throws Exception {
        final var wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        final var metanodeId = new NodeIDEnt(1);
        final var componentId = new NodeIDEnt(2);

        // add a native node
        final var rowFilterFactory = "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory";
        final var nativeNodeId = ((AddNodeResultEnt)ws().executeWorkflowCommand(wfId, getRootID(),
            buildAddNodeCommand(rowFilterFactory, null, 32, 64, null, null))).getNewNodeId();

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

    private static UpdateNodeLabelCommandEnt buildUpdateNodeLabelCommandEnt(final NodeIDEnt nodeId, final String label) {
        return builder(UpdateNodeLabelCommandEnt.UpdateNodeLabelCommandEntBuilder.class)//
            .setKind(KindEnum.UPDATE_NODE_LABEL)//
            .setNodeId(nodeId)//
            .setLabel(label)//
            .build();
    }

    /**
     * When a node has its default {@link NodeAnnotationData}, the {@link NodeAnnotationEnt} will be set to
     * {@code null}, see {@link WorkflowEntityBuilder#buildNodeAnnotationEnt}. Setting the node label to something else and
     * undoing this operation will yield to a non-empty {@link NodeAnnotationEnt}, since the {@link NodeAnnotationData}
     * is no longer considered the default. Instead, the node annotation text will be set to the empty string, see
     * {@link WorkflowEntityBuilder#buildNodeAnnotationEnt}
     */
    private static String getLabelFromNodeInWorkflow(final WorkflowEnt wf, final NodeIDEnt nodeId) {
        var annotation = wf.getNodes().get(nodeId.toString()).getAnnotation();
        return annotation == null ? "" : annotation.getText();
    }

    /**
     * Test whether removing ports from native nodes is (not) allowed.
     *
     * @throws Exception
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
     *
     * @throws Exception
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
     *
     * @throws Exception
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
     *
     * @throws Exception
     */
    public void testCanAddPortToNativeIsInteractive() throws Exception {
        var wfId = loadWorkflow(TestWorkflowCollection.HOLLOW);
        var nodeFactory = "org.knime.gateway.testing.helper.webui.node.DummyNodeDynamicPortsInteractiveFactory";
        var compatiblePortTypeId = CoreUtil.getPortTypeId(BufferedDataTable.TYPE);
        var node = new NodeIDEnt(3);

        // Add node and get workflow
        ws().executeWorkflowCommand(wfId, getRootID(), buildAddNodeCommand(nodeFactory, null, 32, 64, null, null));
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
        var supportsType = portGroup.getSupportedPortTypeIds().stream().filter(ent -> ent.equals(targetPortTypeId)).count() > 0;
        return (canAddInputPort || canAddOutputPort) && supportsType;
    }

    /**
     * Execute, undo and redo of adding an input port to a metanode. Add output port.
     *
     * @throws Exception
     */
    public void testAddPortToMetanode() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var someConfiguredMetanode = new NodeIDEnt(14);
        assertAddUndoRedoContainerPorts(wfId, someConfiguredMetanode);
    }

    /**
     * Execute, undo and redo of adding an input port to a component. Add output port.
     *
     * @throws Exception
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

    private void assertPortAdded(final NodeIDEnt node, final boolean isInPort, final String projectId, final NodeIDEnt wfId,
        final WorkflowEnt originalWfEnt, final CommandResultEnt commandResult) throws Exception {
        var originalNumInPorts = getPortList(originalWfEnt, isInPort, node).size();
        var newWorkflowEnt = ws().getWorkflow(projectId, wfId, Boolean.TRUE).getWorkflow();
        var newPortList = getPortList(newWorkflowEnt, isInPort, node);
        var newNumPorts = newPortList.size();
        assertThat("Expect number of ports to have increased by one", newNumPorts == originalNumInPorts + 1);
        assertThat("Command result is not an `AddPortResultEnt`", commandResult, instanceOf(AddPortResultEnt.class));
        assertThat("New port index returned is wrong", ((AddPortResultEnt)commandResult).getNewPortIdx(), is(newNumPorts - 1));
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
     *
     * @throws Exception
     */
    public void testRemovePortFromMetanode() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var metanodeWithPorts = new NodeIDEnt(24);
        assertRemoveUndoRedoContainerPorts(wfId, metanodeWithPorts);
    }

    /**
     * Execute, undo and redo of removing port from container.
     *
     * @throws Exception
     */
    public void testRemovePortFromComponent() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var componentWithPorts = new NodeIDEnt(25);

        var unchangedWfEnt = ws().getWorkflow(wfId, getRootID(), false).getWorkflow();

        var deleteFixedFlowVarPort =
            buildDeletePortCommandEnt(componentWithPorts, PortCommandEnt.SideEnum.INPUT, 0);
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
     *
     * @throws Exception
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
     *
     * @throws Exception
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
     *
     * @throws Exception
     */
    public void testExecuteCommandsWithinMetanode() throws Exception {
        testExecuteCommandsWithinComponentAndMetanode(ContainerTypeEnum.METANODE);
    }

    /**
     * Some commands weren't working from within a component and metanode (NXT-1141). This is the test for it.
     *
     * @throws Exception
     */
    public void testExecuteCommandsWithinComponent() throws Exception {
        testExecuteCommandsWithinComponentAndMetanode(ContainerTypeEnum.COMPONENT);
    }

    private void testExecuteCommandsWithinComponentAndMetanode(final ContainerTypeEnum containerType)
        throws Exception {
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
     * Test Copy command
     *
     * @throws Exception
     */
    public void testExecuteCopyCommand() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var command = buildCopyCommand(asList(new NodeIDEnt(1), new NodeIDEnt(2)), asList(new AnnotationIDEnt("root_0"), new AnnotationIDEnt("root_1")));
        // execute command
        var commandResult = (CopyResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), command);
        assertCopyResultValid(commandResult);
    }

    private static CopyCommandEnt buildCopyCommand(final List<NodeIDEnt> nodeIds,
        final List<AnnotationIDEnt> annotationIds) {
        return builder(CopyCommandEntBuilder.class)//
            .setKind(KindEnum.COPY)//
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
        var command = buildCutCommand(asList(new NodeIDEnt(1), new NodeIDEnt(2)), asList(new AnnotationIDEnt("root_0"), new AnnotationIDEnt("root_1")));
        var nodeKeysBefore = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
        var annKeysBefore = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
        // execute command
        var commandResult = (CopyResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), command);
        var nodeKeysAfterExecution = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
        var annKeysAfterExecution = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
        assertCopyResultValid(commandResult);
        assertThat("We should have less nodes in the workflow after cutting", nodeKeysAfterExecution.size() < nodeKeysBefore.size());
        assertThat("We should have less annotations in the workflow after cutting", annKeysAfterExecution.size() < annKeysBefore.size());
        assertThat("We should not have more nodes in the workflow after cutting", nodeKeysBefore.containsAll(nodeKeysAfterExecution));
        assertThat("We should not have more annotations in the workflow after cutting", annKeysBefore.containsAll(annKeysAfterExecution));
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
        return builder(CutCommandEntBuilder.class)//
            .setKind(KindEnum.CUT)//
            .setNodeIds(nodeIds)//
            .setAnnotationIds(annotationIds)//
            .build();
    }

    private static Set<String> getAnnotationsKeysFromWorkflow(final WorkflowSnapshotEnt workflow) {
        return workflow.getWorkflow().getWorkflowAnnotations().stream().map(a -> a.getId().toString()).collect(Collectors.toSet());
    }

    /**
     * Test Paste command
     *
     * @throws Exception
     */
    public void testExecutePasteCommand() throws Exception {
        final String wfId = loadWorkflow(TestWorkflowCollection.GENERAL_WEB_UI);
        var copyCommand = buildCopyCommand(asList(new NodeIDEnt(1), new NodeIDEnt(2)), asList(new AnnotationIDEnt("root_0"), new AnnotationIDEnt("root_1")));
        var clipboardContent = ((CopyResultEnt)ws().executeWorkflowCommand(wfId, getRootID(), copyCommand)).getContent();
        // test paste commands
        var pasteCommands = List.of(buildPasteCommand(clipboardContent, null), buildPasteCommand(clipboardContent, List.of(16, 32)));
        for (var pasteCommand: pasteCommands) {
            var nodeKeysBefore = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
            var annKeysBefore = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
            // execute command
            var commandResult = ws().executeWorkflowCommand(wfId, getRootID(), pasteCommand);
            assertThat(commandResult.getSnapshotId(), notNullValue());
            assertThat(commandResult.getKind().toString(), is("pasteResult"));
            var pasteResult = (PasteResultEnt)commandResult;
            assertThat(pasteResult.getNodeIds(), hasSize(2));
            assertThat(pasteResult.getAnnotationIds(), hasSize(2));
            var nodeKeysAfterExecution = ws().getWorkflow(wfId, getRootID(), Boolean.TRUE).getWorkflow().getNodes().keySet();
            var annKeysAfterExecution = getAnnotationsKeysFromWorkflow(ws().getWorkflow(wfId, getRootID(), Boolean.TRUE));
            assertThat("We should have more nodes in the workflow after pasting", nodeKeysAfterExecution.size() > nodeKeysBefore.size());
            assertThat("We shouldn't have lost any nodes while pasting", nodeKeysAfterExecution.containsAll(nodeKeysBefore));
            assertThat("We should have more annotations in the workflow after pasting", annKeysAfterExecution.size() > annKeysBefore.size());
            assertThat("We shouldn't have lost any annotations while pasting", annKeysAfterExecution.containsAll(annKeysBefore));
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
    }

    private static PasteCommandEnt buildPasteCommand(final String clipboardContent, final List<Integer> position) {
        return builder(PasteCommandEntBuilder.class)//
            .setKind(KindEnum.PASTE)//
            .setContent(clipboardContent)//
            .setPosition(position != null ? builder(XYEntBuilder.class).setX(position.get(0)).setY(position.get(1)).build() : null)
            .build();
    }

}
