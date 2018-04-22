package com.netex.apps.intf;

import javafx.util.Pair;

import java.io.IOException;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: mit.app.center.intf.Reader</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public interface Reader {

    List<Pair<List<String>, List<List<Object>>>> read(String filePath, Boolean isFileWithHeader) throws IOException;
}
