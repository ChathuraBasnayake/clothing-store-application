package com.icet.clothify.controller;

import com.icet.clothify.service.ServiceFactory;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.util.ServiceType;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class LoginFormController {

    UserService userService;
    @FXML
    private JFXTextField txtEmail;
    @FXML
    private JFXTextField txtPassword;

    {
        try {
            userService = ServiceFactory.getInstance().getServiceType(ServiceType.USER);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnLoginOnAction(ActionEvent event) {

        if (userService.userLogIn(txtEmail.getText(), txtPassword.getText())) {


            try {

                Parent root = FXMLLoader.load(getClass().getResource("/view/dashboard.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));

                // Close current window

                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

                stage.setResizable(false);
                stage.show();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {


        }


    }

    @FXML
    void hLinkForgotPasswordOnClick(ActionEvent event) {

    }

    @FXML
    void hLinkSignUpOnClick(ActionEvent event) {

    }

}
