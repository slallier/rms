/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */

/**
 * Form used to display a pre formed default email using {@link org.ktunaxa.referral.server.domain.Template}.<br />
 * One EditEmailForm is created and is reloaded, when user switches between templates.
 * 
 * @author Emiel Ackermann
 */

package org.ktunaxa.referral.client.gui;

import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import org.geomajas.gwt.client.command.AbstractCommandCallback;
import org.geomajas.gwt.client.command.GwtCommand;
import org.geomajas.gwt.client.command.GwtCommandDispatcher;
import org.geomajas.gwt.client.util.Html;
import org.geomajas.gwt.client.util.HtmlBuilder;
import org.ktunaxa.bpm.KtunaxaBpmConstant;
import org.ktunaxa.referral.client.form.AbstractTaskForm;
import org.ktunaxa.referral.server.command.dto.GetEmailDataRequest;
import org.ktunaxa.referral.server.command.dto.GetEmailDataResponse;
import org.ktunaxa.referral.server.command.dto.UpdateEmailDataRequest;
import org.ktunaxa.referral.server.command.dto.UpdateEmailDataResponse;
import org.ktunaxa.referral.server.command.dto.ValidateTemplateRequest;
import org.ktunaxa.referral.server.command.dto.ValidateTemplateResponse;
import org.ktunaxa.referral.server.dto.TaskDto;
import org.ktunaxa.referral.server.service.KtunaxaConstant;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;

import java.util.Map;

/**
 * Form used for editing a {@link org.ktunaxa.referral.server.domain.Template} found in a linked database.
 *
 * @author Emiel Ackermann
 */
public class EditEmailTemplateForm extends AbstractTaskForm {

	private final TextItem from = new TextItem();
	private final TextItem subject = new TextItem();
	private final TextAreaItem message = new TextAreaItem();
	private TaskDto task;
	private final Button cancelButton = new Button("Cancel");
	private final Button resetButton = new Button("Reset");
	private final Button editButton = new Button("Edit");
	private final HTMLFlow label = new HTMLFlow();
	private Runnable finishHandler;
	private String templateTitle;
	
	public EditEmailTemplateForm() {
		setStyleName("taskBlockContent");

		RegExpValidator oneAddress = new RegExpValidator();
		oneAddress.setExpression(KtunaxaConstant.MAIL_VALIDATOR_REGEX);
		oneAddress.setErrorMessage("Invalid email address.");

		from.setName(KtunaxaConstant.Email.FROM_NAME);
		from.setTitle("From");
		from.setValidators(oneAddress);
		from.setWidth("100%");
		from.setRequired(true);
		from.setRequiredMessage("Sender email address required.");

		subject.setName(KtunaxaConstant.Email.SUBJECT_NAME);
		subject.setTitle("Subject");
		subject.setWidth("100%");

		message.setShowTitle(false);
		message.setName(KtunaxaConstant.Email.MESSAGE_NAME);
		message.setColSpan(2);
		message.setWidth("100%");
		message.setMinHeight(100);
		message.setHeight("*");

		FormChangedHandler handler = new FormChangedHandler();
		from.addChangedHandler(handler);
		subject.addChangedHandler(handler);
		message.addChangedHandler(handler);
		
		cancelButton.setShowRollOver(false);
		cancelButton.setLayoutAlign(VerticalAlignment.CENTER);
		resetButton.setShowRollOver(false);
		resetButton.setLayoutAlign(VerticalAlignment.CENTER);
		resetButton.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				fillTemplate();
				show();
				disableButtons();
			}
		});
		resetButton.setDisabled(true);
		editButton.setShowRollOver(false);
		editButton.setLayoutAlign(VerticalAlignment.CENTER);
		editButton.setDisabled(true);
		
		setEditForm();
	}
	
	private void setEditForm() {
		label.setWidth100();  
		addMember(label);
		DynamicForm mail = new DynamicForm();
		mail.setWidth100();
		mail.setColWidths("13%", "87%");
		mail.setFields(from, subject);
		
		DynamicForm messageForm = new DynamicForm();
		messageForm.setWidth("87%");
		messageForm.setLayoutAlign(Alignment.RIGHT);
		messageForm.setHeight("*");
		messageForm.setFields(message);
		
		setForms(mail, messageForm);
		
		HLayout buttons = new HLayout(LayoutConstant.MARGIN_SMALL);
		buttons.setHeight(50);
		buttons.addMember(new LayoutSpacer());
		buttons.addMember(cancelButton);
		buttons.addMember(resetButton);
		buttons.addMember(editButton);
		
		addMember(buttons);
	}


	public void refresh(String templateTitle, TaskDto task) {
		this.task = task;
		fillTemplate();
		this.templateTitle = templateTitle;
		updateExplanation();
	}

	/**
	 * Get email data from db and put/convert it into values for the items of the form.
	 */
	public void fillTemplate() {
		GetEmailDataRequest request = new GetEmailDataRequest(templateTitle);
		request.setTask(task);
		GwtCommand command = new GwtCommand(GetEmailDataRequest.COMMAND);
		command.setCommandRequest(request);
		GwtCommandDispatcher.getInstance().execute(command, new AbstractCommandCallback<GetEmailDataResponse>() {
			public void execute(GetEmailDataResponse response) {
				from.setValue(response.getFrom());
				subject.setValue(response.getSubject());
				message.setValue(response.getBody());
			}
		});
	}

	/**
	 * Provides explanation with allowed placeholders, based on notifier String.
	 */
	private void updateExplanation() {
		StringBuilder explanation = new StringBuilder(HtmlBuilder.divClass("commentBlockInfo", 
				"Text between '${' and '}' is replaced with referral data, before the template is shown to a user. " +
		"Allowed placeholders for this template are:"));
//		id, name for all notifiers.
		explanation.append(HtmlBuilder.openTagStyleHtmlContent(Html.Tag.UL, "font-weight:bold",  
				HtmlBuilder.tag(Html.Tag.LI, "${" + KtunaxaBpmConstant.VAR_REFERRAL_ID + "}"),
				HtmlBuilder.tag(Html.Tag.LI, "${" + KtunaxaBpmConstant.VAR_REFERRAL_NAME + "}")));
		if (KtunaxaConstant.Email.START.equals(templateTitle)) {
//			+ deadline
			explanation.append(HtmlBuilder.tag(Html.Tag.LI, "${" + KtunaxaBpmConstant.VAR_COMPLETION_DEADLINE + "}"));
		} else if (KtunaxaConstant.Email.CHANGE.equals(templateTitle)) {
//			+ prov engag lvl, engag lvl, engag comment, deadline
			explanation.append(HtmlBuilder.tag(Html.Tag.LI, 
					"${" + KtunaxaBpmConstant.VAR_PROVINCE_ENGAGEMENT_LEVEL + "}"));
			explanation.append(HtmlBuilder.tag(Html.Tag.LI, 
					"${" + KtunaxaBpmConstant.VAR_ENGAGEMENT_LEVEL + "}"));
			explanation.append(HtmlBuilder.tag(Html.Tag.LI, 
					"${" + KtunaxaBpmConstant.VAR_ENGAGEMENT_COMMENT + "}"));
			explanation.append(HtmlBuilder.tag(Html.Tag.LI, 
					"${" + KtunaxaBpmConstant.VAR_COMPLETION_DEADLINE + "}"));
		}
		explanation.append(HtmlBuilder.closeTag(Html.Tag.UL));
		explanation.append(HtmlBuilder.divClassHtmlContent("commentBlockInfo",
				HtmlBuilder.tagClass(Html.Tag.SPAN, "commentBlockTitleText", "Tip: ") +
		"Copy/paste a placeholder to use it in the template."));
		explanation.append(HtmlBuilder.divClassHtmlContent("commentBlockInfo", 
				HtmlBuilder.tagClass(Html.Tag.SPAN, "commentBlockTitleText", "Note: ") + 
		"Using non-allowed text between the '${' and '}', will break the template."));
		label.setContents(explanation.toString());  
	}

	@Override
	public Map<String, String> getVariables() {
		return null;
	}

	public Button getCancelButton() {
		return cancelButton;
	}
	
	public Button getEditButton() {
		return editButton;
	}
	
	public void disableButtons() {
		resetButton.setDisabled(true);
		editButton.setDisabled(true);
	}

	@Override
	public boolean validate() {
		boolean valid = super.validate();
		if (valid) {
			ValidateTemplateRequest request = new ValidateTemplateRequest();
			request.setTask(task);
			request.setBody(message.getValueAsString());
			request.setSubject(subject.getValueAsString());
			GwtCommand command = new GwtCommand(ValidateTemplateRequest.COMMAND);
			command.setCommandRequest(request);
			GwtCommandDispatcher.getInstance().execute(command, 
					new AbstractCommandCallback<ValidateTemplateResponse>() {
				public void execute(ValidateTemplateResponse response) {
					if (!response.isValid()) {
						String corruptPlaceholder = response.getCorruptPlaceholder();
						if (null != corruptPlaceholder) {
							SC.warn(HtmlBuilder.divClassHtmlContent("commentBlockInfo", 
									"At least one placeholder is not allowed.",
									HtmlBuilder.tagHtmlContent(Html.Tag.P, "First placeholder which is not allowed: ",
											HtmlBuilder.tagClass(Html.Tag.SPAN, "commentBlockTitleText", 
													corruptPlaceholder)), 
									HtmlBuilder.tag(Html.Tag.P, "Please, correct the used placeholders.")));
						} else {
							SC.warn("Unknown problem occurred while editing the template.");
						}
					} else {
						updateTemplate();
						if (null != finishHandler) {
							finishHandler.run();
						} 
						disableButtons();
					}
				}
			});
		}
		return false;
	}

	/**
	 * Update email data from altered template.
	 */
	public void updateTemplate() {
		UpdateEmailDataRequest request = new UpdateEmailDataRequest(templateTitle);
		request.setSubject(subject.getValueAsString());
		request.setFrom(from.getValueAsString());
		request.setBody(message.getValueAsString());
		GwtCommand command = new GwtCommand(UpdateEmailDataRequest.COMMAND);
		command.setCommandRequest(request);
		GwtCommandDispatcher.getInstance().execute(command, new AbstractCommandCallback<UpdateEmailDataResponse>() {
			public void execute(UpdateEmailDataResponse response) {
				if (response.isUpdated()) {
					SC.say("Template has been successfully edited.");
				}
			}
		});
	}

	public void setFinishHandler(Runnable runnable) {
		finishHandler = runnable;
	}

	/**
	 * ChangedHandler which updates the form if changed.
	 *
	 * @author Emiel Ackermann
	 */
	private class FormChangedHandler implements ChangedHandler {
		
		public void onChanged(ChangedEvent event) {
			resetButton.setDisabled(false);
			editButton.setDisabled(false);
		}
	}

}