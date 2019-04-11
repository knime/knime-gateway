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
package com.knime.gateway.service;

import static com.knime.gateway.entity.EntityBuilderManager.builder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.knime.gateway.entity.JavaObjectEnt;
import com.knime.gateway.entity.JavaObjectEnt.JavaObjectEntBuilder;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.ViewDataEnt;
import com.knime.gateway.service.util.ServiceExceptions.InvalidRequestException;
import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.WorkflowExecutor;
import com.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests the javascript views (e.g. whether they are downloaded properly and settings are saved back).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JSViewTestHelper extends AbstractGatewayServiceTestHelper {

	private ObjectMapper m_objectMapper;

    /**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param workflowExecutor
     * @param serviceProvider
     * @param entityResultChecker
     */
    public JSViewTestHelper(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super("jsview", serviceProvider, entityResultChecker, workflowLoader, workflowExecutor);
        m_objectMapper = new ObjectMapper();
        m_objectMapper.registerModule(new Jdk8Module());
        m_objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Tests to get and change node's views data.
     *
     * @throws Exception if an error occurs
     */
    public void testGetAndSetNodeViewsData() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_VIEWS);
        executeWorkflow(wfId);

        //get node views data
        ViewDataEnt viewDataEnt = ns().getViewData(wfId, new NodeIDEnt(2));
        //check plain result
        checkViewDataEnt(viewDataEnt, "null");

        //manipulate the views value and send modification back to server
        String newViewValue = viewDataEnt.getViewValue().getJsonContent().replace("\"chartTitle\":null", "\"chartTitle\":\"title\"");
        JavaObjectEnt viewContentEnt = builder(JavaObjectEntBuilder.class)
            .setClassname(viewDataEnt.getViewValue().getClassname()).setJsonContent(newViewValue).build();
        ns().setViewValue(wfId, new NodeIDEnt(2), true, viewContentEnt);
        //check new view data
        checkViewDataEnt(ns().getViewData(wfId, new NodeIDEnt(2)), "title");

        //check default parameter - i.e. after re-execute, the view data should be the same
        ns().changeAndGetNodeState(wfId, new NodeIDEnt(2), "reset");
        ns().changeAndGetNodeState(wfId, new NodeIDEnt(2), "execute");
        assertThat("View settings have changed after reset",
            ns().getViewData(wfId, new NodeIDEnt(2)).getViewValue().getJsonContent(),
            is(newViewValue));

        //test exceptions when view data is requested for a node that doesn't have a js-view
        try {
            ns().getViewData(wfId, new NodeIDEnt(1));
            fail("Expected ServiceException to be thrown");
        } catch (InvalidRequestException e) {
            assertThat("Unexpected exception message", e.getMessage(), is("Node doesn't provide view data."));
        }
    }

    private void checkViewDataEnt(final ViewDataEnt vd, final String expectedTitle) throws JsonProcessingException, IOException {
        String messagePropertyMismatch = "property doesn't match";
        String columnKeys = "[\"Universe_0_0\",\"Universe_0_1\",\"Universe_1_0\",\"Universe_1_1\",\"Cluster Membership\"]";

        assertThat(messagePropertyMismatch, vd.getJavascriptObjectID(), is("org.knime.js.base.node.viz.plotter.line"));
        assertThat(messagePropertyMismatch, vd.isHideInWizard(), is(false));
        assertThat(messagePropertyMismatch, vd.getViewRepresentation().getClassname(), is("org.knime.js.base.node.viz.plotter.line.LinePlotViewRepresentation"));
        assertThat(messagePropertyMismatch, vd.getViewValue().getClassname(), is("org.knime.js.base.node.viz.plotter.line.LinePlotViewValue"));
        //parse view representation for partial check (required, because the serialization order of the properties varies)
        JsonNode viewRep = m_objectMapper.readTree(vd.getViewRepresentation().getJsonContent());
        assertThat(messagePropertyMismatch, viewRep.get("keyedDataset").get("columnKeys").toString(), is(columnKeys));
        assertThat(messagePropertyMismatch, viewRep.get("keyedDataset").get("rows").get(0).get("values").toString(),
            is("[0.37991494049431646,0.4996498997674314,0.48157471108388306,0.9127872185376678,0.0]"));

        //parse view value for partial check
        JsonNode viewValue = m_objectMapper.readTree(vd.getViewValue().getJsonContent());
        assertThat(messagePropertyMismatch, viewValue.get("yColumns").toString(), is(columnKeys));
        assertThat(messagePropertyMismatch, viewValue.get("chartTitle").asText(), is(expectedTitle));
    }
}
