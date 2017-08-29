/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
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
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
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
 *   Dec 5, 2016 (hornm): created
 */
package org.knime.gateway.codegen.def;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.knime.core.node.util.CheckUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents a service method, i.e. its name, the return type, parameters, description etc.
 *
 * @author Martin Horn, University of Konstanz
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.NONE)
@JsonPropertyOrder({"name", "operation", "description", "result", "params"})
public class ServiceMethod {

    /** HTTP like verb. */
    public enum Operation {
        Get,
        Set,
        Update,
        Delete;

        @JsonValue
        public String getNameLowerCase() {
            return name().toLowerCase();
        }

        @JsonCreator
        public static Operation parseFromLowerCase(final String key) {
            return key == null ? null : Operation.valueOf(StringUtils.capitalize(key));
        }

    }

    private final String m_description;

    private final String m_name;

    private final Operation m_operation;

    private final MethodReturn m_result;

    private final List<MethodParam> m_parameters;

    /**
     * @param name
     * @param operation
     * @param description
     * @param returnType
     * @param parameters
     *
     */
    public ServiceMethod(
        final String name,
        final Operation operation,
        final String description,
        final String returnType,
        final MethodParam... parameters) {
        this(name, operation, description, new MethodReturn("BERND", returnType), parameters);
    }

    /**
     * @param operation TODO
     *
     */
    @JsonCreator
    public ServiceMethod(
        @JsonProperty("name") final String name,
        @JsonProperty("operation") final Operation operation,
        @JsonProperty("description") final String description,
        @JsonProperty("result") final MethodReturn result,
        @JsonProperty("params") final MethodParam... parameters) {
        m_name = name;
        m_operation = CheckUtils.checkArgumentNotNull(operation, "Operation must not be null");
        m_description = description;
        m_result = result;
        m_parameters = Arrays.asList(parameters);
    }

    /**
     * @return the method name
     */
    @JsonProperty("name")
    public String getNameWithoutOperation() {
        return m_name;
    }

    /**
     * @return the method name compose of the operation name (all lower case) followed by the identifier (also referred
     *         to as 'name' - a bit confusing -> TODO)
     */
    @JsonIgnore
    public String getName() {
        return m_operation.getNameLowerCase() + m_name;
    }

    /**
     * @return the operation
     */
    public Operation getOperation() {
        return m_operation;
    }

    /**
     * @return the description
     */
    @JsonProperty("description")
    public String getDescription() {
        return m_description;
    }

    /**
     * @return the return value of the service method
     */
    @JsonProperty("result")
    public MethodReturn getResult() {
        return m_result;
    }

    /**
     * @return all service method parameters
     */
    @JsonProperty("params")
    public List<MethodParam> getParameters() {
        return m_parameters;
    }
}
