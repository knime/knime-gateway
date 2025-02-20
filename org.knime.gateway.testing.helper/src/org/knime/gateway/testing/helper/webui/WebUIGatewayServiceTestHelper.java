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

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.MetaNodeEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.NodeFactoryKeyEnt;
import org.knime.gateway.api.webui.entity.NodePortEnt;
import org.knime.gateway.api.webui.entity.NodeStateEnt;
import org.knime.gateway.api.webui.entity.PatchEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt;
import org.knime.gateway.api.webui.entity.PatchOpEnt.PatchOpEntBuilder;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.ProjectMetadataEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.SpaceItemReferenceEnt;
import org.knime.gateway.api.webui.entity.SpacePathSegmentEnt;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.service.ComponentService;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.NodeRepositoryService;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.api.webui.service.PortService;
import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.impl.webui.entity.DefaultWorkflowSnapshotEnt;
import org.knime.gateway.json.util.JsonUtil;
import org.knime.gateway.testing.helper.GatewayServiceTestHelper;
import org.knime.gateway.testing.helper.ObjectToString;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WebUIGatewayServiceTestHelper extends GatewayServiceTestHelper {

    private final ServiceProvider m_serviceProvider;

    /**
     * Helps to create an {@link ResultChecker}-instance.
     *
     * @return the new result checker instance
     */
    public static ResultChecker createResultChecker() {
        return createResultChecker(WebUIGatewayServiceTestHelper.class);
    }

    /**
     * Helps to create an {@link ResultChecker}-instance.
     *
     * @param testClass the class of the test to determine the plugin to write the test results into (in the
     *            /files-folder of the respective plugin)
     * @return the new result checker instance
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ResultChecker createResultChecker(final Class<?> testClass) {
        var mapper = new ObjectMapper();
        JsonUtil.addWebUIMixIns(mapper);
        JsonUtil.addIDEntityDeSerializer(mapper);
        JsonUtil.addDateTimeDeSerializer(mapper);
        JsonUtil.addBitSetDeSerializer(mapper);

        JsonMapper.builder().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        var objToString = new ObjectToString(mapper);

        /** Same as above but for the snapshot id. */
        objToString.addException(DefaultWorkflowSnapshotEnt.class, "snapshotId",
            (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_SNAPSHOT_ID"));

        /**
         * The name-field of a workflow varies if the test is executed as part of an it-test or unit-test.
         */
        objToString.addException(MetaNodeEnt.class, "name", (v, gen, e) -> {
            if (e.getId().equals(NodeIDEnt.getRootID())) {
                gen.writeString("PLACEHOLDER_FOR_NAME");
            } else {
                gen.writeString(e.getName());
            }
        });

        objToString.addException(NodeStateEnt.class, "warning",
            (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_WARNING_MESSAGE"));

        objToString.addException(NodeStateEnt.class, "error",
            (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_ERROR_MESSAGE"));

        /**
         * Canonical sorting of the connectedVia-list.
         */
        objToString.addException(NodePortEnt.class, "connectedVia", (v, gen, e) ->
            gen.writeString("[ " + (String)((List)v).stream().sorted((o1, o2) -> o1.toString().compareTo(o2.toString()))
                .map(Object::toString).collect(Collectors.joining(", ")) + " ]")
        );

        /**
         * Canonical sorting of the compatibleTypes-list.
         */
        objToString.addException(PortTypeEnt.class, "compatibleTypes", (v, gen, e) -> {
            List<String> l = ((List<String>)v).stream().sorted().collect(Collectors.toList());
            gen.writeRawValue(objToString.toString(l));
        });

        /**
         * Canonical sorting of the patch operations.
         * And skip the values of patches that are non-deterministic (e.g. 'portContentVersion').
         */
        objToString.addException(PatchEnt.class, "ops", (v, gen, e) -> {
            List<PatchOpEnt> l = ((List<PatchOpEnt>)v).stream()
                .map(WebUIGatewayServiceTestHelper::replaceNonDeterministicPatchValues)//
                .sorted(Comparator.<PatchOpEnt, String> comparing(o -> o.getOp().toString())
                    .thenComparing(o -> String.valueOf(o.getFrom())).thenComparing(PatchOpEnt::getPath))//
                .collect(Collectors.toList());
            gen.writeRawValue(objToString.toString(l));
        });

        /**
         * Non-deterministic field.
         */
        objToString.addException(NodePortEnt.class, "portContentVersion",
            (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_VERSION"));

        /**
         * Non-deterministic field.
         */
        objToString.addException(NodeEnt.class, "inputContentVersion",
                (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_VERSION"));

        /**
         * Non-deterministic field.
         */
        objToString.addException(SpaceItemEnt.class, "id", (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_ID"));

        /**
         * Non-deterministic field.
         */
        objToString.addException(SpacePathSegmentEnt.class, "id", (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_ID"));

        objToString.addException(NodeFactoryKeyEnt.class, "settings",
            (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_FACTORY_SETTINGS"));

        /**
         * Non-deterministic field.
         */
        objToString.addException(SpaceItemReferenceEnt.class, "itemId",
            (v, gen, e) -> gen.writeString("PLACEHOLDER_FOR_ITEM_ID"));

        /**
         * Value keeps increasing for legacy workflow metadata due to AP-20406 changes
         */
        objToString.addException(ProjectMetadataEnt.class, "lastEdit",
            (v, gen, e) -> gen.writeString("DATE_TIME_PLACEHOLDER"));

        /**
         * Progress is usually not 'captured' at a fixed state.
         */
        objToString.addException(NodeStateEnt.class, "progress",
            (v, gen, e) -> gen.writeString("PROGRESS_PLACEHOLDER"));

        /**
         * Progress is usually not 'captured' at a fixed state.
         */
        objToString.addException(NodeStateEnt.class, "progressMessages",
            (v, gen, e) -> gen.writeString("PROGRESS_MESSAGES_PLACEHOLDER"));

        try {
            return new ResultChecker(objToString, CoreUtil.resolveToFile("/files/test_snapshots", testClass));
        } catch (IOException ex) {
            // should never happen
            throw new RuntimeException(ex); //NOSONAR
        }
    }

    private static PatchOpEnt replaceNonDeterministicPatchValues(final PatchOpEnt o) {
        String newValue = null;
        if (o.getPath().endsWith("/portContentVersion") || o.getPath().endsWith("/inputContentVersion")) {
            newValue = "PLACEHOLDER_FOR_VERSION";
        } else if (o.getPath().endsWith("/state/warning")) {
            newValue = replaceNodeIdsWithPlaceholder((String)o.getValue());
        }
        return newValue == null ? o
            : builder(PatchOpEntBuilder.class).setPath(o.getPath()).setOp(o.getOp()).setValue(newValue).build();
    }

    private static final Pattern NODE_ID_PATTERN = Pattern.compile("\\d+(:\\d+)+");

    private static String replaceNodeIdsWithPlaceholder(final String stringWithNodeIds) {
        if (stringWithNodeIds == null) {
            return null;
        }
        return NODE_ID_PATTERN.matcher(stringWithNodeIds).replaceAll("NODE_ID_PLACEHOLDER");
    }

    /**
     * @param testClass
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     */
    protected WebUIGatewayServiceTestHelper(final Class<?> testClass, final ResultChecker entityResultChecker,
        final ServiceProvider serviceProvider, final WorkflowLoader workflowLoader) {
        super(testClass, entityResultChecker, workflowLoader);
        m_serviceProvider = serviceProvider;
    }

    /**
     * @param testClass
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    protected WebUIGatewayServiceTestHelper(final Class<?> testClass, final ResultChecker entityResultChecker,
        final ServiceProvider serviceProvider, final WorkflowLoader workflowLoader,
        final WorkflowExecutor workflowExecutor) {
        super(testClass, entityResultChecker, workflowLoader, workflowExecutor);
        m_serviceProvider = serviceProvider;
    }

    /**
     * A shortcut to get the workflow service instance.
     *
     * @return a workflow service instance
     */
    protected WorkflowService ws() {
        return m_serviceProvider.getWorkflowService();
    }

    /**
     * A shortcut to get the node service instance.
     *
     * @return a node service instance
     */
    protected NodeService ns() {
        return m_serviceProvider.getNodeService();
    }

    /**
     * A shortcut to get the node service instance.
     *
     * @return a port service instance
     */
    protected PortService ps() {
        return m_serviceProvider.getPortService();
    }

    /**
     * Shortcut to get the event service instance.
     *
     * @return an event service instance
     */
    protected EventService es() {
        return m_serviceProvider.getEventService();
    }

    /**
     * Shortcut to get the node repository service instance.
     * @return a node repository service instance
     */
    protected NodeRepositoryService nrs() {
        return m_serviceProvider.getNodeRepositoryService();
    }

    /**
     * Shortcut to get a space service instance.
     *
     * @return a space service instance
     */
    protected SpaceService ss() {
        return m_serviceProvider.getSpaceService();
    }

    /**
     * Shortcut to get a component service instance.
     *
     * @return a component service instance
     */
    protected ComponentService cs() {
        return m_serviceProvider.getComponentService();
    }

    /**
     * Execute the given workflow command and return the new workflow snapshot
     *
     * @param commandEnt
     * @param workflowId
     * @return the updated workflow after the command was executed
     * @throws Exception
     */
    @SuppressWarnings("java:S112") // generic exception
    protected void executeWorkflowCommand(final WorkflowCommandEnt commandEnt, final String workflowId)
        throws Exception {
        ws().executeWorkflowCommand(workflowId, NodeIDEnt.getRootID(), commandEnt);
    }

    /**
     * @param wfId
     * @return the updated workflow after the last command was undone
     * @throws Exception
     */
    @SuppressWarnings("java:S112") // generic exception
    protected void undoWorkflowCommand(final String wfId) throws Exception {
        ws().undoWorkflowCommand(wfId, NodeIDEnt.getRootID());
    }

    /**
     * @param wfId
     * @return the update workflow after a command was redone
     * @throws Exception
     */
    @SuppressWarnings("java:S112") // generic exception
    protected void redoWorkflowCommand(final String wfId) throws Exception {
        ws().redoWorkflowCommand(wfId, NodeIDEnt.getRootID());
    }

    /**
     * Get the current state of the workflow, re-throwing any exception as a runtime exception. Useful if used inside a
     * lambda expression.
     *
     * @param workflowId The ID of the workflow to fetch
     * @return The entity describing the current state of the workflow
     */
    @SuppressWarnings({"java:S2221", "java:S112"}) // exception handling
    WorkflowEnt getWorkflow(final String workflowId) {
        WorkflowEnt workflow;
        try {
            workflow = ws().getWorkflow(workflowId, NodeIDEnt.getRootID(), true, null).getWorkflow();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return workflow;
    }

    void awaitTrue(final String workflowId, final Predicate<WorkflowEnt> condition) {
        Awaitility.await().atMost(5, TimeUnit.SECONDS).pollInterval(100, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> condition.test(getWorkflow(workflowId)));
    }

    void awaitFalse(final String workflowId, final Predicate<WorkflowEnt> condition) {
        awaitTrue(workflowId, condition.negate());
    }

}
