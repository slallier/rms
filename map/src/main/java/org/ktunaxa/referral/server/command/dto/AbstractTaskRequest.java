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

package org.ktunaxa.referral.server.command.dto;

import org.geomajas.command.CommandRequest;

/**
 * Base request which contains a task id.
 *
 * @author Joachim Van der Auwera
 */
public abstract class AbstractTaskRequest implements CommandRequest {

	private static final long serialVersionUID = 100L;

	private String taskId;

	/**
	 * Get id of task which needs to be finished.
	 *
	 * @return task id
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * Set id of task which needs to be finished.
	 *
	 * @param taskId task id
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}
