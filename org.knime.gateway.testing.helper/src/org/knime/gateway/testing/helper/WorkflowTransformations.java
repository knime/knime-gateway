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

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.extension.InvalidNodeFactoryExtensionException;
import org.knime.core.node.extension.NodeFactoryExtensionManager;
import org.knime.core.node.workflow.AnnotationData;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.WorkflowAnnotation;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.impl.service.util.WorkflowChangesListener;

/**
 * A utility class that provides a list of {@link WorkflowTransformation}s.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class WorkflowTransformations {

    private WorkflowTransformations() {
        // utility class
    }

    /**
     * A list of many possible transformations that can be applied to a {@link WorkflowManager} (e.g. execution, node
     * removal, port replacement, ...).
     *
     * @return list of {@link WorkflowTransformation}s
     */
    public static List<WorkflowTransformation> createWorkflowTransformations() {
        return Arrays.asList(
            newTransformation(w -> w.executeUpToHere(w.getID().createChild(1)), "node_executed", null),
            newTransformation(w -> w.removeConnection(w.getIncomingConnectionFor(w.getID().createChild(14), 1)),
                "connection_removed"),
            newTransformation(w -> w.removeNode(w.getID().createChild(18)), "node_removed"), newTransformation(w -> {
                try {
                    NodeID id = w.createAndAddNode(NodeFactoryExtensionManager.getInstance()
                        .createNodeFactory("org.knime.base.node.preproc.append.row.AppendedRowsNodeFactory").get());
                    w.getNodeContainer(id)
                        .setUIInformation(NodeUIInformation.builder().setNodeLocation(40, 50, 10, 10).build());
                } catch (InstantiationException | IllegalAccessException | InvalidNodeFactoryExtensionException ex) {
                    throw new IllegalStateException(ex);
                }
            }, "node_added"),
            newTransformation(w -> w.addConnection(w.getID().createChild(13), 1, w.getID().createChild(14), 1),
                "connection_added"),
            newTransformation(w -> w.addWorkflowAnnotation(new WorkflowAnnotation()), "workflow_annotation_added"),
            newTransformation(w -> w.removeAnnotation(w.getWorkflowAnnotations().iterator().next()),
                "workflow_annotation_removed"),
            newTransformation(w -> {
                AnnotationData newAnno = new AnnotationData();
                w.getWorkflowAnnotations().iterator().next().copyFrom(newAnno, false);
            }, "workflow_annotation_changed"), newTransformation(w -> {
                AnnotationData newAnno = new AnnotationData();
                newAnno.setText("new anno text");
                w.getNodeContainer(w.getID().createChild(1)).getNodeAnnotation().copyFrom(newAnno, false);
            }, "node_annotation_added"), newTransformation(w -> {
                AnnotationData newAnno = new AnnotationData();
                newAnno.setText("yet another text");
                w.getNodeContainer(w.getID().createChild(1)).getNodeAnnotation().copyFrom(newAnno, false);
            }, "node_annotation_changed"), newTransformation(w -> {
                NativeNodeContainer oldNC = (NativeNodeContainer)w.getNodeContainer(w.getID().createChild(184));
                ModifiableNodeCreationConfiguration creationConfig = oldNC.getNode().getCopyOfCreationConfig().get();
                creationConfig.getPortConfig().get().getExtendablePorts().get("input").addPort(BufferedDataTable.TYPE);
                creationConfig.getPortConfig().get().getExtendablePorts().get("input").addPort(BufferedDataTable.TYPE);
                w.replaceNode(oldNC.getID(), creationConfig);
            }, "ports_added"));
    }

    private static WorkflowTransformation newTransformation(final Consumer<WorkflowManager> workflowTransformation,
        final String... changeNames) {
        return new WorkflowTransformation() {

            @Override
            public void apply(final WorkflowManager wfm) {
                workflowTransformation.accept(wfm);
            }

            @Override
            public String[] getChangeNames() {
                return changeNames;
            }
        };
    }

    /**
     * Represents a workflow transformation for a {@link WorkflowManager}-instance.
     */
    public interface WorkflowTransformation {

        /**
         * If a workflow transformation is applied to a {@link WorkflowManager} and a {@link WorkflowChangesListener} is
         * registered to this workflow manager, the transformation will result in one or more 'change callbacks' for the
         * listener. This method returns unique names (and thus also the number) of the expected change events.
         *
         * @return names of the expected 'change events' in the order they are expected to arrive (provided a
         *         {@link WorkflowChangesListener} available for the underlying {@link WorkflowManager}); if one of the
         *         change names is <code>null</code>, the respective change/patch will be ignored for testing
         */
        String[] getChangeNames();

        /**
         * Applies the workflow transformation to the provided workflow manager.
         *
         * @param wfm the workflow manager to apply the transformation to
         */
        void apply(WorkflowManager wfm);

    }

}
