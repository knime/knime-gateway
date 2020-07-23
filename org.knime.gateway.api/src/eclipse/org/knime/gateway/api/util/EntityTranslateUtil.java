/*
 * ------------------------------------------------------------------------
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
 */
package org.knime.gateway.api.util;

import static org.knime.core.node.workflow.FileNativeNodeContainerPersistor.loadCreationConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StringReader;
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
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.context.ModifiableNodeCreationConfiguration;
import org.knime.core.node.exec.dataexchange.PortObjectRepository;
import org.knime.core.node.tableview.CellLoadingError;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeUIInformation;
import org.knime.core.node.workflow.NodeUIInformation.Builder;
import org.knime.core.node.workflow.WorkflowAnnotationID;
import org.knime.core.node.workflow.WorkflowCopyContent;
import org.knime.gateway.api.entity.AnnotationIDEnt;
import org.knime.gateway.api.entity.DataCellEnt;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.entity.NodeUIInfoEnt;
import org.knime.gateway.api.entity.WorkflowPartsEnt;

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
     * Deserializes a {@link ModifiableNodeCreationConfiguration} from a json string.
     *
     * @param jsonString
     * @param factory the node factory the config belongs to
     * @return the config
     */
    public static Optional<ModifiableNodeCreationConfiguration>
        translateNodeCreationConfiguration(final String jsonString, final NodeFactory<NodeModel> factory) {
        if (jsonString != null) {
            NodeSettings settings = new NodeSettings("node creation config");
            try {
                JSONConfig.readJSON(settings, new StringReader(jsonString));
                return loadCreationConfig(settings, factory);
            } catch (IOException | InvalidSettingsException ex) {
                // should never happen
                throw new RuntimeException("Problem reading node creation config settings", ex);
            }
        }
        return Optional.empty();
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
