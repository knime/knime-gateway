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
package com.knime.gateway.v0.service;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowLoader;
import com.knime.gateway.v0.entity.BoundsEnt;
import com.knime.gateway.v0.entity.BoundsEnt.BoundsEntBuilder;
import com.knime.gateway.v0.entity.WorkflowEnt;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotFoundException;

/**
 * Tests to make changes to single workflow annotations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ChangeWorkflowAnnotationTestHelper extends AbstractGatewayServiceTestHelper {

    /**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param serviceProvider
     * @param entityResultChecker
     */
    public ChangeWorkflowAnnotationTestHelper(final ServiceProvider serviceProvider,
        final ResultChecker entityResultChecker, final WorkflowLoader workflowLoader) {
        super("changeworkflowannotation", serviceProvider, entityResultChecker, workflowLoader);
    }

    /**
     * Tests to reset, cancel and execute individual nodes and all nodes from the job view.
     *
     * @throws Exception if an error occurs
     */
    public void testChangeAnnotationBounds() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW);

        WorkflowEnt workflow = ws().getWorkflow(wfId).getWorkflow();
        cr(workflow, "workflowent_root");

        //move and resize annotation
        BoundsEnt newBounds = builder(BoundsEntBuilder.class).setX(400).setY(400).setHeight(600).setWidth(600).build();
        as().setAnnotationBounds(wfId, "root_0", newBounds);
        cr(ws().getWorkflow(wfId).getWorkflow(), "workflowent_root_annotation_moved");

        //move and resize back
        as().setAnnotationBounds(wfId, "root_0", workflow.getWorkflowAnnotations().get("root_0").getBounds());
        cr(ws().getWorkflow(wfId).getWorkflow(), "workflowent_root");

        //move and resize annotation within a metanode
        as().setAnnotationBoundsInSubWorkflow(wfId, "6", "6_0", newBounds);
        cr(ws().getSubWorkflow(wfId, "6").getWorkflow(), "workflowent_6_annotation_moved");

        //try moving a non-existing annotation
        try {
            as().setAnnotationBounds(wfId, "root_3", newBounds);
        } catch (NotFoundException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Annotation for id 'root_3' not found"));
        }
    }
}