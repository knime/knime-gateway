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
 * History
 *   Created on Aug 7, 2020 by hornm
 */
package org.knime.gateway.testing.helper.rpc.node;

/**
 * Data service implementation for testing.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ServiceImpl implements Service {

    @Override
    public String method() {
        return "result1234";
    }

}
