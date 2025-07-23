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

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowSaveHook;
import org.knime.gateway.api.webui.util.EntityFactory;
import org.knime.gateway.api.webui.util.WorkflowBuildContext;
import org.knime.gateway.impl.webui.preview.util.AnnotationUtils;
import org.knime.gateway.impl.webui.preview.util.ColorConstants;
import org.knime.gateway.impl.webui.preview.util.ConnectorUtils;
import org.knime.gateway.impl.webui.preview.util.LegacyAnnotationUtils;
import org.knime.gateway.impl.webui.preview.util.NodeUtils;
import org.knime.gateway.impl.webui.preview.util.PortUtils;
import org.knime.gateway.impl.webui.preview.util.ShapeConstants;
import org.knime.gateway.impl.webui.preview.util.TextUtils;
import org.knime.gateway.impl.webui.preview.util.WorkflowBoundsCalculator;
import org.knime.gateway.impl.webui.preview.util.WorkflowBoundsCalculator.BoundingBox;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/**
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class GenerateSVGWorkflowSaveHook extends WorkflowSaveHook {

    static int EMPTY_MARGIN = 20;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSave(final WorkflowManager workflow, final boolean isSaveData, final File artifactsFolder)
        throws IOException {
        long startTime = System.nanoTime(); // TODO remove me

        String fileName = "workflow_new.svg";
        Path outputPath = artifactsFolder.toPath().resolve("../" + fileName);

        var basePath = GenerateSVGWorkflowSaveHook.class.getPackage().getName().replace('.', '/');
        var templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setName("svg");
        templateResolver.setPrefix("/" + basePath + "/templates/");
        templateResolver.setSuffix(".html"); //ThymeLeaf templating default
        templateResolver.setTemplateMode(TemplateMode.HTML); // Need to use HTML and not XML because of foreignObjects
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false); // TODO set to true for production

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

        context.setVariable("textUtils", new TextUtils());
        context.setVariable("nodeUtils", new NodeUtils(workflowEnt.getNodeTemplates()));
        context.setVariable("connectorUtils", new ConnectorUtils());
        context.setVariable("portUtils", new PortUtils());
        context.setVariable("annotationUtils", new AnnotationUtils());
        context.setVariable("legacyAnnotationUtils", new LegacyAnnotationUtils());

        var templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(templateResolver);
        try (Writer writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            templateEngine.process("workflow", context, writer);
        }

        long endTime = System.nanoTime();
        long durationInNanoseconds = endTime - startTime;
        long durationInMilliseconds = durationInNanoseconds / 1_000_000;

        System.out.println(templateEngine.process("workflow", context));
        System.out.println("Preview generation took " + durationInMilliseconds + " ms");
    }

}
