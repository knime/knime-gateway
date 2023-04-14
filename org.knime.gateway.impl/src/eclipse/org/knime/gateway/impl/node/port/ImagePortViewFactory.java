/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
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
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
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
 *   Jul 19, 2022 (hornm): created
 */
package org.knime.gateway.impl.node.port;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.knime.core.data.image.ImageValue;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.workflow.NodeOutPort;
import org.knime.core.webui.data.InitialDataService;
import org.knime.core.webui.data.RpcDataService;
import org.knime.core.webui.node.port.PortContext;
import org.knime.core.webui.node.port.PortView;
import org.knime.core.webui.node.port.PortViewFactory;
import org.knime.core.webui.page.Page;

/**
 * Factory for a port view of a {@link ImagePortObject}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public final class ImagePortViewFactory implements PortViewFactory<ImagePortObject> {

    /*
     * This map is sort of a 'very short term cache'. It keeps image data for a certain image id. The image-id is returned
     * via the initial data service (as part of a (relative) url). And as soon as the provided URL is 'used' in the FE
     * (e.g. in an img-src attribute or via another fetch), the actual image data is fetched through the CEF's
     * middleware service and immediately removed from this map, once it has been fetch.
     */
    static final Map<String, byte[]> IMAGE_DATA_MAP = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public PortView createPortView(final ImagePortObject portObject) {
        var imgValue = (ImageValue) portObject.toDataCell();
        // we append the object-hash to the imageId to make sure the FE re-renderes
        // the image whenever it changes (and not take it from the browser cache)
        var nc = ((NodeOutPort)PortContext.getContext().getNodePort()).getConnectedNodeContainer();
        var imageId =
            nc.getID().toString() + ":" + System.identityHashCode(portObject) + "." + imgValue.getImageExtension();
        return new PortView() {

            @Override
            public Optional<InitialDataService> createInitialDataService() {
                return Optional.of(InitialDataService.builder(() -> {
                    IMAGE_DATA_MAP.put(imageId, getImageData(imgValue));
                    return "ImagePortView/img/" + imageId;
                }).build());
            }

            @Override
            public Optional<RpcDataService> createRpcDataService() {
                return Optional.empty();
            }

            @Override
            public Page getPage() {
                return Page.builder(ImagePortViewFactory.class, "not-used", "vue_component_reference") //
                    // this is the name of the component used and already present in the frontend
                    .markAsReusable("ImagePortView")//
                    .addResources(imageId -> new ByteArrayInputStream(IMAGE_DATA_MAP.remove(imageId)), "img", true)
                    .build();
            }

        };
    }

    private static byte[] getImageData(final ImageValue imgValue) {
        var imageContent = imgValue.getImageContent();
        try (var baos = new ByteArrayOutputStream()) {
            imageContent.save(baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            NodeLogger.getLogger(ImagePortViewFactory.class)
                .error("Image content couldn't be extracted from image port", ex);
            return new byte[0];
        }
    }

}
