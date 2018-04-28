package com.netex.apps.exts;

import com.netex.apps.meta.TaskMeta;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.exts.GroupTask</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/27/2018
 */
public class GroupTask implements Callable<List<String>> {

    private static final Logger logger = LogManager.getLogger(GroupTask.class);

    private int startIndex, endIndex;
    private List<TaskMeta> tasks;
    private CountDownLatch endController;

    GroupTask(int startIndex, int endIndex, List<TaskMeta> tasks, CountDownLatch endController) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.tasks = tasks;
        this.endController = endController;
    }

    @Override
    public List<String> call() {
        logger.info("File conversion is scheduled now!");
        List<String> result = new ArrayList<>();
        for (int index = startIndex; index < endIndex; index++) {
            TaskMeta meta = tasks.get(index);
            List<String> temp = Worker
                .perform(meta.getSrcPath(), meta.getDestPath(), meta.getNameTo(), meta.getType(), meta.getHeader());
            Optional.ofNullable(temp).ifPresent(result::addAll);
        }
        endController.countDown();
        return result;
    }
}
