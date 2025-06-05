package com.icet.clothify.service.custom;

import com.icet.clothify.model.dto.SupplierDTO;
import com.icet.clothify.service.SuperService;

import java.sql.SQLException;
import java.util.List;

public interface SupplierService extends SuperService {

    boolean add(SupplierDTO orderDTO) throws SQLException;

    List<SupplierDTO> getAll() throws SQLException;

    boolean delete(String id) throws SQLException;

    SupplierDTO searchById(String id) throws SQLException;

}
