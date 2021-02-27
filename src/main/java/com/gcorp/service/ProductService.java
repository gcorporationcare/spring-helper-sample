package com.gcorp.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.gcorp.common.Utils;
import com.gcorp.domain.FieldFilter;
import com.gcorp.entity.Product;
import com.gcorp.entity.Shop;
import com.gcorp.entity.User;
import com.gcorp.entity.enumeration.UserRole;
import com.gcorp.exception.RequestException;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.repository.ProductRepository;
import com.gcorp.repository.ShopRepository;

import lombok.NonNull;

/**
 * Service for products, it is a fact a sub-service of shops; We will enable
 * read and write operations allowing users to do operations on products through
 * their owning shop. When we will use the term "parent", we will talk about the
 * owning shop.
 *
 */
@Service
public class ProductService
		extends BaseChildRegistrableService<Product, Long, Shop, Long, ProductRepository, ShopRepository> {

	@Autowired
	ShopRepository shopRepository;
	@Autowired
	ProductRepository productRepository;

	/**
	 * We can also add custom methods (just like normal services). We can even use
	 * the other tools-objects like FieldFilter
	 * 
	 * @return the copy of given product
	 */
	public Product duplicate(@NonNull Long shopId, @NonNull Long id, @NonNull FieldFilter<Product> fieldFilter) {
		Optional<Product> product = productRepository.findById(id);
		if (!product.isPresent()) {
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
		}
		if (!shopId.equals(product.get().getShop().getId())) {
			throw new RequestException("product.copy_from_another_shop", HttpStatus.BAD_REQUEST);
		}
		Product newProduct = new Product();
		newProduct.duplicate(product.get());
		newProduct = productRepository.save(newProduct);
		return fieldFilter.parseEntity(newProduct);
	}

	@Override
	public void canCreate(@NonNull Long shopId, @NonNull Product product) {
		// We can use this method to do automatic stuffs like browsing the shop
		Optional<Shop> shop = shopRepository.findById(shopId);
		if (!shop.isPresent()) {
			throw new RequestException(I18nMessage.RequestError.OBJECT_NOT_FOUND, HttpStatus.NOT_FOUND);
		}
		product.setShop(shop.get());
	}

	@Override
	public void canDelete(@NonNull Long shopId, @NonNull Product product) {
		// We want logged user only to be able to remove products
		User currentUser = (User) Utils.getAuthenticatedUser();
		if (currentUser == null || !UserRole.ADMINISTRATOR.equals(currentUser.getRole())) {
			throw new RequestException(I18nMessage.RequestError.FORBIDDEN_OPERATION, HttpStatus.FORBIDDEN);
		}
	}

	@Override
	public void canUpdate(@NonNull Long shopId, @NonNull Product newProduct, @NonNull Product oldProduct) {
		if (oldProduct.getCode().equals(newProduct.getCode())) {
			return;
		}
		throw new RequestException("product.cant_change_code", HttpStatus.FORBIDDEN);
	}

	@Override
	public ShopRepository parentRepository() {
		return shopRepository;
	}

	@Override
	public ProductRepository repository() {
		return productRepository;
	}

	@Override
	public String getParentField() {
		// Parent's field
		return "shop";
	}

	@Override
	public String getParentIdField() {
		// Parent property.ID property
		return "shop.id";
	}

}
