package com.netex.apps.exts;

import com.netex.apps.meta.TaskMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

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

    public List<Future<List<String>>> classify() {
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
        return result;
    }

    public void destroy() {
        executor.shutdown();
    }
}
