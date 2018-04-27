package com.netex.apps.util;

import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

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

    private static final String FILENAME_PATTERN = "\\.\\d+$";
    private static final Pattern PATTERN = Pattern.compile(FILENAME_PATTERN);

    public static <E> void adjustSize(E node, String propertyName, double delta) {
        Class<?> clazz = node.getClass();
        try {
            Method reader = clazz.getMethod("get" + propertyName);
            Method writer = clazz.getMethod("setPref" + propertyName, double.class);
            writer.invoke(node, Double.class.cast(reader.invoke(node)) + delta);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            System.err.println(e.getCause().getMessage());
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
                    String srcPath = source.getPath();
                    if (StringUtils.isNotEmpty(srcPath)) {
                        String[] parents = StringUtils.split(srcPath, File.separator);
                        StringBuilder builder = new StringBuilder(directory);
                        for (int i = parents.length - level; i < parents.length; i++) {
                            builder.append(File.separator).append(parents[i]);
                        }
                        path = builder.toString();

                    } else {
                        System.err.println("Source file path is empty!");
                    }
                }
            } else {
                path = source.getPath();
                System.out.println("The directory to save file is same as source file directory!");
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
            System.err.println(String.format("%s is not exists, please double check!", directory.getName()));
        }
        return listFiles;
    }

    public static List<Pair<File, Integer>> listAll(File directory, String fileName, Integer level) {
        final List<Pair<File, Integer>> lstFiles = new ArrayList<>();
        if (directory.exists()) {
            if (directory.isDirectory()) {
                File[] files =
                    directory.listFiles((file, pattern) -> file.isDirectory() || file.getName().contains(fileName));
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            level++;
                            List<Pair<File, Integer>> temp = listAll(file, fileName, level);
                            Optional.of(temp).ifPresent(lstFiles::addAll);
                        } else {
                            lstFiles.add(new Pair<>(file, level));
                        }
                    }
                } else {
                    System.err.println(String.format("No file found in %s!", directory.getName()));
                }
            } else if (directory.isFile()) {
                lstFiles.add(new Pair<>(directory, level));
            }
        } else {
            System.err.println(String.format("%s is not exists, please double check!", directory.getName()));
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
}
