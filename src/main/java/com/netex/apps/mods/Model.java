package com.netex.apps.mods;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.mods.Model</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/23/2018
 */
public class Model {

    private static final String BLANK = "";

    private StringProperty srcPath;
    private StringProperty srcNamedAs;
    private StringProperty srcFuzzyName;
    private StringProperty srcFormat;
    private StringProperty destPath;
    private StringProperty destNamedTo;
    private ObservableList<String> destFormat;
    private BooleanProperty isForBatch;
    private BooleanProperty isWithHeader;
    private StringProperty logInfo;

    public Model() {
//        this("", "", "", "", "", "", "", false, false);
        this.srcPath = new SimpleStringProperty(BLANK);
        this.srcNamedAs = new SimpleStringProperty(BLANK);
        this.srcFuzzyName = new SimpleStringProperty(BLANK);
        this.srcFormat = new SimpleStringProperty(BLANK);
        this.destPath = new SimpleStringProperty(BLANK);
        this.destNamedTo = new SimpleStringProperty(BLANK);
        this.destFormat = FXCollections.observableArrayList(".txt", ".csv", ".xls", ".xlsx");
        this.logInfo = new SimpleStringProperty(BLANK);
        this.isForBatch = new SimpleBooleanProperty(false);
        this.isWithHeader = new SimpleBooleanProperty(false);
    }

    public Model(
        String srcPath, String srcNamedAs,
        String srcFuzzyName, String srcFormat,
        String destPath, String destNamedTo,
        String[] destFormat, String logInfo, Boolean isForBatch, Boolean isWithHeader) {
        this.srcPath = new SimpleStringProperty(srcPath);
        this.srcNamedAs = new SimpleStringProperty(srcNamedAs);
        this.srcFuzzyName = new SimpleStringProperty(srcFuzzyName);
        this.srcFormat = new SimpleStringProperty(srcFormat);
        this.destPath = new SimpleStringProperty(destPath);
        this.destNamedTo = new SimpleStringProperty(destNamedTo);
        this.destFormat = FXCollections.observableArrayList(destFormat);
        this.logInfo = new SimpleStringProperty(logInfo);
        this.isForBatch = new SimpleBooleanProperty(isForBatch);
        this.isWithHeader = new SimpleBooleanProperty(isWithHeader);
    }

    public String getSrcPath() {
        return srcPath.get();
    }

    public StringProperty srcPathProperty() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath.set(srcPath);
    }

    public String getSrcNamedAs() {
        return srcNamedAs.get();
    }

    public StringProperty srcNamedAsProperty() {
        return srcNamedAs;
    }

    public void setSrcNamedAs(String srcNamedAs) {
        this.srcNamedAs.set(srcNamedAs);
    }

    public String getSrcFuzzyName() {
        return srcFuzzyName.get();
    }

    public StringProperty srcFuzzyNameProperty() {
        return srcFuzzyName;
    }

    public void setSrcFuzzyName(String srcFuzzyName) {
        this.srcFuzzyName.set(srcFuzzyName);
    }

    public String getSrcFormat() {
        return srcFormat.get();
    }

    public StringProperty srcFormatProperty() {
        return srcFormat;
    }

    public void setSrcFormat(String srcFormat) {
        this.srcFormat.set(srcFormat);
    }

    public String getDestPath() {
        return destPath.get();
    }

    public StringProperty destPathProperty() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath.set(destPath);
    }

    public String getDestNamedTo() {
        return destNamedTo.get();
    }

    public StringProperty destNamedToProperty() {
        return destNamedTo;
    }

    public void setDestNamedTo(String destNamedTo) {
        this.destNamedTo.set(destNamedTo);
    }

    public ObservableList<String> getDestFormat() {
        return destFormat;
    }

    public void setDestFormat(ObservableList<String> destFormat) {
        this.destFormat = destFormat;
    }

    public String getLogInfo() {
        return logInfo.get();
    }

    public StringProperty logInfoProperty() {
        return logInfo;
    }

    public void setLogInfo(String logInfo) {
        this.logInfo.set(logInfo);
    }

    public boolean isIsForBatch() {
        return isForBatch.get();
    }

    public BooleanProperty isForBatchProperty() {
        return isForBatch;
    }

    public void setIsForBatch(boolean isForBatch) {
        this.isForBatch.set(isForBatch);
    }

    public boolean isIsWithHeader() {
        return isWithHeader.get();
    }

    public BooleanProperty isWithHeaderProperty() {
        return isWithHeader;
    }

    public void setIsWithHeader(boolean isWithHeader) {
        this.isWithHeader.set(isWithHeader);
    }
}
