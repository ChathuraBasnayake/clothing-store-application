package com.icet.clothify.service;

import com.icet.clothify.service.custom.impl.ItemServiceImpl;
import com.icet.clothify.service.custom.impl.OrderServiceImpl;
import com.icet.clothify.service.custom.impl.SupplierServiceImpl;
import com.icet.clothify.service.custom.impl.UserServiceImpl;
import com.icet.clothify.util.ServiceType;

import java.sql.SQLException;

public class ServiceFactory {

    private static ServiceFactory instance;

    private ServiceFactory() {
    }

    public static ServiceFactory getInstance() {
        return instance == null ? instance = new ServiceFactory() : instance;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperService> T getServiceType(ServiceType type) throws SQLException {
        return switch (type) {
            case USER -> (T) new UserServiceImpl();
            case ITEM -> (T) new ItemServiceImpl();
            case ORDER -> (T) new OrderServiceImpl();
            case SUPPLIER -> (T) new SupplierServiceImpl();
            default -> throw new IllegalArgumentException("Unknown service type: " + type);
        };
    }

}
