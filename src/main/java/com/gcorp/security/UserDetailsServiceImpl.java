package com.gcorp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import com.gcorp.repository.UserRepository;

public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(final String email) {
		return userRepository.findById(email).get();
	}
}
