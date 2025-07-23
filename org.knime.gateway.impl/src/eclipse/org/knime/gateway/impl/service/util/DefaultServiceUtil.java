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
package org.knime.gateway.impl.service.util;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;

import org.knime.core.data.RowKey;
import org.knime.core.node.workflow.NodeContainer;
import org.knime.core.webui.node.NodePortWrapper;
import org.knime.core.webui.node.NodeWrapper;
import org.knime.core.webui.node.port.PortViewManager;
import org.knime.core.webui.node.view.NodeViewManager;
import org.knime.core.webui.node.view.table.TableViewManager;
import org.knime.gateway.api.webui.entity.SelectionEventEnt;
import org.knime.gateway.impl.webui.service.events.SelectionEventBus;

/**
 * Helper methods useful for the default service implementations (shared between different api implementations, i.e.
 * java-ui, web-ui and webportal).
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class DefaultServiceUtil {

    private DefaultServiceUtil() {
        //utility class
    }

    @SuppressWarnings("unchecked")
    public static <N extends NodeWrapper> void updateDataPointSelection(final NodeContainer nc, final String mode,
        final List<String> selection, final Function<NodeContainer, N> getNodeWrapper) {
        var nodeWrapper = getNodeWrapper.apply(nc);
        TableViewManager<N> tableViewManager;
        if (nodeWrapper instanceof NodePortWrapper) {
            tableViewManager = (TableViewManager<N>)PortViewManager.getInstance().getTableViewManager();
        } else {
            tableViewManager = (TableViewManager<N>)NodeViewManager.getInstance().getTableViewManager();
        }

        Set<RowKey> rowKeys;
        try {
            rowKeys = tableViewManager.callSelectionTranslationService(nodeWrapper, selection);
        } catch (IOException ex) {
            throw new IllegalStateException("Problem translating selection to row keys", ex);
        }
        var hiLiteHandler = tableViewManager.getHiLiteHandler(nodeWrapper).orElseThrow();
        final var selectionEventMode = SelectionEventEnt.ModeEnum.valueOf(mode.toUpperCase(Locale.ROOT));
        SelectionEventBus.processSelectionEvent(hiLiteHandler, nc.getID(), selectionEventMode, true, rowKeys);
    }
}
