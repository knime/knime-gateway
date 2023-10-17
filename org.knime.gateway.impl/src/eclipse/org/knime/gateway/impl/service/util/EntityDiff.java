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
 * History
 *   Feb 24, 2023 (hornm): created
 */
package org.knime.gateway.impl.service.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Utility class to compare two {@link GatewayEntity GatewayEntities} while providing the result as patch operations via
 * a {@link PatchCreator}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
final class EntityDiff {

    private EntityDiff() {
        // utility
    }

    /**
     * Compares two {@link GatewayEntity GatewayEntities} and tracks the differences as patch operations via the
     * provided {@link PatchCreator}.
     *
     * The comparison (and tracking of differences) is carried out recursively for all the nested gateway entities, too
     * (i.e. for properties whose type implements {@link GatewayEntity}).
     *
     * @param <P> the type of patch the patch creator returns
     * @param e1
     * @param e2
     * @param patchCreator any differences will be reflected via respective callbacks on this object
     * @return {@code true} if the provided entities are equal (i.e. no patch operations have been tracked);
     *         {@code false} otherwise
     */
    static <P> boolean compare(final GatewayEntity e1, final GatewayEntity e2, final PatchCreator<P> patchCreator) {
        return compareEntities("", e1, e2, patchCreator);
    }

    private static <P> boolean compareEntities(final String path, final GatewayEntity e1, final GatewayEntity e2,
        final PatchCreator<P> patchCreator) {
        var areEqual = new AtomicBoolean(true);
        e1.forEachPropertyValue(e2, (name, entities) -> {
            var first = entities.getFirst();
            var second = entities.getSecond();
            if (!Objects.equals(first, second)) {
                areEqual.set(false);
                compareObjects(path + "/" + encode(name), first, second, patchCreator);
            }
        });
        return areEqual.get();
    }

    @SuppressWarnings({"java:S6201", "unchecked"})
    private static <P> void compareObjects(final String path, final Object o1, final Object o2,
        final PatchCreator<P> patchCreator) {
        if (o1 == null) {
            patchCreator.added(path, o2);
        } else if (o2 == null) {
            patchCreator.removed(path);
        } else if (o1 instanceof GatewayEntity) {
            compareEntities(path, (GatewayEntity)o1, (GatewayEntity)o2, patchCreator);
        } else if (o1 instanceof List) {
            compareLists(path, (List<?>)o1, (List<?>)o2, patchCreator);
        } else if (o1 instanceof Map) {
            compareMaps(path, (Map<String, ?>)o1, (Map<String, ?>)o2, patchCreator);
        } else {
            patchCreator.replaced(path, o2);
        }
    }

    private static <P> void compareLists(final String path, final List<?> l1, final List<?> l2,
        final PatchCreator<P> patchCreator) {
        var l1Size = l1.size();
        var l2Size = l2.size();
        for (int i = 0; i < Math.max(l1Size, l2Size); i++) {
            var el1 = i < l1Size ? l1.get(i) : null;
            var el2 = i < l2Size ? l2.get(i) : null;
            if (!Objects.equals(el1, el2)) {
                var index = i;
                if (i >= l2Size) {
                    // If an element is removed from a list/array via a patch operation,
                    // the other subsequent elements in the list are all moved one to the 'left'.
                    // And since patch operations are applied one after another, we need to account for that
                    // when determining the list index of the element to remove - thus the 'index - i'.
                    // (e.g. when remove all elements from a list, the indices must be (0,0,0,...) instead
                    // of (0,1,2,3,...)
                    index = l2Size;
                }
                compareObjects(path + "/" + index, el1, el2, patchCreator);
            }
        }
    }

    private static <P> void compareMaps(final String path, final Map<String, ?> m1, final Map<String, ?> m2,
        final PatchCreator<P> patchCreator) {
        var keys = new HashSet<String>();
        keys.addAll(m1.keySet());
        keys.addAll(m2.keySet());
        for (var key : keys) {
            var o1 = m1.get(key);
            var o2 = m2.get(key);
            if (!Objects.equals(o1, o2)) {
                compareObjects(path + "/" + encode(key), m1.get(key), m2.get(key), patchCreator);
            }
        }
    }

    /* JSON pointer encoding according to RFC 6901 */
    private static String encode(final String s) {
        return s.replace("~", "~0").replace("/", "~1");
    }

}
