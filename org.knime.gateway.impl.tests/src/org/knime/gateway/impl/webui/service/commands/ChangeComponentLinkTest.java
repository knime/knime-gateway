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
 * ---------------------------------------------------------------------
 */
package org.knime.gateway.impl.webui.service.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.math.BigDecimal;
import java.net.URI;

import org.junit.Test;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.util.hub.ItemVersion;
import org.knime.core.util.urlresolve.URLResolverUtil;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.api.webui.entity.ChangeComponentLinkCommandEnt;
import org.knime.gateway.api.webui.entity.ChangeComponentLinkCommandEnt.ChangeComponentLinkCommandEntBuilder;
import org.knime.gateway.api.webui.entity.ItemVersionEnt;
import org.knime.gateway.api.webui.entity.SpecificVersionEnt.SpecificVersionEntBuilder;
import org.knime.gateway.api.webui.entity.WorkflowCommandEnt.KindEnum;
import org.knime.gateway.impl.webui.WorkflowKey;

/**
 * Tests {@link ChangeComponentLink}.
 */
public class ChangeComponentLinkTest {

    @Test
    public void testChangeComponentLinkUpdatesVersion() throws Exception {
        var wp = WorkflowCommandsTest.createEmptyWorkflowProject();
        var wfm = wp.getFromCacheOrLoadWorkflowManager().orElseThrow();

        var metanode = wfm.createAndAddSubWorkflow(new PortType[0], new PortType[0], "component");
        var componentId = wfm.convertMetaNodeToSubNode(metanode.getID()).getConvertedNodeID();
        var component = (SubNodeContainer)wfm.getNodeContainer(componentId);

        var initialUri = new URI("knime://My-Hub/Users/test/component?version=1");
        component.setTemplateInformation(
            MetaNodeTemplateInformation.createNewTemplate(SubNodeContainer.class).createLink(initialUri));

        var requestedVersion = builder(SpecificVersionEntBuilder.class)
            .setType(ItemVersionEnt.TypeEnum.SPECIFIC_VERSION)
            .setVersion(Integer.valueOf(5))
            .build();

        ChangeComponentLinkCommandEnt commandEnt = builder(ChangeComponentLinkCommandEntBuilder.class) //
            .setKind(KindEnum.CHANGE_COMPONENT_LINK) //
            .setNodeId(new NodeIDEnt(component.getID())) //
            .setItemVersion(requestedVersion) //
            .build();

        var command = new ChangeComponentLink(commandEnt);
        command.execute(new WorkflowKey(wp.getID(), NodeIDEnt.getRootID()));

        var updatedUri = component.getTemplateInformation().getSourceURI();
        var updatedVersion = URLResolverUtil.parseVersion(updatedUri.getQuery()).orElse(ItemVersion.currentState());
        assertThat(updatedVersion, is(ItemVersion.of(5)));

        WorkflowCommandsTest.disposeWorkflowProject(wp);
    }
}
