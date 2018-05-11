package cn.com.nettex.apps.impl.csv;

import cn.com.nettex.apps.intf.Reader;
import javafx.util.Pair;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: CsvReader</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class CsvReader implements Reader {

    private final static Logger logger = LogManager.getLogger(CsvReader.class);

    @Override
    public List<Pair<List<String>, List<List<Object>>>> read(String filePath, Boolean isFileWithHeader)
            throws IOException {
        List<Pair<List<String>, List<List<Object>>>> result = null;
        CSVFormat csvFormat = CSVFormat.DEFAULT;
        if (isFileWithHeader) csvFormat.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
        final Path path = Paths.get(filePath);
        if (Files.exists(path) && Files.isReadable(path)) {
            try (java.io.Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
                 CSVParser csvParser = new CSVParser(reader, csvFormat)) {
                result = new ArrayList<>();
                if (isFileWithHeader) {
                    List<String> headers = new ArrayList<>();
                    List<List<Object>> records = new ArrayList<>();
                    csvParser.forEach(csvRecord -> {
                        long recNum = csvRecord.getRecordNumber();
                        // Collect csv real data skip header portion
                        if (recNum != 0) {
                            List<Object> data = new ArrayList<>();
                            headers.forEach((header) -> data.add(csvRecord.get(header)));
                            records.add(data);
                        } else {
                            csvRecord.forEach(headers::add);
                        }
                    });
                    Pair<List<String>, List<List<Object>>> csvSheet = new Pair<>(headers, records);
                    result.add(csvSheet);
                } else {
                    List<List<Object>> records = new ArrayList<>();
                    csvParser.forEach(csvRecord -> {
                        List<Object> data = new ArrayList<>();
                        csvRecord.forEach(data::add);
                        records.add(data);
                    });
                    Pair<List<String>, List<List<Object>>> csvSheet = new Pair<>(null, records);
                    result.add(csvSheet);
                }
            }
        } else {
            logger.warn(String.format("%s is not exist or not readable!", filePath));
        }
        return result;
    }
}
