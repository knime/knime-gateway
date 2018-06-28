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

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.dialog.DialogNodeRepresentation;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.js.base.node.quickform.QuickFormConfig;
import org.knime.js.base.node.quickform.QuickFormRepresentationImpl;
import org.knime.js.core.JSONViewContent;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.knime.gateway.v0.entity.MetaNodeDialogCompEnt;

/**
 * Entity-proxy class that proxies {@link MetaNodeDialogCompEnt} and implements {@link DialogNode}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityProxyDialogNode extends AbstractEntityProxy<MetaNodeDialogCompEnt>
    implements DialogNode<DialogNodeRepresentation<DialogNodeValue>, DialogNodeValue> {

    private QuickFormRepresentationImpl<DialogNodeValue, QuickFormConfig<DialogNodeValue>> m_qfRepresentation = null;

    /**
     * @param entity
     * @param clientProxyAccess
     */
    EntityProxyDialogNode(final MetaNodeDialogCompEnt entity, final EntityProxyAccess clientProxyAccess) {
        super(entity, clientProxyAccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveCurrentValue(final NodeSettingsWO content) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodeRepresentation<DialogNodeValue> getDialogRepresentation() {
        return getQFRepresentation();
    }

    private QuickFormRepresentationImpl<DialogNodeValue, QuickFormConfig<DialogNodeValue>> getQFRepresentation() {
        if (m_qfRepresentation == null) {
            try {
                m_qfRepresentation = fromJsonString(getEntity().getRepresentation().getClassname(),
                    getEntity().getRepresentation().getContent());
            } catch (IOException | ClassNotFoundException ex) {
                //should not happen
                throw new IllegalStateException("Problem deserializing quickform representation.", ex);
            }
        }
        return m_qfRepresentation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodeValue createEmptyDialogValue() {
        try {
            return getDefaultValue().getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            //should never happen otherwise it's an implementation error
            throw new IllegalStateException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDialogValue(final DialogNodeValue value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodeValue getDefaultValue() {
        return getQFRepresentation().getDefaultValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodeValue getDialogValue() {
        return getQFRepresentation().getCurrentValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validateDialogValue(final DialogNodeValue value) throws InvalidSettingsException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getParameterName() {
        return getEntity().getParamName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHideInDialog() {
        return getEntity().isIsHideInDialog();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHideInDialog(final boolean hide) {
        // TODO
    }

    @SuppressWarnings("unchecked")
    private static final QuickFormRepresentationImpl<DialogNodeValue, QuickFormConfig<DialogNodeValue>>
        fromJsonString(final String className, final String content)
            throws JsonParseException, JsonMappingException, IOException, ClassNotFoundException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Class<?> clazz = Class.forName(className);
        Thread.currentThread().setContextClassLoader(clazz.getClassLoader());
        try {
            return (QuickFormRepresentationImpl<DialogNodeValue, QuickFormConfig<DialogNodeValue>>)JSONViewContent
                .createObjectMapper().readValue(content, clazz);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }
}
