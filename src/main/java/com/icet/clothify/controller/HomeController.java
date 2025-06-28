package com.icet.clothify.controller;

import com.icet.clothify.service.ServiceFactory;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.util.ServiceType;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.SQLException;

import static com.icet.clothify.util.Util.alert;

public class HomeController {

    @FXML
    private Label totalSalesLabel;
    @FXML
    private Label noUsersLabel;
    @FXML
    private Label noItemsLabel;
    @FXML
    private BarChart<String, Number> salesChart;
    @FXML
    private ListView<String> activityListView;

    private ItemService itemService;
    private UserService userService;
    // You would also have an OrderService to get total sales and chart data

    @FXML
    public void initialize() {
        try {
            itemService = ServiceFactory.getInstance().getServiceType(ServiceType.ITEM);
            userService = ServiceFactory.getInstance().getServiceType(ServiceType.USER);
            // orderService = ServiceFactory.getInstance().getServiceType(ServiceType.ORDER);
        } catch (SQLException e) {
            alert(javafx.scene.control.Alert.AlertType.ERROR, "Initialization Failed", "Could not connect to services.");
            e.printStackTrace();
        }
        refreshData();
    }

    /**
     * This public method can be called from the DashboardController to update the view
     * when it becomes visible.
     */
    public void refreshData() {
        System.out.println("Refreshing Home View data...");
        loadStatCards();
        // In a real app, you would load chart and activity data here as well.
    }

    private void loadStatCards() {
        try {
            noUsersLabel.setText(String.valueOf(userService.getAll().size()));
            noItemsLabel.setText(String.valueOf(itemService.getAll().size()));
            // totalSalesLabel would be populated from the OrderService
            totalSalesLabel.setText("$12,345"); // Placeholder
        } catch (SQLException e) {
            alert(javafx.scene.control.Alert.AlertType.ERROR, "Data Error", "Could not load statistics.");
            e.printStackTrace();
        }
    }
}
