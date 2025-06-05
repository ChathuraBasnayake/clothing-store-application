package com.icet.clothify.service.custom;

import com.icet.clothify.model.dto.OrderDTO;

import java.sql.SQLException;
import java.util.List;

public interface OrderService {

    boolean add(OrderDTO orderDTO) throws SQLException;

    List<OrderDTO> getAll() throws SQLException;

    boolean delete(String id) throws SQLException;

    OrderDTO searchById(String id) throws SQLException;

}
