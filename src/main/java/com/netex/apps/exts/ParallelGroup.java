package com.netex.apps.exts;

import com.netex.apps.meta.TaskMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.exts.ParallelGroup</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/27/2018
 */
public class ParallelGroup {

    private ThreadPoolExecutor executor;
    private List<TaskMeta> tasks;
    private int numThreads;

    public ParallelGroup(List<TaskMeta> tasks, int factor) {
        this.tasks = tasks;
        this.numThreads = factor * (Runtime.getRuntime().availableProcessors());
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads, Thread::new);
    }

    public List<Future<List<String>>> classify() throws InterruptedException {
        int length = tasks.size() / numThreads;
        int startIndex = 0, endIndex = length;

        List<Future<List<String>>> result = new ArrayList<>();
        CountDownLatch endController = new CountDownLatch(numThreads);
        for (int i = 0; i < numThreads; i++) {
            GroupTask task = new GroupTask(startIndex, endIndex, tasks, endController);
            startIndex = endIndex;
            if (i < numThreads - 2) {
                endIndex = endIndex + length;
            } else {
                endIndex = tasks.size();
            }
            Future<List<String>> future = executor.submit(task);
            if (future.isDone()) {
                result.add(future);
            }
        }
        endController.await();
        return result;
    }

    public void destroy() {
        Optional.ofNullable(executor).ifPresent((service) -> {
            try {
                System.out.println("Attempt to shutdown executor!");
                service.shutdown();
                service.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Tasks interrupted!");
            } finally {
                if (!service.isTerminated()) {
                    System.out.println("Cancel non-finished tasks!");
                }
                service.shutdownNow();
                System.out.println("Shutdown finished!");
            }
        });
    }
}
