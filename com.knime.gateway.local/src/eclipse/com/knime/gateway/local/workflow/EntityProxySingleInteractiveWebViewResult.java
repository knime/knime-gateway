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

import java.io.IOException;
import java.io.StringReader;

import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.core.ui.node.workflow.InteractiveWebViewsResultUI.SingleInteractiveWebViewResultUI;
import org.knime.core.util.Pair;
import org.knime.js.core.JavaScriptViewCreator;

import com.knime.gateway.local.workflow.EntityProxySingleInteractiveWebViewResult.MyWebViewResultModel;
import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.NodeEnt;
import com.knime.gateway.v0.entity.WebViewEnt;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;

/**
 * Entity-proxy class that proxies a pair of a view index and a {@link NodeEnt} and implements
 * {@link SingleInteractiveWebViewResultUI}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityProxySingleInteractiveWebViewResult extends AbstractEntityProxy<Pair<Integer, NativeNodeEnt>>
    implements SingleInteractiveWebViewResultUI<MyWebViewResultModel, WebViewContent, WebViewContent> {

    /**
     * See {@link AbstractEntityProxy#AbstractEntityProxy(com.knime.gateway.entity.GatewayEntity, EntityProxyAccess)}.
     *
     * @param index the index of the web view
     * @param conn
     * @param access
     */
    EntityProxySingleInteractiveWebViewResult(final Pair<Integer, NativeNodeEnt> indexNodePair,
        final EntityProxyAccess clientProxyAccess) {
        super(indexNodePair, clientProxyAccess);
    }

    @Override
    public MyWebViewResultModel getModel() {
        try {
            WebViewEnt ent = getAccess().nodeService().getWebView(getEntity().getSecond().getRootWorkflowID(),
                getEntity().getSecond().getNodeID(), getEntity().getFirst());
            return new MyWebViewResultModel(ent, getViewName());
        } catch (NodeNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String getViewName() {
        return getEntity().getSecond().getWebViewNames().get(getEntity().getFirst());
    }

    /**
     * A model that is backed by a {@link WebViewEnt}.
     */
    public final class MyWebViewResultModel implements ViewableModel, WizardNode<WebViewContent, WebViewContent> {

        private final WebViewEnt m_webViewEnt;

        private WizardNode m_wizardNode = null;

        private JavaScriptViewCreator m_viewCreator = null;

        private final String m_viewName;

        public MyWebViewResultModel(final WebViewEnt webViewEnt, final String viewName) {
            m_webViewEnt = webViewEnt;
            m_viewName = viewName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ValidationError validateViewValue(final WebViewContent viewContent) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void loadViewValue(final WebViewContent viewContent, final boolean useAsDefault) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void saveCurrentValue(final NodeSettingsWO content) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WebViewContent getViewRepresentation() {
            return fromJsonString(m_webViewEnt.getViewRepresentation().getContent(), createEmptyViewRepresentation());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WebViewContent getViewValue() {
            return fromJsonString(m_webViewEnt.getViewValue().getContent(), createEmptyViewValue());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WebViewContent createEmptyViewRepresentation() {
            return getWizardNode().createEmptyViewRepresentation();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public WebViewContent createEmptyViewValue() {
            return getWizardNode().createEmptyViewValue();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getJavascriptObjectID() {
            return m_webViewEnt.getJavascriptObjectID();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getViewHTMLPath() {
            try {
                return getViewCreator().createWebResources(m_viewName, getViewRepresentation(), getViewValue());
            } catch (IOException ex) {
                //TODO
                throw new RuntimeException(ex);
            }
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
        @Override
        public boolean isHideInWizard() {
            return m_webViewEnt.isHideInWizard();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setHideInWizard(final boolean hide) {
            throw new UnsupportedOperationException();
        }

        private final WebViewContent fromJsonString(final String s, final WebViewContent webViewContent) {
            try {
                NodeSettings settings = new NodeSettings("settings");
                JSONConfig.readJSON(settings, new StringReader(s));
                webViewContent.loadFromNodeSettings(settings);
            } catch (InvalidSettingsException | IOException ex) {
                //TODO
                throw new RuntimeException(ex);
            }
            return webViewContent;
        }

        private WizardNode getWizardNode() {
            if (m_wizardNode == null) {
                m_wizardNode =
                    (WizardNode)EntityProxyNativeNodeContainer.createNodeFactoryInstance(getEntity().getSecond())
                        .createNodeModel();
            }
            return m_wizardNode;
        }
    }

    private static final WebViewContent fromJsonString(final String s, final WebViewContent webViewContent) {
        try {
            NodeSettings settings = new NodeSettings("settings");
            JSONConfig.readJSON(settings, new StringReader(s));
            webViewContent.loadFromNodeSettings(settings);
        } catch (InvalidSettingsException | IOException ex) {
            //TODO
            throw new RuntimeException(ex);
        }
        return webViewContent;
    }

}
