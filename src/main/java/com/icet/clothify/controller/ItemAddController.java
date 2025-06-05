package com.icet.clothify.controller;

import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.service.ServiceFactory;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.util.ServiceType;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.sql.SQLException;

public class ItemAddController {

    ItemService itemService;
    @FXML
    private Pane ItemAddContent;
    @FXML
    private JFXComboBox<?> comboBoxCategory;
    @FXML
    private JFXComboBox<?> comboBoxSize;
    @FXML
    private JFXTextField txtItemId;
    @FXML
    private JFXTextField txtItemName;
    @FXML
    private JFXTextField txtPrice;
    @FXML
    private JFXTextField txtQuantity;
    @FXML
    private JFXTextField txtSupplier;

    {
        try {
            itemService = ServiceFactory.getInstance().getServiceType(ServiceType.ITEM);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    {
        try {
            System.out.println(itemService.getAll());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void btnAddOnClick(ActionEvent event) {
        try {
            itemService.add(new ItemDTO(null, txtItemName.getText(), comboBoxCategory.getValue().toString(), comboBoxSize.getValue().toString(), Double.parseDouble(txtPrice.getText()), Integer.parseInt(txtQuantity.getText()), Integer.parseInt(txtSupplier.getText())));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
