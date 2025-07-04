/*
 * ------------------------------------------------------------------------
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
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.api.webui.service.util;

import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.api.webui.service.CompositeViewService;
import org.knime.gateway.api.webui.service.KaiService;
import org.knime.gateway.api.webui.service.NodeService;
import org.knime.gateway.api.webui.service.NodeRepositoryService;
import org.knime.gateway.api.webui.service.PortService;
import org.knime.gateway.api.webui.service.EventService;
import org.knime.gateway.api.webui.service.WorkflowService;
import org.knime.gateway.api.webui.service.ComponentService;
import org.knime.gateway.api.webui.service.ComponentEditorService;
import org.knime.gateway.api.webui.service.ApplicationService;

import org.knime.gateway.api.service.GatewayService;

import java.util.ArrayList;
import java.util.List;


/**
 * Lists all gateway services of package <code>com.knime.gateway.service</code>.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public class ListServices {

    private ListServices() {
        //utility class
    }

    /**
     * Lists all gateway service classes of package <code>com.knime.gateway.service</code>.
     * @return the class list
     */
    public static List<Class<? extends GatewayService>> listServiceInterfaces() {
        List<Class<? extends GatewayService>> res = new ArrayList<>();
        res.add(SpaceService.class);
        res.add(CompositeViewService.class);
        res.add(KaiService.class);
        res.add(NodeService.class);
        res.add(NodeRepositoryService.class);
        res.add(PortService.class);
        res.add(EventService.class);
        res.add(WorkflowService.class);
        res.add(ComponentService.class);
        res.add(ComponentEditorService.class);
        res.add(ApplicationService.class);
        return res;
    }
}
