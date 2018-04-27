package com.netex.apps.exts;

import com.netex.apps.meta.TaskMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

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
            result.add(future);
        }
        endController.await();
        return result;
    }

    public void destroy() {
        try {
            System.out.println("Attempt to shutdown executor!");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println("Tasks interrupted!");
        } finally {
            if (!executor.isTerminated()) {
                System.out.println("Cancel non-finished tasks!");
            }
            executor.shutdownNow();
            System.out.println("Shutdown finished!");
        }
    }
}
