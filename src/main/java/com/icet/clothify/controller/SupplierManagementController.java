package com.icet.clothify.controller;

import com.google.inject.Inject;
import com.icet.clothify.model.dto.SupplierDTO;
import com.icet.clothify.service.custom.SupplierService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.util.StringConverter;

import java.sql.SQLException;
import java.util.List;

import static com.icet.clothify.util.AlertUtil.alert;

public class SupplierManagementController {

    @FXML
    private TextField supplierNameField;
    @FXML
    private TextField updateSupplierIdField;
    @FXML
    private TextField supplierCompanyField;
    @FXML
    private TextField supplierEmailField;
    @FXML
    private TextField supplierPhoneField;

    // FXML fields from the "Update Supplier" Tab
    @FXML
    private ComboBox<SupplierDTO> updateSupplierComboBox;
    @FXML
    private TextField updateSupplierNameField;
    @FXML
    private TextField updateSupplierCompanyField;
    @FXML
    private TextField updateSupplierEmailField;
    @FXML
    private TextField updateSupplierPhoneField;

    // FXML fields from the "Delete Supplier" Tab
    @FXML
    private ComboBox<SupplierDTO> deleteSupplierComboBox;

    @Inject
    private SupplierService supplierService;

    @FXML
    public void initialize() {
        setUpdateSupplierComboBox();
    }

    @FXML
    void handleSaveSupplier(ActionEvent actionEvent) {
        if (!supplierService.validateSupplierInputs(supplierNameField.getText(), supplierCompanyField.getText(), supplierEmailField.getText())) {
            return;
        }

        try {
            SupplierDTO newSupplier = new SupplierDTO(null, supplierNameField.getText(), supplierCompanyField.getText(), supplierEmailField.getText(), supplierPhoneField.getText());

            if (supplierService.add(newSupplier)) {
                alert(Alert.AlertType.INFORMATION, "Success", "New supplier has been saved successfully.");
                setUpdateSupplierComboBox();
                clearFields();
            } else {
                alert(Alert.AlertType.ERROR, "Save Failed", "Could not save the supplier due to a system error.");
            }
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Failed to save the supplier to the database.");
            e.printStackTrace();
        }
    }

    public void clearFields() {
        List<TextField> fields = List.of(supplierNameField, supplierCompanyField, supplierEmailField, supplierPhoneField, updateSupplierIdField);
        fields.forEach(TextInputControl::clear);
    }

    public void handleUpdateSupplier(ActionEvent actionEvent) {
        if (!supplierService.validateSupplierInputs(updateSupplierNameField.getText(), updateSupplierCompanyField.getText(), updateSupplierEmailField.getText())) {
            return;
        }
        try {
            SupplierDTO newSupplier = new SupplierDTO(Integer.parseInt(updateSupplierIdField.getText()), updateSupplierNameField.getText(), updateSupplierCompanyField.getText(), updateSupplierEmailField.getText(), updateSupplierPhoneField.getText());

            if (supplierService.update(newSupplier)) {
                alert(Alert.AlertType.INFORMATION, "Success", " supplier has been updated successfully.");
                setUpdateSupplierComboBox();
                clearFields();
            } else {
                alert(Alert.AlertType.ERROR, "Save Failed", "Could not update the supplier due to a system error.");
            }
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Failed to Update the supplier to the database.");
            e.printStackTrace();
        }
    }

    public void handleDeleteSupplier(ActionEvent actionEvent) {

        try {
            boolean delete = supplierService.delete(deleteSupplierComboBox.getValue().getId().toString());
            if (delete) {
                alert(Alert.AlertType.INFORMATION, "Success", " supplier deleted successfully.");
            }

        } catch (SQLException e) {
            alert(Alert.AlertType.WARNING, "DataBase Error", "Error Occurred when Deleting Supplier from database!");
            throw new RuntimeException(e);
        }

        setUpdateSupplierComboBox();
    }

    public void setUpdateSupplierComboBox() {

        List<ComboBox<SupplierDTO>> supplierComboBoxes = List.of(deleteSupplierComboBox, updateSupplierComboBox);

        List<SupplierDTO> all;
        try {
            all = supplierService.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<SupplierDTO> supplierDTOS = FXCollections.observableArrayList(all);

        supplierComboBoxes.forEach(comboBox -> {
            comboBox.setItems(supplierDTOS);

            comboBox.setConverter(new StringConverter() {
                @Override
                public String toString(Object o) {
                    SupplierDTO supplierDTO = (SupplierDTO) o;
                    return supplierDTO.getId() + " : " + supplierDTO.getName();
                }

                @Override
                public Object fromString(String s) {
                    return all.stream().filter(item -> toString(item).equals(s)).findFirst().orElse(null);
                }
            });
        });


        updateSupplierComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateSupplierNameField.setText(newVal.getName());
                updateSupplierCompanyField.setText(newVal.getCompany());
                updateSupplierEmailField.setText(newVal.getEmail());
                updateSupplierPhoneField.setText(newVal.getMobileNO());
                updateSupplierIdField.setText(newVal.getId().toString());
            }
        });

    }
}
