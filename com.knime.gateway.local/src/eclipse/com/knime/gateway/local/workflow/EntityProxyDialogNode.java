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
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettings;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.config.base.JSONConfig;
import org.knime.core.node.dialog.DialogNode;
import org.knime.core.node.dialog.DialogNodeRepresentation;
import org.knime.core.node.dialog.DialogNodeValue;
import org.knime.js.base.node.quickform.QuickFormConfig;
import org.knime.js.base.node.quickform.QuickFormRepresentationImpl;

import com.knime.gateway.v0.entity.MetaNodeDialogCompEnt;

/**
 * Entity-proxy class that proxies {@link MetaNodeDialogCompEnt} and implements {@link DialogNode}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class EntityProxyDialogNode extends AbstractEntityProxy<MetaNodeDialogCompEnt>
    implements DialogNode<DialogNodeRepresentation<DialogNodeValue>, DialogNodeValue> {

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
    @SuppressWarnings("unchecked")
    @Override
    public DialogNodeRepresentation<DialogNodeValue> getDialogRepresentation() {
        DialogNodeValue value = getDialogValue();
        if (value == null) {
            value = getDefaultValue();
        }
        QuickFormConfig<DialogNodeValue> config = getConfig();

        //assumption here for every implementation of QuickFormRepresentation:
        //- it has exactly one constructor
        //- the constructor takes the respective QuickFormValue as first, and QuickFormConfig as second argument
        @SuppressWarnings("rawtypes")
        QuickFormRepresentationImpl rep = instanceForName(getEntity().getRepresentation().getClassname(),
            QuickFormRepresentationImpl.class, value, config);
        rep = fromJsonString(getEntity().getRepresentation().getContent(), rep);
        return rep;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodeValue createEmptyDialogValue() {
        try {
            return getConfig().getDefaultValue().getClass().newInstance();
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
        return getConfig().getDefaultValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DialogNodeValue getDialogValue() {
        if (getEntity().getValue() != null) {
            DialogNodeValue value = createEmptyDialogValue();
            try {
                value.loadFromNodeSettings(JSONConfig.readJSON(new NodeSettings("settings"),
                    new StringReader(getEntity().getValue().getContent())));
            } catch (InvalidSettingsException | IOException ex) {
                //should not happen
                throw new IllegalStateException("Problem reading dialog value.");
            }
            return value;
        } else {
            return null;
        }
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
        return getConfig().getParameterName();
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

    private QuickFormConfig<DialogNodeValue> getConfig() {
        QuickFormConfig<DialogNodeValue> config =
            instanceForName(getEntity().getConfig().getClassname(), QuickFormConfig.class);
        try {
            config.loadSettings(
                JSONConfig
                    .readJSON(new NodeSettings("settings"), new StringReader(getEntity().getConfig().getContent()))
                    .getNodeSettings("model"));
        } catch (InvalidSettingsException | IOException ex) {
            //should never happen
            throw new IllegalStateException(ex);
        }
        return config;
    }

    private static <T> T instanceForName(final String className, final Class<T> superClass, final Object... params) {
        try {
            if (params.length == 0) {
                return (T)Class.forName(className).newInstance();
            } else {
                return (T)Class.forName(className).getConstructors()[0].newInstance(params);
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException
                | InvocationTargetException | SecurityException ex) {
            //should never happen
            throw new IllegalStateException(
                "Can't create instance for class " + className + ". Most likely an implementation error.", ex);
        }
    }

    private static final QuickFormRepresentationImpl fromJsonString(final String s,
        final QuickFormRepresentationImpl rep) {
        try {
            rep.loadFromStream(IOUtils.toInputStream(s, Charset.forName("UTF-8")));
        } catch (IOException ex) {
            throw new IllegalStateException("Problem serializing dialog representation.", ex);
        }
        return rep;
    }

}
