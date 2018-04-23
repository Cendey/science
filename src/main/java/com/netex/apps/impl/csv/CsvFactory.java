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

    @Override
    public Reader createReader() {
        return new CsvReader();
    }

    @Override
    public Writer createWriter() {
        return new CsvWriter();
    }
}
