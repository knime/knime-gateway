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
package com.knime.gateway.local.workflow;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.web.WebViewContent;
import org.knime.core.ui.node.workflow.InteractiveWebViewsResultUI;
import org.knime.core.util.Pair;

import com.knime.gateway.local.workflow.EntityProxySingleInteractiveWebViewResult.MyWebViewResultModel;
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NodeEnt;

/**
 * Entity-proxy class that proxies {@link NodeEnt} and implements {@link InteractiveWebViewsResultUI}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityProxyInteractiveWebViewsResult extends AbstractEntityProxy<NativeNodeEnt>
    implements InteractiveWebViewsResultUI<MyWebViewResultModel, WebViewContent, WebViewContent> {

    private final List<Pair<Integer, NativeNodeEnt>> m_indexNodePairs;

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param conn
     * @param access
     */
    EntityProxyInteractiveWebViewsResult(final NativeNodeEnt entity, final EntityProxyAccess clientProxyAccess) {
        super(entity, clientProxyAccess);
        m_indexNodePairs = new ArrayList<Pair<Integer, NativeNodeEnt>>();
        for (int i = 0; i < entity.getWebViewNames().size(); i++) {
            m_indexNodePairs.add(Pair.create(i, entity));
        }
    }

    @Override
    public int size() {
        return getEntity().getWebViewNames().size();
    }

    @Override
    public EntityProxySingleInteractiveWebViewResult get(final int index) {
        return getAccess().getSingleInteractiveWebViewResult(m_indexNodePairs.get(index));
    }
}
