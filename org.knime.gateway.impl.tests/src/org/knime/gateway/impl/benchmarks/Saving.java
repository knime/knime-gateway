package org.knime.gateway.impl.benchmarks;

import static org.knime.gateway.api.util.CoreUtil.iterateNodes;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.workflow.WorkflowManager;
import org.knime.core.util.LockFailedException;
import org.knime.testing.util.WorkflowManagerUtil;

public class Saving {

    private static List<Path> listChildDirectories(Path parent)  {
        try (var stream = Files.list(parent)) {
            return stream
                    .filter(Files::isDirectory)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void foo() throws IOException, LockFailedException, CanceledExecutionException {

        // /home/ben/git-repositories/workshop-intellij-setup/out/partial-runtime/saving/

//        WorkflowManagerUtil.loadWorkflow()
        var results = new ArrayList<Repeat>();
        var wfs = listChildDirectories(new File("/home/ben/git-repositories/workshop-intellij-setup/out/partial-runtime/saving/").toPath());

        var wfms = new ArrayList<WorkflowManager>();
        for (var path : wfs) {
            try {
                var wfm = WorkflowManagerUtil.loadWorkflow(path.toFile());
                wfms.add(wfm);
            } catch (Throwable e) {
                break;
            }
        }

        var repeats = 3;
        for (var repeatIdx = 0; repeatIdx < repeats; repeatIdx++) {
            for (var wfm : wfms) {
                var tmpDir = Files.createTempDirectory("saving");
                var mon = new ExecutionMonitor();

                var stopwatch = new Stopwatch();
                try {
                    wfm.save(tmpDir.toFile(), mon, true);
                } catch (Throwable e) {
                    break;
                }
                var elapsed = stopwatch.getElapsed();

                var sizeOnDisk = directorySize(tmpDir);
                var nodeCount = countNodes(wfm);
                results.add(new Repeat(repeatIdx, wfm.getName(), elapsed, nodeCount, sizeOnDisk));

            }
        }
        writeRepeatsToCsv(results, new File("/home/ben/git-repositories/workshop-intellij-setup/out/partial-runtime/saving-results/results-without-svg.csv").toPath());

    }

    int countNodes(final WorkflowManager wfm) throws IOException {
        var count = new AtomicInteger();
        iterateNodes(wfm, nc -> {
            count.getAndIncrement();
            return true;
        });
        return count.get();
    }

    long directorySize(Path root) throws IOException {
        try (var stream = Files.walk(root)) {
            return stream
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try { return Files.size(p); }
                        catch (IOException e) { throw new UncheckedIOException(e); }
                    })
                    .sum();
        }
    }

    static void writeRepeatsToCsv(Collection<Repeat> repeats, Path target) throws IOException {
        try (var writer = Files.newBufferedWriter(target)) {
            writer.write("repeat,name,time,topLevelNodes,sizeOnDisk");
            writer.newLine();
            for (var r : repeats) {
                writer
                        .append(Integer.toString(r.repeat()))
                        .append(',')
                        .append(escape(r.name()))
                        .append(',')
                        .append(Long.toString(r.time()))
                        .append(',')
                        .append(Integer.toString(r.nodeCount()))
                        .append(',')
                        .append(Long.toString(r.sizeOnDisk()));
                writer.newLine();
            }
        }
    }

    private static String escape(String value) {
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }

    private record Repeat(int repeat, String name, long time, int nodeCount, long sizeOnDisk) {}

    private static void printWfmInfo(final WorkflowManager wfm) {
        System.out.println("Name: " + wfm.getName());
        System.out.println("NodeContainers: " + wfm.getNodeContainers().size());
        System.out.println("ConnectionContainers: " + wfm.getConnectionContainers().size());
        // also interesting:
        // - nested nodes
        // - size on disk
    }

    private static long getTime() {
        return System.nanoTime();
    }

    static class Stopwatch  {
        private final long startTime;

        public Stopwatch() {
            this.startTime = System.nanoTime();
        }

        public long getElapsed() {
            return System.nanoTime() - this.startTime;
        }
    }


}
