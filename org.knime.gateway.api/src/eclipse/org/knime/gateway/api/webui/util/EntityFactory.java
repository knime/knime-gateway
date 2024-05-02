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
 *   Dec 12, 2022 (hornm): created
 */
package org.knime.gateway.api.webui.util;

import org.knime.gateway.api.entity.GatewayEntity;
import org.knime.gateway.api.webui.entity.NodeDescriptionEnt;
import org.knime.gateway.api.webui.entity.NodeTemplateEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.SpaceItemEnt;
import org.knime.gateway.api.webui.entity.UpdateAvailableEventEnt;
import org.knime.gateway.api.webui.entity.WorkflowEnt;
import org.knime.gateway.api.webui.entity.WorkflowMonitorStateEnt;

/**
 * Entry utility class to access builder-methods for all kind of entities (i.e. {@link GatewayEntity GatewayEntities}).
 * The actual builder-methods are organized in extra classes with the only purpose to namespace them and separate
 * builder-methods that are mostly independent from each other.
 *
 * A few remarks on the order of methods within the 'Builder'-classes being used for 'namespacing':<br>
 * 1. Properties come first and are sorted alphabetically. ENUMs are considered properties too. <br>
 * 2. Next in line is the package-scope constructor<br>
 * 3. Public methods (sorted alphabetically), since they got called externally and have to be found quickly.<br>
 * 4. Private methods (sorted alphabetically)
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class EntityFactory {

    private EntityFactory() {
        // utility class
    }

    /**
     * Entity builder instance to mainly build {@link WorkflowEnt}s and related entities.
     */
    public static final WorkflowEntityFactory Workflow = new WorkflowEntityFactory();

    /**
     * Entity builder instance to mainly build {@link NodeTemplateEnt}s, {@link NodeDescriptionEnt}s and related
     * entities.
     */
    public static final NodeTemplateAndDescriptionEntityFactory NodeTemplateAndDescription =
        new NodeTemplateAndDescriptionEntityFactory();

    /**
     * Entity builder instance to mainly build {@link SpaceItemEnt}s and related entities.
     */
    public static final SpaceEntityFactory Space = new SpaceEntityFactory();

    /**
     * Entity builder instance to build {@link PortTypeEnt}s.
     */
    public static final PortTypeEntityFactory PortType = new PortTypeEntityFactory();

    /**
     * Entity builder instance to mainly build {@link UpdateAvailableEventEnt}s and related entities.
     */
    public static final UpdateStateEntityFactory UpdateState = new UpdateStateEntityFactory();

    /**
     * Entity builder instance to mainly build {@link WorkflowMonitorStateEnt}s and related entities.
     */
    public static final WorkflowMonitorStateEntityFactory WorkflowMonitorState = new WorkflowMonitorStateEntityFactory();

}
