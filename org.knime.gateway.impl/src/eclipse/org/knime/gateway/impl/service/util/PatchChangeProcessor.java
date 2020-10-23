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
package org.knime.gateway.impl.service.util;

import java.util.HashMap;
import java.util.Map;

import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.container.ValueAdded;
import org.javers.core.diff.changetype.container.ValueRemoved;
import org.javers.core.diff.changetype.map.EntryAdded;
import org.javers.core.diff.changetype.map.EntryRemoved;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Processes changes between gateway entities determined using the javers library and forwards them as patch operations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @param <P> the patch object
 */
class PatchChangeProcessor<P> implements ChangeProcessor<P> {

    private final Map<String, GatewayEntity> m_newObjects = new HashMap<>();

    private final PatchCreator<P> m_patchCreator;

    PatchChangeProcessor(final PatchCreator<P> patchCreator) {
        m_patchCreator = patchCreator;
    }

    @Override
    public void onCommit(final CommitMetadata commitMetadata) {
        //
    }

    @Override
    public void onAffectedObject(final GlobalId globalId) {
        //
    }

    @Override
    public void beforeChangeList() {
        //
    }

    @Override
    public void afterChangeList() {
        //
    }

    @Override
    public void beforeChange(final Change change) {
        //
    }

    @Override
    public void afterChange(final Change change) {
        //
    }

    @Override
    public void onPropertyChange(final PropertyChange propertyChange) {
        //
    }

    @Override
    public void onValueChange(final ValueChange valueChange) {
        String path = getPath(valueChange.getAffectedGlobalId(), valueChange.getPropertyName());
        if (valueChange.getRight() == null) {
            m_patchCreator.removed(path);
        } else if (valueChange.getLeft() == null) {
            m_patchCreator.added(path, valueChange.getRight());
        } else {
            m_patchCreator.replaced(path, valueChange.getRight());
        }
    }

    private static String getPath(final GlobalId globalId, final String propertyName) {
        String path = "";
        if (globalId instanceof ValueObjectId) {
            path = "/" + ((ValueObjectId)globalId).getFragment().replace("m_", ""); //NOSONAR
        }
        if ((propertyName != null && globalId instanceof UnboundedValueObjectId) || propertyName != null) {
            path += "/" + propertyName.replace("m_", ""); //NOSONAR
        }
        return path;
    }

    @Override
    public void onReferenceChange(final ReferenceChange referenceChange) {
        //
    }

    @Override
    public void onNewObject(final NewObject newObject) {
        Object newObj = newObject.getAffectedObject()
            .orElseThrow(() -> new IllegalStateException("Object expected to be present."));
        // These are all objects that are newly added to a map or list.
        // The respective patch operation will be added in the onMapChange- or onListChange-methods.
        // It needs to be done this way because all sub-objects of an object added to a list or map
        // are arriving here, too, and we don't want to create an extra patch-operation for each.
        if (m_patchCreator.isNewCollectionObjectValid(newObj)) {
            String path = getPath(newObject.getAffectedGlobalId(), null);
            m_newObjects.put(path, (GatewayEntity)newObj);
        } else if(m_patchCreator.isNewObjectValid(newObj)) {
            String path = getPath(newObject.getAffectedGlobalId(), null);
            m_patchCreator.added(path, newObj);
        } else {
            //
        }
    }

    @Override
    public void onObjectRemoved(final ObjectRemoved objectRemoved) {
        //
    }

    @Override
    public void onContainerChange(final ContainerChange containerChange) {
        //
    }

    @Override
    public void onSetChange(final SetChange setChange) {
        //
    }

    @Override
    public void onArrayChange(final ArrayChange arrayChange) {
        //
    }

    @Override
    public void onListChange(final ListChange listChange) {
        String path = getPath(listChange.getAffectedGlobalId(), listChange.getPropertyName());
        for (ValueRemoved vr : listChange.getValueRemovedChanges()) {
            m_patchCreator.removed(path + "/" + vr.getIndex());
        }
        for (ValueAdded va : listChange.getValueAddedChanges()) {
            Object val = va.getValue();
            String elementPath = path + "/" + va.getIndex(); // NOSONAR
            //NOTE: the value relies on the fact the #onNewObject has been called before, with the right object
            if (val instanceof ValueObjectId) {
                val = m_newObjects.get(elementPath);
            }
            m_patchCreator.added(elementPath, val);
        }
    }

    @Override
    public void onMapChange(final MapChange mapChange) {
        for (EntryRemoved er : mapChange.getEntryRemovedChanges()) {
            ValueObjectId val = (ValueObjectId)er.getValue();
            String path = "/" + val.getFragment().replace("m_", ""); //NOSONAR
            m_patchCreator.removed(path);
        }
        for (EntryAdded ea : mapChange.getEntryAddedChanges()) {
            ValueObjectId val = (ValueObjectId)ea.getValue();
            String path = "/" + val.getFragment().replace("m_", ""); //NOSONAR
            //NOTE: the value relies on the fact the #onNewObject has been called before, with the right object
            m_patchCreator.added(path, m_newObjects.get(path));
        }
    }

    @Override
    public P result() {
        return m_patchCreator.create();
    }

}