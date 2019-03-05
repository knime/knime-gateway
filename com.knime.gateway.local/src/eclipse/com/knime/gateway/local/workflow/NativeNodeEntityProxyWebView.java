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

import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.js.core.JavaScriptViewCreator;

import com.knime.gateway.v0.entity.NativeNodeEnt;

/**
 * A combination of a {@link ViewableModel} and {@link WizardNode} that is backed by a {@link NativeNodeEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class NativeNodeEntityProxyWebView extends AbstractEntityProxyWebView<NativeNodeEnt> {

    private JavaScriptViewCreator<WebViewContent, WebViewContent> m_viewCreator;

    /**
     * see
     * {@link AbstractEntityProxyWebView#AbstractEntityProxyWebViewModel(com.knime.gateway.v0.entity.NodeEnt, String, EntityProxyAccess)}
     */
    NativeNodeEntityProxyWebView(final NativeNodeEnt node, final String viewName, final EntityProxyAccess access) {
        super(node, viewName, access);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WizardViewCreator<WebViewContent, WebViewContent> getViewCreator() {
        if (m_viewCreator == null) {
            m_viewCreator = new JavaScriptViewCreator<WebViewContent, WebViewContent>(getJavascriptObjectID());
        }
        return m_viewCreator;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    protected WizardNode<WebViewContent, WebViewContent> createWizardNode() {
        return (WizardNode<WebViewContent, WebViewContent>)EntityProxyNativeNodeContainer
            .createNodeFactoryInstance(getEntity(), null).createNodeModel();
    }

}
