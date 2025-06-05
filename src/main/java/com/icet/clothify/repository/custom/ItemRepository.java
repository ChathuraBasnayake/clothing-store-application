package com.icet.clothify.repository.custom;

import com.icet.clothify.model.dao.ItemDAO;
import com.icet.clothify.repository.CRUDRepository;

import java.sql.SQLException;

public interface ItemRepository extends CRUDRepository<ItemDAO, String> {
    ItemDAO searchByName(String name) throws SQLException;
}
