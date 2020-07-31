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
package org.knime.gateway.api.webui.entity;

import java.math.BigDecimal;

import org.knime.gateway.api.entity.GatewayEntityBuilder;


import org.knime.gateway.api.entity.GatewayEntity;

/**
 * Represents the node&#39;s progress.
 * 
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"com.knime.gateway.codegen.GatewayCodegen", "src-gen/api/web-ui/configs/org.knime.gateway.api-config.json"})
public interface NodeProgressEnt extends GatewayEntity {


  /**
   * The actual progress, a value between 0 and 1.
   * @return progress 
   **/
  public BigDecimal getProgress();

  /**
   * A progress message.
   * @return message 
   **/
  public String getMessage();


    /**
     * The builder for the entity.
     */
    public interface NodeProgressEntBuilder extends GatewayEntityBuilder<NodeProgressEnt> {

        /**
         * The actual progress, a value between 0 and 1.
         * 
         * @param progress the property value,  
         * @return this entity builder for chaining
         */
        NodeProgressEntBuilder setProgress(BigDecimal progress);
        
        /**
         * A progress message.
         * 
         * @param message the property value,  
         * @return this entity builder for chaining
         */
        NodeProgressEntBuilder setMessage(String message);
        
        
        /**
        * Creates the entity from the builder.
        * 
        * @return the entity
        * @throws IllegalArgumentException most likely in case when a required property hasn't been set
        */
        @Override
        NodeProgressEnt build();
    
    }

}
