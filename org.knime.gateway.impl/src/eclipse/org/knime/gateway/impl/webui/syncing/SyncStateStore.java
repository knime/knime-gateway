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
 *   Dec 4, 2025 (motacilla): created
 */
package org.knime.gateway.impl.webui.syncing;

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.knime.core.node.NodeLogger;
import org.knime.gateway.api.webui.entity.SyncStateEnt;
import org.knime.gateway.api.webui.entity.SyncStateEnt.SyncStateEntBuilder;
import org.knime.gateway.api.webui.entity.SyncStateErrorEnt;
import org.knime.gateway.api.webui.entity.SyncStateErrorEnt.SyncStateErrorEntBuilder;

/**
 * Stores the current sync state of a project and notifies a callback on updates.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
final class SyncStateStore {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(SyncStateStore.class);

    private final Set<Runnable> m_onStateChangeListeners = Collections.synchronizedSet(new HashSet<>());

    private SyncStateEnt.StateEnum m_state = SyncStateEnt.StateEnum.SYNCED;

    private boolean m_autoSyncEnabled = true;

    private Error m_error;

    private boolean m_locked;

    private Runnable m_onUnlock = () -> {
    };

    synchronized SyncStateEnt.StateEnum state() {
        return m_state;
    }

    synchronized boolean isAutoSyncEnabled() {
        return m_autoSyncEnabled;
    }

    synchronized void deferStateChanges() {
        LOGGER.info("Locking SyncStateStore");
        m_locked = true;
    }

    synchronized void allowStateChanges() {
        LOGGER.info("Unlocking SyncStateStore");
        m_locked = false;
        m_onUnlock.run();
        m_onUnlock = () -> {
        };
    }

    synchronized SyncStateEnt buildSyncStateEnt() {
        final var errorEnt =
            m_error == null ? null : buildSyncStateErrorEnt(m_error, m_state == SyncStateEnt.StateEnum.ERROR);
        return builder(SyncStateEntBuilder.class) //
            .setState(m_state) //
            .setIsAutoSyncEnabled(m_autoSyncEnabled) //
            .setError(errorEnt) //
            .build();

    }

    private static SyncStateErrorEnt buildSyncStateErrorEnt(final Error details, final boolean canCopy) {
        return builder(SyncStateErrorEntBuilder.class) //
            .setCode(details.code()) //
            .setTitle(details.title()) //
            .setCanCopy(canCopy) //
            .setStackTrace(details.stackTrace()) //
            .build();
    }

    /**
     * When the store is locked, the update will be deferred until it is unlocked. This can happen when
     * {@link WorkflowSyncer#notifyWorkflowChanged()} is called during an ongoing workflow upload.
     *
     */
    synchronized void changeStateDeferrable(final SyncStateEnt.StateEnum newState) {
        if (m_locked) {
            // We defer the update until allowStateChanges
            m_onUnlock = () -> changeState(newState);
            return;
        }
        changeState(newState);
    }

    synchronized void changeState(final SyncStateEnt.StateEnum state) {
        changeState(state, null);
    }

    synchronized void changeState(final SyncStateEnt.StateEnum state, final Error error) {
        changeState(state, error, m_autoSyncEnabled);
    }

    synchronized void changeState(final SyncStateEnt.StateEnum state, final Error error,
        final boolean autoSyncEnabled) {
        m_state = state;
        m_autoSyncEnabled = autoSyncEnabled;
        m_error = error;
        m_onStateChangeListeners.forEach(Runnable::run);
    }

    synchronized void reset() {
        m_autoSyncEnabled = true;
        m_error = null;
    }

    void addOnStateChangeListener(final Runnable listener) {
        m_onStateChangeListeners.add(listener);
    }

    void removeOnStateChangeListener(final Runnable listener) {
        m_onStateChangeListeners.remove(listener);
    }

    record Error(String code, String title, String stackTrace) {

        Error(final Exception e) {
            this( //
                e.getClass().getSimpleName(), //
                e.getMessage(), //
                Arrays.stream(e.getStackTrace()) //
                    .map(StackTraceElement::toString) //
                    .collect(Collectors.joining("\n") //
                    ) //
            );
        }
    }
}
