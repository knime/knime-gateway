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
package org.knime.gateway.impl.webui.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Test;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.port.database.DatabaseConnectionPortObject;
import org.knime.core.node.port.database.DatabasePortObject;
import org.knime.core.node.port.viewproperty.ShapeHandlerPortObject;
import org.knime.gateway.api.webui.entity.AppStateEnt;
import org.knime.gateway.api.webui.entity.AppStateEnt.AppModeEnum;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.modes.WebUIMode;
import org.knime.gateway.impl.webui.spaces.Space;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProviders;
import org.knime.gateway.testing.helper.TestWorkflowCollection;

/**
 * Tests for the {@link DefaultApplicationService}-implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultApplicationServiceTest extends GatewayServiceTest {

    /**
     * Type ID of the DB data {@link PortObject} type
     */
    private static final String DB_DATA_PORT_OBJECT_TYPE_ID = "org.knime.database.port.DBDataPortObject";

    /**
     * Type ID of the DB session {@link PortObject}
     */
    private static final String DB_SESSION_PORT_OBJECT_TYPE_ID = "org.knime.database.port.DBSessionPortObject";


    /**
     * Test to get the app state.
     *
     * @throws Exception
     */
    @Test
    public void testGetAppState() throws Exception {
        String workflowProjectId = "the_workflow_project_id";
        loadWorkflow(TestWorkflowCollection.HOLLOW, workflowProjectId);
        ProjectManager.getInstance().openAndCacheProject(workflowProjectId);
        ProjectManager.getInstance().setProjectActive(workflowProjectId);

        var appService = DefaultApplicationService.getInstance();

        AppStateEnt appStateEnt = appService.getState();
        assertThat(appStateEnt.hasNodeRecommendationsEnabled(), not(is(nullValue())));
        AppStateEnt appStateEntStripped = stripAppState(appStateEnt);
        cr(appStateEntStripped, "appstate");
    }

    /**
     * Makes sure the set {@link WebUIMode} is supplied via the app state as expected.
     *
     * @throws Exception
     */
    @Test
    public void testGetAppStateWithMode() throws Exception {
        String workflowProjectId = "the_workflow_project_id";
        loadWorkflow(TestWorkflowCollection.HOLLOW, workflowProjectId);

        var appService = DefaultApplicationService.getInstance();

        var modeSysProp = "org.knime.ui.mode";
        System.setProperty(modeSysProp, "DEFAULT");
        var appStateEnt = appService.getState();
        assertThat(appStateEnt.getAppMode(), is(AppModeEnum.DEFAULT));

        System.setProperty(modeSysProp, "JOB-VIEWER");
        appStateEnt = appService.getState();
        assertThat(appStateEnt.getAppMode(), is(AppModeEnum.JOB_VIEWER));

        System.setProperty(modeSysProp, "PLAYGROUND");
        appStateEnt = appService.getState();
        assertThat(appStateEnt.getAppMode(), is(AppModeEnum.PLAYGROUND));

        System.clearProperty(modeSysProp);
    }

    @Override
    protected SpaceProviders createSpaceProviders() {
        var space = mock(Space.class);
        var spaceProvider = mock(SpaceProvider.class);
        when(spaceProvider.getSpace(any())).thenReturn(space);
        var spaceProviders = mock(SpaceProviders.class);
        when(spaceProviders.getProvidersMap()).thenReturn(Map.of("Provider ID for testing", spaceProvider));
        return spaceProviders;
    }

    private static AppStateEnt stripAppState(final AppStateEnt appStateEnt) {
        var availablePortTypes = appStateEnt.getAvailablePortTypes().entrySet().stream().filter(e -> {
            var k = e.getKey();
            return k.equals(BufferedDataTable.class.getName()) || k.equals(PortObject.class.getName())
                || k.equals(DatabaseConnectionPortObject.class.getName())
                || k.equals(DatabasePortObject.class.getName()) || k.equals(ShapeHandlerPortObject.class.getName());
        }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        var suggestedPortTypeIds = appStateEnt.getSuggestedPortTypeIds();
        if (areDatabasePortsAvailable()) {
            assertThat(suggestedPortTypeIds, hasItems(DB_DATA_PORT_OBJECT_TYPE_ID, DB_SESSION_PORT_OBJECT_TYPE_ID));
            suggestedPortTypeIds = suggestedPortTypeIds.stream().filter(id -> !id.equals(DB_DATA_PORT_OBJECT_TYPE_ID))
                .filter(id -> !id.equals(DB_SESSION_PORT_OBJECT_TYPE_ID)).toList();
        }

        return builder(AppStateEnt.AppStateEntBuilder.class) //
            .setAppMode(appStateEnt.getAppMode()) //
            .setOpenProjects(appStateEnt.getOpenProjects()) //
            .setAvailablePortTypes(availablePortTypes) //
            .setAvailableComponentTypes(appStateEnt.getAvailableComponentTypes())
            .setSuggestedPortTypeIds(suggestedPortTypeIds) //
            .setFeatureFlags(appStateEnt.getFeatureFlags()) //
            .build();
    }

    private static boolean areDatabasePortsAvailable() {
        final var portTypeRegistry = PortTypeRegistry.getInstance();
        final var portObjectTypeIds = List.of(DB_DATA_PORT_OBJECT_TYPE_ID, DB_SESSION_PORT_OBJECT_TYPE_ID);
        return portObjectTypeIds.stream() //
            .allMatch(id -> !portTypeRegistry.getObjectClass(id).isEmpty());
    }
}
