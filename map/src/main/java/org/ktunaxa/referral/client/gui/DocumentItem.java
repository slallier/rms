/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */
package org.ktunaxa.referral.client.gui;

import org.ktunaxa.referral.client.referral.FileUploadForm;
import org.ktunaxa.referral.client.referral.event.FileUploadCompleteEvent;
import org.ktunaxa.referral.client.referral.event.FileUploadDoneHandler;
import org.ktunaxa.referral.client.referral.event.FileUploadFailedEvent;
import org.ktunaxa.referral.server.service.KtunaxaConstant;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Form item for searching/uploading documents.
 * 
 * @author Jan De Moerloose
 * 
 */
public class DocumentItem extends CanvasItem {

	private Img busyImg;

	private FileUploadForm form;

	private String documentId;

	private String documentTitle;

	private String documentDisplayUrl;

	private String documentDownLoadUrl;
	
	private boolean uploadSuccess;

	@Override
	protected Canvas createCanvas() {
		VLayout layout = createUploadLayout();
		return layout;
	}

	@Override
	public void clearValue() {
		form.clearValues();
		documentId = null;
		documentTitle = null;
		documentDisplayUrl = null;
		documentDownLoadUrl = null;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getDocumentTitle() {
		return documentTitle;
	}

	public String getDocumentDisplayUrl() {
		return documentDisplayUrl;
	}

	public void setDocumentDisplayUrl(String documentDisplayUrl) {
		this.documentDisplayUrl = documentDisplayUrl;
	}

	public String getDocumentDownLoadUrl() {
		return documentDownLoadUrl;
	}

	public void setDocumentDownLoadUrl(String documentDownLoadUrl) {
		this.documentDownLoadUrl = documentDownLoadUrl;
	}

	public void setDocumentTitle(String documentTitle) {
		this.documentTitle = documentTitle;
	}

	@Override
	public void setValue(String value) {
		if (value == null) {
			uploadSuccess = false;
			clearValue();
		} 
		documentId = value;
		super.setValue(value);
	}

	@Override
	public Boolean validate() {
		// validate if we have a file ready for upload !
		if (documentId == null) {
			return form.validate();
		} else {
			return true;
		}
	}

	private VLayout createUploadLayout() {
		VLayout uploadLayout = new VLayout();
		uploadLayout.setMembersMargin(10);
		uploadLayout.setWidth("*");
		uploadLayout.setHeight(16);
		form = new FileUploadForm("Select a file", GWT.getModuleBaseURL() + "../d/upload/referral/document");

		HLayout messageLayout = new HLayout(10);
		busyImg = new Img("[ISOMORPHIC]/images/loading.gif", 16, 16);
		busyImg.setVisible(false);
		messageLayout.addMember(busyImg);
		final HTMLFlow errorFlow = new HTMLFlow();
		errorFlow.setHeight(16);
		errorFlow.setWidth100();
		errorFlow.setVisible(false);
		messageLayout.addMember(errorFlow);

		form.addFileUploadDoneHandler(new FileUploadDoneHandler() {

			@SuppressWarnings("unchecked")
			public void onFileUploadComplete(FileUploadCompleteEvent event) {
				documentId = event.getString(KtunaxaConstant.FORM_DOCUMENT_ID);
				documentTitle = event.getString(KtunaxaConstant.FORM_DOCUMENT_TITLE);
				documentDisplayUrl = event.getString(KtunaxaConstant.FORM_DOCUMENT_DISPLAY_URL);
				documentDownLoadUrl = event.getString(KtunaxaConstant.FORM_DOCUMENT_DOWNLOAD_URL);
				setValue(documentId);
				busyImg.setVisible(false);
				uploadSuccess = true;
				fireEvent(new ChangedEvent(jsObj));
			}

			public void onFileUploadFailed(FileUploadFailedEvent event) {
				busyImg.setVisible(false);
				errorFlow.setContents("<div style='color: #AA0000'>" + event.getErrorMessage() + "</div>");
				errorFlow.setVisible(true);
				uploadSuccess = false;
				fireEvent(new ChangedEvent(jsObj));
			}
		});

		uploadLayout.addMember(form);
		uploadLayout.addMember(messageLayout);

		return uploadLayout;
	}
	
	
	
	public void upload() {
		form.submit();
		busyImg.setVisible(true);
	}

	public boolean isUploadSuccess() {
		return uploadSuccess;
	}

}