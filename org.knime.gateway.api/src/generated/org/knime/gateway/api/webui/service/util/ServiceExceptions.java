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
package org.knime.gateway.api.webui.service.util;

/**
 * Summarizes auto-generated exceptions that can occur in the executor.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
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

    
}
