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
package org.knime.gateway.api.webui.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knime.core.node.NodeAndBundleInformationPersistor;
import org.knime.core.node.missing.MissingNodeFactory;
import org.knime.core.node.missing.MissingNodeFactory.Reason;
import org.knime.core.node.port.PortType;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt;
import org.knime.gateway.api.webui.entity.NativeNodeInvariantsEnt.TypeEnum;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests {@link WorkflowEntityFactory}.
 */
class WorkflowEntityFactoryTest {

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
    void testMissingNodeTypeDefaultReason() {
        var info = new NodeAndBundleInformationPersistor(MissingNodeFactory.class.getName());
        var factory = new MissingNodeFactory(info, null, new PortType[]{}, new PortType[]{});
        assertThat(factory.getReason()).isEqualTo(Reason.MISSING_EXTENSION);
        var missingNnc = WorkflowManagerUtil.createAndAddNode(m_wfm, factory);
        var resultingEntity = WorkflowEntityFactory.buildNativeNodeInvariantsEnt(missingNnc);
        assertThat(resultingEntity.getType()).isEqualTo(NativeNodeInvariantsEnt.TypeEnum.MISSING);
    }

    @Test
    void testMissingNodeTypeForbidden() {
        var info = new NodeAndBundleInformationPersistor(MissingNodeFactory.class.getName());
        var factory = new MissingNodeFactory(info, null, new PortType[]{}, new PortType[]{},
            MissingNodeFactory.Reason.GOVERNANCE_FORBIDDEN);
        assertThat(factory.getReason()).isEqualTo(Reason.GOVERNANCE_FORBIDDEN);
        var missingNnc = WorkflowManagerUtil.createAndAddNode(m_wfm, factory);
        var resultingEntity = WorkflowEntityFactory.buildNativeNodeInvariantsEnt(missingNnc);
        assertThat(resultingEntity.getType()).isEqualTo(TypeEnum.FORBIDDEN);
    }

    @Test
    void testCreateIconDataURL() throws MalformedURLException {
        var dataUrl = WorkflowEntityFactory
            .createIconDataURL(getClass().getResource("/files/testflows/ContainerNodes/workflow.svg"));
        assertThat(dataUrl).isNotNull().startsWith("data:image/png;base64,");

        var fileUrl = new URL("file://test/blub.svg");
        dataUrl = WorkflowEntityFactory.createIconDataURL(fileUrl);
        assertThat(dataUrl).isNull();

        var bundleresourceUrl = new URL("bundleresource://1234/blub.svg");
        dataUrl = WorkflowEntityFactory.createIconDataURL(bundleresourceUrl);
        assertThat(dataUrl).isNull();

        var forbiddenUrl = new URL("http://blub.com");
        assertThrows(IllegalStateException.class, () -> WorkflowEntityFactory.createIconDataURL(forbiddenUrl));
    }
}
