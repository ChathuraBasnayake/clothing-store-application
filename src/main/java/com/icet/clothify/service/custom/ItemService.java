package com.icet.clothify.service.custom;

import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.service.SuperService;

import java.sql.SQLException;
import java.util.List;

public interface ItemService extends SuperService {

    boolean add(ItemDTO itemDTO) throws SQLException;

    List<ItemDTO> getAll() throws SQLException;

    boolean delete(String id) throws SQLException;

    ItemDTO searchByName(String name) throws SQLException;

    ItemDTO searchById(String id) throws SQLException;
}
