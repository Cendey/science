package com.nettex.apps.meta;

/**
 * <p>Title: science</p>
 * <p>Description: TaskMeta</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/27/2018
 */
public class TaskMeta {

    private String srcPath;
    private String nameAs;
    private String destPath;
    private String nameTo;
    private String type;
    private Boolean header;

    public TaskMeta(String srcPath, String destPath, String type, Boolean header) {
        this.srcPath = srcPath;
        this.destPath = destPath;
        this.type = type;
        this.header = header;
    }

    public TaskMeta(
        String srcPath, String nameAs, String destPath, String type, Boolean header) {
        this.srcPath = srcPath;
        this.nameAs = nameAs;
        this.destPath = destPath;
        this.type = type;
        this.header = header;
    }

    public TaskMeta(String srcPath, String nameAs, String destPath, String nameTo, String type, Boolean header) {
        this.srcPath = srcPath;
        this.nameAs = nameAs;
        this.destPath = destPath;
        this.nameTo = nameTo;
        this.type = type;
        this.header = header;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getNameAs() {
        return nameAs;
    }

    public void setNameAs(String nameAs) {
        this.nameAs = nameAs;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public String getNameTo() {
        return nameTo;
    }

    public void setNameTo(String nameTo) {
        this.nameTo = nameTo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getHeader() {
        return header;
    }

    public void setHeader(Boolean header) {
        this.header = header;
    }
}
