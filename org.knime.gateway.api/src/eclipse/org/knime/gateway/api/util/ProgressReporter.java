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

package org.knime.gateway.api.util;

import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.knime.core.node.NodeLogger;

/**
 * The task needs to be executed inside the reporter (i.e. given as lambda) s.t. the reporter is able to catch
 * exceptions and notify accordingly.
 * <p>
 * Can be moved to knime-gateway and become a ProgressService at some point. To extend for proper error handling, see
 * {@code DesktopAPUtil#runWithProgress(String, NodeLogger, FunctionWithProgress)}.
 *
 */
public interface ProgressReporter {

    public static final ProgressReporter NO_OP = new NullProgressReporter();

    /**
     * Implementations are to provide a {@link IProgressMonitor} to the {@code task} to which it will report its
     * progress.
     *
     * @param name -
     * @param logger -
     * @param task -
     * @return The result of the computation, if successful.
     * @param <R> The result type
     */
    <R> Optional<R> getWithProgress(String name, NodeLogger logger, FunctionWithProgress<R> task);

    /**
     * A function that can report progress and be cancelled.
     *
     * @param <R> result type
     */
    interface FunctionWithProgress<R> {

        /**
         * Invokes the function.
         *
         * @param progressMonitor progress monitor
         * @return computed result
         */
        R invoke(IProgressMonitor progressMonitor);
    }

    /**
     * No-Op implementation
     */
    class NullProgressReporter implements ProgressReporter {

        @Override
        public <R> Optional<R> getWithProgress(final String name, final NodeLogger logger,
            final FunctionWithProgress<R> task) {
            return Optional.ofNullable(task.invoke(new NullProgressMonitor()));
        }
    }

}
