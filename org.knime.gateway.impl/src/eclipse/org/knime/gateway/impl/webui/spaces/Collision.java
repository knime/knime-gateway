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
 * History
 *   Created on Jun 17, 2024 by leonard.woerteler
 */
package org.knime.gateway.impl.webui.spaces;

/**
 * Conflict between a local item and an item in the Hub repository.
 *
 * @param isTypeCompatible whether the two colliding items are type compatible, so one can overwrite the other
 * @param canEdit whether the current user has edit permissions on the item
 * @param canRename whether renaming is a possible resolution (i.e., whether we have write permission to the parent)
 *
 * @author Leonard Wörteler, KNIME GmbH, Konstanz, Germany
 */
public record Collision(boolean isTypeCompatible, boolean canEdit, boolean canRename) {
    /**
     * @return {@code true} if the conflict can be resolved by overwriting the remote item, {@code false} otherwise
     */
    public boolean canOverwrite() {
        return canEdit && isTypeCompatible;
    }
}
