package cn.com.nettex.apps.exts;

import cn.com.nettex.apps.intf.Reader;
import cn.com.nettex.apps.intf.Writer;
import cn.com.nettex.apps.meta.TaskMeta;
import cn.com.nettex.apps.util.Utilities;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>Title: science</p>
 * <p>Description: Worker</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/27/2018
 */
class Worker {

    private static final Logger logger = LogManager.getLogger(Worker.class);

    //For single file conversion
    static List<String> perform(final TaskMeta taskMeta) {
        List<String> result = new ArrayList<>();
        Reader reader = Facade.create(taskMeta.getSrcPath()).createReader();
        Writer writer = Facade.build(taskMeta.getType()).createWriter();
        try {
            List<Pair<List<String>, List<List<Object>>>> contents = reader.read(
                taskMeta.getSrcPath(),
                taskMeta.getHeader());
            Optional.ofNullable(contents).ifPresent(data -> data.forEach(file -> {
                    String destFileName = Utilities.rename(taskMeta.getSrcPath(), taskMeta.getNameTo());
                    final String destFilePath =
                        String.format("%s%s%s%s", taskMeta.getDestPath(), File.separator, destFileName, taskMeta.getType());
                    try {
                        writer.write(file, destFilePath);
                    } catch (IOException e) {
                        logger.error(e.getCause().getMessage());
                    }
                    result.add(String.format("%s%n", destFilePath));
                }
            ));
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }
        return result;
    }
}
