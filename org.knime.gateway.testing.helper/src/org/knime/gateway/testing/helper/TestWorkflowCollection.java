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
     * Workflow to test re-execution of component views.
     */
    COMPONENT_REEXECUTION("/files/testflows/ReexecutingComponent", "component_view_reexecution"),

    /**
     * Workflow to test the try-catch nodes.
     */
    TRY_CATCH("/files/testflows/Try-Catch", "try_catch"),

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
     * Contains nodes with the node messages (including a node message with the 'issue' and 'resolution'
     * properties).
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
    LINKED_COMPONENT("/files/testflows/Linked Component", "linked_component"),

    /**
     * Contains connected and unconnected nodes to test auto connection
     */
    AUTO_CONNECT_NODES("/files/testflows/Auto Connect Nodes", "auto_connect_nodes"),

    /**
     * Workflow to test encrypted metanodes and components.
     */
    ENCRYPTED_METANODE_AND_COMPONENT("/files/testflows/Encrypted Metanode and Component",
            "encrypted_metanode_and_component"),

    /**
     * Minimal workflow to represent the current version of a project.
     */
    VERSIONS_CURRENT_STATE("/files/testflows/Versions/Current State", "current_state"),

    /**
     * Minimal workflow to represent an earlier version of a project.
     */
    VERSIONS_EARLIER_VERSION("/files/testflows/Versions/Earlier Version", "earlier_Version"),

    /**
     * Extended workflow to represent the current version of a project.
     */
    VERSIONS_EXTENDED_CURRENT_STATE("/files/testflows/Versions_Extended/Current Version", "current_state_ext"),

    /**
     * Extended workflow to represent an earlier version of a project.
     */
    VERSIONS_EXTENDED_EARLIER_VERSION("/files/testflows/Versions_Extended/Earlier Version", "earlier_Version_ext");

    private final String m_workflowDir;

    private final String m_name;

    /**
     * @param workflowDir the relative path for the workflow directory
     * @param name the workflow name
     */
    TestWorkflowCollection(final String workflowDir, final String name) {
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