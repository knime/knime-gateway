/*
 * ------------------------------------------------------------------------
 *
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <https://www.gnu.org/licenses/>.
 *
 * ---------------------------------------------------------------------
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
 * ---------------------------------------------------------------------
 */
package org.knime.gateway.api.util;

import org.apache.commons.io.FileUtils;

/**
 * Type- and unit-safe description of data size/volume.
 *
 * @param bytes -
 *
 * @since 5.10
 */
public record DataSize(long bytes) {

    /**
     * Represents the 0-value.
     */
    public static final DataSize ZERO = new DataSize(0);

    /**
     * {@code 1024} bytes, sometimes referred to incorrectly as "kilobyte"
     * @param kibibytes -
     * @return -
     */
    public static DataSize ofKibiBytes(final long kibibytes) {
        return new DataSize(kibibytes * FileUtils.ONE_KB);
    }

    @Override
    public String toString() {
        return FileUtils.byteCountToDisplaySize(this.bytes());
    }
}
