package com.netex.apps.meta;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

public class Workload extends Task<String> {
    private final static Logger logger = LogManager.getLogger(Workload.class);

    private int total;
    private int current;
    private AtomicBoolean shouldThrow = new AtomicBoolean(false);

    public Workload() {
        this.current = 0;
        this.total = 1;
    }

    public synchronized void increase() {
        this.current++;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    protected String call() {
//        http://java-buddy.blogspot.com.br/2014/08/bind-javafx-progressbarprogressproperty.html
        if (isCancelled()) {
            updateValue(String.format("Canceled at %d", System.currentTimeMillis()));
            return null; //ignored
        }
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            updateValue(String.format("Canceled at %d", System.currentTimeMillis()));
            logger.error(e.getCause().getMessage());
            return null; //ignored
        }
        if (shouldThrow.get()) {
            throw new RuntimeException(String.format("Exception throw at %d", System.currentTimeMillis()));
        }
        updateProgress(current, total);
        return null; //ignored
    }
}
