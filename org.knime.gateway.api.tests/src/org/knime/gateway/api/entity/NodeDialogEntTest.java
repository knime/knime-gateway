/*
 * ------------------------------------------------------------------------
 *
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
 * History
 *   Mar 23, 2022 (hornm): created
 */
package org.knime.gateway.api.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.knime.testing.node.ui.NodeDialogTestUtil.createNodeDialog;
import static org.knime.testing.node.ui.NodeDialogTestUtil.createNodeSettingsService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.dialog.NodeAndVariableSettingsRO;
import org.knime.core.webui.node.dialog.NodeAndVariableSettingsWO;
import org.knime.core.webui.node.dialog.NodeDialog;
import org.knime.core.webui.node.dialog.NodeSettingsService;
import org.knime.core.webui.node.dialog.SettingsType;
import org.knime.core.webui.page.Page;
import org.knime.testing.node.dialog.NodeDialogNodeFactory;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests {@link NodeDialogEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
class NodeDialogEntTest {

    private WorkflowManager m_wfm;

    @BeforeEach
    void createWorkflow() throws IOException {
        m_wfm = WorkflowManagerUtil.createEmptyWorkflow();
    }

    @AfterEach
    void disposeWorkflow() {
        WorkflowManagerUtil.disposeWorkflow(m_wfm);
    }

    /**
     * Tests that {@link NodeDialogEnt}-instances can be created without problems even if the input ports of a node are
     * not connected.
     */
    @Test
    void testOpenDialogWithoutConnectedInput() {
        var nc = WorkflowManagerUtil.createAndAddNode(m_wfm,
            new NodeDialogNodeFactory(
                () -> createNodeDialog(Page.create().fromString(() -> "test").relativePath("test.html"),
                    createNodeSettingsService(), null),
                1));

        var nodeDialogEnt = new NodeDialogEnt(nc);
        assertThat(nodeDialogEnt.getInitialData()).containsSequence("a default model setting");

    }

    /**
     * Tests {@link NodeDialogEnt#isWriteProtected()}
     *
     * @throws URISyntaxException
     */
    @Test
    void testIsWriteProtected() throws URISyntaxException {
        var metanode = m_wfm.createAndAddSubWorkflow(new PortType[0], new PortType[0], "component");
        var nc = WorkflowManagerUtil.createAndAddNode(metanode, new NodeDialogNodeFactory(
            () -> createNodeDialog(Page.create().fromString(() -> "test").relativePath("test.html"), createNodeSettingsService(), null),
            1));

        var componentId = m_wfm.convertMetaNodeToSubNode(metanode.getID()).getConvertedNodeID();
        assertThat(new NodeDialogEnt(nc).isWriteProtected()).isFalse();

        var component = (SubNodeContainer)m_wfm.getNodeContainer(componentId);
        component.setTemplateInformation(
            MetaNodeTemplateInformation.createNewTemplate(SubNodeContainer.class).createLink(new URI("file://test")));
        var nodeIterator = component.getWorkflowManager().getNodeContainers().iterator();
        nodeIterator.next(); // component input
        nodeIterator.next(); // component output
        var snc = (SingleNodeContainer) nodeIterator.next();

        assertThat(new NodeDialogEnt(snc).isWriteProtected()).isTrue();
    }

    /**
     * Tests {@link NodeDialogEnt#isCanBeEnlarged()}
     */
    @Test
    void testCanBeEnlarged() {
        var canBeEnlarged = new AtomicBoolean();
        var nc = WorkflowManagerUtil.createAndAddNode(m_wfm, new NodeDialogNodeFactory(() -> new NodeDialog() {

            @Override
            public Page getPage() {
                return Page.create().fromString(() -> "blub").relativePath("index.html");
            }

            @Override
            public Set<SettingsType> getSettingsTypes() {
                return Set.of(SettingsType.MODEL);
            }

            @Override
            public NodeSettingsService getNodeSettingsService() {
                return new NodeSettingsService() {

                    @Override
                    public String fromNodeSettings(final Map<SettingsType, NodeAndVariableSettingsRO> settings,
                        final PortObjectSpec[] specs) {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public void toNodeSettings(final String textSettings,
                        final Map<SettingsType, NodeAndVariableSettingsRO> previousSettings,
                        final Map<SettingsType, NodeAndVariableSettingsWO> settings) {
                        throw new UnsupportedOperationException();
                    }

                };
            }

            @Override
            public Optional<RpcDataService> createRpcDataService() {
                return Optional.empty();
            }

            @Override
            public boolean canBeEnlarged() {
                return canBeEnlarged.get();
            }
        }));
        assertThat(new NodeDialogEnt(nc).isCanBeEnlarged()).isFalse();

        canBeEnlarged.set(true);
        assertThat(new NodeDialogEnt(nc).isCanBeEnlarged()).isTrue();
    }

}
