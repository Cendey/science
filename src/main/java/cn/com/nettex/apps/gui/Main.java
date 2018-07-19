package cn.com.nettex.apps.gui;

import cn.com.nettex.apps.ctrl.Supervisor;
import cn.com.nettex.apps.ctrl.convert.DataConvertController;
import cn.com.nettex.apps.i18n.BaseResourceBundleControl;
import cn.com.nettex.apps.i18n.MessageMeta;
import cn.com.nettex.apps.meta.ConfigMeta;
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
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    private Supervisor manager;

    @Override
    public void start(Stage primaryStage) {
        manager = new Supervisor();
        manager.setPrimaryStage(ConfigMeta.PRIMARY_STAGE, primaryStage);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL dataConvertUrl = classLoader.getResource(ConfigMeta.CONFIGS_DATA_CONVERT_FXML);
        Optional.ofNullable(dataConvertUrl).ifPresent(layout -> {
            FXMLLoader dataConvertLoader = new FXMLLoader(dataConvertUrl);
            dataConvertLoader
                    .setResources(ResourceBundle.getBundle(MessageMeta.MESSAGES_MESSAGE, Locale.getDefault(),
                            new BaseResourceBundleControl()));
            Parent root = null;
            try {
                root = dataConvertLoader.load();
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
            DataConvertController dataConvertController = dataConvertLoader.getController();
            dataConvertController.setStage(primaryStage);
            primaryStage.setResizable(true);
            primaryStage.show();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
