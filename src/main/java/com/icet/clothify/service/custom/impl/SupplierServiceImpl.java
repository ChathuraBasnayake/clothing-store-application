package com.icet.clothify.service.custom.impl;

import com.google.inject.Inject;
import com.icet.clothify.model.dao.SupplierDAO;
import com.icet.clothify.model.dto.SupplierDTO;
import com.icet.clothify.repository.custom.SupplierRepository;
import com.icet.clothify.service.custom.SupplierService;
import com.icet.clothify.util.AlertUtil;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.modelmapper.ModelMapper;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.icet.clothify.util.AlertUtil.alert;

public class SupplierServiceImpl implements SupplierService {

    @Inject
    SupplierRepository supplierRepository;

    ModelMapper modelMapper = new ModelMapper();

    public SupplierServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(SupplierDTO supplierDTO) throws SQLException {
        return supplierRepository.add(modelMapper.map(supplierDTO, SupplierDAO.class));
    }

    @Override
    public List<SupplierDTO> getAll() throws SQLException {
        return supplierRepository.getAll()
                .stream()
                .map(supplierDAO -> modelMapper.map(supplierDAO, SupplierDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return supplierRepository.delete(id);
    }

    @Override
    public SupplierDTO searchById(String id) throws SQLException {
        return modelMapper.map(supplierRepository.searchById(id), SupplierDTO.class);
    }

    public boolean validateSupplierInputs(String name, String company, String email) {
        if (name.trim().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Supplier Name cannot be empty.");
            return false;
        }
        if (company.trim().isEmpty()) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Company Name cannot be empty.");
            return false;
        }

        if (email.trim().isEmpty() || !email.matches("^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$")) {
            alert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            return false;
        }
        return true;
    }

    public void clearAddSupplierForm(List<TextField> textFields) {

        textFields.forEach(TextInputControl::clear);

    }

    public void generateAndShowSuppliersReport() throws SQLException {
        try {

            List<SupplierDTO> all = getAll();

            if (all.isEmpty()) {

                AlertUtil.alert(Alert.AlertType.INFORMATION, "Data Not Found", "Data not Found in the DataBase!");
                return;
            }

            InputStream reportStream = getClass().getResourceAsStream("/reports/supplier-report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(all);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, dataSource);

            JasperViewer.viewReport(jasperPrint, false);


        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public boolean update(SupplierDTO supplierDTO) throws SQLException {
        return supplierRepository.update(modelMapper.map(supplierDTO, SupplierDAO.class));
    }


}
