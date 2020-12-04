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
    GENERAL_WEB_UI("/files/Test Gateway Workflow.knwf", "general_web_ui"),

    /**
     * Workflow to test the execution states.
     */
    EXECUTION_STATES("/files/Execution States.knwf", "execution states"),

    /**
     * A component project.
     */
    COMPONENT_PROJECT("/files/Component.knar", "component project"),

    /**
     * Workflow to test workflow and component metadata.
     */
    METADATA("/files/Workflow Metadata.knwf", "workflow_metadata"),

    /**
     * Workflow to test job managers.
     */
    JOB_MANAGER("/files/Job Manager.knwf", "job_manager");


    private final String m_url;

    private final String m_name;

    /**
     * @param url the workflow url
     * @param name the workflow name
     */
    private TestWorkflowCollection(final String url, final String name) {
        m_url = url;
        m_name = name;
    }

    /**
     * @return url of the workflow file
     */
    @Override
    public URL getUrlZipFile() {
        try {
            return GatewayServiceTestHelper.resolveToURL(m_url, TestWorkflowCollection.class);
        } catch (IOException ex) {
            // should never happen
            throw new RuntimeException(ex); // NOSONAR
        }
    }

    /**
     * @return the file of the workflow folder
     */
    @Override
    public File getUrlFolder() {
        try {
            return GatewayServiceTestHelper.resolveToFile(m_url.substring(0, m_url.length() - 5), TestWorkflowCollection.class);
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