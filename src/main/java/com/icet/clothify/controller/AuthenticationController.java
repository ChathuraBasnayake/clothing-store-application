package com.icet.clothify.controller;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.util.EmailUtil;
import com.icet.clothify.util.OtpUtil;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.concurrent.Task;
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
    private Injector injector;

    @Inject
    UserService userService;


    private String generatedOtp;
    private String emailForPasswordReset;

    Boolean isLoginSucceedWithAdminAcc = false;
    Boolean navigateToSignUp = false;


    @FXML private AnchorPane loginPane;
    @FXML private JFXTextField loginEmail;
    @FXML private JFXPasswordField loginPassword;

    @FXML private AnchorPane signUpPane;
    @FXML private JFXTextField signUpUsername;
    @FXML private JFXTextField signUpCompany;
    @FXML private JFXTextField signUpEmail;
    @FXML private JFXPasswordField signUpPassword;
    @FXML private JFXPasswordField signUpConfirmPassword;
    @FXML private JFXCheckBox signUpIsEmployee;

    @FXML private AnchorPane forgotPasswordPane;
    @FXML private JFXTextField forgotPasswordEmail;


    @FXML private AnchorPane otpPane;
    @FXML private JFXTextField otpField;
    @FXML private JFXPasswordField newPassword;
    @FXML private JFXPasswordField confirmNewPassword;



    @FXML
    void showLoginPane(ActionEvent event) {
        loginPane.setVisible(true);
        signUpPane.setVisible(false);
        forgotPasswordPane.setVisible(false);
        otpPane.setVisible(false);
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
                otpPane.setVisible(false);
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
        otpPane.setVisible(false);
    }

    private void showOtpPane() {
        loginPane.setVisible(false);
        signUpPane.setVisible(false);
        forgotPasswordPane.setVisible(false);
        otpPane.setVisible(true);
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
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
                        loader.setControllerFactory(injector::getInstance);
                        Parent root = loader.load();
                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));
                        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
                        stage.setResizable(false);
                        stage.show();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to load dashboard view.", e);
                    }
                }
            } else {
                alert(Alert.AlertType.WARNING, "Login Error", "Username Or Password could not match");
            }
        } catch (SQLException e) {
            throw new RuntimeException("UserName and PassWord Checking failure! Could not Connect With DataBase ", e);
        }
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
    void sendOtpOnAction(ActionEvent event) {
        emailForPasswordReset = forgotPasswordEmail.getText();
        if (emailForPasswordReset == null || emailForPasswordReset.trim().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Email address cannot be empty.");
            return;
        }

        try {
            if (!userService.userExists(emailForPasswordReset)) {
                alert(Alert.AlertType.ERROR, "Error", "No account found with this email address.");
                return;
            }

            this.generatedOtp = OtpUtil.generateOtp();
            System.out.println("Generated OTP: " + this.generatedOtp); // For debugging, remove in production

            String subject = "Your Password Reset OTP";
            String body = "<h1>Password Reset Request</h1>"
                    + "<p>Dear User,</p>"
                    + "<p>Your One-Time Password (OTP) for resetting your password is:</p>"
                    + "<h2 style='color: #007bff;'>" + this.generatedOtp + "</h2>"
                    + "<p>This OTP is valid for 10 minutes. Please do not share it with anyone.</p>"
                    + "<p>Regards,<br>Clothify Application</p>";

            Task<Void> sendEmailTask = EmailUtil.createSendEmailTask(emailForPasswordReset, subject, body);

            sendEmailTask.setOnSucceeded(e -> {
                alert(Alert.AlertType.INFORMATION, "OTP Sent", "An OTP has been sent to your email address.");
                showOtpPane();
            });

            sendEmailTask.setOnFailed(e -> {
                alert(Alert.AlertType.ERROR, "Email Error", "Failed to send OTP. Please check your connection or contact support.");
                sendEmailTask.getException().printStackTrace();
            });

            new Thread(sendEmailTask).start();

        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Could not verify email. Please try again later.");
            e.printStackTrace();
        }
    }

    @FXML
    void verifyOtpAndResetPassword(ActionEvent event) {
        String enteredOtp = otpField.getText();
        String newPass = newPassword.getText();
        String confirmPass = confirmNewPassword.getText();

        if (enteredOtp == null || !enteredOtp.equals(this.generatedOtp)) {
            alert(Alert.AlertType.WARNING, "Validation Error", "The entered OTP is incorrect.");
            return;
        }
        if (newPass == null || newPass.isEmpty() || newPass.length() < 6) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 6 characters long.");
            return;
        }
        if (!newPass.equals(confirmPass)) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match.");
            return;
        }

        try {
            boolean isUpdated = userService.updatePassword(emailForPasswordReset, newPass);
            if (isUpdated) {
                alert(Alert.AlertType.INFORMATION, "Success", "Your password has been reset successfully. Please log in.");
                showLoginPane(null);
            } else {
                alert(Alert.AlertType.ERROR, "Error", "Failed to update password. Please try again.");
            }
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating your password.");
            e.printStackTrace();
        }
    }


    private void alert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
