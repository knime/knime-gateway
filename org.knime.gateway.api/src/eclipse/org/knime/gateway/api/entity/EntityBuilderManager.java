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
package org.knime.gateway.api.entity;

import java.util.Collections;
import java.util.List;

import org.knime.gateway.api.util.ExtPointUtil;

/**
 * Manages entity builders (i.e. {@link GatewayEntityBuilder}s) and gives access to they implementations (that are
 * injected via the {@link EntityBuilderFactory} extension point).
 *
 * @author Martin Horn, University of Konstanz
 */
public final class EntityBuilderManager {

    private static List<EntityBuilderFactory> BUILDER_FACTORIES;

    private EntityBuilderManager() {
        //utility class
    }

    /**
     * Delivers implementations for entity builder interfaces (see {@link GatewayEntityBuilder}). Implementations are
     * injected via {@link EntityBuilderFactory} extension point.
     *
     * @param builderInterface the builder interface the implementation is requested for
     * @return an implementation of the requested builder interface (it returns a new instance with every method call)
     */
    public static <E extends GatewayEntity, B extends GatewayEntityBuilder<E>> B
        builder(final Class<B> builderInterface) {
        if (BUILDER_FACTORIES == null) {
            BUILDER_FACTORIES = createBuilderFactories();
        }
        B res = null;
        for (EntityBuilderFactory fac : BUILDER_FACTORIES) {
            res = fac.createEntityBuilder(builderInterface).orElse(null);
            if(res!= null) {
                return res;
            }
        }
        throw new IllegalStateException(
            "Failed to create builder instance of of class '" + builderInterface.getSimpleName() + "'");
    }

    private static List<EntityBuilderFactory> createBuilderFactories() {
        List<EntityBuilderFactory> instances = ExtPointUtil
            .collectExecutableExtensions(EntityBuilderFactory.EXT_POINT_ID, EntityBuilderFactory.EXT_POINT_ATTR);
        if (instances.isEmpty()) {
            throw new IllegalStateException("No entity builder factory registered.");
        } else if (instances.size() > 1) {
            Collections.sort(instances, (o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority()));
        } else {
            //
        }
        return instances;
    }

}
