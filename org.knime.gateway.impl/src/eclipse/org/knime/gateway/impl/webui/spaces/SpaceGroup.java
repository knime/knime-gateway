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
 *   3 May 2024 (baqueroj): created
 */
package org.knime.gateway.impl.webui.spaces;

import java.io.IOException;
import java.util.List;

import org.knime.gateway.api.webui.entity.SpaceGroupEnt;

/**
 * Represents a space group which can be a user or a team inside a provider
 *
 * @author Juan Baquero
 * @param <S> The concrete type of spaces contained in this group
 */
public interface SpaceGroup<S extends Space> {

    /**
     * Describe the type of this group
     */
    enum SpaceGroupType {

            /** User group */
            USER,

            /** Team group */
            TEAM;

        public SpaceGroupEnt.TypeEnum toEntity() {
            if (this == USER) {
                return SpaceGroupEnt.TypeEnum.USER;
            }
            return SpaceGroupEnt.TypeEnum.TEAM;
        }
    }

    /**
     * @return name of this group
     */
    String getName();

    /**
     * @return group type
     */
    SpaceGroupType getType();

    /**
     * @return the spaces contained within this group
     */
    List<S> getSpaces();

    /**
     * @return the newly created {@link Space}
     * @throws IOException
     */
    default Space createSpace() throws IOException {
        throw new UnsupportedOperationException("Creation of spaces is not supported in this provider");
    }

    /**
     * Creates a {@link SpaceGroupEnt} for this space group.
     *
     * @return space group entity for this group
     */
    SpaceGroupEnt toEntity();

}
