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

import org.knime.core.node.web.WebViewContent;
import org.knime.core.ui.node.workflow.InteractiveWebViewsResultUI;

import com.knime.gateway.v0.entity.NodeEnt;

/**
 * Entity-proxy class that proxies {@link NodeEnt} and implements {@link InteractiveWebViewsResultUI}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 * @param <E> the node entity type
 */
public class EntityProxyInteractiveWebViewsResult<E extends NodeEnt> extends AbstractEntityProxy<E>
    implements InteractiveWebViewsResultUI<AbstractEntityProxyWebView<E>, WebViewContent, WebViewContent> {

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param conn
     * @param access
     */
    EntityProxyInteractiveWebViewsResult(final E entity, final EntityProxyAccess clientProxyAccess) {
        super(entity, clientProxyAccess);
    }

    @Override
    public int size() {
        return getEntity().getWebViewNames().size();
    }

    @Override
    public SingleInteractiveWebViewResultUI<AbstractEntityProxyWebView<E>, WebViewContent, WebViewContent>
        get(final int index) {
        return new SingleInteractiveWebViewResultUI<AbstractEntityProxyWebView<E>, WebViewContent, WebViewContent>() {

            @Override
            public AbstractEntityProxyWebView<E> getModel() {
                return getAccess().getEntityProxyWebView(getEntity(), getEntity().getWebViewNames().get(index));
            }

            @Override
            public String getViewName() {
                return getEntity().getWebViewNames().get(index);
            }
        };
    }
}
