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

package org.ktunaxa.referral.server.service;

import java.util.List;

import org.ktunaxa.referral.server.domain.ReferenceLayer;
import org.ktunaxa.referral.server.domain.ReferenceLayerType;

/**
 * Service that contains methods revolving around the reference layers.
 * 
 * @author Pieter De Graef
 */
public interface ReferenceLayerService {

	/**
	 * Get all reference layer types.
	 * 
	 * @return Returns the full list of reference layer types.
	 */
	List<ReferenceLayerType> findLayerTypes();

	/**
	 * Get all reference layer objects.
	 * 
	 * @return Returns the full list of reference layers.
	 */
	List<ReferenceLayer> findLayers();
}