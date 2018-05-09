package com.nettex.apps.exts;

import com.nettex.apps.meta.TaskMeta;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * <p>Title: science</p>
 * <p>Description: ParallelGroup</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/27/2018
 */
public class ParallelGroup {

    private static final Logger logger = LogManager.getLogger(ParallelGroup.class);

    private ThreadPoolExecutor executor;
    private List<TaskMeta> tasks;
    private int numCores;

    public ParallelGroup(List<TaskMeta> tasks, int factor) {
        this.tasks = tasks;
        this.numCores = factor * (Runtime.getRuntime().availableProcessors());
        this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(tasks.size() / numCores > 1 ? numCores : 1);
    }

    public List<Future<List<String>>> classify(ProgressBar progressBar) throws InterruptedException {
        int length = tasks.size() / numCores;
        int startIndex = 0, endIndex = length, counter = numCores;
        if (length < 1) {
            counter = 1;
            endIndex = tasks.size();
        }

        List<Future<List<String>>> result = new ArrayList<>();
        CountDownLatch endController = new CountDownLatch(counter);
        DoubleBinding progress = null;
        for (int i = 0; i < counter; i++) {
            GroupTask task = new GroupTask(tasks, endController, startIndex, endIndex);
            DoubleBinding scaleProgress = task.progressProperty().divide(counter);
            if (progress == null) {
                progress = scaleProgress;
            } else {
                progress = progress.add(scaleProgress);
            }
            startIndex = endIndex;
            if (i < counter - 2) {
                endIndex = endIndex + length;
            } else {
                endIndex = tasks.size();
            }
            Future<List<String>> future = executor.submit((Callable<List<String>>) task);
            result.add(future);
        }
        DoubleBinding binding = progress;
        Platform.runLater(() -> progressBar.progressProperty().bind(binding));
        endController.await();
        return result;
    }

    public void destroy() {
        Optional.ofNullable(executor).ifPresent((service) -> {
            try {
                logger.info("Attempt to shutdown executor!");
                service.shutdown();
                service.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Tasks interrupted!");
            } finally {
                if (!service.isTerminated()) {
                    logger.info("Cancel non-finished tasks!");
                }
                service.shutdownNow();
                logger.info("Shutdown finished!");
            }
        });
    }
}
