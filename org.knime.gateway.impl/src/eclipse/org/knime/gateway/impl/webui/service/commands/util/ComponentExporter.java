/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
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
 *   Created on Nov 6, 2024 by Assistant
 */
package org.knime.gateway.impl.webui.service.commands.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;

import org.apache.commons.io.FileUtils;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowExporter;
import org.knime.core.util.LockFailedException;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

/**
 * Utility class for exporting KNIME components as compressed archives with size limits. Provides functionality to save
 * components as templates and compress them for sharing or storage.
 */
@SuppressWarnings({"java:S3553", "OptionalUsedAsFieldOrParameterType"})
public final class ComponentExporter {

    private ComponentExporter() {

    }

    /**
     * Exports a component to a directory without compression.
     *
     * @param component the component to export
     * @param wfArtifactTarget the target directory path
     * @param includeInputData whether to include input data in the export
     * @return the original template information of the component
     * @throws CanceledExecutionException if the operation is canceled
     * @throws MutableServiceCallException if the export fails
     */
    public static MetaNodeTemplateInformation exportToDirectory(final SubNodeContainer component,
        final Path wfArtifactTarget, final boolean includeInputData)
        throws CanceledExecutionException, MutableServiceCallException {
        return saveAsTemplate(component, wfArtifactTarget, includeInputData);
    }

    /**
     * Exports a component as a compressed archive with size validation.
     *
     * <p>
     * This method saves the component as a template, collects its resources, and compresses them into an archive file.
     * Optionally includes example input data if specified.
     *
     * @param component the component (SubNodeContainer) to export
     * @param wfArtifactTarget directory path where the component template will be saved
     * @param compressionTarget file path for the resulting compressed archive
     * @param includeInputData whether to include example input data in the export
     * @param uploadLimit throws if exceeded
     * @return the original template information of the component
     * @throws CanceledExecutionException -
     * @throws MutableServiceCallException -
     * @throws ServiceExceptions.ServiceCallException -
     */
    @SuppressWarnings("java:S125")
    public static MetaNodeTemplateInformation exportComponentWithLimit(final SubNodeContainer component,
        final Path wfArtifactTarget, final Path compressionTarget, final boolean includeInputData,
        final Optional<Long> uploadLimit)
        throws MutableServiceCallException, CanceledExecutionException, ServiceExceptions.ServiceCallException {

        var originalTemplateInfo = saveAsTemplate(component, wfArtifactTarget, includeInputData);

        var workflowExporter = new WorkflowExporter<CancellationException>(!includeInputData);
        final WorkflowExporter.ResourcesToCopy localItems;
        try {
            localItems = workflowExporter.collectResourcesToCopy( //
                List.of(wfArtifactTarget), //
                wfArtifactTarget.getParent() //
            );
        } catch (IOException e) {
            throw new MutableServiceCallException("Could not read component data", true, e);
        }

        final long compressedSize;
        try {
            compressedSize = workflowExporter.exportWorkflowWithLimit( //
                localItems, //
                compressionTarget, //
                uploadLimit.orElse(Long.MAX_VALUE), //
                () -> false, //
                CancellationException::new //
            );
        } catch (IOException e) {
            throw new MutableServiceCallException("Could not write component to archive", true, e);
        }
        assertSize(compressedSize, uploadLimit);
        return originalTemplateInfo;
    }

    /**
     * Saves the component as a template to the specified directory.
     *
     * @param component the component to save
     * @param wfArtifactTarget the target directory
     * @param includeInputData whether to include input data
     * @return the original template information
     * @throws MutableServiceCallException if the save operation fails
     * @throws CanceledExecutionException if the operation is canceled
     */
    private static MetaNodeTemplateInformation saveAsTemplate(final SubNodeContainer component,
        final Path wfArtifactTarget, final boolean includeInputData)
        throws MutableServiceCallException, CanceledExecutionException {
        try {
            return component.saveAsTemplate( //
                wfArtifactTarget.toFile(), //
                new ExecutionMonitor(), //
                includeInputData ? CoreUtil.getExampleInputData(component) : null //
            );
        } catch (IOException e) {
            throw new MutableServiceCallException("Could not write component to disk.", true, e);
        } catch (LockFailedException e) {
            throw new MutableServiceCallException("Could not lock the workflow. Please try re-opening the project.",
                true, e);
        } catch (InvalidSettingsException e) {
            throw new RuntimeException(e); // NOSONAR
            // should not happen unless the component settings are corrupted or there is a bug in the export logic
        } catch (InterruptedException e) {
            throw new CanceledExecutionException();
        }
    }

    /**
     * Validates that the compressed size does not exceed the specified limit.
     *
     * @param compressedSize the actual size of the compressed archive in bytes
     * @param uploadLimit the maximum allowed size in bytes (empty if no limit)
     * @throws MutableServiceCallException if the upload limit is exceeded
     */
    private static void assertSize(final long compressedSize, final Optional<Long> uploadLimit)
        throws ServiceExceptions.ServiceCallException {
        if (uploadLimit.isEmpty()) {
            return;
        }
        if (compressedSize > uploadLimit.get()) {
            throw ServiceExceptions.ServiceCallException.builder() //
                .withTitle("Failed to share component") //
                .withDetails(List.of(
                    "Upload limit exceeded. You may try reducing the size of included assets and resetting nodes.",
                    "Upload limit: " + FileUtils.byteCountToDisplaySize(uploadLimit.get()))) //
                .canCopy(true) //
                .build(); //
        }
    }
}
