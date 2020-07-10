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

import static com.knime.gateway.entity.EntityBuilderManager.builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.knime.core.node.AbstractNodeView.ViewableModel;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.web.ValidationError;
import org.knime.core.node.web.WebViewContent;
import org.knime.core.node.wizard.CSSModifiable;
import org.knime.core.node.wizard.WizardNode;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.knime.gateway.entity.JavaObjectEnt;
import com.knime.gateway.entity.JavaObjectEnt.JavaObjectEntBuilder;
import com.knime.gateway.entity.NodeEnt;
import com.knime.gateway.entity.ViewDataEnt;
import com.knime.gateway.service.util.ServiceExceptions.InvalidRequestException;
import com.knime.gateway.service.util.ServiceExceptions.NodeNotFoundException;

/**
 * A combination of a {@link ViewableModel} and {@link WizardNode} that is backed by a {@link NodeEnt}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public abstract class AbstractEntityProxyWebView<E extends NodeEnt> extends AbstractEntityProxy<E>
    implements ViewableModel, WizardNode<WebViewContent, WebViewContent> {

    private WizardNode<WebViewContent, WebViewContent> m_wizardNode;

    private final String m_viewName;

    private ViewDataEnt m_viewData;

    /**
     * @param node the node to load the view data from
     * @param access the entity access to get access to entites etc.
     */
    AbstractEntityProxyWebView(final E node, final String viewName, final EntityProxyAccess access) {
        super(node, access);
        m_viewName = viewName;
        //try getting the view data
        getViewData();
    }

    private ViewDataEnt getViewData() {
        if (m_viewData == null) {
            try {
                m_viewData =
                    getAccess().nodeService().getViewData(getEntity().getRootWorkflowID(), getEntity().getNodeID());
            } catch (NodeNotFoundException | InvalidRequestException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return m_viewData;
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
        //push view content back to server
        try {
            JavaObjectEnt viewContentEnt =
                builder(JavaObjectEntBuilder.class).setClassname(viewContent.getClass().getCanonicalName())
                    .setJsonContent(((ByteArrayOutputStream)viewContent.saveToStream()).toString("UTF-8")).build();
            getAccess().nodeService().setViewValue(getEntity().getRootWorkflowID(), getEntity().getNodeID(),
                useAsDefault, viewContentEnt);
            //since the view value has been changed, delete the cached view data
            m_viewData = null;
        } catch (IOException | NodeNotFoundException | InvalidRequestException ex) {
            throw new IllegalStateException("Problem saving view value to server. ", ex);
        }
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
        return fromJsonString(getViewData().getViewRepresentation().getJsonContent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebViewContent getViewValue() {
        return fromJsonString(m_viewData.getViewValue().getJsonContent());
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
            String customCSS = null;
            WizardNode<WebViewContent,WebViewContent> node = getWizardNode();
            if (node instanceof CSSModifiable) {
                customCSS = ((CSSModifiable)node).getCssStyles();
            }
            return getViewCreator().createWebResources(m_viewName, getViewRepresentation(), getViewValue(),
                customCSS);
        } catch (IOException ex) {
            throw new IllegalStateException("Problem creating the view html path.", ex);
        }
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

    private WizardNode<WebViewContent, WebViewContent> getWizardNode() {
        if (m_wizardNode == null) {
            m_wizardNode = createWizardNode();
        }
        return m_wizardNode;
    }

    protected abstract WizardNode<WebViewContent, WebViewContent> createWizardNode();

    private static final WebViewContent fromJsonString(final String s) {
        return new StringWebViewContent(s);
    }

    /**
     * WebViewContent that just wraps the content as a string.
     */
    @JsonSerialize(using = StringWebViewContentSerializer.class)
    private static class StringWebViewContent extends JSONViewContent {

        private final String m_content;

        StringWebViewContent(final String content) {
            m_content = content;
        }

        @Override
        public void saveToNodeSettings(final NodeSettingsWO settings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void loadFromNodeSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
            throw new UnsupportedOperationException();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if(obj instanceof StringWebViewContent) {
                return m_content.equals(((StringWebViewContent)obj).m_content);
            }
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return m_content.hashCode();
        }

    }

    private static class StringWebViewContentSerializer extends StdSerializer<StringWebViewContent> {
        private static final long serialVersionUID = 6449229532861985350L;

        public StringWebViewContentSerializer() {
            super(StringWebViewContent.class);
        }

        @Override
        public void serializeWithType(final StringWebViewContent value, final JsonGenerator gen, final SerializerProvider serializers,
            final TypeSerializer typeSer) throws IOException {
            serialize(value, gen, serializers);
        }

        @Override
        public void serialize(final StringWebViewContent value, final JsonGenerator gen,
            final SerializerProvider serializers) throws IOException {
            gen.writeRawValue(value.m_content);
        }
    }
}
