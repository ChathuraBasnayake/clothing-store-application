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

import static com.icet.clothify.util.Util.alert;

public class DashboardController {

    //<editor-fold desc="FXML Fields for the Main Shell">
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
    //</editor-fold>

    //<editor-fold desc="FXML Fields for Included Views (Nodes)">
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
    private Node addItemView;
    //</editor-fold>

    //<editor-fold desc="FXML Fields for Included View Controllers">
    // This uses the "controller injection" convention.
    // The field name must be the fx:id of the <fx:include> tag + "Controller".
    @FXML
    private HomeController homeViewController;
    @FXML
    private MakeOrderController makeOrderViewController;
    @FXML
    private AddUserController addUserController;
    @FXML
    private AddSupplierController addSupplierViewController;
    @FXML
    private SettingsController settingsViewController;
    @FXML
    private AddItemController addItemViewController;
    //</editor-fold>

    private List<Node> views;

    @FXML
    public void initialize() {


        // Set up the live clock in the header
        setupLiveClock();

        // Group all view nodes for easy management (show/hide)
        views = List.of(homeView, addSupplierView, makeOrderView, addUserView, settingsView, addItemView);

        // Show the home view by default when the application starts
        showView(homeView, "Dashboard");
    }

    /**
     * Handles all sidebar navigation button clicks to switch between views.
     */
    @FXML
    void handleNavigation(ActionEvent event) {
        Object source = event.getSource();

        if (source == homeBtn) {
            showView(homeView, "Dashboard");
            homeViewController.refreshData(); // Call a public method on the specific controller
        } else if (source == makeOrderBtn) {
            showView(makeOrderView, "Make New Order");
            makeOrderViewController.initializeData(); // Prepare the order form
        } else if (source == addUserBtn) {
            showView(addUserView, "Add New User");
//            addUserController.clearFields(); // Reset the user form
        } else if (source == addSupplierBtn) {
            showView(addSupplierView, "Add New Supplier");
//            addSupplierViewController.clearFields(); // Reset the supplier form
        } else if (source == settingsBtn) {
            showView(settingsView, "Settings");
            // No specific action needed for the settings placeholder view
        } else if (source == addItemBtn) {
            showView(addItemView, "Add New Item");
            addItemViewController.initializeData(); // Prepare the item form
        }
    }

    /**
     * Handles the logout process.
     */
    @FXML
    void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Authentication_form.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Clothify - Login");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            // Close the current dashboard window
            ((Stage) logoutBtn.getScene().getWindow()).close();

        } catch (IOException e) {
            alert(javafx.scene.control.Alert.AlertType.ERROR, "Logout Error", "Could not load the login screen.");
            e.printStackTrace();
        }
    }

    /**
     * A helper method to make the requested view visible and hide all others.
     *
     * @param viewToShow The Node (the root of an included FXML) to display.
     * @param title      The title to set in the header bar.
     */
    private void showView(Node viewToShow, String title) {
        titleLabel.setText(title);
        views.forEach(view -> view.setVisible(false)); // Hide all views
        viewToShow.setVisible(true); // Show the target view
        viewToShow.toFront(); // Bring it to the front of the StackPane
    }

    /**
     * Sets up a timeline to update the date and time label every second.
     */
    private void setupLiveClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, h:mm:ss a");
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            dateTimeLabel.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}
