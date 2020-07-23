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
package org.knime.gateway.impl.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.knime.gateway.api.entity.ConnectionEnt;
import org.knime.gateway.api.entity.EntityBuilderManager;
import org.knime.gateway.api.entity.GatewayEntity;
import org.knime.gateway.api.entity.NodeEnt;
import org.knime.gateway.api.entity.NodeInPortEnt;
import org.knime.gateway.api.entity.NodeOutPortEnt;
import org.knime.gateway.api.entity.PatchEnt;
import org.knime.gateway.api.entity.PatchEnt.PatchEntBuilder;
import org.knime.gateway.api.entity.PatchOpEnt;
import org.knime.gateway.api.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.entity.WorkflowAnnotationEnt;
import org.knime.gateway.impl.entity.DefaultPatchEnt.DefaultPatchEntBuilder;
import org.knime.gateway.impl.entity.DefaultPatchOpEnt.DefaultPatchOpEntBuilder;

/**
 * Processes changes between gateway entities determined using the javers library and returns them as {@link PatchEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
class PatchEntChangeProcessor implements ChangeProcessor<PatchEnt> {
    static final PatchEnt EMPTY_PATCH = EntityBuilderManager.builder(PatchEntBuilder.class).build();

    private final List<PatchOpEnt> m_ops = new ArrayList<PatchOpEnt>();

    private final Map<String, GatewayEntity> m_newObjects = new HashMap<String, GatewayEntity>();

    private final UUID m_newSnapshotID;

    private final String m_targetTypeID;

    public PatchEntChangeProcessor(final UUID newSnapshotID, final String targetTypeID) {
        m_newSnapshotID = newSnapshotID;
        m_targetTypeID = targetTypeID;
    }

    @Override
    public void onCommit(final CommitMetadata commitMetadata) {
    }

    @Override
    public void onAffectedObject(final GlobalId globalId) {
    }

    @Override
    public void beforeChangeList() {
    }

    @Override
    public void afterChangeList() {
    }

    @Override
    public void beforeChange(final Change change) {
    }

    @Override
    public void afterChange(final Change change) {
    }

    @Override
    public void onPropertyChange(final PropertyChange propertyChange) {
    }

    @Override
    public void onValueChange(final ValueChange valueChange) {
        GlobalId globalId = valueChange.getAffectedGlobalId();
        String path = "";
        if (globalId instanceof ValueObjectId) {
            path = "/" + ((ValueObjectId)globalId).getFragment().replaceAll("m_", "");
            path += "/" + valueChange.getPropertyName().replace("m_", "");
        }
        if (globalId instanceof UnboundedValueObjectId) {
            path = "/" + valueChange.getPropertyName().replace("m_", "");
        }
        m_ops.add(new DefaultPatchOpEntBuilder().setOp(OpEnum.REPLACE).setPath(path).setValue(valueChange.getRight())
            .build());
    }

    @Override
    public void onReferenceChange(final ReferenceChange referenceChange) {
    }

    @Override
    public void onNewObject(final NewObject newObject) {
        Object newObj = newObject.getAffectedObject().get();
        // these are all objects that are newly added to a map
        // the respective patch operation will be added in the onMapChange- or onListChange-methods
        if (newObj instanceof NodeEnt || newObj instanceof ConnectionEnt || newObj instanceof WorkflowAnnotationEnt
            || newObj instanceof NodeInPortEnt || newObj instanceof NodeOutPortEnt) {
            ValueObjectId globalId = (ValueObjectId)newObject.getAffectedGlobalId();
            String path = "/" + globalId.getFragment().replaceAll("m_", "");
            m_newObjects.put(path, (GatewayEntity)newObject.getAffectedObject().get());
        }
    }

    @Override
    public void onObjectRemoved(final ObjectRemoved objectRemoved) {
    }

    @Override
    public void onContainerChange(final ContainerChange containerChange) {
    }

    @Override
    public void onSetChange(final SetChange setChange) {
    }

    @Override
    public void onArrayChange(final ArrayChange arrayChange) {
    }

    @Override
    public void onListChange(final ListChange listChange) {
        for (ValueRemoved vr : listChange.getValueRemovedChanges()) {
            ValueObjectId val = (ValueObjectId)vr.getValue();
            String path = "/" + val.getFragment().replaceAll("m_", "");
            m_ops.add(new DefaultPatchOpEntBuilder().setOp(OpEnum.REMOVE).setPath(path).build());
        }
        for(ValueAdded va : listChange.getValueAddedChanges()) {
            ValueObjectId val = (ValueObjectId)va.getValue();
            String path = "/" + val.getFragment().replaceAll("m_", "");
            //NOTE: setValue relies on the fact the #onNewObject has been called before, with the right object
            m_ops.add(
                new DefaultPatchOpEntBuilder().setOp(OpEnum.ADD).setPath(path).setValue(m_newObjects.get(path))
                    .build());
        }
    }

    @Override
    public void onMapChange(final MapChange mapChange) {
        for(EntryRemoved er : mapChange.getEntryRemovedChanges()) {
            ValueObjectId val = (ValueObjectId)er.getValue();
            String path = "/" + val.getFragment().replaceAll("m_", "");
            m_ops.add(new DefaultPatchOpEntBuilder().setOp(OpEnum.REMOVE).setPath(path).build());
        }
        for(EntryAdded ea : mapChange.getEntryAddedChanges()) {
            ValueObjectId val = (ValueObjectId)ea.getValue();
            String path = "/" + val.getFragment().replaceAll("m_", "");
            //NOTE: setValue relies on the fact the #onNewObject has been called before, with the right object
            m_ops.add(
                new DefaultPatchOpEntBuilder().setOp(OpEnum.ADD).setPath(path).setValue(m_newObjects.get(path))
                    .build());
        }
    }

    @Override
    public PatchEnt result() {
        return new DefaultPatchEntBuilder().setOps(m_ops).setSnapshotID(m_newSnapshotID).setTargetTypeID(m_targetTypeID)
            .build();
    }
}