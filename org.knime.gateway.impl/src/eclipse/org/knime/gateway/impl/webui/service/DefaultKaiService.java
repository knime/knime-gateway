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
 *   Feb 23, 2024 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.gateway.impl.webui.service;

import java.util.Optional;

import org.knime.gateway.api.webui.entity.KaiFeedbackEnt;
import org.knime.gateway.api.webui.entity.KaiMessageEnt.RoleEnum;
import org.knime.gateway.api.webui.entity.KaiRequestEnt;
import org.knime.gateway.api.webui.entity.KaiUiStringsEnt;
import org.knime.gateway.api.webui.service.KaiService;
import org.knime.gateway.impl.webui.entity.DefaultKaiUiStringsEnt;
import org.knime.gateway.impl.webui.entity.DefaultKaiWelcomeMessagesEnt;
import org.knime.gateway.impl.webui.kai.KaiHandler;
import org.knime.gateway.impl.webui.kai.KaiHandler.Position;
import org.knime.gateway.impl.webui.kai.KaiHandler.UiStrings;

/**
 * Receives calls from the frontend and delegates them to a {@link KaiHandler}.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultKaiService implements KaiService {

    private final KaiHandler m_kaiHandler = ServiceDependencies.getServiceDependency(KaiHandler.class, false);

    /**
     * @return the singleton instance of this service
     */
    public static DefaultKaiService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultKaiService.class);
    }

    private Optional<KaiHandler> getListener() {
        return Optional.ofNullable(m_kaiHandler);
    }

    @Override
    public void abortAiRequest(final String kaiChainId) {
        getListener().ifPresent(l -> l.onCancel(kaiChainId));
    }

    @Override
    public KaiUiStringsEnt getUiStrings() {
        return getListener().map(KaiHandler::getUiStrings).map(DefaultKaiService::fromUiStrings).orElse(null);
    }

    private static KaiUiStringsEnt fromUiStrings(final UiStrings uiStrings) {
        var welcomeMessages = uiStrings.welcomeMessages();
        return new DefaultKaiUiStringsEnt(uiStrings.disclaimer(),
            new DefaultKaiWelcomeMessagesEnt(welcomeMessages.qa(), welcomeMessages.build()));
    }

    @Override
    public void makeAiRequest(final String kaiChainId, final KaiRequestEnt kaiRequestEnt) {
        var messages = kaiRequestEnt.getMessages().stream()//
            .map(m -> new KaiHandler.Message(fromRoleEnum(m.getRole()), m.getContent())).toList();
        var startPosition = kaiRequestEnt.getStartPosition();
        var request = new KaiHandler.Request(kaiRequestEnt.getConversationId(), kaiChainId,
            kaiRequestEnt.getProjectId(), kaiRequestEnt.getWorkflowId(), kaiRequestEnt.getSelectedNodes(), messages,
            startPosition == null ? null : new Position(startPosition.getX(), startPosition.getY()));
        getListener().ifPresent(l -> l.onNewRequest(request));
    }

    private static KaiHandler.Role fromRoleEnum(final RoleEnum role) {
        return switch (role) {
            case ASSISTANT -> KaiHandler.Role.ASSISTANT;
            case USER -> KaiHandler.Role.USER;
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }

    @Override
    public void submitFeedback(final String kaiFeedbackId, final KaiFeedbackEnt kaiFeedback) {
        getListener().ifPresent(l -> l.onFeedback(kaiFeedbackId, kaiFeedback.getProjectId(), kaiFeedback.isPositive(),
            kaiFeedback.getComment()));
    }

}
