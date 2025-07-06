package com.icet.clothify.controller;

import com.google.inject.Inject;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.service.custom.OrderService;
import com.icet.clothify.service.custom.SupplierService;
import com.icet.clothify.service.custom.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

import static com.icet.clothify.util.AlertUtil.alert;

public class ReportsController {

    @Inject
    OrderService orderService;
    @Inject
    UserService userService;
    @Inject
    SupplierService supplierService;
    @Inject
    ItemService itemService;
    //<editor-fold desc="FXML Fields">
    @FXML
    private Button printInventoryBtn;
    @FXML
    private Button printEmployeesBtn;
    @FXML
    private Button printSuppliersBtn;
    @FXML
    private ToggleGroup timeframeToggleGroup;
    @FXML
    private ToggleButton dailyToggle;
    @FXML
    private ToggleButton monthlyToggle;
    @FXML
    private ToggleButton annualToggle;

    @FXML
    private DatePicker dailyDatePicker;
    @FXML
    private Button generateSalesBtn;
    @FXML
    private LineChart<String, Number> salesLineChart;
    @FXML
    private PieChart salesPieChart;

    @FXML
    public void initialize() {
        dailyDatePicker.setValue(LocalDate.now());

        timeframeToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            dailyDatePicker.setVisible(newToggle == dailyToggle);
        });

        loadDummyChartData();
    }


    @FXML
    void handlePrintReport(ActionEvent event) {
        Object source = event.getSource();
        String reportType = "";
        try {
            if (source == printInventoryBtn) {
                itemService.generateAndShowItemsInventoryReport();
                reportType = "Inventory";
            } else if (source == printEmployeesBtn) {

                userService.generateAndShowUsersReport();

                reportType = "Employee";
            } else if (source == printSuppliersBtn) {
                supplierService.generateAndShowSuppliersReport();
                reportType = "Supplier";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Generating " + reportType + " report using JasperReports...");
    }

    @FXML
    void handleGenerateAnalytics(ActionEvent event) {
        ToggleButton selectedToggle = (ToggleButton) timeframeToggleGroup.getSelectedToggle();
        if (selectedToggle == null) return;

        System.out.println("Generating analytics for: " + selectedToggle.getText());

        loadDummyChartData();
    }

    private void loadDummyChartData() {

        salesLineChart.getData().clear();
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName("Monthly Sales");

        lineSeries.setName("Monthly Sales Report");
        try {
            orderService.getMonthlySales(6).forEach((s, aDouble) -> {

                lineSeries.getData().add(new XYChart.Data<>(s, aDouble));
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        salesLineChart.setTitle("Monthly Sales Report");
        salesLineChart.getData().add(lineSeries);

        try {
            System.out.println(orderService.getMonthlySales(6) + "gfsfdgsrdfgsdfsd");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            Map<String, Double> categorySales = orderService.getSalesByCategory();

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            categorySales.forEach((category, sales) -> pieChartData.add(new PieChart.Data(category, sales)));

            salesPieChart.setData(pieChartData);

        } catch (SQLException e) {
            alert(Alert.AlertType.ERROR, "Chart Error", "Could not load category sales data.");
            e.printStackTrace();
        }
    }
}
