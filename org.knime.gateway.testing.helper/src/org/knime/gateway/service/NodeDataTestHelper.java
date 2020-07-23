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
package org.knime.gateway.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Assert;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.data.DirectAccessTable.UnknownRowCountException;
import org.knime.core.data.filestore.FileStoreCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.tableview.CellLoadingError;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LockFailedException;
import org.knime.core.util.Pair;
import org.knime.gateway.api.entity.DataCellEnt;
import org.knime.gateway.api.entity.DataRowEnt;
import org.knime.gateway.api.entity.DataTableEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.service.NodeService;
import org.knime.gateway.api.util.EntityTranslateUtil;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestUtil;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests to make sure that data at a node's output port is still exactly the same data when passed through the gateway
 * (i.e. data correctness).
 *
 * These tests are really about the data. The
 * {@link NodeService#getOutputDataTable(java.util.UUID, String, Integer, Long, Integer)}-endpoint is further tested in
 * {@link ViewWorkflowTestHelper}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class NodeDataTestHelper extends AbstractGatewayServiceTestHelper {

	private WorkflowManager m_workflowManager;

    /**
     * See
     * {@link AbstractGatewayServiceTestHelper#AbstractGatewayServiceTestHelper(String, WorkflowLoader, ServiceProvider, ResultChecker)}.
     *
     * @param workflowLoader
     * @param serviceProvider
     * @param entityResultChecker
     * @throws LockFailedException
     * @throws UnsupportedWorkflowVersionException
     * @throws CanceledExecutionException
     * @throws InvalidSettingsException
     * @throws IOException
     */
    public NodeDataTestHelper(final ServiceProvider serviceProvider, final ResultChecker entityResultChecker,
        final WorkflowLoader workflowLoader) throws IOException, InvalidSettingsException, CanceledExecutionException,
        UnsupportedWorkflowVersionException, LockFailedException {
        super("nodedata", serviceProvider, entityResultChecker, workflowLoader);

        m_workflowManager = TestUtil.loadWorkflow(TestWorkflow.DATA.getUrlFolder());
    }

    /**
     * Retrieves node data from the server and compares it to the data of the locally loaded workflow.
     *
     * @throws Exception if an error occurs
     */
    public void testCompareNodeDataForBufferedDataTable() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.DATA);

        //compare first port of node #3
        DataTableEnt tableEnt = ns().getOutputDataTable(wfId, new NodeIDEnt(3), 1, 0l, 100);
        compare(tableEnt, new NodeIDEnt(3), 1, 0, 100);

        //compare the first port of node #5 (transposed table)
        tableEnt = ns().getOutputDataTable(wfId, new NodeIDEnt(5), 1, 0l, 100);
        compare(tableEnt, new NodeIDEnt(5), 1, 0, 100);

        TestUtil.cancelAndCloseLoadedWorkflow(m_workflowManager);
    }

    /**
     * Retrieves table data provided by {@link DirectAccessTable}-port object and compares.
     *
     * @throws Exception if an error occurs
     */
    public void testCompareNodeDataForDirectAccessTable() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.DATA);

        //compare first port of node #8
        DataTableEnt tableEnt = ns().getOutputDataTable(wfId, new NodeIDEnt(8), 1, 23l, 100);
        compare(tableEnt, new NodeIDEnt(8), 1, 23, 100);

        TestUtil.cancelAndCloseLoadedWorkflow(m_workflowManager);
    }

    /**
     * Tests the behavior when a {@link DirectAccessTable} throws a {@link UnknownRowCountException}.
     *
     * @throws Exception
     */
    public void testDirectAccessTableUnknownRowCount() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.DATA);

        //get first port of node #9
        DataTableEnt tableEnt = ns().getOutputDataTable(wfId, new NodeIDEnt(9), 1, 23l, 100);
        compare(tableEnt, new NodeIDEnt(9), 1, 23l, 100);

        TestUtil.cancelAndCloseLoadedWorkflow(m_workflowManager);
    }

    /**
     * Test the behaviour when rows are tried to accessed that are out of the table range.
     *
     * @throws Exception
     */
    public void testDirectAccessTableExceedingTotalRowCount() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.DATA);
        Assert.assertThat("Empty list of rows expected",
            ns().getOutputDataTable(wfId, new NodeIDEnt(8), 1, 1001l, 10).getRows().isEmpty(), is(true));

        TestUtil.cancelAndCloseLoadedWorkflow(m_workflowManager);
    }

    /**
     * Compares the actual table ({@link BufferedDataTable} or {@link DirectAccessTable}) to a {@link DataTableEnt}
     * cell-wise by deserializing the cells from the respective entities.
     *
     * @param tableEnt the entity table that should resemble the actual table
     * @param nodeID the id of the node to compare the table to
     * @param the port index to compare to
     * @param startIdx the index to start comparing from
     * @param count the number of rows to compare
     * @throws UnknownRowCountException
     * @throws CanceledExecutionException
     * @throws IndexOutOfBoundsException
     */
    private void compare(final DataTableEnt tableEnt, final NodeIDEnt nodeID, final int portIdx, final long startIdx,
        final int count) throws IndexOutOfBoundsException, CanceledExecutionException, UnknownRowCountException {
        Pair<List<DataRow>, Long> dataRowsAndTotalCount = getDataRowsAndTotalCount(nodeID, portIdx, startIdx, count);

        assertThat("Total row count doesn't match", tableEnt.getNumTotalRows(), is(dataRowsAndTotalCount.getSecond()));

        Iterator<DataRow> rowIt = dataRowsAndTotalCount.getFirst().iterator();
        for (int i = 0; i < tableEnt.getRows().size(); i++) {
            DataRow row = rowIt.next();
            DataRowEnt rowEnt = tableEnt.getRows().get(i);
            for (int j = 0; j < row.getNumCells(); j++) {
                DataCell cell = row.getCell(j);
                DataCellEnt cellEnt = rowEnt.getColumns().get(j);

                //deserialize cell from entity
                DataCell remoteCell = EntityTranslateUtil.translateDataCellEnt(cellEnt, cell.getType());
                if (cell instanceof FileStoreCell) {
                    //FileStoreCells are not supported, yet
                    //-> an error cell is expected
                    assertTrue("A loading error is expected here", remoteCell instanceof CellLoadingError);
                    assertThat(((CellLoadingError)remoteCell).getErrorMessage(),
                        is("FileStoreCells are not supported, yet"));
                } else {
                    //compare
                    assertEquals("table cells are not equal", cell, remoteCell);
                }
            }
        }
    }

    /**
     * Gets the data rows and total row count of node with given id at port with given index.
     *
     * @param nodeID
     * @param portIdx
     * @return
     * @throws CanceledExecutionException
     * @throws IndexOutOfBoundsException
     * @throws UnknownRowCountException
     */
    private Pair<List<DataRow>, Long> getDataRowsAndTotalCount(final NodeIDEnt nodeID, final int portIdx, final long from,
        final int count) throws IndexOutOfBoundsException, CanceledExecutionException {
        PortObject portObject =
            m_workflowManager.getNodeContainer(nodeID.toNodeID(m_workflowManager.getID())).getOutPort(portIdx)
                .getPortObject();
        if (portObject instanceof BufferedDataTable) {
            BufferedDataTable bdt = (BufferedDataTable)portObject;
            return Pair.create(
                StreamSupport.stream((bdt).spliterator(), false).skip(from).limit(count).collect(Collectors.toList()),
                bdt.size());
        } else if (portObject instanceof DirectAccessTable) {
            DirectAccessTable dat = (DirectAccessTable)portObject;
            long rowCount = -1;
            try {
                rowCount = dat.getRowCount();
            } catch (UnknownRowCountException ex) {
                //
            }
            return Pair.create(dat.getRows(from, count, null), rowCount);
        } else {
            throw new RuntimeException("Not a table");
        }
    }
}
