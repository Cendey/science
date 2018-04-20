package mit.app.center.impl.csv;

import javafx.util.Pair;
import mit.app.center.intf.Writer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: mit.app.center.impl.csv.CsvWriter</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class CsvWriter implements Writer {

    @Override
    public void write(Pair<List<String>, List<List<String>>> dataInfo, String filePath) throws IOException {
        if (dataInfo != null) {
            Boolean isFileWithHeader = dataInfo.getKey() != null;
            CSVFormat csvFormat = CSVFormat.DEFAULT;
            if (isFileWithHeader) {
                String[] headers = (String[]) dataInfo.getKey().toArray();
                csvFormat = csvFormat.withHeader(headers);
            }
            try (BufferedWriter writer = Files
                .newBufferedWriter(Paths.get(filePath), Charset.forName("utf-8"),
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.APPEND); CSVPrinter csvPrinter = new CSVPrinter(
                writer, csvFormat)) {
                for (List<String> csvRecord : dataInfo.getValue()) {
                    csvPrinter.printRecord(csvRecord);
                }
                csvPrinter.flush();
            }
        }
    }
}
