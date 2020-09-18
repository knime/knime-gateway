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
 * History
 *   Created on Sep 9, 2020 by moritz
 */
package org.knime.gateway.impl.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

/**
 * This class loader makes sure that Mockito is correctly configured (by providing this file
 * mockito-extensions/org.mockito.plugins.MockMaker) to support mocking of final classes (e.g. the WorkflowManager).
 *
 * @author Moritz Heine, KNIME GmbH, Konstanz, Germany
 */
class ClassLoaderFinalClassMock extends ClassLoader {

    private static final String MOCK_MAKER_RESOURCE = "mockito-extensions/org.mockito.plugins.MockMaker";

    private final ClassLoader m_mockitoClassLoader;

    private final ClassLoader m_knimeClassLoader;

    /**
     *
     */
    ClassLoaderFinalClassMock(final ClassLoader mockitoClassLoader, final ClassLoader knimeClassLoader) {
        m_mockitoClassLoader = mockitoClassLoader;
        m_knimeClassLoader = knimeClassLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearAssertionStatus() {
        m_mockitoClassLoader.clearAssertionStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResource(final String name) {
        if (MOCK_MAKER_RESOURCE.equals(name)) {
            return m_knimeClassLoader.getResource(name);
        }

        return m_mockitoClassLoader.getResource(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResourceAsStream(final String name) {
        if (MOCK_MAKER_RESOURCE.equals(name)) {
            return m_knimeClassLoader.getResourceAsStream(name);
        }

        return m_mockitoClassLoader.getResourceAsStream(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<URL> getResources(final String name) throws IOException {
        if (MOCK_MAKER_RESOURCE.equals(name)) {
            return m_knimeClassLoader.getResources(name);
        }

        return m_mockitoClassLoader.getResources(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> loadClass(final String name) throws ClassNotFoundException {
        return org.mockito.Mockito.class.getClassLoader().loadClass(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClassAssertionStatus(final String className, final boolean enabled) {
        m_mockitoClassLoader.setClassAssertionStatus(className, enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaultAssertionStatus(final boolean enabled) {
        m_mockitoClassLoader.setDefaultAssertionStatus(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPackageAssertionStatus(final String packageName, final boolean enabled) {
        m_mockitoClassLoader.setPackageAssertionStatus(packageName, enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return m_mockitoClassLoader.toString();
    }
}