package com.netex.apps.impl.txt;


import com.netex.apps.intf.Writer;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TextWriter implements Writer {

    private static final Logger logger = LogManager.getLogger(TextWriter.class);

    @Override
    public void write(Pair<List<String>, List<List<Object>>> dataInfo, String filePath) throws IOException {
        final Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        Optional.ofNullable(dataInfo).ifPresent(datInfo -> {
            try (final PrintWriter writer = new PrintWriter(
                Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW))) {
                Optional.ofNullable(dataInfo.getKey())
                    .ifPresent(header -> writer.println(header.stream().collect(Collectors.joining("\t"))));
                Optional.ofNullable(dataInfo.getValue()).ifPresent(data -> data.forEach(
                    row -> writer.println(row.stream().map(String::valueOf).collect(Collectors.joining("\t")))));
            } catch (IOException e) {
                logger.error(e.getCause().getMessage());
            }
        });
    }
}
