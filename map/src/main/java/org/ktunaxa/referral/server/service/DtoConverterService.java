/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Geosparc nv, http://www.geosparc.com/, Belgium.
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.ktunaxa.referral.server.service;

import org.ktunaxa.referral.server.domain.ReferenceLayer;
import org.ktunaxa.referral.server.domain.ReferenceLayerType;
import org.ktunaxa.referral.server.dto.ReferenceLayerDto;
import org.ktunaxa.referral.server.dto.ReferenceLayerTypeDto;

/**
 * General interface for converting domain objects to their DTO counterparts and vice versa.
 * 
 * @author Pieter De Graef
 */
public interface DtoConverterService {

	/**
	 * Convert a layer type to a DTO.
	 * 
	 * @param layerType
	 *            The layer type to convert.
	 * @return Returns the DTO version of the layer type.
	 */
	ReferenceLayerTypeDto toDto(ReferenceLayerType layerType);

	/**
	 * Convert a reference layer to a DTO.
	 * 
	 * @param layer
	 *            The reference layer to convert.
	 * @return Returns the DTO version of the reference layer.
	 */
	ReferenceLayerDto toDto(ReferenceLayer layer);
}