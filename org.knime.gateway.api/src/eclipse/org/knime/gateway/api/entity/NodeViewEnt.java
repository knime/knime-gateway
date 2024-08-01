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
 *   Sep 28, 2021 (hornm): created
 */
package org.knime.gateway.api.entity;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.report.ReportUtil.ImageFormat;
import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.PageResourceManager.PageType;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.gateway.api.entity.RenderingConfigEnt.DefaultRenderingConfigEnt;
import org.knime.gateway.api.entity.RenderingConfigEnt.ImageRenderingConfigEnt;
import org.knime.gateway.api.entity.RenderingConfigEnt.ReportRenderingConfigEnt;

/**
 * Node view entity containing the info required by the UI (i.e. frontend) to be able display a node view.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NodeViewEnt extends UIExtensionEnt<NodeWrapper> {

    private final NodeInfoEnt m_info;

    private List<String> m_initialSelection;

    private RenderingConfigEnt m_renderingConfigEnt;

    private Map<String, ColorModelEnt> m_colorModelsEnt;

    private ColorModelEnt m_columnNamesColorModelEnt;

    /**
     * @param nnc the Native node container to create the node view entity for
     * @param initialSelection the initial selection (e.g. a list of row keys or something else), supplied lazily (will
     *            not be called, if the node is not executed)
     * @param renderingConfigEnt represents the context in which the node should be rendered
     * @return a new instance
     */
    public static NodeViewEnt create(final NativeNodeContainer nnc, final Supplier<List<String>> initialSelection,
        final RenderingConfigEnt renderingConfigEnt) {
        return create(nnc, initialSelection, renderingConfigEnt,
            !(renderingConfigEnt instanceof DefaultRenderingConfigEnt));
    }

    private static NodeViewEnt create(final NativeNodeContainer nnc, final Supplier<List<String>> initialSelection,
        final RenderingConfigEnt renderingConfigEnt, final boolean isUsedForImageOrReportGeneration) {
        final var state = nnc.getNodeContainerState();
        final var isAndCanBeUsedForReportGeneration =
            renderingConfigEnt instanceof ReportRenderingConfigEnt reportRenderingConfigEnt
                && reportRenderingConfigEnt.canBeUsedInReport();
        if (state.isExecuted() || ((isUsedForImageOrReportGeneration || isAndCanBeUsedForReportGeneration)
            && state.isExecutionInProgress() && !state.isWaitingToBeExecuted())) {
            try {
                NodeViewManager.getInstance().updateNodeViewSettings(nnc);
                return new NodeViewEnt(nnc, initialSelection, NodeViewManager.getInstance(), null, renderingConfigEnt,
                    isUsedForImageOrReportGeneration);
            } catch (InvalidSettingsException ex) {
                NodeLogger.getLogger(NodeViewEnt.class).error("Failed to update node view settings", ex);
                return new NodeViewEnt(nnc, null, null, ex.getMessage(), renderingConfigEnt,
                    isUsedForImageOrReportGeneration);
            }
        } else {
            return new NodeViewEnt(nnc, null, null, null, renderingConfigEnt, isUsedForImageOrReportGeneration);
        }
    }

    /**
     * @param nnc the Native node container to create the node view entity for
     * @param initialSelection the initial selection (e.g. a list of row keys or something else), supplied lazily (will
     *            not be called, if the node is not executed)
     * @return a new instance
     */
    public static NodeViewEnt create(final NativeNodeContainer nnc, final Supplier<List<String>> initialSelection) {
        return create(nnc, initialSelection, new DefaultRenderingConfigEnt());
    }

    /**
     * @param nnc the Native node container to create the node view entity for
     * @param initialSelection the initial selection (e.g. a list of row keys or something else), supplied lazily (will
     *            not be called, if the node is not executed)
     * @return a new instance
     */
    public static NodeViewEnt create(final NativeNodeContainer nnc, final Supplier<List<String>> initialSelection,
        final ImageFormat imageFormat) {
        boolean canBeUsedInReport = NodeViewManager.getInstance().canBeUsedInReport(nnc);
        return create(nnc, initialSelection, new ReportRenderingConfigEnt(imageFormat, canBeUsedInReport));
    }

    /**
     * @param nnc the Native node container to create the node view entity for
     * @param initialSelection the initial selection (e.g. a list of row keys or something else), supplied lazily (will
     *            not be called, if the node is not executed)
     * @param actionId if the view is to be used for image generation, it specifies a unique action-id used to
     *            communicate the image back to the java-side;
     * @return a new instance
     */
    public static NodeViewEnt create(final NativeNodeContainer nnc, final Supplier<List<String>> initialSelection,
        final ImageFormat imageFormat, final String actionId) {
        return create(nnc, initialSelection, new ImageRenderingConfigEnt(imageFormat, actionId));
    }

    /**
     * Creates a new instances without a initial selection and without the underlying node being registered with the
     * selection event source.
     *
     * @param nnc the node to create the node view entity for
     * @return a new instance
     */
    public static NodeViewEnt create(final NativeNodeContainer nnc) {
        return create(nnc, null);
    }

    /**
     * Package scoped for testing purposes only
     */
    NodeViewEnt(final NativeNodeContainer nnc, final Supplier<List<String>> initialSelection,
        final NodeViewManager nodeViewManager, final String customErrorMessage,
        final RenderingConfigEnt renderingConfigEnt, final boolean isUsedForImageOrReportGeneration) {
        super(NodeWrapper.of(nnc), nodeViewManager == null ? null : nodeViewManager.getPageResourceManager(),
            nodeViewManager == null ? null : nodeViewManager.getDataServiceManager(), PageType.VIEW,
            isRunAsDesktopApplication() || isUsedForImageOrReportGeneration);
        CheckUtils.checkArgument(NodeViewManager.hasNodeView(nnc), "The provided node doesn't have a node view");
        m_initialSelection = initialSelection == null ? null : initialSelection.get();
        m_info = new NodeInfoEnt(nnc, customErrorMessage);
        m_renderingConfigEnt = renderingConfigEnt;
        final var spec =
            nodeViewManager == null ? null : nodeViewManager.getInputDataTableSpecIfTableView(nnc).orElse(null);
        if (spec != null) {
            m_colorModelsEnt = getColorHandlerColumns(spec);
            m_columnNamesColorModelEnt = getColumnNamesColorHandler(spec);
        }
    }

    /**
     * @return the color model containing colors for the column names
     */
    private static ColorModelEnt getColumnNamesColorHandler(final DataTableSpec spec) {
        return spec.getColumnNamesColorHandler().map(h -> new ColorModelEnt(h.getColorModel())).orElse(null);
    }

    /**
     * @return a map from the name of a column to its attached color model
     */
    private static Map<String, ColorModelEnt> getColorHandlerColumns(final DataTableSpec spec) {
        return spec.stream().filter(colSpec -> colSpec.getColorHandler() != null).collect(
            Collectors.toMap(DataColumnSpec::getName, col -> new ColorModelEnt(col.getColorHandler().getColorModel())));
    }

    /**
     * @return additional info for the node providing the view
     */
    public NodeInfoEnt getNodeInfo() {
        return m_info;
    }

    /**
     * @return the initial selection (e.g. a list of row keys)
     */
    public List<String> getInitialSelection() {
        return m_initialSelection;
    }

    /**
     * @return the renderingConfig indicating whether the view represented by this view entity is used for the purpose
     *         of an interactive view, image generation or report generation via an image or report output port.
     */
    public RenderingConfigEnt getRenderingConfig() {
        return m_renderingConfigEnt;
    }

    /**
     * @return the representation to the color model to be used by the frontend to translate numeric or nominal values
     *         to hex colors. Can be null if no color model was provided.
     */
    public Map<String, ColorModelEnt> getColorModels() {
        return m_colorModelsEnt;
    }

    /**
     *
     * @return the color model to be used by the frontend to translate column names to hex colors. Can be null if no
     *         color model was provided.
     */
    public ColorModelEnt getColumnNamesColorModel() {
        return m_columnNamesColorModelEnt;
    }

}
