package com.icet.clothify.service.custom.impl;

import com.google.inject.Inject;
import com.icet.clothify.model.dao.ItemDAO;
import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.model.dto.SupplierDTO;
import com.icet.clothify.repository.custom.ItemRepository;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.util.AlertUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.modelmapper.ModelMapper;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.icet.clothify.util.AlertUtil.alert;

public class ItemServiceImpl implements ItemService {

    @Inject
    ItemRepository itemRepository;

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

    @Override
    public Integer getItemCount() throws SQLException {
        return getAll().size();
    }

    @Override
    public void generateAndShowItemsInventoryReport() throws SQLException {
        try {

            List<ItemDTO> all = getAll();

            if (all.isEmpty()) {

                AlertUtil.alert(Alert.AlertType.INFORMATION, "Data Not Found", "Data not Found in the DataBase!");
                return;
            }

            InputStream reportStream = getClass().getResourceAsStream("/reports/inventory-report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(all);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

            JasperViewer.viewReport(jasperPrint, false); // 'false' means the app doesn't exit on close

            // Optionally, export to PDF
            // String pdfPath = "reports/UserDirectory.pdf";
            // JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);
            // System.out.println("Report saved to " + pdfPath);

        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean update(ItemDTO itemDTO) throws SQLException {
        return itemRepository.update(modelMapper.map(itemDTO, ItemDAO.class));
    }
}
