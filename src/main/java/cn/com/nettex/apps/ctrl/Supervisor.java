package cn.com.nettex.apps.ctrl;

import cn.com.nettex.apps.intf.Assign;
import cn.com.nettex.apps.meta.ConfigMeta;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Supervisor {
    private static final Logger logger = LogManager.getLogger(Supervisor.class);
    private Map<String, Stage> stages = new HashMap<>();


    public void addStage(String name, Stage stage) {
        stages.put(name, stage);
    }

    public Stage getStage(String name) {
        return stages.get(name);
    }

    public void setPrimaryStage(String primaryStageName, Stage primaryStage) {
        this.addStage(primaryStageName, primaryStage);
    }


    public boolean loadStage(String name, String resources, StageStyle... styles) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource(resources));
            Pane tempPane = loader.load();

            Assign<Supervisor> controlledStage = loader.getController();
            controlledStage.assign(this);

            Scene tempScene = new Scene(tempPane);
            Stage tempStage = new Stage();
            tempStage.setScene(tempScene);

            for (StageStyle style : styles) {
                tempStage.initStyle(style);
            }

            this.addStage(name, tempStage);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setStage(String name) {
        this.getStage(name).show();
        return true;
    }

    public boolean setStage(String show, String close) {
        getStage(close).close();
        setStage(show);
        return true;
    }

    public boolean unloadStage(String name) {
        if (stages.remove(name) == null) {
            logger.warn(ConfigMeta.STAGE_IS_NOT_EXIST_PLEASE_DOUBLE_CHECK);
            return false;
        } else {
            logger.info(ConfigMeta.STAGE_IS_SUCCESSFULLY_REMOVED);
            return true;
        }
    }
}
