package org.ktunaxa.referral.server.dto;

import java.io.Serializable;

/**
 * Definition of type of documents that can be attached to a referral.
 * 
 * @author Pieter De Graef
 */
public class DocumentTypeDto implements Serializable {

	private static final long serialVersionUID = 100L;

	private long id;

	/** What type of document being stored in system - Internal response, external response, etc... */
	private String description;

	// ------------------------------------------------------------------------
	// Constructors:
	// ------------------------------------------------------------------------

	public DocumentTypeDto() {
	};

	public DocumentTypeDto(long id) {
		this.id = id;
	};

	// ------------------------------------------------------------------------
	// Getters and setters:
	// ------------------------------------------------------------------------

	/**
	 * The type's unique identifier.
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