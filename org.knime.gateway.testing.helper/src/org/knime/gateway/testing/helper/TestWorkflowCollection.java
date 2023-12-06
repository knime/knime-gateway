package org.knime.gateway.testing.helper;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.knime.gateway.api.util.CoreUtil;

/**
 * Workflows used in the tests.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public enum TestWorkflowCollection implements org.knime.gateway.testing.helper.TestWorkflow {

    /**
     * The main workflow the web-ui tests are based on.
     */
    GENERAL_WEB_UI("/files/testflows/Test Gateway Workflow", "general_web_ui"),

    /**
     * Workflow to test the execution states.
     */
    EXECUTION_STATES("/files/testflows/Execution States", "execution states"),

    /**
     * A component project.
     */
    COMPONENT_PROJECT("/files/testflows/Component", "component project"),

    /**
     * Workflow to test workflow and component metadata.
     */
    METADATA("/files/testflows/Workflow Metadata", "workflow_metadata"),

    /**
     * Workflow to test workflow and component metadata, new format.
     */
    METADATA2("/files/testflows/Workflow Metadata New Format", "workflow_metadata2"),


    /**
     * Workflow to test job managers.
     */
    STREAMING_EXECUTION("/files/testflows/Streaming Execution", "streaming_execution"),

    /**
     * Workflow to test loop execution.
     */
    LOOP_EXECUTION("/files/testflows/Loop Execution", "loop execution"),

    /**
     * Workflow to test ports and port types.
     */
    PORTS("/files/testflows/Ports", "ports"),

        /**
         * A workflow without any native nodes, but an empty metanode (#1) and an empty component (#2).
         */
    HOLLOW("/files/testflows/Hollow", "hollow"),

    /**
     * Workflow with different types of nodes having a view.
     */
    VIEW_NODES("/files/testflows/View Nodes", "view nodes"),

    /**
     * Contains different kinds of metanodes and components and nodes and annotations that can be collapsed.
     */
    METANODES_COMPONENTS("/files/testflows/Metanodes and Components", "metanodes_components"),

    /**
     * Contains nested linked components.
     */
    NESTED_LINKED_COMPONENT_PROJECT("/files/testflows/Nested-Linked-Components/Project", "nested_linked_components"),

    /**
     * Contains a node with the new node message properties.
     */
    NODE_MESSAGE("/files/testflows/Node Message", "node_message"),

    /**
     * Contains a contemporary and a legacy annotation.
     */
    ANNOTATIONS("/files/testflows/Annotations", "annotations"),

    /**
     * To test operations on bendpoints.
     */
    BENDPOINTS("/files/testflows/Bendpoints", "bendpoints"),

    /**
     * To test updating linked components.
     */
    UPDATE_LINKED_COMPONENTS("/files/testflows/Update Linked Components", "update_linked_components"),

    /**
     * Linked component (!) needed to mock URL to file resolver.
     */
    LINKED_COMPONENT("/files/testflows/Linked Component", "linked_component");

    private final String m_workflowDir;

    private final String m_name;

    /**
     * @param the relative path for the workflow directory
     * @param name the workflow name
     */
    private TestWorkflowCollection(final String workflowDir, final String name) {
        m_workflowDir = workflowDir;
        m_name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL createKnwfFileAndGetUrl() throws IOException {
        return TestWorkflow.createKnwfFile(getWorkflowDir()).toURI().toURL();
    }

    /**
     * @return the file of the workflow folder
     */
    @Override
    public File getWorkflowDir() {
        try {
            return CoreUtil.resolveToFile(m_workflowDir, TestWorkflowCollection.class);
        } catch (IOException ex) {
            // should never happen
            throw new RuntimeException(ex); // NOSONAR
        }
    }

    /**
     * @return name of the loaded workflow
     */
    @Override
    public String getName() {
        return m_name;
    }

}