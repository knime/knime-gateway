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
 *   17 Jul 2025 (albrecht): created
 */
package org.knime.gateway.impl.webui.preview.util;

import java.util.Map;

/**
 * Color constants for shapes drawn on the workflow canvas. Derived from component/workflow/util/colors.ts
 *
 * @author Christian Albrecht, KNIME GmbH, Konstanz, Germany
 * @since 5.6
 */
@SuppressWarnings("javadoc")
public final class ColorConstants {

    /* Color values are made available as a map, because static fields are not easily accessible for ThymeLeaf.
     * In case they are supposed to be used in Java as well a type safe map can be created as in ShapeConstants.
     */

    public static final Map<String, String> KNIME_COLORS = Map.ofEntries(
        Map.entry("Yellow", "hsl(50.8, 100%, 50%)"),            // #FFD800
        Map.entry("Masala", "hsl(12, 4.2%, 23.3%)"),            // #3E3A39
        Map.entry("DoveGray", "hsl(0, 0%, 43.1%)"),             // #6E6E6E
        Map.entry("SilverSand", "hsl(200, 5%, 76.5%)"),         // #C0C4C6
        Map.entry("Porcelain", "hsl(200, 10.33%, 94.3%)"),      // #EFF1F2
        Map.entry("White", "hsl(0, 0%, 100%)"),                 // #FFFFFF
        Map.entry("Black", "hsl(0, 3%, 12%)"),                  // #201E1E

        Map.entry("Avocado", "hsl(78, 38.7%, 75.7%)"),          // #CBD9A9
        Map.entry("AvocadoDark", "hsl(60, 23%, 49.4%)"),        // #9B9B61
        Map.entry("MeadowLight", "hsl(70, 78.3%, 54.9%)"),      // #C8E632
        Map.entry("Aquamarine", "hsl(188, 63%, 71.4%)"),        // #88D8E4
        Map.entry("StoneLight", "hsl(220, 4.3%, 86.5%)"),       // #DBDCDE
        Map.entry("StoneGray", "hsl(0, 0%, 53.3%)"),            // #888888
        Map.entry("Wood", "hsl(24, 46.4%, 67.1%)"),             // #D2A384
        Map.entry("Meadow", "hsl(128, 50%, 47.1%)"),            // #3CB44B
        Map.entry("Lavender", "hsl(305, 27.7%, 46.1%)"),        // #965591
        Map.entry("Coral", "hsl(0, 100%, 64.7%)"),              // #FF4B4B
        Map.entry("Carrot", "hsl(29, 100%, 59.8%)"),            // #FF9632
        Map.entry("Stone", "hsl(0, 0%, 66.7%)"),                // #AAAAAA
        Map.entry("AquamarineDark", "hsl(193, 60.9%, 43.1%)"),  // #2B94B1
        Map.entry("HibiscusDark", "hsl(329, 71.4%, 52%)")       // #DC2D87
    );

    public static final Map<String, String> NODE_COLORS = Map.ofEntries(
        Map.entry("Component", KNIME_COLORS.get("SilverSand")),
        Map.entry("Configuration", KNIME_COLORS.get("Avocado")),
        Map.entry("Container", KNIME_COLORS.get("AvocadoDark")),
        Map.entry("Learner", KNIME_COLORS.get("MeadowLight")),
        Map.entry("Loop", KNIME_COLORS.get("Aquamarine")),
        Map.entry("LoopEnd", KNIME_COLORS.get("Aquamarine")),
        Map.entry("LoopStart", KNIME_COLORS.get("Aquamarine")),
        Map.entry("Manipulator", KNIME_COLORS.get("Yellow")),
        Map.entry("Metanode", KNIME_COLORS.get("StoneLight")),
        Map.entry("MetanodeSecondary", KNIME_COLORS.get("StoneGray")),
        Map.entry("Other", KNIME_COLORS.get("Wood")),
        Map.entry("Predictor", KNIME_COLORS.get("Meadow")),
        Map.entry("QuickForm", KNIME_COLORS.get("Avocado")),
        Map.entry("ScopeEnd", KNIME_COLORS.get("Lavender")),
        Map.entry("ScopeStart", KNIME_COLORS.get("Lavender")),
        Map.entry("Sink", KNIME_COLORS.get("Coral")),
        Map.entry("Source", KNIME_COLORS.get("Carrot")),
        Map.entry("VirtualIn", KNIME_COLORS.get("Stone")),
        Map.entry("VirtualOut", KNIME_COLORS.get("Stone")),
        Map.entry("Visualizer", KNIME_COLORS.get("AquamarineDark")),
        Map.entry("Widget", KNIME_COLORS.get("AquamarineDark"))
    );

    public static final Map<String, String> PORT_COLORS = Map.ofEntries(
        Map.entry("table", KNIME_COLORS.get("Black")),
        Map.entry("flowVariable", KNIME_COLORS.get("Coral")),

        Map.entry("generic", "hsl(0, 0%, 61%)"),
        Map.entry("inactive", "hsl(0, 100%, 50%)"), // x
        Map.entry("inactiveOutline", "hsla(0, 100%, 100%, 66%)") // outline around "×"
    );

    public static final Map<String, String> CONNECTOR_COLORS = Map.ofEntries(
        Map.entry("default", KNIME_COLORS.get("StoneGray")),
        Map.entry("flowVariable", PORT_COLORS.get("flowVariable"))
    );

    public static final Map<String, String> TRAFFIC_LIGHT = Map.ofEntries(
        Map.entry("red", "hsl(357, 72%, 45%)"),
        Map.entry("redBorder", "hsl(348, 94%, 21%)"),
        Map.entry("yellow", KNIME_COLORS.get("Yellow")),
        Map.entry("yellowBorder", "hsla(0, 0%, 0%, 54%)"),
        Map.entry("green", "hsl(107, 43%, 55%)"),
        Map.entry("greenBorder", "hsl(116, 47%, 26%)"),
        Map.entry("blue", "hsl(206, 69.7%, 55.9%)"),
        Map.entry("inactive", "hsl(0, 0%, 100%)"),
        Map.entry("inactiveBorder", "hsl(0, 0%, 73%)"),
        Map.entry("background", "hsl(192, 6.8%, 85.7%)")
    );

    public static final Map<String, Object> COLORS = Map.of(
        "knimeColors", KNIME_COLORS,
        "nodeBackgroundColors", NODE_COLORS,
        "portColors", PORT_COLORS,
        "connectorColors", CONNECTOR_COLORS,
        "trafficLight", TRAFFIC_LIGHT,

        "textDefault", KNIME_COLORS.get("Masala"),
        "linkDecorator", KNIME_COLORS.get("Black"),
        "darkeningMask", "hsla(0, 0%, 0%, 33.3%)",

        "error", TRAFFIC_LIGHT.get("red"),
        "warning", KNIME_COLORS.get("Yellow")
    );

    private ColorConstants() {}

}
