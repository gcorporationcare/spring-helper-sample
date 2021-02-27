package com.gcorp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gcorp.entity.Shop;
import com.gcorp.repository.ShopRepository;
import com.gcorp.service.BaseSearchableService;
import com.gcorp.service.ShopService;

@RestController
@RequestMapping("/shops")
public class ShopController extends BaseRegistrableController<Shop, Long, ShopRepository>{

	@Autowired
	ShopService shopService;

	@Override
	public BaseSearchableService<Shop, Long, ShopRepository> service() {
		return shopService;
	}
}
