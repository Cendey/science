package cn.com.nettex.apps.gui;

import cn.com.nettex.apps.ctrl.strategy.Director;
import cn.com.nettex.apps.meta.ElemMeta;
import cn.com.nettex.apps.meta.ViewMeta;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 * Refer: http://javajdk.net/tutorial/multiple-javafx-scenes-sharing-one-menubar/
 */
public class Main extends Application {

    private Director manager;
    private static BorderPane root = new BorderPane();

    public static BorderPane getRoot() {
        return root;
    }

    @Override
    public void start(Stage primaryStage) {
        manager = Director.getInstance(primaryStage);
        initView();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Optional.ofNullable(classLoader).ifPresent(layout -> {
            Parent menuBar = manager.getChild(ViewMeta.PRIMARY_STAGE);
            root.setTop(menuBar);
            Scene scene = new Scene(root, 971, 600);
            URL css = classLoader.getResource(ElemMeta.CSS_SCIENCE_CSS);
            scene.getStylesheets().add(Objects.requireNonNull(css).toExternalForm());
            primaryStage.setScene(scene);
            URL imageUrl = classLoader.getResource(ElemMeta.PICTURE_OFFICE_PNG);
            Image icon = new Image(Objects.requireNonNull(imageUrl).toExternalForm());
            primaryStage.getIcons().add(icon);
            primaryStage.setResizable(true);
            primaryStage.show();
        });
    }

    private void initView() {
        manager.loadChild(ViewMeta.PRIMARY_STAGE, ViewMeta.CONFIG_MENUS_FXML);
        manager.loadChild(ViewMeta.CONVERT_STAGE, ViewMeta.CONFIGS_DATA_CONVERT_FXML);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
