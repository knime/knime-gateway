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
 *   Oct 26, 2022 (kai): created
 */
package org.knime.gateway.testing.helper.webui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.knime.core.node.NodeInfo;
import org.knime.core.node.NodeTriple;
import org.knime.core.ui.workflowcoach.data.NodeTripleProvider;
import org.knime.core.ui.workflowcoach.data.NodeTripleProviderFactory;

/**
 * A test node triple provider used in {@link NodeRecommendationsTestHelper}. Please note: This must be registered as an
 * extension point {@code org.knime.core.ui.nodetriples} of this fragment using the {@code fragment.xml}.
 *
 * @author Kai Franze, KNIME GmbH
 */
public class TestNodeTripleProviderFactory implements NodeTripleProviderFactory {

    @Override
    public List<NodeTripleProvider> createProviders() {
        return Collections.singletonList(new TestNodeTripleProvider());
    }

    @Override
    public String getPreferencePageID() {
        return "";
    }

    static final class TestNodeTripleProvider implements NodeTripleProvider {

        @Override
        public String getName() {
            return "Test node triple provider";
        }

        @Override
        public String getDescription() {
            return "Test node triple provider description";
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public Stream<NodeTriple> getNodeTriples() throws IOException {
            return Stream.concat(getSuccessorRecommendations(), getSourceRecommendations());
        }

        /**
         * Provides 13 valid successor recommendations and one not present in the node repository
         * @return
         */
        private static Stream<NodeTriple> getSuccessorRecommendations() {
            var node = new NodeInfo(//
                "org.knime.base.node.util.sampledata.SampleDataNodeFactory", //
                "Data Generator");
            var successors = Stream.of(//
                new NodeInfo(// 1
                    "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory", //
                    "Row Filter"),
                new NodeInfo(// 2
                    "org.knime.base.node.preproc.filter.column.DataColumnSpecFilterNodeFactory", //
                    "Column Filter"),
                new NodeInfo(// 3
                    "org.knime.ext.poi3.node.io.filehandling.excel.writer.ExcelTableWriterNodeFactory", //
                    "Excel Writer"),
                new NodeInfo(// 4
                    "org.knime.base.node.io.filehandling.csv.writer.CSVWriter2NodeFactory", //
                    "CSV Writer"),
                new NodeInfo(// 5
                    "org.knime.base.views.node.scatterplot.ScatterPlotNodeFactory", //
                    "Scatter Plot"),
                new NodeInfo(// 6
                    "org.knime.js.base.node.viz.plotter.scatterSelectionAppender.ScatterPlotNodeFactory", //
                    "Scatter Plot"),
                new NodeInfo(// 7
                    "org.knime.base.views.node.barchart.BarChartNodeFactory", //
                    "Bar Chart"),
                new NodeInfo(// 8
                    "org.knime.dynamic.js.v30.DynamicJSNodeFactory", //
                    "Bar Chart"),
                new NodeInfo(// 9
                    "org.knime.js.base.node.viz.plotter.line.LinePlotNodeFactory", //
                    "Line Plot"),
                new NodeInfo(// 10
                    "org.knime.ext.jep.JEPNodeFactory", //
                    "Math Formula"),
                new NodeInfo(// 11
                    "org.knime.js.base.node.viz.pagedTable.PagedTableViewNodeFactory", //
                    "Table View"),
                new NodeInfo(// 12
                    "org.knime.base.node.preproc.groupby.GroupByNodeFactory", //
                    "GroupBy"),
                new NodeInfo(// 13, extra successor since 12 recommendations are the default
                    "org.knime.base.node.preproc.stringmanipulation.StringManipulationNodeFactory", //
                    "String Manipulation"),
                new NodeInfo(// 14, compatible but non-interactive input ports, must be filtered out
                    "org.knime.gateway.testing.helper.webui.node.DummyNodeDynamicPortsInteractiveFactory", //
                    "Dummy Node"),
                new NodeInfo(// 15, not present in node repository, must be filtered out
                    "non.existing.factory", //
                    "Non-Existing Node"));
            return successors.map(successor -> new NodeTriple(null, node, successor));
        }


        /**
         * Provides 13 valid source recommendations and one not present in the node repository
         * @return
         */
        private static Stream<NodeTriple> getSourceRecommendations() {
            var successor = new NodeInfo(//
                "org.knime.base.node.preproc.filter.row.RowFilterNodeFactory", //
                "Row Filter");
            var nodes = Stream.of(//
                new NodeInfo(// 1
                    "org.knime.base.node.util.sampledata.SampleDataNodeFactory", //
                    "Data Generator"),
                new NodeInfo(// 2
                    "org.knime.ext.poi3.node.io.filehandling.excel.reader.ExcelTableReaderNodeFactory", //
                    "Excel Reader"),
                new NodeInfo(// 3
                    "org.knime.base.node.io.tablecreator.TableCreator2NodeFactory", //
                    "Table Creator"),
                new NodeInfo(// 4
                    "org.knime.base.node.io.filehandling.csv.reader.CSVTableReaderNodeFactory", //
                    "CSV Reader"),
                new NodeInfo(// 5
                    "org.knime.base.node.io.filehandling.table.reader.KnimeTableReaderNodeFactory", //
                    "Table Reader"),
                new NodeInfo(// 6
                    "org.knime.base.node.io.filehandling.csv.reader.FileReaderNodeFactory", //
                    "File Reader"),
                new NodeInfo(// 7
                    "org.knime.filehandling.core.fs.local.node.LocalConnectorNodeFactory", //
                    "Local File System Connector"),
                new NodeInfo(// 8
                    "org.knime.database.node.io.reader.DBReadNodeFactory", //
                    "DB Reader"),
                new NodeInfo(// 9
                    "org.knime.database.node.io.reader.query.DBQueryReaderNodeFactory", //
                    "DB Query Reader"),
                new NodeInfo(// 10
                    "org.knime.filehandling.utility.nodes.listpaths.ListFilesAndFoldersNodeFactory", //
                    "List Files/Folders"),
                new NodeInfo(// 11
                    "org.knime.time.node.create.createdatetime.CreateDateTimeNodeFactory", //
                    "Create Date&Time Range"),
                new NodeInfo(// 12
                    "org.knime.database.node.connector.generic.DBConnectorNodeFactory", //
                    "DB Connector"),
                new NodeInfo(// 13, extra successor since 12 recommendations are the default
                    "org.knime.base.node.io.variablecreator.VariableCreatorNodeFactory", //
                    "Variable Creator"),
                new NodeInfo(// 14, a non-source node
                    "org.knime.base.node.preproc.stringmanipulation.StringManipulationNodeFactory", //
                    "String Manipulation"),
                new NodeInfo(// 15, not present in node repository, must be filtered out
                    "non.existing.factory", //
                    "Non-Existing Node"));
            return nodes.map(node -> new NodeTriple(null, node, successor));
        }

        @Override
        public Optional<LocalDateTime> getLastUpdate() {
            return Optional.empty();
        }

    }

}
