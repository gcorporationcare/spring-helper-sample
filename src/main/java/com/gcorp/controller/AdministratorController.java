package com.gcorp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcorp.entity.Administrator;
import com.gcorp.repository.AdministratorRepository;
import com.gcorp.service.AdministratorService;
import com.gcorp.service.BaseSearchableService;

@RestController
@RequestMapping("/administrators")
public class AdministratorController extends BaseSearchableController<Administrator, Long, AdministratorRepository> {

	@Autowired
	AdministratorService administratorService;

	@Override
	public BaseSearchableService<Administrator, Long, AdministratorRepository> service() {
		return administratorService;
	}
}
