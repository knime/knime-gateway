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
import java.util.function.Supplier;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.workflow.MetaNodeTemplateInformation;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowExporter;
import org.knime.core.util.LockFailedException;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.service.util.ServiceExceptions;

/**
 * Utility class for exporting KNIME components as compressed archives with size limits. Provides functionality to save
 * components as templates and compress them for sharing or storage.
 */
@SuppressWarnings("java:S3553")
public final class ComponentExporter {

    private ComponentExporter() {

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
     * @throws IOException if file operations fail
     * @throws CanceledExecutionException if the operation is canceled
     * @throws LockFailedException if the component cannot be locked for export
     * @throws InvalidSettingsException if component settings are invalid
     * @throws ServiceExceptions.ServiceCallException if size limits are exceeded
     */
    @SuppressWarnings("java:S125")
    public static MetaNodeTemplateInformation exportComponentWithLimit(final SubNodeContainer component,
        final Path wfArtifactTarget, final Path compressionTarget, final boolean includeInputData,
        Optional<Long> uploadLimit) throws IOException, CanceledExecutionException, LockFailedException,
        InvalidSettingsException, ServiceExceptions.ServiceCallException {

        Supplier<PortObject[]> exampleInputData = () -> CoreUtil.getExampleInputData(component).orElseThrow();
        var originalTemplateInfo = component.saveAsTemplate( //
            wfArtifactTarget.toFile(), //
            new ExecutionMonitor(), //
            includeInputData ? exampleInputData.get() : null //
        );
        // now have /tmp/knime_<PROJ_NAME>/MetaTemplateUpload<ID>/<COMPONENT_NAME>/{workflow.knime,...}

        // TODO NXT-4217 no need to compress if source and target are both LOCAL (NOSONAR)
        //  (for LOCAL it is effectively just a move op)
        var workflowExporter = new WorkflowExporter<CancellationException>(!includeInputData);
        final var localItems = workflowExporter.collectResourcesToCopy( //
            List.of(wfArtifactTarget), //
            wfArtifactTarget.getParent() //
        );

        final var compressedSize = workflowExporter.exportWorkflowWithLimit( //
            localItems, //
            compressionTarget, //
            uploadLimit.orElse(Long.MAX_VALUE), //
            () -> false, //
            CancellationException::new //
        );
        //  now have /tmp/knime_<PROJ_NAME/knime_uploaded_item<ID>/<COMPONENT_NAME>.knwf/{workflow.knime,...}
        assertSize(compressedSize, uploadLimit);
        return originalTemplateInfo;
    }

    /**
     * Validates that the compressed size does not exceed the specified limit.
     * 
     * @param compressedSize the actual size of the compressed archive in bytes
     * @param uploadLimit the maximum allowed size in bytes
     * @throws ServiceExceptions.ServiceCallException if the size limit is exceeded
     */
    private static void assertSize(final long compressedSize, final Optional<Long> uploadLimit)
        throws ServiceExceptions.ServiceCallException {
        if (uploadLimit.isEmpty()) {
            return;
        }
        if (compressedSize > uploadLimit.get()) {
            throw ServiceExceptions.ServiceCallException.builder() //
                .withTitle("Failed to share component") //
                .withDetails(
                    "Upload limit exceeded. You may try reducing the size of included assets and resetting nodes.") //
                .canCopy(true) //
                .build(); //
        }
    }
}
