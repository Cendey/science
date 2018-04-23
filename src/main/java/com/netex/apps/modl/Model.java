package com.netex.apps.modl;

/**
 * <p>Title: science</p>
 * <p>Description: com.netex.apps.modl.Model</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 04/23/2018
 */
public class Model {

    private String srcFileOrDirectory;
    private String srcFileFuzzyName;
    private String srcFileFormat;
    private String targetFileOrDirectory;
    private String targetFileRenamedAs;
    private String targetFileFormat;

    public Model(String srcFileOrDirectory, String targetFileOrDirectory, String targetFileFormat) {
        this.srcFileOrDirectory = srcFileOrDirectory;
        this.targetFileOrDirectory = targetFileOrDirectory;
        this.targetFileFormat = targetFileFormat;
    }

    public Model(
        String srcFileOrDirectory, String srcFileFuzzyName, String targetFileOrDirectory, String targetFileFormat) {
        this.srcFileOrDirectory = srcFileOrDirectory;
        this.srcFileFuzzyName = srcFileFuzzyName;
        this.targetFileOrDirectory = targetFileOrDirectory;
        this.targetFileFormat = targetFileFormat;
    }

    public Model(
        String srcFileOrDirectory, String srcFileFuzzyName, String targetFileOrDirectory, String targetFileRenamedAs,
        String targetFileFormat) {
        this.srcFileOrDirectory = srcFileOrDirectory;
        this.srcFileFuzzyName = srcFileFuzzyName;
        this.targetFileOrDirectory = targetFileOrDirectory;
        this.targetFileRenamedAs = targetFileRenamedAs;
        this.targetFileFormat = targetFileFormat;
    }

    public String getSrcFileOrDirectory() {
        return srcFileOrDirectory;
    }

    public void setSrcFileOrDirectory(String srcFileOrDirectory) {
        this.srcFileOrDirectory = srcFileOrDirectory;
    }

    public String getSrcFileFuzzyName() {
        return srcFileFuzzyName;
    }

    public void setSrcFileFuzzyName(String srcFileFuzzyName) {
        this.srcFileFuzzyName = srcFileFuzzyName;
    }

    public String getSrcFileFormat() {
        return srcFileFormat;
    }

    public void setSrcFileFormat(String srcFileFormat) {
        this.srcFileFormat = srcFileFormat;
    }

    public String getTargetFileOrDirectory() {
        return targetFileOrDirectory;
    }

    public void setTargetFileOrDirectory(String targetFileOrDirectory) {
        this.targetFileOrDirectory = targetFileOrDirectory;
    }

    public String getTargetFileRenamedAs() {
        return targetFileRenamedAs;
    }

    public void setTargetFileRenamedAs(String targetFileRenamedAs) {
        this.targetFileRenamedAs = targetFileRenamedAs;
    }

    public String getTargetFileFormat() {
        return targetFileFormat;
    }

    public void setTargetFileFormat(String targetFileFormat) {
        this.targetFileFormat = targetFileFormat;
    }
}
