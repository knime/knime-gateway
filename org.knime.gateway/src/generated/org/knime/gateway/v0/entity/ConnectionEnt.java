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
package org.knime.gateway.v0.entity;

import org.knime.gateway.v0.entity.XYEnt;

import org.knime.gateway.entity.GatewayEntityBuilder;


import org.knime.gateway.entity.GatewayEntity;

/**
 * A single connection between two nodes.
 * 
 * @author Martin Horn, University of Konstanz
 */
@javax.annotation.Generated(value = "org.knime.gateway.codegen.GatewayCodegen", date = "2018-01-10T17:43:16.679+01:00")
public interface ConnectionEnt extends GatewayEntity {

  /**
   * The type of the connection (standard, workflow input / output /through).
   */
  public enum TypeEnum {
    STD("STD"),
    
    WFMIN("WFMIN"),
    
    WFMOUT("WFMOUT"),
    
    WFMTHROUGH("WFMTHROUGH");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

  }


  /**
   * The destination node.
   * @return dest , never <code>null</code>
   **/
  public String getDest();

  /**
   * The destination port, starting at 0.
   * @return destPort , never <code>null</code>
   **/
  public Integer getDestPort();

  /**
   * The source node.
   * @return source , never <code>null</code>
   **/
  public String getSource();

  /**
   * The source port, starting at 0.
   * @return sourcePort , never <code>null</code>
   **/
  public Integer getSourcePort();

  /**
   * Whether the connection can currently be deleted.
   * @return deletable 
   **/
  public Boolean isDeletable();

  /**
   * Whether it&#39;s a connection between two flow variable ports.
   * @return flowVariablePortConnection 
   **/
  public Boolean isFlowVariablePortConnection();

  /**
   * Get bendPoints
   * @return bendPoints 
   **/
  public java.util.List<XYEnt> getBendPoints();

  /**
   * The type of the connection (standard, workflow input / output /through).
   * @return type 
   **/
  public TypeEnum getType();


    /**
     * The builder for the entity.
     */
    public interface ConnectionEntBuilder extends GatewayEntityBuilder<ConnectionEnt> {

        /**
         * The destination node.
         * 
         * @param dest the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setDest(String dest);
        
        /**
         * The destination port, starting at 0.
         * 
         * @param destPort the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setDestPort(Integer destPort);
        
        /**
         * The source node.
         * 
         * @param source the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setSource(String source);
        
        /**
         * The source port, starting at 0.
         * 
         * @param sourcePort the property value, NOT <code>null</code>! 
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setSourcePort(Integer sourcePort);
        
        /**
         * Whether the connection can currently be deleted.
         * 
         * @param deletable the property value,  
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setDeletable(Boolean deletable);
        
        /**
         * Whether it&#39;s a connection between two flow variable ports.
         * 
         * @param flowVariablePortConnection the property value,  
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setFlowVariablePortConnection(Boolean flowVariablePortConnection);
        
        /**
         * 
         * @param bendPoints the property value,  
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setBendPoints(java.util.List<XYEnt> bendPoints);
        
        /**
         * The type of the connection (standard, workflow input / output /through).
         * 
         * @param type the property value,  
         * @return this entity builder for chaining
         */
        ConnectionEntBuilder setType(TypeEnum type);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        ConnectionEnt build();
    
    }

}
