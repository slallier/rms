/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */

package org.ktunaxa.referral.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Status value object that referrals can point to. Only a limited set of possible status' are known:
 * <ul>
 * <li>New</li>
 * <li>In progress</li>
 * <li>Finished</li>
 * <li>Stopped</li>
 * </ul>
 * 
 * @author Pieter De Graef
 */
@Entity
@Table(name = "referral_status")
public class ReferralStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/** The general process status of the referral (new, in progress, finished, stopped). */
	@Column(nullable = false, name = "title")
	private String title;

	/** Extra clarification for this type of document, just in case the title alone is not clear enough. */
	@Column(nullable = false, name = "description")
	private String description;

	// ------------------------------------------------------------------------
	// Constructors:
	// ------------------------------------------------------------------------

	public ReferralStatus() {
	}

	public ReferralStatus(long id) {
		this.id = id;
	}

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
	 * @return the value of the identifier.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Get the type of document being stored in system - Internal response, external response, etc...
	 * 
	 * @return What type of document being stored in system - Internal response, external response, etc...
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the type of document being stored in system - Internal response, external response, etc...
	 * 
	 * @param title
	 *            The new document type.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Set extra clarification for this type of document, just in case the title alone is not clear enough.
	 * 
	 * @param description
	 *            The new value of description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Ask extra clarification for this type of document, just in case the title alone is not clear enough.
	 * 
	 * @return Extra clarification for this type of document, just in case the title alone is not clear enough.
	 */
	public String getDescription() {
		return description;
	}
}