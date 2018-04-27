package com.netex.apps.impl.csv;

import com.netex.apps.intf.Writer;
import javafx.util.Pair;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.impl.csv.CsvWriter</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class CsvWriter implements Writer {

    @Override
    public void write(Pair<List<String>, List<List<Object>>> dataInfo, String filePath) throws IOException {
        if (dataInfo != null) {
            Boolean isFileWithHeader = dataInfo.getKey() != null;
            CSVFormat csvFormat = CSVFormat.DEFAULT;
            if (isFileWithHeader) {
                String[] headers = (String[]) dataInfo.getKey().toArray();
                csvFormat = csvFormat.withHeader(headers);
            }
            final Path path = Paths.get(filePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            try (BufferedWriter writer = Files
                    .newBufferedWriter(path, Charset.forName("utf-8"),
                            StandardOpenOption.CREATE_NEW, StandardOpenOption.APPEND); CSVPrinter csvPrinter = new CSVPrinter(
                    writer, csvFormat)) {
                for (List<Object> csvRecord : dataInfo.getValue()) {
                    csvPrinter.printRecord(csvRecord);
                }
                csvPrinter.flush();
            }
        }

    }
}
