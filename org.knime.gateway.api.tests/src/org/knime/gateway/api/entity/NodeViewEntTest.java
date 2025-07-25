/*
 * ------------------------------------------------------------------------
 *
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
 * History
 *   Oct 4, 2021 (hornm): created
 */
package org.knime.gateway.api.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.property.ColorAttr;
import org.knime.core.data.property.ColorHandler;
import org.knime.core.data.property.ColorModel;
import org.knime.core.data.property.ColorModelNominal;
import org.knime.core.data.property.ColorModelRange;
import org.knime.core.internal.KNIMEPath;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.config.base.JSONConfig.WriterConfig;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.report.ReportUtil.ImageFormat;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeMessage;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.virtual.subnode.VirtualSubNodeInputNodeFactory;
import org.knime.core.util.Pair;
import org.knime.core.webui.data.ApplyDataService;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.view.NodeTableView;
import org.knime.core.webui.node.view.NodeView;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.core.webui.page.Page;
import org.knime.core.webui.page.Resource;
import org.knime.gateway.api.entity.RenderingConfigEnt.DefaultRenderingConfigEnt;
import org.knime.gateway.api.entity.RenderingConfigEnt.ImageRenderingConfigEnt;
import org.knime.gateway.api.entity.RenderingConfigEnt.RenderingConfigType;
import org.knime.gateway.api.entity.RenderingConfigEnt.ReportRenderingConfigEnt;
import org.knime.testing.node.SourceNodeTestFactory;
import org.knime.testing.node.view.NodeViewNodeFactory;
import org.knime.testing.node.view.NodeViewNodeModel;
import org.knime.testing.node.view.NodeViewTestUtil;
import org.knime.testing.util.WorkflowManagerUtil;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tests {@link NodeViewEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("java:S2698") // we accept assertions without messages
class NodeViewEntTest {

    private static final String JAVA_AWT_HEADLESS = "java.awt.headless";

    private WorkflowManager m_wfm;

    private String m_javaAwtHeadlessSysPropValue;

    @BeforeEach
    void setUp() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
        m_javaAwtHeadlessSysPropValue = System.getProperty(JAVA_AWT_HEADLESS);
        // we assume that all test are run in 'headful' mode
        System.clearProperty(JAVA_AWT_HEADLESS);
    }

    @AfterEach
    void tearDown() {
        WorkflowManagerUtil.disposeWorkflow(m_wfm);
        m_wfm = null;
        if (m_javaAwtHeadlessSysPropValue == null) {
            System.clearProperty(JAVA_AWT_HEADLESS);
        } else {
            System.setProperty(JAVA_AWT_HEADLESS, m_javaAwtHeadlessSysPropValue);
        }
    }

    /**
     * Tests the creation of {@link NodeViewEnt} instances.
     *
     * @throws IOException
     * @throws InvalidSettingsException
     */
    @Test
    @SuppressWarnings("java:S5961") // Allow too big number of assertions
    void testNodeViewEnt() throws IOException, InvalidSettingsException {

        NativeNodeContainer nncWithoutNodeView =
            WorkflowManagerUtil.createAndAddNode(m_wfm, new VirtualSubNodeInputNodeFactory(null, new PortType[0]));
        Assertions.assertThatThrownBy(() -> NodeViewEnt.create(nncWithoutNodeView))
            .isInstanceOf(IllegalArgumentException.class);

        Function<NodeViewNodeModel, NodeView> nodeViewCreator = m -> new TestNodeView();
        NativeNodeContainer nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(nodeViewCreator));

        // test entity  when node is _not_ executed
        var ent = NodeViewEnt.createForImageGeneration(nnc, null, ImageFormat.SVG, "test_action_id");
        assertThat(ent.getInitialData()).isNull();
        var renderingConfig = ent.getRenderingConfig();
        assertThat(ent.getRenderingConfig().getType()).isEqualTo(RenderingConfigType.IMAGE);
        assertThat(((ImageRenderingConfigEnt)renderingConfig).actionId()).isEqualTo("test_action_id");
        assertThat(((ImageRenderingConfigEnt)renderingConfig).imageFormat()).isEqualTo(ImageFormat.SVG);
        assertThat(ent.getNodeInfo().getNodeState()).isEqualTo("configured");
        assertThat(ent.getNodeInfo().isCanExecute()).isTrue();
        assertThat(ent.isDeactivationRequired()).isNull();

        initViewSettingsAndExecute(nnc);
        ent = NodeViewEnt.create(nnc, null);
        checkViewSettings(ent, "view setting value");

        overwriteViewSettingWithFlowVariable(nnc);
        ent = NodeViewEnt.create(nnc, null);
        checkViewSettings(ent, KNIMEPath.getWorkspaceDirPath().getAbsolutePath());
        checkOutgoingFlowVariable(nnc, "exposed_flow_variable", "exposed view settings value");

        nnc.setNodeMessage(NodeMessage.newWarning("node message"));
        nnc.getNodeAnnotation().getData().setText("node annotation");
        ent = NodeViewEnt.create(nnc, null);
        assertThat(ent.getProjectId()).startsWith("workflow");
        assertThat(ent.getWorkflowId()).isEqualTo("root");
        assertThat(ent.getNodeId()).isEqualTo("root:2");
        assertThat(ent.getInitialData()).contains("view setting key");
        assertThat(ent.getRenderingConfig().getType()).isEqualTo(RenderingConfigType.DEFAULT);
        assertThat(ent.getInitialSelection()).isNull();
        assertThat(ent.getColorModels()).isNull();
        assertThat(ent.getColumnNamesColorModel()).isNull();
        assertThat(ent.isDeactivationRequired()).isTrue();
        var resourceInfo = ent.getResourceInfo();
        assertThat(resourceInfo.getPath()).endsWith("index.html");
        assertThat(resourceInfo.getBaseUrl()).isEqualTo("https://org.knime.core.ui.view/");
        assertThat(resourceInfo.getType()).isEqualTo(Resource.ContentType.HTML.toString());
        assertThat(resourceInfo.getId()).isNotNull();
        var nodeInfo = ent.getNodeInfo();
        assertThat(nodeInfo.getNodeName()).isEqualTo("NodeView");
        assertThat(nodeInfo.getNodeAnnotation()).isEqualTo("node annotation");
        assertThat(nodeInfo.getNodeState()).isEqualTo("executed");
        assertThat(nodeInfo.getNodeWarnMessage()).isEqualTo("node message");
        assertThat(nodeInfo.getNodeErrorMessage()).isNull();
        assertThat(nodeInfo.isCanExecute()).isNull();

        // a node view as a 'component' without initial data
        nodeViewCreator = m -> {
            Page p = Page.create().fromFile().bundleID("org.knime.gateway.api.tests").basePath("files").relativeFilePath("component.js");
            return NodeViewTestUtil.createNodeView(p);
        };
        nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(nodeViewCreator));
        m_wfm.executeAllAndWaitUntilDone();
        ent = NodeViewEnt.create(nnc, null);
        resourceInfo = ent.getResourceInfo();
        assertThat(ent.getInitialData()).isNull();
        assertThat(resourceInfo.getType()).isEqualTo(Resource.ContentType.SHADOW_APP.toString());
        assertThat(resourceInfo.getPath()).endsWith("component.js");
        assertThat(resourceInfo.getBaseUrl()).isEqualTo("https://org.knime.core.ui.view/");

        // test to create a node view entity while running headless (e.g. on the executor)
        NativeNodeContainer nnc2 = nnc;
        runOnExecutor(() -> {
            var ent2 = NodeViewEnt.create(nnc2, null);
            assertThat(ent2.getResourceInfo().getPath()).endsWith("component.js");
            assertThat(ent2.getResourceInfo().getBaseUrl()).isNull();
        });

    }

    /**
     * Extra tests for the {@link NodeViewEnt}'s {@link NodeInfoEnt#isCanExecute()} property.
     *
     * @throws IOException
     */
    @Test
    void testCanExecuteNodeViewEnt() throws IOException {
        // node view node with one unconnected input
        var nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(1, 1));
        var ent = NodeViewEnt.create(nnc, null);
        assertThat(ent.getNodeInfo().getNodeState()).isEqualTo("idle");
        assertThat(ent.getNodeInfo().isCanExecute()).isFalse();

        // test node view with available input spec
        var nnc2 = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(0, 1));
        m_wfm.addConnection(nnc2.getID(), 1, nnc.getID(), 1);
        ent = NodeViewEnt.create(nnc, null);
        assertThat(ent.getNodeInfo().getNodeState()).isEqualTo("configured");
        assertThat(ent.getNodeInfo().isCanExecute()).isTrue();

        // test node view with available input spec but failing configure-call (i.e. node is idle but input spec available)
        var nnc3 = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(1, 0) {

            @Override
            public NodeViewNodeModel createNodeModel() {
                return new NodeViewNodeModel(1, 0) {
                    @Override
                    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
                        throw new InvalidSettingsException("problem");
                    }
                };
            }
        });
        m_wfm.addConnection(nnc2.getID(), 1, nnc3.getID(), 1);
        ent = NodeViewEnt.create(nnc3, null);
        assertThat(ent.getNodeInfo().getNodeState()).isEqualTo("idle");
        assertThat(ent.getNodeInfo().isCanExecute()).isTrue();
        assertThat(ent.getNodeInfo().getNodeWarnMessage()).isEqualTo("problem");
    }

    /**
     * Tests whether {@link ResourceInfoEnt#getBaseUrl()} is present or not depending on at least two conditions.
     */
    @Test
    void testBaseUrlInNodeViewEnt() {
        Function<NodeViewNodeModel, NodeView> nodeViewCreator = m -> new TestNodeView();
        NativeNodeContainer nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(nodeViewCreator));
        m_wfm.executeAllAndWaitUntilDone();

        var ent = NodeViewEnt.create(nnc, null);
        assertThat(ent.getResourceInfo().getBaseUrl()).isEqualTo("https://org.knime.core.ui.view/");

        // base url is _not_ expected to be set if run within 'executor' unless
        // the NodeViewEnt is used for image generation
        runOnExecutor(() -> {
            var ent2 = NodeViewEnt.create(nnc, null);
            assertThat(ent2.getResourceInfo().getBaseUrl()).isNull();

            var ent3 = NodeViewEnt.createForImageGeneration(nnc, null, ImageFormat.PNG, "blub");
            assertThat(ent3.getResourceInfo().getBaseUrl()).isEqualTo("https://org.knime.core.ui.view/");
        });

    }

    @Test
    void testNodeViewEntForReporting() {
        Function<NodeViewNodeModel, NodeView> nodeViewCreator = m -> new TestNodeView();
        var nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(nodeViewCreator));
        m_wfm.executeAllAndWaitUntilDone();

        // isUsedForReportingGeneration=true but the node view doesn't support it
        var ent = NodeViewEnt.createForReportGeneration(nnc, null, ImageFormat.PNG);
        var renderingConfig = ent.getRenderingConfig();
        assertThat(renderingConfig.getType()).isEqualTo(RenderingConfigType.REPORT);
        assertThat(((ReportRenderingConfigEnt)renderingConfig).canBeUsedInReport()).isFalse();
        assertThat(((ReportRenderingConfigEnt)renderingConfig).imageFormat()).isEqualTo(ImageFormat.PNG);

        nodeViewCreator = m -> new TestNodeView() {
            @Override
            public boolean canBeUsedInReport() {
                return true;
            }
        };
        nnc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeViewNodeFactory(nodeViewCreator));
        m_wfm.executeAllAndWaitUntilDone();

        // isUsedForReportingGeneration=true and the node view supports it
        ent = NodeViewEnt.createForReportGeneration(nnc, null, ImageFormat.PNG);
        renderingConfig = ent.getRenderingConfig();
        assertThat(renderingConfig.getType()).isEqualTo(RenderingConfigType.REPORT);
        assertThat(((ReportRenderingConfigEnt)ent.getRenderingConfig()).canBeUsedInReport()).isTrue();
    }

    private static void initViewSettingsAndExecute(final NativeNodeContainer nnc) throws InvalidSettingsException {
        var nodeSettings = new NodeSettings("node_settings");
        nodeSettings.addNodeSettings("model");
        nodeSettings.addNodeSettings("internal_node_subsettings");

        // some dummy view settings
        var viewSettings = nodeSettings.addNodeSettings("view");
        viewSettings.addString("view setting key", "view setting value");
        viewSettings.addString("view setting key 2", "view setting value 2");
        viewSettings.addString("exposed view setting key", "exposed view settings value");

        var parent = nnc.getParent();
        parent.loadNodeSettings(nnc.getID(), nodeSettings);
        parent.executeAllAndWaitUntilDone();
    }

    private static void overwriteViewSettingWithFlowVariable(final NativeNodeContainer nnc)
        throws InvalidSettingsException {

        var parent = nnc.getParent();
        var nodeSettings = new NodeSettings("node_settings");
        parent.saveNodeSettings(nnc.getID(), nodeSettings);
        var viewVariables = nodeSettings.addNodeSettings("view_variables");
        viewVariables.addString("version", "V_2019_09_13");
        var variableTree = viewVariables.addNodeSettings("tree");

        var variableTreeNode = variableTree.addNodeSettings("view setting key");
        variableTreeNode.addString("used_variable", "knime.workspace");
        variableTreeNode.addString("exposed_variable", null);

        var exposedVariableTreeNode = variableTree.addNodeSettings("exposed view setting key");
        exposedVariableTreeNode.addString("used_variable", null);
        exposedVariableTreeNode.addString("exposed_variable", "exposed_flow_variable");

        parent.loadNodeSettings(nnc.getID(), nodeSettings);
        parent.executeAllAndWaitUntilDone();

    }

    private static void checkViewSettings(final NodeViewEnt ent, final String expectedSettingValue)
        throws IOException, InvalidSettingsException {
        var settingsWithOverwrittenFlowVariable = new NodeSettings("");
        var mapper = new ObjectMapper();
        var result = mapper.readTree(ent.getInitialData()).get("result").toString();
        JSONConfig.readJSON(settingsWithOverwrittenFlowVariable, new StringReader(result));
        assertThat(settingsWithOverwrittenFlowVariable.getString("view setting key")).isEqualTo(expectedSettingValue);
    }

    private static void checkOutgoingFlowVariable(final NativeNodeContainer nnc, final String key, final String value) {
        var outgoingFlowVariables = nnc.getOutgoingFlowObjectStack().getAllAvailableFlowVariables();
        assertThat(outgoingFlowVariables).containsKey(key);
        assertThat(outgoingFlowVariables.get(key).getValueAsString()).isEqualTo(value);
    }

    /**
     * Tests that a {@link NodeViewEnt} is created for {@link NodeTableView} instances.
     *
     * @throws Exception
     */
    @Test
    void testNodeViewEntWithTableInputSpec() throws Exception {
        var pair = createExecutedNodeTableView();
        var wfm = pair.getFirst();
        var nnc = pair.getSecond();
        var nodeViewEnt = NodeViewEnt.create(nnc);

        assertThat(nodeViewEnt.getColorModels()).isEmpty();
        assertThat(nodeViewEnt.getColumnNamesColorModel()).isNull();

        WorkflowManagerUtil.disposeWorkflow(wfm);
    }

    /**
     * Tests that a {@link NodeViewEnt} is created for {@link NodeTableView} instances with the input table taken from a
     * port which is not the first one.
     *
     * @throws Exception
     */
    @Test
    void testNodeViewEntWithTableInputSpecOnOtherPort() throws Exception {
        var inputTablePortIndex = 5;
        var pair = createExecutedNodeTableView(inputTablePortIndex);
        var wfm = pair.getFirst();
        var nnc = pair.getSecond();
        var nodeViewEnt = NodeViewEnt.create(nnc);

        assertThat(nodeViewEnt.getColorModels()).isEmpty();
        assertThat(nodeViewEnt.getColumnNamesColorModel()).isNull();

        WorkflowManagerUtil.disposeWorkflow(wfm);
    }

    private static Pair<WorkflowManager, NativeNodeContainer> createExecutedNodeTableView() throws Exception {
        Function<NodeViewNodeModel, NodeView> nodeViewCreator = m -> NodeViewTestUtil
            .createTableView(Page.create().fromString(() -> "blub").relativePath("index.html"), null, null, null, null);

        return createExecutedNodeTableView(nodeViewCreator, 0);
    }

    private static Pair<WorkflowManager, NativeNodeContainer> createExecutedNodeTableView(final int inPortIdx)
        throws Exception {

        Function<NodeViewNodeModel, NodeView> nodeViewCreator = m -> NodeViewTestUtil
            .createTableView(Page.create().fromString(() -> "blub").relativePath("index.html"), null, null, null, null, inPortIdx);

        return createExecutedNodeTableView(nodeViewCreator, inPortIdx);

    }

    private static Pair<WorkflowManager, NativeNodeContainer> createExecutedNodeTableView(
        final Function<NodeViewNodeModel, NodeView> nodeViewCreator, final int inPortIdx) throws Exception {
        final var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        NativeNodeContainer nnc =
            WorkflowManagerUtil.createAndAddNode(wfm, new NodeViewNodeFactory(inPortIdx + 1, 0, nodeViewCreator));
        // We need the other connections as well for the workflow to be executable
        for (var i = 0; i <= inPortIdx; i++) {
            SourceNodeTestFactory.connectSourceNodeToInputPort(wfm, nnc, i);
        }
        wfm.executeAllAndWaitUntilDone();
        return new Pair<>(wfm, nnc);
    }

    private class TestNodeView implements NodeView {

        private NodeSettingsRO m_settings;

        @Override
        public Optional<InitialDataService<String>> createInitialDataService() {
            return Optional.of(InitialDataService
                .builder(() -> JSONConfig.toJSONString(m_settings, WriterConfig.DEFAULT)).onDeactivate(() -> {
                }).build());
        }

        @Override
        public Optional<RpcDataService> createRpcDataService() {
            return Optional.empty();
        }

        @Override
        public Optional<ApplyDataService<?>> createApplyDataService() {
            return Optional.empty();
        }

        @Override
        public Page getPage() {
            return Page.create().fromString(() -> "blub").relativePath("index.html");
        }

        @Override
        public void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
            //
        }

        @Override
        public void loadValidatedSettingsFrom(final NodeSettingsRO settings) {
            m_settings = settings;
        }

    }

    private static Color green = new Color(0, 255, 0);

    private static String greenHex = "#00FF00";

    private static Color blue = new Color(0, 0, 255);

    private static String blueHex = "#0000FF";

    /**
     * Tests that if the NodeView provides color models, {@link ColorModelEnt} are created.
     *
     * @throws IOException
     * @throws InvalidSettingsException
     */
    @Test
    void testNodeViewEntWithColorModels() throws IOException, InvalidSettingsException {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();

        var minValue = 0d;
        var maxValue = 1d;
        var minColor = green;
        var maxColor = blue;
        var numericColorModel = new ColorModelRange(minValue, minColor, maxValue, maxColor);

        var cell1 = "Cluster_1";
        var cell2 = "Cluster_2";
        Map<DataCell, ColorAttr> nominalColorModel = new HashMap<>();
        nominalColorModel.put(new StringCell(cell1), ColorAttr.getInstance(green));
        nominalColorModel.put(new StringCell(cell2), ColorAttr.getInstance(blue));

        var columnName1 = "ColumnName1";
        var columnName2 = "ColumnName2";
        Map<DataCell, ColorAttr> columnNamesColorMap = new HashMap<>();
        columnNamesColorMap.put(new StringCell(columnName1), ColorAttr.getInstance(blue));
        columnNamesColorMap.put(new StringCell(columnName2), ColorAttr.getInstance(green));
        var columnNamesColorModel = new ColorModelNominal(columnNamesColorMap, new ColorAttr[0]);

        var numericColorColumnName = "numericColumn";
        var nominalColorColumnName = "nominalColumn";
        final Map<String, ColorModel> colorModels = new HashMap<>();
        colorModels.put(numericColorColumnName, numericColorModel);
        colorModels.put(nominalColorColumnName, new ColorModelNominal(nominalColorModel, new ColorAttr[0]));

        var ent = createNodeViewEntWithColorModels(wfm, colorModels, columnNamesColorModel);
        var colorModelsEnt = ent.getColorModels();
        assertThat(String.valueOf(colorModelsEnt.get(numericColorColumnName).getType())).isEqualTo("NUMERIC");
        assertThat(String.valueOf(colorModelsEnt.get(nominalColorColumnName).getType())).isEqualTo("NOMINAL");

        var numericModel = (NumericColorModelEnt)(colorModelsEnt.get(numericColorColumnName).getModel());
        assertThat(numericModel.getMinValue()).isEqualTo(minValue);
        assertThat(numericModel.getMaxValue()).isEqualTo(maxValue);
        assertThat(numericModel.getMinColor()).isEqualTo(greenHex);
        assertThat(numericModel.getMaxColor()).isEqualTo(blueHex);
        @SuppressWarnings("unchecked")
        var nominalModel = (Map<String, String>)(colorModelsEnt.get(nominalColorColumnName).getModel());
        assertThat(nominalModel).containsEntry(cell1, greenHex).containsEntry(cell2, blueHex);

        var columnNamesColorModelEnt = ent.getColumnNamesColorModel();
        assertThat(String.valueOf(columnNamesColorModelEnt.getType())).isEqualTo("NOMINAL");
        @SuppressWarnings("unchecked")
        var columnNamesModel = (Map<String, String>)(columnNamesColorModelEnt.getModel());
        assertThat(columnNamesModel).containsEntry(columnName1, blueHex).containsEntry(columnName2, greenHex);

        WorkflowManagerUtil.disposeWorkflow(wfm);
    }

    private static NodeViewEnt createNodeViewEntWithColorModels(final WorkflowManager wfm,
        final Map<String, ColorModel> colorModels, final ColorModel columnNamesColorModel)
        throws InvalidSettingsException {

        final DataColumnSpec[] dataColumnSpecs = colorModels.entrySet().stream().map(e -> {
            final var creator = new DataColumnSpecCreator(e.getKey(), StringCell.TYPE);
            creator.setColorHandler(new ColorHandler(e.getValue()));
            return creator.createSpec();
        }).toArray(DataColumnSpec[]::new);

        final var sc = new DataTableSpecCreator();
        final var columnNamesColorHandler = new ColorHandler(columnNamesColorModel);
        sc.setColumnNamesColorHandler(columnNamesColorHandler);
        sc.addColumns(dataColumnSpecs);
        final var spec = sc.createSpec();

        return createNodeViewEntWithInputSpec(wfm, spec);
    }

    private static NodeViewEnt createNodeViewEntWithInputSpec(final WorkflowManager wfm, final DataTableSpec spec) {
        Function<NodeViewNodeModel, NodeView> nodeViewCreator = m -> NodeViewTestUtil
            .createTableView(Page.create().fromString(() -> "blub").relativePath("index.html"), null, null, null, null);
        var nnc = WorkflowManagerUtil.createAndAddNode(wfm, new NodeViewNodeFactory(1, 0, nodeViewCreator));
        var source = WorkflowManagerUtil.createAndAddNode(wfm, new TestNodeFactory(spec));
        wfm.addConnection(source.getID(), 1, nnc.getID(), 1);
        wfm.executeAllAndWaitUntilDone();
        return new NodeViewEnt(nnc, () -> Collections.emptyList(), NodeViewManager.getInstance(), "",
            new DefaultRenderingConfigEnt(), true);
    }

    /**
     * Simulates to run stuff as if it was run on the executor (which usually means to run the AP headless).
     *
     * @param r
     */
    private static void runOnExecutor(final Runnable r) {
        System.setProperty(JAVA_AWT_HEADLESS, "true");
        try {
            r.run();
        } finally {
            System.clearProperty(JAVA_AWT_HEADLESS);
        }
    }

    private static class TestNodeFactory extends NodeFactory<NodeModel> {

        private final DataTableSpec m_outputSpec;

        public TestNodeFactory(final DataTableSpec outputSpec) {
            m_outputSpec = outputSpec;
        }

        @Override
        public NodeModel createNodeModel() {
            return new NodeModel(0, 1) {

                @Override
                protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
                    return new DataTableSpec[]{m_outputSpec};
                }

                @Override
                protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec)
                    throws Exception {
                    var con = exec.createDataContainer(m_outputSpec);
                    con.close();
                    return new BufferedDataTable[]{con.getTable()};
                }

                @Override
                protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
                    //
                }

                @Override
                protected void saveSettingsTo(final NodeSettingsWO settings) {
                    //
                }

                @Override
                protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec)
                    throws IOException, CanceledExecutionException {
                    //
                }

                @Override
                protected void reset() {
                    //
                }

                @Override
                protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
                    throws InvalidSettingsException {
                    //
                }

                @Override
                protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec)
                    throws IOException, CanceledExecutionException {
                    //
                }
            };
        }

        @Override
        protected int getNrNodeViews() {
            return 0;
        }

        @Override
        public org.knime.core.node.NodeView<NodeModel> createNodeView(final int viewIndex, final NodeModel nodeModel) {
            return null;
        }

        @Override
        protected boolean hasDialog() {
            return false;
        }

        @Override
        protected NodeDialogPane createNodeDialogPane() {
            return null;
        }

    }

}
