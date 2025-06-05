package com.icet.clothify.DataBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    private static DataBase instance;
    private final Connection connection;

    private DataBase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3307/clothing_store", "root", "2007");
    }

    public static DataBase getInstance() throws SQLException {
        return instance == null ? instance = new DataBase() : instance;
    }

    public Connection getConnection() {
        return connection;
    }


}
