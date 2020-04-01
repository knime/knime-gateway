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
package com.knime.gateway.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.codec.binary.Base64;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;
import org.knime.core.data.DataTypeRegistry;
import org.knime.core.data.MissingCell;
import org.knime.core.data.def.BooleanCell;
import org.knime.core.data.def.BooleanCell.BooleanCellFactory;
import org.knime.core.data.def.DoubleCell;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.LongCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.exec.dataexchange.PortObjectRepository;
import org.knime.core.node.tableview.CellLoadingError;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.NodeUIInformation.Builder;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;

import com.knime.gateway.entity.AnnotationIDEnt;
import com.knime.gateway.entity.DataCellEnt;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.NodeUIInfoEnt;
import com.knime.gateway.entity.WorkflowPartsEnt;

/**
 * Helper methods to translate gateway entities into KNIME-core objects.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityTranslateUtil {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(EntityTranslateUtil.class);

    private EntityTranslateUtil() {
        //utility class
    }

    /**
     * Translates {@link WorkflowPartsEnt} into {@link WorkflowCopyContent}.
     *
     * @param ent the entity
     * @param entity2NodeID function that translates a {@link NodeIDEnt} to a {@link NodeID}-instance
     * @param entity2AnnotationID function that translates a string into a {@link WorkflowAnnotationID}-instance.
     * @return the newly created translation result
     */
    public static WorkflowCopyContent translateWorkflowPartsEnt(final WorkflowPartsEnt ent,
        final Function<NodeIDEnt, NodeID> entity2NodeID,
        final Function<AnnotationIDEnt, WorkflowAnnotationID> entity2AnnotationID) {
        return WorkflowCopyContent.builder()
            .setNodeIDs(ent.getNodeIDs().stream().map(e -> entity2NodeID.apply(e)).toArray(size -> new NodeID[size]))
            .setAnnotationIDs(ent.getAnnotationIDs().stream().map(s -> entity2AnnotationID.apply(s))
                .toArray(size -> new WorkflowAnnotationID[size]))
            .build();
    }

    /**
     * Translates {@link NodeUIInfoEnt} into {@link NodeUIInformation}.
     *
     * @param ent the entity
     * @return the newly created translation result
     */
    public static NodeUIInformation translateNodeUIInfoEnt(final NodeUIInfoEnt ent) {
        Builder builder = NodeUIInformation.builder()
            .setNodeLocation(ent.getBounds().getX(), ent.getBounds().getY(), ent.getBounds().getWidth(),
                ent.getBounds().getHeight())
            //NodeUIInfoEnt always has absolute coordinates
            .setHasAbsoluteCoordinates(true);

        if (ent.isSymbolRelative() != null) {
            builder.setIsSymbolRelative(ent.isSymbolRelative());
        }
        if (ent.isDropLocation() != null) {
            builder.setIsDropLocation(ent.isDropLocation());
        }
        if (ent.isSnapToGrid() != null) {
            builder.setSnapToGrid(ent.isSnapToGrid());
        }
        return builder.build();
    }

    /**
     * Translates, i.e. deserializes a {@link DataCell} from a {@link DataCellEnt}.
     *
     * @param cellEnt the entity
     * @param typeFromSpec the cell's data type as given by the table spec
     * @return a new data cell
     */
    public static DataCell translateDataCellEnt(final DataCellEnt cellEnt, final DataType typeFromSpec) {
        String s = cellEnt.getValueAsString();

        //if a problem occurred on the server side
        if (cellEnt.isProblem() != null && cellEnt.isProblem()) {
            return new ErrorCell(cellEnt.getValueAsString());
        }

        //missing cell
        if (cellEnt.isMissing() != null && cellEnt.isMissing()) {
            return new MissingCell(cellEnt.getValueAsString());
        }


        Class<? extends DataCell> cellClass;
        if (cellEnt.getType() != null) {
            //use type provided with the entity for deserialization
            try {
                cellClass = DataTypeRegistry.getInstance().getCellClass(cellEnt.getType())
                    .orElseThrow(ClassNotFoundException::new);
            } catch (ClassCastException | ClassNotFoundException ex) {
                return new ErrorCell(
                    "Cannot deserialize cell of type '" + cellEnt.getType() + "': " + ex.getClass().getSimpleName());
            }
        } else {
            //use the type from the data table spec
            cellClass = typeFromSpec.getCellClass();
            if (cellClass == null) {
                //should never happen
                return new ErrorCell("No type given for cell for deserialization. The cell's value is '"
                    + cellEnt.getValueAsString() + "'");
            }
        }

        //serialized binary value
        if (cellEnt.isBinary() != null && cellEnt.isBinary()) {
            Optional<DataCellSerializer<DataCell>> serializer =
                DataTypeRegistry.getInstance().getSerializer(cellClass);
            if (!serializer.isPresent()) {
                return new ErrorCell("No serializer available for cell of type '" + typeFromSpec.toPrettyString() + "'");
            }
            ByteArrayInputStream bytes =
                new ByteArrayInputStream(Base64.decodeBase64(cellEnt.getValueAsString().getBytes()));
            try (DataCellObjectInputStream in =
                new DataCellObjectInputStream(bytes, DataTypeRegistry.class.getClassLoader())) {
                return serializer.get().deserialize(in);
            } catch (IOException ex) {
                LOGGER.error("Problem deserializing cell", ex);
                return new ErrorCell("Problem deserializing cell: " + ex.getMessage() + " (see log for more details)");
            }
        }

        //create the basic types
        DataType type = DataType.getType(cellClass);
        if (type.equals(DoubleCell.TYPE)) {
            return new DoubleCell(Double.valueOf(s));
        } else if (type.equals(IntCell.TYPE)) {
            return new IntCell(Integer.valueOf(s));
        } else if (type.equals(StringCell.TYPE)) {
            return new StringCell(s);
        } else if (type.equals(LongCell.TYPE)) {
            return new LongCell(Long.valueOf(s));
        } else if (type.equals(BooleanCell.TYPE)) {
            return BooleanCellFactory.create(s);
        } else {
            //we should actually never end up here
            return new ErrorCell("Cell of type '" + type.toPrettyString()
                + " couldn't be deserialized. Most likely an implementation problem.");
        }
    }

    /**
     * Cell representing a loading error.
     */
    private static final class ErrorCell extends DataCell implements CellLoadingError {

        private String m_errorMessage;

        private ErrorCell(final String errorMessage) {
            m_errorMessage = errorMessage;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getErrorMessage() {
            return m_errorMessage;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return m_errorMessage;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean equalsDataCell(final DataCell dc) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return 0;
        }
    }

    /**
     * Input stream used for deserializing a data cell. Mainly copied from {@link PortObjectRepository}
     */
    private static final class DataCellObjectInputStream extends ObjectInputStream implements DataCellDataInput {

        private final ClassLoader m_loader;

        /**
         * Create new stream.
         *
         * @param in to read from
         * @param loader class loader for restoring cell.
         * @throws IOException if super constructor throws it.
         */
        DataCellObjectInputStream(final InputStream in, final ClassLoader loader) throws IOException {
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
                throw new IOException("Can't read nested cell: " + e.getMessage(), e);
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
        protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
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
