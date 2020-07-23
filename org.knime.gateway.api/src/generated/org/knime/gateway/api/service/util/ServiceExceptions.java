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
package org.knime.gateway.api.service.util;

/**
 * Summarizes auto-generated exceptions that can occur in the executor.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
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
    * A resource couldn&#39;t be found.
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
    * If the an action is not allowed because it&#39;s not applicable or it doesn&#39;t exist.
    */
    public static class ActionNotAllowedException extends Exception {
        public ActionNotAllowedException(String message) {
            super(message);
        }
        
        public ActionNotAllowedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

   /**
    * If the request is invalid for a reason.
    */
    public static class InvalidRequestException extends Exception {
        public InvalidRequestException(String message) {
            super(message);
        }
        
        public InvalidRequestException(String message, Throwable cause) {
            super(message, cause);
        }
    }

   /**
    * If settings couldn&#39;t be applied.
    */
    public static class InvalidSettingsException extends Exception {
        public InvalidSettingsException(String message) {
            super(message);
        }
        
        public InvalidSettingsException(String message, Throwable cause) {
            super(message, cause);
        }
    }

   /**
    * If node is not in the right state to apply the settings.
    */
    public static class IllegalStateException extends Exception {
        public IllegalStateException(String message) {
            super(message);
        }
        
        public IllegalStateException(String message, Throwable cause) {
            super(message, cause);
        }
    }

   /**
    * If a wizard page is not available.
    */
    public static class NoWizardPageException extends Exception {
        public NoWizardPageException(String message) {
            super(message);
        }
        
        public NoWizardPageException(String message, Throwable cause) {
            super(message, cause);
        }
    }

   /**
    * If the executor got a timeout, e.g., because a workflow didn&#39;t finish execution before the timeout.
    */
    public static class TimeoutException extends Exception {
        public TimeoutException(String message) {
            super(message);
        }
        
        public TimeoutException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    
}
