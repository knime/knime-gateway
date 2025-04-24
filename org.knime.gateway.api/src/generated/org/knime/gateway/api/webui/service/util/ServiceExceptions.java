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
 * ------------------------------------------------------------------------
 */
package org.knime.gateway.api.webui.service.util;

import org.knime.gateway.api.service.GatewayException;
import java.util.Map;

/**
 * Summarizes auto-generated exceptions that can occur in the executor.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@jakarta.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public final class ServiceExceptions {

   /**
    * If a Gateway service call failed for some reason.
    */
    public static class ServiceCallException extends GatewayException {
        
        public ServiceCallException(String message) {
            super(false);
            addProperty("message", message);
        }

        public ServiceCallException(String message, Throwable cause) {
            super(false);
            addProperty("message", message);
            initCause(cause);
        }

       /**
        * "De-serialises" the exception from a gateway-problem-description properties. For testing purposes only.
        */
        public ServiceCallException(Map<String, String> gatewayProblemDescription) {
            super(gatewayProblemDescription);
        }

    }

   /**
    * If a Gateway service call failed due to a network error.
    */
    public static class NetworkException extends GatewayException {
        
        public NetworkException(String message) {
            super(false);
            addProperty("message", message);
        }

        public NetworkException(String message, Throwable cause) {
            super(false);
            addProperty("message", message);
            initCause(cause);
        }

       /**
        * "De-serialises" the exception from a gateway-problem-description properties. For testing purposes only.
        */
        public NetworkException(Map<String, String> gatewayProblemDescription) {
            super(gatewayProblemDescription);
        }

    }

   /**
    * A description for a given node could not be determined.
    */
    public static class NodeDescriptionNotAvailableException extends GatewayException {
        
        public NodeDescriptionNotAvailableException(String message) {
            super(false);
            addProperty("message", message);
        }

        public NodeDescriptionNotAvailableException(String message, Throwable cause) {
            super(false);
            addProperty("message", message);
            initCause(cause);
        }

       /**
        * "De-serialises" the exception from a gateway-problem-description properties. For testing purposes only.
        */
        public NodeDescriptionNotAvailableException(Map<String, String> gatewayProblemDescription) {
            super(gatewayProblemDescription);
        }

    }

   /**
    * The requested node was not found.
    */
    public static class NodeNotFoundException extends GatewayException {
        
        public NodeNotFoundException(String message) {
            super(false);
            addProperty("message", message);
        }

        public NodeNotFoundException(String message, Throwable cause) {
            super(false);
            addProperty("message", message);
            initCause(cause);
        }

       /**
        * "De-serialises" the exception from a gateway-problem-description properties. For testing purposes only.
        */
        public NodeNotFoundException(Map<String, String> gatewayProblemDescription) {
            super(gatewayProblemDescription);
        }

    }

   /**
    * The requested element was not found.
    */
    public static class NoSuchElementException extends GatewayException {
        
        public NoSuchElementException(String message) {
            super(false);
            addProperty("message", message);
        }

        public NoSuchElementException(String message, Throwable cause) {
            super(false);
            addProperty("message", message);
            initCause(cause);
        }

       /**
        * "De-serialises" the exception from a gateway-problem-description properties. For testing purposes only.
        */
        public NoSuchElementException(Map<String, String> gatewayProblemDescription) {
            super(gatewayProblemDescription);
        }

    }

   /**
    * The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
    */
    public static class NotASubWorkflowException extends GatewayException {
        
        public NotASubWorkflowException(String message) {
            super(false);
            addProperty("message", message);
        }

        public NotASubWorkflowException(String message, Throwable cause) {
            super(false);
            addProperty("message", message);
            initCause(cause);
        }

       /**
        * "De-serialises" the exception from a gateway-problem-description properties. For testing purposes only.
        */
        public NotASubWorkflowException(Map<String, String> gatewayProblemDescription) {
            super(gatewayProblemDescription);
        }

    }

   /**
    * If the request is invalid for a reason.
    */
    public static class InvalidRequestException extends GatewayException {
        
        public InvalidRequestException(String message) {
            super(false);
            addProperty("message", message);
        }

        public InvalidRequestException(String message, Throwable cause) {
            super(false);
            addProperty("message", message);
            initCause(cause);
        }

       /**
        * "De-serialises" the exception from a gateway-problem-description properties. For testing purposes only.
        */
        public InvalidRequestException(Map<String, String> gatewayProblemDescription) {
            super(gatewayProblemDescription);
        }

    }

   /**
    * If the an operation is not allowed, e.g., because it&#39;s not applicable.
    */
    public static class OperationNotAllowedException extends GatewayException {
        
        public OperationNotAllowedException(String message) {
            super(false);
            addProperty("message", message);
        }

        public OperationNotAllowedException(String message, Throwable cause) {
            super(false);
            addProperty("message", message);
            initCause(cause);
        }

       /**
        * "De-serialises" the exception from a gateway-problem-description properties. For testing purposes only.
        */
        public OperationNotAllowedException(Map<String, String> gatewayProblemDescription) {
            super(gatewayProblemDescription);
        }

    }

   /**
    * If there was an I/O error of some kind.
    */
    public static class IOException extends GatewayException {
        
        public IOException(String message) {
            super(false);
            addProperty("message", message);
        }

        public IOException(String message, Throwable cause) {
            super(false);
            addProperty("message", message);
            initCause(cause);
        }

       /**
        * "De-serialises" the exception from a gateway-problem-description properties. For testing purposes only.
        */
        public IOException(Map<String, String> gatewayProblemDescription) {
            super(gatewayProblemDescription);
        }

    }

   /**
    * If there was a collision, e.g. due to naming conflicts
    */
    public static class CollisionException extends GatewayException {
        
        public CollisionException(String message) {
            super(false);
            addProperty("message", message);
        }

        public CollisionException(String message, Throwable cause) {
            super(false);
            addProperty("message", message);
            initCause(cause);
        }

       /**
        * "De-serialises" the exception from a gateway-problem-description properties. For testing purposes only.
        */
        public CollisionException(Map<String, String> gatewayProblemDescription) {
            super(gatewayProblemDescription);
        }

    }

    
}
