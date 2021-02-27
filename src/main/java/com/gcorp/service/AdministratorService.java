package com.gcorp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gcorp.domain.SearchFilter;
import com.gcorp.domain.SearchFilter.SearchFilterOperator;
import com.gcorp.domain.SearchFilters;
import com.gcorp.entity.Administrator;
import com.gcorp.repository.AdministratorRepository;

/**
 * Service for Administrators, we will enable read only end points only The data
 * will be inserted by the application at start and won't be modified by users.
 *
 */
@Service
public class AdministratorService extends BaseSearchableService<Administrator, Long, AdministratorRepository> {

	@Autowired
	AdministratorRepository administratorRepository;

	@Override
	public AdministratorRepository repository() {
		return administratorRepository;
	}

	/**
	 * Let's imagine we do not want administrator with the keyword "secret" to be
	 * visible on the API
	 */
	@Override
	public SearchFilters<Administrator> getDefaultFilters() {
		return SearchFilters.of(new SearchFilter(true, "email", SearchFilterOperator.IS_LIKE, "secret"));
	}
}
