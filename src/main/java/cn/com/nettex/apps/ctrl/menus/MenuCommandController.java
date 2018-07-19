package cn.com.nettex.apps.ctrl.menus;

import cn.com.nettex.apps.ctrl.Supervisor;
import cn.com.nettex.apps.intf.Assign;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.application.Platform.exit;

/**
 * <p>Title: science</p>
 * <p>Description: cn.com.nettex.apps.ctrl.menus.MenuCommandController</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: MIT Edu</p>
 *
 * @author <chao.deng@kewill.com>
 * @version 1.0
 * @date 07/19/2018
 */
public class MenuCommandController implements Assign<Supervisor>, Initializable {

    private Supervisor manager;
    public MenuItem miExit;

    @Override
    public void assign(Supervisor from) {
        manager = from;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void exitApp(ActionEvent event) {
        exit();
    }
}
