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
package com.knime.gateway.jsonrpc.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knime.gateway.json.util.ObjectMapperUtil;

/**
 * Implementation of the {@link GenericServerRequestHandler} extension point that executes json-rpc 2.0 requests and
 * delegates the respective calls to the default service implementations. However, with one difference to the
 * {@link JsonRpcServerRequestHandler}: the requests and responses are encoded into binary json (bson).
 *
 * The workflows the default service implementations work on are added via the {@link JobPoolListener}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class BinaryJsonRpcServerRequestHandler extends JsonRpcServerRequestHandler {

    @Override
    protected ObjectMapper getObjectMapper() {
        return ObjectMapperUtil.getInstance().getBinaryObjectMapper();
    }

}
