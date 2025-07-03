package com.icet.clothify.controller;

import com.google.inject.Inject;
import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.model.dto.OrderDTO;
import com.icet.clothify.model.dto.OrderItemDTO;
import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.service.custom.OrderService;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.util.EmailUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.icet.clothify.util.AlertUtil.alert;

public class MakeOrderController {

    private final ObservableList<OrderItem> currentOrderItems = FXCollections.observableArrayList();
    @FXML
    private ComboBox<ItemDTO> orderItemComboBox;
    @FXML
    private TextField orderItemQtyField;
    @FXML
    private TextField customerEmailField;
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

    @Inject
    private ItemService itemService;
    @Inject
    private UserService userService;
    @Inject
    private OrderService orderService;

    private List<ItemDTO> allItemsList = new ArrayList<>();

    @FXML
    public void initialize() {
        // === FIX APPLIED HERE ===
        // This setup tells the TableView how to populate its columns from the OrderItem class.
        colItemName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colItemQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colItemPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colItemTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Set the items for the table. The table will now automatically update
        // whenever the currentOrderItems list changes.
        orderItemsTableView.setItems(currentOrderItems);

        initializeData();
    }

    public void initializeData() {
        try {
            setupItemComboBox();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void handleAddToOrder(ActionEvent event) {
        if (orderIdField.getText().isEmpty()) {
            orderIdField.setText(orderService.generateOrderId());
        }
        ItemDTO selectedItem = orderItemComboBox.getValue();

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

        for (OrderItem item : currentOrderItems) {
            if (item.getItemId() == selectedItem.getId()) {
                item.setQty(item.getQty() + quantity);
                orderItemsTableView.refresh(); // Refresh table to show updated total
                orderService.updateOrderTotal(currentOrderItems, orderTotalLabel);
                return;
            }
        }

        currentOrderItems.add(new OrderItem(selectedItem.getId(), selectedItem.getName(), quantity, selectedItem.getPrice()));
        // No need to call updateOrderTotal here if it's handled by a listener, but keeping it is safe.
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
        if (!orderService.handlePlaceOrderValidations(currentOrderItems, orderPaymentMethodComboBox, orderEmployeeComboBox, customerEmailField))
            return;

        try {
            String orderId = orderIdField.getText();
            OrderDTO orderDTO = new OrderDTO(
                    orderId,
                    orderItemsToDTO(),
                    Double.parseDouble(orderTotalLabel.getText().replace("LKR ", "")),
                    orderPaymentMethodComboBox.getValue(), LocalDateTime.now(),
                    orderEmployeeComboBox.getValue().getId()
            );

            boolean isAdded = orderService.placeOrder(orderDTO);

            if (isAdded) {

                String recipient = customerEmailField.getText();
                String subject = "Your Clothify Order #" + orderId + " has been confirmed!";

                String body = EmailUtil.generateOrderConfirmationEmailBody(
                        orderId,
                        currentOrderItems,
                        recipient
                );

                Task<Void> sendEmailTask = EmailUtil.createSendEmailTask(recipient, subject, body);

                sendEmailTask.setOnSucceeded(event1 -> {

                    alert(Alert.AlertType.WARNING, "Email  send", "Occurs when sending Email " + customerEmailField.getText());


                });

                sendEmailTask.setOnFailed(event1 -> {

                    alert(Alert.AlertType.WARNING, "Email Not send", "Error Occurs when sending Email " + customerEmailField.getText());

                });

                new Thread(sendEmailTask).start();

                alert(Alert.AlertType.INFORMATION, "Success", "Order has been placed successfully!");
                setupItemComboBox(); // Refresh item list (stock changes)
                clearOrderForm();
            } else {
                alert(Alert.AlertType.ERROR, "Order Failed", "Could not place the order due to a system error.");
            }

        } catch (SQLException e) {
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
                    filteredItems.setPredicate(s -> true);
                } else {
                    String lowerCaseFilter = newVal.toLowerCase();
                    filteredItems.setPredicate(item -> {
                        if (item.getName().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        } else return String.valueOf(item.getId()).contains(lowerCaseFilter);
                    });
                }
            });
        });

        orderItemComboBox.setItems(filteredItems);

        orderItemComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(ItemDTO item) {
                return item == null ? "" : item.getId() + " : " + item.getName() + " (LKR " + item.getPrice() + ")";
            }

            @Override
            public ItemDTO fromString(String string) {
                return allItemsList.stream()
                        .filter(item -> toString(item).equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        orderItemComboBox.setEditable(true);

        List<UserDTO> userDTOList = userService.getAll();

        orderEmployeeComboBox.setItems(FXCollections.observableArrayList(userDTOList));

        orderEmployeeComboBox.setConverter(new StringConverter<UserDTO>() {
            @Override
            public String toString(UserDTO userDTO) {
                return userDTO.getId() + " : " + userDTO.getName();
            }

            @Override
            public UserDTO fromString(String s) {
                return userDTOList.stream()
                        .filter(item -> toString(item).equals(s))
                        .findFirst()
                        .orElse(null);
            }
        });
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
        customerEmailField.setText("");
    }

    private List<OrderItemDTO> orderItemsToDTO() {
        ModelMapper modelMapper = new ModelMapper();
        return currentOrderItems.stream()
                .map(orderItem -> modelMapper.map(orderItem, OrderItemDTO.class))
                .collect(Collectors.toList());
    }

    public void handleSearchOrder(ActionEvent actionEvent) {
    }

    public void handleProcessReturn(ActionEvent actionEvent) {
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
