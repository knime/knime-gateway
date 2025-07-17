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
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.knime.core.node.NodeLogger;
import org.knime.gateway.api.webui.service.util.MutableServiceCallException;
import org.knime.gateway.impl.service.util.CallThrottle;
import org.knime.gateway.impl.webui.spaces.SpaceProvider;
import org.knime.gateway.impl.webui.spaces.SpaceProvider.SpaceAndItemId;

/**
 * Listen for changes in the given {@link LocalSpace} and notify listeners. This implementation notifies without
 * restrictions, it is left to the caller to throttle notifications.
 *
 * @author Benjamin Moser, KNIME GmbH
 */
final class LocalSpaceItemChangeNotifier implements SpaceProvider.SpaceItemChangeNotifier {

    private static final String SYSPROP = "org.knime.ui.local_space_change_notifier";

    private static final Duration DEFAULT_POLL_INTERVAL = Duration.ofMillis(500);

    private static final NodeLogger LOGGER = NodeLogger.getLogger(LocalSpaceItemChangeNotifier.class);

    private final LocalSpaceProvider m_spaceProvider;

    private final Map<SpaceAndItemId, Subscription> m_subscriptions = new HashMap<>();

    private final AsNeeded<FileAlterationMonitor> m_monitorAsNeeded =
        new AsNeeded<>(LocalSpaceItemChangeNotifier::createAndStartMonitor, LocalSpaceItemChangeNotifier::stopMonitor);

    LocalSpaceItemChangeNotifier(final LocalSpaceProvider space) {
        m_spaceProvider = space;
    }

    static boolean isEnabled() {
        return Boolean.getBoolean(SYSPROP + "_enabled");
    }

    private static Duration getPollInterval() {
        var configuredProperty = System.getProperty(SYSPROP + "_pollInterval");
        if (configuredProperty != null) {
            return Duration.ofMillis(Long.parseLong(configuredProperty));
        }
        return DEFAULT_POLL_INTERVAL;
    }

    private static void stopMonitor(final FileAlterationMonitor monitor) {
        try {
            monitor.stop();
        } catch (Exception e) {
            LOGGER.warn("Could not stop monitor", e);
        }
    }

    private static FileAlterationMonitor createAndStartMonitor() {
        var monitor = new FileAlterationMonitor(getPollInterval().toMillis());
        try {
            monitor.start();
        } catch (Exception e) {
            LOGGER.warn("Could not start monitor", e);
        }
        return monitor;
    }

    private static IOFileFilter createFilter(final Predicate<? super File> predicate) {
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

    private static void assertLocal(final String spaceId) {
        assert LocalSpace.LOCAL_SPACE_ID.equals(spaceId) : "Cannot attach %s to space with ID other than %s (is: %s)"
            .formatted( //
                LocalSpaceItemChangeNotifier.class.getName(), //
                LocalSpace.LOCAL_SPACE_ID, //
                spaceId //
            );
    }

    @Override
    public void subscribeToItem(final String spaceId, final String itemId, final Runnable callback) {
        var item = new SpaceAndItemId(spaceId, itemId);
        var throttledCallback = new CallThrottle(callback, this.getClass().getName() + " callThrottle");
        var listener = fileOrDirectoryCreationOrDeletionInCurrent(throttledCallback::invoke);
        var observer = createObserver(item);
        observer.addListener(listener);
        m_monitorAsNeeded.getAndIncrementUsages().addObserver(observer);
        m_subscriptions.put(item, new Subscription(observer, throttledCallback));
    }

    @Override
    public void unsubscribe(final String spaceId, final String itemId) {
        var subscription = m_subscriptions.remove(new SpaceAndItemId(spaceId, itemId));
        if (subscription != null) {
            m_monitorAsNeeded.mapAndDecrementUsages(monitor -> monitor.removeObserver(subscription.observer()));
            subscription.callback().dispose();
        }
    }

    @Override
    public void unsubscribeAll() {
        new HashSet<>(m_subscriptions.keySet()).forEach(item -> this.unsubscribe(item.spaceId(), item.itemId()));
    }

    @SuppressWarnings({"java:S1602", "java:S1941"})
    private FileAlterationObserver createObserver(final SpaceAndItemId item) {
        assertLocal(item.spaceId());
        File targetPath;
        try {
            targetPath = m_spaceProvider.getSpace(item.spaceId()).getAbsolutePath(item.itemId()).toFile();
        } catch (final MutableServiceCallException ex) {
            throw new IllegalStateException(ex.toGatewayException("Failed to resolve local item"));
        }
        var isSibling = createFilter(file -> {
            // Avoid reporting changes in subdirectories.
            return targetPath.equals(file.getParentFile());
        });
        var isValidWorkspaceItem = createFilter(file -> {
            // Constrain to items that can be displayed.
            return LocalSpace.isValidItem(file.toPath());
        });
        return new FileAlterationObserver( //
            targetPath, //
            FileFilterUtils.and(isSibling, isValidWorkspaceItem) //
        );
    }

    private record Subscription(FileAlterationObserver observer, CallThrottle callback) {

    }

    private static class AsNeeded<V> {
        private final Supplier<V> m_setUp;

        private final Consumer<V> m_dispose;

        V m_value;

        private int m_usages;

        AsNeeded(final Supplier<V> setUp, final Consumer<V> dispose) {
            m_setUp = setUp;
            m_dispose = dispose;
        }

        V getAndIncrementUsages() {
            if (m_value == null) {
                m_value = m_setUp.get();
            }
            m_usages = m_usages + 1;
            return m_value;
        }

        /**
         * This accepts a mapping function because the value may be disposed by {@code m_dispose};
         *
         * @param mapper
         */
        void mapAndDecrementUsages(final Consumer<? super V> mapper) {
            mapper.accept(m_value);
            m_usages = m_usages - 1;
            if (m_usages == 0) {
                m_dispose.accept(m_value);
                m_value = null;
            }
        }

    }
}
