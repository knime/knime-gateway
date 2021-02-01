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
 */
package org.knime.gateway.testing.helper.rpc.port;

import javax.swing.JComponent;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DirectAccessTable;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.ModelContentRO;
import org.knime.core.node.ModelContentWO;
import org.knime.core.node.port.AbstractSimplePortObjectSpec;
import org.knime.core.node.port.DataTableSpecProvider;

/**
 * To test {@link DirectAccessTable} in the remote workflow editor.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class DirectAccessTablePortObjectSpec extends AbstractSimplePortObjectSpec implements DataTableSpecProvider {


    @SuppressWarnings("javadoc")
    public static class DirectAccessTablePortObjectSpecSerializer
        extends AbstractSimplePortObjectSpecSerializer<DirectAccessTablePortObjectSpec> {
    }

    private DataTableSpec m_spec;

    /**
     * No-arg for de-/serialization
     */
    public DirectAccessTablePortObjectSpec() {
        //
    }

    DirectAccessTablePortObjectSpec(final DataTableSpec spec) {
        m_spec = spec;
    }

    @Override
    public DataTableSpec getDataTableSpec() {
        return m_spec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent[] getViews() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void save(final ModelContentWO model) {
        //
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void load(final ModelContentRO model) throws InvalidSettingsException {
        //
    }

}
