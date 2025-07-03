package com.icet.clothify.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static com.icet.clothify.util.AlertUtil.alert;

public class DashboardController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Button homeBtn;
    @FXML
    private Button makeOrderBtn;
    @FXML
    private Button addUserBtn;
    @FXML
    private Button addSupplierBtn;
    @FXML
    private Button settingsBtn;
    @FXML
    private Button addItemBtn;
    @FXML
    private Button logoutBtn;
    @FXML
    private Button reportsBtn;

    @FXML
    private Node homeView;
    @FXML
    private Node addSupplierView;
    @FXML
    private Node makeOrderView;
    @FXML
    private Node addUserView;
    @FXML
    private Node settingsView;
    @FXML
    private Node inventoryManagement;
    @FXML
    private Node reportsView;

    @FXML
    private HomeController homeViewController;
    @FXML
    private MakeOrderController makeOrderViewController;
    @FXML
    private InventoryManagementController inventoryManagementController;

    private List<Node> views;

    @FXML
    public void initialize() {

        setupLiveClock();

        views = List.of(homeView, addSupplierView, makeOrderView, addUserView, settingsView, inventoryManagement, reportsView);

        showView(homeView, "Dashboard");


    }

    @FXML
    void handleNavigation(ActionEvent event) {
        Object source = event.getSource();

        if (source == homeBtn) {
            showView(homeView, "Dashboard");
            homeViewController.refreshData();
        } else if (source == makeOrderBtn) {
            showView(makeOrderView, "Make New Order");
            makeOrderViewController.initializeData();
        } else if (source == addUserBtn) {
            showView(addUserView, "Add New User");
        } else if (source == addSupplierBtn) {
            showView(addSupplierView, "Add New Supplier");
        } else if (source == settingsBtn) {
            showView(settingsView, "Settings");
        } else if (source == addItemBtn) {
            showView(inventoryManagement, "Add New Item");
            inventoryManagementController.initializeData();
        } else if (source == reportsBtn) { // Handle new button click
            showView(reportsView, "Reports & Analytics");
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Authentication_form.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Clothify - Login");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            ((Stage) logoutBtn.getScene().getWindow()).close();

        } catch (IOException e) {
            alert(javafx.scene.control.Alert.AlertType.ERROR, "Logout Error", "Could not load the login screen.");
            e.printStackTrace();
        }
    }


    private void showView(Node viewToShow, String title) {
        titleLabel.setText(title);
        views.forEach(view -> view.setVisible(false));
        viewToShow.setVisible(true);
        viewToShow.toFront();
    }

    private void setupLiveClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, h:mm:ss a");
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            dateTimeLabel.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}
