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
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.Test;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.statistics.UnivariateStatistics;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.rpc.json.impl.ObjectMapperUtil;
import org.knime.core.webui.node.port.PortContext;
import org.knime.core.webui.node.port.PortView;

/**
 * Tests {@link StatisticsPortViewFactory}, i.e. the integration of the statistics port view.
 */
@SuppressWarnings("restriction")
public class StatisticsPortViewFactoryTest {

    /**
     * Asserts that the correct page is returned by the {@link PortView} created by the
     * {@link StatisticsPortViewFactory}.
     *
     * @throws IOException
     */
    @Test
    public void testGetPage() throws IOException {
        var bdt = TestingUtilities.createTable(2);
        var port = TestingUtilities.createNodeOutPort(bdt);
        PortContext.pushContext(port.get());
        try {
            var portView = new StatisticsPortViewFactory().createPortView(bdt);
            var page = portView.getPage();
            assertThat(page.getContentType().toString(), is("VUE_COMPONENT_LIB"));
            var pageId = page.getPageIdForReusablePage().orElse(null);
            assertThat(pageId, is("tableview"));
        } finally {
            PortContext.removeLastContext();
            port.dispose();
        }
    }

    /**
     * Checks the {@link InitialDataService} of the {@link PortView} created by the {@link StatisticsPortViewFactory}.
     *
     * @throws IOException
     */
    @Test
    public void testInitialData() throws IOException {
        var bdt = TestingUtilities.createTable(2);
        var port = TestingUtilities.createNodeOutPort(bdt);
        PortContext.pushContext(port.get());
        try {
            var initialData =
                new StatisticsPortViewFactory().createPortView(bdt).createInitialDataService().get().getInitialData();
            assertThat(initialData, containsString("{\"result\":{"));
            assertThat(initialData, containsString("\"table\":{"));

            var mapper = ObjectMapperUtil.getInstance().getObjectMapper();

            // check set of displayed columns
            var nStats = UnivariateStatistics.getDefaultStatistics().size();
            var displayedColumns = mapper.readTree(initialData).get("result").get("table").get("displayedColumns");
            assertThat(displayedColumns.size(), is(nStats));
            IntStream.range(0, nStats).forEach(i -> {
                var actual = displayedColumns.get(i).asText();
                var expected = UnivariateStatistics.getDefaultStatistics().get(i).getName();
                assertThat(actual, is(expected));
            });

            assertThat(initialData, containsString("\"settings\":{"));
            var settings = mapper.readTree(initialData).get("result").get("settings");
            TestingUtilities.assertViewSettings( //
                settings, //
                StatisticsPortViewFactory.getSettingsForDataTable(new DataTableSpec(), bdt.getSpec().getNumColumns()) //
            );
        } finally {
            PortContext.removeLastContext();
            port.dispose();
        }
    }

}
