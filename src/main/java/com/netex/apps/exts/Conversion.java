package com.netex.apps.exts;

import com.netex.apps.intf.Factory;
import com.netex.apps.intf.Reader;
import com.netex.apps.intf.Writer;
import com.netex.apps.util.Utilities;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.exts.Conversion</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/27/2018
 */
class Conversion {

    //For single file conversion
    static List<String> convert(String srcPath, String destPath, String nameTo, String type, Boolean withHeader) {
        List<String> result = new ArrayList<>();
        Factory readFactory = AbstractFactory.create(srcPath);
        Factory writeFactory = AbstractFactory.build(type);
        Reader reader = readFactory.createReader();
        Writer writer = writeFactory.createWriter();
        try {
            List<Pair<List<String>, List<List<Object>>>> contents = reader.read(srcPath, withHeader);
            if (contents != null && contents.size() > 0) {
                contents.forEach(file -> {
                        String destFileName = Utilities.rename(srcPath, nameTo);
                        try {
                            writer.write(file, destPath + File.separator + destFileName + type);
                        } catch (IOException e) {
                            System.err.println(e.getCause().getMessage());
                        }
                        result.add(destFileName);
                    }
                );
            }
        } catch (IOException e) {
            System.err.println(e.getCause().getMessage());
        }
        return result;
    }

    //For batch file(s) conversion
    static List<String> convert(
        String srcPath, String nameAs, String destPath, String nameTo, String type, Boolean withHeader) {
        File directory = new File(srcPath);
        List<Pair<File, Integer>> lstFiles = Utilities.listAll(directory, nameAs, 0);
        List<String> result = new ArrayList<>();
        Optional.of(lstFiles).ifPresent(files -> files.forEach(file -> {
            String path = Utilities.compose(file, destPath);
            String destFileName = Utilities.rename(srcPath, nameTo);
            String destFilePath = path + File.separator + destFileName + type;
            Factory readFactory = AbstractFactory.create(srcPath);
            Factory writeFactory = AbstractFactory.build(type);
            Reader reader = readFactory.createReader();
            Writer writer = writeFactory.createWriter();
            try {
                List<Pair<List<String>, List<List<Object>>>> contents =
                    reader.read(file.getKey().getPath(), withHeader);
                if (contents != null && contents.size() > 0) {
                    contents.forEach(data -> {
                            try {
                                writer.write(data, destFilePath);
                            } catch (IOException e) {
                                System.err.println(e.getCause().getMessage());
                            }
                            result.add(destFilePath);
                        }
                    );
                }
            } catch (IOException e) {
                System.err.println(e.getCause().getMessage());
            }
        }));
        return result;
    }
}
