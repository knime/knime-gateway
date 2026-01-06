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
 *   Aug 3, 2020 (hornm): created
 */
package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.awaitility.Awaitility;
import org.junit.Test;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.util.VersionId;
import org.knime.gateway.api.webui.entity.AddComponentCommandEnt.AddComponentCommandEntBuilder;
import org.knime.gateway.api.webui.entity.AddComponentPlaceholderResultEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt;
import org.knime.gateway.api.webui.entity.ComponentPlaceholderEnt.StateEnum;
import org.knime.gateway.api.webui.entity.PatchOpEnt.OpEnum;
import org.knime.gateway.api.webui.entity.WorkflowChangedEventTypeEnt.WorkflowChangedEventTypeEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.api.webui.entity.XYEnt.XYEntBuilder;
import org.knime.gateway.api.webui.service.ComponentService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.service.events.EventConsumer;
import org.knime.gateway.impl.webui.spaces.LinkVariants;
import org.knime.gateway.impl.webui.spaces.SpaceProvidersManager;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflow;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Test for the endpoints of the node service.
 *
 * TODO NXT-4388 re-introduce and fix `testCancelAndRetryComponentLoadJob`.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @author Kai Franze, KNIME GmbH, Germany
 * @author Tobias Kampmann, TNG
 */
public class ComponentServiceTestHelper extends WebUIGatewayServiceTestHelper {

    /**
     * @param entityResultChecker
     * @param serviceProvider
     * @param workflowLoader
     * @param workflowExecutor
     * @param projectManager
     */
    public ComponentServiceTestHelper(final ResultChecker entityResultChecker, final ServiceProvider serviceProvider,
        final WorkflowLoader workflowLoader, final WorkflowExecutor workflowExecutor,
        final ProjectManager projectManager) {
        super(ComponentServiceTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor,
            projectManager);
    }

    /**
     * Tests {@link ComponentService#getComponentDescription(String, NodeIDEnt, String, NodeIDEnt)}.
     *
     * @throws Exception
     */
    public void testGetComponentDescription() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.METADATA);

        // New component
        var componentNew = new NodeIDEnt(9);
        var descNew = cs().getComponentDescription(projectId, getRootID(), null, componentNew);
        cr(descNew, "component_description_new");

        // Component with partial description
        var componentPartial = new NodeIDEnt(4);
        var descPartial = cs().getComponentDescription(projectId, getRootID(), null, componentPartial);
        cr(descPartial, "component_description_partial");

        // Component with full description
        var componentFull = new NodeIDEnt(6);
        var descFull = cs().getComponentDescription(projectId, getRootID(), null, componentFull);
        cr(descFull, "component_description_full");

        // Empty component
        var componentEmpty = new NodeIDEnt(11);
        var descEmpty = cs().getComponentDescription(projectId, getRootID(), null, componentEmpty);
        cr(descEmpty, "component_description_empty");

        // Metanode
        var metanode = new NodeIDEnt(2);
        assertThrows(ServiceCallException.class,
            () -> cs().getComponentDescription(projectId, getRootID(), null, metanode));

        // Native node
        var nativeNode = new NodeIDEnt(5);
        assertThrows(ServiceCallException.class,
            () -> cs().getComponentDescription(projectId, getRootID(), null, nativeNode));

        // Non-existing node
        var nan = new NodeIDEnt(99);
        assertThrows(ServiceCallException.class, () -> cs().getComponentDescription(projectId, getRootID(), null, nan));
    }

    /**
     * Tests {@link ComponentService#getComponentDescription(String, NodeIDEnt, String, NodeIDEnt)}.
     *
     * @throws Exception
     */
    public void testGetComponentDescriptionWithVersions() throws Exception {
        var testWorkflowWithVersion = TestWorkflow.WithVersion.of( //
            TestWorkflowCollection.VERSIONS_EXTENDED_CURRENT_STATE, //
            TestWorkflowCollection.VERSIONS_EXTENDED_EARLIER_VERSION::getWorkflowDir //
        );
        var projectId = loadWorkflow(testWorkflowWithVersion);
        var nodeId = new NodeIDEnt("root:9"); // Component

        // Current state
        var currentCompDesc =
            cs().getComponentDescription(projectId, getRootID(), VersionId.currentState().toString(), nodeId);

        var version = VersionId.parse("2");
        loadVersionAndSetActive(projectId, version);

        // Earlier version
        var versionCompDesc = cs().getComponentDescription(projectId, getRootID(), version.toString(), nodeId);

        // the descriptions should be different
        assertThat(currentCompDesc.getDescription().getValue(), is(not(versionCompDesc.getDescription().getValue())));
    }

    @Test
    public void testGetLinkVariants() throws Exception {
        final var projectId = loadWorkflow(TestWorkflowCollection.UPDATE_LINKED_COMPONENTS);
        // here it unfortunately is not feasible fully stub out the dependencies because
        // ComponentService#getLinkVariants uses SubNodeContainer#getTemplateInformation#getSourceURI
        ServiceDependencies.setServiceDependency(LinkVariants.class, new LinkVariants.KnimeUrlResolverVariants());
        var result = cs().getLinkVariants(projectId, getRootID(), new NodeIDEnt(7) );
        assertThat("Returned variants", !result.isEmpty());
    }

}
