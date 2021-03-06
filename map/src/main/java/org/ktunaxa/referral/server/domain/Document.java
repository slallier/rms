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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A document that can be associated with a certain referral. This document has some meta-data to further clarify it's
 * contents, and can possibly be included in the final report.
 * 
 * @author Pieter De Graef
 */
@Entity
@Table(name = "document")
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	/** This document's title. */
	@Column(nullable = false, name = "title")
	private String title;

	/** A short description to further clarify this document. */
	@Column(nullable = false, name = "description")
	private String description;

	/** Document display URL in the CMIS */
	@Column(nullable = false, name = "display_url")
	private String displayUrl;

	/** Document download URL in the CMIS */
	@Column(nullable = false, name = "download_url")
	private String downloadUrl;

	/** What type of document being stored in system - Internal response, external response, etc... */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "type_id", nullable = false)
	private DocumentType type = DocumentType.DEFAULT;

	/** The unique identifier for the document within the CMS. */
	@Column(nullable = false, name = "document_id")
	private String documentId;

	/** The date at which this document was added to the associated referral. */
	@Column(nullable = false, name = "addition_date")
	private Date additionDate = new Date();

	/** The user who added this document to the associated referral. */
	@Column(name = "added_by")
	private String addedBy;

	/** Is this document visible only to KLRA staff? */
	@Column(nullable = false, name = "confidential")
	private boolean confidential;

	/** Should this document be added to the final report? */
	@Column(nullable = false, name = "include_in_report")
	private boolean includeInReport;

	/** The associated referral. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "referral_id", nullable = false)
	private Referral referral;

	// ------------------------------------------------------------------------
	// Constructors:
	// ------------------------------------------------------------------------

	public Document() {
	};

	public Document(long id) {
		this.id = id;
	};

	// ------------------------------------------------------------------------
	// Getters and setters:
	// ------------------------------------------------------------------------

	/**
	 * The document's unique identifier.
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
	 * Get this document's title.
	 * 
	 * @return This document's title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set this document's title.
	 * 
	 * @param title
	 *            The new title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Return a short description to further clarify this document.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set a short description to further clarify this document.
	 * 
	 * @param description
	 *            The new description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the document download URL in the CMS.
	 * 
	 * @return document download URL
	 */
	public String getDownloadUrl() {
		return downloadUrl;
	}
	
	/**
	 * Sets the document download URL in the CMS.
	 * 
	 * @param document download URL
	 */
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	/**
	 * Returns the document display URL in the CMS.
	 * 
	 * @return document display URL
	 */
	public String getDisplayUrl() {
		return displayUrl;
	}
	
	/**
	 * Sets the document display URL in the CMS.
	 * 
	 * @param document display URL
	 */
	public void setDisplayUrl(String displayUrl) {
		this.displayUrl = displayUrl;
	}

	/**
	 * Returns the unique identifier for the document within the CMS.
	 * 
	 * @return The document identifier.
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * Set the unique identifier for the document within the CMS.
	 * 
	 * @param documentId
	 *            The new value.
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * Return the date at which this document was added to the associated referral.
	 * 
	 * @return The addition-date.
	 */
	public Date getAdditionDate() {
		return additionDate;
	}

	/**
	 * Set the date at which this document was added to the associated referral.
	 * 
	 * @param additionDate
	 *            The new date.
	 */
	public void setAdditionDate(Date additionDate) {
		this.additionDate = additionDate;
	}

	/**
	 * Return the user who added this document to the associated referral.
	 * 
	 * @return The user who added this document to the associated referral.
	 */
	public String getAddedBy() {
		return addedBy;
	}

	/**
	 * Set the user who added this document to the associated referral.
	 * 
	 * @param addedBy
	 *            The new value.
	 */
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}

	/**
	 * Return of this document is visible only to KLRA staff.
	 * 
	 * @return Is this document visible only to KLRA staff?
	 */
	public boolean isConfidential() {
		return confidential;
	}

	/**
	 * Determine whether or not this document is visible only to KLRA staff.
	 * 
	 * @param confidential
	 *            True or false.
	 */
	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
	}

	/**
	 * Return if this document should be added to the final report.
	 * 
	 * @return Should this document be added to the final report?
	 */
	public boolean isIncludeInReport() {
		return includeInReport;
	}

	/**
	 * Determine whether or not this document should be added to the final report.
	 * 
	 * @param includeInReport
	 *            True or false.
	 */
	public void setIncludeInReport(boolean includeInReport) {
		this.includeInReport = includeInReport;
	}

	/**
	 * The associated referral.
	 * 
	 * @return The associated referral.
	 */
	public Referral getReferral() {
		return referral;
	}

	/**
	 * Set the associated referral.
	 * 
	 * @param referral
	 *            The associated referral.
	 */
	public void setReferral(Referral referral) {
		this.referral = referral;
	}

	/**
	 * Get the type of document being stored in system - Internal response, external response, etc...
	 * 
	 * @return What type of document being stored in system - Internal response, external response, etc...
	 */
	public DocumentType getType() {
		return type;
	}

	/**
	 * Set the type of document being stored in system - Internal response, external response, etc...
	 * 
	 * @param type
	 *            The new document type.
	 */
	public void setType(DocumentType type) {
		this.type = type;
	}
}