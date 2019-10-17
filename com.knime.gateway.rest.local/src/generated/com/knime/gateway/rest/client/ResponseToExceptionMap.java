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
package com.knime.gateway.rest.client;

import java.util.Optional;

/**
 * Maps the triple (service, method, exception) to a http-response.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
@javax.annotation.Generated(value = {"org.knime.gateway.codegen.GatewayCodegen", "src-gen/com.knime.gateway.rest.local-config.json"})
public class ResponseToExceptionMap {

    private ResponseToExceptionMap() {
        //utility class
    }

    /**
     * Determines for a particular service-method and http response code a (optional) exception name.
     *
     * @param service
     * @param method
     * @param status
     * @return the exception name or an empty optional if there is no mapping
     */
   	public static Optional<String> getExceptionName(String service, String method, int status) {
		if("WizardExecution".equals(service) && "executeToNextPage".equals(method) && 406 == status) {
			return Optional.of("InvalidSettingsException");
		} else {
			return Optional.empty();	
		}
   	}


}
