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
 *   Jul 18, 2022 (hornm): created
 */
package org.knime.gateway.impl;

import java.util.List;
import java.util.Optional;

import org.knime.core.customization.APCustomization;
import org.knime.core.customization.APCustomizationProviderService;
import org.knime.core.webui.node.port.PortViewManager;
import org.knime.core.webui.node.port.PortViewManager.PortViewDescriptor;
import org.knime.gateway.impl.node.port.DirectAccessTablePortViewFactory;
import org.knime.gateway.impl.node.port.FlowVariablePortViewFactory;
import org.knime.gateway.impl.node.port.FlowVariableSpecViewFactory;
import org.knime.gateway.impl.node.port.ImagePortViewFactory;
import org.knime.gateway.impl.node.port.StatisticsPortViewFactory;
import org.knime.gateway.impl.node.port.TablePortViewFactory;
import org.knime.gateway.impl.node.port.TableSpecViewFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Bundle activator of the gateway.impl plugin.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class GatewayImplPlugin implements BundleActivator {

    private static GatewayImplPlugin instance;

    private ServiceTracker<APCustomizationProviderService, APCustomizationProviderService>
            m_customizationServiceTracker;

    @Override
    public void start(final BundleContext context) throws Exception {
        instance = this; // NOSONAR
        // Temporary solution to register port views with port types/objects.
        // To be removed once it's part of the PortObject/PortType API.
        // NOTE: the port-object-class-names are used to avoid the initialization of the PortTypeRegistry at this point

        PortViewManager.registerPortViews("org.knime.core.node.BufferedDataTable", //
            List.of( //
                new PortViewDescriptor("Table", new TableSpecViewFactory()), //
                new PortViewDescriptor("Table", new TablePortViewFactory()), //
                new PortViewDescriptor("Statistics", new StatisticsPortViewFactory())//
            ), //
            List.of(0, 2), //
            List.of(1, 2)//
        );

        PortViewManager.registerPortViews("org.knime.core.node.port.flowvariable.FlowVariablePortObject", //
            List.of(//
                new PortViewDescriptor("Flow variables", new FlowVariableSpecViewFactory()), //
                new PortViewDescriptor("Flow variables", new FlowVariablePortViewFactory())), //
            List.of(0), //
            List.of(1)//
        );

        PortViewManager.registerPortViews("org.knime.core.node.port.image.ImagePortObject", //
            List.of(new PortViewDescriptor("Image", new ImagePortViewFactory())), //
            List.of(), //
            List.of(0)//
        );

        PortViewManager.registerPortViews("org.knime.core.data.DirectAccessTable", //
            List.of(new PortViewDescriptor("Table", new DirectAccessTablePortViewFactory())), List.of(), List.of(0));

        m_customizationServiceTracker = new ServiceTracker<>(context, APCustomizationProviderService.class, null);
        m_customizationServiceTracker.open();
    }

    /** @return the currently active instance (after bundle is started). */
    public static GatewayImplPlugin getInstance() {
        return instance;
    }

    /** @return The currently active customisation, not null. */
    public APCustomization getCustomization() {
        return Optional.ofNullable(m_customizationServiceTracker.getService())
                .map(APCustomizationProviderService::getCustomization).orElse(APCustomization.DEFAULT);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        m_customizationServiceTracker.close();
        m_customizationServiceTracker = null;
        instance = null; // NOSONAR
    }

}
