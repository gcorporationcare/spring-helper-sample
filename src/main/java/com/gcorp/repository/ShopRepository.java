package com.gcorp.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.gcorp.entity.Shop;
import com.gcorp.repository.custom.CustomShopRepository;

/**
 * We can extends JPA interface extending PagingAndSortingRepository.<br/>
 * The extension of JpaSpecificationExecutor and CustomShopRepository are
 * mandatory.<br/>
 * The first one enables the use of specifications on our repository.<br/>
 * The second one will help us adding the custom method findByFilters which will
 * be used for generating dynamic specification from requested URL.
 */
public interface ShopRepository
		extends PagingAndSortingRepository<Shop, Long>, CustomShopRepository, JpaSpecificationExecutor<Shop> {

}
