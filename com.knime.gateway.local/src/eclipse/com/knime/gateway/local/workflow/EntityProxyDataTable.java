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
package com.knime.gateway.local.workflow;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.container.ContainerTable;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTable.KnowsRowCountTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;

import com.knime.gateway.v0.entity.DataRowEnt;
import com.knime.gateway.v0.entity.DataTableEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.NodePortEnt;
import com.knime.gateway.v0.service.util.ServiceExceptions.InvalidRequestException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;

/**
 * Entity-proxy class that proxies {@link NodePortEnt} and implements {@link PortObject} and {@link KnowsRowCountTable}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityProxyDataTable extends AbstractEntityProxy<NodePortEnt> implements PortObject, KnowsRowCountTable {

    /**
     * The size of the chunks to be retrieved and cached.
     */
    private static final int CHUNK_SIZE = 50;

    private final DataTableSpec m_spec;

    private final NodeEnt m_nodeEnt;

    /* cached chunks */
    private final List<DataTableEnt> m_chunks;

    /**
     * Creates a new entity proxy.
     *
     * @param portEnt the port
     * @param nodeEnt the node
     * @param spec the spec of the data table that is proxied
     * @param access see {@link AbstractEntityProxy}
     */
    public EntityProxyDataTable(final NodePortEnt portEnt, final NodeEnt nodeEnt, final DataTableSpec spec,
        final EntityProxyAccess access) {
        super(portEnt, access);
        m_nodeEnt = nodeEnt;
        m_spec = spec;
        m_chunks = new ArrayList<DataTableEnt>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataTableSpec getDataTableSpec() {
        return m_spec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloseableRowIterator iterator() {
        return new CloseableRowIterator() {
            private long m_index = 0;

            @Override
            public DataRow next() {
                DataRow row = new DataRow() {

                    private DataRowEnt m_row = getRow(m_index);

                    @Override
                    public Iterator<DataCell> iterator() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public int getNumCells() {
                        return getChunk(0).getColumnNames().size();
                    }

                    @Override
                    public RowKey getKey() {
                        return new RowKey(m_row.getRowID());
                    }

                    @Override
                    public DataCell getCell(final int index) {
                        return new DataCell() {

                            private static final long serialVersionUID = 6064183116336622584L;

                            @Override
                            public String toString() {
                                return m_row.getColumns().get(index).getValueAsString();
                            }

                            @Override
                            public int hashCode() {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            protected boolean equalsDataCell(final DataCell dc) {
                                throw new UnsupportedOperationException();
                            }
                        };
                    }
                };
                m_index++;
                return row;
            }

            @Override
            public boolean hasNext() {
                return m_index < getChunk(0).getNumTotalRows();
            }

            @Override
            public void close() {
                //nothing to do here
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSummary() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortObjectSpec getSpec() {
        return m_spec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent[] getViews() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    @Override
    public int getRowCount() {
        return (int)size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long size() {
        return getChunk(0).getNumTotalRows();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveToFile(final File f, final NodeSettingsWO settings, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void ensureOpen() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedDataTable[] getReferenceTables() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putIntoTableRepository(final HashMap<Integer, ContainerTable> rep) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeFromTableRepository(final HashMap<Integer, ContainerTable> rep) {
        throw new UnsupportedOperationException();
    }

    private DataRowEnt getRow(final long index) {
        return getChunk(chunkIndex(index)).getRows().get(indexInChunk(index));
    }

    private static int chunkIndex(final long index) {
        return (int)index / CHUNK_SIZE;
    }

    private static int indexInChunk(final long index) {
        return (int)index % CHUNK_SIZE;
    }

    private DataTableEnt getChunk(final int chunkIndex) {
        if (chunkIndex >= m_chunks.size() || m_chunks.get(chunkIndex) == null) {
            try {
                DataTableEnt ent = getAccess().nodeService().getOutputDataTable(m_nodeEnt.getRootWorkflowID(),
                    m_nodeEnt.getNodeID(), getEntity().getPortIndex(), (long)chunkIndex * CHUNK_SIZE, CHUNK_SIZE);
                if (chunkIndex < m_chunks.size()) {
                    m_chunks.set(chunkIndex, ent);
                } else {
                    m_chunks.add(ent);
                }
            } catch (NodeNotFoundException | InvalidRequestException ex) {
                //should never happen
                throw new IllegalStateException(ex);
            }
        }
        return m_chunks.get(chunkIndex);
    }

}
