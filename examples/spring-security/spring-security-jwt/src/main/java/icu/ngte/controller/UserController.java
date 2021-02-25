package icu.ngte.controller;

import icu.ngte.model.User;
import icu.ngte.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController {

  @Autowired private UserService userService;

  // @Secured({"ROLE_ADMIN", "ROLE_USER"})
  @PreAuthorize("hasRole('ADMIN')")
  @RequestMapping(value = "/users", method = RequestMethod.GET)
  public List<User> listUser() {
    return userService.findAll();
  }

  // @Secured("ROLE_USER")
  @PreAuthorize("hasRole('USER')")
  //// @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
  public User getOne(@PathVariable(value = "id") Long id) {
    return userService.findById(id);
  }
}
