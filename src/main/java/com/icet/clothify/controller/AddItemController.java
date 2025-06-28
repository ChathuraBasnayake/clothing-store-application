package com.icet.clothify.controller;

import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.model.dto.SupplierDTO;
import com.icet.clothify.service.ServiceFactory;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.service.custom.SupplierService;
import com.icet.clothify.util.ServiceType;
import javafx.collections.FXCollections;
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

import static com.icet.clothify.util.Util.alert;

public class AddItemController {

    // IMPORTANT: Replace with your actual Gemini API key
    private static final String GEMINI_API_KEY = "AIzaSyAsG0NX0sjiLxOJGpF8vF96yp7IfhPx47E";

    //<editor-fold desc="FXML-Fields">
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
    //</editor-fold>

    private ItemService itemService;
    private SupplierService supplierService;

    @FXML
    public void initialize() {
        try {
            itemService = ServiceFactory.getInstance().getServiceType(ServiceType.ITEM);
            supplierService = ServiceFactory.getInstance().getServiceType(ServiceType.SUPPLIER);
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Initialization Failed", "Could not connect to services.");
            e.printStackTrace();
        }

        initializeData();
    }

    public void initializeData() {
        itemCategoryComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Kids", "Unisex"));
        itemSizeComboBox.setItems(FXCollections.observableArrayList("Small", "Medium", "Large", "XL", "XXL"));
        descriptionErrorLabel.setVisible(false);
        reloadSuppliers();
        clearFields();
    }


    @FXML
    void handleAddItemSubmit(ActionEvent event) {
        if (!itemService.validateInputsForItem(itemNameField, itemCategoryComboBox, itemSizeComboBox, supplierComboBox, itemPriceField, itemQuantityField)) {
            return;
        }

        try {
            ItemDTO newItem = new ItemDTO(
                    null, // ID is auto-generated in the database
                    itemNameField.getText(),
                    itemCategoryComboBox.getValue(),
                    itemDescriptionArea.getText(),
                    itemSizeComboBox.getValue(),
                    Double.parseDouble(itemPriceField.getText()),
                    Integer.parseInt(itemQuantityField.getText()),
                    supplierComboBox.getValue().getId()
            );

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

    @FXML
    void generateItemDescription(ActionEvent event) {
        if (GEMINI_API_KEY.equals("YOUR_API_KEY_HERE")) {
            alert(Alert.AlertType.ERROR, "API Key Missing", "Please add your Gemini API key to the AddItemController.java file.");
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

    private String createAIPrompt(String name, String category, String size, String price) {
        return String.format(
                "Generate a compelling, short (below 255 characters) marketing description for a clothing item. " +
                        "Item Name: %s, Category: %s, Size: %s, Price: LKR %s. " +
                        "The tone should be stylish and appealing. Do not include the item name in the response.", name, category, size, price);
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

    private void reloadSuppliers() {
        try {
            List<SupplierDTO> suppliers = supplierService.getAll();
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

}
