/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */

package org.ktunaxa.referral.server.command.dto;

import org.geomajas.command.CommandRequest;

/**
 * Request object for {@link org.ktunaxa.referral.server.command.bpm.GetTasksCommand} command.
 *
 * @author Joachim Van der Auwera
 */
public class GetTasksRequest implements CommandRequest {

	private static final long serialVersionUID = 100L;

	public static final String COMMAND = "command.bpm.GetTasks";

	private boolean includeUnassignedTasks;
	private String assignee;
	private String referralId;

	/**
	 * Get whether unassigned tasks should be included.
	 *
	 * @return should unassigned tasks be included?
	 */
	public boolean isIncludeUnassignedTasks() {
		return includeUnassignedTasks;
	}

	/**
	 * Set whether unassigned tasks should be included.
	 *
	 * @param  includeUnassignedTasks should unassigned tasks be included?
	 */
	public void setIncludeUnassignedTasks(boolean includeUnassignedTasks) {
		this.includeUnassignedTasks = includeUnassignedTasks;
	}

	/**
	 * Get the assignee for which to return the open tasks.
	 *
	 * @return assignee to return tasks for
	 */
	public String getAssignee() {
		return assignee;
	}

	/**
	 * Set the assignee for which to return the open tasks.
	 *
 	 * @param assignee assignee
	 */
	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	/**
	 * Get the process instance id for which to include tasks.
	 *
	 * @return process instance id
	 */
	public String getReferralId() {
		return referralId;
	}

	/**
	 * Set the process instance if which which to include tasks.
	 *
	 * @param referralId process instance id
	 */
	public void setReferralId(String referralId) {
		this.referralId = referralId;
	}
}
