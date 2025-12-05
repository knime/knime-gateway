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
 */

package org.knime.gateway.impl.project;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.knime.gateway.api.util.VersionId;

class ProjectWfmCacheTest {

    @Test
    void testLoadsCurrentStateOnlyOnceAndCaches() throws Exception {
        var loader = countingLoader();
        var cache = new ProjectWfmCache(loader);

        var first = cache.getWorkflowManager(VersionId.currentState());
        var second = cache.getWorkflowManager(VersionId.currentState());

        assertThat(first).isSameAs(second);
        assertThat(loader.m_calls.get()).isEqualTo(1);
    }

    @Test
    void testLoadsFixedVersionOnceCachesAndReportsContains() {
        var loader = countingLoader();
        var cache = new ProjectWfmCache(loader);
        var v1 = VersionId.parse("1");

        var first = cache.getWorkflowManager(v1);
        var second = cache.getWorkflowManager(v1);

        assertThat(first).isSameAs(second);
        assertThat(cache.contains(v1)).isTrue();
        assertThat(loader.m_calls.get()).isEqualTo(1); // only fixed version was loaded
    }

    @Test
    void testGetWorkflowManagerIfLoadedReflectsLoadingState() {
        var loader = countingLoader();
        var cache = new ProjectWfmCache(loader);
        var fixed = VersionId.parse("2");

        assertThat(cache.getWorkflowManagerIfLoaded(fixed)).isEmpty();
        cache.getWorkflowManager(fixed);
        assertThat(cache.getWorkflowManagerIfLoaded(fixed)).isPresent();
    }

    @Test
    void testDisposeClearsEntriesAndAllowsReload() {
        var loader = countingLoader();
        var cache = new ProjectWfmCache(loader);
        var fixed = VersionId.parse("3");

        cache.getWorkflowManager(VersionId.currentState());
        cache.getWorkflowManager(fixed);
        assertThat(loader.m_calls.get()).isEqualTo(2);

        cache.dispose(VersionId.currentState());
        cache.dispose(fixed);

        assertThat(cache.contains(VersionId.currentState())).isFalse();
        assertThat(cache.contains(fixed)).isFalse();

        cache.getWorkflowManager(VersionId.currentState());
        cache.getWorkflowManager(fixed);
        assertThat(loader.m_calls.get()).isEqualTo(4);
    }

    @Test
    void testDisposeAllClearsFixedVersionsAndCurrent() {
        var loader = countingLoader();
        var cache = new ProjectWfmCache(loader);
        var fixed = VersionId.parse("4");

        cache.getWorkflowManager(VersionId.currentState());
        cache.getWorkflowManager(fixed);
        cache.dispose();

        assertThat(cache.contains(VersionId.currentState())).isFalse();
        assertThat(cache.contains(fixed)).isFalse();
    }

    @Test
    void testLruEvictsOldestFixedVersion() {
        var loader = countingLoader();
        var cache = new ProjectWfmCache(loader);

        // load 6 versions -> capacity is 5
        for (var i = 0; i < 6; i++) {
            cache.getWorkflowManager(VersionId.parse(Integer.toString(i)));
        }

        assertThat(cache.contains(VersionId.parse("0"))).isFalse(); // evicted oldest
        assertThat(cache.contains(VersionId.parse("5"))).isTrue();
    }

    @Test
    void testContains() throws Exception {
        var instance = new ProjectWfmCache(ignored -> null);
        Assertions.assertFalse(instance.contains(VersionId.currentState()));
        Assertions.assertFalse(instance.contains(someVersion()));
        instance.getWorkflowManager(VersionId.currentState());
        Assertions.assertTrue(instance.contains(VersionId.currentState()));
        Assertions.assertFalse(instance.contains(someVersion()));
        instance.getWorkflowManager(VersionId.currentState());
        Assertions.assertTrue(instance.contains(VersionId.currentState()));
    }

    private static VersionId someVersion() {
        return VersionId.parse("13");
    }

    private static class CountingLoader implements WorkflowManagerLoader {
        final AtomicInteger m_calls = new AtomicInteger();
        final Map<VersionId, org.knime.core.node.workflow.WorkflowManager> m_cache = new HashMap<>();

        @Override
        public org.knime.core.node.workflow.WorkflowManager load(final VersionId version) {
            m_calls.incrementAndGet();
            return m_cache.computeIfAbsent(version, v -> org.mockito.Mockito.mock(
                org.knime.core.node.workflow.WorkflowManager.class));
        }
    }

    private static CountingLoader countingLoader() {
        return new CountingLoader();
    }
}
