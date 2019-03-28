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

import com.knime.gateway.entity.GatewayEntity;
import com.knime.gateway.entity.PatchEnt;

/**
 * Applies patches ({@link PatchEnt}) to {@link GatewayEntity gateway entities}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface EntityPatchApplier {

    static final String EXT_POINT_ID = "com.knime.gateway.local.patch.EntityPatchApplier";

    static final String EXT_POINT_ATTR = "EntityPatchApplier";

    /**
     * Normal priority, <code>0</code>.
     */
    public static final int NORMAL_PRIORITY = 0;

    /**
     * @return the priority with what that patch applier will be used in case of multiple registered patch appliers in
     *         the {@link EntityPatchApplierManager}
     */
    default int getPriority() {
        return NORMAL_PRIORITY;
    }

    /**
     * Applies the given path to the entity.
     *
     * @param entity the entity the patch is applied on
     * @param patch the patch to apply
     * @return the entity with the patch applied
     * @throws IllegalArgumentException if the entity type id doesn't match with the target type id of the patch
     */
    <T extends GatewayEntity> T applyPatch(T entity, PatchEnt patch) throws IllegalArgumentException;

}