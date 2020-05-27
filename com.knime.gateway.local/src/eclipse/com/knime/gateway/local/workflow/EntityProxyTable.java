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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JComponent;

import org.eclipse.jface.preference.IPreferenceStore;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTable;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.data.RowIterator;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultCellIterator;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.tableview.AsyncDataRow;
import org.knime.core.node.tableview.AsyncTable;
import org.knime.core.node.workflow.BufferedDataTableView;
import org.knime.workbench.ui.KNIMEUIPlugin;
import org.knime.workbench.ui.preferences.PreferenceConstants;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.knime.gateway.entity.DataRowEnt;
import com.knime.gateway.entity.DataTableEnt;
import com.knime.gateway.entity.NodeEnt;
import com.knime.gateway.entity.NodePortEnt;
import com.knime.gateway.service.util.ServiceExceptions.InvalidRequestException;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.util.EntityTranslateUtil;

/**
 * Entity-proxy class that proxies {@link NodePortEnt} and implements {@link PortObject} and {@link AsyncTable}.
 *
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
class EntityProxyTable extends AbstractEntityProxy<NodePortEnt>
    implements PortObject, AsyncTable, DirectAccessTable, DataTable {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(EntityProxyTable.class);

    /**
     * Number of threads to be used to download the table chunks.
     */
    private static final int NUM_CHUNK_LOADER_THREADS = 1;

    /**
     * The size of the chunks to be retrieved and cached.
     */
    private final int m_chunkSize;

    private final DataTableSpec m_spec;

    private final NodeEnt m_nodeEnt;

    private Long m_totalRowCount = null;

    /* keeps track of the row count if not known in advance */
    private long m_currentRowCount = 0;

    private static final ExecutorService REMOTE_TABLE_CHUNK_LOADER_EXECUTORS =
        Executors.newFixedThreadPool(NUM_CHUNK_LOADER_THREADS, new ThreadFactory() {
            private final AtomicInteger m_counter = new AtomicInteger();

            @Override
            public Thread newThread(final Runnable r) {
                Thread t = new Thread(r, "Remote Table Chunk Loader-" + m_counter.incrementAndGet());
                return t;
            }
        });

    private BiConsumer<Long, Long> m_rowsAvailableCallback;

    private Consumer<Long> m_rowCountKnownCallback;

    /* list of loading chunks to be remembered for cancellation on cancel() */
    private List<CompletableFuture<DataTableEnt>> m_loadingChunks = new ArrayList<>();

    /* LRU cache for retrieved rows that maps row index to row */
    private Cache<Long, DataRow> m_rowCache = CacheBuilder.newBuilder().weakValues().build();

    /**
     * Creates a new entity proxy.
     *
     * @param portEnt the port
     * @param nodeEnt the node
     * @param spec the spec of the data table that is proxied
     * @param access see {@link AbstractEntityProxy}
     */
    public EntityProxyTable(final NodePortEnt portEnt, final NodeEnt nodeEnt, final DataTableSpec spec,
        final EntityProxyAccess access) {
        super(portEnt, access);
        m_nodeEnt = nodeEnt;
        m_spec = spec;
        IPreferenceStore prefStore = KNIMEUIPlugin.getDefault().getPreferenceStore();
        m_chunkSize = prefStore.getInt(PreferenceConstants.P_REMOTE_WORKFLOW_EDITOR_TABLE_VIEW_CHUNK_SIZE);
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
    public RowIterator iterator() {
        return new DirectAcessDataTable(m_chunkSize, this).iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<DataRow> getRows(final long from, final int count, final ExecutionMonitor exec)
        throws IndexOutOfBoundsException, CanceledExecutionException {
        int newCount = count;
        if (m_totalRowCount != null) {
            if (from >= m_totalRowCount) {
                return Collections.emptyList();
            } else if (from + count > m_totalRowCount) {
                newCount = (int)(m_totalRowCount - from);
            }
        }

        List<DataRow> chunk = new ArrayList<>(count);
        for (long i = from; i < from + count; i++) {
            DataRow row;
            if ((row = m_rowCache.getIfPresent(i)) != null) {
                chunk.add(row);
            } else {
                break;
            }
        }

        if (chunk.size() == newCount) {
            //all requested rows are in the cache
            LOGGER.debug("Getting chunk from cache (#rows in cache: " + m_rowCache.size() + ")");
            return chunk;
        } else {
            //not all rows in cache -> retrieve the whole chunk (again)
            LOGGER.debug("Requesting new chunk");
            CompletableFuture<DataTableEnt> futureChunk = getChunkAsync(from, newCount);
            registerFutureChunkForCancellation(futureChunk);
            return IntStream.range(0, newCount).mapToObj(idx -> {
                return createAsyncDataRow(from, idx, futureChunk);
            }).collect(Collectors.toList());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getRowCount() throws UnknownRowCountException {
        if(m_totalRowCount == null) {
            throw new UnknownRowCountException();
        } else {
            return m_totalRowCount;
        }
    }

    private AsyncDataRow createAsyncDataRow(final long from, final int idx, final CompletableFuture<DataTableEnt> futureChunk) {
        CompletableFuture<DataRow> futureRow = futureChunk.handleAsync((c, e) -> {
            if (c != null) {
                if (idx < c.getRows().size()) {
                    DataRow row = new EntityProxyDataRow(c.getRows().get(idx), getAccess());
                    m_rowCache.put(from + idx, row);
                    return row;
                } else {
                    return null;
                }
            } else {
                assert e != null;
                throw new CompletionException(e);
            }
        });
        return new AsyncDataRow(from + idx, getDataTableSpec().getNumColumns(), futureRow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowsAvailableCallback(final BiConsumer<Long, Long> rowsAvailableCallback) {
        m_rowsAvailableCallback = rowsAvailableCallback;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowCountKnownCallback(final Consumer<Long> rowCountKnownCallback) {
        if (m_rowCountKnownCallback != rowCountKnownCallback) {
            m_rowCountKnownCallback = rowCountKnownCallback;
            if (m_rowCountKnownCallback != null && m_totalRowCount != null) {
                //immediately inform row count callback if total row count is already known
                m_rowCountKnownCallback.accept(m_totalRowCount);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel() {
        m_loadingChunks.forEach(f -> f.cancel(true));
        m_loadingChunks.clear();
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
     * Synchronous access to a chunk. A http-request will be made and the methods blocks till response is received.
     */
    private DataTableEnt getChunk(final long from, final int count) {
        try {
            return getAccess().nodeService().getOutputDataTable(m_nodeEnt.getRootWorkflowID(), m_nodeEnt.getNodeID(),
                getEntity().getPortIndex(), from, count);
        } catch (NodeNotFoundException | InvalidRequestException ex) {
            //should never happen
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Retrieves a chunk asynchronously.
     */
    private CompletableFuture<DataTableEnt> getChunkAsync(final long from, final int count) {
        return CompletableFuture.supplyAsync(() -> {
            DataTableEnt chunk = getChunk(from, count);
            synchronized (m_nodeEnt) {
                if (m_totalRowCount == null) {
                    if (chunk.getNumTotalRows() >= 0) {
                        //set row count, if known
                        m_totalRowCount = chunk.getNumTotalRows();
                    } else {
                        if (chunk.getRows() != null) {
                            long size = chunk.getRows().size();
                            m_currentRowCount = Math.max(m_currentRowCount, from + size);
                            if (size > 0 && size < m_chunkSize) {
                                //definitely end of table -> row count found empirically
                                m_totalRowCount = m_currentRowCount;
                            }
                        }

                        if (chunk.getRows() == null || chunk.getRows().size() == 0) {
                            //maybe the end of table (or beyond) -> total row count maybe found "empirically"
                            if (from == m_currentRowCount) {
                                m_totalRowCount = m_currentRowCount;
                            }
                        }
                    }
                    if (m_totalRowCount != null && m_rowCountKnownCallback != null) {
                        m_rowCountKnownCallback.accept(m_totalRowCount);
                    }
                }
            }

            if (m_rowsAvailableCallback != null) {
                m_rowsAvailableCallback.accept(from, from + chunk.getRows().size());
            }
            return chunk;
        }, REMOTE_TABLE_CHUNK_LOADER_EXECUTORS);
    }

    private void registerFutureChunkForCancellation(final CompletableFuture<DataTableEnt> future) {
        //clean list from finished chunks first
        if (!m_loadingChunks.isEmpty()) {
            m_loadingChunks = m_loadingChunks.stream().filter(f -> !f.isDone()).collect(Collectors.toList());
        }
        m_loadingChunks.add(future);
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
            return EntityTranslateUtil.translateDataCellEnt(getEntity().getColumns().get(index),
                getDataTableSpec().getColumnSpec(index).getType());
        }

    }
}
