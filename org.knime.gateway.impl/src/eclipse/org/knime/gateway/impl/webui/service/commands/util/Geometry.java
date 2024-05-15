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

import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.knime.core.node.util.CheckUtils;
import org.knime.gateway.api.webui.entity.XYEnt;

/**
 * Utility class for geometric objects.
 *
 * @author Benjamin Moser, KNIME GmbH, Konstanz, Germany
 */
public final class Geometry {

    private Geometry() {

    }

    private static sealed class Vec2D {  // NOSONAR: sealed, cannot make final

        private final int m_x;

        private final int m_y;

        private Vec2D(final int x, final int y) {
            m_x = x;
            m_y = y;
        }

        static <T extends Vec2D> T zip(final Vec2D a, final Vec2D b, final IntBinaryOperator zipper,
            final BiFunction<Integer, Integer, T> wrapper) {
            return wrapper.apply( //
                zipper.applyAsInt(a.x(), b.x()), //
                zipper.applyAsInt(a.y(), b.y()) //
            );
        }

        static <T extends Vec2D> T map(final T a, final IntUnaryOperator mapper,
            final BiFunction<Integer, Integer, T> wrapper) {
            return wrapper.apply( //
                mapper.applyAsInt(a.x()), //
                mapper.applyAsInt(a.y()) //
            );
        }

        public static <T extends Vec2D> T min(final Vec2D a, final Vec2D b,
            final BiFunction<Integer, Integer, T> wrapper) {
            return zip(a, b, Math::min, wrapper);
        }

        public int x() {
            return m_x;
        }

        public int y() {
            return m_y;
        }

        public int[] toArray() {
            return new int[]{x(), y()};
        }

        public boolean isZero() {
            return x() == 0 && y() == 0;
        }

    }

    /**
     * Represents a point in the 2-dimensional space.
     */
    public static final class Point extends Vec2D implements Comparable<Point> {

        /**
         * An instance containing maximal values
         */
        public static final Point MAX_VALUE = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

        Point(final int x, final int y) {
            super(x, y);
        }

        /**
         * @param xy the x,y coordinates of the point. Any additional array elements are ignored.
         * @return the new instance
         */
        public static Point of(final int... xy) {
            return new Point(xy[0], xy[1]);
        }

        /**
         * @param ent the {@link XYEnt} to create the point from
         * @return the new instance
         */
        public static Point of(final XYEnt ent) {
            return new Point(ent.getX(), ent.getY());
        }

        /**
         * @return the min of two points
         */
        public static Point min(final Point a, final Point b) {
            return min(a, b, Point::new);
        }

        /**
         * @param pointStreams One or multiple streams of points
         * @return The minimal point
         */
        @SafeVarargs
        public static Point min(final Stream<Point>... pointStreams) {
            return Stream.of(pointStreams).flatMap(s -> s).reduce(Point.MAX_VALUE, Point::min);
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof Point)) {
                return false;
            }

            if (other == this) {
                return true;
            }

            return new EqualsBuilder()//
                .append(this.x(), ((Point)other).x())//
                .append(this.y(), ((Point)other).y())//
                .build();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()//
                .append(this.x())//
                .append(this.y())//
                .build();
        }

        @Override
        public int compareTo(final Point other) {
            return Comparator //
                .comparingInt(Point::x) //
                .thenComparingInt(Point::y) //
                .compare(this, other); //
        }

        public Point translate(final Delta delta) {
            return zip(this, delta, Integer::sum, Point::new);
        }
    }

    /**
     * Represents a delta in the 2-dim space.
     */
    public static final class Delta extends Vec2D {

        Delta(final int dx, final int dy) {
            super(dx, dy);
        }

        /**
         * @param dx
         * @param dy
         * @return a new instance
         */
        public static Delta of(final int dx, final int dy) {
            return new Delta(dx, dy);
        }

        /**
         * Calculates the delta between two points.
         *
         * @param a
         * @param b
         * @return a new instance
         */
        public static Delta of(final Point a, final Point b) {
            return zip(a, b, (p, q) -> p - q, Delta::new);
        }

        /**
         * @param ent the {@link XYEnt} representing the deltas
         * @return a new instance
         */
        public static Delta of(final XYEnt ent) {
            return new Delta(ent.getX(), ent.getY());
        }

        /**
         * @return a new instance with the inverted deltas
         */
        public Delta invert() {
            return map(this, i -> i * (-1), Delta::new);
        }

    }

    /**
     * The more precise name for this would be "closed interval": Start and end values are inclusive, that is, part of
     * the range.
     *
     * @param start
     * @param end
     */
    record Range(int start, int end) {

        boolean intersects(final Range other) {
            return
            // this:      [------------]
            // other:          [------------]
            (this.start() < other.start() && other.start() < this.end()) //
                // this:           [------------]
                // other:     [------------]
                || (other.start() < this.start() && this.start() < other.end());
        }
    }

    public static class Rectangle {

        public static final Comparator<Rectangle> NORTH_WEST_ORDERING = Comparator.comparing(Rectangle::leftTop);

        private final Point m_leftTop;

        private final int m_width;

        private final int m_height;

        Rectangle(final Point leftTop, final int width, final int height) {
            CheckUtils.checkArgument(width >= 0, "Width must be non-negative");
            CheckUtils.checkArgument(height >= 0, "Height must be non-negative");
            m_leftTop = leftTop;
            m_width = width;
            m_height = height;
        }

        Range xRange() {
            return new Range(leftTop().x(), leftTop().x() + m_width);
        }

        public Point leftTop() {
            return m_leftTop;
        }

        int width() {
            return m_width;
        }

        int height() {
            return m_height;
        }

    }

    /**
     * @implNote {@link Bounds#MIN_VALUE} and {@link Bounds#MAX_VALUE} must have zero width and height, else
     *  {@link Range} computation runs into value overflows.
     */
    public static final class Bounds extends Rectangle {

        public static final Bounds MIN_VALUE = new Bounds(new Point(Integer.MIN_VALUE, Integer.MIN_VALUE), 0, 0);

        public static final Bounds MAX_VALUE = new Bounds(new Point(Integer.MAX_VALUE, Integer.MAX_VALUE), 0, 0);

        /**
         * @param coords array of the shape [x, y, width, height, ...]. Any further elements are ignored.
         */
        public Bounds(final int... coords) {
            this( //
                new Point(coords[0], coords[1]), //
                coords[2], //
                coords[3] //
            );
        }

        public Bounds(final Point leftTop, final int width, final int height) {
            super(leftTop, width, height);
        }

        public Bounds translate(final Delta delta) {
            return new Bounds(this.leftTop().translate(delta), this.width(), this.height());
        }
    }

}
