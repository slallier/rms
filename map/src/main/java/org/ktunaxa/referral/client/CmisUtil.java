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

package org.ktunaxa.referral.client;

/**
 * Utility for handling CMIS related things.
 *
 * @author Joachim Van der Auwera
 */
public final class CmisUtil {

	private static final String GUEST_ACCESS_PARAMETER = "guest=true";
	private static final String PARAMETER_SEPARATOR_FIRST = "?";
	private static final String PARAMETER_SEPARATOR_MORE = "&";

	private CmisUtil() {
		// hide constructor
	}

	/**
	 * Assure a document/URL uses guest security.
	 *
	 * @param url URL to "fix"
	 * @return fixed URL
	 */
	public static String addGuestAccess(String url) {
		if (url.contains(GUEST_ACCESS_PARAMETER)) {
			return url;
		} else if (url.contains(PARAMETER_SEPARATOR_FIRST)) {
			return url + PARAMETER_SEPARATOR_MORE + GUEST_ACCESS_PARAMETER;
		} else {
			return url + PARAMETER_SEPARATOR_FIRST + GUEST_ACCESS_PARAMETER;
		}
	}


}
