package com.netex.apps.gui;

import com.netex.apps.ctrl.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("configs/science.fxml");
        if (resource != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            Parent root = fxmlLoader.load();
            final Scene scene = new Scene(root, 650, 400);
            primaryStage.setScene(scene);
            final Controller controller = fxmlLoader.getController();
            controller.setStage(primaryStage);
            primaryStage.setOnCloseRequest(we -> System.out.println("Stage is closing"));
            primaryStage.show();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    private StageStyle configStageStyle() {
        StageStyle stageStyle = StageStyle.DECORATED;
        List<String> unnamedParams = getParameters().getUnnamed();
        if (unnamedParams.size() > 0) {
            String stageStyleParam = unnamedParams.get(0);
            if (stageStyleParam.equalsIgnoreCase("transparent")) {
                stageStyle = StageStyle.TRANSPARENT;
            } else if (stageStyleParam.equalsIgnoreCase("undecorated")) {
                stageStyle = StageStyle.UNDECORATED;
            } else if (stageStyleParam.equalsIgnoreCase("utility")) {
                stageStyle = StageStyle.UTILITY;
            }
        }
        return stageStyle;
    }
}
