package com.netex.apps.impl.txt;


import com.netex.apps.intf.Reader;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextReader implements Reader {

    @Override
    public List<Pair<List<String>, List<List<Object>>>> read(String filePath, Boolean isFileWithHeader) throws IOException {
        List<Pair<List<String>, List<List<Object>>>> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            Stream<String> lines = reader.lines();
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
            final String value = item.trim();
            if (StringUtils.isNotEmpty(value)) {
                if (NumberUtils.isParsable(value)) {
                    data.add(Double.parseDouble(value));
                } else {
                    data.add(item);
                }
            } else {
                data.add(item);
            }
        });
        return data;
    }
}
