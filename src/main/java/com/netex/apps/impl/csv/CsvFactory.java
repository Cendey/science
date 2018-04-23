package com.netex.apps.impl.csv;


import com.netex.apps.intf.Factory;
import com.netex.apps.intf.Reader;
import com.netex.apps.intf.Writer;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.impl.csv.CsvFactory</p>
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
