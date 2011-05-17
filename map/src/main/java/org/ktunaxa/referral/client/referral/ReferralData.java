/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */
package org.ktunaxa.referral.client.referral;

import java.util.Date;
import java.util.HashMap;

import org.geomajas.gwt.client.map.feature.Feature;
import org.geomajas.gwt.client.map.layer.VectorLayer;
import org.geomajas.layer.feature.attribute.AssociationValue;
import org.geomajas.layer.feature.attribute.IntegerAttribute;
import org.geomajas.layer.feature.attribute.LongAttribute;
import org.geomajas.layer.feature.attribute.PrimitiveAttribute;

import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * Wizard data holding feature under construction.
 * 
 * @author Jan De Moerloose
 * 
 */
public class ReferralData {

	private Feature feature;

	private VectorLayer layer;

	public ReferralData(VectorLayer layer) {
		this.layer = layer;
		feature = new Feature(layer);
		// set defaults, TODO: make generic implementation calling FeatureModel.newInstance() on the server
		feature.setIntegerAttribute("primaryClassificationNumber", 3500);
		feature.setIntegerAttribute("secondaryClassificationNumber", 10);
		feature.setIntegerAttribute("calendarYear", 11);
		feature.setStringAttribute("externalProjectId", "-99");
		feature.setStringAttribute("externalFileId", "-99");
		feature.setIntegerAttribute("activeRetentionPeriod", 2);
		feature.setIntegerAttribute("semiActiveRetentionPeriod", 5);
		feature.setBooleanAttribute("confidential", false);
		Date nextMonth = new Date();
		CalendarUtil.addMonthsToDate(nextMonth, 1);
		feature.setDateAttribute("responseDeadline", nextMonth);
		feature.setManyToOneAttribute("type", new AssociationValue(new LongAttribute(1L),
				new HashMap<String, PrimitiveAttribute<?>>()));
		feature.setManyToOneAttribute("finalDisposition", new AssociationValue(new IntegerAttribute(1),
				new HashMap<String, PrimitiveAttribute<?>>()));
		feature.setManyToOneAttribute("applicationType", new AssociationValue(new LongAttribute(1L),
				new HashMap<String, PrimitiveAttribute<?>>()));
		feature.setManyToOneAttribute("status", new AssociationValue(new LongAttribute(1L),
				new HashMap<String, PrimitiveAttribute<?>>()));
		feature.setIntegerAttribute("provincialAssessmentLevel", 1);
		feature.setIntegerAttribute("finalAssessmentLevel", 1);
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public Feature getFeature() {
		return feature;
	}

	public VectorLayer getLayer() {
		return layer;
	}

}