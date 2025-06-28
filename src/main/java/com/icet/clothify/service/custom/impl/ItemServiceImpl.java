package com.icet.clothify.service.custom.impl;

import com.icet.clothify.model.dao.ItemDAO;
import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.model.dto.SupplierDTO;
import com.icet.clothify.repository.DAOFactory;
import com.icet.clothify.repository.custom.ItemRepository;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.util.RepositoryType;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.icet.clothify.util.Util.alert;

public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository = DAOFactory.getInstance().getServices(RepositoryType.ITEM);

    ModelMapper modelMapper = new ModelMapper();

    public ItemServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(ItemDTO itemDTO) throws SQLException {

        return itemRepository.add(modelMapper.map(itemDTO, ItemDAO.class));
    }

    @Override
    public List<ItemDTO> getAll() throws SQLException {
        return itemRepository.getAll().stream().map(itemDAO -> modelMapper.map(itemDAO, ItemDTO.class)).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return itemRepository.delete(id);
    }


    @Override
    public ItemDTO searchById(String id) throws SQLException {
        return modelMapper.map(itemRepository.searchById(id), ItemDTO.class);
    }

    @Override
    public boolean validateInputsForItem(TextField itemNameField, ComboBox<String> itemCategoryComboBox, ComboBox<String> itemSizeComboBox, ComboBox<SupplierDTO> supplierComboBox, TextField itemPriceField, TextField itemQuantityField) {
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
}
