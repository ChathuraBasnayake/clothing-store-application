package com.icet.clothify.controller;

import com.google.inject.Inject;
import com.icet.clothify.model.dto.OrderDTO;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.service.custom.OrderService;
import com.icet.clothify.service.custom.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.icet.clothify.util.AlertUtil.alert;

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
    @FXML
    private CategoryAxis xAxis;

    @Inject
    private ItemService itemService;
    @Inject
    private UserService userService;
    @Inject
    private OrderService orderService;

    @FXML
    public void initialize() {
        refreshData();
    }


    public void refreshData() {
        if (orderService == null || userService == null || itemService == null) {
            return;
        }
        System.out.println("Refreshing Home View data...");
        loadStatCards();
        loadSalesBarChart();
        loadRecentActivity();
    }

    private void loadStatCards() {
        try {
            noUsersLabel.setText(String.valueOf(userService.getAll().size()));
            noItemsLabel.setText(String.valueOf(itemService.getItemCount()));
            double totalSales = orderService.getTotalSalesAmount();
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "LK"));
            totalSalesLabel.setText(currencyFormat.format(totalSales));
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Data Error", "Could not load dashboard statistics.");
            e.printStackTrace();
        }
    }

    private void loadSalesBarChart() {
        try {
            Map<String, Double> monthlySales = orderService.getMonthlySales(3);

            System.out.println("Fetched Monthly Sales Data for Chart: " + monthlySales);
            if (monthlySales == null || monthlySales.isEmpty()) {
                System.out.println("No monthly sales data found to display in the chart.");
                Platform.runLater(() -> salesChart.setData(FXCollections.observableArrayList()));
                return;
            }

            xAxis.setCategories(FXCollections.observableArrayList(monthlySales.keySet()));

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Sales in LKR");
            monthlySales.forEach((month, sales) -> {
                series.getData().add(new XYChart.Data<>(month, sales));
            });

            ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList(series);

            Platform.runLater(() -> {
                salesChart.setData(barChartData);
            });

        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Chart Error", "Could not load monthly sales data for the chart.");
            e.printStackTrace();
        }
    }

    private void loadRecentActivity() {
        try {
            List<String> activities = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, hh:mm a");
            List<OrderDTO> recentOrders = orderService.getRecentOrders(3);
            for (OrderDTO order : recentOrders) {
                activities.add(String.format("Order #%s placed (%s)",
                        order.getId(),
                        order.getOrderDate().format(formatter))
                );
            }
            activityListView.setItems(FXCollections.observableArrayList(activities));
        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Activity Error", "Could not load recent activities.");
            e.printStackTrace();
        }
    }
}
