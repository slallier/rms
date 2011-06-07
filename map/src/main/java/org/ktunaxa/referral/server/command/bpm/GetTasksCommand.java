/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */

package org.ktunaxa.referral.server.command.bpm;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.geomajas.command.Command;
import org.ktunaxa.referral.server.command.dto.GetTasksRequest;
import org.ktunaxa.referral.server.command.dto.GetTasksResponse;
import org.ktunaxa.referral.server.dto.TaskDto;
import org.ktunaxa.referral.server.service.DtoConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Get a list of tasks, which tasks depends on the request.
 * You can request unassigned tasks, task for an assignee or tasks for a process instance.
 * 
 * @author Joachm Van der Auwera
 */
@Component
public class GetTasksCommand implements Command<GetTasksRequest, GetTasksResponse> {

	@Autowired
	private TaskService taskService;

	@Autowired
	private DtoConverterService converterService;

	public GetTasksResponse getEmptyCommandResponse() {
		return new GetTasksResponse();
	}

	public void execute(GetTasksRequest request, GetTasksResponse response) throws Exception {

		List<TaskDto> taskDtos = new ArrayList<TaskDto>();
		response.setTasks(taskDtos);

		if (request.isIncludeUnassignedTasks()) {
			TaskQuery taskQuery = taskService.createTaskQuery();
			taskQuery.taskUnnassigned();
			add(taskDtos, taskService.createTaskQuery().taskUnnassigned().list());
		}
		if (null != request.getAssignee()) {
			TaskQuery taskQuery = taskService.createTaskQuery();
			taskQuery.taskUnnassigned();
			add(taskDtos, taskService.createTaskQuery().taskAssignee(request.getAssignee()).list());
		}
		if (null != request.getProcessInstanceId()) {
			TaskQuery taskQuery = taskService.createTaskQuery();
			taskQuery.taskUnnassigned();
			add(taskDtos, taskService.createTaskQuery().processInstanceId(request.getProcessInstanceId()).list());
		}
	}

	private void add(List<TaskDto> taskDtos, List<Task> tasks) {
		for (Task task : tasks) {
			taskDtos.add(converterService.toDto(task));
		}
	}

}