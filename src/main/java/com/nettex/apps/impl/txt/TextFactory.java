package com.nettex.apps.impl.txt;


import com.nettex.apps.intf.Factory;
import com.nettex.apps.intf.Reader;
import com.nettex.apps.intf.Writer;

public class TextFactory implements Factory {

    private static Reader textReader;
    private static Writer textWriter;

    @Override
    public Reader createReader() {
        if (textReader == null) {
            textReader = new TextReader();
        }
        return textReader;
    }

    @Override
    public Writer createWriter() {
        if (textWriter == null) {
            textWriter = new TextWriter();
        }
        return textWriter;
    }
}
