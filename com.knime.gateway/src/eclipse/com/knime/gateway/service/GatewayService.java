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
package com.knime.gateway.service;

import com.knime.gateway.entity.GatewayEntity;

/**
 * Marks gateway services, i.e. usually singleton classes with methods that define the communication between remote
 * points. The service methods either take entities (see {@link GatewayEntity}) or primitives as parameters (or return
 * type).
 *
 * Note: all gateway service methods that are intended to invoke a method on a particular KNIME executor are required to
 * have the workflow/job id as the very first parameter. It will be extracted by the server that mediates the
 * communication to the executor.
 *
 * @author Martin Horn, University of Konstanz
 */
public interface GatewayService {

}
