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
 *   Sep 2, 2025 (hornm): created
 */
package org.knime.gateway.api.webui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.knime.gateway.api.entity.EntityBuilderManager;
import org.knime.gateway.api.service.GatewayException;
import org.knime.gateway.api.webui.entity.GatewayProblemDescriptionEnt;
import org.knime.gateway.api.webui.entity.GatewayProblemDescriptionEnt.GatewayProblemDescriptionEntBuilder;

/**
 * Factory for miscellaneous entities. See also {@link EntityFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @since 5.8
 */
@SuppressWarnings("static-method")
public final class MiscEntityFactory {

    /**
     * "title" property of unexpected exceptions.
     */
    public static final String UNEXPECTED_TITLE = "An unexpected error occurred";

    /**
     * Creates a {@link GatewayProblemDescriptionEnt} from a known/gateway exception.
     *
     * @param gatewayException known gateway exception
     * @return problem entity
     */
    public GatewayProblemDescriptionEnt buildKnownProblemDescriptionEnt(final GatewayException gatewayException) {
        final var details = gatewayException.getDetails();
        final var additionalProperties = new HashMap<>(gatewayException.getAdditionalProperties());
        if (gatewayException.getCause() != null) {
            additionalProperties.put("stackTrace", ExceptionUtils.getStackTrace(gatewayException));
        }
        return EntityBuilderManager.builder(GatewayProblemDescriptionEntBuilder.class) //
            .setTitle(gatewayException.getTitle()) //
            .setCode(gatewayException.getClass().getSimpleName()) //
            .setStatus(gatewayException.getStatus().stream().boxed().findAny().orElse(null)) //
            .setDetails(details == null || details.isEmpty() ? null : details) //
            .setCanCopy(gatewayException.isCanCopy()) //
            .setAdditionalProperties(additionalProperties) //
            .build();
    }

    /**
     * Creates a {@link GatewayProblemDescriptionEnt} from an unknown exception.
     *
     * @param throwable unknown exception
     * @return problem entity
     */
    public GatewayProblemDescriptionEnt buildUnknownProblemDescriptionEnt(final Throwable throwable) {
        return EntityBuilderManager.builder(GatewayProblemDescriptionEntBuilder.class) //
            .setTitle(UNEXPECTED_TITLE) //
            .setCode(throwable.getClass().getSimpleName()) //
            .setDetails(List.of(throwable.getClass().getSimpleName() + ": " + throwable.getMessage())) //
            .setCanCopy(true) //
            .setAdditionalProperties(Map.of( //
                "message", throwable.getMessage(), //
                "stackTrace", ExceptionUtils.getStackTrace(throwable)//
            )) //
            .build();
    }

}
