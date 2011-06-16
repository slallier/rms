/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */

package org.ktunaxa.referral.client.gui;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import org.geomajas.command.CommandResponse;
import org.geomajas.gwt.client.command.AbstractCommandCallback;
import org.geomajas.gwt.client.command.GwtCommand;
import org.geomajas.gwt.client.command.GwtCommandDispatcher;
import org.geomajas.gwt.client.widget.CardLayout;
import org.ktunaxa.referral.client.form.AbstractTaskForm;
import org.ktunaxa.referral.client.form.DiscussEvaluationForm;
import org.ktunaxa.referral.client.form.EmptyForm;
import org.ktunaxa.referral.client.form.EvaluateOrFinishForm;
import org.ktunaxa.referral.client.form.ProvincialResultForm;
import org.ktunaxa.referral.client.form.ReviewReferralForm;
import org.ktunaxa.referral.client.form.ValueSelectForm;
import org.ktunaxa.referral.server.command.dto.FinishTaskRequest;
import org.ktunaxa.referral.server.dto.TaskDto;

import java.util.Map;

/**
 * Block which displays the current task with the ability to set variables and finish the task.
 *
 * @author Joachim Van der Auwera
 */
public class CurrentTaskBlock extends CardLayout {

	private static final String KEY_NO = "no";
	private static final String KEY_CURRENT = "curr";

	// form names should match task names for the form
	private static final String FORM_EMPTY = "empty";
	private static final String FORM_DISCUSS_EVALUATION = "discussEvaluationResult.form";
	private static final String FORM_EVALUATE_OR_FINISH = "evaluateOrFinish.form";
	private static final String FORM_PROVINCIAL_RESULT = "provincialResult.form";
	private static final String FORM_REVIEW_REFERRAL = "reviewReferral.form";
	private static final String FORM_VALUE_SELECT = "valueSelect.form";
	private static final String[] TASKS_WITH_FORM = {
			FORM_DISCUSS_EVALUATION,
			FORM_EVALUATE_OR_FINISH,
			FORM_PROVINCIAL_RESULT,
			FORM_REVIEW_REFERRAL,
			FORM_VALUE_SELECT
	};

	private final CardLayout taskForms = new CardLayout();
	private final Button finishButton = new Button("Finish task");

	public CurrentTaskBlock() {
		super();
		setWidth100();
		setHeight100();

		VLayout noTask = new VLayout();
		HTMLFlow noTaskText = new HTMLFlow("No current task, please select one.");
		noTaskText.setWidth100();
		noTaskText.setHeight100();
		noTask.addMember(noTaskText);
		noTask.setWidth100();
		noTask.setHeight100();

		finishButton.setShowRollOver(false);
		finishButton.addClickHandler(new FinishTaskClickHandler());
		VLayout currentTask = new VLayout();
		currentTask.setMembersMargin(5);
		currentTask.setWidth100();
		currentTask.setHeight100();
		taskForms.addCard(FORM_EMPTY, new EmptyForm());
		taskForms.addCard(FORM_DISCUSS_EVALUATION, new DiscussEvaluationForm());
		taskForms.addCard(FORM_EVALUATE_OR_FINISH, new EvaluateOrFinishForm());
		taskForms.addCard(FORM_PROVINCIAL_RESULT, new ProvincialResultForm());
		taskForms.addCard(FORM_REVIEW_REFERRAL, new ReviewReferralForm());
		taskForms.addCard(FORM_VALUE_SELECT, new ValueSelectForm());
		taskForms.setWidth100();
		//taskForms.setHeight("*");
		currentTask.addMember(taskForms);
		HLayout finishLayout = new HLayout();
		finishLayout.setWidth100();
		//finishLayout.setHeight(30);
		finishLayout.addMember(new LayoutSpacer());
		finishLayout.addMember(finishButton);
		currentTask.addMember(finishLayout);

		addCard(KEY_NO, noTask);
		addCard(KEY_CURRENT, currentTask);
		showCard(KEY_NO);
	}

	public void refresh(TaskDto task) {
		if (null == task) {
			showCard(KEY_NO);
		} else {
			String formKey = FORM_EMPTY;
			for (int i = TASKS_WITH_FORM.length - 1 ; i >= 0 ; i--) {
				if (null != task.getFormKey() && task.getFormKey().endsWith(TASKS_WITH_FORM[i])) {
					formKey = TASKS_WITH_FORM[i];
				}
			}
			taskForms.showCard(formKey);
			((AbstractTaskForm) taskForms.getCurrentCard()).refresh(task);
			showCard(KEY_CURRENT);

			finishButton.enable();
		}
		Canvas canvas = getCurrentCard();
		if (canvas instanceof Layout) {
			((Layout) canvas).reflow();
		}
	}

	/**
	 * Click handler for the "Finish task" button.
	 *
	 * @author Joachim Van der Auwera
	 */
	private class FinishTaskClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			finishButton.disable();
			Canvas card = taskForms.getCurrentCard();
			if (card instanceof AbstractTaskForm) {
				AbstractTaskForm taskForm = (AbstractTaskForm) card;
				boolean valid = taskForm.validate();
				TaskDto currentTask = MapLayout.getInstance().getCurrentTask();
				if (valid && null != currentTask) {
					Map<String, String> variables = taskForm.getVariables();
					FinishTaskRequest request = new FinishTaskRequest();
					request.setTaskId(currentTask.getId());
					request.setVariables(variables);
					GwtCommand command = new GwtCommand(FinishTaskRequest.COMMAND);
					command.setCommandRequest(request);
					GwtCommandDispatcher.getInstance().execute(command, new AbstractCommandCallback<CommandResponse>() {
						public void execute(CommandResponse response) {
							MapLayout mapLayout = MapLayout.getInstance();
							mapLayout.setReferralAndTask(mapLayout.getCurrentReferral(), null); // clear task
							mapLayout.focusBpm();
						}
					});
				} else {
					finishButton.enable();
				}
			}
		}
	}
}