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
 */
package org.knime.gateway.impl.webui.spaces.local;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.knime.core.node.NodeLogger;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;

/**
 * Listen for changes in the given {@link LocalWorkspace} and notify listeners.
 * This implementation notifies without restrictions, it is left to the caller to throttle notifications.
 */
final class LocalResourceChangedNotifier implements SpaceProvider.ProviderResourceChangedNotifier {

    private static final Duration POLL_INTERVAL = Duration.ofMillis(500);

    private static final NodeLogger LOGGER = NodeLogger.getLogger(LocalResourceChangedNotifier.class);

    private final LocalWorkspace m_space;

    private final Map<SpaceProvider.SpaceAndItemId, FileAlterationMonitor> m_subscriptions = new HashMap<>();

    public LocalResourceChangedNotifier(final LocalWorkspace space) {
        m_space = space;
    }

    @SuppressWarnings({"java:S1602", "java:S1941"})
    private static Optional<FileAlterationMonitor> createAndStartMonitor(final Runnable callback,
        final File targetPath) {
        var isSibling = createFilter(file -> {
            // Avoid reporting changes in subdirectories.
            return targetPath.equals(file.getParentFile());
        });
        var isValidWorkspaceItem = createFilter(file -> {
            // Constrain to items that can be displayed.
            return LocalWorkspace.isValidWorkspaceItem(file.toPath());
        });
        var observer = new FileAlterationObserver(targetPath, FileFilterUtils.and(isSibling, isValidWorkspaceItem));
        observer.addListener(fileOrDirectoryCreationOrDeletionInCurrent(callback));
        var monitor = new FileAlterationMonitor(POLL_INTERVAL.toMillis());
        try {
            monitor.addObserver(observer);
            monitor.start();
        } catch (Exception e) {
            LOGGER.warn("Could not attach observer or start monitor", e);
            return Optional.empty();
        }
        return Optional.of(monitor);
    }

    private static IOFileFilter createFilter(final Predicate<File> predicate) {
        return new IOFileFilter() {
            @Override
            public boolean accept(final File file) {
                return predicate.test(file);
            }

            @Override
            public boolean accept(final File file, final String s) {
                // never called in this use-case
                return true;
            }
        };
    }

    private static void stop(final FileAlterationMonitor monitor) {
        if (monitor == null) {
            return;
        }
        try {
            monitor.stop();
        } catch (Exception e) {
            LOGGER.warn("Could not stop unsubscribed monitor", e);
        }
    }

    @SuppressWarnings("java:S1188")
    private static FileAlterationListener fileOrDirectoryCreationOrDeletionInCurrent(final Runnable callback) {
        return new FileAlterationListener() {
            @Override
            public void onDirectoryChange(final File file) {
                // triggers also if a directory is created/deleted within a subdirectory of target
                // currently not interested
            }

            @Override
            public void onDirectoryCreate(final File file) {
                // triggers only if a directly is created directly in the target, not in subdirectories
                callback.run();
            }

            @Override
            public void onDirectoryDelete(final File file) {
                // triggers only if a directly is deleted directly in the target, not in subdirectories
                callback.run();
            }

            @Override
            public void onFileChange(final File file) {
                // does not trigger on file creations or modifications in subdirectories
                // does not trigger on file creations (e.g. `touch`) in target directory
                // triggers on file modifications in target
                // currently not interested
            }

            @Override
            public void onFileCreate(final File file) {
                // does not trigger on file creations or modifications in subdirectories
                callback.run();
            }

            @Override
            public void onFileDelete(final File file) {
                // does not trigger on file removals in subdirectories
                callback.run();
            }

            @Override
            public void onStart(final FileAlterationObserver fileAlterationObserver) {
                // currently not interested
            }

            @Override
            public void onStop(final FileAlterationObserver fileAlterationObserver) {
                // currently not interested
            }
        };
    }

    @Override
    public void subscribeToItem(final String space, final String item, final Runnable callback) {
        if (!assertLocal(space)) {
            return;
        }
        var key = new SpaceProvider.SpaceAndItemId(space, item);
        if (m_subscriptions.containsKey(key)) {
            unsubscribe(space, item);
        }
        var path = m_space.getAbsolutePath(item).toFile();
        var monitor = createAndStartMonitor(callback, path);
        if (monitor.isEmpty()) {
            return;
        }
        m_subscriptions.put(key, monitor.get());
    }

    private boolean assertLocal(final String space) {
        if (!space.equals(LocalWorkspace.LOCAL_SPACE_ID)) {
            LOGGER.warn("Cannot attach %s to space with ID other than %s (is: %s)".formatted( //
                this.getClass().getName(), //
                LocalWorkspace.LOCAL_SPACE_ID, //
                space //
            ));
            return false;
        }
        return true;
    }

    @Override
    public void unsubscribe(final String spaceId, final String itemId) {
        var removedMonitor = m_subscriptions.remove(new SpaceProvider.SpaceAndItemId(spaceId, itemId));
        stop(removedMonitor);
    }

    @Override
    public void unsubscribeAll() {
        m_subscriptions.values().forEach(LocalResourceChangedNotifier::stop);
        m_subscriptions.clear();
    }
}
