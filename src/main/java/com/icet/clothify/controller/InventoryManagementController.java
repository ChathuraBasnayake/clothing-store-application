package com.icet.clothify.controller;

import com.google.inject.Inject;
import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.model.dto.SupplierDTO;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.service.custom.SupplierService;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import static com.icet.clothify.util.AlertUtil.alert;

public class InventoryManagementController {

    private static final Dotenv dotenv = Dotenv.load();
    private static final String GEMINI_API_KEY = dotenv.get("GEMINI_API_KEY");

    @FXML
    private TextField itemNameField;
    @FXML
    private ComboBox<String> itemCategoryComboBox;
    @FXML
    private ComboBox<String> itemSizeComboBox;
    @FXML
    private TextField itemPriceField;
    @FXML
    private TextField itemQuantityField;
    @FXML
    private ComboBox<SupplierDTO> supplierComboBox;
    @FXML
    private TextArea itemDescriptionArea;
    @FXML
    private Button generateDescriptionBtn;
    @FXML
    private Label descriptionErrorLabel;
    List<SupplierDTO> suppliers;
    @FXML
    private Label currentSupplierLabel;
    @FXML
    private ComboBox<ItemDTO> updateItemComboBox;
    @FXML
    private TextField updateItemIdField;
    @FXML
    private TextField updateItemNameField;
    @FXML
    private TextField updateItemPriceField;
    @FXML
    private TextField updateItemQuantityField;
    @FXML
    private ComboBox<String> updateItemCategoryComboBox;
    @FXML
    private ComboBox<String> updateItemSizeComboBox;
    @FXML
    private ComboBox<SupplierDTO> updateSupplierComboBox;
    @FXML
    private ComboBox<ItemDTO> deleteItemComboBox;

    @Inject
    private ItemService itemService;
    @Inject
    private SupplierService supplierService;

    @FXML
    public void initialize() {
        initializeData();
        setItemCategoryComboBox();
    }

    public void initializeData() {
        itemCategoryComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Kids", "Unisex"));
        itemSizeComboBox.setItems(FXCollections.observableArrayList("Small", "Medium", "Large", "XL", "XXL"));
        descriptionErrorLabel.setText("Fill in Name, Category, Size, and Price first.");
        reloadSuppliers();
        clearFields();
    }

    private Task<String> createAIGenerationTask(String prompt) {
        return new Task<>() {
            @Override
            protected String call() throws Exception {
                String model = "gemini-1.5-flash-latest";
                URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + GEMINI_API_KEY);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                String jsonPayload = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt.replace("\"", "\\\"") + "\"}]}]}";

                try (OutputStream os = con.getOutputStream()) {
                    os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) response.append(line);
                        return parseGeminiResponse(response.toString());
                    }
                } else {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) errorResponse.append(line);
                        throw new IOException("API request failed: " + responseCode + "\nDetails: " + errorResponse);
                    }
                }
            }
        };
    }


    public void clearFields() {
        List.of(itemNameField, itemPriceField, itemDescriptionArea, itemQuantityField).forEach(TextInputControl::clear);
        List.of(itemCategoryComboBox, itemSizeComboBox, supplierComboBox).forEach(cb -> cb.getSelectionModel().clearSelection());
        descriptionErrorLabel.setVisible(false);
    }

    @FXML
    void generateItemDescription(ActionEvent event) {
        if (GEMINI_API_KEY.equals("YOUR_API_KEY_HERE")) {
            alert(Alert.AlertType.ERROR, "API Key Missing", "Please add your Gemini API key to the InventoryManagementController.java file.");
            return;
        }

        String name = itemNameField.getText();
        String category = itemCategoryComboBox.getValue();
        String size = itemSizeComboBox.getValue();
        String price = itemPriceField.getText();

        if (name.isEmpty() || category == null || size == null || price.isEmpty()) {
            descriptionErrorLabel.setText("Fill in Name, Category, Size, and Price first.");
            descriptionErrorLabel.setVisible(true);
            return;
        }

        generateDescriptionBtn.setDisable(true);
        generateDescriptionBtn.setText("Generating...");
        descriptionErrorLabel.setVisible(false);
        itemDescriptionArea.clear();

        String prompt = createAIPrompt(name, category, size, price);

        Task<String> generateTask = createAIGenerationTask(prompt);

        generateTask.setOnSucceeded(e -> {
            itemDescriptionArea.setText(generateTask.getValue());
            enableGenerateButton();
        });

        generateTask.setOnFailed(e -> {
            Throwable exception = generateTask.getException();
            alert(Alert.AlertType.ERROR, "AI Error", "Failed to generate description.\n" + exception.getMessage());
            enableGenerateButton();
            exception.printStackTrace();
        });

        new Thread(generateTask).start();
    }

    private void enableGenerateButton() {
        generateDescriptionBtn.setDisable(false);
        generateDescriptionBtn.setText("Generate with AI ✨");
    }

    private String parseGeminiResponse(String response) {
        try {
            String searchText = "\"text\": \"";
            int start = response.indexOf(searchText) + searchText.length();
            int end = response.indexOf("\"", start);
            return response.substring(start, end).replace("\\n", "\n").replace("\\\"", "\"");
        } catch (Exception e) {
            System.err.println("Failed to parse Gemini AI response: " + response);
            return "Error: Could not parse AI response.";
        }
    }

    private String createAIPrompt(String name, String category, String size, String price) {
        return String.format("Generate a compelling, short (below 255 characters) marketing description for a clothing item. " + "Item Name: %s, Category: %s, Size: %s, Price: LKR %s. " + "The tone should be stylish and appealing. Do not include the item name in the response.", name, category, size, price);
    }

    private void reloadSuppliers() {
        try {
            suppliers = supplierService.getAll();
            supplierComboBox.setItems(FXCollections.observableArrayList(suppliers));
            supplierComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(SupplierDTO supplierDTO) {
                    return supplierDTO == null ? "" : supplierDTO.getId() + " : " + supplierDTO.getName();
                }

                @Override
                public SupplierDTO fromString(String s) {
                    return null;
                }
            });
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Data Reload Error", "Could not reload the supplier list.");
            e.printStackTrace();
        }
    }

    public void handleSave(ActionEvent actionEvent) {
        if (!itemService.validateInputsForItem(itemNameField, itemCategoryComboBox, itemSizeComboBox, supplierComboBox, itemPriceField, itemQuantityField)) {
            return;
        }

        try {
            ItemDTO newItem = new ItemDTO(null, // ID is auto-generated in the database
                    itemNameField.getText(), itemCategoryComboBox.getValue(), itemDescriptionArea.getText(), itemSizeComboBox.getValue(), Double.parseDouble(itemPriceField.getText()), Integer.parseInt(itemQuantityField.getText()), supplierComboBox.getValue().getId());

            if (itemService.add(newItem)) {
                alert(Alert.AlertType.INFORMATION, "Success", "Item saved to the inventory successfully.");
                clearFields();
            } else {
                alert(Alert.AlertType.WARNING, "Save Failed", "Could not save the item due to a system error.");
            }
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Failed to save the item. Please try again.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.ERROR, "Invalid Number Format", "Price and Quantity must be valid numbers.");
        }


    }

    public void handleUpdate(ActionEvent actionEvent) {

        if (!itemService.validateInputsForItem(updateItemNameField, updateItemCategoryComboBox, updateItemSizeComboBox, updateSupplierComboBox, updateItemPriceField, updateItemQuantityField)) {
            return;
        }


        try {

            itemService.update(new ItemDTO(Integer.parseInt(updateItemIdField.getText()), updateItemNameField.getText(), updateItemCategoryComboBox.getValue().toString(), null, updateItemSizeComboBox.getValue().toString(), Double.parseDouble(updateItemPriceField.getText()), Integer.parseInt(updateItemQuantityField.getText()), updateSupplierComboBox.getValue().getId()));

            alert(Alert.AlertType.INFORMATION, "Updated", "Updated successfully");

            setItemCategoryComboBox();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void handleDelete(ActionEvent actionEvent) {

        try {
            String id = String.valueOf(deleteItemComboBox.getValue().getId());
            itemService.delete(id);

            alert(Alert.AlertType.INFORMATION, "Removed Successfully", "Item Id " + id + " has been removed successfully");

            setItemCategoryComboBox();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void setItemCategoryComboBox() {

        List<ComboBox<ItemDTO>> comboBoxes = List.of(deleteItemComboBox, updateItemComboBox);

        List<ItemDTO> all;
        try {
            all = itemService.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<ItemDTO> itemDTOS = FXCollections.observableArrayList(all);

        comboBoxes.forEach(comboBox -> {
            comboBox.setItems(itemDTOS);

            comboBox.setConverter(new StringConverter() {
                @Override
                public String toString(Object o) {
                    ItemDTO itemDTO = (ItemDTO) o;
                    return itemDTO.getId() + " : " + itemDTO.getName() + " - " + itemDTO.getCategory();
                }

                @Override
                public ItemDTO fromString(String s) {

                    ItemDTO itemDTO = all.stream().filter(item -> toString(item).equals(s)).findFirst().orElse(null);
                    return itemDTO;
                }
            });
        });

        updateSupplierComboBox.setItems(FXCollections.observableArrayList(suppliers));
        updateSupplierComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(SupplierDTO supplierDTO) {
                return supplierDTO == null ? "" : supplierDTO.getId() + " : " + supplierDTO.getName();
            }

            @Override
            public SupplierDTO fromString(String s) {
                return null;
            }
        });

        updateItemComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateItemIdField.setText(String.valueOf(newVal.getId()));
                updateItemNameField.setText(newVal.getName());
                updateItemPriceField.setText(String.valueOf(newVal.getPrice()));
                updateItemQuantityField.setText(String.valueOf(newVal.getQuantity()));
                updateItemCategoryComboBox.setValue(newVal.getCategory());
                updateItemSizeComboBox.setValue(newVal.getSize());
            }
        });
    }
}
