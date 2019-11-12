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
package com.knime.gateway.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.knime.core.node.NodeLogger;

/**
 * Utility methods that help to deal with extension points.
 *
 * @author Martin Horn, University of Konstanz
 */
public class ExtPointUtil {

    private static final NodeLogger LOGGER = NodeLogger.getLogger(ExtPointUtil.class);

    private ExtPointUtil() {
        // utility class
    }

    /**
     * Methods the collects and instantiates objects of a 'java'-type attribute (i.e. a abstract class or interface) of
     * an extension point.
     *
     * @param extPointID the extension point id
     * @param extPointAttr the extension point attributes of the class to get the instances for
     * @return the list of all instances for the class-extension point attribute
     */
    public static <C> List<C> collectExecutableExtensions(final String extPointID, final String extPointAttr) {

        List<C> instances = new ArrayList<C>();

        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(extPointID);
        if (point == null) {
            LOGGER.error("Invalid extension point: " + extPointID);
            throw new IllegalStateException("ACTIVATION ERROR: " + " --> Invalid extension point: " + extPointID);
        }

        for (IConfigurationElement elem : point.getConfigurationElements()) {
            String attr = elem.getAttribute(extPointAttr);
            String decl = elem.getDeclaringExtension().getUniqueIdentifier();
            LOGGER.debug("Found configuration element for " + extPointID + " from " + elem.getContributor().getName());

            if (attr == null || attr.isEmpty()) {
                LOGGER.error(
                    "The extension '" + decl + "' doesn't provide the required attribute '" + extPointAttr + "'");
                LOGGER.error("Extension " + decl + " ignored.");
                continue;
            }

            // try instantiating.
            C instance = null;
            try {
                instance = (C)elem.createExecutableExtension(extPointAttr);
            } catch (UnsatisfiedLinkError ule) {
                // in case an implementation tries to load an external lib
                // when the factory class gets loaded
                LOGGER.error("Unable to load a library required for '" + attr + "'");
                LOGGER.error(
                    "Either specify it in the -Djava.library.path " + "option at the program's command line, or");
                LOGGER.error("include it in the LD_LIBRARY_PATH variable.");
                LOGGER.error("Extension " + attr + " ('" + decl + "') ignored.", ule);
            } catch (CoreException ex) {
                Throwable cause = ex.getStatus().getException();
                if (cause != null) {
                    LOGGER.error("Problems during initialization of executable extension with attribute id '" + attr + "': " + cause.getMessage(), ex);
                    if (decl != null) {
                        LOGGER.error("Extension " + decl + " ignored.");
                    }
                } else {
                    LOGGER.error("Problems during initialization of executable extension with attribute id '" + attr + "'", ex);
                    if (decl != null) {
                        LOGGER.error("Extension " + decl + " ignored.");
                    }
                }
            } catch (Throwable t) {
                LOGGER.error("Problems during initialization of executable extension with attribute id '"
                    + attr + "'", t);
                if (decl != null) {
                    LOGGER.error("Extension " + decl + " ignored.");
                }
            }

            if (instance != null) {
                instances.add(instance);
            }
        }

        return instances;

    }

}
