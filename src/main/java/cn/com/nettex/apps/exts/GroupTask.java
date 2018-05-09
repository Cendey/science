package cn.com.nettex.apps.exts;

import cn.com.nettex.apps.meta.TaskMeta;
import com.google.common.base.Stopwatch;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * <p>Title: science</p>
 * <p>Description: GroupTask</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/27/2018
 */
public class GroupTask extends Task<List<String>> implements Callable<List<String>> {

    private static final Logger logger = LogManager.getLogger(GroupTask.class);

    private int startIndex, endIndex;
    private List<TaskMeta> tasks;
    private CountDownLatch endController;

    GroupTask(List<TaskMeta> tasks, CountDownLatch endController, int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.tasks = tasks;
        this.endController = endController;
    }

    @Override
    public List<String> call() {
        logger.info("File conversion is scheduled now!");
        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> result = new ArrayList<>();
        for (int index = startIndex; index < endIndex; index++) {
            Optional.ofNullable(Worker.perform(tasks.get(index))).ifPresent(result::addAll);
            updateProgress(index - startIndex + 1, endIndex - startIndex);
        }
        endController.countDown();
        stopwatch.stop();
        Duration duration = stopwatch.elapsed();
        logger.error(String.format("Time consumed: %d minutes", duration.toMinutes()));
        return result;
    }
}
