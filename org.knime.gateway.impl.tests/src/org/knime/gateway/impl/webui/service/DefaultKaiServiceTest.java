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
 *   Feb 26, 2024 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.gateway.impl.webui.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.knime.gateway.api.webui.entity.KaiMessageEnt.RoleEnum;
import org.knime.gateway.impl.webui.entity.DefaultKaiMessageEnt;
import org.knime.gateway.impl.webui.entity.DefaultKaiRequestEnt;
import org.knime.gateway.impl.webui.kai.KaiHandler;
import org.knime.gateway.impl.webui.kai.KaiHandler.CodeAssistant;
import org.knime.gateway.impl.webui.kai.KaiHandler.UiStrings;
import org.knime.gateway.impl.webui.kai.KaiHandler.WelcomeMessages;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Contains unit tests for the {@link DefaultKaiService}.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
@RunWith(MockitoJUnitRunner.class)
public final class DefaultKaiServiceTest extends GatewayServiceTest {

    @Mock
    private KaiHandler m_kaiHandler;

    /**
     * Registers the mock listener in the DefaultKaiService.
     */
    @Before
    public void setup() {
        ServiceDependencies.setServiceDependency(KaiHandler.class, m_kaiHandler);
    }

    /**
     * Tests that the getUiStrings method correctly delegates to the listener.
     *
     * @throws Exception not thrown
     */
    @Test
    public void testGetUiStrings() throws Exception {
        var uiStrings =
            new UiStrings("my disclaimer", new WelcomeMessages("Qa welcome message", "Build welcome message"),
                new CodeAssistant("disclaimer test"));
        Mockito.when(m_kaiHandler.getUiStrings()).thenReturn(uiStrings);
        var returnedUiStrings = DefaultKaiService.getInstance().getUiStrings();
        assertEquals(uiStrings.disclaimer(), returnedUiStrings.getDisclaimer());
        assertEquals(uiStrings.welcomeMessages().qa(), returnedUiStrings.getWelcomeMessages().getQa());
        assertEquals(uiStrings.welcomeMessages().build(), returnedUiStrings.getWelcomeMessages().getBuild());
    }

    /**
     * Tests that the makeAiRequest correctly delegates to the listener.
     *
     * @throws Exception not thrown
     */
    @Test
    public void testMakeAiRequest() throws Exception {
        DefaultKaiMessageEnt message = new DefaultKaiMessageEnt(RoleEnum.USER, "Hello there");
        List<String> selectedNodes = List.of("bli", "bla", "blub");
        var request = new DefaultKaiRequestEnt("foo", "bar", "baz", selectedNodes, List.of(message));

        var expectedRequest = new KaiHandler.Request("foo", "qa", "bar", "baz", selectedNodes,
            List.of(new KaiHandler.Message(KaiHandler.Role.USER, "Hello there")));
        DefaultKaiService.getInstance().makeAiRequest("qa", request);
        Mockito.verify(m_kaiHandler)//
            .onNewRequest(expectedRequest);
    }

    /**
     * Tests that the abortAiRequest correctly delegates to the listener.
     *
     * @throws Exception not thrown
     */
    @Test
    public void testAbortAiRequest() throws Exception {
        DefaultKaiService.getInstance().abortAiRequest("build");
        Mockito.verify(m_kaiHandler).onCancel("build");
    }

}
