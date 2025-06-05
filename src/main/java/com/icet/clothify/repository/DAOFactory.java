package com.icet.clothify.repository;

import com.icet.clothify.repository.custom.impl.ItemRepositoryImpl;
import com.icet.clothify.repository.custom.impl.OrderRepositoryImpl;
import com.icet.clothify.repository.custom.impl.SupplierRepositoryImpl;
import com.icet.clothify.repository.custom.impl.UserRepositoryImpl;
import com.icet.clothify.util.RepositoryType;

import java.sql.SQLException;

public class DAOFactory {

    private static DAOFactory instance;

    private DAOFactory() {
    }

    public static DAOFactory getInstance() {

        return instance == null ? new DAOFactory() : instance;

    }

    public <T extends SuperRepository> T getServices(RepositoryType type) throws SQLException {

        return switch (type) {
            case USER -> (T) new UserRepositoryImpl();
            case ITEM -> (T) new ItemRepositoryImpl();
            case ORDER -> (T) new OrderRepositoryImpl();
            case SUPPLIER -> (T) new SupplierRepositoryImpl();
            default -> throw new IllegalArgumentException("Unknown service type: " + type);
        };

    }
}
