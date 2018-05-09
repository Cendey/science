package cn.com.nettex.apps.impl.csv;


import cn.com.nettex.apps.intf.Factory;
import cn.com.nettex.apps.intf.Reader;
import cn.com.nettex.apps.intf.Writer;

/**
 * <p>Title: science</p>
 * <p>Description: CsvFactory</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class CsvFactory implements Factory {

    private static Reader csvReader;
    private static Writer csvWriter;

    @Override
    public Reader createReader() {
        if (csvReader == null) {
            csvReader = new CsvReader();
        }
        return csvReader;
    }

    @Override
    public Writer createWriter() {
        if (csvWriter == null) {
            csvWriter = new CsvWriter();
        }
        return csvWriter;
    }
}
