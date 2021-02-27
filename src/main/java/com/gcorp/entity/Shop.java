package com.gcorp.entity;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gcorp.annotation.DefaultField;
import com.gcorp.common.Utils;
import com.gcorp.constraint.AllOrNone;
import com.gcorp.constraint.InvalidWhen;
import com.gcorp.constraint.ValidationStep;
import com.gcorp.convention.SqlNamingConvention;
import com.gcorp.domain.FieldFilter;
import com.gcorp.field.Country;
import com.gcorp.field.FaxNumber;
import com.gcorp.field.HomeNumber;
import com.gcorp.field.MobileNumber;
import com.gcorp.field.converter.CountryConverter;
import com.gcorp.field.converter.FaxNumberConverter;
import com.gcorp.field.converter.HomeNumberConverter;
import com.gcorp.field.converter.MobileNumberConverter;
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
@Table(name = "shop")

/**
 * 
 * Spring helper annotation
 *
 */
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
/**
 * 
 * We want to trigger a validation exception when opening time is after closing
 * time
 *
 */
@InvalidWhen("opening != null && opening.isAfter(closing)")
/**
 * We also want opening and closing times to be both provided, or both empty
 *
 */
@AllOrNone(value = { Shop.OPENING_COLUMN, Shop.CLOSING_COLUMN })
public class Shop extends BaseEntity {

	protected static final String NAME_COLUMN = "name";
	protected static final String EMAIL_COLUMN = "email";
	protected static final String COUNTRY_COLUMN = "country";
	protected static final String FAX_COLUMN = "fax";
	protected static final String HOME_COLUMN = "home";
	protected static final String MOBILE_COLUMN = "mobile";
	protected static final String OPENING_COLUMN = "opening";
	protected static final String CLOSING_COLUMN = "closing";

	private static final long serialVersionUID = 1L;

	/**
	 * Name, email and id are by default included in API responses No need to add
	 * the @DefaultField annotation
	 */
	// -------------------------------------------------
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = SqlNamingConvention.Column.ID)
	private Long id;
	// -------------------------------------------------
	@Column(name = Shop.NAME_COLUMN, nullable = false)
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private String name;
	// -------------------------------------------------
	@Column(name = Shop.EMAIL_COLUMN, nullable = false)
	@Email(message = I18nMessage.DataError.EMAIL_VALUE_EXPECTED, groups = { ValidationStep.Simple.class })
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private String email;

	/**
	 * Let's imagine we want the opening and closing fields to be available in API
	 * response, when the user did not specify anything, we just have to add the
	 * default field annotation.
	 */
	// -------------------------------------------------
	@DefaultField
	@Column(name = OPENING_COLUMN)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SqlNamingConvention.ColumnUtils.API_TIME_FORMAT)
	private LocalTime opening;
	// -------------------------------------------------
	@DefaultField
	@Column(name = CLOSING_COLUMN)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = SqlNamingConvention.ColumnUtils.API_TIME_FORMAT)
	private LocalTime closing;

	/**
	 * For the custom object-fields, we just need to specify the corresponding
	 * converter
	 */
	// -------------------------------------------------
	@Convert(converter = CountryConverter.class)
	@Column(name = COUNTRY_COLUMN, nullable = false)
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private Country country;
	// -------------------------------------------------
	@Convert(converter = FaxNumberConverter.class)
	@Column(name = FAX_COLUMN, nullable = false)
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private FaxNumber fax;
	// -------------------------------------------------
	@Convert(converter = HomeNumberConverter.class)
	@Column(name = HOME_COLUMN, nullable = false)
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private HomeNumber home;
	// -------------------------------------------------
	@Convert(converter = MobileNumberConverter.class)
	@Column(name = MOBILE_COLUMN, nullable = false)
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private MobileNumber mobile;

	@Override
	public void format() {
		// This portion of code will be applied every time we register something to
		// database
		email = email.toLowerCase().trim();
		name = Utils.getProperNoun(name);
	}
}
