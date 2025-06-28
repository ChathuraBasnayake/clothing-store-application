package com.icet.clothify.controller;

import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.model.dto.OrderDTO;
import com.icet.clothify.model.dto.OrderItemDTO;
import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.ServiceFactory;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.service.custom.OrderService;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.util.ServiceType;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.icet.clothify.util.Util.alert;

public class MakeOrderController {

    private final ObservableList<OrderItem> currentOrderItems = FXCollections.observableArrayList();
    //<editor-fold desc="FXML-Fields">
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
    //</editor-fold>
    @FXML
    private Label orderTotalLabel;
    private ItemService itemService;
    private UserService userService;
    private OrderService orderService;
    private List<ItemDTO> allItemsList = new ArrayList<>();

    @FXML
    public void initialize() {
        // Initialize services
        try {
            itemService = ServiceFactory.getInstance().getServiceType(ServiceType.ITEM);
            userService = ServiceFactory.getInstance().getServiceType(ServiceType.USER);
            orderService = ServiceFactory.getInstance().getServiceType(ServiceType.ORDER);
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Initialization Failed", "Could not connect to services.");
            e.printStackTrace();
            return;
        }

        initializeData();
    }

    /**
     * Public method to load or reload data for this view.
     */
    public void initializeData() {
        // Setup table columns
        colItemName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colItemQty.setCellValueFactory(cellData -> cellData.getValue().qtyProperty().asObject());
        colItemPrice.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());
        colItemTotal.setCellValueFactory(cellData -> cellData.getValue().totalProperty().asObject());
        orderItemsTableView.setItems(currentOrderItems);

        // Populate combo boxes
        orderPaymentMethodComboBox.setItems(FXCollections.observableArrayList("Cash", "Card", "Online Transfer"));

        try {
            // Load employees (users)
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

            // Setup the searchable item combo box
            setupItemComboBox();

        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Data Loading Error", "Could not load data for the order screen.");
            e.printStackTrace();
        }

        // Set a new order ID and clear the form
        clearOrderForm();
    }


    @FXML
    void handleAddToOrder(ActionEvent event) {
        ItemDTO selectedItem = orderItemComboBox.getValue();

        // FIX: Add a null check before calling the service layer.
        if (selectedItem == null) {
            alert(Alert.AlertType.WARNING, "Selection Error", "Please select an item from the list before adding.");
            return;
        }

        if (!orderService.validateAddToTheOrder(selectedItem, orderItemQtyField)) return;

        int quantity;
        try {
            quantity = Integer.parseInt(orderItemQtyField.getText());
        } catch (NumberFormatException e) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid quantity.");
            return;
        }

        // Check if the item is already in the cart to update quantity
        for (OrderItem item : currentOrderItems) {
            if (item.getItemId() == selectedItem.getId()) {
                item.setQty(item.getQty() + quantity);
                orderItemsTableView.refresh(); // Refresh table to show updated total
                orderService.updateOrderTotal(currentOrderItems, orderTotalLabel);
                return;
            }
        }

        // Add as a new item to the cart
        currentOrderItems.add(new OrderItem(selectedItem.getId(), selectedItem.getName(), quantity, selectedItem.getPrice()));
        orderService.updateOrderTotal(currentOrderItems, orderTotalLabel);

    }

    @FXML
    void handleRemoveSelectedItem(ActionEvent event) {
        OrderItem selected = orderItemsTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            currentOrderItems.remove(selected);
            orderService.updateOrderTotal(currentOrderItems, orderTotalLabel);
        } else {
            alert(Alert.AlertType.WARNING, "Selection Error", "Please select an item from the table to remove.");
        }
    }

    @FXML
    void handlePlaceOrder(ActionEvent event) {
        if (!orderService.handlePlaceOrderValidations(currentOrderItems, orderPaymentMethodComboBox, orderEmployeeComboBox))
            return;

        try {
            String orderId = orderIdField.getText();
            OrderDTO orderDTO = new OrderDTO(
                    orderId,
                    orderItemsToDTO(),
                    Double.parseDouble(orderTotalLabel.getText().replace("LKR ", "")),
                    orderPaymentMethodComboBox.getValue(),
                    orderEmployeeComboBox.getValue().getId()
            );

            boolean isAdded = orderService.placeOrder(orderDTO);

            if (isAdded) {
                alert(Alert.AlertType.INFORMATION, "Success", "Order has been placed successfully!");
                setupItemComboBox(); // Refresh item list (stock changes)
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


    private void setupItemComboBox() throws SQLException {
        // 1. Fetch the master list of all available items (quantity > 0)
        allItemsList = itemService.getAll().stream()
                .filter(itemDTO -> itemDTO.getQuantity() > 0)
                .toList();
        ObservableList<ItemDTO> observableItems = FXCollections.observableArrayList(allItemsList);

        FilteredList<ItemDTO> filteredItems = new FilteredList<>(observableItems, itemDTO -> true);

        orderItemComboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                final ItemDTO selectedItem = orderItemComboBox.getSelectionModel().getSelectedItem();
                if (selectedItem != null && newVal.equals(orderItemComboBox.getConverter().toString(selectedItem))) {
                    return;
                }

                if (newVal == null || newVal.isEmpty()) {
                    filteredItems.setPredicate(s -> true); // If empty, show all items
                } else {
                    String lowerCaseFilter = newVal.toLowerCase();
                    filteredItems.setPredicate(item -> {
                        // Show item if its name or ID contains the filter text
                        if (item.getName().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        } else return String.valueOf(item.getId()).contains(lowerCaseFilter);
                    });
                }
            });
        });

        // 4. Set the ComboBox's items to be the FilteredList
        orderItemComboBox.setItems(filteredItems);

        // 5. Use a clear and correct StringConverter
        orderItemComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ItemDTO item) {
                // Return an empty string for null values. This prevents errors and "Items Not Found" text.
                return item == null ? "" : item.getId() + " : " + item.getName() + " (LKR " + item.getPrice() + ")";
            }

            @Override
            public ItemDTO fromString(String string) {
                // This is used to convert the text back to an item. It's mainly for when a selection is made.
                // It will return null if the text doesn't exactly match an item, which is fine.
                return allItemsList.stream()
                        .filter(item -> toString(item).equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        orderItemComboBox.setEditable(true);
    }

    private void clearOrderForm() {
        orderIdField.setText(orderService.generateOrderId());
        currentOrderItems.clear();
        orderItemComboBox.getSelectionModel().clearSelection();
        orderItemComboBox.getEditor().clear();
        orderItemQtyField.setText("1");
        orderPaymentMethodComboBox.getSelectionModel().clearSelection();
        orderEmployeeComboBox.getSelectionModel().clearSelection();
        orderService.updateOrderTotal(currentOrderItems, orderTotalLabel);
    }

    private List<OrderItemDTO> orderItemsToDTO() {
        ModelMapper modelMapper = new ModelMapper();
        return currentOrderItems.stream()
                .map(orderItem -> modelMapper.map(orderItem, OrderItemDTO.class))
                .collect(Collectors.toList());
    }


    /**
     * Inner class representing an item within an order, for display in the TableView.
     * Uses JavaFX properties for automatic table updates.
     */
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

        public SimpleStringProperty nameProperty() {
            return name;
        }

        public SimpleIntegerProperty qtyProperty() {
            return qty;
        }

        public SimpleDoubleProperty priceProperty() {
            return price;
        }

        public SimpleDoubleProperty totalProperty() {
            return total;
        }
    }
}
