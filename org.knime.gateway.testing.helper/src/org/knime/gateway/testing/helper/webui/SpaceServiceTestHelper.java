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
 *   Dec 9, 2022 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.webui.LocalWorkspace;
import org.knime.gateway.impl.webui.Space;
import org.knime.gateway.impl.webui.SpaceProvider;
import org.knime.gateway.impl.webui.SpaceProviders;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Tests {@link SpaceService}-implementations.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class SpaceServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     */
    protected SpaceServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor) {
        super(SpaceServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    /**
     * Tests {@link SpaceService#listWorkflowGroup(String, String)} for the local workspace.
     */
    public void testListWorkflowGroupForLocalWorkspace() throws Exception {
        var testWorkspacePath = getTestWorkspacePath();
        ServiceDependencies.setServiceDependency(SpaceProviders.class, () -> List.of(createLocalSpaceProviderForTesting(testWorkspacePath)));
        var spaceId = LocalWorkspace.LOCAL_WORKSPACE_SPACE_ID;
        var root = ss().listWorkflowGroup(spaceId, Space.ROOT_ITEM_ID);
        cr(root, "workspace_root");

        var group1Id = getItemIdForItemWithName(root.getItems(), "Group1");
        var group1 = ss().listWorkflowGroup(spaceId, group1Id);
        cr(group1, "workspace_group1");

        var group11Id = getItemIdForItemWithName(group1.getItems(), "Group11");
        var group11 = ss().listWorkflowGroup(spaceId, group11Id);
        cr(group11, "workspace_group11");

        var emptyGroupId = getItemIdForItemWithName(root.getItems(), "EmptyGroup");
        var emptyGroup = ss().listWorkflowGroup(spaceId, emptyGroupId);
        cr(emptyGroup, "workspace_empty_group");

        var dataTxtId = getItemIdForItemWithName(root.getItems(), "data.txt");
        assertThrows(InvalidRequestException.class, () -> ss().listWorkflowGroup(spaceId, dataTxtId));
    }

    private static SpaceProvider createLocalSpaceProviderForTesting(final Path testWorkspacePath)  {
        var localWorkspace = new LocalWorkspace(testWorkspacePath);
        return new SpaceProvider() {
            @Override public String getId() {
                return "local-testing";
            }

            @Override public List<Space> getSpaces() {
                return Collections.singletonList(localWorkspace);
            }
        };
    }

    private static String getItemIdForItemWithName(final List<SpaceItemEnt> items, final String name) {
        return items.stream().filter(i -> i.getName().equals(name)).map(SpaceItemEnt::getId).findFirst().orElse(null);
    }

    private static Path getTestWorkspacePath() throws IOException {
        return CoreUtil.resolveToFile("/files/test_workspace", SpaceServiceTestHelper.class).toPath();
    }

}
