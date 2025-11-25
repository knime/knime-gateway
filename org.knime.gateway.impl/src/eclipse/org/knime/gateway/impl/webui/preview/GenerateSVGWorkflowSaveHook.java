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
 *   Jul 2, 2025 (hornm): created
 */
package org.knime.gateway.impl.webui.preview;

import static org.knime.core.node.workflow.WorkflowPersistor.SVG_WORKFLOW_FILE;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.knime.core.node.util.CheckUtils;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowSaveHook;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.webui.preview.util.AnnotationUtils;
import org.knime.gateway.impl.webui.preview.util.ColorConstants;
import org.knime.gateway.impl.webui.preview.util.ConnectorUtils;
import org.knime.gateway.impl.webui.preview.util.LabelUtils;
import org.knime.gateway.impl.webui.preview.util.LegacyAnnotationUtils;
import org.knime.gateway.impl.webui.preview.util.NodeUtils;
import org.knime.gateway.impl.webui.preview.util.PortUtils;
import org.knime.gateway.impl.webui.preview.util.ShapeConstants;
import org.knime.gateway.impl.webui.preview.util.TemplateUtils;
import org.knime.gateway.impl.webui.preview.util.WorkflowBoundsCalculator;
import org.knime.gateway.impl.webui.preview.util.WorkflowBoundsCalculator.BoundingBox;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 * Workflow save hook, which generates and saves a preview of the current state of the workflow as an SVG image
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 5.6
 */
public class GenerateSVGWorkflowSaveHook extends WorkflowSaveHook {

    private static final String FILE_NAME = SVG_WORKFLOW_FILE;
    private static final String TEMP_FILE = SVG_WORKFLOW_FILE + ".temp";

    private static final int EMPTY_MARGIN = 20;

    /**
     * Renders a preview of the given {@link WorkflowManager} instance in SVG format to the provided file path.
     *
     * @param workflow the {@link WorkflowManager} instance containing the current state of the workflow to be rendered
     * @param filePath the {@link Path} to the svg file to be written
     * @throws IllegalArgumentException if the provided arguments are null
     * @throws IOException if writing the output to the specified file path fails
     */
    static void renderPreviewSVG(final WorkflowManager workflow, final Path filePath)
        throws IllegalArgumentException, IOException {

        CheckUtils.checkArgumentNotNull(workflow, "Workflow can not be null for preview generation");
        CheckUtils.checkArgumentNotNull(filePath, "File path can not be null for preview generation");

        var basePath = GenerateSVGWorkflowSaveHook.class.getPackage().getName().replace('.', '/');
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setName("svg");
        templateResolver.setPrefix("/" + basePath + "/templates/");
        templateResolver.setSuffix(".html"); //ThymeLeaf templating default
        templateResolver.setTemplateMode(TemplateMode.HTML); // Need to use HTML and not XML because of foreignObjects
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(true);

        var context = new Context();
        var workflowEnt = EntityFactory.Workflow.buildWorkflowEnt(workflow, WorkflowBuildContext.builder());
        BoundingBox workflowBounds = WorkflowBoundsCalculator.getWorkflowBoundingBox(workflowEnt);
        String viewBox = (workflowBounds != null)
                ? String.format("%d %d %d %d", workflowBounds.minX(), workflowBounds.minY(), workflowBounds.width(), workflowBounds.height())
                : String.format("0 0 %d %d", EMPTY_MARGIN, EMPTY_MARGIN);
        context.setVariable("bounds", workflowBounds);
        context.setVariable("viewbox", viewBox);

        context.setVariable("annotations", workflowEnt.getWorkflowAnnotations());
        context.setVariable("nodes", workflowEnt.getNodes().values());
        context.setVariable("connections", workflowEnt.getConnections().values());

        context.setVariable("colors", ColorConstants.COLORS);
        context.setVariable("shapes", ShapeConstants.SHAPES);

        context.setVariable("templateUtils", new TemplateUtils());
        context.setVariable("nodeUtils", new NodeUtils(workflowEnt.getNodeTemplates()));
        context.setVariable("connectorUtils", new ConnectorUtils(workflowEnt.getNodes()));
        context.setVariable("portUtils", new PortUtils());
        context.setVariable("labelUtils", new LabelUtils());
        context.setVariable("annotationUtils", new AnnotationUtils());
        context.setVariable("legacyAnnotationUtils", new LegacyAnnotationUtils());

        var templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new WhiteSpaceRemovalDialect());
        try (Writer writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            templateEngine.process("workflow", context, writer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSave(final WorkflowManager workflow, final boolean isSaveData, final File artifactsFolder)
        throws IOException {

        /* We want to achieve a state where either a correct SVG file of the current workflow state is written
         * or there is no preview file at all. To achieve this a previously existing file is always deleted first,
         * the writing is first done to a temp file to catch cases where writing fails half way through the preview
         * generation and only parts of the file can be written. Upon success the file is then copied to the actual
         * location with an atomic move operation.
         */

//        final Path tempPath = artifactsFolder.toPath().resolve("../" + TEMP_FILE);
//        final Path outputPath = artifactsFolder.toPath().resolve("../" + FILE_NAME);
//
//        // Delete existing workflow preview file first always
//        Files.deleteIfExists(tempPath);
//        Files.deleteIfExists(outputPath); // in case deletion fails, IOException should be thrown here already
//
//        try {
//            renderPreviewSVG(workflow, tempPath);
//            Files.move(tempPath, outputPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
//        } catch (IOException | IllegalArgumentException ex) {
//            Files.deleteIfExists(outputPath);
//            throw new IOException("Workflow preview generation failed", ex);
//        } finally {
//            Files.deleteIfExists(tempPath);
//        }
    }

}
