package com.gcorp.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gcorp.entity.User;
import com.gcorp.exception.RequestException;
import com.gcorp.model.LoginRequest;
import com.gcorp.repository.UserRepository;
import com.gcorp.security.JwtTokenProvider;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

	@Autowired
	UserRepository userRepository;
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@ResponseBody
	@ApiOperation(value = "login", notes = "Authenticates and log in an user")
	@PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.CREATED)
	public User login(@Valid @RequestBody LoginRequest credentials) {
		final String badCredentials = "user.bad_credentials";
		Optional<User> user = userRepository.findById(credentials.getEmail());
		if (!user.isPresent()) {
			throw new RequestException(badCredentials, HttpStatus.UNAUTHORIZED);
		}
		User loggedUser = user.get();
		if (!bCryptPasswordEncoder.matches(credentials.getPassword(), loggedUser.getPassword())) {
			throw new RequestException(badCredentials, HttpStatus.UNAUTHORIZED);
		}
		loggedUser.setToken(jwtTokenProvider.generateToken(loggedUser));
		return loggedUser;
	}
}
