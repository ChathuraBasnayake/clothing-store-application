package com.icet.clothify.controller;

import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.ServiceFactory;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.util.ServiceType;
import com.jfoenix.controls.JFXCheckBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.icet.clothify.util.Util.alert;

public class AddUserController {

    @FXML
    private TextField userIdField;
    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField companyField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private JFXCheckBox isEmployeeCheckBox;

    private UserService userService;

    @FXML
    public void initialize() {
        try {
            userService = ServiceFactory.getInstance().getServiceType(ServiceType.USER);
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Initialization Failed", "Could not connect to user service.");
            e.printStackTrace();
        }
        clearFields();
    }

    @FXML
    void handleCreateUser(ActionEvent actionEvent) {
        if (!validateInputsForUser()) {
            return;
        }

        try {
            UserDTO newUser = new UserDTO(
                    userIdField.getText(),
                    userNameField.getText(),
                    companyField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    null, // Role can be set here if needed
                    isEmployeeCheckBox.isSelected()
            );

            if (userService.add(newUser)) {
                alert(Alert.AlertType.INFORMATION, "User Created", "User was created successfully.");
                clearFields();
            } else {
                alert(Alert.AlertType.WARNING, "Creation Failed", "User could not be created. User ID or Email might already exist.");
            }
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Failed to save the user due to a database error.");
            e.printStackTrace();
        }
    }

    public void clearFields() {
        userIdField.setText(generateUserId());
        List.of(userNameField, companyField, emailField, passwordField, confirmPasswordField).forEach(
                TextInputControl::clear
        );
        isEmployeeCheckBox.setSelected(false);
    }

    private String generateUserId() {
        // USER-20250619143055
        return "USER-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    private boolean validateInputsForUser() {
        if (userNameField.getText().trim().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Full Name cannot be empty.");
            return false;
        }
        String email = emailField.getText().trim();
        if (email.isEmpty() || !email.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$")) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Password cannot be empty.");
            return false;
        }
        if (passwordField.getText().length() < 6) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 6 characters long.");
            return false;
        }
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match.");
            return false;
        }
        return true;
    }
}

