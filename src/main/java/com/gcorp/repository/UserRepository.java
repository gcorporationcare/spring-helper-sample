package com.gcorp.repository;

import org.springframework.data.repository.CrudRepository;

import com.gcorp.entity.User;

public interface UserRepository extends CrudRepository<User, String> {

}
