package com.icet.clothify.service.custom.impl;

import com.google.inject.Inject;
import com.icet.clothify.controller.MakeOrderController;
import com.icet.clothify.model.dao.OrderDAO;
import com.icet.clothify.model.dao.OrderItemDAO;
import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.model.dto.OrderDTO;
import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.repository.custom.OrderRepository;
import com.icet.clothify.service.custom.OrderService;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.icet.clothify.util.AlertUtil.alert;

public class OrderServiceImpl implements OrderService {

    @Inject
    OrderRepository orderRepository;

    ModelMapper modelMapper = new ModelMapper();

    public OrderServiceImpl() throws SQLException {
    }

    @Override
    public boolean placeOrder(OrderDTO orderDTO) throws SQLException {
        OrderDAO orderDAO = modelMapper.map(orderDTO, OrderDAO.class);

        if (orderDAO.getOrderItems() != null) {
            for (OrderItemDAO item : orderDAO.getOrderItems()) {
                item.setOrder(orderDAO); // important for Hibernate
                item.setId(null);
            }
        }


        return orderRepository.add(orderDAO);
    }

    @Override
    public List<OrderDTO> getAll() throws SQLException {
        return orderRepository.getAll()
                .stream()
                .map(orderDAO -> modelMapper.map(orderDAO, OrderDTO.class))
                .toList();
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return orderRepository.delete(id);
    }

    @Override
    public OrderDTO searchById(String id) throws SQLException {
        return modelMapper.map(orderRepository.searchById(id), OrderDTO.class);
    }

    public String generateOrderId() {
        // ORD-20250619143055
        return "ORD-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    public boolean validateAddToTheOrder(ItemDTO selectedItem, TextField orderItemQtyField) {
        if (selectedItem.getQuantity() < Integer.parseInt(orderItemQtyField.getText())) {
            alert(Alert.AlertType.WARNING, "Quantity Not Found", "Only " + selectedItem.getQuantity() + " pieces are available in the stock");
            return false;
        }
        if (selectedItem == null) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please select an item to add.");
            return false;
        }

        return true;
    }


    public void updateOrderTotal(ObservableList<MakeOrderController.OrderItem> currentOrderItems, Label orderTotalLabel) {
        double total = currentOrderItems.stream().mapToDouble(MakeOrderController.OrderItem::getTotal).sum();

        orderTotalLabel.setText(String.format("LKR %.2f", total));
    }

    @Override
    public boolean handlePlaceOrderValidations(ObservableList<MakeOrderController.OrderItem> currentOrderItems, ComboBox<String> orderPaymentMethodComboBox, ComboBox<UserDTO> orderEmployeeComboBox, TextField customerEmailField) {
        if (currentOrderItems.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Cannot place an empty order. Please add items.");
            return false;
        }
        if (orderPaymentMethodComboBox.getValue() == null) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please select a payment method.");
            return false;
        }
        if (orderEmployeeComboBox.getValue() == null) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please select a processing employee.");
            return false;
        }
        String email = customerEmailField.getText().trim();
        if (email.isEmpty() || !email.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$")) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            return false;
        }

        return true;

    }

    @Override
    public double getTotalSalesAmount() throws SQLException {
        return getAll().stream().mapToDouble(OrderDTO::getTotal).sum();
    }

    @Override
    public Map<String, Double> getSalesByCategory() throws SQLException {
        return orderRepository.getSalesByCategory();
    }

    @Override
    public Map<String, Double> getMonthlySales(int i) throws SQLException {
        return orderRepository.getMonthlySales(i);
    }

    @Override
    public List<OrderDTO> getRecentOrders(int count) throws SQLException {
        List<OrderDAO> recentOrders = orderRepository.getRecentOrders(count);
        System.out.println(recentOrders);
        return orderRepository.getRecentOrders(count)
                .stream()
                .map(orderDAO -> modelMapper.map(orderDAO, OrderDTO.class))
                .toList();
    }


}
