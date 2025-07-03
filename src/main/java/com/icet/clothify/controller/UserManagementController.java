package com.icet.clothify.controller;

import com.google.inject.Inject;
import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.custom.UserService;
import com.jfoenix.controls.JFXCheckBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.icet.clothify.util.AlertUtil.alert;

public class UserManagementController {

    @FXML
    private TextField userIdField;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField companyField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private JFXCheckBox isEmployeeCheckBox;

    // --- FXML Fields for "Update User" Tab ---
    @FXML
    private ComboBox<UserDTO> updateUserComboBox;
    @FXML
    private TextField updateUserIdField;
    @FXML
    private TextField updateUserNameField;
    @FXML
    private TextField updateEmailField;
    @FXML
    private TextField updateCompanyField;
    @FXML
    private JFXCheckBox updateIsEmployeeCheckBox;

    // --- FXML Fields for "Delete User" Tab ---
    @FXML
    private ComboBox<UserDTO> deleteUserComboBox;


    @Inject
    private UserService userService;

    @FXML
    public void initialize() {

        setUpdateUserComboBox();
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
                    null,
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

    private boolean validateUpdateUserInputs() {
        if (updateUserNameField.getText().trim().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Full Name cannot be empty.");
            return false;
        }

        String email = updateEmailField.getText().trim();
        if (email.isEmpty() || !email.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$")) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            return false;
        }

        return true;
    }


    public void handleUpdateUser(ActionEvent actionEvent) {
        if (!validateUpdateUserInputs()) {
            return;
        }


        try {
            UserDTO userDTO = new UserDTO(updateUserIdField.getText(),
                    updateUserNameField.getText(),
                    updateCompanyField.getText(),
                    updateEmailField.getText(),
                    null,
                    null,
                    updateIsEmployeeCheckBox.isSelected());

            if (userService.update(userDTO)) {
                alert(Alert.AlertType.INFORMATION, "Success", " supplier has been updated successfully.");
                setUpdateUserComboBox();
                clearFields();
            } else {
                alert(Alert.AlertType.ERROR, "Save Failed", "Could not update the supplier due to a system error.");
            }
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Failed to Update the supplier to the database.");
            e.printStackTrace();
        }


    }

    public void handleDeleteUser(ActionEvent actionEvent) {

        try {
            boolean delete = userService.delete(deleteUserComboBox.getValue().getId().toString());
            if (delete) {
                alert(Alert.AlertType.INFORMATION, "Success", " supplier deleted successfully.");
            }

        } catch (SQLException e) {
            alert(Alert.AlertType.WARNING, "DataBase Error", "Error Occurred when Deleting Supplier from database!");
            throw new RuntimeException(e);
        }

        setUpdateUserComboBox();
    }

    public void setUpdateUserComboBox() {

        List<ComboBox<UserDTO>> supplierComboBoxes = List.of(deleteUserComboBox, updateUserComboBox);

        List<UserDTO> all;
        try {
            all = userService.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<UserDTO> userDTOS = FXCollections.observableArrayList(all);

        supplierComboBoxes.forEach(comboBox -> {
            comboBox.setItems(userDTOS);

            comboBox.setConverter(new StringConverter() {
                @Override
                public String toString(Object o) {
                    UserDTO userDTO = (UserDTO) o;
                    return userDTO.getId() + " : " + userDTO.getName();
                }

                @Override
                public Object fromString(String s) {
                    return all.stream().filter(item -> toString(item).equals(s)).findFirst().orElse(null);
                }
            });
        });


        updateUserComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateUserNameField.setText(newVal.getName());
                updateUserIdField.setText(newVal.getId());
                updateCompanyField.setText(newVal.getCompany());
                updateEmailField.setText(newVal.getEmail());
                isEmployeeCheckBox.setSelected(newVal.getIsEmployee());
            }
        });

    }

}

