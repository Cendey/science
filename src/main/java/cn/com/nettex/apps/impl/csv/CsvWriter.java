package cn.com.nettex.apps.impl.csv;

import cn.com.nettex.apps.intf.Writer;
import cn.com.nettex.apps.util.Utilities;
import javafx.util.Pair;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: CsvWriter</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class CsvWriter implements Writer {

    private final static Logger logger = LogManager.getLogger(CsvWriter.class);

    @Override
    public void write(Pair<List<String>, List<List<Object>>> dataInfo, String filePath) throws IOException {
        if (dataInfo != null) {
            Boolean isFileWithHeader = dataInfo.getKey() != null;
            CSVFormat csvFormat = CSVFormat.DEFAULT;
            if (isFileWithHeader) {
                String[] headers = (String[]) dataInfo.getKey().toArray();
                csvFormat = csvFormat.withHeader(headers);
            }
            final Path parent = Paths.get(Utilities.liveParent(filePath));
            if (Files.isWritable(parent)) {
                final Path path = Paths.get(filePath);
                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.APPEND);
                     CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {
                    for (List<Object> csvRecord : dataInfo.getValue()) {
                        csvPrinter.printRecord(csvRecord);
                    }
                    csvPrinter.flush();
                }
            } else {
                logger.warn(
                        String.format("%s is not writable or not have authorize to write in this directory!", parent));
            }
        }
    }
}
