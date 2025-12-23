package org.knime.gateway.api.util;

import org.apache.commons.io.FileUtils;

/**
 * Type- and unit-safe description of data size/volume.
 * 
 * @param bytes -
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
