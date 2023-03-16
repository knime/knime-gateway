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
 *   Sep 15, 2020 (hornm): created
 */
package org.knime.gateway.testing.helper;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.awaitility.Awaitility.await;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.extension.InvalidNodeFactoryExtensionException;
import org.knime.core.node.extension.NodeFactoryExtensionManager;
import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.NodeIDEnt;

/**
 * A utility class that provides a list of {@link WorkflowTransformation}s carried out on the
 * {@link TestWorkflowCollection#GENERAL_WEB_UI} workflow.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class WorkflowTransformations {

    private static List<WorkflowTransformations> workflowTransformationList;

    /**
     * @return list of all workflow transformations (multiple transformations per test-workflow)
     */
    public static List<WorkflowTransformations> getAllWorkflowTransformations() {
        if (workflowTransformationList == null) {
            workflowTransformationList = new ArrayList<>();
            for (TestWorkflowCollection testWorkflow : TestWorkflowCollection.values()) {
                List<WorkflowTransformation> trans = createWorkflowTransformations(testWorkflow);
                if (!trans.isEmpty()) {
                    workflowTransformationList
                        .add(new WorkflowTransformations(testWorkflow, getWorkflowIdFor(testWorkflow), trans));
                }
            }
            workflowTransformationList = Collections.unmodifiableList(workflowTransformationList);
        }
        return workflowTransformationList;
    }

    /**
     * A list of many possible transformations that can be applied to a {@link WorkflowManager} (e.g. execution, node
     * removal, port replacement, ...).
     *
     * @param testWorkflow the test workflow to create the workflow transformations for
     *
     * @return list of {@link WorkflowTransformation}s
     */
    public static List<WorkflowTransformation>
        createWorkflowTransformations(final TestWorkflowCollection testWorkflow) {
        switch (testWorkflow) {
            case GENERAL_WEB_UI:
                return createTransformationsForGeneral();
            case STREAMING_EXECUTION:
                return createTransformationsForStreamingExecution();
            default:
                //
        }
        return Collections.emptyList();
    }

    private static NodeIDEnt getWorkflowIdFor(final TestWorkflowCollection testWorkflow) {
        if (testWorkflow == TestWorkflowCollection.STREAMING_EXECUTION) {
            return new NodeIDEnt(3);
        } else {
            return NodeIDEnt.getRootID();
        }
    }

    private static List<WorkflowTransformation> createTransformationsForGeneral() {
        return asList(newTransformation(w -> w.executeUpToHere(w.getID().createChild(1)), "node_executed"), //
            newTransformation(w -> w.resetAndConfigureNode(w.getID().createChild(1)), "node_reset"), //
            newTransformation(w -> w.removeConnection(w.getIncomingConnectionFor(w.getID().createChild(26), 1)),
                "connection_removed"), //
            newTransformation(w -> w.removeNode(w.getID().createChild(18)), "node_removed"), newTransformation(w -> {
                try {
                    NodeID id = w.createAndAddNode(NodeFactoryExtensionManager.getInstance()
                        .createNodeFactory("org.knime.base.node.preproc.append.row.AppendedRowsNodeFactory").get());
                    w.getNodeContainer(id)
                        .setUIInformation(NodeUIInformation.builder().setNodeLocation(40, 50, 10, 10).build());
                } catch (InstantiationException | IllegalAccessException | InvalidNodeFactoryExtensionException ex) {
                    throw new IllegalStateException(ex);
                }
            }, "node_added"), //
            newTransformation(w -> w.addConnection(w.getID().createChild(25), 3, w.getID().createChild(26), 1),
                "connection_added"), //
            newTransformation(w -> w.addWorkflowAnnotation(new AnnotationData(), -1), "workflow_annotation_added"), //
            newTransformation(w -> w.removeAnnotation(w.getWorkflowAnnotations().iterator().next().getID()),
                "workflow_annotation_removed"), //
            newTransformation(w -> {
                var newAnno = new AnnotationData();
                w.getWorkflowAnnotations().iterator().next().copyFrom(newAnno, false);
            }, "workflow_annotation_changed"), //
            newTransformation(w -> {
                w.getWorkflowAnnotations().iterator().next().setDimension(10, 11, 12, 13);
            }, "workflow_annotation_moved_and_resized"), //
            newTransformation(w -> {
                var newAnno = new AnnotationData();
                newAnno.setText("new anno text");
                w.getNodeContainer(w.getID().createChild(1)).getNodeAnnotation().copyFrom(newAnno, false);
            }, "node_annotation_added"), //
            newTransformation(w -> {
                var newAnno = new AnnotationData();
                newAnno.setText("yet another text");
                w.getNodeContainer(w.getID().createChild(1)).getNodeAnnotation().copyFrom(newAnno, false);
            }, "node_annotation_changed"), //
            newTransformation(w -> {
                NativeNodeContainer oldNC = (NativeNodeContainer)w.getNodeContainer(w.getID().createChild(186));
                ModifiableNodeCreationConfiguration creationConfig = oldNC.getNode().getCopyOfCreationConfig().get();
                creationConfig.getPortConfig().get().getExtendablePorts().get("input").addPort(BufferedDataTable.TYPE);
                creationConfig.getPortConfig().get().getExtendablePorts().get("input").addPort(BufferedDataTable.TYPE);
                w.replaceNode(oldNC.getID(), creationConfig);
            }, "ports_added"), //
            newTransformation(w -> ((WorkflowManager)w.getNodeContainer(w.getID().createChild(6))).setName("New Name"),
                "metanode_renamed"), //
            newTransformation(
                w -> ((SubNodeContainer)w.getNodeContainer(w.getID().createChild(23))).setName("New Name"),
                "component_renamed"));
    }

    private static List<WorkflowTransformation> createTransformationsForStreamingExecution() {
        return singletonList(newTransformation(WorkflowManager::executeAll, "streaming_execution",
            wfm -> await().atMost(5, TimeUnit.SECONDS).pollInterval(300, TimeUnit.MILLISECONDS).until(() -> {
                var ncs1 = wfm.getNodeContainer(wfm.getID().createChild(3)).getNodeContainerState();
                var ncs2 = wfm.getNodeContainer(wfm.getID().createChild(5)).getNodeContainerState();
                return ncs1.isExecuted() && ncs2.isExecutionInProgress() && !ncs2.isWaitingToBeExecuted();
            })));
    }

    private static WorkflowTransformation newTransformation(final Consumer<WorkflowManager> workflowTransformation,
        final String name) {
        return newTransformation(workflowTransformation, name, wfm -> {
            try {
                boolean done = wfm.waitWhileInExecution(10, TimeUnit.SECONDS);
                if (!done) {
                    throw new IllegalStateException("Workflow still executing");
                }
            } catch (InterruptedException ex) { // NOSONAR should never been interrupted
                throw new IllegalStateException(ex);
            }
        });
    }

    private static WorkflowTransformation newTransformation(final Consumer<WorkflowManager> workflowTransformation,
        final String name, final Consumer<WorkflowManager> waitForStableState) {
        return new WorkflowTransformation() {

            @Override
            public void apply(final WorkflowManager wfm) {
                workflowTransformation.accept(wfm);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public void waitForStableState(final WorkflowManager wfm) throws InterruptedException {
                waitForStableState.accept(wfm);
            }

        };
    }

    /**
     * Represents a workflow transformation for a {@link WorkflowManager}-instance.
     */
    public interface WorkflowTransformation {

        /**
         * @return the name for this particular workflow transformation
         */
        String getName();

        /**
         * Applies the workflow transformation to the provided workflow manager.
         *
         * @param wfm the workflow manager to apply the transformation to
         */
        void apply(WorkflowManager wfm);

        /**
         * Called right after the workflow transformation has been applied in order to wait till the workflow is in a
         * stable state (i.e. no more changes are expected).
         *
         * @param wfm
         * @throws InterruptedException
         */
        void waitForStableState(final WorkflowManager wfm) throws InterruptedException;

    }

    private final TestWorkflowCollection m_testWorkflowProject;

    private final NodeIDEnt m_testWorkflowId;

    private final List<WorkflowTransformation> m_transformations;

    private WorkflowTransformations(final TestWorkflowCollection testWorkflowProject, final NodeIDEnt testWorkflowId,
        final List<WorkflowTransformation> transformations) {
        m_testWorkflowProject = testWorkflowProject;
        m_testWorkflowId = testWorkflowId;
        m_transformations = transformations;
    }

    /**
     * @return the testworkflow project the workflow transformations are applied on
     */
    public TestWorkflowCollection getTestWorkflowProject() {
        return m_testWorkflowProject;
    }

    /**
     * @return the workflow id, if it's a sub-workflow (component or metanode) of the testworkflow project or
     *         {@link NodeIDEnt#getRootID()} if it's the workflow project itself
     */
    public NodeIDEnt getWorkflowId() {
        return m_testWorkflowId;
    }

    /**
     * @return list of all transformations to be applied to the workflow
     */
    public List<WorkflowTransformation> getTransformations() {
        return m_transformations;
    }

    @Override
    public String toString() {
        return "Transformations for " + m_testWorkflowProject.getName();
    }

}
