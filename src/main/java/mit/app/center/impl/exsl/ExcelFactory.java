package mit.app.center.impl.exsl;

import mit.app.center.intf.Reader;
import mit.app.center.intf.Factory;
import mit.app.center.intf.Writer;

/**
 * <p>Title: science</p>
 * <p>Description: mit.app.center.impl.exsl.ExcelFactory</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/20/2018
 */
public class ExcelFactory implements Factory {

    @Override
    public Reader createReader() {
        return new ExcelReader();
    }

    @Override
    public Writer createWriter() {
        return new ExcelWriter();
    }
}
