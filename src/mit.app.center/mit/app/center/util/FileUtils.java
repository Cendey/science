package mit.app.center.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Title: science</p>
 * <p>Description: mit.app.center.util.FileUtils</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/19/2018
 */
public class FileUtils {

    // Read file using lines() and Stream Approach
    private static Stream<String> crunchReadFile(String crunchFile) {

        Stream<String> crunchStream = null;
        try {

            // Read all lines from a file as a Stream. Bytes from the file are decoded into characters using the UTF-8 charset
            crunchStream = Files.lines(Paths.get(crunchFile));

        } catch (IOException e) {
            e.printStackTrace();
        }

        log("============= Result from lines() and Stream Approach =============");
        if (crunchStream != null) {
            crunchStream.forEach(System.out::println);
        }
        return crunchStream;
    }

    // Read file using newBufferedReader and Stream Approach
    private static void crunchBufferReadFile(String crunchFile) {
        BufferedReader crunchBufferReader = null;
        try {

            // newBufferedReader opens a file for reading
            crunchBufferReader = Files.newBufferedReader(Paths.get(crunchFile));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // toList: returns a Collector that accumulates the input elements into a new List
        // lines(): returns a Stream, the elements of which are lines read from this BufferedReader
        List<String> lstCrunchFiles =
            crunchBufferReader != null ? crunchBufferReader.lines().collect(Collectors.toList())
                : Collections.emptyList();

        log("\n============= Result from newBufferedReader and Stream Approach =============");

        // forEach: performs the given action for each element of the Iterable until all elements have been processed or the
        // action throws an exception.
        lstCrunchFiles.forEach(System.out::println);

    }

    private static void log(String string) {
        System.out.println(string);

    }

//    http://javasampleapproach.com/java/java-read-write-csv-file-opencsv-example
//    https://mydevgeek.com/csv-file-reading-writing-java-using-google-jcsv/
//    https://crunchify.com/how-to-read-a-file-line-by-line-using-java-8-stream-files-lines-and-files-newbufferedreader-utils/
//    https://crunchify.com/how-to-read-a-file-line-by-line-using-java-8-stream-files-lines-and-files-newbufferedreader-utils/
//    https://www.mkyong.com/java8/java-8-stream-read-a-file-line-by-line/
//    https://alvinalexander.com/blog/post/java/how-open-read-file-java-string-array-list
    static void writeToCsv(Stream<String> stream, String directory, String fileName) {
        String fileSeparator = File.separator;
        String csvFile = directory.concat(fileSeparator).concat(fileName);
        try {
            File file = new File(csvFile);
            if (!file.exists()) {
                boolean success = file.createNewFile();
                if (!success) {
                    log("Create new file failed!");
                } else {
                    final Path path = Paths.get(csvFile);
                    final Path csv = path.resolve(csvFile);
                    try (final PrintWriter pw = new PrintWriter(Files.newBufferedWriter(csv, StandardOpenOption.CREATE_NEW))) {
                        stream.map(line -> line.split("\\s+")).map(line -> Stream.of(line)
                            .collect(Collectors.joining(",")))
                            .forEach(pw::println);
                    } catch (IOException e) {
                        log(e.getCause().getMessage());
                    }
                }
            } else {
                log("File exists!");
            }
        } catch (IOException e) {
            log(e.getCause().getMessage());
        }

    }
}
