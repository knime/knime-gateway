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
 *   Nov 9, 2023 (hornm): created
 */
package org.knime.gateway.impl.webui.service.events;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

import org.knime.core.node.extension.NodeSpecCollectionProvider;
import org.knime.core.node.extension.NodeSpecCollectionProvider.Progress;
import org.knime.core.node.extension.NodeSpecCollectionProvider.Progress.ProgressEvent;
import org.knime.core.node.extension.NodeSpecCollectionProvider.Progress.ProgressListener;
import org.knime.gateway.api.webui.entity.NodeRepositoryLoadingProgressEventEnt;
import org.knime.gateway.api.webui.entity.NodeRepositoryLoadingProgressEventEnt.NodeRepositoryLoadingProgressEventEntBuilder;
import org.knime.gateway.api.webui.entity.NodeRepositoryLoadingProgressEventTypeEnt;

/**
 * Event source emitting {@link NodeRepositoryLoadingProgressEventEnt}s.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class NodeRepositoryLoadingProgressEventSource
    extends EventSource<NodeRepositoryLoadingProgressEventTypeEnt, NodeRepositoryLoadingProgressEventEnt> {

    private static final long EVENT_INTERVAL_IN_MS = 200;

    private ProgressListener m_listener;

    private LastEventInfo m_lastEventInfo;

    /**
     * @param eventConsumer
     */
    public NodeRepositoryLoadingProgressEventSource(final EventConsumer eventConsumer) {
        super(eventConsumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<NodeRepositoryLoadingProgressEventEnt> addEventListenerAndGetInitialEventFor(
        final NodeRepositoryLoadingProgressEventTypeEnt eventTypeEnt, final String projectId) {
        if (Progress.isDone()) {
            removeAllEventListeners();
        } else if (m_listener == null) {
            m_listener = this::handleNodeSpecCollectionProviderProgressEvent;
            NodeSpecCollectionProvider.Progress.addListener(m_listener);
        }
        return Optional.empty();
    }

    private void handleNodeSpecCollectionProviderProgressEvent(final ProgressEvent progressEvent) {
        var now = System.currentTimeMillis();
        if (m_lastEventInfo == null || !Objects.equals(m_lastEventInfo.extensionName, progressEvent.extensionName())
            || m_lastEventInfo.time + EVENT_INTERVAL_IN_MS < now) {
            sendEvent(builder(NodeRepositoryLoadingProgressEventEntBuilder.class)
                .setProgress(BigDecimal.valueOf(progressEvent.overallProgress()))
                .setExtensionName(progressEvent.extensionName()).build(), null);
            m_lastEventInfo = new LastEventInfo(progressEvent.extensionName(), now);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventListener(final NodeRepositoryLoadingProgressEventTypeEnt eventTypeEnt,
        final String projectId) {
        removeAllEventListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllEventListeners() {
        if (m_listener != null) {
            NodeSpecCollectionProvider.Progress.removeListener(m_listener);
            m_listener = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getName() {
        return "NodeRepositoryLoadingProgressEvent";
    }

    private record LastEventInfo(String extensionName, long time) {
    }

}
