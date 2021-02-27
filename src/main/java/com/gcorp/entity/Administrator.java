package com.gcorp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.gcorp.common.Utils;
import com.gcorp.convention.SqlNamingConvention;
import com.gcorp.domain.FieldFilter;

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
@Table(name = "administrator")

/**
 * 
 * Spring helper annotation
 *
 */
@JsonFilter(FieldFilter.JSON_FILTER_NAME)
public class Administrator extends BaseEntity {

	private static final String NAME_COLUMN = "name";
	private static final String EMAIL_COLUMN = "email";

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = SqlNamingConvention.Column.ID)
	private Long id;
	// -------------------------------------------------
	@Column(name = Administrator.NAME_COLUMN, nullable = false)
	private String name;
	// -------------------------------------------------
	@Column(name = Administrator.EMAIL_COLUMN, nullable = false)
	private String email;

	@Override
	public void format() {
		email = email.toLowerCase().trim();
		name = Utils.getProperNoun(name);
	}
}
