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
package com.knime.gateway.remote;

import com.knime.gateway.remote.service.util.JaversRepository;
import com.knime.gateway.remote.service.util.WorkflowEntRepository;

/**
 * Tests for {@link WorkflowEntRepository} and it's {@link JaversRepository}-implementation.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class JaversRepositoryTest extends AbstractWorkflowEntRepositoryTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected WorkflowEntRepository createRepo() {
        return new JaversRepository();
    }
}
