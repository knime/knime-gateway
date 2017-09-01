/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * History
 *   Nov 14, 2016 (hornm): created
 */
package org.knime.gateway.util;

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
