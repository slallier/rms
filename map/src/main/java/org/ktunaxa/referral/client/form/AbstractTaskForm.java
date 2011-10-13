/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */

package org.ktunaxa.referral.client.form;

import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.layout.VLayout;
import org.ktunaxa.referral.client.gui.LayoutConstant;
import org.ktunaxa.referral.server.dto.TaskDto;

import java.util.Map;

/**
 * Base class for building a task form.
 *
 * @author Joachim Van der Auwera
 */
public abstract class AbstractTaskForm extends VLayout {

	private HTMLFlow taskTitle = new HTMLFlow();
	private DynamicForm[] forms;

	public AbstractTaskForm() {
		super();

		setWidth100();
		setHeight100();
		setMembersMargin(LayoutConstant.MARGIN_SMALL);
		taskTitle.setWidth100();
		addMember(taskTitle);
	}

	public void refresh(TaskDto task) {
		taskTitle.setContents("<h3>" + task.getName() + "</h3>" +
				"<div>" + task.getDescription() + "</div>");
	}

	/**
	 * Set the form field items.
	 *
	 * @param formItems form field items
	 */
	public void setFields(FormItem... formItems) {
		DynamicForm form = new DynamicForm();
		form.setWidth100();
		form.setFields(formItems);
		form.setColWidths("160", "100%");
		form.setStyleName("taskBlockContent");
		setForms(form);
	}

	/**
	 * Set the forms for this task form. This method should only be called once!
	 *
	 * @param forms forms to add
	 */
	public void setForms(DynamicForm... forms) {
		if (null != this.forms) {
			throw new IllegalStateException("setForms should only be called once with a non-null value");
		}
		this.forms = forms;
		if (null != forms) {
			for (DynamicForm form : forms) {
				addMember(form);
			}
		}
	}

	/**
	 * Validate the form value and return whether validation succeeded.
	 * This will display validation markers if needed.
	 *
	 * @return true when validation succeeded
	 */
	public boolean validate() {
		boolean valid = true;
		if (null != forms) {
			for (DynamicForm form : forms) {
				valid &= form.validate();
			}
		}
		return valid;
	}

	/**
	 * Validate the form value using callbacks for the correct case. Display validation markers if needed.
	 *
	 * @param valid action to run when validation succeeded
	 * @param invalid action to run when validation failed
	 */
	public void validate(Runnable valid, Runnable invalid) {
		if (validate()) {
			if (null != valid) {
				valid.run();
			}
		} else {
			if (null != invalid) {
				invalid.run();
			}
		}
	}

	/**
	 * Get the variables from the form.
	 *
	 * @return form values
	 */
	public abstract Map<String, String> getVariables();

	/**
	 * Convert object to string in a way that works when the object is null.
	 *
	 * @param object object to convert to string
	 * @return string for the object or null if null was passed
	 */
	protected String nullSafeToString(Object object) {
		if (null == object) {
			return null;
		}
		return object.toString();
	}
}