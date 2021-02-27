package com.gcorp.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcorp.entity.Product;
import com.gcorp.repository.custom.CustomProductRepository;

/**
 * We can extends JPA interface extending PagingAndSortingRepository.<br/>
 * The extension of JpaSpecificationExecutor and CustomProductRepository are
 * mandatory.<br/>
 * The first one enables the use of specifications on our repository.<br/>
 * The second one will help us adding the custom method findByFilters which will
 * be used for generating dynamic specification from requested URL.
 */
public interface ProductRepository
		extends PagingAndSortingRepository<Product, Long>, CustomProductRepository, JpaSpecificationExecutor<Product> {

}
