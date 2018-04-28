package com.netex.apps.util;

import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.util.Utilities</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/19/2018
 */
public class Utilities {

    private static final Logger logger = LogManager.getLogger(Utilities.class);

    private static final String FILENAME_PATTERN = "\\.\\d+$";
    private static final Pattern PATTERN = Pattern.compile(FILENAME_PATTERN);

    public static <E> void adjustSize(E node, String propertyName, double delta) {
        Class<?> clazz = node.getClass();
        try {
            Method reader = clazz.getMethod("get" + propertyName);
            Method writer = clazz.getMethod("setPref" + propertyName, double.class);
            writer.invoke(node, Double.class.cast(reader.invoke(node)) + delta);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logger.error(e.getCause().getMessage());
        }
    }

    public static String compose(Pair<File, Integer> sourceFile, String directory) {
        String path = null;
        if (sourceFile != null) {
            File source = sourceFile.getKey();
            Integer level = sourceFile.getValue();
            if (StringUtils.isNotEmpty(directory)) {
                if (level == 0) {
                    path = directory;
                } else {
                    String srcPath = source.getParent();
                    if (StringUtils.isNotEmpty(srcPath)) {
                        String[] parents = StringUtils.split(srcPath, File.separator);
                        StringBuilder builder = new StringBuilder(directory);
                        for (int i = parents.length - level; i < parents.length; i++) {
                            builder.append(File.separator).append(parents[i]);
                        }
                        path = builder.toString();
                    } else {
                        logger.warn("Source file path is empty!");
                    }
                }
            } else {
                path = source.getPath();
                logger.warn("The directory to save file is same as source file directory!");
            }
        }
        return path;
    }

    public static List<Pair<File, Integer>> list(File directory, String fileName) {
        final List<Pair<File, Integer>> listFiles = new ArrayList<>();
        if (directory.exists()) {
            if (directory.isDirectory()) {
                Optional.ofNullable(directory.listFiles((file, pattern) -> file.getName().contains(fileName)))
                    .ifPresent(matchedFiles -> Arrays.stream(matchedFiles).map(file -> new Pair<>(file, 1))
                        .forEachOrdered(listFiles::add));
            } else if (directory.isFile()) {
                listFiles.add(new Pair<>(directory, 0));
            }
        } else {
            logger.error(String.format("%s is not exists, please double check!", directory.getName()));
        }
        return listFiles;
    }

    public static List<Pair<File, Integer>> listAll(File directory, String fileName, int level) {
        final List<Pair<File, Integer>> lstFiles = new ArrayList<>();
        if (directory.exists()) {
            if (directory.isDirectory()) {
                File[] files = directory.listFiles((file, pattern) -> {
                    File temp = new File(file.getPath() + File.separator + pattern);
                    return temp.isDirectory() || (temp.isFile() && pattern.contains(fileName));
                });
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            List<Pair<File, Integer>> temp = listAll(file, fileName, level + 1);
                            Optional.of(temp).ifPresent(lstFiles::addAll);
                        } else {
                            lstFiles.add(new Pair<>(file, level));
                        }
                    }
                } else {
                    logger.error(String
                        .format("No file found in directory %s, which name like %s!", directory.getName(), fileName));
                }
            } else if (directory.isFile()) {
                lstFiles.add(new Pair<>(directory, level));
            }
        } else {
            logger.error(String.format("%s is not exists, please double check!", directory.getName()));
        }
        return lstFiles;
    }

    public static String rename(String srcPath, String prefix) {
        String fileName = null, suffix = null;
        if (StringUtils.isNotEmpty(srcPath)) {
            srcPath = srcPath.trim();
            Matcher matcher = PATTERN.matcher(srcPath);
            if (matcher.matches()) {
                suffix = matcher.group();
            }
            if (StringUtils.isNotEmpty(prefix)) {
                if (StringUtils.isNotEmpty(suffix)) {
                    fileName = prefix.concat(suffix);
                } else {
                    fileName = prefix;
                }
            } else {
                fileName = FilenameUtils.getName(srcPath);
            }
        }
        return fileName;
    }

    public static boolean isValidName(String text) {
        Pattern pattern = Pattern.compile(
            "# Match a valid Windows filename (unspecified file system).          \n" +
                "^                                # Anchor to start of string.        \n" +
                "(?!                              # Assert filename is not: CON, PRN, \n" +
                "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n" +
                "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n" +
                "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n" +
                "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n" +
                "  (?:\\.[^.]*)?                  # followed by optional extension    \n" +
                "  $                              # and end of string                 \n" +
                ")                                # End negative lookahead assertion. \n" +
                "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n" +
                "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n" +
                "$                                # Anchor to end of string.            ",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}
