/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 *
 */
package com.knime.gateway.local.util.missing;

import java.io.IOException;

import javax.swing.JComponent;

import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.port.AbstractPortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortObjectZipInputStream;
import org.knime.core.node.port.PortObjectZipOutputStream;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;

/**
 * Placeholder for a missing/unknown port object (i.e. port type) that cannot be found in the {@link PortTypeRegistry}
 * (e.g. because the respective extension is not available).
 *
 * @author Martin Horn, University of Konstanz
 */
public class MissingPortObject extends AbstractPortObject {

    /**
     * Convenience accessor for the port type.
     */
    public static final PortType TYPE = PortTypeRegistry.getInstance().getPortType(MissingPortObject.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSummary() {
        return "Unknown Port Object (Extension not available)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortObjectSpec getSpec() {
        return new PortObjectSpec() {

            @Override
            public JComponent[] getViews() {
                return null;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent[] getViews() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void save(final PortObjectZipOutputStream out, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void load(final PortObjectZipInputStream in, final PortObjectSpec spec, final ExecutionMonitor exec)
        throws IOException, CanceledExecutionException {
        //nothing to do
    }

}
