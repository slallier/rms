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

package org.ktunaxa.referral.client;

import org.geomajas.gwt.client.widget.attribute.AttributeFormFieldRegistry;
import org.geomajas.gwt.client.widget.attribute.AttributeFormFieldRegistry.DataSourceFieldFactory;
import org.geomajas.gwt.client.widget.attribute.AttributeFormFieldRegistry.FormItemFactory;
import org.ktunaxa.referral.client.gui.CommentPanel;
import org.ktunaxa.referral.client.gui.DocumentPanel;
import org.ktunaxa.referral.client.gui.LayerPanel;
import org.ktunaxa.referral.client.gui.MainGui;
import org.ktunaxa.referral.client.gui.ReferralPanel;
import org.ktunaxa.referral.client.gui.SearchPanel;
import org.ktunaxa.referral.client.i18n.LocalizedMessages;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.SelectionType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripSeparator;

/**
 * Entry point and main class for GWT application. This class defines the layout and functionality of this application.
 * 
 * @author Pieter De Graef
 */
public class KtunaxaEntryPoint implements EntryPoint {

	private static final String RFA_ID = "MIN001";

	private static final String RFA_TITLE = "Mining SoilDigger";

	private static final String RFA_DESCRIPTION = "SoilDigger wants to create a copper mine. "
			+ "It will provide work for 12 permanent and sometimes up to 20 people. "
			+ "Twenty tons will be excavated daily. And I need more text because I want to see it being truncated. "
			+ "Even when the screen is large and the display font for this description is small.";

	private LocalizedMessages messages = GWT.create(LocalizedMessages.class);

	private MainGui mainGui;

	private LayerPanel layerPanel;

	private SearchPanel searchPanel;

	private VLayout printPanel;

	private DocumentPanel documentPanel;

	private CommentPanel commentPanel2;

	private VLayout helpPanel;

	public void onModuleLoad() {
		AttributeFormFieldRegistry.registerCustomFormItem("textArea", new DataSourceFieldFactory() {

			public DataSourceField create() {
				return new DataSourceTextField();
			}
		}, new FormItemFactory() {

			public FormItem create() {
				return new TextAreaItem();
			}
		}, null);

		VLayout layout = new VLayout();
		layout.setWidth100();
		layout.setHeight100();

		mainGui = new MainGui();

		layout.addMember(createHeader());
		layout.addMember(createMenuBar());

		VLayout subHeader = new VLayout();
		subHeader.setSize("100%", "15px");
		subHeader.setStyleName("subHeader");
		layout.addMember(subHeader);

		layout.addMember(mainGui);
		layout.draw();
	}

	private Canvas createHeader() {
		HLayout header = new HLayout();
		header.setSize("100%", "44");
		header.setStyleName("header");

		HTMLFlow rfaLabel = new HTMLFlow(messages.applicationTitle(RFA_ID, RFA_TITLE));
		rfaLabel.setStyleName("headerText");
		rfaLabel.setTooltip(RFA_DESCRIPTION);
		rfaLabel.setHoverWidth(700);
		rfaLabel.setWidth100();
		header.addMember(rfaLabel);

		LayoutSpacer spacer = new LayoutSpacer();
		header.addMember(spacer);

		ToolStrip headerBar = new ToolStrip();
		headerBar.setMembersMargin(2);
		headerBar.setSize("445", "44");
		headerBar.addFill();
		headerBar.setStyleName("headerRight");

		headerBar.addMember(new ToolStripButton("Select"));
		headerBar.addMember(new ToolStripButton("Finish"));
		headerBar.addMember(new ToolStripButton("Tasks"));
		headerBar.addMember(new ToolStripButton("Request"));
		headerBar.addSpacer(10);
		header.addMember(headerBar);

		return header;
	}

	private Canvas createMenuBar() {
		ToolStrip menuBar = new ToolStrip();
		menuBar.setMembersMargin(5);
		menuBar.setSize("100%", "36");
		menuBar.addSpacer(6);
		menuBar.setBorder("none");

		// Create the panels:
		layerPanel = new LayerPanel(mainGui.getMapWidget());
		searchPanel = new SearchPanel(mainGui.getMapWidget());
		printPanel = new VLayout();
		documentPanel = new DocumentPanel();
		commentPanel2 = new CommentPanel();
		helpPanel = new VLayout();

		// Layer button:
		ToolStripButton layerButton = new ToolStripButton("LAYERS");
		layerButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mainGui.showLeftLayout(layerPanel, "Manage layers");
				mainGui.hideBottomLayout();
			}
		});
		layerButton.setActionType(SelectionType.RADIO);
		layerButton.setRadioGroup("the-only-one");
		menuBar.addMember(layerButton);
		menuBar.addMember(new ToolStripSeparator());

		// Activate Layer stuff
		layerButton.select();
		mainGui.showLeftLayout(layerPanel, "Manage layers");
		mainGui.hideBottomLayout();

		// Search button:
		ToolStripButton searchButton = new ToolStripButton("GIS SEARCH");
		searchButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mainGui.showLeftLayout(searchPanel, "Search the map");
				mainGui.hideBottomLayout();
			}
		});
		searchButton.setActionType(SelectionType.RADIO);
		searchButton.setRadioGroup("the-only-one");
		menuBar.addMember(searchButton);
		menuBar.addMember(new ToolStripSeparator());

		// Print button:
		ToolStripButton printButton = new ToolStripButton("PRINT");
		printButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mainGui.showLeftLayout(printPanel, "Print the map");
				mainGui.hideBottomLayout();
			}
		});
		printButton.setActionType(SelectionType.RADIO);
		printButton.setRadioGroup("the-only-one");
		menuBar.addMember(printButton);
		menuBar.addMember(new ToolStripSeparator());

		// Document button:
		ToolStripButton documentButton = new ToolStripButton("DOCUMENTS");
		documentButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mainGui.showLeftLayout(documentPanel, "Manage documents");
				mainGui.hideBottomLayout();
			}
		});
		documentButton.setActionType(SelectionType.RADIO);
		documentButton.setRadioGroup("the-only-one");
		menuBar.addMember(documentButton);
		menuBar.addMember(new ToolStripSeparator());

		// Comments button:
		ToolStripButton commentButton = new ToolStripButton("COMMENTS");
		commentButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mainGui.showLeftLayout(commentPanel2, "Manage comments");
				mainGui.hideBottomLayout();
			}
		});
		commentButton.setActionType(SelectionType.RADIO);
		commentButton.setRadioGroup("the-only-one");
		menuBar.addMember(commentButton);
		menuBar.addMember(new ToolStripSeparator());

		// Help button:
		ToolStripButton helpButton = new ToolStripButton("HELP");
		helpButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mainGui.showLeftLayout(helpPanel, "Help");
				mainGui.hideBottomLayout();
			}
		});
		helpButton.setActionType(SelectionType.RADIO);
		helpButton.setRadioGroup("the-only-one");
		menuBar.addMember(helpButton);

		// Referral button:
		menuBar.addMember(new ToolStripSeparator());
		ToolStripButton referralButton = new ToolStripButton("REFERRAL - TEST");
		referralButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				mainGui.showLeftLayout(new ReferralPanel(mainGui.getMapWidget()), "Referral form test");
				mainGui.hideBottomLayout();
			}
		});
		referralButton.setActionType(SelectionType.RADIO);
		referralButton.setRadioGroup("the-only-one");
		menuBar.addMember(referralButton);
		return menuBar;
	}
}