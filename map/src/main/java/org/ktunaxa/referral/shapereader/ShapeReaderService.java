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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.geotools.data.DataStore;
import org.ktunaxa.referral.server.domain.ReferenceLayer;

/**
 * <p>
 * Central service for uploading shape files into the Ktunaxa database. It contains methods for finding, reading,
 * validating and persisting shape files.
 * </p>
 * <p>
 * Implementations of this interface are expected to be transactional, so that all database changes occur within a
 * single transaction.
 * </p>
 * 
 * @author Pieter De Graef
 */
public interface ShapeReaderService {
	
	/**
	 * Retrieve a list of available shape files to chose from. The idea is that in some location a list of shape files
	 * can be retrieved, of which we want to upload one. Where these shape files come from, is up to the implementations
	 * of this service.
	 * 
	 * @param subDirectory extra path element to indicate sub-package/directory
	 * @return Returns the full list of available shape files. The idea is that some GUI is presented to the user, so he
	 *         can choose one.
	 * @throws IOException
	 *             Thrown if something goes wrong while retrieving available shape files.
	 */
	File[] getAllFiles(String subDirectory) throws IOException;

	/**
	 * Actually read a shape file (.shp) and return it in the form of a GeoTools DataStore.
	 * 
	 * @param file
	 *            The chosen shape file to read. Must be a .shp file - no compressed file!
	 * @return Returns the GeoTools DataStore representation of that shape file. This DataStore can read the contents.
	 * @throws IOException problem reading or converting shape file
	 */
	DataStore read(File file) throws IOException;

	/**
	 * Validate the given GeoTools DataStore to see if it's contents can be used as a reference layer (base or value).
	 * For validation to succeed, 2 specific attributes need to be available:
	 * <ul>
	 * <li>RMS_LABEL</li>
	 * <li>RMS_STYLE</li>
	 * </ul>
	 * 
	 * @param dataStore
	 *            The DataStore to validate.
	 * @throws IOException
	 *             Thrown when the DataStore does not meet the requirements.
	 */
	void validateFormat(DataStore dataStore) throws IOException;

	/**
	 * Get the full list of known reference layers (both base and value).
	 * 
	 * @return The full list of known reference layers (both base and value).
	 */
	List<ReferenceLayer> getAllLayers();

	/**
	 * Persist the given DataStore as the new contents for the given layer into the database, replacing the old
	 * contents. Implementing classes must make sure that the entire operation is done within a single transaction.
	 * 
	 * @param layer
	 *            The reference layer to replace the contents in.
	 * @param dataStore
	 *            The data source containing the new contents.
	 * @throws IOException
	 *             Thrown when any error occurs within the process. This should automatically roll back the database to
	 *             the original state.
	 */
	void persist(ReferenceLayer layer, DataStore dataStore) throws IOException;

}