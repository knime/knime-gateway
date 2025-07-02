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
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.WorkflowSaveHook;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.DefaultTemplateResolver;

/**
 * TODO
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class GenerateSVGWorkflowSaveHook extends WorkflowSaveHook {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSave(final WorkflowManager workflow, final boolean isSaveData, final File artifactsFolder)
        throws IOException {

        var templateEngine = new TemplateEngine();
        var templateResolver = new DefaultTemplateResolver(); // TODO use different resolver
        var template = new String(this.getClass().getResourceAsStream("workflow_preview_template.svg").readAllBytes(),
            StandardCharsets.UTF_8); // TODO close
        templateResolver.setTemplate(template);
        templateResolver.setName("svg");
        templateEngine.addTemplateResolver(templateResolver);
        var context = new Context();
        context.setVariable("workflow", new SVGWorkflow(workflow));
        // templateEngine.process("svg", new Context(), new FileWriter(null /* TODO */));
        var res = templateEngine.process("svg", context);
    }

    public class SVGWorkflow {

        private final WorkflowManager m_wfm;

        SVGWorkflow(final WorkflowManager wfm) {
            m_wfm = wfm;

        }

        public List<SVGNode> getNodes() {
            return m_wfm.getNodeContainers().stream().map(SVGNode::new).toList();
        }

    }

    public class SVGNode {

        private final NodeContainer m_nc;

        SVGNode(final NodeContainer nc) {
            m_nc = nc;
        }

        public String getName() {
            return m_nc.getName();
        }

    }

}
