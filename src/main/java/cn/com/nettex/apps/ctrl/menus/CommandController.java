package cn.com.nettex.apps.ctrl.menus;

import cn.com.nettex.apps.ctrl.strategy.Director;
import cn.com.nettex.apps.intf.Assign;
import cn.com.nettex.apps.meta.ViewMeta;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Platform.exit;

/**
 * <p>Title: science</p>
 * <p>Description: cn.com.nettex.apps.ctrl.menus.CommandController</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 07/19/2018
 */
public class CommandController implements Assign<Director>, Initializable {

    private static final Logger logger = LogManager.getLogger(CommandController.class);

    private Director manager;
    public MenuItem miExit;

    @Override
    public void assign(Director from) {
        manager = from;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void exitApp(ActionEvent event) {
        logger.info(event.getSource());
        exit();
    }

    public void showConvertView(ActionEvent event) {
        logger.info(event.getSource());
        manager.show(ViewMeta.CONVERT_STAGE);
    }

    public void showExtractView(ActionEvent event) {
        logger.info(event.getSource());
        manager.show(ViewMeta.EXTRACT_STAGE);
    }

    public void closeActiveStage(ActionEvent event) {
        logger.info(event.getSource());
        manager.close();
    }
}
