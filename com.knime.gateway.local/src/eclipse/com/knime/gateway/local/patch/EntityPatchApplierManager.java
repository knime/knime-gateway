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

    private static final NodeLogger LOGGER = NodeLogger.getLogger(EntityPatchApplierManager.class);

    private static EntityPatchApplier PATCH_APPLIER;

    private EntityPatchApplierManager() {
        //utility class
    }

    /**
     * Collects and returns the available patch applier with the highest priority.
     *
     * @return the available patch applier
     * @throws IllegalStateException if no patch applier is available
     */
    public static EntityPatchApplier getPatchApplier() {
        if (PATCH_APPLIER == null) {
            PATCH_APPLIER = createPatchApplier();
        }
        return PATCH_APPLIER;
    }

    private static EntityPatchApplier createPatchApplier() {
        List<EntityPatchApplier> instances = ExtPointUtil.collectExecutableExtensions(EntityPatchApplier.EXT_POINT_ID,
            EntityPatchApplier.EXT_POINT_ATTR);
        if (instances.size() == 0) {
            throw new IllegalStateException("No patch applier registered.");
        } else if (instances.size() > 1) {
            LOGGER.warn("Multiple patch appliers registered. The one with the highest priority used.");
            Collections.sort(instances, (o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));
        }
        return instances.get(0);
    }

}
