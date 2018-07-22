package cn.com.nettex.apps.ctrl.strategy;

import cn.com.nettex.apps.gui.Main;
import cn.com.nettex.apps.i18n.BaseResourceBundleControl;
import cn.com.nettex.apps.i18n.MessageMeta;
import cn.com.nettex.apps.intf.Assign;
import cn.com.nettex.apps.meta.ElemMeta;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class Director {

    private static final Logger logger = LogManager.getLogger(Supervisor.class);
    private Map<String, Parent> children = new HashMap<>();
    private Map<String, Assign<Director>> controllers = new HashMap<>();
    private Stage primaryStage;
    private static Director instance;

    private Director(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public static Director getInstance(Stage primaryStage) {
        if (instance == null) {
            instance = new Director(primaryStage);
        }
        return instance;
    }

    private Stage getPrimaryStage() {
        return primaryStage;
    }

    private void addChild(String name, Parent node) {
        children.put(name, node);
    }

    public Parent getChild(String name) {
        return children.get(name);
    }

    private void addController(String name, Assign<Director> controller) {
        controllers.put(name, controller);
    }

    private Assign<Director> getController(String name) {
        return controllers.get(name);
    }

    public void loadChild(String name, String resources) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resourceUrl = classLoader.getResource(resources);
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            loader.setResources(ResourceBundle.getBundle(
                MessageMeta.MESSAGES_MESSAGE, Locale.getDefault(),
                new BaseResourceBundleControl()));
            Parent child = null;
            try {
                child = loader.load();
            } catch (IOException e) {
                logger.error(e.getCause().getMessage());
            }
            Assign<Director> controller = loader.getController();
            controller.assign(this);
            this.addChild(name, child);
            this.addController(name, controller);
        } catch (Exception e) {
            logger.error(e.getCause().getMessage());
        }
    }

    public boolean unloadChild(String name) {
        if (children.containsKey(name)) {
            children.remove(name);
            logger.info(ElemMeta.STAGE_IS_SUCCESSFULLY_REMOVED);
            return true;
        } else {
            logger.warn(ElemMeta.STAGE_IS_NOT_EXIST_PLEASE_DOUBLE_CHECK);
            return false;
        }
    }

    public void close() {
        Main.getRoot().setCenter(null);
    }

    public void show(String activeChild) {
        getController(activeChild).changeStatus(getPrimaryStage());
        Main.getRoot().setCenter(getChild(activeChild));
    }
}
