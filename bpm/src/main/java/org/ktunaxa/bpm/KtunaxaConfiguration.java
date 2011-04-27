/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */

package org.ktunaxa.bpm;

import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.task.Task;
import org.activiti.spring.ProcessEngineFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Configuration settings for the Ktunaxa business flows.
 *
 * @author Joachim Van der Auwera
 */
public class KtunaxaConfiguration {

	private final Logger log = LoggerFactory.getLogger(KtunaxaConfiguration.class);

	@Autowired
	private ProcessEngineFactoryBean processEngineFactoryBean;

	private String mapDashboardBaseUrl = "http://localhost:8080/map/";
	private String bpmDashboardBaseUrl = "http://localhost:8080/activiti-explorer/";

	/**
	 * Get map dashboard base URL.
	 *
	 * @return base URL
	 */
	public String getMapDashboardBaseUrl() {
		return mapDashboardBaseUrl;
	}

	/**
	 * Set map dashboard base URL.
	 *
	 * @param mapDashboardBaseUrl base URL
	 */
	public void setMapDashboardBaseUrl(String mapDashboardBaseUrl) {
		this.mapDashboardBaseUrl = mapDashboardBaseUrl;
	}

	/**
	 * Get BPM dashboard base URL.
	 *
	 * @return base URL
	 */
	public String getBpmDashboardBaseUrl() {
		return bpmDashboardBaseUrl;
	}

	/**
	 * Set BPM dashboard base URL.
	 *
	 * @param bpmDashboardBaseUrl base URL
	 */
	public void setBpmDashboardBaseUrl(String bpmDashboardBaseUrl) {
		this.bpmDashboardBaseUrl = bpmDashboardBaseUrl;
	}

	/**
	 * Get the URL to link to a referral with given id in the mapping dashboard.
	 *
	 * @param execution current execution
	 * @return URL for referral in mapping dashboard
	 */
	public String getReferralUrl(DelegateExecution execution) {
		String referralId = (String) execution.getVariable(KtunaxaBpmConstant.VAR_REFERRAL_ID);
		return mapDashboardBaseUrl + "?" + KtunaxaBpmConstant.QUERY_REFERRAL_ID + "=" + referralId + "&" +
				KtunaxaBpmConstant.QUERY_TASK_ID + "=" + getTaskId(execution);
	}

	/**
	 * Get a HTML except with a link to the referral in the mapping dashboard, using the id as string.
	 *
	 * @param execution current execution
	 * @return HTML link
	 */
	public String getReferralIdLink(DelegateExecution execution) {
		String referralId = (String) execution.getVariable(KtunaxaBpmConstant.VAR_REFERRAL_ID);
		return "<A HREF=\"" + getReferralUrl(execution) + "\">" + referralId + "</A>";
	}

	/**
	 * Get a HTML except with a link to the referral in the mapping dashboard, using the id and description as string.
	 *
	 * @param execution current execution
	 * @return HTML link
	 */
	public String getReferralLink(DelegateExecution execution) {
		String referralId = (String) execution.getVariable(KtunaxaBpmConstant.VAR_REFERRAL_ID);
		String description = (String) execution.getVariable(KtunaxaBpmConstant.VAR_DESCRIPTION);
		return "<A HREF=\"" + getReferralUrl(execution) + "\">" + referralId + ", " + description + "</A>";
	}

	private String getTaskId(DelegateExecution execution) {
		try {
			TaskService taskService = processEngineFactoryBean.getObject().getTaskService();
			Task task = taskService.createTaskQuery().executionId(execution.getId()).singleResult();
			return task.getId();
		} catch (Exception exception) {
			log.error("Could not create task service, so no task id", exception);
			return "???";
		}
	}
}
