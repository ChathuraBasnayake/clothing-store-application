package com.icet.clothify.controller;

import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.model.dto.OrderDTO;
import com.icet.clothify.model.dto.SupplierDTO;
import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.ServiceFactory;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.service.custom.OrderService;
import com.icet.clothify.service.custom.SupplierService;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.util.ServiceType;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    private static final String GEMINI_API_KEY = "AIzaSyAsG0NX0sjiLxOJGpF8vF96yp7IfhPx47E";

    private ItemService itemService;
    private SupplierService supplierService;
    private final ObservableList<OrderItem> currentOrderItems = FXCollections.observableArrayList();
    private UserService userService;
    private OrderService orderService;
    private List<ItemDTO> allItemsList = new ArrayList<>();

    // Common FXML Fields
    @FXML
    private StackPane contentStackPane;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private Button homeBtn;
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
    private Button makeOrderBtn;
    @FXML
    private VBox homeView;
    @FXML
    private VBox addUserView;
    @FXML
    private VBox addSupplierView;
    @FXML
    private VBox settingsView;
    @FXML
    private VBox addItemView;
    @FXML
    private VBox makeOrderView;

    // Add Supplier FXML Fields
    @FXML
    private TextField supplierNameField;
    @FXML
    private TextField supplierCompanyField;
    @FXML
    private TextField supplierEmailField;
    @FXML
    private TextField supplierPhoneField;

    // Home View FXML Fields
    @FXML
    private BarChart<String, Number> salesChart;
    @FXML
    private ListView<String> activityListView;

    // Add Item View FXML Fields
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

    // Add User View FXML Fields
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
    private CheckBox isEmployeeCheckBox;

    // Make Order View FXML Fields
    @FXML
    private ComboBox<ItemDTO> orderItemComboBox;
    @FXML
    private TextField orderItemQtyField;
    @FXML
    private TableView<OrderItem> orderItemsTableView;
    @FXML
    private TableColumn<OrderItem, String> colItemName;
    @FXML
    private TableColumn<OrderItem, Integer> colItemQty;
    @FXML
    private TableColumn<OrderItem, Double> colItemPrice;
    @FXML
    private TableColumn<OrderItem, Double> colItemTotal;
    @FXML
    private TextField orderIdField;
    @FXML
    private ComboBox<String> orderPaymentMethodComboBox;
    @FXML
    private ComboBox<UserDTO> orderEmployeeComboBox;
    @FXML
    private Label orderTotalLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            userService = ServiceFactory.getInstance().getServiceType(ServiceType.USER);
            orderService = ServiceFactory.getInstance().getServiceType(ServiceType.ORDER);
            itemService = ServiceFactory.getInstance().getServiceType(ServiceType.ITEM);
            supplierService = ServiceFactory.getInstance().getServiceType(ServiceType.SUPPLIER);
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Initialization Failed", "Could not connect to services. The application may not function correctly.");
            e.printStackTrace();
        }

        setupLiveClock();
        setupComboBoxes();
        setupOrderView();

        homeView.setVisible(true);
        descriptionErrorLabel.setVisible(false);
    }

    // =================================================================================
    // Navigation
    // =================================================================================
    @FXML
    void handleNavigation(ActionEvent event) {
        List.of(homeView, addUserView, addItemView, settingsView, makeOrderView, addSupplierView).forEach(view -> view.setVisible(false));

        Object source = event.getSource();

        if (source == homeBtn) {
            homeView.setVisible(true);
        } else if (source == addUserBtn) {
            addUserView.setVisible(true);
        } else if (source == settingsBtn) {
            settingsView.setVisible(true);
        } else if (source == addItemBtn) {
            addItemView.setVisible(true);
        } else if (source == makeOrderBtn) {
            makeOrderView.setVisible(true);
        } else if (source == logoutBtn) {
            handleLogout(event);
        } else if (source == addSupplierBtn) {
            addSupplierView.setVisible(true);
        }
    }

    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/Authentication_form.fxml")));
            Stage stage = new Stage();
            stage.setTitle("Clothify - Login");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            alert(Alert.AlertType.ERROR, "Logout Error", "Could not load the login screen.");
            e.printStackTrace();
        }
    }

    // =================================================================================
    // Add Supplier Logic (NEW)
    // =================================================================================

    @FXML
    void handleSaveSupplier(ActionEvent actionEvent) {
        if (!validateSupplierInputs()) {
            return;
        }

        SupplierDTO newSupplier = new SupplierDTO(
                null,
                supplierNameField.getText(),
                supplierCompanyField.getText(),
                supplierEmailField.getText(),
                supplierPhoneField.getText()
        );

        try {
            if (supplierService.add(newSupplier)) {
                alert(Alert.AlertType.INFORMATION, "Success", "New supplier has been saved successfully.");
                clearAddSupplierForm();
                reloadSuppliers(); // Refresh the supplier list in the "Add Item" form
            } else {
                alert(Alert.AlertType.ERROR, "Save Failed", "Could not save the supplier due to a system error.");
            }
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Failed to save the supplier to the database.");
            e.printStackTrace();
        }
    }

    private boolean validateSupplierInputs() {
        if (supplierNameField.getText().trim().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Supplier Name cannot be empty.");
            return false;
        }
        if (supplierCompanyField.getText().trim().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Company Name cannot be empty.");
            return false;
        }
        String email = supplierEmailField.getText().trim();
        if (email.isEmpty() || !email.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$")) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            return false;
        }
        return true;
    }

    private void clearAddSupplierForm() {
        supplierNameField.clear();
        supplierCompanyField.clear();
        supplierEmailField.clear();
        supplierPhoneField.clear();
    }

    private void reloadSuppliers() {
        try {
            List<SupplierDTO> suppliers = supplierService.getAll();
            supplierComboBox.setItems(FXCollections.observableArrayList(suppliers));
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Data Reload Error", "Could not reload the supplier list.");
            e.printStackTrace();
        }
    }

    // Make Order View Logic

    private void setupOrderView() {
        colItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colItemQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colItemPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colItemTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        orderItemsTableView.setItems(currentOrderItems);

        orderPaymentMethodComboBox.setItems(FXCollections.observableArrayList("Cash", "Card", "Online Transfer"));

        try {
            allItemsList = itemService.getAll();
            orderItemComboBox.setItems(FXCollections.observableArrayList(allItemsList));
            orderItemComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(ItemDTO item) {
                    return item == null ? "" : item.getName() + " (LKR " + item.getPrice() + ")";
                }

                @Override
                public ItemDTO fromString(String string) {
                    return allItemsList.stream()
                            .filter(item -> (item.getName() + " (LKR " + item.getPrice() + ")").equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });

            orderItemComboBox.setEditable(true);
            orderItemComboBox.getEditor().textProperty().addListener((obs, oldText, newText) -> {
                String selectedText = orderItemComboBox.getConverter().toString(orderItemComboBox.getValue());
                if (newText != null && newText.equals(selectedText)) {
                    return;
                }

                if (newText == null || newText.isEmpty()) {
                    orderItemComboBox.setItems(FXCollections.observableArrayList(allItemsList));
                    orderItemComboBox.hide();
                } else {
                    List<ItemDTO> filteredList = allItemsList.stream()
                            .filter(item -> item.getName().toLowerCase().contains(newText.toLowerCase()))
                            .collect(Collectors.toList());
                    orderItemComboBox.setItems(FXCollections.observableArrayList(filteredList));
                    orderItemComboBox.show();
                }
            });

            List<UserDTO> allUsers = userService.getAll();
            orderEmployeeComboBox.setItems(FXCollections.observableArrayList(allUsers));
            orderEmployeeComboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(UserDTO user) {
                    return user == null ? "" : user.getName();
                }

                @Override
                public UserDTO fromString(String string) {
                    return null;
                }
            });

        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Data Loading Error", "Could not load data for the order screen.");
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddToOrder(ActionEvent event) {
        ItemDTO selectedItem = orderItemComboBox.getValue();
        if (selectedItem == null) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please select an item to add.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(orderItemQtyField.getText());
            if (quantity <= 0) {
                alert(Alert.AlertType.WARNING, "Validation Error", "Quantity must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid quantity.");
            return;
        }

        for (OrderItem item : currentOrderItems) {
            if (item.getItemId() == selectedItem.getId()) {
                item.setQty(item.getQty() + quantity);
                orderItemsTableView.refresh();
                updateOrderTotal();
                return;
            }
        }

        currentOrderItems.add(new OrderItem(selectedItem.getId(), selectedItem.getName(), quantity, selectedItem.getPrice()));
        updateOrderTotal();
    }

    @FXML
    void handleRemoveSelectedItem(ActionEvent event) {
        OrderItem selected = orderItemsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentOrderItems.remove(selected);
            updateOrderTotal();
        } else {
            alert(Alert.AlertType.WARNING, "Selection Error", "Please select an item from the table to remove.");
        }
    }

    private void updateOrderTotal() {
        double total = currentOrderItems.stream()
                .mapToDouble(OrderItem::getTotal)
                .sum();
        orderTotalLabel.setText(String.format("LKR %.2f", total));
    }

    @FXML
    void handlePlaceOrder(ActionEvent event) {
        if (currentOrderItems.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Cannot place an empty order. Please add items.");
            return;
        }
        if (orderPaymentMethodComboBox.getValue() == null) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please select a payment method.");
            return;
        }
        if (orderEmployeeComboBox.getValue() == null) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please select a processing employee.");
            return;
        }

        try {

            String orderId = generateUniqueOrderId();

            Double total = Double.parseDouble(orderTotalLabel.getText().replace("LKR ", ""));
            String paymentMethod = orderPaymentMethodComboBox.getValue();
            Integer employeeId = orderEmployeeComboBox.getValue().getId();

            OrderDTO newOrder = new OrderDTO(orderId, new ArrayList<>(currentOrderItems), total, paymentMethod, employeeId.toString());

            boolean isAdded = orderService.placeOrder(newOrder);
            if (isAdded) {
                alert(Alert.AlertType.INFORMATION, "Success", "Order has been placed successfully!");
                clearOrderForm();
            } else {
                alert(Alert.AlertType.ERROR, "Order Failed", "Could not place the order due to a system error.");
            }

        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Failed to save the order to the database.");
            e.printStackTrace();
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred while placing the order.");
            e.printStackTrace();
        }
    }

    private String generateUniqueOrderId() {
        // Generates an ID like "ORD-20250619143055"
        return "ORD-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    private void clearOrderForm() {
        currentOrderItems.clear();
        orderItemComboBox.getSelectionModel().clearSelection();
        orderItemQtyField.setText("1");
        orderPaymentMethodComboBox.getSelectionModel().clearSelection();
        orderEmployeeComboBox.getSelectionModel().clearSelection();
        updateOrderTotal();
    }

    // =================================================================================
    // Other Logic (Add Item, Add User, etc.)
    // =================================================================================
    @FXML
    void handleAddItemSubmit(ActionEvent event) {
        if (!validateInputsForItem()) {
            return;
        }

        try {
            ItemDTO newItem = new ItemDTO(
                    null,
                    itemNameField.getText(),
                    itemCategoryComboBox.getValue(),
                    itemSizeComboBox.getValue(),
                    Double.parseDouble(itemPriceField.getText()),
                    Integer.parseInt(itemQuantityField.getText()),
                    Integer.parseInt(supplierComboBox.getValue().getId())

            );

            if (itemService.add(newItem)) {
                alert(Alert.AlertType.INFORMATION, "Success", "Item saved to the inventory successfully.");
                clearAddItemForm();
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

    private boolean validateInputsForItem() {
        if (itemNameField.getText().trim().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Item Name cannot be empty.");
            return false;
        }
        if (itemCategoryComboBox.getValue() == null) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please select an Item Category.");
            return false;
        }
        if (itemSizeComboBox.getValue() == null) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please select an Item Size.");
            return false;
        }
        if (supplierComboBox.getValue() == null) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please select a Supplier.");
            return false;
        }
        try {
            Double.parseDouble(itemPriceField.getText());
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Item Price must be a valid number (e.g., 1500.00).");
            return false;
        }
        try {
            Integer.parseInt(itemQuantityField.getText());
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Item Quantity must be a valid whole number (e.g., 50).");
            return false;
        }
        return true;
    }

    private void clearAddItemForm() {
        List.of(itemNameField, itemPriceField, itemDescriptionArea, itemQuantityField).forEach(
                TextInputControl::clear
        );
        List.of(itemCategoryComboBox, itemSizeComboBox, supplierComboBox).forEach(
                comboBox -> comboBox.getSelectionModel().clearSelection()
        );
        descriptionErrorLabel.setVisible(false);
    }

    @FXML
    void generateItemDescription(ActionEvent event) {
        if (GEMINI_API_KEY.equals("YOUR_API_KEY")) {
            alert(Alert.AlertType.ERROR, "API Key Missing", "Please add your Gemini API key to the DashboardController.java file.");
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

        Task<String> generateTask = new Task<>() {
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
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        return parseGeminiResponse(response.toString());
                    }
                } else {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getErrorStream(), StandardCharsets.UTF_8))) {
                        StringBuilder errorResponse = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            errorResponse.append(responseLine.trim());
                        }
                        throw new IOException("API request failed with response code: " + responseCode + " " + con.getResponseMessage() + "\nDetails: " + errorResponse);
                    }
                }
            }
        };

        generateTask.setOnSucceeded(e -> {
            itemDescriptionArea.setText(generateTask.getValue());
            enableGenerateButton();
        });

        generateTask.setOnFailed(e -> {
            Throwable exception = generateTask.getException();
            alert(Alert.AlertType.ERROR, "AI Error", "Failed to generate description from the API.\n" + exception.getMessage());
            enableGenerateButton();
            exception.printStackTrace();
        });

        new Thread(generateTask).start();
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

    private void enableGenerateButton() {
        generateDescriptionBtn.setDisable(false);
        generateDescriptionBtn.setText("Generate Description");
    }

    private String createAIPrompt(String name, String category, String size, String price) {
        return String.format(
                "Generate a compelling, short (2-3 sentences) marketing description for a clothing item. " +
                        "Item Name: %s, Category: %s, Size: %s, Price: LKR %s. " +
                        "The tone should be stylish and appealing. Do not include the item name in the response.", name, category, size, price);
    }

    @FXML
    void handleCreateUser(ActionEvent actionEvent) {
        if (!validateInputsForUser()) {
            return;
        }

        try {
            UserDTO newUser = new UserDTO(
                    null,
                    userNameField.getText(),
                    companyField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    null,
                    isEmployeeCheckBox.isSelected()
            );

            boolean isAdded = userService.add(newUser);
            if (Boolean.TRUE.equals(isAdded)) {
                alert(Alert.AlertType.INFORMATION, "User Created", "User was created successfully.");
                clearAddUserForm();
            } else {
                alert(Alert.AlertType.WARNING, "Creation Failed", "User could not be created. Please check the details and try again.");
            }
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Database Error", "Failed to save the user due to a database error.");
            e.printStackTrace();
        }
    }

    private boolean validateInputsForUser() {
        if (userNameField.getText().trim().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Username cannot be empty.");
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

    private void clearAddUserForm() {
        List.of(userNameField, companyField, emailField, passwordField, confirmPasswordField).forEach(
                TextInputControl::clear
        );
        isEmployeeCheckBox.setSelected(false);
    }

    private void setupLiveClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, h:mm:ss a");
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime now = LocalDateTime.now();
            dateTimeLabel.setText(now.format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void setupComboBoxes() {
        itemCategoryComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Kids", "Unisex"));
        itemSizeComboBox.setItems(FXCollections.observableArrayList("Small", "Medium", "Large", "XL", "XXL"));

        reloadSuppliers();
    }

    private void alert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static class OrderItem {
        private final SimpleIntegerProperty itemId;
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty qty;
        private final SimpleDoubleProperty price;
        private final SimpleDoubleProperty total;

        public OrderItem(Integer itemId, String name, int quantity, double price) {
            this.itemId = new SimpleIntegerProperty(itemId);
            this.name = new SimpleStringProperty(name);
            this.qty = new SimpleIntegerProperty(quantity);
            this.price = new SimpleDoubleProperty(price);
            this.total = new SimpleDoubleProperty(quantity * price);
        }

        public int getItemId() {
            return itemId.get();
        }

        public String getName() {
            return name.get();
        }

        public int getQty() {
            return qty.get();
        }

        public void setQty(int qty) {
            this.qty.set(qty);
            this.total.set(qty * this.price.get());
        }

        public double getPrice() {
            return price.get();
        }

        public double getTotal() {
            return total.get();
        }
    }
}