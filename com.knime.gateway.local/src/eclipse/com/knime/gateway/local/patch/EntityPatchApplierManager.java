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
package com.knime.gateway.local.patch;

import java.util.Collections;
import java.util.List;

import org.knime.core.node.NodeLogger;

import com.knime.gateway.util.ExtPointUtil;

/**
 * Manages the {@link EntityPatchApplier} extension point, i.e. collecting and providing the implementations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityPatchApplierManager {
    private static final EntityPatchApplier INSTANCE = createPatchApplier();

    private EntityPatchApplierManager() {
        //utility class
    }

    /**
     * Returns the available patch applier with the highest priority.
     *
     * @return the available patch applier
     * @throws IllegalStateException if no patch applier is available
     */
    public static EntityPatchApplier getPatchApplier() {
        return INSTANCE;
    }

    private static EntityPatchApplier createPatchApplier() {
        List<EntityPatchApplier> instances = ExtPointUtil.collectExecutableExtensions(EntityPatchApplier.EXT_POINT_ID,
            EntityPatchApplier.EXT_POINT_ATTR);

        if (instances.isEmpty()) {
            throw new IllegalStateException(
                "No patch applier registered at extension point " + EntityPatchApplier.EXT_POINT_ID + ".");
        } else if (instances.size() > 1) {
            Collections.sort(instances, (o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));
            NodeLogger.getLogger(EntityPatchApplierManager.class)
                .warn("Multiple patch appliers registered at extension point " + EntityPatchApplier.EXT_POINT_ID
                    + ". The one with the highest priority will be used (" + instances.get(0).getClass().getName()
                    + ".");
        }
        return instances.get(0);
    }
}
