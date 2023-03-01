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
package org.knime.gateway.impl.webui.entity;

import static org.knime.gateway.api.util.EntityUtil.immutable;


import org.knime.gateway.api.webui.entity.PortGroupEnt;

/**
 * Within natives nodes, ports belong to port groups.  Port groups in turn are used to describe whether and how many additional input or output ports of a certain type can be added to a node depending on the current state of the node.
 *
 * @param inputRange
 * @param outputRange
 * @param canAddInPort
 * @param canAddOutPort
 * @param supportedPortTypeIds
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.impl-config.json"})
public record DefaultPortGroupEnt(
    java.util.List<Integer> inputRange,
    java.util.List<Integer> outputRange,
    Boolean canAddInPort,
    Boolean canAddOutPort,
    java.util.List<String> supportedPortTypeIds) implements PortGroupEnt {

    /**
     * Validation for required parameters not being {@code null}.
     */
    public DefaultPortGroupEnt {
    }

    @Override
    public String getTypeID() {
        return "PortGroup";
    }
  
    @Override
    public java.util.List<Integer> getInputRange() {
        return inputRange;
    }
    
    @Override
    public java.util.List<Integer> getOutputRange() {
        return outputRange;
    }
    
    @Override
    public Boolean isCanAddInPort() {
        return canAddInPort;
    }
    
    @Override
    public Boolean isCanAddOutPort() {
        return canAddOutPort;
    }
    
    @Override
    public java.util.List<String> getSupportedPortTypeIds() {
        return supportedPortTypeIds;
    }
    
    /**
     * A builder for {@link DefaultPortGroupEnt}.
     */
    public static class DefaultPortGroupEntBuilder implements PortGroupEntBuilder {

        private java.util.List<Integer> m_inputRange;

        private java.util.List<Integer> m_outputRange;

        private Boolean m_canAddInPort;

        private Boolean m_canAddOutPort;

        private java.util.List<String> m_supportedPortTypeIds;

        @Override
        public DefaultPortGroupEntBuilder setInputRange(java.util.List<Integer> inputRange) {
             m_inputRange = inputRange;
             return this;
        }

        @Override
        public DefaultPortGroupEntBuilder setOutputRange(java.util.List<Integer> outputRange) {
             m_outputRange = outputRange;
             return this;
        }

        @Override
        public DefaultPortGroupEntBuilder setCanAddInPort(Boolean canAddInPort) {
             m_canAddInPort = canAddInPort;
             return this;
        }

        @Override
        public DefaultPortGroupEntBuilder setCanAddOutPort(Boolean canAddOutPort) {
             m_canAddOutPort = canAddOutPort;
             return this;
        }

        @Override
        public DefaultPortGroupEntBuilder setSupportedPortTypeIds(java.util.List<String> supportedPortTypeIds) {
             m_supportedPortTypeIds = supportedPortTypeIds;
             return this;
        }

        @Override
        public DefaultPortGroupEnt build() {
            return new DefaultPortGroupEnt(
                immutable(m_inputRange),
                immutable(m_outputRange),
                immutable(m_canAddInPort),
                immutable(m_canAddOutPort),
                immutable(m_supportedPortTypeIds));
        }
    
    }

}
