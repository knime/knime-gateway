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
 *   Aug 15, 2022 (hornm): created
 */
package org.knime.gateway.impl.webui.service.events;

import java.util.Optional;

import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.gateway.api.webui.entity.SelectionEventTypeEnt;
import org.knime.gateway.impl.service.events.EventSource;
import org.knime.gateway.impl.service.events.SelectionEvent;
import org.knime.gateway.impl.service.events.SelectionEventSource;
import org.knime.gateway.impl.service.util.DefaultServiceUtil;
import org.knime.gateway.impl.service.util.EventConsumer;

/**
 * A event source that delegates to the actual {@link SelectionEventSource}-implementation (which is also used by other
 * projects and not just the gateway API implementation). With the delegation of the call it also turns a
 * {@link SelectionEventTypeEnt} into a {@link NativeNodeContainer} as required by the
 * {@link SelectionEventSource}-implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class SelectionEventSourceDelegator extends EventSource<SelectionEventTypeEnt, SelectionEvent> {

    private final SelectionEventSource m_delegate;

    /**
     * @param eventConsumer
     */
    public SelectionEventSourceDelegator(final EventConsumer eventConsumer) {
        super(eventConsumer);
        m_delegate = new SelectionEventSource(eventConsumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SelectionEvent>
        addEventListenerAndGetInitialEventFor(final SelectionEventTypeEnt selectionEventType) {
        return m_delegate.addEventListenerAndGetInitialEventFor(getNNC(selectionEventType));
    }

    private static NativeNodeContainer getNNC(final SelectionEventTypeEnt selectionEventType) {
        return (NativeNodeContainer)DefaultServiceUtil.getNodeContainer(selectionEventType.getProjectId(),
            selectionEventType.getWorkflowId(), selectionEventType.getNodeId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventListener(final SelectionEventTypeEnt selectionEventType) {
        m_delegate.removeEventListener(getNNC(selectionEventType));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllEventListeners() {
        m_delegate.removeAllEventListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getName() {
        return "SelectionEvent";
    }

}
