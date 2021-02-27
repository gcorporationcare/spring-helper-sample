package com.gcorp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gcorp.domain.FieldFilter;
import com.gcorp.entity.Product;
import com.gcorp.entity.Shop;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.repository.ProductRepository;
import com.gcorp.repository.ShopRepository;
import com.gcorp.service.BaseChildSearchableService;
import com.gcorp.service.ProductService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * The keyword "parent" in URL mapping is mandatory for child services.
 *
 */
@RestController
@RequestMapping("/shops/{parent}/products")
public class ProductController
		extends BaseChildRegistrableController<Product, Long, Shop, Long, ProductRepository, ShopRepository> {
	@Autowired
	ProductService productService;

	@Override
	public BaseChildSearchableService<Product, Long, Long, ProductRepository> service() {
		return productService;
	}

	/**
	 * We must deal with our custom services method ourselves
	 * 
	 * @param shopId      the parent's shop
	 * @param id          the ID of the product we're copying
	 * @param fieldFilter will be parsed by resolver
	 * @return the duplicated shop
	 */
	@ResponseBody
	@ApiOperation(value = "duplicate", notes = "Duplicate a given product")
	@ApiImplicitParams({
			@ApiImplicitParam(name = I18nMessage.FIELDS_PARAMETER, dataType = I18nMessage.STRING_DATA_TYPE, paramType = I18nMessage.QUERY_PARAM_TYPE, value = I18nMessage.FIELDS_PARAMETER_DESCRIPTION) })
	@PostMapping(value = "/{id}/vote", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Product duplicate(@PathVariable(I18nMessage.PARENT_PARAMETER) Long shopId,
			@PathVariable(I18nMessage.ID_PARAMETER) Long id, FieldFilter<Product> fieldFilter) {
		return productService.duplicate(shopId, id, fieldFilter);
	}
}
