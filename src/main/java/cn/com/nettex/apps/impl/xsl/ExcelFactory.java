package cn.com.nettex.apps.impl.xsl;


import cn.com.nettex.apps.intf.Factory;
import cn.com.nettex.apps.intf.Reader;
import cn.com.nettex.apps.intf.Writer;

/**
 * <p>Title: science</p>
 * <p>Description: ExcelFactory</p>
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
