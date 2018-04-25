package com.netex.apps.gui;

import com.netex.apps.ctrl.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource("configs/science.fxml");
        if (resource != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            Parent root = fxmlLoader.load();
            final Scene scene = new Scene(root, 650, 400);
            primaryStage.setScene(scene);
            URL imageUrl = classLoader.getResource("picture/office.png");
            Image icon = new Image(Objects.requireNonNull(imageUrl).toExternalForm());
            primaryStage.getIcons().add(icon);
            final Controller controller = fxmlLoader.getController();
            controller.setStage(primaryStage);
            primaryStage.setResizable(true);
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
