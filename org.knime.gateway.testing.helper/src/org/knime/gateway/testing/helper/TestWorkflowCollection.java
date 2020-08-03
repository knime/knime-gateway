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
     * The main workflow the tests are based on.
     */
    GENERAL("/files/Test Gateway Workflow.knwf", "workflow");

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
            throw new RuntimeException(ex);
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
            throw new RuntimeException(ex);
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