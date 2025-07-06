package com.icet.clothify.controller;

import com.google.inject.Inject;
import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.custom.UserService;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AuthenticationController {

    @Inject
    UserService userService;

    Boolean isLoginSucceedWithAdminAcc = false;
    Boolean navigateToSignUp = false;
    @FXML
    private JFXTextField forgotPasswordEmail;
    @FXML
    private AnchorPane forgotPasswordPane;
    @FXML
    private JFXTextField loginEmail;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private JFXTextField loginPassword;
    @FXML
    private JFXTextField signUpConfirmPassword;
    @FXML
    private JFXTextField signUpEmail;
    @FXML
    private AnchorPane signUpPane;
    @FXML
    private JFXTextField signUpPassword;
    @FXML
    private JFXTextField signUpUsername;
    @FXML
    private JFXTextField signUpCompany;
    @FXML
    private JFXCheckBox signUpIsEmployee;

    @FXML
    void showLoginPane(ActionEvent event) {
        loginPane.setVisible(true);
        signUpPane.setVisible(false);
        forgotPasswordPane.setVisible(false);
    }

    @FXML
    void showSignUpPane(ActionEvent event) {
        try {
            if (!userService.isAdmin(loginEmail.getText()) || loginEmail.getText().isEmpty()) {
                alert(Alert.AlertType.INFORMATION, "SignUp", "First you have to sign up with an admin account");
                navigateToSignUp = true;
                showLoginPane(null);
            } else {
                loginPane.setVisible(false);
                signUpPane.setVisible(true);
                forgotPasswordPane.setVisible(false);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void showForgotPasswordPane(ActionEvent event) {
        loginPane.setVisible(false);
        signUpPane.setVisible(false);
        forgotPasswordPane.setVisible(true);
    }

    @FXML
    void loginOnAction(ActionEvent event) {
        try {
            if (userService.userVerifier(loginEmail.getText(), loginPassword.getText())) {
                if (Boolean.TRUE.equals(navigateToSignUp)) {
                    navigateToSignUp = false;
                    showSignUpPane(null);
                } else {
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
                }
            } else {
                alert(Alert.AlertType.WARNING, "Login Error", "Username Or Password could not match");
            }
        } catch (SQLException e) {

            throw new RuntimeException("UserName and PassWord Checking failure! Could t Connect With DataBase ", e);
        }

        // Implement your login logic here
        System.out.println("Login button clicked.");
    }

    @FXML
    void signUpOnAction(ActionEvent event) {

        try {
            boolean isAdded = userService.add(new UserDTO(null, signUpUsername.getText(), signUpCompany.getText(), signUpEmail.getText(), signUpPassword.getText(), signUpConfirmPassword.getText(), signUpIsEmployee.isSelected()));
            if (Boolean.TRUE.equals(isAdded)) {
                navigateToSignUp = false;
                alert(Alert.AlertType.INFORMATION, "UserSignUp", "User SignUp Successfully");
                showLoginPane(null);
            } else {
                alert(Alert.AlertType.WARNING, "UserSignUp", "User SignUp Failure Due to internal Error");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to adding User to Database Sql Error!", e);
        }
    }

    @FXML
    void sendLinkOnAction(ActionEvent event) {
        System.out.println("Send reset link button clicked.");
    }

    private void alert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}