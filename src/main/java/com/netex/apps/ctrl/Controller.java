package com.netex.apps.ctrl;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

public class Controller {


    public MenuItem miExit;
    public TextField srcFileOrDirectory;
    public Button btnSrcFileOrDirectory;
    public Label lblSrcFile;
    public TextField targetFileOrDirectory;
    public Button btnTargetFileOrDirectory;
    public ComboBox cboTargetFileType;
    public TextField txtFuzzySrcFileName;
    public Button btnStartConv;
    public Button btnCancelFileConv;
    public TextArea txteareLogInfo;
    public ProgressBar pbForConv;
    public CheckBox cbxIndicatorForBatch;
    public TextField txtFuzzyTargetFileName;
    public CheckBox cbxNeedFileHeader;

    public void exitApp(){

    }
    
    public void showSrcFileOrDirectoryTips(){
        
    }

    public void chooseSrcFileOrDirectory(ActionEvent keyEvent) {
    }

    public void showTargetFileOrDirectoryTips(MouseEvent mouseEvent) {
    }

    public void chooseTargetFileOrDirectory(ActionEvent keyEvent) {
    }

    public void chooseTargetFileType(ActionEvent keyEvent) {
    }

    public void chooseSrcFuzzyFileName(MouseEvent mouseEvent) {
    }

    public void startFileConv(ActionEvent keyEvent) {
    }

    public void cancelFileConv(ActionEvent keyEvent) {
    }

    public void validateBatchConv(ActionEvent actionEvent) {
    }

    public void chooseTargetFuzzyFileName(MouseEvent mouseEvent) {
    }
}
