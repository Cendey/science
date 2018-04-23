package com.netex.apps.impl.xsl;


import com.netex.apps.intf.Factory;
import com.netex.apps.intf.Reader;
import com.netex.apps.intf.Writer;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.impl.xsl.ExcelFactory</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class ExcelFactory implements Factory {

    private static Reader excelReader;
    private static Writer excelWriter;

    @Override
    public Reader createReader() {
        if (excelReader == null) {
            excelReader = new ExcelReader();
        }
        return excelReader;
    }

    @Override
    public Writer createWriter() {
        if (excelWriter == null) {
            excelWriter = new ExcelWriter();
        }
        return excelWriter;
    }
}
