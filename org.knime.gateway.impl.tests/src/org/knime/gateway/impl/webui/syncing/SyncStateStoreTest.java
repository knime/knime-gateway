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
 *   Dec 23, 2025 (assistant): added
 */
package org.knime.gateway.impl.webui.syncing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.knime.gateway.api.webui.entity.ProjectSyncStateEnt;

/**
 * Unit tests for {@link SyncStateStore} focusing on deferring state changes.
 */
public class SyncStateStoreTest {

    /**
     * Verifies deferred state changes are applied when unlocking.
     */
    @Test
    public void testDefersStateChangeWhileLockedAndAppliesOnUnlock() {
        var stateChangeCalls = new AtomicInteger();
        var store = new SyncStateStore(stateChangeCalls::incrementAndGet);

        store.deferStateChanges();
        store.changeStateDeferrable(ProjectSyncStateEnt.StateEnum.ERROR);

        assertThat(store.state()).isEqualTo(ProjectSyncStateEnt.StateEnum.SYNCED);
        assertThat(stateChangeCalls.get()).isZero();

        store.allowStateChanges();

        assertThat(store.state()).isEqualTo(ProjectSyncStateEnt.StateEnum.ERROR);
        assertThat(stateChangeCalls.get()).isEqualTo(1);
    }

    /**
     * Verifies the last queued deferred state wins when multiple are present.
     */
    @Test
    public void testLastDeferredStateWinsWhenMultipleQueued() {
        var stateChangeCalls = new AtomicInteger();
        var store = new SyncStateStore(stateChangeCalls::incrementAndGet);

        store.deferStateChanges();
        store.changeStateDeferrable(ProjectSyncStateEnt.StateEnum.DIRTY);
        store.changeStateDeferrable(ProjectSyncStateEnt.StateEnum.ERROR);

        store.allowStateChanges();
        store.allowStateChanges(); // a second unlock should have no effect

        assertThat(store.state()).isEqualTo(ProjectSyncStateEnt.StateEnum.ERROR);
        assertThat(stateChangeCalls.get()).isEqualTo(1);
    }
}
