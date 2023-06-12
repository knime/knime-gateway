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
 */
package org.knime.gateway.impl.node.port;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.webui.data.rpc.json.impl.ObjectMapperUtil;
import org.knime.core.webui.node.port.PortContext;
import org.knime.core.webui.node.view.table.TableViewViewSettings;
import org.knime.testing.node.view.NodeViewNodeFactory;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests {@link TableSpecViewFactory}.
 */
@SuppressWarnings({"restriction"})
public class TableSpecViewFactoryTest {

    private static final DataColumnSpec[] COLSPECS = { //
        new DataColumnSpecCreator("int", IntCell.TYPE).createSpec(),
        new DataColumnSpecCreator("string", StringCell.TYPE).createSpec(),
        new DataColumnSpecCreator("long", LongCell.TYPE).createSpec(),
        new DataColumnSpecCreator("double", DoubleCell.TYPE).createSpec(),
        new DataColumnSpecCreator("boolean", BooleanCell.TYPE).createSpec(),
        new DataColumnSpecCreator("mixed-type", DataType.getCommonSuperType(StringCell.TYPE, DoubleCell.TYPE))
            .createSpec() //
    };

    static Disposable<NodeOutPort> createNodeOutPort(final BufferedDataTable bdt) throws IOException {
        var nc = createNodeWithPortView();
        var port = mock(NodeOutPort.class);
        when(port.getPortObject()).thenReturn(bdt);
        when(port.getConnectedNodeContainer()).thenReturn(nc);
        return new Disposable<NodeOutPort>() {
            @Override
            public NodeOutPort get() {
                return port;
            }

            @Override
            public void dispose() {
                WorkflowManagerUtil.disposeWorkflow(nc.getParent());
            }
        };
    }

    /*
     * returns the node with the port view
     */
    private static NativeNodeContainer createNodeWithPortView() throws IOException {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        return WorkflowManagerUtil.createAndAddNode(wfm, new NodeViewNodeFactory());
    }

    @Test
    public void testTableSpecViewPage() throws IOException {
        var bdt = TablePortViewFactoryTest.createTable(2);
        var port = createNodeOutPort(bdt);
        PortContext.pushContext(port.get());
        try {
            var portView = new TableSpecViewFactory().createPortView(new DataTableSpec(COLSPECS));
            var page = portView.getPage();
            assertThat(page.getContentType().toString(), is("VUE_COMPONENT_LIB"));
            var pageId = page.getPageIdForReusablePage().orElse(null);
            assertThat(pageId, is("tableview"));
        } finally {
            PortContext.removeLastContext();
            port.dispose();
        }
    }

    @Test
    public void testTableSpecInitialData() throws IOException {
        var bdt = TablePortViewFactoryTest.createTable(2);
        var port = createNodeOutPort(bdt);
        PortContext.pushContext(port.get());
        try {
            var portView = new TableSpecViewFactory().createPortView(new DataTableSpec(COLSPECS));
            var initialData = portView.createInitialDataService().get().getInitialData();
            var mapper = ObjectMapperUtil.getInstance().getObjectMapper();
            var jsonNode = mapper.readTree(initialData);
            TestingUtilities.assertViewSettings( //
                jsonNode.get("result").get("settings"), //
                TableViewViewSettings.getSpecViewSettings(new DataTableSpec()) //
            );
            var table = jsonNode.get("result").get("table");
            var rows = table.get("rows");
            var columns = mapper.treeToValue(table.get("displayedColumns"), String[].class);
            assertThat(rows.size(), is(0));
            assertThat(columns, is(new String[]{"int", "string", "long", "double", "boolean", "mixed-type"}));
        } finally {
            PortContext.removeLastContext();
            port.dispose();
        }

    }

    interface Disposable<T> {
        T get();

        void dispose();
    }
}
