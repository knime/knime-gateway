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
package com.knime.gateway.entity;

import com.knime.gateway.service.GatewayService;

/**
 * Represents an object (i.e. a message) to be passed between remote endpoints, usually as parameter or return type of
 * service methods (see {@link GatewayService}).
 *
 * Entities therefor need be able to be serialized in a way and are either composed of other entities or primitives.
 * Entites should be immutable and be created with the respective builder (see {@link GatewayEntityBuilder}).
 *
 * @author Martin Horn, University of Konstanz
 */
public interface GatewayEntity {

    /**
     * @return a unique identifier for the type of the entity
     */
    String getTypeID();

}
