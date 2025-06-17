package com.icet.clothify.service.custom.impl;

import com.icet.clothify.model.dao.ItemDAO;
import com.icet.clothify.model.dto.ItemDTO;
import com.icet.clothify.repository.DAOFactory;
import com.icet.clothify.repository.custom.ItemRepository;
import com.icet.clothify.service.custom.ItemService;
import com.icet.clothify.util.RepositoryType;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository = DAOFactory.getInstance().getServices(RepositoryType.ITEM);

    ModelMapper modelMapper = new ModelMapper();

    public ItemServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(ItemDTO itemDTO) throws SQLException {

        return itemRepository.add(modelMapper.map(itemDTO, ItemDAO.class));
    }

    @Override
    public List<ItemDTO> getAll() throws SQLException {

        System.out.println(itemRepository.getAll() + "ASER");
        return itemRepository.getAll().stream().map(itemDAO -> modelMapper.map(itemDAO, ItemDTO.class)).collect(Collectors.toList());
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return itemRepository.delete(id);
    }

    @Override
    public ItemDTO searchByName(String name) throws SQLException {
        return modelMapper.map(itemRepository.searchByName(name), ItemDTO.class);
    }

    @Override
    public ItemDTO searchById(String id) throws SQLException {
        return modelMapper.map(itemRepository.searchById(id), ItemDTO.class);
    }


}
