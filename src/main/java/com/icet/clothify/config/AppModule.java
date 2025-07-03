package com.icet.clothify.config;

import com.google.inject.AbstractModule;
import com.icet.clothify.repository.custom.ItemRepository;
import com.icet.clothify.repository.custom.OrderRepository;
import com.icet.clothify.repository.custom.SupplierRepository;
import com.icet.clothify.repository.custom.UserRepository;
import com.icet.clothify.repository.custom.impl.ItemRepositoryImpl;
import com.icet.clothify.repository.custom.impl.OrderRepositoryImpl;
import com.icet.clothify.repository.custom.impl.SupplierRepositoryImpl;
import com.icet.clothify.repository.custom.impl.UserRepositoryImpl;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.service.custom.OrderService;
import com.icet.clothify.service.custom.SupplierService;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.service.custom.impl.ItemServiceImpl;
import com.icet.clothify.service.custom.impl.OrderServiceImpl;
import com.icet.clothify.service.custom.impl.SupplierServiceImpl;
import com.icet.clothify.service.custom.impl.UserServiceImpl;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ItemService.class).to(ItemServiceImpl.class);
        bind(SupplierService.class).to(SupplierServiceImpl.class);
        bind(OrderService.class).to(OrderServiceImpl.class);
        bind(UserService.class).to(UserServiceImpl.class);


        bind(UserRepository.class).to(UserRepositoryImpl.class);
        bind(ItemRepository.class).to(ItemRepositoryImpl.class);
        bind(OrderRepository.class).to(OrderRepositoryImpl.class);
        bind(SupplierRepository.class).to(SupplierRepositoryImpl.class);
    }
}
