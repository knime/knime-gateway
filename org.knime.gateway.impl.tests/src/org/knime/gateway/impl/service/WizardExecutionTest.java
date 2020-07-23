/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package org.knime.gateway.impl.service;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.UUID;

import org.junit.Test;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.entity.WizardPageInputEnt.WizardPageInputEntBuilder;
import org.knime.gateway.impl.project.WorkflowProject;
import org.knime.gateway.impl.project.WorkflowProjectManager;
import org.knime.gateway.service.AbstractGatewayServiceTestHelper.TestWorkflow;
import org.knime.gateway.testing.helper.GatewayTestCollection;
import org.knime.gateway.testing.helper.TestUtil;

/**
 * Test for the wizard execution implementation that is not covered by {@link GatewayTestCollection}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WizardExecutionTest {

    /**
     * Tests that 'clearReport' is called when workflow is reseted to previous page.
     *
     * @throws Exception
     */
    @Test
    public void testClearReportCallOnResetToPreviousPage() throws Exception {
        UUID uuid = UUID.randomUUID();
        WorkflowProject workflowProject = mock(WorkflowProject.class);
        WorkflowProjectManager.addWorkflowProject(uuid, workflowProject);
        WorkflowManager wfm = TestUtil.loadWorkflow(TestWorkflow.WIZARD_EXECUTION.getUrlFolder());
        when(workflowProject.openProject()).thenReturn(wfm);
        when(workflowProject.getID()).thenReturn(uuid.toString());
        DefaultWizardExecutionService.getInstance().executeToNextPage(uuid, false, 60000L,
            builder(WizardPageInputEntBuilder.class).build());
        DefaultWizardExecutionService.getInstance().executeToNextPage(uuid, false, 60000L,
            builder(WizardPageInputEntBuilder.class)
                .setViewValues(Collections.singletonMap("5:0:1", "{\"integer\": 3 }")).build());
        DefaultWizardExecutionService.getInstance().resetToPreviousPage(uuid, 2000l);

        //here is what we are actually testing for
        verify(workflowProject).clearReport();
    }

}
