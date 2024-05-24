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
 *   Nov 10, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.entity;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.impl.webui.entity.AppStateEntityFactory.buildAppStateEntDiff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppStateEntBuilder;

/**
 * Tests for {@link AppStateEntityFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class AppStateEntityFactoryTest {

    /**
     * Tests {@link AppStateEntityFactory#buildAppStateEntDiff(org.knime.gateway.api.webui.entity.AppStateEnt,
     * org.knime.gateway.api.webui.entity.AppStateEnt, boolean)}.
     */
    @Test
    public void testBuildAppStateEntDiff() {
        var oldAppState = builder(AppStateEntBuilder.class) //
            .setAvailableComponentTypes(emptyList()) //
            .setAvailablePortTypes(emptyMap()) //
            .setDevMode(Boolean.FALSE) //
            .setFeatureFlags(emptyMap()) //
            .setFileExtensionToNodeTemplateId(emptyMap()) //
            .setHasNodeCollectionActive(Boolean.FALSE) //
            .setHasNodeRecommendationsEnabled(Boolean.FALSE) //
            .setNodeRepositoryLoaded(Boolean.FALSE) //
            .setOpenProjects(emptyList()) //
            .setScrollToZoomEnabled(Boolean.FALSE) //
            .setSuggestedPortTypeIds(emptyList()) //
            .build();

        var newAppState = builder(AppStateEntBuilder.class) //
            .setAvailableComponentTypes(oneNullElementList()) //
            .setAvailablePortTypes(oneNullElementMap()) //
            .setDevMode(Boolean.TRUE) //
            .setFeatureFlags(oneNullElementMap()) //
            .setFileExtensionToNodeTemplateId(oneNullElementMap()) //
            .setHasNodeCollectionActive(Boolean.TRUE) //
            .setHasNodeRecommendationsEnabled(Boolean.TRUE) //
            .setNodeRepositoryLoaded(Boolean.TRUE) //
            .setOpenProjects(oneNullElementList()) //
            .setScrollToZoomEnabled(Boolean.TRUE) //
            .setSuggestedPortTypeIds(oneNullElementList()) //
            .build();

        var expectedAppStateDiff = builder(AppStateEntBuilder.class) //
            .setOpenProjects(oneNullElementList()) //
            .setHasNodeCollectionActive(Boolean.TRUE) //
            .setHasNodeRecommendationsEnabled(Boolean.TRUE) //
            .setScrollToZoomEnabled(Boolean.TRUE) //
            .setNodeRepositoryLoaded(Boolean.TRUE) //
            .build();

        assertThat(buildAppStateEntDiff(null, newAppState, true), sameInstance(newAppState));
        assertThat(buildAppStateEntDiff(oldAppState, newAppState, true), is(expectedAppStateDiff));

        expectedAppStateDiff = builder(AppStateEntBuilder.class) //
                .setOpenProjects(null) //
                .setHasNodeCollectionActive(Boolean.TRUE) //
                .setHasNodeRecommendationsEnabled(Boolean.TRUE) //
                .setScrollToZoomEnabled(Boolean.TRUE) //
                .setNodeRepositoryLoaded(Boolean.TRUE) //
                .build();
        assertThat(buildAppStateEntDiff(oldAppState, newAppState, false), is(expectedAppStateDiff));

    }

    private static <T> List<T> oneNullElementList() {
        var res = new ArrayList<T>(1);
        res.add(null);
        return res;
    }

    private static <T> Map<String, T> oneNullElementMap() {
        var res = new HashMap<String, T>(1);
        res.put("blub", null);
        return res;
    }

}
