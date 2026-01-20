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
import java.util.function.Consumer;

import org.knime.core.node.workflow.NodeContext;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.gateway.api.webui.entity.KaiFeedbackEnt;
import org.knime.gateway.api.webui.entity.KaiInquiryResponseEnt;
import org.knime.gateway.api.webui.entity.KaiMessageEnt.RoleEnum;
import org.knime.gateway.api.webui.entity.KaiQuickActionRequestEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionResponseEnt;
import org.knime.gateway.api.webui.entity.KaiQuickActionsAvailableEnt;
import org.knime.gateway.api.webui.entity.KaiRequestEnt;
import org.knime.gateway.api.webui.entity.KaiUiStringsEnt;
import org.knime.gateway.api.webui.entity.KaiUsageEnt;
import org.knime.gateway.api.webui.service.KaiService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NodeNotFoundException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NotASubWorkflowException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.ServiceCallException;
import org.knime.gateway.impl.project.Project;
import org.knime.gateway.impl.project.ProjectManager;
import org.knime.gateway.impl.webui.WorkflowKey;
import org.knime.gateway.impl.webui.WorkflowMiddleware;
import org.knime.gateway.impl.webui.WorkflowUtil;
import org.knime.gateway.impl.webui.entity.DefaultKaiUiStringsEnt;
import org.knime.gateway.impl.webui.entity.DefaultKaiWelcomeMessagesEnt;
import org.knime.gateway.impl.webui.kai.KaiHandler;
import org.knime.gateway.impl.webui.kai.KaiHandler.KaiCommand;
import org.knime.gateway.impl.webui.kai.KaiHandler.KaiCommandExecutor;
import org.knime.gateway.impl.webui.kai.KaiHandler.Position;
import org.knime.gateway.impl.webui.kai.KaiHandler.UiStrings;
import org.knime.gateway.impl.webui.service.commands.AbstractWorkflowCommand;
import org.knime.gateway.impl.webui.service.commands.WorkflowCommand;

/**
 * Receives calls from the frontend and delegates them to a {@link KaiHandler}.
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultKaiService implements KaiService {

    private final class KaiCommandExecutorImpl implements KaiCommandExecutor {
        private final WorkflowKey m_workflowKey;

        private KaiCommandExecutorImpl(final WorkflowKey workflowKey) {
            m_workflowKey = workflowKey;
        }

        @Override
        public void execute(final KaiCommand kaiCommand) throws ServiceCallException {
            WorkflowCommand command = new KaiCommandAdapter(kaiCommand);
            var commands = m_workflowMiddleware.getCommands();
            commands.setCommandToExecute(command);
            commands.execute(m_workflowKey, null);
        }

        @Override
        public void executeWorkflowAction(final Consumer<WorkflowManager> workflowAction)
            throws NodeNotFoundException, NotASubWorkflowException {
            var wfm = WorkflowUtil.getWorkflowManager(m_workflowKey);
            workflowAction.accept(wfm);
        }
    }

    private static class KaiCommandAdapter extends AbstractWorkflowCommand {

        private final KaiCommand m_kaiCommand;

        KaiCommandAdapter(final KaiCommand kaiCommand) {
            m_kaiCommand = kaiCommand;
        }

        @Override
        protected boolean executeWithWorkflowLockAndContext() throws ServiceCallException {
            return m_kaiCommand.execute(getWorkflowManager());
        }

        @Override
        public void undo() {
            m_kaiCommand.undo(getWorkflowManager());
        }

        @Override
        public void redo() {
            m_kaiCommand.execute(getWorkflowManager());
        }

    }

    private final KaiHandler m_kaiHandler = ServiceDependencies.getServiceDependency(KaiHandler.class, false);

    private final WorkflowMiddleware m_workflowMiddleware =
        ServiceDependencies.getServiceDependency(WorkflowMiddleware.class, true);

    private final ProjectManager m_projectManager =
        ServiceDependencies.getServiceDependency(ProjectManager.class, true);

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
        var projectId = kaiRequestEnt.getProjectId();
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        var workflowKey = new WorkflowKey(kaiRequestEnt.getProjectId(), kaiRequestEnt.getWorkflowId());
        var commandExecutor = new KaiCommandExecutorImpl(workflowKey);

        var messages = kaiRequestEnt.getMessages().stream()//
            .map(m -> new KaiHandler.Message(fromRoleEnum(m.getRole()), m.getContent()))//
            .toList();

        var startPosition = kaiRequestEnt.getStartPosition();
        var request = new KaiHandler.Request(kaiRequestEnt.getConversationId(), kaiChainId, projectId, commandExecutor,
            kaiRequestEnt.getSelectedNodes(), messages,
            startPosition == null ? null : new Position(startPosition.getX(), startPosition.getY()));
        try (var wfScope = getWfScope(request.projectId())) {
            getListener().ifPresent(l -> l.onNewRequest(request));
        }
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
        try (var wfScope = getWfScope(kaiFeedback.getProjectId())) {
            getListener().ifPresent(l -> l.onFeedback(kaiFeedbackId, kaiFeedback.getProjectId(),
                kaiFeedback.isPositive(), kaiFeedback.getComment()));
        }
    }

    @Override
    public KaiUsageEnt getUsage(final String projectId) {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        try (var wfScope = getWfScope(projectId)) {
            return getListener().map(l -> l.getUsage(projectId)).orElse(null);
        }
    }

    @Override
    public KaiQuickActionResponseEnt executeQuickAction(final String kaiQuickActionId,
        final KaiQuickActionRequestEnt kaiQuickActionRequest) {
        DefaultServiceContext.assertWorkflowProjectId(kaiQuickActionRequest.getProjectId());
        try (var wfScope = getWfScope(kaiQuickActionRequest.getProjectId())) {
            return getListener().map(l -> l.executeQuickAction(kaiQuickActionId, kaiQuickActionRequest)).orElse(null);
        }
    }

    @Override
    public KaiQuickActionsAvailableEnt listQuickActions(final String projectId) {
        DefaultServiceContext.assertWorkflowProjectId(projectId);
        try (var wfScope = getWfScope(projectId)) {
            return getListener().map(l -> l.listQuickActions(projectId)).orElse(null);
        }
    }

    @Override
    public void respondToInquiry(final String kaiChainId, final KaiInquiryResponseEnt kaiInquiryResponse) {
        DefaultServiceContext.assertWorkflowProjectId(kaiInquiryResponse.getProjectId());
        try (var wfScope = getWfScope(kaiInquiryResponse.getProjectId())) {
            getListener().ifPresent(l -> l.onInquiryResponse(kaiChainId, kaiInquiryResponse));
        }
    }

    private WorkflowManagerScope getWfScope(final String projectId) {
        return new WorkflowManagerScope(m_projectManager.getProject(projectId)//
            .flatMap(Project::getWorkflowManagerIfLoaded)//
            .orElseThrow());
    }

    private static final class WorkflowManagerScope implements AutoCloseable {

        WorkflowManagerScope(final WorkflowManager wfm) {
            NodeContext.pushContext(wfm);
        }

        @Override
        public void close() {
            NodeContext.removeLastContext();
        }
    }

}
