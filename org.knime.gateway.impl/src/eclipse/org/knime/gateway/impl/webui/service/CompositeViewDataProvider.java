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
 *   Feb 4, 2025 (hornm): created
 */
package org.knime.gateway.impl.webui.service;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.gateway.api.entity.NodeViewEnt;

/**
 * Provider for the composite view data of components.
 *
 * Pragmatic solution to avoid circular dependencies between knime-gateway and knime-js-core plugins.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public interface CompositeViewDataProvider {

    /**
     * Returns the composite view data of the component.
     *
     * @param snc the container of the component
     * @param createNodeViewEnt A function that creates a NodeViewEntity from a native node container to be rendered in
     *            the composite view
     * @return A string representation of the page to show, that embed the views of the containing nodes into a
     *         customizable layout.
     * @throws IOException -
     */
    String getCompositeViewData(final SubNodeContainer snc,
        final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt) throws IOException;

    /**
     * Triggers the re-execution of the component.
     *
     * @param snc -
     * @param resetNodeIdSuffix The nodeId suffix, i.e., not starting with root, that triggered the re-execution
     * @param viewValues -
     * @param createNodeViewEnt -
     * @throws IOException -
     */
    void triggerComponentReexecution(final SubNodeContainer snc, final String resetNodeIdSuffix,
        final Map<String, String> viewValues, final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt)
        throws IOException;

}
