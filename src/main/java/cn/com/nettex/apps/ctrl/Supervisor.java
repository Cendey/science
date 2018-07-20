package cn.com.nettex.apps.ctrl;

import cn.com.nettex.apps.i18n.BaseResourceBundleControl;
import cn.com.nettex.apps.i18n.MessageMeta;
import cn.com.nettex.apps.intf.Assign;
import cn.com.nettex.apps.meta.ElemMeta;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resourceUrl = classLoader.getResource(resources);
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(ResourceBundle.getBundle(MessageMeta.MESSAGES_MESSAGE, Locale.getDefault(),
                    new BaseResourceBundleControl()));
            Parent root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                logger.error(e.getCause().getMessage());
            }

            Assign<Supervisor> controller = loader.getController();
            controller.assign(this);

            Scene scene = new Scene(Objects.requireNonNull(root), 971, 600);
            URL css = classLoader.getResource(ElemMeta.CSS_SCIENCE_CSS);
            scene.getStylesheets().add(Objects.requireNonNull(css).toExternalForm());
            Stage primary = new Stage();
            primary.setScene(scene);
            URL imageUrl = classLoader.getResource(ElemMeta.PICTURE_OFFICE_PNG);
            Image icon = new Image(Objects.requireNonNull(imageUrl).toExternalForm());
            primary.getIcons().add(icon);

            for (StageStyle style : styles) {
                primary.initStyle(style);
            }

            this.addStage(name, primary);

            return true;
        } catch (Exception e) {
            logger.error(e.getCause().getMessage());
            return false;
        }
    }

    public boolean setStage(String name) {
        Stage stage = this.getStage(name);
        stage.setResizable(true);
        stage.show();
        return true;
    }

    public boolean setStage(String show, String hide) {
        getStage(hide).hide();
        setStage(show);
        return true;
    }

    public boolean unloadStage(String name) {
        if (stages.remove(name) == null) {
            logger.warn(ElemMeta.STAGE_IS_NOT_EXIST_PLEASE_DOUBLE_CHECK);
            return false;
        } else {
            logger.info(ElemMeta.STAGE_IS_SUCCESSFULLY_REMOVED);
            return true;
        }
    }
}
