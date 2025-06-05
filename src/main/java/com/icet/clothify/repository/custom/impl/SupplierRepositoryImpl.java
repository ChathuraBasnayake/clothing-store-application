package com.icet.clothify.repository.custom.impl;

import com.icet.clothify.DataBase.DataBase;
import com.icet.clothify.model.dao.SupplierDAO;
import com.icet.clothify.repository.custom.SupplierRepository;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SupplierRepositoryImpl implements SupplierRepository {

    private final Connection connection = DataBase.getInstance().getConnection();
    private final QueryRunner runner = new QueryRunner();

    public SupplierRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean add(SupplierDAO dao) throws SQLException {
        return runner.update(connection, "INSERT INTO supplier (name, company, email) VALUES (?, ?, ?)",
                dao.getName(),
                dao.getCompany(),
                dao.getEmail()
        ) > 0;
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return runner.update(connection, "DELETE FROM supplier WHERE supplier_id = ?", Integer.parseInt(id)) > 0;
    }

    @Override
    public boolean update(SupplierDAO dao) throws SQLException {
        return runner.update(connection, "UPDATE supplier SET name = ?, company = ?, email = ? WHERE supplier_id = ?",
                dao.getName(),
                dao.getCompany(),
                dao.getEmail(),
                dao.getSupplier_id()
        ) > 0;
    }

    @Override
    public SupplierDAO searchById(String id) throws SQLException {
        ResultSetHandler<SupplierDAO> handler = new BeanHandler<>(SupplierDAO.class);
        return runner.query(connection, "SELECT * FROM supplier WHERE supplier_id = ?", handler, id);
    }

    @Override
    public List<SupplierDAO> getAll() throws SQLException {
        ResultSetHandler<List<SupplierDAO>> handler = new BeanListHandler<>(SupplierDAO.class);
        return runner.query(connection, "SELECT * FROM supplier", handler);
    }
}
