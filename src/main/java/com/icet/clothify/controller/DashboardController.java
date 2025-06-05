package com.icet.clothify.controller;

import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.model.dto.SupplierDTO;
import com.icet.clothify.service.ServiceFactory;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.service.custom.SupplierService;
import com.icet.clothify.util.ServiceType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {
    ItemService itemService;
    SupplierService supplierService;
    @FXML
    private Button addItemBtn;
    @FXML
    private VBox addItemView;
    @FXML
    private Button addUserBtn;
    @FXML
    private VBox addUserView;
    @FXML
    private TextField companyField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private StackPane contentStackPane;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label descriptionErrorLabel;
    @FXML
    private TextField emailField;
    @FXML
    private Button generateDescriptionBtn;
    @FXML
    private Button homeBtn;
    @FXML
    private VBox homeView;
    @FXML
    private ComboBox itemCategoryComboBox;
    @FXML
    private TextArea itemDescriptionArea;
    @FXML
    private TextField itemNameField;
    @FXML
    private TextField itemPriceField;
    @FXML
    private TextField itemQuantityField;
    @SuppressWarnings("rawtypes")
    @FXML
    private ComboBox itemSizeComboBox;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button settingsBtn;
    @FXML
    private VBox settingsView;
    @FXML
    private ComboBox supplierComboBox;
    @FXML
    private TextField userIdField;
    @FXML
    private TextField userNameField;

    {
        try {
            itemService = ServiceFactory.getInstance().getServiceType(ServiceType.ITEM);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    {
        try {
            supplierService = ServiceFactory.getInstance().getServiceType(ServiceType.SUPPLIER);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @FXML
    void generateItemDescription(ActionEvent event) {

    }

    @FXML
    void handleAddItemSubmit(ActionEvent event) {

        try {
            itemService.add(new ItemDTO(null, itemNameField.getText(), itemCategoryComboBox.getValue().toString(), itemSizeComboBox.getValue().toString(), Double.parseDouble(itemPriceField.getText()), Integer.parseInt(itemQuantityField.getText()), Integer.parseInt(supplierComboBox.getValue().toString())));

            alert("Success", "Item saved to the inventory SuccesFully");

        } catch (SQLException e) {

            alert("Unsuccessful", "Item is not saved to the inventory try Again");

        }

//        Clear Fields -----------------------------------------

        List.of(itemNameField, itemPriceField, itemDescriptionArea, itemQuantityField, itemPriceField).forEach(
                TextInputControl::clear
        );
        List.of(itemCategoryComboBox, supplierComboBox, supplierComboBox).forEach(comboBox -> comboBox.getSelectionModel().clearSelection()
        );


    }

    private void alert(String alertTile, String alertDescription) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(alertTile);
        alert.setHeaderText(null); // You can set a header text or leave it null
        alert.setContentText(alertDescription);

        alert.showAndWait();

    }

    @FXML
    void handleNavigation(ActionEvent event) {

        List.of(homeView, addUserView, addItemView, settingsView).forEach(each -> {
            each.setVisible(false);
        });

        Object source = event.getSource();

        if (source == homeBtn) homeView.setVisible(true);
        else if (source == addUserBtn) addUserView.setVisible(true);
        else if (source == settingsBtn) settingsView.setVisible(true);
        else if (source == addItemBtn) addItemView.setVisible(true);

    }


    @SuppressWarnings("CallToPrintStackTrace")
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

//        Date and Time -----------------------------

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy h:mm a");

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            dateTimeLabel.setText(now.format(formatter));
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Animation.INDEFINITE);

        clock.play();
//-------------------------------------------------------

//        Load comboBoxes -------------------------------

        //noinspection unchecked
        itemCategoryComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Kids", "Unisex"));
        //noinspection unchecked
        itemSizeComboBox.setItems((FXCollections.observableArrayList("Small", "Medium", "Large", "XL", "XXL")));

        try {
            System.out.println(supplierService.getAll());
            //noinspection unchecked
            supplierComboBox.setItems(FXCollections.observableArrayList(supplierService.getAll().stream().map(SupplierDTO::getSupplier_id).collect(Collectors.<String>toList())));
        } catch (SQLException e) {
        e.printStackTrace();
        }

    }
}
