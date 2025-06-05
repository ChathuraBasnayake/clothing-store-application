package com.icet.clothify.service.custom;

import com.icet.clothify.model.dto.SupplierDTO;

import java.sql.SQLException;
import java.util.List;

public interface SupplierService {

    boolean add(SupplierDTO orderDTO) throws SQLException;

    List<SupplierDTO> getAll() throws SQLException;

    boolean delete(String id) throws SQLException;

    SupplierDTO searchById(String id) throws SQLException;

}
