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
 *   Nov 14, 2025 (assistant): extracted from WorkflowCommandTestHelper
 */
package org.knime.gateway.testing.helper.webui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThrows;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;
import static org.knime.gateway.api.entity.NodeIDEnt.getRootID;
import static org.knime.gateway.api.util.KnimeUrls.buildLinkVariantEnt;

import java.util.Map.Entry;

import org.hamcrest.core.IsInstanceOf;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ComponentNodeEnt;
import org.knime.gateway.api.webui.entity.LinkVariantEnt;
import org.knime.gateway.api.webui.entity.NodeEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentLinkInformationCommandEnt;
import org.knime.gateway.api.webui.entity.UpdateComponentLinkInformationCommandEnt.UpdateComponentLinkInformationCommandEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt;
import org.knime.gateway.api.webui.entity.WorkflowSnapshotEnt;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.webui.service.ServiceDependencies;
import org.knime.gateway.impl.webui.spaces.LinkVariants;
import org.knime.gateway.testing.helper.ResultChecker;
import org.knime.gateway.testing.helper.ServiceProvider;
import org.knime.gateway.testing.helper.TestWorkflowCollection;
import org.knime.gateway.testing.helper.WorkflowExecutor;
import org.knime.gateway.testing.helper.WorkflowLoader;

/**
 * Dedicated tests for {@link UpdateComponentLinkInformationCommandEnt}.
 */
public class UpdateComponentLinkCommandTestHelper extends WebUIGatewayServiceTestHelper {

    public UpdateComponentLinkCommandTestHelper(final ResultChecker entityResultChecker,
        final ServiceProvider serviceProvider, final WorkflowLoader workflowLoader,
        final WorkflowExecutor workflowExecutor) {
        super(WorkflowCommandTestHelper.class, entityResultChecker, serviceProvider, workflowLoader, workflowExecutor);
    }

    public void testUpdateComponentLinkInformation() throws Exception {
        var projectId = loadWorkflow(TestWorkflowCollection.METANODES_COMPONENTS);
        var linkedComponent = new NodeIDEnt(1);
        var notLinkedComponent = new NodeIDEnt(10);
        var oldLink = "knime://LOCAL/Component/";
        var newLinkVariant = buildLinkVariantEnt(LinkVariantEnt.VariantEnum.WORKFLOW_RELATIVE);
        ServiceDependencies.setServiceDependency(LinkVariants.class, new LinkVariants.KnimeUrlResolverVariants());

        // Test happy path
        var command1 = buildUpdateComponentLinkInformationCommand(linkedComponent, newLinkVariant);
        var nodeBefore = getNodeEntFromWorkflowSnapshotEnt(
            ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.FALSE), linkedComponent);
        assertComponentWithLink(nodeBefore, oldLink);

        ws().executeWorkflowCommand(projectId, getRootID(), command1);
        var nodeAfter = getNodeEntFromWorkflowSnapshotEnt(
            ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.FALSE), linkedComponent);
        assertComponentLinkType(nodeAfter, newLinkVariant.getVariant());
        assertThat(((ComponentNodeEnt)nodeAfter).getLink().getUrl(), startsWith("knime://knime.workflow/"));

        // Test undo command
        ws().undoWorkflowCommand(projectId, getRootID());
        var nodeUndone = getNodeEntFromWorkflowSnapshotEnt(
            ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.FALSE), linkedComponent);
        assertComponentWithLink(nodeUndone, oldLink);

        // Test not a component
        var command2 = buildUpdateComponentLinkInformationCommand(new NodeIDEnt(99), newLinkVariant);
        assertThrows(ServiceCallException.class, () -> ws().executeWorkflowCommand(projectId, getRootID(), command2));

        // Test not a linked component
        var command3 = buildUpdateComponentLinkInformationCommand(notLinkedComponent, newLinkVariant);
        assertThrows(ServiceCallException.class, () -> ws().executeWorkflowCommand(projectId, getRootID(), command3));

        // Test unlink a component
        var command4 = buildUpdateComponentLinkInformationCommand(linkedComponent,
            buildLinkVariantEnt(LinkVariantEnt.VariantEnum.NONE));
        ws().executeWorkflowCommand(projectId, getRootID(), command4);
        var nodeUnlinked = getNodeEntFromWorkflowSnapshotEnt(
            ws().getWorkflow(projectId, NodeIDEnt.getRootID(), null, Boolean.FALSE), linkedComponent);
        assertComponentWithLink(nodeUnlinked, null);
    }

    private static UpdateComponentLinkInformationCommandEnt
        buildUpdateComponentLinkInformationCommand(final NodeIDEnt nodeIdEnt, final LinkVariantEnt linkVariant) {
        return builder(UpdateComponentLinkInformationCommandEntBuilder.class)//
            .setKind(WorkflowCommandEnt.KindEnum.UPDATE_COMPONENT_LINK_INFORMATION)//
            .setNodeId(nodeIdEnt)//
            .setLinkVariant(linkVariant)//
            .build();
    }

    private static NodeEnt getNodeEntFromWorkflowSnapshotEnt(final WorkflowSnapshotEnt workflowSnapshotEnt,
        final NodeIDEnt nodeIdEnt) {
        return workflowSnapshotEnt//
            .getWorkflow()//
            .getNodes()//
            .entrySet()//
            .stream()//
            .filter(entry -> entry.getKey().equals(nodeIdEnt.toString()))//
            .findFirst()//
            .map(Entry::getValue)//
            .orElseThrow();
    }

    private static void assertComponentWithLink(final NodeEnt nodeEnt, final String expectedUrl) {
        assertThat("The node is not a component", nodeEnt, new IsInstanceOf(ComponentNodeEnt.class));
        var link = ((ComponentNodeEnt)nodeEnt).getLink();
        if (expectedUrl == null) {
            assertThat("There should not be a link", link, nullValue());
        } else {
            var actualUrl = ((ComponentNodeEnt)nodeEnt).getLink().getUrl();
            assertThat("The links do not match", actualUrl, equalTo(expectedUrl));
        }
    }

    private static void assertComponentLinkType(final NodeEnt nodeEnt,
        final LinkVariantEnt.VariantEnum expectedVariant) {
        assertThat("The node is not a component", nodeEnt, new IsInstanceOf(ComponentNodeEnt.class));
        if (expectedVariant == LinkVariantEnt.VariantEnum.NONE) {
            assertThat("There should not be a link", ((ComponentNodeEnt)nodeEnt).getLink(), nullValue());
            return;
        }

        var link = ((ComponentNodeEnt)nodeEnt).getLink();
        assertThat("There should be a link", link, notNullValue());
        assertThat("There should be a link type", link.getCurrentLinkVariant(), notNullValue());
        assertThat("Unexpected link type", link.getCurrentLinkVariant().getVariant(), equalTo(expectedVariant));
    }
}
