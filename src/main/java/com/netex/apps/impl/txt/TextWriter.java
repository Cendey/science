package com.netex.apps.impl.txt;


import com.netex.apps.intf.Writer;
import javafx.util.Pair;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class TextWriter implements Writer {
    @Override
    public void write(Pair<List<String>, List<List<Object>>> dataInfo, String filePath) throws IOException {
        try (final PrintWriter writer = new PrintWriter(Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW))) {
            List<String> header = dataInfo.getKey();
            if (header != null) {
                writer.println(header.stream().collect(Collectors.joining("\t")));
            }
            List<List<Object>> data = dataInfo.getValue();
            data.forEach(row -> writer.println(row.stream().map(String::valueOf).collect(Collectors.joining("\t"))));
        }
    }
}
