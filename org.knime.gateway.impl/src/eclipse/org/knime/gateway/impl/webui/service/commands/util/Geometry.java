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
 */
package org.knime.gateway.impl.webui.service.commands.util;

import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

import org.knime.gateway.api.webui.entity.XYEnt;

public final class Geometry {

    private Geometry() {

    }

    public static class Vec2D { // NOSONAR

        private final int x;

        private final int y;

        private Vec2D(int a, int b) {
            x = a;
            y = b;
        }

        static <T extends Vec2D> T zip(Vec2D a, Vec2D b, IntBinaryOperator zipper,
            BiFunction<Integer, Integer, T> wrapper) {
            return wrapper.apply( //
                zipper.applyAsInt(a.x(), b.x()), //
                zipper.applyAsInt(a.y(), b.y()) //
            );
        }

        static <T extends Vec2D> T map(T a, IntUnaryOperator mapper, BiFunction<Integer, Integer, T> wrapper) {
            return wrapper.apply( //
                mapper.applyAsInt(a.x()), //
                mapper.applyAsInt(a.y()) //
            );
        }

        public static <T extends Vec2D> T min(Vec2D a, Vec2D b, BiFunction<Integer, Integer, T> wrapper) {
            return zip(a, b, Math::min, wrapper);
        }

        public int x() {
            return x;
        }

        public int y() {
            return y;
        }

        public int[] toArray() {
            return new int[]{x(), y()};
        }

        public boolean isZero() {
            return x() == 0 && y() == 0;
        }
    }

    public static class Point extends Vec2D {

        public static final Point MAX_VALUE = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        public Point(int a, int b) {
            super(a, b);
        }

        public static Point of(int[] pos) {
            return new Point(pos[0], pos[1]);
        }

        public static Point of(XYEnt ent) {
            return new Point(ent.getX(), ent.getY());
        }

        public static Point min(Point a, Point b) {
            return min(a, b, Point::new);
        }

        @SafeVarargs
        public static Point min(Stream<Point>... pointStreams) {
            return Stream.of(pointStreams).flatMap(s -> s).reduce(Point.MAX_VALUE, Point::min);
        }

    }

    public static class Delta extends Vec2D {

        public Delta(int a, int b) {
            super(a, b);
        }

        public static Delta of(Point a, Point b) {
            return zip(a, b, (p, q) -> p - q, Delta::new);
        }

        public static Delta of(XYEnt ent) {
            return new Delta(ent.getX(), ent.getY());
        }

        public Delta invert() {
            return map(this, i -> i * (-1), Delta::new);
        }

    }
}
