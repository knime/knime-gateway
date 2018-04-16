/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
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
