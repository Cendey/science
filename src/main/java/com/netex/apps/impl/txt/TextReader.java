package com.netex.apps.impl.txt;


import com.netex.apps.intf.Reader;
import com.netex.apps.meta.Formats;
import javafx.util.Pair;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextReader implements Reader {

    @Override
    public List<Pair<List<String>, List<List<Object>>>> read(String filePath, Boolean isFileWithHeader) throws IOException {
        List<Pair<List<String>, List<List<Object>>>> result = new ArrayList<>();
        try (BufferedReader crunchBufferReader = Files.newBufferedReader(Paths.get(filePath))) {
            Stream<String> lines = crunchBufferReader.lines();
            List<List<Object>> contents = new ArrayList<>();
            if (isFileWithHeader) {
                int rowNum = 0;
                List<String> header = new ArrayList<>();
                for (String line : lines.collect(Collectors.toList())) {
                    if (rowNum++ == 0) {
                        header.addAll(Arrays.asList(line.split("\t")));
                    } else {
                        contents.add(parse(line));
                    }
                }
                result.add(new Pair<>(header, contents));
            } else {
                lines.forEach(line -> contents.add(parse(line)));
                result.add(new Pair<>(null, contents));
            }
        }
        return result;
    }

    private List<Object> parse(String line) {
        final List<Object> data = new ArrayList<>();
        Arrays.stream(line.split("\t")).forEach(item -> {
            if (NumberUtils.isParsable(item.trim())) {
                data.add(Double.parseDouble(item.trim()));
            } else if (Formats.isParsable(item.trim())) {
                try {
                    data.add(DateUtils.parseDate(item.trim(), Formats.DATE_PATTERNS));
                } catch (ParseException e) {
                    System.out.println(e.getCause().getMessage());
                    data.add(null);
                }
            } else {
                data.add(item);
            }
        });
        return data;
    }
}
