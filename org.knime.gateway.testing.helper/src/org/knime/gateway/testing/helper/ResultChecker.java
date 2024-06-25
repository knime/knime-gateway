/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright by KNIME AG, Zurich, Switzerland
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.com
 * email: contact@knime.com
 * ---------------------------------------------------------------------
 */
package org.knime.gateway.testing.helper;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;

/**
 * Compares objects to a representation stored to files (i.e. snapshot testing).
 *
 * The objects (e.g. WorkflowEnt) are compared by turning them into a string via jackson and compare it to a static
 * string.
 *
 * A snapshot is stored into its own file, placed in a directory named after the test-class which carries out the test.
 *
 * Snapshot can be re-written by deleting the respective snapshot file (.snap). If a snapshot doesn't match, a debug
 * file (.snap.debug) will be created next to the original snapshot file what allows direct file comparison. The debug
 * file will be deleted automatically as soon as the respective snapshot matches again.
 *
 * @author Martin Horn, KNIME GmbH, Konstanz, Germany
 */
public class ResultChecker {

    private final File m_resultDirectory;

    private final ObjectToString m_objToString;

    /**
     * Creates a new instance of the result checker.
     *
     * @param objToString turns objects into strings subsequently used for comparison, if <code>null</code>
     *            {@link Object#toString()} is used
     * @param resultDirectory the directory to read the result-snapshots from (and write them to, if not present), can
     *            be <code>null</code> if there are no exceptions
     */
    public ResultChecker(final ObjectToString objToString, final File resultDirectory) {
        m_objToString = objToString;
        m_resultDirectory = resultDirectory;
    }

    /**
     * Checks an object by comparing it to a string referenced by a specific test name and a result key.
     *
     * @param testClass the class running the test
     * @param obj the object to snapshot-test
     * @param snapshotName the name for the snapshot
     * @throws AssertionError if the result check failed (e.g. if the entity differs from the representation referenced
     *             by the given key)
     */
    public void checkObject(final Class<?> testClass, final String snapshotName, final Object obj) {
        try {
            compareWithSnapshotFromFile(testClass, snapshotName, obj);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to compare with snapshot from file", ex); // NOSONAR
        }
    }

    /**
     * See {@link #checkObject(Class, String, Object)} where the object to be checked is a string. In this case, no
     * object mapper is required.
     *
     * @param testClass
     * @param snapshotName
     * @param s
     */
    public void checkString(final Class<?> testClass, final String snapshotName, final String s) {
        checkObject(testClass, snapshotName, s);
    }

    private void compareWithSnapshotFromFile(final Class<?> testClass, final String snapshotName, final Object obj)
        throws IOException {
        String actual = m_objToString == null ? obj.toString() : m_objToString.toString(obj);
        Path snapFile = getSnapshotFile(testClass, snapshotName);
        if (Files.exists(snapFile)) {
            // load expected snapshot and compare
            String expected = new String(Files.readAllBytes(snapFile), StandardCharsets.UTF_8);
            Path debugFile = getSnapshotDebugFile(testClass, snapshotName);
            if (!actual.equals(expected)) {
                // write debug file if snapshot doesn't match
                Files.write(debugFile, actual.getBytes(StandardCharsets.UTF_8));
                assertEquals(snapshotName, testClass, expected.replace("\r", ""), actual.replace("\r", ""));
            } else if (Files.exists(debugFile)) {
                // if snapshot matches, delete debug file (might not exist)
                Files.delete(debugFile);
            } else {
                //
            }
        } else {
            // just write the snapshot
            Files.createDirectories(snapFile.getParent());
            Files.write(snapFile, actual.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        }
    }

    private static void assertEquals(final String snapshotName, final Class<?> testClass, final String expected,
        final String actual) {
        assertThat(String.format("Snapshot '%s' in test '%s' doesn't match", snapshotName, testClass.getSimpleName()),
            actual, compareWithDiff(expected.strip()));
    }

    private static Matcher<String> compareWithDiff(final String expected) {
        return new BaseMatcher<String>() { // NOSONAR

            @Override
            public boolean matches(final Object item) {
                return (item instanceof String str) && str.strip().equals(expected);
            }

            @Override
            public void describeMismatch(final Object item, final Description description) {
                if (item instanceof String str) {
                    Patch<String> diff = DiffUtils.diff(expected, str, null);
                    description.appendText("there are differences");
                    if (matchSorted(expected, str)) {
                        description.appendText(" (NOTE: snapshots match if their lines are sorted!)");
                    }

                    description.appendText(
                        ":\n" + diff.getDeltas().stream().map(Object::toString).collect(Collectors.joining(",\n")));
                } else {
                    description.appendText("not a String");
                }
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Snapshot file content");
            }

            private boolean matchSorted(final String a, final String b) {
                return a.lines().sorted().collect(Collectors.joining("\n"))
                    .equals(b.lines().sorted().collect(Collectors.joining("\n")));
            }
        };
    }

    private Path getSnapshotFile(final Class<?> testClass, final String snapshotName) {
        return Paths.get(m_resultDirectory.getAbsolutePath(), getDirFromClass(testClass) + snapshotName + ".snap");
    }

    private Path getSnapshotDebugFile(final Class<?> testClass, final String snapshotName) {
        return Paths.get(m_resultDirectory.getAbsolutePath(),
            getDirFromClass(testClass) + snapshotName + ".snap.debug");
    }

    private static String getDirFromClass(final Class<?> testClass) {
        return "/" + testClass.getCanonicalName() + "/";
    }

}