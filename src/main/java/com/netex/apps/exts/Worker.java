package com.netex.apps.exts;

import com.netex.apps.intf.Factory;
import com.netex.apps.intf.Reader;
import com.netex.apps.intf.Writer;
import com.netex.apps.meta.TaskMeta;
import com.netex.apps.util.Utilities;
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
 * <p>Description: com.netex.apps.exts.Worker</p>
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
    static List<String> perform(TaskMeta taskMeta) {
        List<String> result = new ArrayList<>();
        Boolean withHeader = taskMeta.getHeader();
        Factory readFactory = Facade.create(taskMeta.getSrcPath());
        Factory writeFactory = Facade.build(taskMeta.getType());
        Reader reader = readFactory.createReader();
        Writer writer = writeFactory.createWriter();
        try {
            List<Pair<List<String>, List<List<Object>>>> contents = reader.read(taskMeta.getSrcPath(), withHeader);
            Optional.ofNullable(contents).ifPresent(data -> data.forEach(file -> {
                        String destFileName = Utilities.rename(taskMeta.getSrcPath(), taskMeta.getNameTo());
                        final String destFilePath = taskMeta.getDestPath() + File.separator + destFileName + taskMeta.getType();
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
