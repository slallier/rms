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

package org.ktunaxa.referral.shapereader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.ktunaxa.referral.server.domain.ReferenceBase;
import org.ktunaxa.referral.server.domain.ReferenceBaseAttribute;
import org.ktunaxa.referral.server.domain.ReferenceLayer;
import org.ktunaxa.referral.server.domain.ReferenceValue;
import org.ktunaxa.referral.server.domain.ReferenceValueAttribute;
import org.ktunaxa.referral.server.service.KtunaxaConstant;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.operation.valid.TopologyValidationError;

/**
 * Implementation of the LayerPersistService, which does not specify transactional behavior. This service expects such
 * configurations to be done a level higher, spanning all methods.
 * 
 * @author Pieter De Graef
 */
@Component("layerPersistService")
public class LayerPersistServiceImpl implements LayerPersistService {

	private final Logger log = LoggerFactory.getLogger(LayerPersistServiceImpl.class);

	private static final String STYLE_ATTRIBUTE = "RMS_STYLE";

	private static final String LABEL_ATTRIBUTE = "RMS_LABEL";

	private int srid = KtunaxaConstant.LAYER_SRID;

	private Map<String, String> styleAttributeMap = new HashMap<String, String>();

	private Map<String, String> labelAttributeMap = new HashMap<String, String>();

	@Autowired
	@Qualifier("postgisSessionFactory")
	private SessionFactory sessionFactory;

	/**
	 * Return the code for the SRID that all features/geometries should match.
	 * 
	 * @return The actual SRID code.
	 */
	public int getSrid() {
		return srid;
	}

	/**
	 * Clear all contents in the given reference layer from the database.
	 * 
	 * @param layer
	 *            The reference layer instance to clear.
	 */
	public void clearLayer(ReferenceLayer layer) {
		int count;
		if (layer.getType().isBaseLayer()) {
			// batch delete
			Session session = sessionFactory.getCurrentSession();
			session
					.createSQLQuery(
							"delete from reference_base_attribute where id in " +
							"(select attr.id from reference_base_attribute as attr, " +
							"reference_base as base where attr.reference_base_id=base.id and base.layer_id=:layerId)")
					.setLong("layerId", layer.getId()).executeUpdate();
			count = session
			.createSQLQuery(
					"delete from reference_base as base where base.layer_id=:layerId")
			.setLong("layerId", layer.getId()).executeUpdate();

			log.info(count + " base reference objects deleted");
		} else {
			// batch delete
			Session session = sessionFactory.getCurrentSession();
			session.createSQLQuery(
					"delete from reference_value_attribute where id in "
							+ "(select attr.id from reference_value_attribute as attr, "
							+ "reference_value as value where attr.reference_value_id=value.id" +
									" and value.layer_id=:layerId)")
					.setLong("layerId", layer.getId()).executeUpdate();
			session.createSQLQuery("delete from reference_value as value where value.layer_id=:layerId")
					.setLong("layerId", layer.getId()).executeUpdate();

		}
	}

	/**
	 * Validate a single 'feature'. These features are part of a GeoTools DataStore (usually read from shape files). Use
	 * this validation method before actually persisting a feature.<br/>
	 * This method check for empty and invalid geometries only.
	 * 
	 * @param feature
	 *            The feature to validate.
	 * @throws IOException
	 *             If validation fails, an error is thrown. In this case no changes should be committed.
	 */
	public void validate(SimpleFeature feature) throws IOException {
		Geometry geometry = (Geometry) feature.getDefaultGeometry();
		if (geometry.isEmpty()) {
			log.warn("An empty geometry was found in the shape file. Feature ID=" + feature.getID());
		}
		if (!geometry.isValid()) {
			IsValidOp validOp = new IsValidOp(geometry);
			TopologyValidationError err = validOp.getValidationError();
			log.warn("An invalid geometry was found in the shape file: " + err.getMessage()
					+ ". Feature ID=" + feature.getID());
		}
		if (geometry.getSRID() == 0) {
			geometry.setSRID(getSrid()); // special case...
		} else if (geometry.getSRID() != getSrid()) {
			throw new IOException("Geometry has wrong coordinate system. SRID=" + geometry.getSRID());
		}
	}

	/**
	 * Convert a GeoTools feature to a ReferenceBase domain object.
	 * 
	 * @param layer
	 *            The base layer wherein the feature should lie.
	 * @param feature
	 *            The actual feature to convert.
	 * @return The converted feature as a ReferenceBase object.
	 */
	public ReferenceBase convertToBase(ReferenceLayer layer, SimpleFeature feature) {
		ReferenceBase base = new ReferenceBase();
		base.setLayer(layer);
		base.setGeometry((Geometry) feature.getDefaultGeometry());
		SimpleFeatureType type = feature.getFeatureType();
		Expression labelExpr = getLabelAttributeExpression(type.getTypeName());
		Expression styleExpr = getStyleAttributeExpression(type.getTypeName());
		Object temp = labelExpr.evaluate(feature);
		if (temp != null) {
			base.setLabel(temp.toString());
		}
		temp = styleExpr.evaluate(feature);
		if (temp != null) {
			base.setStyleCode(temp.toString());
		}

		List<ReferenceBaseAttribute> attributes = new ArrayList<ReferenceBaseAttribute>();
		for (AttributeDescriptor descr : feature.getFeatureType().getAttributeDescriptors()) {
			String key = descr.getLocalName();
			Object value = feature.getAttribute(key);
			if (value != null && !(value instanceof Geometry)) {
				ReferenceBaseAttribute attribute = new ReferenceBaseAttribute();
				attribute.setReference(base);
				attribute.setKey(key);
				attribute.setValue(value.toString());
				attributes.add(attribute);
			}
		}
		base.setAttributes(attributes);
		return base;
	}

	private boolean isPropertyName(Expression expression, String propertyName) {
		if (expression instanceof PropertyName) {
			PropertyName prop = (PropertyName) expression;
			if (prop.getPropertyName().equals(propertyName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Convert a GeoTools feature to a ReferenceValue domain object.
	 * 
	 * @param layer
	 *            The value layer wherein the feature should lie.
	 * @param feature
	 *            The actual feature to convert.
	 * @return The converted feature as a ReferenceValue object.
	 */
	public ReferenceValue convertToValue(ReferenceLayer layer, SimpleFeature feature) {
		ReferenceValue value = new ReferenceValue();
		value.setLayer(layer);
		value.setGeometry((Geometry) feature.getDefaultGeometry());
		SimpleFeatureType type = feature.getFeatureType();
		Expression labelExpr = getLabelAttributeExpression(type.getTypeName());
		Expression styleExpr = getStyleAttributeExpression(type.getTypeName());
		Object temp = labelExpr.evaluate(feature);
		if (temp != null) {
			value.setLabel(temp.toString());
		}
		temp = styleExpr.evaluate(feature);
		if (temp != null) {
			value.setStyleCode(temp.toString());
		}

		List<ReferenceValueAttribute> attributes = new ArrayList<ReferenceValueAttribute>();
		for (AttributeDescriptor descr : feature.getFeatureType().getAttributeDescriptors()) {
			String key = descr.getLocalName();
			Object attributeValue = feature.getAttribute(key);
			if (attributeValue != null && !(attributeValue instanceof Geometry)) {
				ReferenceValueAttribute attribute = new ReferenceValueAttribute();
				attribute.setReference(value);
				attribute.setKey(key);
				attribute.setValue(attributeValue.toString());
				attributes.add(attribute);
			}
		}
		value.setAttributes(attributes);
		return value;
	}

	/**
	 * Persist a reference base object in the database.
	 * 
	 * @param reference
	 *            The object to save.
	 */
	public void persist(ReferenceBase reference) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(reference);
	}

	/**
	 * Persist a reference value object in the database.
	 * 
	 * @param reference
	 *            The object to save.
	 */
	public void persist(ReferenceValue reference) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(reference);
	}

	// ------------------------------------------------------------------------
	// Extra setters:
	// ------------------------------------------------------------------------

	/**
	 * Set a new value for the required SRID that all geometries must match.
	 * 
	 * @param srid
	 *            The new SRID value.
	 */
	public void setSrid(int srid) {
		this.srid = srid;
	}

	public void flushSession() {
		sessionFactory.getCurrentSession().flush();
		sessionFactory.getCurrentSession().clear();
	}

	public void setStyleAttributeMap(Map<String, String> styleAttributeMap) {
		this.styleAttributeMap = styleAttributeMap;
	}

	public void setLabelAttributeMap(Map<String, String> labelAttributeMap) {
		this.labelAttributeMap = labelAttributeMap;
	}

	public Expression getLabelAttributeExpression(String typeName) {
		try {
			if (labelAttributeMap.containsKey(typeName)) {
				return CQL.toExpression(labelAttributeMap.get(typeName));
			} else {
				return CQL.toExpression(LABEL_ATTRIBUTE);
			}
		} catch (CQLException e) {
			return Expression.NIL;
		}
	}

	public Expression getStyleAttributeExpression(String typeName) {
		try {
			if (styleAttributeMap.containsKey(typeName)) {
				return CQL.toExpression(styleAttributeMap.get(typeName));
			} else {
				return CQL.toExpression(STYLE_ATTRIBUTE);
			}
		} catch (CQLException e) {
			return Expression.NIL;
		}
	}

}