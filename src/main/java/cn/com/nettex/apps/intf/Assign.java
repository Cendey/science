package cn.com.nettex.apps.intf;

import javafx.stage.Stage;

public interface Assign<T> {

    void assign(T from);

    default void changeStatus(Stage stage) {
    }
}
