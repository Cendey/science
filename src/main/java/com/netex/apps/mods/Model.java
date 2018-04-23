package com.netex.apps.mods;

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

    private String srcPath;
    private String srcNamedAs;
    private String srcFuzzyName;
    private String srcFormat;
    private String destPath;
    private String destNamedTo;
    private String destFormat;

    public Model(String srcPath, String destPath, String destFormat) {
        this.srcPath = srcPath;
        this.destPath = destPath;
        this.destFormat = destFormat;
    }

    public Model(
        String srcPath, String srcFuzzyName, String destPath, String destFormat) {
        this.srcPath = srcPath;
        this.srcFuzzyName = srcFuzzyName;
        this.destPath = destPath;
        this.destFormat = destFormat;
    }

    public Model(
        String srcPath, String srcFuzzyName, String destPath, String destNamedTo,
        String destFormat) {
        this.srcPath = srcPath;
        this.srcFuzzyName = srcFuzzyName;
        this.destPath = destPath;
        this.destNamedTo = destNamedTo;
        this.destFormat = destFormat;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getSrcNamedAs() {
        return srcNamedAs;
    }

    public void setSrcNamedAs(String srcNamedAs) {
        this.srcNamedAs = srcNamedAs;
    }

    public String getSrcFuzzyName() {
        return srcFuzzyName;
    }

    public void setSrcFuzzyName(String srcFuzzyName) {
        this.srcFuzzyName = srcFuzzyName;
    }

    public String getSrcFormat() {
        return srcFormat;
    }

    public void setSrcFormat(String srcFormat) {
        this.srcFormat = srcFormat;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    public String getDestNamedTo() {
        return destNamedTo;
    }

    public void setDestNamedTo(String destNamedTo) {
        this.destNamedTo = destNamedTo;
    }

    public String getDestFormat() {
        return destFormat;
    }

    public void setDestFormat(String destFormat) {
        this.destFormat = destFormat;
    }
}
