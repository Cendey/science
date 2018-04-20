package mit.app.center.impl.csv;

import mit.app.center.intf.Factory;
import mit.app.center.intf.Reader;
import mit.app.center.intf.Writer;

/**
 * <p>Title: science</p>
 * <p>Description: mit.app.center.impl.csv.CsvFactory</p>
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
