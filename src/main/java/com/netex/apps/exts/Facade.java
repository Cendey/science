package com.netex.apps.exts;


import com.netex.apps.impl.csv.CsvFactory;
import com.netex.apps.impl.txt.TextFactory;
import com.netex.apps.impl.xsl.ExcelFactory;
import com.netex.apps.intf.Factory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.detect.TypeDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Facade {

    private static final Logger logger = LogManager.getLogger(Facade.class);

    private static final Tika parser = new Tika(new TypeDetector());

    static Factory create(String filePath) {
        Factory factory = null;
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            File file = new File(path.toUri());
            if (file.isFile()) {
                String fileType = null;
                try {
                    fileType = parser.detect(file);
                } catch (IOException e) {
                    logger.error(e.getCause().getMessage());
                }
                if (StringUtils.isNotEmpty(fileType)) {
                    switch (fileType) {
                        case "text/plain":
                            factory = new TextFactory();
                            break;
                        case "text/csv":
                            factory = new CsvFactory();
                            break;
                        case "application/vnd.ms-excel":
                        case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                            factory = new ExcelFactory();
                            break;
                        default:
                            factory = new TextFactory();
                            logger.warn(String
                                .format("The file type: {%s} is treated as plain text file to process!", fileType));
                    }
                }
            }
        }
        return factory;
    }

    static Factory build(String fileFormat) {
        Factory factory = null;
        if (StringUtils.isNotEmpty(fileFormat)) {
            fileFormat = fileFormat.trim();
            switch (fileFormat) {
                case ".text":
                case ".txt":
                    factory = new TextFactory();
                    break;
                case ".csv":
                    factory = new CsvFactory();
                    break;
                case ".xls":
                case ".xlsx":
                    factory = new ExcelFactory();
                    break;
                default:
                    logger.error(String.format("The file type: {%s} is not support now!", fileFormat));
            }
        }
        return factory;
    }
}
