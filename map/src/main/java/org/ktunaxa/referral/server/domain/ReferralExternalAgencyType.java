/*
 * Ktunaxa Referral Management System.
 *
 * Copyright (C) see version control system
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ktunaxa.referral.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Definition of an external agency type for referrals. Possibilities are:
 * <ul>
 * <li></li>
 * </ul>
 * 
 * @author Joachim Van der Auwera
 */
@Entity
@Table(name = "referral_external_agency_type")
public class ReferralExternalAgencyType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/** Title for the external agency type. */
	@Column(nullable = false, name = "title")
	private String title;

	/** Description for the external agency type. Extra clarification in case the title is not clear enough. */
	@Column(nullable = false, name = "description")
	private String description;

	// ------------------------------------------------------------------------
	// Constructors:
	// ------------------------------------------------------------------------

	public ReferralExternalAgencyType() {
	}

	public ReferralExternalAgencyType(long id) {
		this.id = id;
	}

	// ------------------------------------------------------------------------
	// Getters and setters:
	// ------------------------------------------------------------------------

	/**
	 * Unique identifier for the external agency type.
	 * 
	 * @param id
	 *            The new value for the identifier.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Get the value of the identifier.
	 * 
	 * @return the value of the identifier.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Get the title of the external agency type.
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the title of the external agency type.
	 * 
	 * @param title title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Set the description for the external agency type.
	 * <p/>
	 * Extra clarification for this external agency type, in case the title is not clear enough.
	 *
	 * @param description description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Set the description for the external agency type.
	 * <p/>
	 * Extra clarification for this external agency type, in case the title is not clear enough.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
}