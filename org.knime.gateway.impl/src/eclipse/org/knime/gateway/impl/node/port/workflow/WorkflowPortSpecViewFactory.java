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
 *   Nov 1, 2025 (hornm): created
 */
package org.knime.gateway.impl.node.port.workflow;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.NodeID;
import org.knime.core.node.workflow.NodeID.NodeIDSuffix;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.node.workflow.capture.WorkflowPortObjectSpec;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.port.PortSpecViewFactory;
import org.knime.core.webui.node.port.PortView;
import org.knime.core.webui.page.Page;
import org.knime.gateway.api.entity.NodeIDEnt;
import org.knime.gateway.impl.webui.preview.GenerateSVGWorkflowSaveHook;
import org.osgi.framework.FrameworkUtil;

/**
 * TODO
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class WorkflowPortSpecViewFactory implements PortSpecViewFactory<WorkflowPortObjectSpec> {

    // TODO OS-independent!?
    private static final String BASE_PATH = "src/eclipse/org/knime/gateway/impl/node/port/workflow";

    @Override
    public PortView createPortView(final WorkflowPortObjectSpec portObjectSpec) {
        return createWorkflowPortView(portObjectSpec);
    }

    static PortView createWorkflowPortView(final WorkflowPortObjectSpec spec) {
        var ws = spec.getWorkflowSegment();
        WorkflowManager wfm = ws.loadWorkflow();
        return new PortView() {

            @Override
            public Page getPage() {
                return Page.create().fromFile().bundleClass(getClass()).basePath(BASE_PATH)
                    .relativeFilePath("index.html") //
                    .addResourceFile("main.css") //
                    .addResourceFile("script.js") //
                    .addResources(resource -> {
                        String res;
                        NodeContainer nc;
                        if (resource.equals("root.html")) {
                            nc = wfm;
                        } else {
                            var tmp = new NodeIDEnt(resource.replace(".html", "").replace("_", ":")).toNodeID(wfm);
                            // TODO hack
                            var nodeID = NodeID.fromString(
                                NodeIDSuffix.create(WorkflowManager.EXTRACTED_WORKFLOW_ROOT.getID(), tmp).toString());
                            nc = wfm.findNodeContainer(nodeID);
                        }
                        WorkflowManager subWfm;
                        if (nc instanceof WorkflowManager w) {
                            subWfm = w;
                        } else if (nc instanceof SubNodeContainer snc) {
                            subWfm = snc.getWorkflowManager();
                        } else {
                            throw new IllegalArgumentException(
                                "NodeID " + nc.getID() + " does not point to a sub-workflow");
                        }
                        res = renderHtmlFromWfm(subWfm);
                        return new ByteArrayInputStream(res.getBytes(StandardCharsets.UTF_8));
                    }, "workflow", false);
            }

            private static String renderHtmlFromWfm(final WorkflowManager wfm) {
                var writer = new StringWriter();
                try {
                    GenerateSVGWorkflowSaveHook.renderPreviewSVG(wfm, writer);
                } catch (IllegalArgumentException | IOException ex) {
                    // TODO
                    ex.printStackTrace();
                }
                var svg = writer.toString();
                var breadcrumb = renderBreadcrumb(wfm);
                try {
                    var template = FileUtils.readFileToString(
                        getAbsolutePath(BASE_PATH + "/workflow.template.html").toFile(), StandardCharsets.UTF_8);
                    return template.replace("<!-- SVG -->", svg).replace("<!-- BREADCRUMB -->", breadcrumb);
                } catch (IOException ex) {
                    // TODO
                    throw new RuntimeException("Failed to read workflow HTML template", ex);
                }
            }

            /**
             * @param wfm
             * @return
             */
            private static String renderBreadcrumb(final WorkflowManager wfm) {
                var project = wfm.getProjectWFM();
                var levels = new LinkedList<String>();
                var currentWfm = wfm;
                while (true) {
                    String label = null;
                    String href = null;
                    if (currentWfm == project) {
                        label = "root";
                        href = "root.html";
                    } else {
                        var nodeID = currentWfm.getID().toString().replace(":", "_");
                        label = currentWfm.getName();
                        href = nodeID + ".html";
                    }
                    if (label != null) {
                        if (levels.isEmpty()) {
                            levels.addFirst("<span>" + label + "</span>");
                        } else {
                            levels.addFirst("<a href=\"" + href + "\"" + ">" + label + "</a>");
                        }
                    }
                    if (currentWfm == project) {
                        break;
                    }
                    var ncParent = currentWfm.getDirectNCParent();
                    if (ncParent instanceof SubNodeContainer snc) {
                        currentWfm = snc.getParent();
                    } else {
                        currentWfm = (WorkflowManager)ncParent;
                    }
                }
                var breadcrumb = String.join("<span> > </span>", levels);
                return breadcrumb;
            }

            @Override
            public Optional<InitialDataService<String>> createInitialDataService() {
                return Optional.of(InitialDataService.builder(() -> "unused").onDeactivate(() -> {
                    try {
                        // TODO comment why serialize
                        ws.serializeAndDisposeWorkflow();
                    } catch (IOException ex) {
                        // TODO
                        throw new RuntimeException(ex);
                    }
                }).build());
            }

            @Override
            public Optional<RpcDataService> createRpcDataService() {
                return Optional.empty();
            }

        };
    }

    /*
     * The bundle path + base path.
     */
    private static Path getAbsolutePath(final String relativePath) {
        var bundle = FrameworkUtil.getBundle(WorkflowPortSpecViewFactory.class);
        var bundleUrl = bundle.getEntry(".");
        try {
            // must not use url.toURI() -- FileLocator leaves spaces in the URL (see eclipse bug 145096)
            // -- taken from TableauHyperActivator.java line 158
            var url = FileLocator.toFileURL(bundleUrl);
            return Paths.get(new URI(url.getProtocol(), url.getFile(), null)).resolve(relativePath).normalize();
        } catch (IOException | URISyntaxException ex) {
            throw new IllegalStateException("Failed to resolve the directory " + relativePath, ex);
        }
    }

}
