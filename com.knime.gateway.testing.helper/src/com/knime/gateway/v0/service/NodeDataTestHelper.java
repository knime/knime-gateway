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
package com.knime.gateway.v0.service;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.UUID;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.RowIterator;
import org.knime.core.data.filestore.FileStoreCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PageableDataTable;
import org.knime.core.node.port.PageableDataTable.UnknownRowCountException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.tableview.CellLoadingError;
import org.knime.core.node.workflow.UnsupportedWorkflowVersionException;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LockFailedException;

import com.knime.gateway.testing.helper.ResultChecker;
import com.knime.gateway.testing.helper.ServiceProvider;
import com.knime.gateway.testing.helper.TestUtil;
import com.knime.gateway.testing.helper.WorkflowLoader;
import com.knime.gateway.util.EntityTranslateUtil;
import com.knime.gateway.util.EntityUtil;
import com.knime.gateway.v0.entity.DataCellEnt;
import com.knime.gateway.v0.entity.DataRowEnt;
import com.knime.gateway.v0.entity.DataTableEnt;

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

        m_workflowManager = TestUtil.loadWorkflow(TestWorkflow.WORKFLOW_DATA.getUrlFolder());
    }

    /**
     * Retrieves node data from the server and compares it to the data of the locally loaded workflow.
     *
     * @throws Exception if an error occurs
     */
    public void testCompareNodeDataForBufferedDataTable() throws Exception {
    	UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_DATA);

        //compare first port of node #3
        DataTableEnt tableEnt = ns().getOutputDataTable(wfId, "3", 1, 0l, 100);
        DataTable table = getDataTable("3", 1);
        compare(table, tableEnt, 0);

        //compare the first port of node #5 (transposed table)
        tableEnt = ns().getOutputDataTable(wfId, "5", 1, 0l, 100);
        table = getDataTable("5", 1);
        compare(table, tableEnt, 0);
    }

    /**
     * Retrieves table data provided by {@link PageableDataTable}-port object and compares.
     *
     * @throws Exception if an error occurs
     */
    public void testCompareNodeDataForPageableDataTable() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_DATA);

        //compare first port of node #6
        DataTableEnt tableEnt = ns().getOutputDataTable(wfId, "6", 1, 23l, 100);
        DataTable table = getDataTable("6", 1);
        compare(table, tableEnt, 23);

        TestUtil.cancelAndCloseLoadedWorkflow(m_workflowManager);
    }

    /**
     * Tests the behavior when a {@link PageableDataTable} throws a {@link UnknownRowCountException}.
     *
     * @throws Exception
     */
    public void testPageableTableUnknownRowCount() throws Exception {
        UUID wfId = loadWorkflow(TestWorkflow.WORKFLOW_DATA);

        try {
            //get first port of node #7
            ns().getOutputDataTable(wfId, "7", 1, 23l, 100);
            fail("Expected a IllegalStateException to be thrown");
        } catch (IllegalStateException e) {
            assertThat("Unexpected exception message", e.getMessage(),
                containsString("Total size of the table could not be determined"));
        }

        TestUtil.cancelAndCloseLoadedWorkflow(m_workflowManager);
    }

    /**
     * Compares a {@link DataTable} to a {@link DataTableEnt} cell-wise by deserializing the cells from the
     * respective entities.
     *
     * @param table the actual table
     * @param tableEnt the entity table that should resemble the actual table
     * @param startIdx the index to start comparing from
     */
    private static void compare(final DataTable table, final DataTableEnt tableEnt, final int startIdx) {
        assertThat("Total row count doesn't match", tableEnt.getNumTotalRows(), is(getTableSize(table)));

        RowIterator rowIt = table.iterator();
        for (int i = 0; i < startIdx; i++) {
            rowIt.next();
        }
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

    private static long getTableSize(final DataTable table) {
        if (table instanceof BufferedDataTable) {
            return ((BufferedDataTable)table).size();
        } else if (table instanceof PageableDataTable) {
            try {
                return ((PageableDataTable)table).calcTotalRowCount();
            } catch (UnknownRowCountException ex) {
                throw new RuntimeException("Unknown row count", ex);
            }
        } else {
            throw new RuntimeException("Unknown table instance");
        }
    }

    /**
     * Gets the data table of node with given id at port with given index.
     *
     * @param nodeID
     * @param portIdx
     * @return
     */
    private DataTable getDataTable(final String nodeID, final int portIdx) {
        PortObject portObject =
            m_workflowManager.getNodeContainer(EntityUtil.stringToNodeID(m_workflowManager.getID().toString(), nodeID))
                .getOutPort(portIdx).getPortObject();
        if (portObject instanceof BufferedDataTable) {
            return (BufferedDataTable)portObject;
        } else if (portObject instanceof PageableDataTable) {
            return (PageableDataTable)portObject;
        } else {
            throw new RuntimeException("Not a table");
        }
    }

}
