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
 *   Mar 6, 2024 (Adrian Nembach, KNIME GmbH, Konstanz, Germany): created
 */
package org.knime.gateway.impl.webui.kai;

import java.util.List;

/**
 *
 * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
 */
public interface KaiHandler {

    /**
     * Invoked when the user sends a message in the chat.
     *
     * @param request the user request
     */
    void onNewRequest(Request request);

    /**
     * Invoked if the user cancels the answer of the currently processed message.
     *
     * @param chainType the type of chain to cancel
     */
    void onCancel(String chainType);

    /**
     * @return the UI strings (disclaimer and welcome messages) in JSON format
     */
    UiStrings getUiStrings();

    /**
     * Invoked if the user provides feedback on one of K-AI's answers.
     * @param kaiFeedbackId ID of the feedback
     * @param projectId ID of the top-level workflow
     * @param isPositive whether the feedback is positive or negative
     * @param comment the user provided
     * @param kaiFeedback Id of the feedback
     */
    void onFeedback(String kaiFeedbackId, String projectId, boolean isPositive, String comment);

    /**
     * Encapsulates the welcome messages that K-AI displays in the UI.
     *
     * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
     * @param qa welcome message for the Q&A mode
     * @param build welcome message for the Build mode
     */
    record WelcomeMessages(String qa, String build) {
    }

    /**
     * Container for the disclaimer and welcome messages that K-AI displays in the UI.
     *
     * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
     * @param disclaimer users have to accept before they can chat with K-AI
     * @param welcomeMessages the messages K-AI starts the conversation with
     */
    record UiStrings(String disclaimer, WelcomeMessages welcomeMessages) {
    }

    /**
     * Represents a message in a conversation with K-AI
     *
     * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
     * @param role who the message is from
     * @param content of the message
     */
    record Message(Role role, String content) {
    }

    /**
     * Role of the sender of the message.
     *
     * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
     */
    enum Role {
        USER("user"),
        ASSISTANT("assistant");

        private String m_toString;

        Role(final String toString) {
            m_toString = toString;
        }

        @Override
        public String toString() {
            return m_toString;
        }
    }

    /**
     * Represents a user request sent to K-AI.
     *
     * @author Adrian Nembach, KNIME GmbH, Konstanz, Germany
     * @param conversationId ID of the conversation (null at start of conversation)
     * @param chainType i.e. qa or build mode
     * @param projectId ID of the workflow the user is interacting with
     * @param workflowId ID of the subworkflow the user is interacting with
     * @param selectedNodes IDs of the nodes the user selected
     * @param messages of the conversation
     */
    record Request(String conversationId, String chainType, String projectId, String workflowId,
        List<String> selectedNodes, List<Message> messages) {
    }
}
