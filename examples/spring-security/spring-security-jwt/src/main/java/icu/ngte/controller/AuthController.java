package icu.ngte.controller;

import icu.ngte.config.TokenProvider;
import icu.ngte.service.UserService;
import icu.ngte.model.AuthToken;
import icu.ngte.model.LoginUser;
import icu.ngte.model.User;
import icu.ngte.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private TokenProvider jwtTokenUtil;

  @Autowired private UserService userService;

  @RequestMapping(value = "/signin", method = RequestMethod.POST)
  public ResponseEntity<?> signin(@RequestBody LoginUser loginUser)
      throws AuthenticationException {

    final Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginUser.getUsername(), loginUser.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    final String token = jwtTokenUtil.generateToken(authentication);
    return ResponseEntity.ok(new AuthToken(token));
  }

  @RequestMapping(value = "/signup", method = RequestMethod.POST)
  public User saveUser(@RequestBody UserDTO user) {
    return userService.save(user);
  }
}
