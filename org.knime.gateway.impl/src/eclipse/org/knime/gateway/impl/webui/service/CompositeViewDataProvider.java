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
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.knime.core.node.workflow.NativeNodeContainer;
import org.knime.core.node.workflow.SubNodeContainer;
import org.knime.gateway.api.entity.NodeViewEnt;

import com.fasterxml.jackson.databind.util.RawValue;

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
     * @throws IOException if composite data cannot be received
     */
    String getCompositeViewData(final SubNodeContainer snc,
        final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt) throws IOException;

    /**
     * Will start the re-execution process for resetNode and down-stream nodes by updating the values provided by
     * viewValues and returns the page (maybe null), the nodes that are reset and re-executed
     *
     * @param snc the sub-node component container that should be re-executed
     * @param resetNodeIdSuffix The nodeId suffix, i.e., not starting with root, that triggered the re-execution
     * @param viewValues updates of the in-component-node that triggered the re-execution
     * @param createNodeViewEnt function to generate the view of a native node
     * @return the re-executed or re-executing page. This will be used to handle the partial re-execution to show
     *         already views of nodes that are already executed
     * @throws IOException if re-execution cannot be triggered
     */
    PageContainer triggerComponentReexecution(final SubNodeContainer snc, final String resetNodeIdSuffix,
        final Map<String, String> viewValues, final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt)
        throws IOException;

    /**
     * Will start the re-execution process by updating the values provided by viewValues and returns the page (maybe
     * null), the nodes that are reset and re-executed
     *
     * @param snc the sub-node component container that should be re-executed
     * @param viewValues updates of the in-component-nodes
     * @param createNodeViewEnt function to generate the view of a native
     * @return the re-executed or re-executing page. This will be used to handle the partial re-execution to show
     *         already views of nodes that are already executed
     * @throws IOException if re-execution cannot be triggered
     *
     */
    PageContainer triggerCompleteComponentReexecution(final SubNodeContainer snc, final Map<String, String> viewValues,
        final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt) throws IOException;

    /**
     * Set the view values as new default values for the component.
     * Will re-execute if needed.
     *
     * @param snc the container of the component
     * @param viewValues the view values to set as new default values
     * @throws IOException if the view values cannot be set as new default values
     */
    void setViewValuesAsNewDefault(final SubNodeContainer snc, final Map<String, String> viewValues) throws IOException;

    /**
     * Query the current page while reexecuting
     *
     * @param snc the container of the component to reexecute
     * @param nodeIdThatTriggered The ID of the node that triggered the reexecution from within a component
     * @param createNodeViewEnt A function that creates a NodeViewEntity from a native node container
     * @return the re-executed or re-executing page. This will be used to handle the partial re-execution to show
     *         already views of nodes that are already executed
     * @throws IOException if reexecution status cannot be polled
     *
     */
    PageContainer pollComponentReexecutionStatus(final SubNodeContainer snc, final String nodeIdThatTriggered,
        final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt) throws IOException;

    /**
     * Query the complete, current page while reexecuting
     *
     * @param snc the container of the component to reexecute
     * @param createNodeViewEnt A function that creates a NodeViewEntity from a native node container
     * @return the re-executed or re-executing page. This will be used to handle the partial re-execution to show
     *         already views of nodes that are already executed
     * @throws IOException if reexecution status cannot be polled
     *
     */
    PageContainer pollCompleteComponentReexecutionStatus(final SubNodeContainer snc,
        final Function<NativeNodeContainer, NodeViewEnt> createNodeViewEnt) throws IOException;

    // TODO(NXT-3423): Duplicated from org.knime.core.wizard.rpc.PageContainer. Needs deduplication
    /**
     * Object that contains the wizard page and some additional information.
     *
     * @author Martin Horn, KNIME GmbH, Konstanz, Germany
     * @since 4.5
     */
    interface PageContainer {

        /**
         * @return the nodes that have been reset by a re-execution event and are effectively re-executed (no longer
         *         pending re-execution; e.g. finished, failed, deactivated, etc.) or an empty list if the nodes reset
         *         by the re-execution event are still awaiting execution.
         */
        List<String> getReexecutedNodes();

        /**
         * @return the nodes that have been reset or <code>null</code> if the component is in executed state and
         *         {@link #getPage()} returns page content
         */
        List<String> getResetNodes();

        /**
         * Returns the actual page content, i.e. as json-serialized object.
         *
         * @return the actual page content or <code>null</code> if the component is in execution
         */
        RawValue getPage();

    }

}
