package com.netex.apps.exts;


import com.netex.apps.impl.csv.CsvFactory;
import com.netex.apps.impl.txt.TextFactory;
import com.netex.apps.impl.xsl.ExcelFactory;
import com.netex.apps.intf.Factory;
import com.netex.apps.intf.Reader;
import com.netex.apps.intf.Writer;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.apache.tika.detect.TypeDetector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AbstractFactory {

    private static Tika tika = new Tika(new TypeDetector());
    private Reader _reader;
    private Writer _writer;

    public static Factory create(String filePath) {
        Factory factory = null;
        Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            File file = new File(path.toUri());
            if (file.isFile()) {
                try {
                    String fileType = tika.detect(file);
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
                            System.err.println(String.format("The file type: {%s} is not support now!", fileType));
                    }
                } catch (IOException e) {
                    System.out.println(e.getCause().getMessage());
                }
            }
        }
        return factory;
    }

    public static Factory build(String fileFormat) {
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
                    System.err.println(String.format("The file type: {%s} is not support now!", fileFormat));
            }
        }
        return factory;
    }
}
