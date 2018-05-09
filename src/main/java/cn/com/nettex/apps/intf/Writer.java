package cn.com.nettex.apps.intf;

import javafx.util.Pair;

import java.io.IOException;
import java.util.List;

/**
 * <p>Title: science</p>
 * <p>Description: Writer</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public interface Writer {

    void write(Pair<List<String>, List<List<Object>>> dataInfo, String filePath) throws IOException;
}
