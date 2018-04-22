package com.netex.apps.meta;

public class Option {
    private boolean isHeader;
    private boolean isBatch;

    public Option(boolean isHeader, boolean isBatch) {
        this.isHeader = isHeader;
        this.isBatch = isBatch;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public boolean isBatch() {
        return isBatch;
    }

    public void setBatch(boolean batch) {
        isBatch = batch;
    }
}
