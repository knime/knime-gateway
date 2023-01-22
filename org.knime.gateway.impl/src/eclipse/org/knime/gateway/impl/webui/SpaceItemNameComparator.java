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
 *   Jan 22, 2023 (leonard.woerteler): created
 */
package org.knime.gateway.impl.webui;

import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

import org.knime.core.data.sort.AlphanumericComparator;

/**
 * Comparator for comparing the names of space items. Since file names are naturally structured by periods and other
 * special characters, we partition the input strings into alphanumeric and non-alphanumeric characters and compare the
 * resulting sequences lexicographically using the a case-insensitive {@link AlphanumericComparator} for string
 * comparisons.
 *
 * If two strings compare as equal using this scheme, they are compared according to their
 * {@link Comparator#naturalOrder() natural order} to make sure that case differences don't lead to ambiguity.
 *
 * @author Leonard Wörteler, KNIME GmbH, Konstanz, Germany
 */
public enum SpaceItemNameComparator implements Comparator<String> {

    /** Singleton instance of this comparator. */
    INSTANCE;

    /** Comparator for string segments. */
    private static final Comparator<String> INNER = new AlphanumericComparator(String::compareToIgnoreCase);

    /** Pattern for splitting a name into alternating segments of alphanumeric and non-alphanumeric characters. */
    private static final Pattern ALPHANUM_BORDER = Pattern.compile(
        "(?<=\\p{Alnum})(?!\\p{Alnum})|" + // border between an alphanumeric and a non-alphanumeric character or...
        "(?<!\\p{Alnum})(?=\\p{Alnum})",   // border between a non-alphanumeric and an alphanumeric character
        Pattern.UNICODE_CHARACTER_CLASS
    );

    @Override
    public int compare(final String o1, final String o2) {
        final var res = Arrays.compare(ALPHANUM_BORDER.split(o1), ALPHANUM_BORDER.split(o2), INNER);
        return res != 0 ? res : o1.compareTo(o2);
    }
}
