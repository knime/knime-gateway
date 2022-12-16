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
 *   Dec 8, 2022 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.knime.gateway.api.webui.entity.WorkflowGroupContentEnt;
import org.knime.gateway.api.webui.service.SpaceService;
import org.knime.gateway.api.webui.service.util.ServiceExceptions.InvalidRequestException;
import org.knime.gateway.impl.webui.Space;
import org.knime.gateway.impl.webui.SpaceProviders;

/**
 * The default workflow service implementation for the web-ui.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultSpaceService implements SpaceService {

    /**
     * Returns the singleton instance for this service.
     *
     * @return the singleton instance
     */
    public static DefaultSpaceService getInstance() {
        return ServiceInstances.getDefaultServiceInstance(DefaultSpaceService.class);
    }

    private final SpaceProviders m_spaceProviders =
        ServiceDependencies.getServiceDependency(SpaceProviders.class, true);

    DefaultSpaceService() {
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WorkflowGroupContentEnt listWorkflowGroup(final String spaceId, final String itemId)
        throws InvalidRequestException, org.knime.gateway.api.webui.service.util.ServiceExceptions.IOException {
        try {
            return getSpace(spaceId).listWorkflowGroup(itemId);
        } catch (NoSuchElementException e) {
            throw new InvalidRequestException("Problem fetching space items", e);
        } catch (IOException e) {
            throw new org.knime.gateway.api.webui.service.util.ServiceExceptions.IOException(e.getMessage(), e);
        }
    }

    /**
     * @param spaceId
     * @return the space for the given id if available
     * @throws NoSuchElementException if there is no space for the given id
     */
    public Space getSpace(final String spaceId) {
        return m_spaceProviders.get().stream().flatMap(sp -> sp.getSpaces().stream())
            .filter(s -> spaceId.equals(s.getId())).findFirst()
            .orElseThrow(() -> new NoSuchElementException("No space available for space id '" + spaceId + "'"));
    }

}
