package com.icet.clothify.service.custom.impl;

import com.google.inject.Inject;
import com.icet.clothify.model.dao.UserDAO;
import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.repository.custom.UserRepository;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.util.AlertUtil;
import javafx.scene.control.Alert;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.modelmapper.ModelMapper;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    @Inject
    UserRepository userRepository;

    ModelMapper modelMapper = new ModelMapper();


    public UserServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(UserDTO userDTO) throws SQLException {
        return userRepository.add(modelMapper.map(userDTO, UserDAO.class));
    }

    @Override
    public List<UserDTO> getAll() throws SQLException {
        return userRepository.getAll()
                .stream()
                .map(userDAO -> modelMapper.map(userDAO, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return userRepository.delete(id);
    }

    @Override
    public boolean update(UserDTO userDTO) throws SQLException {
        return userRepository.update(modelMapper.map(userDTO, UserDAO.class));
    }

    @Override
    public UserDTO searchById(String id) throws SQLException {
        return modelMapper.map(userRepository.searchById(id), UserDTO.class);
    }

    @Override
    public boolean userVerifier(String email, String password) throws SQLException {
        UserDAO userDAO = userRepository.searchByEmail(email);
        if (userDAO == null) {
            return false;
        }
        return userDAO.getPassword().equals(password);
    }

    @Override
    public boolean isAdmin(String email) throws SQLException {
        UserDTO userDTO = searchByEmail(email);
        return userDTO != null && userDTO.getIsEmployee();
    }

    @Override
    public UserDTO searchByEmail(String name) throws SQLException {
        UserDAO userDAO = userRepository.searchByEmail(name);
        if (userDAO == null) {
            return null;
        }
        return modelMapper.map(userDAO, UserDTO.class);
    }

    public void generateAndShowUsersReport() throws SQLException {
        try {

            List<UserDTO> userList = getAll();

            if (userList.isEmpty()) {

                AlertUtil.alert(Alert.AlertType.INFORMATION, "Data Not Found", "Data not Found in the DataBase!");
                return;
            }

            InputStream reportStream = getClass().getResourceAsStream("/reports/users-report.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(userList);

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
}

