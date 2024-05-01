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
 *   Oct 6, 2023 (hornm): created
 */
package org.knime.gateway.impl.service.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;

import org.junit.Test;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests for {@link WorkflowChangesListener}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowChangesListenerTest {

    /**
     * Tests that the workflow changes listener is 'listening' (i.e. listeners are registered with the underlying
     * workflow manager etc.) depending on whether callbacks are registered etc.
     *
     * @throws IOException
     */
    @Test
    public void testIsListening() throws IOException {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var workflowChangesListener = new WorkflowChangesListener(wfm);

        assertThat(workflowChangesListener.m_isListening, is(false));

        Runnable callback = () -> {
        };
        workflowChangesListener.addWorkflowChangeCallback(callback);
        assertThat(workflowChangesListener.m_isListening, is(true));
        Runnable callback2 = () -> {
        };
        workflowChangesListener.addWorkflowChangeCallback(callback2);
        assertThat(workflowChangesListener.m_isListening, is(true));

        workflowChangesListener.removeCallback(callback);
        assertThat(workflowChangesListener.m_isListening, is(true));
        workflowChangesListener.removeCallback(callback2);
        assertThat(workflowChangesListener.m_isListening, is(false));

        var tracker = workflowChangesListener.createWorkflowChangeTracker();
        assertThat(workflowChangesListener.m_isListening, is(true));

        workflowChangesListener.removeWorkflowChangesTracker(tracker);
        assertThat(workflowChangesListener.m_isListening, is(false));

        workflowChangesListener.addWorkflowChangeCallback(callback);
        assertThat(workflowChangesListener.m_isListening, is(true));
        workflowChangesListener.close();
        assertThat(workflowChangesListener.m_isListening, is(false));
    }

}
