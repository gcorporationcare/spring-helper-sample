package com.gcorp.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.gcorp.common.Utils;
import com.gcorp.entity.Shop;
import com.gcorp.entity.User;
import com.gcorp.entity.enumeration.UserRole;
import com.gcorp.exception.RequestException;
import com.gcorp.field.PhoneNumber;
import com.gcorp.i18n.I18nMessage;
import com.gcorp.repository.ShopRepository;

import lombok.NonNull;

/**
 * Service for shops, we will enable read and write operations, but we will do
 * some test in order to avoid saving inconsistent data.
 *
 */
@Service
public class ShopService extends BaseRegistrableService<Shop, Long, ShopRepository> {

	@Autowired
	ShopRepository shopRepository;

	@Override
	public void canCreate(@NonNull Shop shop) {
		// Here we can do extra validation to verify the consistency of the data we're
		// trying to create
		// Let's imagine we do not want to create shop with numbers not being in same
		// area
		List<PhoneNumber> numbers = Arrays.asList(shop.getFax(), shop.getHome(), shop.getMobile());
		List<PhoneNumber> invalid = numbers.stream().filter(p -> !shop.getCountry().equals(p.getAreaCode()))
				.collect(Collectors.toList());
		if (invalid.isEmpty()) {
			return;
		}
		throw new RequestException("shop.not_same_area", HttpStatus.BAD_REQUEST);
	}

	@Override
	public void canDelete(@NonNull Shop shop) {
		// We want logged user only to be able to remove shops
		User currentUser = (User) Utils.getAuthenticatedUser();
		if (currentUser == null || !UserRole.ADMINISTRATOR.equals(currentUser.getRole())) {
			throw new RequestException(I18nMessage.RequestError.FORBIDDEN_OPERATION, HttpStatus.FORBIDDEN);
		}
	}

	@Override
	public void canUpdate(@NonNull Shop newShop, @NonNull Shop oldShop) {
		if (oldShop.getCountry().equals(newShop.getCountry())) {
			return;
		}
		// We do not want to relocate countries
		throw new RequestException("shop.cant_be_relocated", HttpStatus.FORBIDDEN);
	}

	@Override
	public ShopRepository repository() {
		return shopRepository;
	}

}
