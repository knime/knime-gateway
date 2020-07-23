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
package org.knime.gateway.api.entity;


import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * A cell of a data table.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/org.knime.gateway.api-config.json"})
public interface DataCellEnt extends GatewayEntity {


  /**
   * The type of the data cell represented by the implementing java-class name. If not given, it is assumed that the type can be infered from the data table spec.
   * @return type 
   **/
  public String getType();

  /**
   * The cell value as a string.
   * @return valueAsString 
   **/
  public String getValueAsString();

  /**
   * If it&#39;s a missing cell - The error message is passed via the &#39;valueAsString&#39;-prop.
   * @return missing 
   **/
  public Boolean isMissing();

  /**
   * Whether the &#39;valueAsString&#39;-prop contains a serialized binary string, that is base64-encoded.
   * @return binary 
   **/
  public Boolean isBinary();

  /**
   * Indicates whether there was a problem creating this cell. The details are given via the &#39;valueAsString&#39;-prop.
   * @return problem 
   **/
  public Boolean isProblem();


    /**
     * The builder for the entity.
     */
    public interface DataCellEntBuilder extends GatewayEntityBuilder<DataCellEnt> {

        /**
         * The type of the data cell represented by the implementing java-class name. If not given, it is assumed that the type can be infered from the data table spec.
         * 
         * @param type the property value,  
         * @return this entity builder for chaining
         */
        DataCellEntBuilder setType(String type);
        
        /**
         * The cell value as a string.
         * 
         * @param valueAsString the property value,  
         * @return this entity builder for chaining
         */
        DataCellEntBuilder setValueAsString(String valueAsString);
        
        /**
         * If it&#39;s a missing cell - The error message is passed via the &#39;valueAsString&#39;-prop.
         * 
         * @param missing the property value,  
         * @return this entity builder for chaining
         */
        DataCellEntBuilder setMissing(Boolean missing);
        
        /**
         * Whether the &#39;valueAsString&#39;-prop contains a serialized binary string, that is base64-encoded.
         * 
         * @param binary the property value,  
         * @return this entity builder for chaining
         */
        DataCellEntBuilder setBinary(Boolean binary);
        
        /**
         * Indicates whether there was a problem creating this cell. The details are given via the &#39;valueAsString&#39;-prop.
         * 
         * @param problem the property value,  
         * @return this entity builder for chaining
         */
        DataCellEntBuilder setProblem(Boolean problem);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        DataCellEnt build();
    
    }

}
