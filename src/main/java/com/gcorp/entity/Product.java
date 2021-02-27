package com.gcorp.entity;

import java.beans.Transient;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gcorp.annotation.Configure;
import com.gcorp.annotation.DefaultField;
import com.gcorp.annotation.FieldSort;
import com.gcorp.annotation.NotCopyable;
import com.gcorp.annotation.Translated;
import com.gcorp.common.Utils;
import com.gcorp.constraint.InvalidWhen;
import com.gcorp.constraint.ValidationStep;
import com.gcorp.convention.SqlNamingConvention;
import com.gcorp.domain.FieldFilter;
import com.gcorp.entity.enumeration.ProductType;
import com.gcorp.entity.translation.ProductTranslation;
import com.gcorp.exception.ValidationException;
import com.gcorp.field.MoneyCurrency;
import com.gcorp.field.converter.MoneyCurrencyConverter;
import com.gcorp.i18n.I18nMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LomBok annotations for avoiding vanilla code
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/**
 * Persistence annotations
 */
@Entity
@Table(name = "product")

/**
 * 
 * Spring helper annotation
 *
 */
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
/**
 * We want the products to be browsed in the following order 1. Sorted by
 * ascending order of linked shop's name (we use the field and not the column's
 * name)<br/>
 * 2. Sorted by ascending order of names<br/>
 * 3. Sorted by highest prices<br/>
 */
@Configure(defaultSort = { @FieldSort("shop.name"), @FieldSort(Product.NAME_COLUMN),
		@FieldSort(value = Product.PRICE_COLUMN, ascending = false) })
public class Product extends BaseTranslatableEntity<ProductTranslation> {

	protected static final String TYPE_COLUMN = "type";
	protected static final String CODE_COLUMN = "code";
	protected static final String NAME_COLUMN = "name";
	protected static final String PRICE_COLUMN = "price";
	protected static final String SHOP_ID_COLUMN = "shop_id";
	protected static final String CURRENCY_COLUMN = "currency";
	protected static final String DESCRIPTION_COLUMN = "description";

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = SqlNamingConvention.Column.ID)
	private Long id;

	// -------------------------------------------------
	/**
	 * We will later add a possibility to create product from another.<br/>
	 * This field is computed and initialized only if null, so we do not want it to
	 * be copied to duplicate.
	 */
	@NotCopyable
	@Column(name = Product.CODE_COLUMN, nullable = false)
	private String code;
	// -------------------------------------------------
	@DefaultField
	@Column(name = Product.PRICE_COLUMN, nullable = false)
	private double price;
	// -------------------------------------------------
	@DefaultField
	@Convert(converter = MoneyCurrencyConverter.class)
	@Column(name = CURRENCY_COLUMN, nullable = false)
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private MoneyCurrency currency;
	// -------------------------------------------------
	@ManyToOne
	@JoinColumn(name = Product.SHOP_ID_COLUMN)
	private Shop shop;
	// -------------------------------------------------
	@DefaultField
	@Enumerated(EnumType.STRING)
	@Column(name = Product.TYPE_COLUMN, nullable = false)
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private ProductType type;

	/**
	 * The @Translated annotation is needed on these fields in order to know which
	 * fields must be replaced on loading depending on user language
	 */
	// -------------------------------------------------
	@Translated
	@Column(name = Product.NAME_COLUMN, nullable = false)
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private String name;
	// -------------------------------------------------
	@Translated
	@Column(name = Product.DESCRIPTION_COLUMN, nullable = false)
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private String description;

	/**
	 * Copy the information of a given product
	 * 
	 * @param product the product to copy values from
	 */
	public void duplicate(Product product) {
		this.copy(product, new String[] { "translations" });
		this.name = String.format("%s (copy)", name);
	}

	@Override
	public void format() {
		if (code == null) {
			// We can use format when we need computed values
			code = UUID.randomUUID().toString();
		}

		name = Utils.getProperNoun(name);
		if (price <= 0) {
			// We can also use format method in order to trigger some manual validations
			throw new ValidationException(I18nMessage.DataError.FORBIDDEN_VALUE_GIVEN, Utils.generateViolations(
					InvalidWhen.class, I18nMessage.DataError.FORBIDDEN_VALUE_GIVEN, this, PRICE_COLUMN, price));
		}
		if (description == null) {
			// Or use it in order to set default values
			description = "N/A";
		}
	}

	@Override
	@JsonIgnore // Important since we do not want it to be part of API responses
	@Transient // Important since not a field to save into database
	public String getCurrentLanguage() {
		// We get the current locale to defined which language is enabled
		Locale locale = LocaleContextHolder.getLocale();
		return locale.getLanguage();
	}
}
