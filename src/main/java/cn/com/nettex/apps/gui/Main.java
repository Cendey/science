package cn.com.nettex.apps.gui;

import cn.com.nettex.apps.ctrl.Supervisor;
import cn.com.nettex.apps.meta.ViewMeta;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {

    private static final Logger logger = LogManager.getLogger(Main.class);

    private Supervisor manager;

    @Override
    public void start(Stage primaryStage) {
        manager = new Supervisor();
        manager.setPrimaryStage(ViewMeta.PRIMARY_STAGE, primaryStage);

        initView();

        manager.setStage(ViewMeta.PRIMARY_STAGE);
    }

    private void initView() {
        manager.loadStage(ViewMeta.PRIMARY_STAGE, ViewMeta.CONFIG_MENUS_FXML);
        manager.loadStage(ViewMeta.CONVERT_STAGE, ViewMeta.CONFIGS_DATA_CONVERT_FXML);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
