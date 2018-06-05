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
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.WizardNode;
import org.knime.core.node.wizard.WizardViewCreator;
import org.knime.js.core.JavaScriptViewCreator;

import com.knime.gateway.v0.entity.NativeNodeEnt;
import com.knime.gateway.v0.entity.ViewDataEnt;
import com.knime.gateway.v0.service.util.ServiceExceptions.NodeNotFoundException;
import com.knime.gateway.v0.service.util.ServiceExceptions.NotSupportedException;

/**
 * A combination of a {@link ViewableModel} and {@link WizardNode} that is backed by a {@link NativeNodeEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class EntityProxyWebViewModel extends AbstractEntityProxy<NativeNodeEnt>
    implements ViewableModel, WizardNode<WebViewContent, WebViewContent> {

    private WizardNode<WebViewContent, WebViewContent> m_wizardNode = null;

    private JavaScriptViewCreator<WebViewContent, WebViewContent> m_viewCreator = null;

    private final String m_viewName;

    private final ViewDataEnt m_viewData;

    /**
     * @param node the node to load the view data from
     * @param access the entity access to get access to entites etc.
     */
    EntityProxyWebViewModel(final NativeNodeEnt node, final String viewName, final EntityProxyAccess access) {
        super(node, access);
        m_viewName = viewName;
        m_viewData = getViewData(node, access);
    }

    private static ViewDataEnt getViewData(final NativeNodeEnt node, final EntityProxyAccess access) {
        try {
            return access.nodeService().getViewData(node.getRootWorkflowID(), node.getNodeID());
        } catch (NodeNotFoundException | NotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
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
        return fromJsonString(m_viewData.getViewRepresentation().getContent(), createEmptyViewRepresentation());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebViewContent getViewValue() {
        return fromJsonString(m_viewData.getViewValue().getContent(), createEmptyViewValue());
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
        return m_viewData.getJavascriptObjectID();
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
        return m_viewData.isHideInWizard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInWizard(final boolean hide) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    private WizardNode<WebViewContent, WebViewContent> getWizardNode() {
        if (m_wizardNode == null) {
            m_wizardNode = (WizardNode<WebViewContent, WebViewContent>)EntityProxyNativeNodeContainer
                .createNodeFactoryInstance(getEntity()).createNodeModel();
        }
        return m_wizardNode;
    }

    private static final WebViewContent fromJsonString(final String s, final WebViewContent webViewContent) {
        try {
            webViewContent.loadFromStream(IOUtils.toInputStream(s, Charset.forName("UTF-8")));
        } catch (IOException ex) {
            throw new IllegalStateException("Problem serializing web view.", ex);
        }
        return webViewContent;
    }
}
