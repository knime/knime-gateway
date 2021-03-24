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
 *   Mar 17, 2021 (hornm): created
 */
package org.knime.gateway.impl.webui;

import java.util.HashSet;
import java.util.Set;

/**
 * A utility class to calculate the tanimoto bi-gram distance.
 *
 * @author Marcel Hanser, KNIME AG, Zurich, Switzerland
 */
public final class TanimotoBiGramDistance {

    private TanimotoBiGramDistance() {
        // utility class
    }

    /**
     * Copied from the Tanimoto BiGram distance from the distmatrix package.
     *
     * @param textA
     * @param textB
     * @return the distance value
     */
    public static double computeTanimotoBiGramDistance(final String textA, final String textB) {

        Set<String> gramsA = split(textA, 2);
        Set<String> gramsB = split(textB, 2);

        int nominator = cardinalityOfIntersection(gramsA, gramsB);
        int inAButNotInB = cardinalityOfRelativeComplement(gramsA, gramsB);
        int inBButNotInA = cardinalityOfRelativeComplement(gramsB, gramsA);

        int denominator = nominator + inAButNotInB + inBButNotInA;

        if (denominator > 0) {
            return 1.0 - nominator / (double)denominator;
        } else {
            return 1.0;
        }
    }

    private static int cardinalityOfIntersection(final Set<String> a, final Set<String> b) {
        int toReturn = 0;
        for (String gram : a) {
            if (b.contains(gram)) {
                toReturn++;
            }
        }
        return toReturn;
    }

    private static int cardinalityOfRelativeComplement(final Set<String> a, final Set<String> b) {
        int toReturn = 0;
        for (String gram : a) {
            if (!b.contains(gram)) {
                toReturn++;
            }
        }
        return toReturn;
    }

    private static Set<String> split(final String a, final int count) {
        Set<String> toReturn = new HashSet<>(a.length() > 1 ? (a.length() - 1) : 12);

        for (int i = 0; i < a.length() - count + 1; i++) {
            toReturn.add(a.substring(i, i + count));
        }
        return toReturn;
    }
}
