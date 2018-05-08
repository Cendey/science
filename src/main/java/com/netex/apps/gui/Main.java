package com.netex.apps.gui;

import com.netex.apps.ctrl.Controller;
import com.netex.apps.meta.ConfigMeta;
import com.netex.apps.util.I18NManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource(ConfigMeta.CONFIGS_SCIENCE_FXML);
        Optional.ofNullable(resource).ifPresent(layout -> {
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            fxmlLoader
                    .setResources(ResourceBundle.getBundle(ConfigMeta.MESSAGES_MESSAGE, I18NManager.getDefaultLocale()));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                logger.error(e.getCause().getMessage());
            }
            Scene scene = new Scene(Objects.requireNonNull(root), 971, 600);
            URL css = classLoader.getResource(ConfigMeta.CSS_SCIENCE_CSS);
            scene.getStylesheets().add(Objects.requireNonNull(css).toExternalForm());
            primaryStage.setScene(scene);
            URL imageUrl = classLoader.getResource(ConfigMeta.PICTURE_OFFICE_PNG);
            Image icon = new Image(Objects.requireNonNull(imageUrl).toExternalForm());
            primaryStage.getIcons().add(icon);
            Controller controller = fxmlLoader.getController();
            controller.setStage(primaryStage);
            controller.setFxmlLoader(fxmlLoader);
            primaryStage.setResizable(true);
            primaryStage.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
