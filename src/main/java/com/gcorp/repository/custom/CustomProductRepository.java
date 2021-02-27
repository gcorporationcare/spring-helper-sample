package com.gcorp.repository.custom;

import com.gcorp.entity.Product;
import com.gcorp.repository.BaseRepository;

/**
 * In order to add custom method in Spring repository, we need to write a custom
 * interface implementing our custom method. Here we want our repository to
 * inherit the findByFilters method
 *
 */
public interface CustomProductRepository extends BaseRepository<Product, Long> {

}
