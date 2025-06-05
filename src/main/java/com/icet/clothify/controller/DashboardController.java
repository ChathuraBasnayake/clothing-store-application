package com.icet.clothify.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    @FXML
    private Pane ItemAddContent;

    @FXML
    void addUserOnClicked(MouseEvent event) throws IOException {

        // Load the new window (NewWindow.fxml)
        Parent newRoot = FXMLLoader.load(getClass().getResource("/view/user_signup_form.fxml"));

        // Create and show the new stage
        Stage newStage = new Stage();
        newStage.setScene(new Scene(newRoot));
        newStage.setTitle("New Window");
        newStage.show();

        // Close the current stage
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();


    }

    @FXML
    void addItemOnclicked(MouseEvent event) {
        try {
            Parent newContent = FXMLLoader.load(getClass().getResource("/view/itemAddForm.fxml"));
            ItemAddContent.getChildren().setAll(newContent);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
