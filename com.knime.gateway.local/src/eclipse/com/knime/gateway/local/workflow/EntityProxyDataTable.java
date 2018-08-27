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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.swing.JComponent;

import org.apache.commons.codec.binary.Base64;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DataTypeRegistry;
import org.knime.core.data.MissingCell;
import org.knime.core.data.RowKey;
import org.knime.core.data.UnmaterializedCell;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.container.ContainerTable;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.BooleanCell.BooleanCellFactory;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.eclipseUtil.GlobalObjectInputStream;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.BufferedDataTable.KnowsRowCountTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.exec.dataexchange.PortObjectRepository;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.workflow.BufferedDataTableView;

import com.knime.gateway.v0.entity.DataCellEnt;
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
class EntityProxyDataTable extends AbstractEntityProxy<NodePortEnt> implements PortObject, KnowsRowCountTable {
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
                    private final DataRowEnt m_row = getRow(m_index);

                    @Override
                    public Iterator<DataCell> iterator() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public int getNumCells() {
                        return m_row.getColumns().size();
                    }

                    @Override
                    public RowKey getKey() {
                        return new RowKey(m_row.getRowID());
                    }

                    @Override
                    public DataCell getCell(final int index) {
                        return createDataCell(m_row.getColumns().get(index),
                            getDataTableSpec().getColumnSpec(index).getType());
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
        return new JComponent[] {new BufferedDataTableView(this)};
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

    private DataCell createDataCell(final DataCellEnt cellEnt, final DataType type) {
        String s = cellEnt.getValueAsString();

        //if a problem occurred on the server side
        if (cellEnt.isProblem() != null && cellEnt.isProblem()) {
            //TODO pass problem message, too
            return UnmaterializedCell.getInstance();
        }

        //missing cell
        if(cellEnt.isMissing() != null && cellEnt.isMissing()) {
           return new MissingCell(cellEnt.getValueAsString());
        }

        //serialized binary value
        if (cellEnt.isBinary() != null && cellEnt.isBinary()) {
            Optional<DataCellSerializer<DataCell>> serializer =
                DataTypeRegistry.getInstance().getSerializer(type.getCellClass());
            if (!serializer.isPresent()) {
                //TODO also pass a problem message
                return UnmaterializedCell.getInstance();
            }
            ByteArrayInputStream bytes =
                new ByteArrayInputStream(Base64.decodeBase64(cellEnt.getValueAsString().getBytes()));
            try (DataCellObjectInputStream in =
                new DataCellObjectInputStream(bytes, this.getClass().getClassLoader())) {
                return serializer.get().deserialize(in);
            } catch (IOException ex) {
                //TODO also pass the exception message etc.
                return UnmaterializedCell.getInstance();
            }
        }

        //create the basic types
        if (type.equals(DoubleCell.TYPE)) {
            return new DoubleCell(Double.valueOf(s));
        } else if (type.equals(IntCell.TYPE)) {
            return new IntCell(Integer.valueOf(s));
        } else if (type.equals(StringCell.TYPE)) {
            return new StringCell(s);
        } else if (type.equals(BooleanCell.TYPE)) {
            return BooleanCellFactory.create(s);
        } else {
            return UnmaterializedCell.getInstance();
        }
    }

    /**
     * Input stream used for deserializing a data cell. Mainly copied from {@link PortObjectRepository}
     */
    private static final class DataCellObjectInputStream
        extends GlobalObjectInputStream implements DataCellDataInput {

        private final ClassLoader m_loader;

        /** Create new stream.
         * @param in to read from
         * @param loader class loader for restoring cell.
         * @throws IOException if super constructor throws it.
         */
        DataCellObjectInputStream(final InputStream in,
                final ClassLoader loader) throws IOException {
            super(in);
            m_loader = loader;
        }

        /** {@inheritDoc} */
        @Override
        public DataCell readDataCell() throws IOException {
            try {
                return readDataCellImpl();
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                throw new IOException("Can't read nested cell: "
                        + e.getMessage(), e);
            }
        }

        private DataCell readDataCellImpl() throws Exception {
            String clName = readUTF();
            Class<? extends DataCell> cellClass = DataTypeRegistry.getInstance().getCellClass(clName)
                    .orElseThrow(() -> new IOException("No implementation for cell class '" + clName + "' found."));
            Optional<DataCellSerializer<DataCell>> cellSerializer =
                DataTypeRegistry.getInstance().getSerializer(cellClass);
            if (cellSerializer.isPresent()) {
                return cellSerializer.get().deserialize(this);
            } else {
                return (DataCell)readObject();
            }
        }

        /** {@inheritDoc} */
        @Override
        protected Class<?> resolveClass(final ObjectStreamClass desc)
                throws IOException, ClassNotFoundException {
            if (m_loader != null) {
                try {
                    return Class.forName(desc.getName(), true, m_loader);
                } catch (ClassNotFoundException cnfe) {
                    // ignore and let super do it.
                }
            }
            return super.resolveClass(desc);
        }

    }


}
