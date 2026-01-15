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
package org.knime.gateway.api.webui.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.ItemVersionEnt;
import org.knime.gateway.api.webui.entity.LinkVariantEnt;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt;
import org.knime.gateway.api.webui.entity.TemplateLinkEnt;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests {@link TemplateLinkEntityFactory}.
 */
class TemplateLinkEntityFactoryTest {

    private WorkflowManager m_wfm;

    @BeforeEach
    void createEmptyWorkflow() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
    }

    @AfterEach
    void disposeWorkflow() {
        WorkflowManagerUtil.disposeWorkflow(m_wfm);
    }

    @Test
    void testBuildTemplateLinkEntFromWorkflow() throws URISyntaxException {
        var metanode = m_wfm.createAndAddSubWorkflow(new PortType[0], new PortType[0], "component");
        var componentId = m_wfm.convertMetaNodeToSubNode(metanode.getID()).getConvertedNodeID();
        var component = (SubNodeContainer)m_wfm.getNodeContainer(componentId);

        var linkUri = new URI("knime://My-Hub/Users/test/component?version=most-recent");
        component.setTemplateInformation(
            MetaNodeTemplateInformation.createNewTemplate(SubNodeContainer.class).createLink(linkUri));

        var linkEnt = TemplateLinkEntityFactory.buildTemplateLinkEnt( //
            component, //
            s -> Optional.of(SpaceProviderEnt.TypeEnum.HUB) //
        );

        assertThat(linkEnt).isNotNull();
        assertThat(linkEnt.getUrl()).isEqualTo(linkUri.toString());
        assertThat(linkEnt.getUpdateStatus()).isEqualTo(TemplateLinkEnt.UpdateStatusEnum.UP_TO_DATE);
        assertThat(linkEnt.isLinkVariantChangeable()).isTrue();
        assertThat(linkEnt.getIsHubItemVersionChangeable()).isEqualTo(Boolean.TRUE);
        assertThat(linkEnt.getTargetHubItemVersion().getType()).isEqualTo(ItemVersionEnt.TypeEnum.MOST_RECENT);
        assertThat(linkEnt.getCurrentLinkVariant()).isNotNull();
        assertThat(linkEnt.getCurrentLinkVariant().getVariant())
            .isEqualTo(LinkVariantEnt.VariantEnum.MOUNTPOINT_ABSOLUTE_PATH);
    }
}
