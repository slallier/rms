/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */

package org.ktunaxa.referral.client.widget.attribute;

import org.geomajas.gwt.client.widget.attribute.FeatureForm;
import org.geomajas.layer.feature.attribute.AssociationValue;

import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

/**
 * An {@link AttributeBlockDetail} is a widget that shows a detail form of a one-to-many value.
 * 
 * @author Jan De Moerloose
 * 
 * @param <W> the actual widget
 */
public class AttributeBlockDetail<W extends Widget> extends VLayout {

	private FeatureForm<W> form;

	/**
	 * The ToolStrip that contains the different buttons (save, reset, ...).
	 */
	private ToolStrip toolStrip;

	private IButton cancelButton;

	private IButton saveButton;
	
	private AssociationValue value;

	public AttributeBlockDetail(FeatureForm<W> form) {
		this.form = form;
		form.setDisabled(false);
		form.addItemChangedHandler(new ItemChangedHandler() {

			public void onItemChanged(ItemChangedEvent event) {
				updateButtonState(true);
			}

		});
		buildGui();
	}

	public IButton getBackButton() {
		return cancelButton;
	}

	public IButton getSaveButton() {
		return saveButton;
	}

	public AssociationValue getValue() {
		return value;
	}
	
	public boolean validate() {
		return form.validate();
	}

	private void buildGui() {
		setSize("100%", "100%");

		toolStrip = new ToolStrip();
		toolStrip.setWidth100();
		toolStrip.setPadding(2);
		toolStrip.setMembersMargin(5);
		cancelButton = new CancelButton();
		saveButton = new SaveButton();
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth("*");
		toolStrip.addMember(spacer);
		toolStrip.addMember(saveButton);
		toolStrip.addMember(cancelButton);

		VLayout layout = new VLayout(5);
		layout.setWidth100();
		layout.addMember(toolStrip);
		layout.addMember(form.getWidget());
		addMember(layout);
		updateButtonState(false);
	}
	
	private void updateButtonState(boolean formChanged) {
		// TODO: itemchangedevent does not work for selectitem !!!!
//		if (formChanged) {
//			saveButton.setDisabled(false);
//		} else {
//			saveButton.setDisabled(true);
//		}
	}

	/** Definition of the Save button. */
	private class SaveButton extends IButton implements com.smartgwt.client.widgets.events.ClickHandler {

		public SaveButton() {
			setIcon("[ISOMORPHIC]/geomajas/osgeo/save1.png");
			setShowDisabledIcon(false);
			setTitle("Save");
			setTooltip("Upload the document");
			addClickHandler(this);
		}

		public void onClick(ClickEvent event) {
			form.fromForm(value);
			updateButtonState(false);
		}
	}

	// -------------------------------------------------------------------------
	// Private class BackButton:
	// -------------------------------------------------------------------------

	/** Definition of the Back button to go back to the list. */
	private class CancelButton extends IButton implements com.smartgwt.client.widgets.events.ClickHandler {

		public CancelButton() {
			setIcon("[ISOMORPHIC]/geomajas/osgeo/undo.png");
			setShowDisabledIcon(false);
			setTitle("Cancel");
			setTooltip("Cancel editing and return to the list");
		}

		public void onClick(ClickEvent event) {
			form.toForm(value);
			updateButtonState(false);
		}
	}

	// -------------------------------------------------------------------------
	// Private class ResetButton:
	// -------------------------------------------------------------------------

	// -------------------------------------------------------------------------
	// Private class CancelButton:
	// -------------------------------------------------------------------------


	public void showDetails(AssociationValue value) {
		this.value = value;
		form.clear();
		form.toForm(value);
		updateButtonState(false);
	}

	public void fromForm() {
		form.fromForm(value);
	}

}