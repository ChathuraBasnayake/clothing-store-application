package com.icet.clothify.service.custom.impl;

import com.icet.clothify.model.dao.SupplierDAO;
import com.icet.clothify.model.dto.SupplierDTO;
import com.icet.clothify.repository.DAOFactory;
import com.icet.clothify.repository.custom.SupplierRepository;
import com.icet.clothify.service.custom.SupplierService;
import com.icet.clothify.util.RepositoryType;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SupplierServiceImpl implements SupplierService {

    SupplierRepository supplierRepository = DAOFactory.getInstance().getServices(RepositoryType.SUPPLIER);

    ModelMapper modelMapper = new ModelMapper();

    public SupplierServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(SupplierDTO supplierDTO) throws SQLException {
        return supplierRepository.add(modelMapper.map(supplierDTO, SupplierDAO.class));
    }

    @Override
    public List<SupplierDTO> getAll() throws SQLException {
        return supplierRepository.getAll()
                .stream()
                .map(supplierDAO -> modelMapper.map(supplierDAO, SupplierDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return supplierRepository.delete(id);
    }

    @Override
    public SupplierDTO searchById(String id) throws SQLException {
        return modelMapper.map(supplierRepository.searchById(id), SupplierDTO.class);
    }
}
