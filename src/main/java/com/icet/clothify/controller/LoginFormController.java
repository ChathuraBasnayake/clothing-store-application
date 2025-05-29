package com.icet.clothify.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private JFXTextField txtEmail;

    @FXML
    private JFXTextField txtPassword;

    @FXML
    void btnLoginOnAction(ActionEvent event) {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("/view/user_signup_form.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            // Close current window

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

            stage.setResizable(false);
            stage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void hLinkForgotPasswordOnClick(ActionEvent event) {

    }

    @FXML
    void hLinkSignUpOnClick(ActionEvent event) {

    }

}
