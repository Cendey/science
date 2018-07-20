package cn.com.nettex.apps.mods;

import cn.com.nettex.apps.i18n.I18NManager;
import cn.com.nettex.apps.i18n.MessageMeta;
import cn.com.nettex.apps.meta.FileMeta;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * <p>Title: science</p>
 * <p>Description: Model</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/23/2018
 */
public class Model {

    private static final String BLANK = "";
    private static final FileMeta[] COMMON_FILE_EXTENSION = {
            new FileMeta("Text File", ".text"),
            new FileMeta("ASCII Text File", ".txt"),
            new FileMeta("Comma Separated Values", ".csv"),
            new FileMeta("Microsoft Excel", ".xls"),
            new FileMeta("Office Open XML Workbook", ".xlsx")
    };

    private StringProperty windowsTitle;
    private StringProperty srcPath;
    private StringProperty srcNamedAs;
    private StringProperty srcFuzzyName;
    private StringProperty srcFormat;
    private StringProperty destPath;
    private StringProperty destNamedTo;
    private StringProperty destRenameTo;
    private ObservableList<FileMeta> destFormat;
    private BooleanProperty isForBatch;
    private BooleanProperty isWithHeader;

    public Model() {
//        this("", "", "", "", "", "", "", false, false);
        this.windowsTitle = new SimpleStringProperty(I18NManager.get(MessageMeta.WINDOWS_TITLE));
        this.srcPath = new SimpleStringProperty(BLANK);
        this.srcNamedAs = new SimpleStringProperty(BLANK);
        this.srcFuzzyName = new SimpleStringProperty(BLANK);
        this.srcFormat = new SimpleStringProperty(BLANK);
        this.destPath = new SimpleStringProperty(BLANK);
        this.destNamedTo = new SimpleStringProperty(BLANK);
        this.destRenameTo = new SimpleStringProperty(BLANK);
        this.destFormat = FXCollections.observableArrayList(COMMON_FILE_EXTENSION);
        this.isForBatch = new SimpleBooleanProperty(false);
        this.isWithHeader = new SimpleBooleanProperty(false);
    }

    public Model(
            String title,
            String srcPath, String srcNamedAs,
            String srcFuzzyName, String srcFormat,
            String destPath, String destNamedTo,
            FileMeta[] destFormat, Boolean isForBatch, Boolean isWithHeader) {
        this.windowsTitle = new SimpleStringProperty(title);
        this.srcPath = new SimpleStringProperty(srcPath);
        this.srcNamedAs = new SimpleStringProperty(srcNamedAs);
        this.srcFuzzyName = new SimpleStringProperty(srcFuzzyName);
        this.srcFormat = new SimpleStringProperty(srcFormat);
        this.destPath = new SimpleStringProperty(destPath);
        this.destNamedTo = new SimpleStringProperty(destNamedTo);
        this.destFormat = FXCollections.observableArrayList(destFormat);
        this.isForBatch = new SimpleBooleanProperty(isForBatch);
        this.isWithHeader = new SimpleBooleanProperty(isWithHeader);
    }

    public String getWindowsTitle() {
        return windowsTitle.get();
    }

    public StringProperty windowsTitleProperty() {
        return windowsTitle;
    }

    public void setWindowsTitle(String windowsTitle) {
        this.windowsTitle.set(windowsTitle);
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

    public String getDestRenameTo() {
        return destRenameTo.get();
    }

    public StringProperty destRenameToProperty() {
        return destRenameTo;
    }

    public void setDestRenameTo(String destRenameTo) {
        this.destRenameTo.set(destRenameTo);
    }

    public ObservableList<FileMeta> getDestFormat() {
        return destFormat;
    }

    public void setDestFormat(ObservableList<FileMeta> destFormat) {
        this.destFormat = destFormat;
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
