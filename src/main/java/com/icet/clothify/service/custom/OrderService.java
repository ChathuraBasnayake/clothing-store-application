package com.icet.clothify.service.custom;

import com.icet.clothify.controller.MakeOrderController;
import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.model.dto.OrderDTO;
import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.SuperService;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface OrderService extends SuperService {

    boolean placeOrder(OrderDTO orderDTO) throws SQLException;

    List<OrderDTO> getAll() throws SQLException;

    boolean delete(String id) throws SQLException;

    OrderDTO searchById(String id) throws SQLException;

    public String generateOrderId();

    boolean validateAddToTheOrder(ItemDTO selectedItem, TextField orderItemQtyField);

    void updateOrderTotal(ObservableList<MakeOrderController.OrderItem> currentOrderItems, Label orderTotalLabel);

    boolean handlePlaceOrderValidations(ObservableList<MakeOrderController.OrderItem> currentOrderItems, ComboBox<String> orderPaymentMethodComboBox, ComboBox<UserDTO> orderEmployeeComboBox, TextField customerEmailField);

    double getTotalSalesAmount() throws SQLException;

    public Map<String, Double> getSalesByCategory() throws SQLException;

    public Map<String, Double> getMonthlySales(int i) throws SQLException;

    List<OrderDTO> getRecentOrders(int i) throws SQLException;
}
