package com.icet.clothify.util;

import javafx.scene.control.Alert;

public class Util {
    public static void alert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
