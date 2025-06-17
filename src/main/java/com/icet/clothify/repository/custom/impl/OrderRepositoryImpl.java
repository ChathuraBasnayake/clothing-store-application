package com.icet.clothify.repository.custom.impl;

import com.icet.clothify.DataBase.DataBase;
import com.icet.clothify.controller.DashboardController;
import com.icet.clothify.model.dao.OrderDAO;
import com.icet.clothify.repository.custom.OrderRepository;
import javafx.scene.control.Alert;
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

        System.out.println(dao.getId());


        connection.setAutoCommit(false);

        int updateOrder = runner.update(connection, "INSERT INTO orders (id, total, paymentMethod, employeeId) VALUES (?, ?, ?, ?)",

                dao.getId(),
                (dao.getTotal()),
                (dao.getPaymentMethod()),
                (dao.getEmployeeId())
        );
        if (updateOrder > 0) {

            boolean allDetailsAreUpdated = true;

            List<DashboardController.OrderItem> orderItems = dao.getOrderItems();


            for (DashboardController.OrderItem orderItem : orderItems) {

                System.out.println(orderItem.getQty());

                int updateOrderDetail = runner.update(connection, "INSERT INTO orderDetails ( orderId, ItemId, qty,unitPrice,name,total) VALUES (?, ?, ?,?,?,?)",

                        dao.getId(),
                        orderItem.getItemId(),
                        orderItem.getQty(),
                        orderItem.getPrice(),
                        orderItem.getName(),
                        orderItem.getTotal()


                );
                if (updateOrderDetail <= 0) {
                    allDetailsAreUpdated = false;
                    break;
                }

            }
            if (allDetailsAreUpdated) {
                boolean allStockAreUpdated = true;
                for (DashboardController.OrderItem orderItem : orderItems) {
                    int updateStock = runner.update(connection, "UPDATE items SET quantity = quantity - ?  WHERE id = ?;",
                            orderItem.getQty(), orderItem.getItemId());

                    if (updateStock <= 0) {
                        allStockAreUpdated = false;
                        break;
                    }
                }
                if (allStockAreUpdated) {
                    connection.commit();
                    connection.setAutoCommit(true);
                    new Alert(Alert.AlertType.INFORMATION, "OrderPlacement Successfully").show();
                    return true;
                }
            }
        }

        connection.rollback();
        connection.setAutoCommit(true);
        return false;
    }

    @Override
    public boolean delete(String id) throws SQLException {

        return (runner.update(connection, "DELETE FROM `orders` WHERE id = ?", Integer.parseInt(id))) > 0;
    }

    @Override
    public boolean update(OrderDAO dao) throws SQLException {

        return (runner.update(connection, "UPDATE orders SET  total = ?, paymentMethod = ?, employeeId = ? WHERE id = ?", dao.getTotal(), dao.getPaymentMethod(), dao.getEmployeeId(), dao.getId())) > 0;

    }

    @Override
    public OrderDAO searchById(String id) throws SQLException {
        ResultSetHandler<OrderDAO> handler = new BeanHandler<>(OrderDAO.class);
        return runner.query(connection, "SELECT * FROM `orders` WHERE id = ?", handler, id);
    }

    @Override
    public List<OrderDAO> getAll() throws SQLException {
        ResultSetHandler<List<OrderDAO>> handler = new BeanListHandler<>(OrderDAO.class);

        return runner.query(connection, "SELECT * FROM `orders`", handler);

    }
}
