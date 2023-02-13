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
 *   Feb 10, 2023 (hornm): created
 */
package org.knime.gateway.impl.node.port;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.knime.core.data.image.png.PNGImageContent;
import org.knime.core.node.port.image.ImagePortObject;
import org.knime.core.node.port.image.ImagePortObjectSpec;
import org.knime.core.node.workflow.NodeContext;
import org.knime.core.node.workflow.SingleNodeContainer;
import org.knime.core.webui.data.json.JsonInitialDataService;
import org.knime.core.webui.node.port.PortView;
import org.knime.shared.workflow.storage.clipboard.InvalidDefClipboardContentVersionException;
import org.knime.shared.workflow.storage.clipboard.SystemClipboardFormat;
import org.knime.shared.workflow.storage.clipboard.SystemClipboardFormat.ObfuscatorException;
import org.knime.testing.util.WorkflowManagerUtil;

/**
 * Tests {@link ImagePortViewFactory}.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ImagePortViewFactoryTest {

    /**
     * Tests the page and initial-data-service of the {@link PortView} returned by the {@link ImagePortViewFactory}.
     *
     * @throws InvalidDefClipboardContentVersionException
     * @throws IOException
     * @throws ObfuscatorException
     * @throws IllegalArgumentException
     */
    @Test
    public void testImagePortViewPageAndInitialDataService()
        throws InvalidDefClipboardContentVersionException, IOException, IllegalArgumentException, ObfuscatorException {
        var wfm = WorkflowManagerUtil.createEmptyWorkflow();
        var content = SystemClipboardFormat.deserialize(WORKFLOW_SNIPPET);
        wfm.paste(content);
        try {
            // NodeContext.pushContext(null);
            var snc = (SingleNodeContainer)wfm.getNodeContainer(wfm.getID().createChild(5));
            NodeContext.pushContext(snc);
            PortView portView;
            byte[] pngImageData = createPngImageData();
            var portObject = createImagePortObject(pngImageData);
            try {
                portView = new ImagePortViewFactory().createPortView(portObject);
            } finally {
                NodeContext.removeLastContext();
            }
            var page = portView.getPage();
            assertThat(page.getContentType().toString(), is("VUE_COMPONENT_REFERENCE"));
            var pageId = page.getPageIdForReusablePage().orElse(null);
            assertThat(pageId, is("ImagePortView"));

            var initialData = ((JsonInitialDataService)portView.createInitialDataService().get()).getInitialData();
            var imageId = snc.getID().toString() + ":" + System.identityHashCode(portObject);
            assertThat(initialData, is("{\"result\":\"ImagePortView/img/" + imageId + "\"}"));
            assertThat(ImagePortViewFactory.IMAGE_DATA_MAP.size(), is(1));
            assertThat(ImagePortViewFactory.IMAGE_DATA_MAP.get(imageId), is(pngImageData));

            var imgResource = page.getResource("img/" + imageId).get();
            assertThat(IOUtils.toByteArray(imgResource.getInputStream()), is(pngImageData));
            assertThat(ImagePortViewFactory.IMAGE_DATA_MAP.isEmpty(), is(true));
        } finally {
            WorkflowManagerUtil.disposeWorkflow(wfm);
        }
    }

    private static ImagePortObject createImagePortObject(final byte[] pngImageData) {
        var imageContent = new PNGImageContent(pngImageData);
        return new ImagePortObject(imageContent, new ImagePortObjectSpec(PNGImageContent.TYPE));
    }

    private static byte[] createPngImageData() {
        var img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        var rand = new Random(58342);
        for (var x = 0; x < img.getHeight(); x++) {
            for (var y = 0; y < img.getWidth(); y++) {
                var val = rand.nextInt(256);
                int p = (0 << 24) | (val << 16) | (val << 8) | val;
                img.setRGB(x, y, p);
            }
        }

        var out = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e); // NOSONAR
        }
    }

    private static final String WORKFLOW_SNIPPET =
        "010280kPm7oOttKUyPd7M1tpCYihArjsfwul4Y0OgEHySUrSzo-611Bk8V1Ninf64jRv-mo5nm4USmFmqgfG8RipHP45e4dJAKZEfbS0S7K5GWD5-c57cBcxn11FUu-09e2jae57-cexeMN0hrycPej2Y6qb9nCHl2e2DLVFaIsxPtfssKrZ86pdDSxhkAR5dgAz75yg6HD2-Tp5GBkxF8hz8Y_UHJsVopJoZK9xaN-P_Us2YjbJEVcGwl5WtH7uQf2urDVJekOXFjIOLDX0IPUzLOiKkM0hbhubUQJxt6XtYbXx-Lw6_xNhGSRvFfGgC3JwEDZC3n2p31F9oJfLGEH3NICsKTlKCHXgtiT84uNS_irlWcuQP7zLrAZm4EeQZscBlaE1Xq8-N0M3Ceql6uyAST2cZR2V9CCmVFTlY88PgZxDKXGxPLufsfOYafaazJPqM1vWeDSWq7q2OpGOZeXkda8aTzp95D8Kjvr0iPEZMD3OpKVkr5a_gtsHY72YUAszFkruYxJNzG8vXOoipXcz8EcIVGXgo7jN4Bn1umUApRqtP3OmxpB8Y8aePwYzz_KlqN2LH6UgADE_2t2uW1SLhOSfzRPLpgJW2M3rVTF4kqurpUR8q0PlBCAy1TWvxcrEuBK59Xw8l3GAlVa_VmbVFT0gfIUu0jF_JLl-nxtVxw1RSOHOOaI8Ftb3pBOHY1h8fRpwIp3IxCFqftqanf0srgzr9-urbur6BfNOjm0EMBmHKKZs3tEra3W0T_wD6Yr8LOhEOJZHHrot74lYjIPvVxF8v30FZW9wcahf8PfOy2grCBF92nhDBJpmr3tEDX5pqLbOuEHFKPjz2JiJBQxg5wSGbGueuptQR6hDmAXEaXpQJDGRzeWnRPQx494PnSYaUT1IB9U1h9VaVaiLtiLZgs9HEYoen9sBLi2Cfah4iClLtbzq7Z2LZQO4If96fI6vufD6oow4a6FGFQbEgSW5Qz5uIOP-AAgdc5cpDnjIu_2jfNxDmgiGOmt1IpL0ioQDS-FMZM6N_SKzUtFSHrZj1crrRRe6riK47z9Mz-wJ45fGh4RMBplm8vUQhFf4jhn0OilsiZgEwdbQgpLwLgpnkVuqNwXyaDgg8jGbTeyjZBD73VI5LaXuvug3D3DPeaH97K85C8Q4E13ueZqJPsSAWCj_Yy7myCatqhAFVCx-i4RpYWJYDykCJ9tUHYBhscOmSNEvRCr_PqcK7B5cdrebOYaMsMLXWa7XChcgvec0f1WhO-Yb709Yl1tU6vz_5n3OYYjoB9UfEzjIQrbNSPDmIeUSJIvdZd6lVbH-aUH-bGHfHLu_GfApcu9S3yhRsQY0Oc-0sWA64pI7ajhZ2yUv4YGnTT-3swrSkfjUqVCkWJMI3miD3WxG2gMNaNJyrb9lh5B2gr3JEAv7wpTMeKRJYELatNG-9QQhgKSEIYSQGfHIlkr3RDKnmV65ZQ6UeW7TdOKSO7-IqltU-ZlnnL-ngi8ldhIQvNk-AfYE8V-fzc5yGH2mdlFjAK8jqgBnKkKXQxvFkSxPoy6osd_WPdBW2spIIJWw8xPz9_vDfSo9X7KGkgb4gu6nm2VCoqwptALvX-snsEnTzKlYj9iv4qk6IGvmlFHoy7vVYU6FAG1FvBrVZpeNNLgS7uXE3-V5s2BpDWb40cf9BQp19stZJWVsliaeGH6yCN5u-Xt0-4CcTsir78cWhfyzaeVklcjmUdBq2TURZjT6kEhetO8GqOk4Xl8hwRrvW9-L-2YNogTZny33AQ_DX9AXfwFEI1DHSm8jSUNRXW_TCEk83RQ1JozIMjqz01lPOVJ-6Ar5dpWML4dKzgZL6tRhHY9vqSEf4K1W4baayffl97BjjUi6sa5Fnhr50DC7l6yXB0QUl4iyERHvybulQ6IDlhJb5AkB49nl5vu7BK5kfALH2UXOLfg96c8kycWjpgFfNJ_el4BjF1_LRRFjSL2ViSjKdgD6CiSnWxOBIqa8CG8DF4VInbpSt9COSjH6cRcB7CDSiozV023sAxWM1YOfphg0uviMeL_0cAQuYELrssejaBdfL9LeyPac26BbOq-VYrOpEPpESDvVcXQxFI7D1biC-ZA7z7oFDTPrSEpWunc_HzoN1gh4dQzJmTLIAp7KTy1HQEfQzhhD-UKcPXHzwVjaaRm9tPkY026JdoNR9DFMvuLfUEAcbHp2460--zWhhE5DzcRJpQ5GvO3TwRSNgBuT9z6hjx5Ad4-UA2j-m4mCi_5Z99Oe3ORJtVkvqa-AHHXq3saMSagKNyhimsf2thf6LMDjfpsfVsN-8-PNuKHcQzaatUt93rWUSex8l5NHbBLOoGApYjktpn3x019GpQ_K6hZMOMdO3JhsbZB63-qfUniNRmUCc5CUXaD2o9pyMVetgf9ji7af3_Z734rDaYc68QyC5UfUeCNR4tt8VzHzfnUTy-GDA6DkkrfRDIPqac1cfWw68FwJzQ1ss4H1zdj5-OmqE9Qa6BoVZI3NkU2RKVPtCUrn9ilTI7RaVsba2aHXuI2Q_N9iSCXuEjebMbiq5Mma_TH5kVvLi2J-LLEfl0uCTU37KPowrMsVliR9kgSkmDsOdk9WOxqZXAi3HWlSWgJSGyIVIsSS9nB_Wj45R22LuK5wj9f-C9NQ4tVXL4Kxj1_ceK24p05RUOMW-uucWXl9ZZvqSk9Aojs_mNhPull0MaRkf7f2aeA5OtMjx8EElFHcsXGrw6L-kyrbqH7PX3JTiLXIgUh5kas-smMIg72cxHu2mia8nsUE77Z6mnpa8S2mmBqPzzQc-I3XdJ6gbnc9izYQeEfSS_zrqak-V5yMFHWwrmSectab6OZcQOgZI1gzpRCWu0TTyo2_z4kfFMr_-3OKZ_Ji6mRYqZsKaw9JFqjdd1x1KD8EoyiFCGo3bcj0Y83t3k_EitKs999opqE72R9nN-FPSDtkKOH-SHpAnTB3OzzQPqrzOcYmYohtoTAdtouoiWr-SzhZcyOHUM8TpkwHqc269owDuB7pRz6BIkXpQeJ-EzwQ3L3NedrUdq27rbTwrCVF8OcN48giljIN7iiDMyrRRG-j1pL3ON7wJ7QuuVhlfbmao4_XZn5nAAEXyKJoR7aV-U619AF5ajYbdiJgxk-b9PBmN7t4AiuuwaOOmfQfZmmMoDPBZGhv4gm5uZU1KvUCJf2emSt2b0ft-tbGnDNdS9Tqk5RzRuFIcOyt5jkUZPFzYuKg5UyCvraMi6hqab2qfXWKZxPzLYi3gGy8bET4fol2TT0t4PHmCJnqKfu5oIexL9hvG6Zvxd9MIsKK5CLvOav8o1wEo3qp9oNEnoOWJQRRZqlpgWVCF90vaok2hP0FiUjub4UYyj1StOfSnkJE78eYScJXMNpYfgqIWzi71E9_Fyf3duuR2E6EQP0IVF8VLS05h_FiPisi2pBMpPCrHGe9K90mvRSSpjpnPfUnRRTUGZvDrpdIIDpOGeLqOhnQAPe1d0UWKqbx5M30jWPgDSuQEd3TqJbIWvXPvegLRhBy2TL9kvA8XlAl7yaQLND5D6lUMAa8Ze2I9SDcukVQkGHyx9AUNEcrz5iaxMHYQHiqJIbuNkG_LdC_G-18lM6WzkbRlMF4d9GxyPWPnJ4E-uF1ycjjRSAjgbL5qxbFetB_wSL3vAdP3ojXubWF6lAT8ZbuhO0TOOEOOKf35fj8bKulco_RsmjesmEc4mMeiYWcMVIe6Tcjq9RZ3djOZSzO_X1ePBf2J_ZlYbqVabk-iWVLBXApXVQ3aLxWYtFlUSX5QVuDyPCLE0kI7QSFbkXWI01nn5UILbWp_7jODxFagUfN4omnIwlgNjOneEsX62sS_daLoiY8nLegSfypRaXB9x7KE_S1vsisIxPpLHYzVXpwJ7I5BCu6QQlhMTngXmISTATVQ3_eE4lwsLlLPTcA3mooZULhXH-MOEk1utCRloG_sq50P-D4Fxk3_bZO8FmP_X7suaW6AOb7pVT4ZjDSc76WzqEUJL-5mEtMkb7NAiPMG4-jiMs_iGAf7781MdOYDC0EyxDNuucbxgQ1ZQteAAEzWkuKqnQEm3DYIPWXtbBid90CGEgOBV9eK3oEyR1xGfaUgv6Votl1Mbd2zyFoQvKMBlYnAc9Mv3jTXhQIOMJ05128mHw81W7MR1gGbWVYMn4sGn1ySOiDtzA-WbFcMlaX2ThMvVXi5HEI-o5JyFymOEgW0Y1Zx21bvkmUgJd1X8FLRFP7hPvlGBpRNOfSAyYYBIFYtG7LKfZlTXi4OeNX6rsW7xK5m6zvuhc5GAI-8ztKw_uzOOxRUkZYGKbxfCNU6_Z4EDjzRB0fGpZvx4uMRsgbmY-yJ5j5NSeFZoAdz9QkQbHhXIO2Mox8iIl0kWJA9CBsqMWENAW-Bccd5a8zwaCT9pnfBE94PfhoWYVHpGfd7AE9pk8KS4n04V9W7v9icShhhXoiuA-zUxJrZKvdpTehrSIRymuLYee-mLhBHUAKtG0iLmZ88AwxChHPt5NslFKLFXEijYGUWgHR6IreMgiP3oKO1BcUleNmVOyhVFREdhLdKG-30ZoXiDNU5ZBXfQEI2yuEoUjFYmERhdTV1oWlUIuBhx4rM6FAqPJr5l86-sq5FzY67-m8LFs-MTVtjAJxLXoPRuo9_yit3A==";
}
