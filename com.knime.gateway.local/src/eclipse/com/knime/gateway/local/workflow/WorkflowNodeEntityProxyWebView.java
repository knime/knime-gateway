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

import java.util.HashMap;
import java.util.Map;

import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.core.wizard.SubnodeViewValue;
import org.knime.core.wizard.SubnodeViewableModel.SubnodeWizardViewCreator;
import org.knime.js.core.JSONWebNode;
import org.knime.js.core.JSONWebNodePage;
import org.knime.js.core.JSONWebNodePageConfiguration;
import org.knime.js.core.JavaScriptViewCreator;

import com.knime.gateway.v0.entity.WrappedWorkflowNodeEnt;

/**
 * A combination of a {@link ViewableModel} and {@link WizardNode} that is backed by a {@link WrappedWorkflowNodeEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class WorkflowNodeEntityProxyWebView extends AbstractEntityProxyWebView<WrappedWorkflowNodeEnt> {

    /**
     * see
     * {@link AbstractEntityProxyWebView#AbstractEntityProxyWebViewModel(com.knime.gateway.v0.entity.NodeEnt, String, EntityProxyAccess)}
     */
    WorkflowNodeEntityProxyWebView(final WrappedWorkflowNodeEnt node, final String viewName,
        final EntityProxyAccess access) {
        super(node, viewName, access);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected WizardNode<WebViewContent, WebViewContent> createWizardNode() {
        return new WizardNode<WebViewContent, WebViewContent>() {

            @Override
            public ValidationError validateViewValue(final WebViewContent viewContent) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void loadViewValue(final WebViewContent viewContent, final boolean useAsDefault) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void saveCurrentValue(final NodeSettingsWO content) {
                throw new UnsupportedOperationException();
            }

            @Override
            public WebViewContent getViewRepresentation() {
                throw new UnsupportedOperationException();
            }

            @Override
            public WebViewContent getViewValue() {
                throw new UnsupportedOperationException();
            }

            @Override
            public WebViewContent createEmptyViewRepresentation() {
                Map<String, JSONWebNode> emptyNode = new HashMap<String, JSONWebNode>();
                return new JSONWebNodePage(new JSONWebNodePageConfiguration(), emptyNode);
            }

            @Override
            public WebViewContent createEmptyViewValue() {
                return new SubnodeViewValue();
            }

            @Override
            public String getJavascriptObjectID() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getViewHTMLPath() {
                throw new UnsupportedOperationException();
            }

            @Override
            public WizardViewCreator<WebViewContent, WebViewContent> getViewCreator() {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isHideInWizard() {
                return false;
            }

            @Override
            public void setHideInWizard(final boolean hide) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WizardViewCreator<WebViewContent, WebViewContent> getViewCreator() {
        return new JavaScriptViewCreator<WebViewContent, WebViewContent>() {

            {
                setWebTemplate(SubnodeWizardViewCreator.createSubnodeWebTemplate());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String getViewValueJSONString(final WebViewContent val) {
                assert val instanceof StringWebViewContent;
                return ((StringWebViewContent)val).getContent();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public String getViewRepresentationJSONString(final WebViewContent rep) {
                assert rep instanceof StringWebViewContent;
                return ((StringWebViewContent)rep).getContent();
            }

        };
    }

}
