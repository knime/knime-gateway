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
     * {@code 1024 * 1024} bytes, sometimes also incorrectly referred to as "megabyte".
     * 
     * @param mebibytes
     * @return
     */
    public static DataSize ofMebibytes(final long mebibytes) {
        return new DataSize(mebibytes * FileUtils.ONE_MB);
    }

    @Override
    public String toString() {
        return FileUtils.byteCountToDisplaySize(this.bytes());
    }
}
