package com.icet.clothify.repository.custom;

import com.icet.clothify.model.dao.OrderDAO;
import com.icet.clothify.repository.CRUDRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface OrderRepository extends CRUDRepository<OrderDAO, String> {
    public Map<String, Double> getSalesByCategory() throws SQLException;

    Map<String, Double> getMonthlySales(int i) throws SQLException;

    List<OrderDAO> getRecentOrders(int i) throws SQLException;

//    public Map<String, Double> getMonthlySales(int i);

}
