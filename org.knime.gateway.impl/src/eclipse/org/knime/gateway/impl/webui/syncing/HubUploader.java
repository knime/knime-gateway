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
 *   Nov 19, 2025 (motacilla): created
 */
package org.knime.gateway.impl.webui.syncing;

import java.io.IOException;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.workflow.contextv2.HubSpaceLocationInfo;
import org.knime.gateway.api.webui.entity.SpaceProviderEnt.ResetOnUploadEnum;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.LoggedOutException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.NetworkException;
import org.knime.gateway.impl.service.util.WorkflowManagerResolver;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;

/**
 * Uploads workflows to Hub spaces.
 *
 * @author Kai Franze, KNIME GmbH, Germany
 */
final class HubUploader {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(HubUploader.class);

    private final SpaceProvider m_spaceProvider;

    HubUploader(final SpaceProvider spaceProvider) {
        m_spaceProvider = spaceProvider;
    }

    /**
     * TODO:
     *  - Get space, get item ID
     *  - How to handle "exclude data" a.k.a. reset before upload?
     *  - Basically call {@code HubSpace.saveBackTo(...)}, but we need to handle the size check
     *    happening in {@code HubSpaceAsyncTransfor.performAsyncUploadWithLimit(...)} a little different
     *  - We want to either catch the exception or at least return the information that the threshold was exceeded
     *
     * @throws SyncThresholdException if the workflow exceeds the sync threshold
     */
    void uploadProjectWithThreshold(final String projectId, final int syncThresholdMB)
        throws IOException, SyncThresholdException {
        var context = WorkflowManagerResolver.get(projectId).getContextV2();

        if (!(context.getLocationInfo() instanceof HubSpaceLocationInfo hubInfo)) {
            throw new UnsupportedOperationException("Uploading is only supported for workflows stored in Hub spaces.");
        }

        try {
            LOGGER.info("Uploading workflow to Hub for project ID: " + projectId);

            final var space = m_spaceProvider.getSpace(hubInfo.getSpaceItemId());
            final var localWorkflow = context.getExecutorInfo().getLocalWorkflowPath();
            final var targetURI = space.toPathBasedKnimeUrl(hubInfo.getWorkflowItemId());
            final var excludeDataInWorkflows = m_spaceProvider.getConnection(false) //
                .map(SpaceProvider.SpaceProviderConnection::getResetOnUploadMode) //
                .map(ResetOnUploadEnum.MANDATORY::equals) //
                .orElse(false);
            final var uploadLimitBytes = syncThresholdMB * 1024L * 1024L;
            space.saveBackToWithLimit(localWorkflow, targetURI, excludeDataInWorkflows, uploadLimitBytes,
                msg -> new SyncThresholdException(msg));

            LOGGER.info("Upload to Hub for project ID " + projectId + " completed successfully.");
        } catch (LoggedOutException | MutableServiceCallException | NetworkException e) {
            throw new IOException("Failed to upload workflow for project ID <%s>".formatted(projectId), e);
        }
    }

    void uploadProject(final String projectId) throws IOException {
        var context = WorkflowManagerResolver.get(projectId).getContextV2();
        if (!(context.getLocationInfo() instanceof HubSpaceLocationInfo hubInfo)) {
            return; // TODO: Log ot throw?
        }

        try {
            LOGGER.info("Uploading workflow to Hub for project ID: " + projectId);

            final var space = m_spaceProvider.getSpace(hubInfo.getSpaceItemId());
            final var localWorkflow = context.getExecutorInfo().getLocalWorkflowPath();
            final var targetURI = space.toPathBasedKnimeUrl(hubInfo.getWorkflowItemId());
            final var excludeDataInWorkflows = m_spaceProvider.getConnection(false) //
                .map(SpaceProvider.SpaceProviderConnection::getResetOnUploadMode) //
                .map(ResetOnUploadEnum.MANDATORY::equals) //
                .orElse(false);
            space.saveBackTo(localWorkflow, targetURI, excludeDataInWorkflows, new NullProgressMonitor());

            LOGGER.info("Upload to Hub for project ID " + projectId + " completed successfully.");
        } catch (LoggedOutException | MutableServiceCallException | NetworkException
                | UnsupportedOperationException e) {
            return; // TODO: Log or throw?
        }
    }

    static final class SyncThresholdException extends Exception {

        private static final long serialVersionUID = 1L;

        SyncThresholdException(final String message) {
            super("Sync threshold exceeded: " + message);
        }
    }
}
