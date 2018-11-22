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
package com.knime.gateway.service;

/**
 * General exception when something goes wrong with a service call.
 *
 * @author Martin Horn, University of Konstanz
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 2608597105909156098L;

    /**
     * @see RuntimeException#RuntimeException(String)
     *
     * @param message
     */
    public ServiceException(final String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     *
     * @param message
     * @param cause
     */
    public ServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
