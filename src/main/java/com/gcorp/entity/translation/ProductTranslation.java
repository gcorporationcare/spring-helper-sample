package com.gcorp.entity.translation;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gcorp.convention.SqlNamingConvention;
import com.gcorp.entity.BaseTranslation;
import com.gcorp.entity.Product;
import com.google.common.base.Strings;

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
 * Persistence annotations<br/>
 * Unique constraint since we do not want to have 2 translations for same
 * language
 */
@Entity
@Table(name = "product_translation", uniqueConstraints = {
		@UniqueConstraint(columnNames = { SqlNamingConvention.Column.LANGUAGE, ProductTranslation.SOURCE_ID_COLUMN }) })
public class ProductTranslation extends BaseTranslation {

	protected static final String NAME_COLUMN = "name";
	protected static final String SOURCE_ID_COLUMN = "source_id";
	protected static final String DESCRIPTION_COLUMN = "description";

	private static final long serialVersionUID = 1L;
	// -------------------------------------------------
	@NotNull
	@JsonBackReference
	@ManyToOne(optional = false)
	@JoinColumn(name = ProductTranslation.SOURCE_ID_COLUMN)
	protected Product source;
	// -------------------------------------------------
	@NotNull
	@NotEmpty
	@Column(name = ProductTranslation.NAME_COLUMN, nullable = false)
	private String name;
	// -------------------------------------------------
	@NotNull
	@NotEmpty
	@Column(name = ProductTranslation.DESCRIPTION_COLUMN, nullable = false)
	private String description;

	@Override
	public boolean translated() {
		return !Strings.isNullOrEmpty(description) && !Strings.isNullOrEmpty(name);
	}

}
