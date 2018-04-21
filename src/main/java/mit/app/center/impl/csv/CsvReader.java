package mit.app.center.impl.csv;

import javafx.util.Pair;
import mit.app.center.intf.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: mit.app.center.impl.csv.CsvReader</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class CsvReader implements Reader {

    @Override
    public List<Pair<List<String>, List<List<Object>>>> read(String filePath, Boolean isFileWithHeader)
            throws IOException {
        List<Pair<List<String>, List<List<Object>>>> result;
        CSVFormat csvFormat = CSVFormat.DEFAULT;
        if (isFileWithHeader) csvFormat.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
        try (java.io.Reader reader = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, csvFormat)
        ) {
            result = new ArrayList<>();
            if (isFileWithHeader) {
                List<String> lstHeader = new ArrayList<>(csvParser.getHeaderMap().keySet());
                List<List<Object>> lstData = new ArrayList<>();
                csvParser.forEach((csvRecord) -> {
                    long recNum = csvRecord.getRecordNumber();
                    // Collect csv real data skip header portion
                    if (recNum != 0) {
                        List<Object> data = new ArrayList<>();
                        lstHeader.forEach((header) -> data.add(csvRecord.get(header)));
                        lstData.add(data);
                    }
                });
                Pair<List<String>, List<List<Object>>> csvSheet = new Pair<>(lstHeader, lstData);
                result.add(csvSheet);
            } else {
                List<List<Object>> lstData = new ArrayList<>();
                csvParser.forEach((csvRecord) -> {
                    List<Object> data = new ArrayList<>();
                    csvRecord.forEach(data::add);
                    lstData.add(data);
                });
                Pair<List<String>, List<List<Object>>> csvSheet = new Pair<>(null, lstData);
                result.add(csvSheet);
            }
        }
        return result;
    }
}
