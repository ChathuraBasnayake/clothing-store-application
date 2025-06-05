package com.icet.clothify.repository.custom.impl;

import com.icet.clothify.DataBase.DataBase;
import com.icet.clothify.model.dao.ItemDAO;
import com.icet.clothify.repository.custom.ItemRepository;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ItemRepositoryImpl implements ItemRepository {

    final QueryRunner runner = new QueryRunner();
    Connection connection = DataBase.getInstance().getConnection();

    public ItemRepositoryImpl() throws SQLException {
    }


    @Override
    public boolean add(ItemDAO dao) {

        try {
            return (runner.update(connection, "INSERT INTO item (name, category, size, quantity, price, supplier_id) VALUES (?, ?, ?, ?, ?, ?)", dao.getName(), dao.getCategory(), dao.getSize(), dao.getQuantity(), dao.getPrice(), dao.getSupplier_id())) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean delete(String id) {

        try {
            return (runner.update(connection, "DELETE FROM item WHERE item_id = ?", Integer.parseInt(id)) > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(ItemDAO dao) {

        try {
            return (runner.update(connection, "UPDATE item SET name = ?, category = ?, size = ?, quantity = ?, price = ?, supplier_id = ? WHERE item_id = ?", dao.getName(), dao.getCategory(), dao.getSize(), dao.getQuantity(), dao.getPrice(), dao.getSupplier_id(), dao.getId())) > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public ItemDAO searchById(String id) {

        ResultSetHandler<ItemDAO> handler = new BeanHandler<>(ItemDAO.class);
        try {
            return runner.query(connection, "SELECT * FROM item WHERE item_id = ?", handler, Integer.parseInt(id));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public List<ItemDAO> getAll() {

        ResultSetHandler<List<ItemDAO>> handler = new BeanListHandler<>(ItemDAO.class);
        try {
            return runner.query(connection, "SELECT * FROM item", handler);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ItemDAO searchByName(String name) throws SQLException {
        ResultSetHandler<ItemDAO> handler = new BeanHandler<>(ItemDAO.class);
        return runner.query(connection,
                "SELECT * FROM user WHERE name = ?",
                handler,
                name
        );
    }

}