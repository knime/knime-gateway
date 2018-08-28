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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

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
import org.knime.core.data.def.DefaultCellIterator;
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
import org.knime.core.node.tableview.AsyncDataRow;
import org.knime.core.node.tableview.AsyncDataTable;
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
class EntityProxyDataTable extends AbstractEntityProxy<NodePortEnt>
    implements PortObject, KnowsRowCountTable, AsyncDataTable {
    /**
     * The size of the chunks to be retrieved and cached.
     */
    private static final int CHUNK_SIZE = 100;

    private final DataTableSpec m_spec;

    private final NodeEnt m_nodeEnt;

    /* cached chunks */
    private final List<DataTableEnt> m_chunks;

    /* future chunks queued for loading */
    private final Map<Integer, CompletableFuture<DataTableEnt>> m_loadingChunks =
        new HashMap<Integer, CompletableFuture<DataTableEnt>>();

    private static final ExecutorService REMOTE_TABLE_CHUNK_LOADER_EXECUTORS =
        Executors.newFixedThreadPool(1, new ThreadFactory() {
            private final AtomicInteger m_counter = new AtomicInteger();

            @Override
            public Thread newThread(final Runnable r) {
                Thread t = new Thread(r, "Remote Table Chunk Loader-" + m_counter.incrementAndGet());
                return t;
            }
        });

    private Consumer<long[]> m_rowsAvailableCallback;

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
                CompletableFuture<DataTableEnt> futureChunk = getChunkAsync(chunkIndex(m_index));
                DataRow row;
                if(futureChunk.isDone()) {
                    //chunk already loaded
                    row = new EntityProxyDataRow(getRow(futureChunk.getNow(null), m_index), getAccess());
                } else {
                    //chunk not loaded, yet, or still loading
                    final long idx = m_index;
                    CompletableFuture<DataRow> futureRow = futureChunk.thenApply(c -> {
                        return new EntityProxyDataRow(getRow(c, idx), getAccess());
                    });
                    row = new AsyncDataRow(idx, getDataTableSpec().getNumColumns(), futureRow);
                }
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
    public void setRowsAvailableCallback(final Consumer<long[]> rowsAvailableCallback) {
        m_rowsAvailableCallback = rowsAvailableCallback;
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
        m_loadingChunks.values().forEach(c -> c.cancel(false));
        m_loadingChunks.clear();
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

    private static DataRowEnt getRow(final DataTableEnt chunk, final long index) {
        return chunk.getRows().get(indexInChunk(index));
    }

    private static int chunkIndex(final long index) {
        return (int)index / CHUNK_SIZE;
    }

    private static int indexInChunk(final long index) {
        return (int)index % CHUNK_SIZE;
    }

    /**
     * Synchronous access to a chunk. A http-request will be made and the methods blocks till response is received.
     */
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

    /**
     * Retrieves a chunk for a given index asynchronously.
     *
     * @param chunkIndex the index of the chunk
     * @return the chunk as a future
     */
    private CompletableFuture<DataTableEnt> getChunkAsync(final int chunkIndex) {
        if (chunkIndex < m_chunks.size() && m_chunks.get(chunkIndex) != null) {
            //chunk already loaded
            return CompletableFuture.completedFuture(m_chunks.get(chunkIndex));
        } else if (m_loadingChunks.containsKey(chunkIndex)) {
            //chunk is currently loading
            return m_loadingChunks.get(chunkIndex);
        } else {
            //add chunk to queue to be loaded
            CompletableFuture<DataTableEnt> futureChunk = CompletableFuture.supplyAsync(() -> {
                DataTableEnt chunk = getChunk(chunkIndex);
                long from = chunkIndex * CHUNK_SIZE;
                long to = from + CHUNK_SIZE;
                m_rowsAvailableCallback.accept(new long[]{from, to});
                return chunk;
            }, REMOTE_TABLE_CHUNK_LOADER_EXECUTORS);
            m_loadingChunks.put(chunkIndex, futureChunk);
            return futureChunk;
        }
    }

    private static DataCell createDataCell(final DataCellEnt cellEnt, final DataType type) {
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
                new DataCellObjectInputStream(bytes, DataTypeRegistry.class.getClassLoader())) {
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

    private class EntityProxyDataRow extends AbstractEntityProxy<DataRowEnt> implements DataRow {

        /**
         * @param entity
         * @param clientProxyAccess
         */
        EntityProxyDataRow(final DataRowEnt entity, final EntityProxyAccess clientProxyAccess) {
            super(entity, clientProxyAccess);
        }

        @Override
        public Iterator<DataCell> iterator() {
            return new DefaultCellIterator(this);
        }

        @Override
        public int getNumCells() {
            return getEntity().getColumns().size();
        }

        @Override
        public RowKey getKey() {
            return new RowKey(getEntity().getRowID());
        }

        @Override
        public DataCell getCell(final int index) {
            return createDataCell(getEntity().getColumns().get(index),
                getDataTableSpec().getColumnSpec(index).getType());
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
