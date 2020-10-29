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
 *   Oct 27, 2020 (hornm): created
 */
package org.knime.gateway.impl.jsonrpc.table;

import static org.knime.gateway.testing.helper.rpc.TableServiceTestHelper.createTable;
import static org.knime.gateway.testing.helper.rpc.TableServiceTestHelper.mockNodeOutPort;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.gateway.impl.jsonrpc.table.JsonRpcTableServiceTest.TestJsonRpcTableServerFactory;
import org.knime.gateway.impl.jsonrpc.table.JsonRpcTableServiceTest.TestRpcTransport;
import org.knime.gateway.impl.rpc.table.Table;
import org.knime.gateway.impl.rpc.table.TableCell;
import org.knime.gateway.impl.rpc.table.TableService;

/**
 * Tests the correct serialization of a table into json.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JsonRpcTableSerializationTest {

    private static final String SNAPSHOTS_DIR =
        "src/__snapshots__/" + JsonRpcTableSerializationTest.class.getSimpleName();

    /**
     * Snapshot tests for the json-serialization of a {@link Table}.
     */
    @Test
    public void testTableToJsonSerialization() {
        BufferedDataTable table = createTable(5);
        String jsonRpcResponse = getJsonRpcTableResponse(table);
        matchSnapshot("table_response", jsonRpcResponse);
    }

    /**
     * Snapshot test for json-serialization of a table with truncated values.
     */
    @Test
    public void testTableWithTruncatedValues() {
        DataTableSpec spec = new DataTableSpec(new String[]{"col"}, new DataType[]{StringCell.TYPE});
        String value = "very_long_string_" + StringUtils.repeat("*", TableCell.MAX_STRING_LENGTH) + "_string_end";
        BufferedDataTable table = createTable(spec, new DefaultRow("rowkey", value));
        matchSnapshot("table_response_truncated", getJsonRpcTableResponse(table));
    }

    private static String getJsonRpcTableResponse(final BufferedDataTable bdt) {
        TestRpcTransport transport = new TestRpcTransport();
        TestJsonRpcTableServerFactory factory = new TestJsonRpcTableServerFactory(transport);
        factory.createRpcServer(mockNodeOutPort(bdt));
        TableService tableService = factory.getRpcClient().getService(TableService.class);
        tableService.getTable(0, (int)bdt.size());
        return transport.getLastResponse();
    }

    private static void matchSnapshot(final String snapshotName, final String s) {
        try {
            matchSnapshotFromFile(snapshotName, s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO fuse with ResultChecker
    private static void matchSnapshotFromFile(final String snapshotName, final String s) throws IOException {
        Path snapFile = Paths.get(SNAPSHOTS_DIR, snapshotName + ".snap");
        if (Files.exists(snapFile)) {
            // load expected snapshot and compare
            String expected = new String(Files.readAllBytes(snapFile));
            Path debugFile = Paths.get(SNAPSHOTS_DIR, snapshotName + ".snap.debug");
            if (!s.equals(expected)) {
                // write debug file if snapshot doesn't match
                Files.write(debugFile, s.getBytes(StandardCharsets.UTF_8));
                Assert.fail(String.format("Snapshot '%s' doesn't match. Got: <<%s>>, but expected is <<%s>>",
                    snapshotName, s, expected));
            } else if (Files.exists(debugFile)) {
                // if snapshot matches, delete debug file (might not exist)
                Files.delete(debugFile);
            }
        } else {
            // just write the snapshot
            Files.createDirectories(Paths.get(SNAPSHOTS_DIR));
            Files.write(snapFile, s.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        }
    }

}
