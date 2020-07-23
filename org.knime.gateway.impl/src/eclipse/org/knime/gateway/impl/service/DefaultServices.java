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
 * ---------------------------------------------------------------------
 *
 */
package org.knime.gateway.impl.service;

import java.util.NoSuchElementException;

import org.knime.gateway.api.service.AnnotationService;
import org.knime.gateway.api.service.GatewayService;
import org.knime.gateway.api.service.NodeService;
import org.knime.gateway.api.service.WizardExecutionService;
import org.knime.gateway.api.service.WorkflowService;

/**
 * Provides the default service implementations for gateway services.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DefaultServices {

    private DefaultServices() {
        //utility class
    }

    /**
     * Maps a service interface to its default service implementation.
     *
     * @param serviceInterface the service interface the default implementation is requested for
     * @return the default implementation for the provided service interface
     * @throws NoSuchElementException if no default service implementation has been found
     */
    @SuppressWarnings("unchecked")
    public static <S extends GatewayService> S getDefaultService(final Class<S> serviceInterface)
        throws NoSuchElementException {
        if(serviceInterface.equals(WorkflowService.class)) {
            return (S)DefaultWorkflowService.getInstance();
        } else if(serviceInterface.equals(NodeService.class)) {
            return (S)DefaultNodeService.getInstance();
        } else if (serviceInterface.equals(AnnotationService.class)) {
            return (S)DefaultAnnotationService.getInstance();
        } else if (serviceInterface.equals(WizardExecutionService.class)) {
            return (S)DefaultWizardExecutionService.getInstance();
        } else {
            throw new NoSuchElementException(
                "No default service implementation found for " + serviceInterface.getSimpleName());
        }
    }
}
