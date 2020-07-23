/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 */
package org.knime.gateway.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.IOException;
import java.util.UUID;

import org.knime.gateway.api.entity.JavaObjectEnt;
import org.knime.gateway.api.entity.JavaObjectEnt.JavaObjectEntBuilder;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.ViewDataEnt;
import org.knime.gateway.api.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

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
        UUID wfId = loadWorkflow(TestWorkflow.VIEWS);
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
