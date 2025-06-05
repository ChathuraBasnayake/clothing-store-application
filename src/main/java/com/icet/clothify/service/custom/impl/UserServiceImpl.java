package com.icet.clothify.service.custom.impl;

import com.icet.clothify.model.dao.UserDAO;
import com.icet.clothify.model.dto.UserDTO;
import com.icet.clothify.repository.DAOFactory;
import com.icet.clothify.repository.custom.UserRepository;
import com.icet.clothify.service.custom.UserService;
import com.icet.clothify.util.RepositoryType;
import org.modelmapper.ModelMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    UserRepository userRepository = DAOFactory.getInstance().getServices(RepositoryType.USER);

    ModelMapper modelMapper = new ModelMapper();

    public UserServiceImpl() throws SQLException {
    }

    @Override
    public boolean add(UserDTO userDTO) throws SQLException {
        return userRepository.add(modelMapper.map(userDTO, UserDAO.class));
    }

    @Override
    public List<UserDTO> getAll() throws SQLException {
        return userRepository.getAll()
                .stream()
                .map(userDAO -> modelMapper.map(userDAO, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String id) throws SQLException {
        return userRepository.delete(id);
    }

    @Override
    public UserDTO searchById(String id) throws SQLException {
        return modelMapper.map(userRepository.searchById(id), UserDTO.class);
    }

    @Override
    public boolean userLogIn(String email, String password) throws SQLException {
        return userRepository.searchByName(email).getPassword().equals(password);
    }

    @Override
    public UserDTO searchByName(String name) throws SQLException {
        return modelMapper.map(userRepository.searchByName(name), UserDTO.class);
    }
}
