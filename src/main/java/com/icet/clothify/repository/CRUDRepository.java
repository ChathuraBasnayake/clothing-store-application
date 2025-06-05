package com.icet.clothify.repository;


import java.sql.SQLException;
import java.util.List;

public interface CRUDRepository<T, ID> extends SuperRepository {

    boolean add(T dao) throws SQLException;

    boolean delete(String id) throws SQLException;

    boolean update(T dao) throws SQLException;

    T searchById(String id) throws SQLException;  // instead of boolean

    List<T> getAll() throws SQLException;


}
