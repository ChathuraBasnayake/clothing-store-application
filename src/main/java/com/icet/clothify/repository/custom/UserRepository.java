package com.icet.clothify.repository.custom;

import com.icet.clothify.model.dao.UserDAO;
import com.icet.clothify.repository.CRUDRepository;

import java.sql.SQLException;

public interface UserRepository extends CRUDRepository<UserDAO, String> {
    UserDAO searchByEmail(String name) throws SQLException;
}
