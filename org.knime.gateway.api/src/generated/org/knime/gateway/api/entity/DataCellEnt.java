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
