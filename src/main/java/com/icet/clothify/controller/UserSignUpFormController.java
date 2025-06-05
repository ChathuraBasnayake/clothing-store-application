package com.icet.clothify.controller;

import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.ServiceFactory;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.util.ServiceType;
import com.jfoenix.controls.JFXCheckBox;
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

public class UserSignUpFormController {

    UserService userService = ServiceFactory.getInstance().getServiceType(ServiceType.USER);
    @FXML
    private JFXCheckBox checkBoxAdmin;
    @FXML
    private JFXTextField txtCompany;
    @FXML
    private JFXTextField txtConfirmPassword;
    @FXML
    private JFXTextField txtEmail;
    @FXML
    private JFXTextField txtFirstName;
    @FXML
    private JFXTextField txtLastName;
    @FXML
    private JFXTextField txtPassword;
    @FXML
    private JFXTextField txtUserId;

    public UserSignUpFormController() throws SQLException {
    }


    @FXML
    void btnCreateAccountOnAction(ActionEvent event) {


        boolean state = false;
        try {
            state = userService.add(new UserDTO(1, txtFirstName.getText() + " " + txtLastName.getText(), txtCompany.getText(), txtEmail.getText(), txtPassword.getText(), txtConfirmPassword.getText(), checkBoxAdmin.isSelected()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (state) {


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

        System.out.println(state);
    }

    @FXML
    void btnReturnDashBoardOnClick(ActionEvent event) {

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

}
