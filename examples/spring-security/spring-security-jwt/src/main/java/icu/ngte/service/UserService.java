package icu.ngte.service;

import icu.ngte.model.User;
import icu.ngte.model.UserDTO;

import java.util.List;

public interface UserService {

    User save(UserDTO user);
    List<User> findAll();
    void delete(long id);
    User findOne(String username);

    User findById(Long id);
}
