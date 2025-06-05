package com.icet.clothify.repository.custom.impl;

import com.icet.clothify.DataBase.DataBase;
import com.icet.clothify.model.dao.UserDAO;
import com.icet.clothify.repository.custom.UserRepository;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    private final Connection connection = DataBase.getInstance().getConnection();
    private final QueryRunner runner = new QueryRunner();

    public UserRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean add(UserDAO dao) throws SQLException {
        return runner.update(connection, "INSERT INTO user (name, company, email, password, is_employee) VALUES (?, ?, ?, ?, ?)", dao.getName(), dao.getCompany(), dao.getEmail(), dao.getPassword(), dao.isEmployee()) > 0;
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return runner.update(connection, "DELETE FROM user WHERE id = ?", Integer.parseInt(id)) > 0;
    }

    @Override
    public boolean update(UserDAO dao) throws SQLException {
        return runner.update(connection, "UPDATE user SET name = ?, company = ?, email = ?, password = ?, is_employee = ? WHERE id = ?", dao.getName(), dao.getCompany(), dao.getEmail(), dao.getPassword(), dao.isEmployee(), dao.getId()) > 0;
    }

    @Override
    public UserDAO searchById(String id) throws SQLException {
        ResultSetHandler<UserDAO> handler = new BeanHandler<>(UserDAO.class);
        return runner.query(connection, "SELECT * FROM user WHERE id = ?", handler, Integer.parseInt(id));
    }

    @Override
    public List<UserDAO> getAll() throws SQLException {
        ResultSetHandler<List<UserDAO>> handler = new BeanListHandler<>(UserDAO.class);
        return runner.query(connection, "SELECT * FROM user", handler);
    }

    @Override
    public UserDAO searchByName(String email) throws SQLException {
        ResultSetHandler<UserDAO> handler = new BeanHandler<>(UserDAO.class);
        return runner.query(connection, "SELECT * FROM user WHERE email = ?", handler, email);
    }
}