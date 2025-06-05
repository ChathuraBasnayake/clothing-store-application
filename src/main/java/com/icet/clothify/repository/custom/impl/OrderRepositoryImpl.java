package com.icet.clothify.repository.custom.impl;

import com.icet.clothify.DataBase.DataBase;
import com.icet.clothify.model.dao.OrderDAO;
import com.icet.clothify.repository.custom.OrderRepository;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    Connection connection = DataBase.getInstance().getConnection();

    QueryRunner runner = new QueryRunner();

    public OrderRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean add(OrderDAO dao) throws SQLException {

        return (runner.update(connection, "INSERT INTO `order` (items, total, payment_method, employee_id) VALUES (?, ?, ?, ?)",
                (dao.getItems()),
                (dao.getTotal()),
                (dao.getPaymentMethod()),
                (dao.getEmployeeId())
        )) > 0;

    }

    @Override
    public boolean delete(String id) throws SQLException {

        return (runner.update(connection, "DELETE FROM `order` WHERE id = ?", Integer.parseInt(id))) > 0;
    }

    @Override
    public boolean update(OrderDAO dao) throws SQLException {

        return (runner.update(connection, "UPDATE order SET items = ?, total = ?, payment_method = ?, employee_id = ? WHERE id = ?", dao.getItems(), dao.getTotal(), dao.getPaymentMethod(), dao.getEmployeeId(), dao.getId())) > 0;

    }

    @Override
    public OrderDAO searchById(String id) throws SQLException {
        ResultSetHandler<OrderDAO> handler = new BeanHandler<>(OrderDAO.class);
        return runner.query(connection, "SELECT * FROM `order` WHERE id = ?", handler, id);
    }

    @Override
    public List<OrderDAO> getAll() throws SQLException {
        ResultSetHandler<List<OrderDAO>> handler = new BeanListHandler<>(OrderDAO.class);

        return runner.query(connection, "SELECT * FROM `order`", handler);

    }
}
