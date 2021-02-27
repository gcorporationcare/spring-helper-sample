package com.gcorp.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcorp.entity.Administrator;
import com.gcorp.repository.custom.CustomAdministratorRepository;

/**
 * We can extends JPA interface extending PagingAndSortingRepository.<br/>
 * The extension of JpaSpecificationExecutor and CustomProductRepository are
 * mandatory.<br/>
 * The first one enables the use of specifications on our repository.<br/>
 * The second one will help us adding the custom method findByFilters which will
 * be used for generating dynamic specification from requested URL.
 */
public interface AdministratorRepository extends PagingAndSortingRepository<Administrator, Long>,
		CustomAdministratorRepository, JpaSpecificationExecutor<Administrator> {

	public Administrator findByEmail(String email);
}
