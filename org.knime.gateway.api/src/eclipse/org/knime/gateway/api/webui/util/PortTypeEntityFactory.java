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

import static org.knime.gateway.api.entity.EntityBuilderManager.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.flowvariable.FlowVariablePortObject;
import org.knime.core.webui.node.port.PortSpecViewFactory;
import org.knime.core.webui.node.port.PortViewManager;
import org.knime.core.webui.node.port.PortViewManager.PortViewDescriptor;
import org.knime.gateway.api.util.CoreUtil;
import org.knime.gateway.api.webui.entity.PortTypeEnt;
import org.knime.gateway.api.webui.entity.PortTypeEnt.PortTypeEntBuilder;
import org.knime.gateway.api.webui.entity.PortViewDescriptorEnt;
import org.knime.gateway.api.webui.entity.PortViewDescriptorEnt.PortViewDescriptorEntBuilder;
import org.knime.gateway.api.webui.entity.PortViewDescriptorMappingEnt;
import org.knime.gateway.api.webui.entity.PortViewsEnt;
import org.knime.gateway.api.webui.entity.PortViewsEnt.PortViewsEntBuilder;

/**
 * See {@link EntityFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@SuppressWarnings("static-method")
public final class PortTypeEntityFactory {

    PortTypeEntityFactory() {
        //
    }

    /**
     * @param ptype The port type
     * @param availableTypes Available port types to be considered for listing ports compatible to the given one
     * @param includeInteractionInfo whether to include properties that are only required if one interacts with the
     *            workflow
     * @return An entity describing the given port type
     */
    @SuppressWarnings("java:S2301") // boolean parameter
    public PortTypeEnt buildPortTypeEnt(final PortType ptype, final Collection<PortType> availableTypes,
        final boolean includeInteractionInfo) {
        var kind = getPortTypeKind(ptype);
        List<String> compatibleTypes = Collections.emptyList();
        if (kind == PortTypeEnt.KindEnum.OTHER) {
            compatibleTypes = availableTypes.stream() //
                .filter(t -> !PortObject.TYPE.equals(t)) //
                .filter(t -> portTypesAreDifferentButCompatible(ptype, t)) //
                .map(CoreUtil::getPortTypeId)//
                .collect(Collectors.toList());
        }
        return builder(PortTypeEntBuilder.class)//
            .setName(ptype.getName())//
            .setKind(kind)//
            .setColor(WorkflowEntityFactory.hexStringColor(ptype.getColor()))//
            .setCompatibleTypes(compatibleTypes.isEmpty() ? null : compatibleTypes)//
            .setHidden(ptype.isHidden() ? Boolean.TRUE : null)//
            .setViews(includeInteractionInfo ? buildPortViewsEnt(ptype) : null)//
            .build();
    }

    private PortViewsEnt buildPortViewsEnt(final PortType ptype) {
        var views = PortViewManager.getPortViews(ptype);
        if (views == null) {
            return null;
        }
        return builder(PortViewsEntBuilder.class).setDescriptors(buildPortViewDescriptorEnt(views.viewDescriptors())) //
            .setDescriptorMapping( //
                builder(PortViewDescriptorMappingEnt.PortViewDescriptorMappingEntBuilder.class)//
                    .setConfigured(views.configuredIndices())//
                    .setExecuted(views.executedIndices())//
                    .build()//
            )//
            .build();
    }

    private List<PortViewDescriptorEnt> buildPortViewDescriptorEnt(final List<PortViewDescriptor> viewDescriptors) {
        return viewDescriptors.stream().map(vd -> //
        builder(PortViewDescriptorEntBuilder.class) //
            .setLabel(vd.label()) //
            .setIsSpecView(vd.viewFactory() instanceof PortSpecViewFactory ? true : null) //
            .build() //
        ).collect(Collectors.toList());
    }

    private PortTypeEnt.KindEnum getPortTypeKind(final PortType ptype) {
        if (BufferedDataTable.TYPE.equals(ptype)) {
            return PortTypeEnt.KindEnum.TABLE;
        } else if (FlowVariablePortObject.TYPE.equals(ptype)) {
            return PortTypeEnt.KindEnum.FLOWVARIABLE;
        } else if (PortObject.TYPE.equals(ptype)) {
            return PortTypeEnt.KindEnum.GENERIC;
        } else {
            return PortTypeEnt.KindEnum.OTHER;
        }
    }

    private boolean portTypesAreDifferentButCompatible(final PortType p1, final PortType p2) {
        return !p1.equals(p2) && (p1.isSuperTypeOf(p2) || p2.isSuperTypeOf(p1));
    }

}
