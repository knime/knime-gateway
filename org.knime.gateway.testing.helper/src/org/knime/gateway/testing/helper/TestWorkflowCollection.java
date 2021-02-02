package org.knime.gateway.testing.helper;

import java.io.File;
import java.io.IOException;
import java.net.URL;

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
     * Workflow to test job managers.
     */
    STREAMING_EXECUTION("/files/testflows/Streaming Execution", "streaming_execution");


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
            return GatewayServiceTestHelper.resolveToFile(m_workflowDir, TestWorkflowCollection.class);
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