package com.icet.clothify.service.custom;

import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.service.SuperService;
import java.sql.SQLException;
import java.util.List;

public interface UserService extends SuperService {

    /**
     * Checks if a user exists with the given email.
     * @param email The email to check.
     * @return true if the user exists, false otherwise.
     */
    boolean userExists(String email) throws SQLException;

    /**
     * Updates the password for the user with the specified email.
     * @param email The user's email.
     * @param newPassword The new password to set.
     * @return true if the update was successful, false otherwise.
     */
    boolean updatePassword(String email, String newPassword) throws SQLException;

    boolean add(UserDTO userDTO) throws SQLException;

    List<UserDTO> getAll() throws SQLException;

    boolean delete(String id) throws SQLException;

    boolean update(UserDTO userDTO) throws SQLException;

    UserDTO searchById(String id) throws SQLException;

    void generateAndShowUsersReport() throws SQLException;

    // Method signatures for other user-related business logic
    boolean userVerifier(String email, String password) throws SQLException;

    boolean isAdmin(String email) throws SQLException;

    UserDTO searchByEmail(String email) throws SQLException;
}
