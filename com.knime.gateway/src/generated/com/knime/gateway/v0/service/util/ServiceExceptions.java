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
package com.knime.gateway.v0.service.util;

/**
 * Summarizes auto-generated exceptions that can occur in the executor.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen")
public final class ServiceExceptions {

    /**
    * The requested node was not found.
    */
    public static class NodeNotFoundException extends Exception {
        public NodeNotFoundException(String message) {
            super(message);
        }
        
        public NodeNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
    * The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
    */
    public static class NotASubWorkflowException extends Exception {
        public NotASubWorkflowException(String message) {
            super(message);
        }
        
        public NotASubWorkflowException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
    * A resource couldn&#39;t be found. Please refer to the exception message for more details.
    */
    public static class NotFoundException extends Exception {
        public NotFoundException(String message) {
            super(message);
        }
        
        public NotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
    * A operation is not allowed to be performed. Please refer ot the exception message for more details.
    */
    public static class NotAllowedException extends Exception {
        public NotAllowedException(String message) {
            super(message);
        }
        
        public NotAllowedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    
}
