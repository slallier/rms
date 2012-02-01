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

package org.ktunaxa.referral.server.security;

import org.geomajas.plugin.staticsecurity.configuration.LayerAuthorization;

import java.util.Set;

/**
 * Custom authorization implementation.
 *
 * @author Joachim Van der Auwera
 */
public class AppAuthorizationImpl extends LayerAuthorization implements AppAuthorization {

	private AppAuthorizationInfo appAuthorizationInfo;

	protected AppAuthorizationImpl() {
		// for deserialization
	}

	public AppAuthorizationImpl(AppAuthorizationInfo info) {
		super(info);
		appAuthorizationInfo = info;
	}

	public Set<String> getBpmRoles() {
		return appAuthorizationInfo.getBpmRoles();
	}

	public boolean isAdmin() {
		return appAuthorizationInfo.isAdmin();
	}
}
