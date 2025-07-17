/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.org; Email: contact@knime.org
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
 * ---------------------------------------------------------------------
 *
 * History
 *   Mar 26, 2025 (franziskaobergfell): created
 */
package org.knime.gateway.api.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.util.CheckUtils;

/**
 * Describes <b>"known"/"expected" exceptions</b>. As a superset of checked exceptions, "known" exceptions represent
 * these explicitly anticipated by the application logic. A known exception will have the application react in a
 * graceful and coordinated manner. An example is the inability to carry out a user action such as renaming a file.
 * <p>
 * Any exception that does not implement this class is considered an <b>"unknown"/"unexpected"</b> exception. These are
 * failure cases which should not occur during normal operation of the application. An example is a
 * {@code NullPointerException} thrown by a faulty implementation. By its nature, the application can only react to such
 * exceptions in a general manner.
 *
 * @author Franziska Obergfell, KNIME GmbH, Konstanz, Germany
 * @since 5.5
 */
public abstract class GatewayException extends Exception {

    private static final long serialVersionUID = 1L;

    private static final NodeLogger LOGGER = NodeLogger.getLogger(GatewayException.class);

    private static final String STATUS_KEY = "status";

    private static final String TITLE_KEY = "title";

    private static final String DETAILS_KEY = "details";

    private static final Set<String> BUILT_IN_PROPERTIES = Set.of(STATUS_KEY, TITLE_KEY, DETAILS_KEY);

    private final Map<String, String> m_properties = new LinkedHashMap<>();

    private final boolean m_canCopy;

    private static String createMessage(final String title, final List<String> details) {
        final var sb = new StringBuilder(title);
        if (details != null) {
            details.forEach(detail -> sb.append("\n * ").append(detail));
        }
        return sb.toString();
    }

    /**
     * Creates an exception meant to be sent to the front end.
     *
     * @param status status code, {@code -1} if not applicable
     * @param title issue title, not {@code null}
     * @param details issue details, may be {@code null}
     * @param additionalProps additional properties, may be {@code null}
     * @param canCopy flag indicating whether the problem description is supposed to be copyable
     * @param cause cause of the problem, may be {@code null}
     * @since 5.7
     */
    protected GatewayException(final int status, final String title, final List<String> details,
        final Map<String, String> additionalProps, final boolean canCopy, final Throwable cause) {
        super(createMessage(title, details), cause);

        // add the additional properties first so they are overwritten by the built-in ones
        if (additionalProps != null) {
            m_properties.putAll(additionalProps);
        }

        if (status >= 0) {
            m_properties.put(STATUS_KEY, Integer.toString(status));
        }
        m_properties.put(TITLE_KEY, CheckUtils.checkArgumentNotNull(title));
        m_properties.put(DETAILS_KEY, details == null ? ""
            : details.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining("\n")));
        m_canCopy = canCopy;
    }

    /**
     * Gets the status code of the exception as per "Problem Details” / RFC9457 standard.
     *
     * @return Exception title property if present or {@code null} if not present
     * @since 5.7
     */
    public OptionalInt getStatus() {
        final var statusStr = m_properties.get(STATUS_KEY);
        if (!StringUtils.isBlank(statusStr)) {
            try {
                return OptionalInt.of(Integer.parseInt(statusStr));
            } catch (final NumberFormatException e) {
                LOGGER.debug("Could not parse status code", e);
            }
        }
        return OptionalInt.empty();
    }

    /**
     * Gets the title of the exception as per "Problem Details” / RFC9457 standard.
     *
     * @return Exception title property if present or {@code null} if not present
     */
    public String getTitle() {
        return m_properties.get(TITLE_KEY);
    }

    /**
     * Gets the details of the exception as per "Problem Details” / RFC9457 standard.
     *
     * @return Exception details property if present or {@code null} if not present
     * @since 5.7
     */
    public List<String> getDetails() {
        final var detailsString = m_properties.get(DETAILS_KEY);
        return StringUtils.isBlank(detailsString) ? List.of() : detailsString.lines().toList();
    }

    /**
     * Checks whether exception properties can be copied
     *
     * @return {@code true} if copying properties is possible, {@code false} otherwise.
     */
    public boolean isCanCopy() {
        return m_canCopy;
    }

    /**
     * Add a new property to the exception.
     *
     * @param key the name of the property to be set
     * @param value the value the property should be set to
     */
    public void addProperty(final String key, final String value) {
        m_properties.put(key, value);
    }

    /**
     * Retrieves additional exception properties, excluding "title" and "details". Map of property names to property
     * value.
     *
     * @return Key-values pairs of additional properties, excluding "title" and "details".
     */
    public Map<String, String> getAdditionalProperties() {
        return m_properties.entrySet().stream() //
            .filter(entry -> !BUILT_IN_PROPERTIES.contains(entry.getKey())) //
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Required builder phase to set the title of the exception.
     *
     * @param <T> type of the resulting exception
     * @since 5.7
     */
    public interface NeedsTitle<T extends GatewayException> {

        /**
         * Sets the title of the exception.
         *
         * @param title title of the exception, must not be {@code null}
         * @return next builder phase
         */
        NeedsDetails<T> withTitle(String title);
    }

    /**
     * Required builder phase to set the details of the exception.
     *
     * @param <T> type of the resulting exception
     * @since 5.7
     */
    public interface NeedsDetails<T extends GatewayException> {

        /**
         * Sets the details of the exception.
         *
         * @param details details of the exception, must not be {@code null}
         * @return next builder phase
         */
        NeedsCopyFlag<T> withDetails(Collection<String> details);

        /**
         * Sets the details of the exception.
         *
         * @param details details of the exception, must not be {@code null}
         * @return next builder phase
         */
        default NeedsCopyFlag<T> withDetails(final String... details) {
            return withDetails(List.of(details));
        }
    }

    /**
     * Required builder phase to set the copy flag of the exception.
     *
     * @param <T> type of the resulting exception
     * @since 5.7
     */
    public interface NeedsCopyFlag<T extends GatewayException> {

        /**
         * Sets whether the user should be able to copy failure details.
         *
         * @param canCopy {@code true} if the user should be able to copy failure details, {@code false} otherwise
         * @return next builder phase
         */
        FinalStage<T> canCopy(boolean canCopy);
    }

    /**
     * Final stage of the builder pattern to set optional properties of the exception and build it.
     *
     * @param <T> type of the resulting exception
     * @since 5.7
     */
    public interface FinalStage<T> {

        /**
         * Sets additional properties of the exception.
         *
         * @param additionalProps additional properties, may be {@code null}
         * @return this builder
         */
        FinalStage<T> withAdditionalProps(Map<String, String> additionalProps);

        /**
         * Sets an additional property of the exception. If the key or value is {@code null}, the property will not
         * be set. If the key is already set, it will be overwritten.
         *
         * @param key name of the property to be set, may be {@code null}
         * @param value value of the property to be set, may be {@code null}
         * @return this builder
         */
        FinalStage<T> withAdditionalProp(String key, String value);

        /**
         * Sets the status code of the exception.
         *
         * @param statusCode status code, {@code -1} if not applicable
         * @return this builder
         */
        FinalStage<T> withStatusCode(int statusCode);

        /**
         * Sets the cause of the exception.
         *
         * @param cause cause of the problem, may be {@code null}
         * @return this builder
         */
        FinalStage<T> withCause(Throwable cause);

        /**
         * Builds the exception instance.
         *
         * @return the built exception
         */
        T build();
    }

    /**
     * Abstract builder class for creating instances of {@link GatewayException}.
     *
     * @param <T> type of the resulting exception
     * @since 5.7
     */
    @SuppressWarnings("javadoc") // only exposed via interfaces anyway
    protected abstract static class Builder<T extends GatewayException>
    implements NeedsTitle<T>, NeedsDetails<T>, NeedsCopyFlag<T>, FinalStage<T> {

        protected int m_statusCode = -1;
        protected String m_title;
        protected List<String> m_details;
        protected final Map<String, String> m_additionalProps = new LinkedHashMap<>();
        protected boolean m_canCopy;
        protected Throwable m_cause;

        @Override
        public NeedsDetails<T> withTitle(final String title) {
            m_title = CheckUtils.checkArgumentNotNull(title);
            return this;
        }

        @Override
        public NeedsCopyFlag<T> withDetails(final Collection<String> details) {
            m_details = List.copyOf(CheckUtils.checkArgumentNotNull(details));
            return this;
        }

        @Override
        public FinalStage<T> canCopy(final boolean canCopy) {
            m_canCopy = canCopy;
            return this;
        }

        @Override
        public FinalStage<T> withAdditionalProp(final String key, final String value) {
            if (key != null && value != null) {
                m_additionalProps.put(key, value);
            }
            return this;
        }

        @Override
        public FinalStage<T> withAdditionalProps(final Map<String, String> additionalProps) {
            if (additionalProps != null) {
                m_additionalProps.putAll(additionalProps);
            }
            return this;
        }

        @Override
        public FinalStage<T> withStatusCode(final int statusCode) {
            m_statusCode = statusCode;
            return this;
        }

        @Override
        public FinalStage<T> withCause(final Throwable cause) {
            m_cause = cause;
            return this;
        }
    }
}
