package com.icet.clothify.service.custom;

import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.SuperService;

import java.sql.SQLException;
import java.util.List;

public interface UserService extends SuperService {

    boolean add(UserDTO userDTO) throws SQLException;

    List<UserDTO> getAll() throws SQLException;

    boolean delete(String id) throws SQLException;

    boolean update(UserDTO userDTO) throws SQLException;

    UserDTO searchByEmail(String name) throws SQLException;

    UserDTO searchById(String id) throws SQLException;

    boolean userVerifier(String email, String password) throws SQLException;

    boolean isAdmin(String email) throws SQLException;

    public void generateAndShowUsersReport() throws SQLException;
}
