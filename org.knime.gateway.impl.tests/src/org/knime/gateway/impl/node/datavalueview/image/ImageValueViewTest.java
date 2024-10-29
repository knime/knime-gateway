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
 *   Oct 29, 2024 (Paul Bärnreuther): created
 */
package org.knime.gateway.impl.node.datavalueview.image;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.junit.Test;
import org.knime.base.data.xml.SvgCellFactory;
import org.knime.base.data.xml.SvgValue;
import org.knime.core.data.image.png.PNGImageContent;
import org.knime.core.data.image.png.PNGImageValue;
import org.knime.core.data.util.LockedSupplier;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

/**
 * @author Paul Bärnreuther
 */
public class ImageValueViewTest {
    /**
     * Test that the initial data of the {@link SvgValueView} is the string representation of the SVG.
     *
     * @throws IOException if the provided svg was invalid
     */
    @Test
    public void testSvgValueView() throws IOException {
        final var svgString = "<svg height=\"100\" width=\"100\" xmlns=\"http://www.w3.org/2000/svg\">"
            + "  <circle r=\"45\" cx=\"50\" cy=\"50\" fill=\"red\" />" //
            + "</svg> ";

        final var svgValue = toSvgValue(svgString);
        ImageInitialData initialData = new SvgValueView(svgValue).getInitialData();
        assertThat(initialData.mimeType(), is("image/svg+xml"));
        try (final var expectedDocumentSupplier = svgValue.getDocumentSupplier();
                final var inputDataDocumentSupplier = toDocumentSupplier(initialData)) {
            final var expectedDocument = expectedDocumentSupplier.get();
            final var inputDataDocument = inputDataDocumentSupplier.get();
            assertThat(getStringFromDocument(expectedDocument), is(getStringFromDocument(inputDataDocument)));
        }

    }

    /**
     * Get the string representation of the SVG document. We use this in order to compare the SVG documents. Using the
     * {@link SVGDocument#isEqualNode(org.w3c.dom.Node)} instead yields false, even if the documents yield the same
     * string, since a reference comparison is performed on attribute nodes.
     */
    private static String getStringFromDocument(final Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException ex) {
            throw new IllegalArgumentException("Could not serialize SVG document", ex);
        }
    }

    private static LockedSupplier<SVGDocument> toDocumentSupplier(final ImageInitialData initialData)
        throws IOException {
        final var svgString = new String(initialData.data(), StandardCharsets.UTF_8);
        final var svgValue = toSvgValue(svgString);
        return svgValue.getDocumentSupplier();
    }

    private static SvgValue toSvgValue(final String svgString) throws IOException {
        return (SvgValue)SvgCellFactory.create(svgString);
    }

    /**
     * Test that the initial data of the {@link PNGImageValueView} is the base64 encoded image.
     */
    @Test
    public void testPNGImageValueView() {
        final var pngImageValue = Mockito.mock(PNGImageValue.class);
        final var pngContent = Mockito.mock(PNGImageContent.class);
        Mockito.when(pngImageValue.getImageContent()).thenReturn(pngContent);
        final var imageBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Mockito.when(pngContent.getByteArray()).thenReturn(imageBytes);

        ImageInitialData initialData = new PNGImageValueView(pngImageValue).getInitialData();
        assertThat(initialData.data(), is(imageBytes));
        assertThat(initialData.mimeType(), is("image/png"));
    }
}
