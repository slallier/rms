/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Geosparc nv, http://www.geosparc.com/, Belgium.
 * Copyright 2011 Ktunaxa Nation Counsil, http://www.ktunaxa.org/, Canada.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.ktunaxa.referral.server.dto;

import java.io.Serializable;

/**
 * Status value object that referrals can point to. Only a limited set of possible status' are known:
 * <ul>
 * <li>New</li>
 * <li>In progress</li>
 * <li>Approved</li>
 * <li>Denied</li>
 * </ul>
 * 
 * @author Pieter De Graef
 */
public class ReferralStatusDto implements Serializable {

	private static final long serialVersionUID = 100L;

	private long id;

	private String description;

	// ------------------------------------------------------------------------
	// Constructors:
	// ------------------------------------------------------------------------

	public ReferralStatusDto() {
	};

	public ReferralStatusDto(long id) {
		this.id = id;
	};

	// ------------------------------------------------------------------------
	// Getters and setters:
	// ------------------------------------------------------------------------

	/**
	 * The aspect's unique identifier.
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
	 * @return The value of the identifier.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Set the value of description.
	 * 
	 * @param description
	 *            The new value of description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the value of description.
	 * 
	 * @return The value of description.
	 */
	public String getDescription() {
		return description;
	}
}