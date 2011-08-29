/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */
package org.ktunaxa.referral.client.referral;

import org.geomajas.gwt.client.map.feature.Feature;
import org.geomajas.gwt.client.spatial.geometry.Geometry;
import org.ktunaxa.referral.client.gui.LayoutConstant;
import org.ktunaxa.referral.client.referral.event.FileUploadCompleteEvent;
import org.ktunaxa.referral.client.referral.event.FileUploadDoneHandler;
import org.ktunaxa.referral.client.referral.event.FileUploadFailedEvent;
import org.ktunaxa.referral.client.referral.event.GeometryUploadHandler;
import org.ktunaxa.referral.client.referral.event.GeometryUploadSuccessEvent;
import org.ktunaxa.referral.client.wkt.WktParser;
import org.ktunaxa.referral.server.service.KtunaxaConstant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Panel to upload the referral geometry via a local shape file.
 * 
 * @author Jan De Moerloose
 */
public class UploadShapePanel extends VLayout implements UploadGeometryPanel {

	public static final String NAME = "UploadShapePanel";

	private Img busyImg;

	private HTMLFlow errorFlow;

	private Feature feature;

	private HandlerManager handlerManager = new HandlerManager(this);

	public UploadShapePanel() {
		// base layout, completed when the feature is set
		setLayoutAlign(Alignment.CENTER);
		HTMLFlow explanation = new HTMLFlow("<h3>Attach a zipped shape file</h3><div><p>In order to attach a geometry"
				+ " to this referral, you have to provide a shape file (zipped) that contains the project area. Please"
				+ " make sure that the zipped file contains at least the .shp, .prj, .dbf and .shx files.</p>"
				+ "<p>Please note that it may take a while to correctly interpret the provided shape file.</p>"
				+ "</div>");
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setHeight(20);

		addMember(explanation);
		addMember(spacer);
	}

	public void setFeature(Feature referral) {
		this.feature = referral;

		// finish layout, add shape upload form

		final FileUploadForm form = new FileUploadForm("Select a file (.zip)", GWT.getModuleBaseURL()
				+ "../d/upload/referral/geometry/", ReferralUtil.createId(feature));
		form.setHeight(40);

		HLayout btnLayout = new HLayout(LayoutConstant.MARGIN_LARGE);
		busyImg = new Img(LayoutConstant.LOADING_IMAGE,
				LayoutConstant.LOADING_IMAGE_WIDTH, LayoutConstant.LOADING_IMAGE_HEIGHT);
		busyImg.setVisible(false);
		IButton uploadButton = new IButton("Upload ShapeFile");
		uploadButton.setAutoFit(true);
		uploadButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				form.submit();
				busyImg.setVisible(true);
			}
		});
		btnLayout.addMember(uploadButton);
		btnLayout.addMember(busyImg);
		errorFlow = new HTMLFlow();
		errorFlow.setHeight100();
		errorFlow.setWidth100();
		errorFlow.setVisible(false);
		btnLayout.addMember(errorFlow);

		form.addFileUploadDoneHandler(new FileUploadDoneHandler() {

			public void onFileUploadComplete(FileUploadCompleteEvent event) {
				busyImg.setVisible(false);
				WktParser wktParser = new WktParser(KtunaxaConstant.LAYER_SRID);
				Geometry geometry = wktParser.parse(event.getString(KtunaxaConstant.FORM_GEOMETRY));
				if (geometry != null) {
					errorFlow.setContents("");
					errorFlow.setVisible(false);
					feature.setGeometry(geometry);
					handlerManager.fireEvent(new GeometryUploadSuccessEvent());
				} else {
					errorFlow.setContents("<div style='color: #AA0000'>Geometry not in this UTM zone, "
							+ "did you pick the wrong shape file ?</div>");
					errorFlow.setVisible(true);
				}
			}

			public void onFileUploadFailed(FileUploadFailedEvent event) {
				busyImg.setVisible(false);
				errorFlow.setContents("<div style='color: #AA0000'>" + event.getErrorMessage() + "</div>");
				errorFlow.setVisible(true);
			}
		});

		addMember(form);
		addMember(btnLayout);
	}

	public HandlerRegistration addGeometryUploadHandler(GeometryUploadHandler handler) {
		return handlerManager.addHandler(GeometryUploadHandler.TYPE, handler);
	}

}
