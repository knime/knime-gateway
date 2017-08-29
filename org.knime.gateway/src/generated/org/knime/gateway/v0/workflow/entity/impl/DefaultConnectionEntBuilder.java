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
 */
package org.knime.gateway.v0.workflow.entity.impl;

import java.util.List;
import org.knime.gateway.v0.workflow.entity.ConnectionEnt;
import org.knime.gateway.v0.workflow.entity.XYEnt;
import org.knime.gateway.v0.workflow.entity.builder.ConnectionEntBuilder;

import org.knime.gateway.entities.EntityBuilderFactory;
import org.knime.gateway.entities.EntityBuilderManager;

/**
 * Default implementation of the ConnectionEntBuilder-interface. E.g. used if no other {@link EntityBuilderFactory}
 * implementation (provided via the respective extension point, see {@link EntityBuilderManager}) is available.
 *
 * @author Martin Horn, University of Konstanz
 */
 public class DefaultConnectionEntBuilder implements ConnectionEntBuilder {
    
	String m_Dest;
	int m_DestPort;
	String m_Source;
	int m_SourcePort;
	boolean m_IsDeleteable;
	boolean m_IsFlowVariablePortConnection;
	List<XYEnt> m_BendPoints;
	String m_Type;

	@Override
    public ConnectionEnt build() {
        return new DefaultConnectionEnt(this);
    }

	@Override
    public ConnectionEntBuilder setDest(final String Dest) {
		m_Dest = Dest;			
        return this;
    }
        
	@Override
    public ConnectionEntBuilder setDestPort(final int DestPort) {
		m_DestPort = DestPort;			
        return this;
    }
        
	@Override
    public ConnectionEntBuilder setSource(final String Source) {
		m_Source = Source;			
        return this;
    }
        
	@Override
    public ConnectionEntBuilder setSourcePort(final int SourcePort) {
		m_SourcePort = SourcePort;			
        return this;
    }
        
	@Override
    public ConnectionEntBuilder setIsDeleteable(final boolean IsDeleteable) {
		m_IsDeleteable = IsDeleteable;			
        return this;
    }
        
	@Override
    public ConnectionEntBuilder setIsFlowVariablePortConnection(final boolean IsFlowVariablePortConnection) {
		m_IsFlowVariablePortConnection = IsFlowVariablePortConnection;			
        return this;
    }
        
	@Override
    public ConnectionEntBuilder setBendPoints(final List<XYEnt> BendPoints) {
		m_BendPoints = BendPoints;			
        return this;
    }
        
	@Override
    public ConnectionEntBuilder setType(final String Type) {
		m_Type = Type;			
        return this;
    }
        
}

