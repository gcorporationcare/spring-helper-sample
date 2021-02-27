package com.gcorp;

import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.gcorp.entity.Administrator;
import com.gcorp.entity.Product;
import com.gcorp.entity.Shop;
import com.gcorp.entity.User;
import com.gcorp.entity.enumeration.ProductType;
import com.gcorp.entity.enumeration.UserRole;
import com.gcorp.entity.translation.ProductTranslation;
import com.gcorp.field.Country;
import com.gcorp.field.FaxNumber;
import com.gcorp.field.HomeNumber;
import com.gcorp.field.MobileNumber;
import com.gcorp.field.MoneyCurrency;
import com.gcorp.repository.AdministratorRepository;
import com.gcorp.repository.ProductRepository;
import com.gcorp.repository.ShopRepository;
import com.gcorp.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * We use this class to provide data on API start
 * 
 * 
 */
@Slf4j
@Component
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	AdministratorRepository administratorRepository;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ShopRepository shopRepository;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		generateAdministrators();
		generateShops();
		generateUsers();
	}

	private void generateUsers() {
		newUser(UserRole.SIMPLE);
		newUser(UserRole.ADMINISTRATOR);
	}

	private User newUser(UserRole role) {
		return userRepository.save(new User(String.format("user-%s@mail.com", role),
				bCryptPasswordEncoder.encode(role.toString()), role.toString(), role, null));
	}

	private void generateAdministrators() {
		log.info("Providing some default data for administrators");
		List<Administrator> administrators = IntStream.range(1, 11).mapToObj(i -> {
			Administrator administrator = new Administrator(null, String.format("Admin-%d", i),
					String.format("fake-admin-%d@mail.com", i));
			if (i % 3 == 0) {
				// These administrators won't be visible to API
				administrator.setEmail("secret-" + administrator.getEmail());
			}
			return administrator;
		}).collect(Collectors.toList());
		administratorRepository.saveAll(administrators);
		log.info("Saved {} administrators to database", administrators);
	}

	private void generateShops() {
		// 1- US shop
		Shop usShop = newShop(Country.find("us"));
		generateProducts(usShop, MoneyCurrency.find("usd"));
		log.info("Saved US {} shop to database", usShop);

		// 2- FR Shop
		Shop frShop = newShop(Country.find("fr"));
		generateProducts(frShop, MoneyCurrency.find("eur"));
		log.info("Saved FR {} shop to database", frShop);
	}

	private Shop newShop(Country country) {
		final String number = "1";
		Shop shop = new Shop();
		shop.setName(String.format("Shop %s", country.getCode()));
		shop.setCountry(country);
		shop.setEmail(String.format("shop-%s@mail.com", country.getCode()));
		shop.setOpening(LocalTime.of(8, 0));
		shop.setClosing(LocalTime.of(18, 0));
		shop.setFax(new FaxNumber(country, number, number, number));
		shop.setHome(new HomeNumber(country, number, number, number));
		shop.setMobile(new MobileNumber(country, number, number, number));
		return shopRepository.save(shop);
	}

	private void generateProducts(Shop shop, MoneyCurrency currency) {
		List<Product> products = IntStream.range(1, 11).mapToObj(i -> {
			ProductType type = ProductType.STANDARD;
			if (i > 4) {
				type = ProductType.PREMIUM;
			} else if (i > 8) {
				type = ProductType.GOLD;
			}
			return newProduct(i, shop, currency, type);
		}).collect(Collectors.toList());
		LocaleContextHolder.setLocale(Locale.ENGLISH);
		productRepository.saveAll(products);
	}

	private Product newProduct(int index, Shop shop, MoneyCurrency currency, ProductType type) {
		Random random = new Random();
		Product product = new Product();
		product.setPrice(Math.abs(random.nextDouble()));
		product.setShop(shop);
		product.setCurrency(currency);
		product.setType(type);
		final String enName = String.format("%s-product-%d", shop.getCountry().getCode(), index);
		final String enDescription = String.format("%s-description-%d", shop.getCountry().getCode(), index);
		product.setName(enName);
		product.setDescription(enDescription);
		ProductTranslation fr = new ProductTranslation(product,
				String.format("%s-produit-%d", shop.getCountry().getCode(), index),
				String.format("%s-description-%d", shop.getCountry().getCode(), index));
		fr.setLanguage(Locale.FRENCH.getLanguage());
		ProductTranslation en = new ProductTranslation(product, enName, enDescription);
		en.setLanguage(Locale.ENGLISH.getLanguage());
		product.getTranslations().add(fr);
		product.getTranslations().add(en);
		return product;
	}
}
