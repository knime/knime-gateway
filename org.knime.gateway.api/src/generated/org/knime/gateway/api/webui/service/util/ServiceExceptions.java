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
import java.util.List;
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

        private static final long serialVersionUID = 1L;

        private ServiceCallException(final int status, final String title, final List<String> details,
            final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
            super(status, title, details, additionalProps, canCopy, cause);
        }

        /**
         * Creates a fluent builder for {@link ServiceCallException}.
         *
         * @return fluent builder
         */
        public static NeedsTitle<ServiceCallException> builder() {
            return new Builder<ServiceCallException>() {

                @Override
                public ServiceCallException build() {
                    return new ServiceCallException(m_statusCode, m_title, m_details, m_additionalProps, m_canCopy, m_cause);
                }
            };
        }
    }

   /**
    * If a Gateway service call failed due to a network error.
    */
    public static class NetworkException extends GatewayException {

        private static final long serialVersionUID = 1L;

        private NetworkException(final int status, final String title, final List<String> details,
            final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
            super(status, title, details, additionalProps, canCopy, cause);
        }

        /**
         * Creates a fluent builder for {@link NetworkException}.
         *
         * @return fluent builder
         */
        public static NeedsTitle<NetworkException> builder() {
            return new Builder<NetworkException>() {

                @Override
                public NetworkException build() {
                    return new NetworkException(m_statusCode, m_title, m_details, m_additionalProps, m_canCopy, m_cause);
                }
            };
        }
    }

   /**
    * A description for a given node could not be determined.
    */
    public static class NodeDescriptionNotAvailableException extends GatewayException {

        private static final long serialVersionUID = 1L;

        private NodeDescriptionNotAvailableException(final int status, final String title, final List<String> details,
            final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
            super(status, title, details, additionalProps, canCopy, cause);
        }

        /**
         * Creates a fluent builder for {@link NodeDescriptionNotAvailableException}.
         *
         * @return fluent builder
         */
        public static NeedsTitle<NodeDescriptionNotAvailableException> builder() {
            return new Builder<NodeDescriptionNotAvailableException>() {

                @Override
                public NodeDescriptionNotAvailableException build() {
                    return new NodeDescriptionNotAvailableException(m_statusCode, m_title, m_details, m_additionalProps, m_canCopy, m_cause);
                }
            };
        }
    }

   /**
    * The requested node was not found.
    */
    public static class NodeNotFoundException extends GatewayException {

        private static final long serialVersionUID = 1L;

        private NodeNotFoundException(final int status, final String title, final List<String> details,
            final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
            super(status, title, details, additionalProps, canCopy, cause);
        }

        /**
         * Creates a fluent builder for {@link NodeNotFoundException}.
         *
         * @return fluent builder
         */
        public static NeedsTitle<NodeNotFoundException> builder() {
            return new Builder<NodeNotFoundException>() {

                @Override
                public NodeNotFoundException build() {
                    return new NodeNotFoundException(m_statusCode, m_title, m_details, m_additionalProps, m_canCopy, m_cause);
                }
            };
        }
    }

   /**
    * The requested element was not found.
    */
    public static class NoSuchElementException extends GatewayException {

        private static final long serialVersionUID = 1L;

        private NoSuchElementException(final int status, final String title, final List<String> details,
            final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
            super(status, title, details, additionalProps, canCopy, cause);
        }

        /**
         * Creates a fluent builder for {@link NoSuchElementException}.
         *
         * @return fluent builder
         */
        public static NeedsTitle<NoSuchElementException> builder() {
            return new Builder<NoSuchElementException>() {

                @Override
                public NoSuchElementException build() {
                    return new NoSuchElementException(m_statusCode, m_title, m_details, m_additionalProps, m_canCopy, m_cause);
                }
            };
        }
    }

   /**
    * The requested node is not a sub-workflow (i.e. a meta- or sub-node), but is required to be.
    */
    public static class NotASubWorkflowException extends GatewayException {

        private static final long serialVersionUID = 1L;

        private NotASubWorkflowException(final int status, final String title, final List<String> details,
            final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
            super(status, title, details, additionalProps, canCopy, cause);
        }

        /**
         * Creates a fluent builder for {@link NotASubWorkflowException}.
         *
         * @return fluent builder
         */
        public static NeedsTitle<NotASubWorkflowException> builder() {
            return new Builder<NotASubWorkflowException>() {

                @Override
                public NotASubWorkflowException build() {
                    return new NotASubWorkflowException(m_statusCode, m_title, m_details, m_additionalProps, m_canCopy, m_cause);
                }
            };
        }
    }

   /**
    * If the request is invalid for a reason.
    */
    public static class InvalidRequestException extends GatewayException {

        private static final long serialVersionUID = 1L;

        private InvalidRequestException(final int status, final String title, final List<String> details,
            final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
            super(status, title, details, additionalProps, canCopy, cause);
        }

        /**
         * Creates a fluent builder for {@link InvalidRequestException}.
         *
         * @return fluent builder
         */
        public static NeedsTitle<InvalidRequestException> builder() {
            return new Builder<InvalidRequestException>() {

                @Override
                public InvalidRequestException build() {
                    return new InvalidRequestException(m_statusCode, m_title, m_details, m_additionalProps, m_canCopy, m_cause);
                }
            };
        }
    }

   /**
    * If the an operation is not allowed, e.g., because it&#39;s not applicable.
    */
    public static class OperationNotAllowedException extends GatewayException {

        private static final long serialVersionUID = 1L;

        private OperationNotAllowedException(final int status, final String title, final List<String> details,
            final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
            super(status, title, details, additionalProps, canCopy, cause);
        }

        /**
         * Creates a fluent builder for {@link OperationNotAllowedException}.
         *
         * @return fluent builder
         */
        public static NeedsTitle<OperationNotAllowedException> builder() {
            return new Builder<OperationNotAllowedException>() {

                @Override
                public OperationNotAllowedException build() {
                    return new OperationNotAllowedException(m_statusCode, m_title, m_details, m_additionalProps, m_canCopy, m_cause);
                }
            };
        }
    }

   /**
    * If there was a collision, e.g. due to naming conflicts
    */
    public static class CollisionException extends GatewayException {

        private static final long serialVersionUID = 1L;

        private CollisionException(final int status, final String title, final List<String> details,
            final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
            super(status, title, details, additionalProps, canCopy, cause);
        }

        /**
         * Creates a fluent builder for {@link CollisionException}.
         *
         * @return fluent builder
         */
        public static NeedsTitle<CollisionException> builder() {
            return new Builder<CollisionException>() {

                @Override
                public CollisionException build() {
                    return new CollisionException(m_statusCode, m_title, m_details, m_additionalProps, m_canCopy, m_cause);
                }
            };
        }
    }

   /**
    * If a web request could not be authorized because the space provider isn&#39;t logged in
    */
    public static class LoggedOutException extends GatewayException {

        private static final long serialVersionUID = 1L;

        private LoggedOutException(final int status, final String title, final List<String> details,
            final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
            super(status, title, details, additionalProps, canCopy, cause);
        }

        /**
         * Creates a fluent builder for {@link LoggedOutException}.
         *
         * @return fluent builder
         */
        public static NeedsTitle<LoggedOutException> builder(final String spaceProviderId) {
            return new Builder<LoggedOutException>() {

                @Override
                public LoggedOutException build() {
                    withAdditionalProp("spaceProviderId", spaceProviderId);
                    return new LoggedOutException(m_statusCode, m_title, m_details, m_additionalProps, m_canCopy, m_cause);
                }
            };
        }
    }
}
