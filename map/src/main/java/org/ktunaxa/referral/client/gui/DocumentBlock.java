/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */
package org.ktunaxa.referral.client.gui;

import java.util.Date;

import org.geomajas.layer.feature.attribute.AssociationValue;
import org.ktunaxa.referral.client.widget.attribute.AbstractAttributeBlock;
import org.ktunaxa.referral.server.service.KtunaxaConstant;

import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;

/**
 * Block that shows document information.
 * 
 * @author Jan De Moerloose
 * 
 */
public class DocumentBlock extends AbstractAttributeBlock {

	private HLayout title;

	private HLayout infoLayout;

	private HTMLFlow content;

	private HTMLFlow titleText;

	private HTMLFlow info;

	private IButton editButton;

	private IButton deleteButton;

	private Img checked;

	public DocumentBlock(AssociationValue value) {
		super(value);
		buildGui();
	}

	@Override
	public void expand() {
		setStyleName("documentBlock");
		title.setStyleName("documentBlockTitle");
		infoLayout.setVisible(true);
		content.setVisible(true);
	}

	@Override
	public void collapse() {
		setStyleName("documentBlockCollapsed");
		title.setStyleName("documentBlockTitleCollapsed");
		infoLayout.setVisible(false);
		content.setVisible(false);
	}

	@Override
	protected boolean valueEquals(AbstractAttributeBlock other) {
		AssociationValue otherValue = other.getValue();
		AssociationValue value = getValue();
		if (otherValue == null || value == null) {
			return false;
		} else if (otherValue.getId().isEmpty() || value.getId().isEmpty()) {
			return value.getAttributeValue(KtunaxaConstant.ATTRIBUTE_DOCUMENT_ID).equals(
					otherValue.getAttributeValue(KtunaxaConstant.ATTRIBUTE_DOCUMENT_ID));
		} else {
			return otherValue.getId().getValue().equals(value.getId().getValue());
		}
	}

	@Override
	public void redrawValue() {
		titleText.setContents("<div class='documentBlockTitleText'>" + getDocumentTitle() + "</div>");
		info.setContents("<div class='documentBlockInfo'>Added by " + getAddedBy() + " @ " + getAdditionDate()
				+ "</div>");
		content.setContents("<div class='documentBlockType'>" + "Description : " + getDocumentDescription()
				+ "<br>Type : " + getDocumentTypeTitle() + "<br>" + "<a target='_ktunaxa_doc' href='"
				+ getDocumentDisplayUrl() + "'>Open</a>  " + "<a href='" + getDocumentDownloadUrl() + "'>Save</a>"
				+ "</div>");
		if (isIncludeInReport()) {
			checked.setSrc("[ISOMORPHIC]/skins/ActivitiBlue/images/MultiUploadItem/icon_add_files.png");
		} else {
			checked.setSrc("[ISOMORPHIC]/skins/ActivitiBlue/images/MultiUploadItem/icon_remove_files.png");
		}
	}

	@Override
	public String getDeleteMessage() {
		return "Are you sure you want to delete this document ?";
	}

	private void buildGui() {
		setStyleName("documentBlock");

		title = new HLayout(LayoutConstant.MARGIN_LARGE);
		title.setSize(LayoutConstant.BLOCK_TITLE_WIDTH, LayoutConstant.BLOCK_TITLE_HEIGHT);
		title.setLayoutLeftMargin(LayoutConstant.MARGIN_LARGE);
		title.setStyleName("documentBlockTitle");
		title.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (content.isVisible()) {
					collapse();
				} else {
					expand();
				}
			}
		});
		title.setCursor(Cursor.HAND);
		checked = new Img("[ISOMORPHIC]/skins/ActivitiBlue/images/MultiUploadItem/icon_remove_files.png", 16, 16);
		if (isIncludeInReport()) {
			checked.setSrc("[ISOMORPHIC]/skins/ActivitiBlue/images/MultiUploadItem/icon_add_files.png");
		}
		checked.setLayoutAlign(VerticalAlignment.CENTER);
		title.addMember(checked);
		titleText = new HTMLFlow();
		titleText.setSize(LayoutConstant.BLOCK_TITLE_WIDTH, LayoutConstant.BLOCK_TITLE_HEIGHT);
		title.addMember(titleText);
		addMember(title);

		infoLayout = new HLayout(LayoutConstant.MARGIN_SMALL);
		infoLayout.setHeight(24);
		infoLayout.setLayoutRightMargin(LayoutConstant.MARGIN_SMALL);
		infoLayout.setLayoutTopMargin(LayoutConstant.MARGIN_SMALL);
		info = new HTMLFlow();
		info.setSize("*", "24");
		infoLayout.addMember(info);

		LayoutSpacer space = new LayoutSpacer();
		space.setWidth(LayoutConstant.SPACER_LARGE);
		infoLayout.addMember(space);

		editButton = new IButton();
		editButton.setIcon("[ISOMORPHIC]/geomajas/osgeo/edit.png");
		editButton.setIconWidth(LayoutConstant.ICON_BUTTON_SMALL_ICON_WIDTH);
		editButton.setIconHeight(LayoutConstant.ICON_BUTTON_SMALL_ICON_HEIGHT);
		editButton.setWidth(LayoutConstant.ICON_BUTTON_SMALL_WIDTH);
		editButton.setHeight(LayoutConstant.ICON_BUTTON_SMALL_HEIGHT);
		editButton.setTooltip("Edit comment");
		infoLayout.addMember(editButton);

		deleteButton = new IButton();
		deleteButton.setIcon("[ISOMORPHIC]/geomajas/silk/remove.png");
		deleteButton.setIconWidth(LayoutConstant.ICON_BUTTON_SMALL_ICON_WIDTH);
		deleteButton.setIconHeight(LayoutConstant.ICON_BUTTON_SMALL_ICON_HEIGHT);
		deleteButton.setWidth(LayoutConstant.ICON_BUTTON_SMALL_WIDTH);
		deleteButton.setHeight(LayoutConstant.ICON_BUTTON_SMALL_HEIGHT);
		deleteButton.setTooltip("Delete comment");
		deleteButton.setHoverWrap(false);
		infoLayout.addMember(deleteButton);
		addMember(infoLayout);

		content = new HTMLFlow();
		content.setHeight(20);
		content.setWidth100();
		redrawValue();
		addMember(content);
	}

	private String getDocumentTypeTitle() {
		AssociationValue type = (AssociationValue) getValue().getAttributeValue("type");
		if (type != null) {
			return (String) type.getAttributeValue("title");
		}
		return "";
	}

	private String getDocumentTitle() {
		return (String) getValue().getAttributeValue("title");
	}

	private String getDocumentDescription() {
		return (String) getValue().getAttributeValue(KtunaxaConstant.ATTRIBUTE_DOCUMENT_DESCRIPTION);
	}

	private String getDocumentDisplayUrl() {
		return (String) getValue().getAttributeValue("displayUrl");
	}

	private String getDocumentDownloadUrl() {
		return (String) getValue().getAttributeValue("downloadUrl");
	}

	private Date getAdditionDate() {
		return (Date) getValue().getAttributeValue("additionDate");
	}

	private String getAddedBy() {
		return (String) getValue().getAttributeValue("addedBy");
	}

	private Boolean isIncludeInReport() {
		return (Boolean) getValue().getAttributeValue("includeInReport");
	}

	@Override
	protected boolean evaluate(String filter) {
		if (filter != null) {
			String lcText = filter.toLowerCase();
			String compare = getDocumentTitle();
			if (compare != null && compare.toLowerCase().contains(lcText)) {
				return true;
			}
			compare = getDocumentDescription();
			if (compare != null && compare.toLowerCase().contains(lcText)) {
				return true;
			}
			compare = getAddedBy();
			if (compare != null && compare.toLowerCase().contains(lcText)) {
				return true;
			}
			compare = getDocumentTypeTitle();
			if (compare != null && compare.toLowerCase().contains(lcText)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void addEditHandler(ClickHandler clickHandler) {
		editButton.addClickHandler(clickHandler);
	}

	@Override
	public void addDeleteHandler(ClickHandler clickHandler) {
		deleteButton.addClickHandler(clickHandler);
	}

}
