package com.netex.apps.impl.txt;


import com.netex.apps.intf.Factory;
import com.netex.apps.intf.Reader;
import com.netex.apps.intf.Writer;

public class TextFactory implements Factory {

    @Override
    public Reader createReader() {
        return new TextReader();
    }

    @Override
    public Writer createWriter() {
        return new TextWriter();
    }
}
