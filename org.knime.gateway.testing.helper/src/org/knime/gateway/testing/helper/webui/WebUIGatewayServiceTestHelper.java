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
 *   Aug 3, 2020 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import java.io.IOException;

import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.WorkflowNodeEnt;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.impl.webui.entity.DefaultNodeEnt;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowSnapshotEnt;
import org.knime.gateway.impl.webui.service.DefaultWorkflowService;
import org.knime.gateway.json.util.JsonUtil;
import org.knime.gateway.testing.helper.GatewayServiceTestHelper;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ResultChecker.PropertyExceptions;
import org.knime.gateway.testing.helper.WorkflowLoader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WebUIGatewayServiceTestHelper extends GatewayServiceTestHelper {

    /**
     * Helps to create an {@link ResultChecker}-instance.
     *
     * @param rewriteTestResults whether to overwrite the result-snapshots
     * @return the new result checker instance
     */
    public static ResultChecker createResultChecker(final boolean rewriteTestResults) {
        PropertyExceptions pe = new PropertyExceptions();

        /**
         * Name of the field that holds the root workflow id (e.g. in NodeEnt). Since the id changes with every run it
         * cannot be compared.
         */
        pe.addException(DefaultNodeEnt.class, "rootWorkflowID",
            (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_WORKFLOW_ID"));

        /** Same as above but for the snapshot id. */
        pe.addException(DefaultWorkflowSnapshotEnt.class, "snapshotID",
            (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_SNAPSHOT_ID"));

        /**
         * The name-field of a workflow varies if the test is executed as part of an it-test or unit-test.
         */
        pe.addException(WorkflowNodeEnt.class, "name", (v, gen, e) -> {
            if (e.getId().equals(NodeIDEnt.getRootID())) {
                gen.writeString("PLACEHOLDER_FOR_NAME");
            } else {
                gen.writeString(e.getName());
            }
        });

        ObjectMapper objectMapper = new ObjectMapper();
        JsonUtil.addWebUIMixIns(objectMapper);
        JsonUtil.addIDEntitySerializer(objectMapper);
        objectMapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        try {
            return new ResultChecker(rewriteTestResults, pe, objectMapper,
                resolveToFile("/files", WebUIGatewayServiceTestHelper.class));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param testName
     * @param entityResultChecker
     * @param workflowLoader
     */
    protected WebUIGatewayServiceTestHelper(final String testName, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader) {
        super(testName, entityResultChecker, workflowLoader);
    }

    /**
     * A shortcut to get the workflow service instance.
     *
     * @return a workflow service instance
     */
    protected WorkflowService ws() {
        return DefaultWorkflowService.getInstance();
    }

}