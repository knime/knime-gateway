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

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knime.enterprise.executor.JobPoolListener;
import com.knime.enterprise.executor.genericmsg.GenericMessageIDs;
import com.knime.enterprise.executor.genericmsg.GenericServerRequestHandler;
import com.knime.gateway.json.util.ObjectMapperUtil;

/**
 * Implementation of the {@link GenericServerRequestHandler} extension point that executes json-rpc 2.0 requests and
 * delegates the respective calls to the default service implementations.
 *
 * The workflows the default service implementations work on are added via the {@link JobPoolListener}.
 *
 * @author Martin Horn, University of Konstanz
 */
public class JsonRpcServerRequestHandler implements GenericServerRequestHandler {

    private final JsonRpcRequestHandler m_jsonRpcRequestHandler;

    /**
     * New server request handler.
     */
    public JsonRpcServerRequestHandler() {
        m_jsonRpcRequestHandler = new JsonRpcRequestHandler();
    }

    /**
     * @return the object mapper to use for json-rpc-request deserialization and json-rpc-response serialization
     */
    protected ObjectMapper getObjectMapper() {
        return ObjectMapperUtil.getInstance().getObjectMapper();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageId() {
        return GenericMessageIDs.JSON_RPC_2_0;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] handle(final UUID jobId, final byte[] messageBody) {
        return m_jsonRpcRequestHandler.handle(messageBody);
    }
}
