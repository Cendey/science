package com.netex.apps.gui;

import com.netex.apps.ctrl.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File config = new File(Objects.requireNonNull(classLoader.getResource("log/log4j2.xml")).getPath());
        context.setConfigLocation(config.toURI());
    }

    @Override
    public void start(Stage primaryStage) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resource = classLoader.getResource("configs/science.fxml");
        Optional.ofNullable(resource).ifPresent(layout -> {
            FXMLLoader fxmlLoader = new FXMLLoader(resource);
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                logger.error(e.getCause().getMessage());
            }
            final Scene scene = new Scene(root, 650, 400);
            primaryStage.setScene(scene);
            URL imageUrl = classLoader.getResource("picture/office.png");
            Image icon = new Image(Objects.requireNonNull(imageUrl).toExternalForm());
            primaryStage.getIcons().add(icon);
            final Controller controller = fxmlLoader.getController();
            controller.setStage(primaryStage);
            primaryStage.setResizable(true);
            primaryStage.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
