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
 *   Apr 29, 2020 (hornm): created
 */
package org.knime.next.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.node.workflow.WorkflowManager;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.knime.gateway.entity.NodeIDEnt;
import com.knime.gateway.entity.WorkflowSnapshotEnt;
import com.knime.gateway.remote.endpoint.WorkflowProject;
import com.knime.gateway.remote.endpoint.WorkflowProjectManager;
import com.knime.gateway.remote.service.DefaultWorkflowService;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.service.util.ServiceExceptions.NotASubWorkflowException;

/**
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@Path("/")
public class InitService {

    private Map<String, WorkflowReference> m_registeredWorkflows = new HashMap<>();

    @GET
    @Path("/workflows")
    @Produces({"application/json"})
    public Response getOpenWorkflows() {

        IEditorReference[] editorReferences =
            PlatformUI.getWorkbench().getWorkbenchWindows()[0].getPages()[0].getEditorReferences();

        Map<String, WorkflowReference> newRegisteredWorkflows = new HashMap<>();
        for (IEditorReference ref : editorReferences) {
            if (!ref.getId().equals("org.knime.workbench.editor.WorkflowEditor")) {
                continue;
            }
            String name;
            try {
                name = ((FileStoreEditorInput)ref.getEditorInput()).getURI().toString().replace("/workflow.knime", "");
            } catch (PartInitException e) {
                throw new RuntimeException(e);
            }
            if (!m_registeredWorkflows.containsKey(name)) {
                UUID uuid = UUID.randomUUID();
                WorkflowProjectManager.addWorkflowProject(uuid, new WorkflowProject() {

                    @Override
                    public WorkflowManager openProject() {
                        Display.getDefault().syncExec(() -> ref.getEditor(true));
                        return findWorkflowManager(name);
                    }

                    @Override
                    public String getName() {
                        return ref.getName();
                    }

                    @Override
                    public String getID() {
                        return uuid.toString();
                    }
                });
                WorkflowSnapshotEnt wf = null;
                WorkflowManager wfm = findWorkflowManager(name);
                if (wfm != null) {
                    try {
                        wf = DefaultWorkflowService.getInstance().getWorkflow(uuid, NodeIDEnt.getRootID());
                    } catch (NotASubWorkflowException | NodeNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                newRegisteredWorkflows.put(name, new WorkflowReference(name, uuid, wf));
            } else {
                newRegisteredWorkflows.put(name, m_registeredWorkflows.get(name));
            }
        }
        m_registeredWorkflows = newRegisteredWorkflows;
        List<WorkflowReference> list = new ArrayList<>(m_registeredWorkflows.values());
        GenericEntity<Collection<WorkflowReference>> entity = new GenericEntity<>(list, list.getClass());
        return Response.ok(entity).build();

    }

    private static WorkflowManager findWorkflowManager(final String path) {
        for (NodeContainer nc : WorkflowManager.ROOT.getNodeContainers()) {
            if (nc instanceof WorkflowManager) {
                WorkflowManager wfm = (WorkflowManager)nc;
                if (wfm.getContext() != null) {
                    String mountpointURI = wfm.getContext().getMountpointURI().map(URI::toString).orElse(null);
                    if (path.equals(mountpointURI)) {
                        return wfm;
                    }
                }
            }
        }
        return null;
    }

    @JsonAutoDetect(fieldVisibility = Visibility.ANY)
    class WorkflowReference {

        private String name;

        private UUID id;

        private WorkflowSnapshotEnt workflow;

        WorkflowReference(final String name, final UUID id, final WorkflowSnapshotEnt wf) {
            this.name = name;
            this.id = id;
            workflow = wf;
        }

    }

}