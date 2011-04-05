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
package org.ktunaxa.referral.client.layer;

import java.util.ArrayList;
import java.util.List;

import org.geomajas.gwt.client.map.MapModel;
import org.geomajas.gwt.client.map.event.LayerChangedHandler;
import org.geomajas.gwt.client.map.event.LayerFilteredEvent;
import org.geomajas.gwt.client.map.event.LayerLabeledEvent;
import org.geomajas.gwt.client.map.event.LayerShownEvent;
import org.geomajas.gwt.client.map.event.MapViewChangedEvent;
import org.geomajas.gwt.client.map.event.MapViewChangedHandler;
import org.geomajas.gwt.client.map.layer.VectorLayer;
import org.ktunaxa.referral.server.dto.ReferenceLayerDto;
import org.ktunaxa.referral.server.dto.ReferenceLayerTypeDto;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * A reference layer is a wrapper of the Geomajas layer that takes care of handling the sublayers as if they were
 * individual Geomajas layers.
 * 
 * @author Jan De Moerloose
 * 
 */
public class ReferenceLayer {

	private VectorLayer layer;

	private MapModel mapModel;

	private List<ReferenceSubLayer> subLayers = new ArrayList<ReferenceSubLayer>();

	private List<ReferenceLayerTypeDto> layerTypes;

	private HandlerManager handlerManager;

	public ReferenceLayer(VectorLayer layer, List<ReferenceLayerDto> subLayerDtos,
			List<ReferenceLayerTypeDto> layerTypes) {
		this.layer = layer;
		this.layerTypes = layerTypes;
		handlerManager = new HandlerManager(this);
		// forward layer changed events
		this.layer.addLayerChangedHandler(new LayerChangedForwarder());
		mapModel = layer.getMapModel();
		mapModel.getMapView().addMapViewChangedHandler(new LayerShowingHandler());
		for (ReferenceLayerDto referenceLayerDto : subLayerDtos) {
			subLayers.add(new ReferenceSubLayer(this, referenceLayerDto));
		}
		updateShowing();
	}

	/**
	 * Add a handler that registers changes in layer status.
	 * 
	 * @param handler
	 *            The new handler to be added.
	 */
	public HandlerRegistration addLayerChangedHandler(LayerChangedHandler handler) {
		return handlerManager.addHandler(LayerChangedHandler.TYPE, handler);
	}

	public double getCurrentScale() {
		return mapModel.getMapView().getCurrentScale();
	}

	public double getPixelLength() {
		return mapModel.getMapInfo().getPixelLength();
	}

	public void updateShowing() {
		List<Long> layerIds = new ArrayList<Long>();
		for (ReferenceSubLayer subLayer : subLayers) {
			subLayer.updateShowing();
			if (subLayer.isShowing()) {
				layerIds.add(subLayer.getCode());
			}
		}
		StringBuilder builder = null;
		if (layerIds.size() > 0) {
			for (Long id : layerIds) {
				if (builder == null) {
					builder = new StringBuilder();
				} else {
					builder.append(" or ");
				}
				builder.append("layer.code = " + id);
			}
		}
		String filter = (builder == null ? null : builder.toString());
		layer.setFilter(filter);
		if (!layer.isVisible()) {
			layer.setVisible(true);
		}
		// indicates show status has changed for one or more sublayers
		handlerManager.fireEvent(new LayerShownEvent(layer));
	}

	/**
	 * Updates the show status when the map view changes.
	 * 
	 * @author Jan De Moerloose
	 * 
	 */
	public class LayerShowingHandler implements MapViewChangedHandler {

		public void onMapViewChanged(MapViewChangedEvent event) {
			if(!event.isPanDragging()){
				updateShowing();
			}
		}
	}

	public List<ReferenceSubLayer> getSubLayers() {
		return subLayers;
	}

	public List<ReferenceLayerTypeDto> getLayerTypes() {
		return layerTypes;
	}

	/**
	 * Forwards layer changed events to our listeners.
	 * 
	 * @author Jan De Moerloose
	 * 
	 */
	public class LayerChangedForwarder implements LayerChangedHandler {

		public void onVisibleChange(LayerShownEvent event) {
			handlerManager.fireEvent(event);
		}

		public void onLabelChange(LayerLabeledEvent event) {
			handlerManager.fireEvent(event);
		}

		public void onFilterChange(LayerFilteredEvent event) {
			handlerManager.fireEvent(event);
		}

	}

}