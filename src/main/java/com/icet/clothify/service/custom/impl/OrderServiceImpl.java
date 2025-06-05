package com.icet.clothify.service.custom.impl;

import com.icet.clothify.model.dao.OrderDAO;
import com.icet.clothify.model.dto.OrderDTO;
import com.icet.clothify.repository.DAOFactory;
import com.icet.clothify.repository.custom.OrderRepository;
import com.icet.clothify.service.custom.OrderService;
import com.icet.clothify.util.RepositoryType;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository = DAOFactory.getInstance().getServices(RepositoryType.ORDER);

    ModelMapper modelMapper = new ModelMapper();

    public OrderServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(OrderDTO orderDTO) throws SQLException {
        return orderRepository.add(modelMapper.map(orderDTO, OrderDAO.class));
    }

    @Override
    public List<OrderDTO> getAll() throws SQLException {
        return orderRepository.getAll()
                .stream()
                .map(orderDAO -> modelMapper.map(orderDAO, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return orderRepository.delete(id);
    }

    @Override
    public OrderDTO searchById(String id) throws SQLException {
        return modelMapper.map(orderRepository.searchById(id), OrderDTO.class);
    }
}
