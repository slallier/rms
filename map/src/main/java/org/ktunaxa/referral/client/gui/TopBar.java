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

package org.ktunaxa.referral.client.gui;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.geomajas.command.CommandResponse;
import org.geomajas.gwt.client.command.AbstractCommandCallback;
import org.geomajas.gwt.client.command.GwtCommand;
import org.geomajas.gwt.client.command.GwtCommandDispatcher;
import org.geomajas.gwt.client.command.event.TokenChangedEvent;
import org.geomajas.gwt.client.command.event.TokenChangedHandler;
import org.geomajas.layer.feature.Feature;
import org.geomajas.plugin.staticsecurity.client.util.SsecAccess;
import org.ktunaxa.bpm.KtunaxaBpmConstant;
import org.ktunaxa.referral.client.referral.ReferralUtil;
import org.ktunaxa.referral.client.security.UserContext;
import org.ktunaxa.referral.client.widget.CommunicationHandler;
import org.ktunaxa.referral.server.command.dto.BulkReferralRequest;
import org.ktunaxa.referral.server.command.dto.CloseReferralRequest;
import org.ktunaxa.referral.server.command.dto.DeleteReferralRequest;
import org.ktunaxa.referral.server.command.dto.FinishReferralRequest;
import org.ktunaxa.referral.server.command.dto.ResetReferralRequest;
import org.ktunaxa.referral.server.service.KtunaxaConstant;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.toolbar.ToolStripMenuButton;
import com.smartgwt.client.widgets.toolbar.ToolStripSeparator;

/**
 * Top menu bar of the general layout. The top bar has a user section (login/logout,status) on the right and a list of
 * links. On the left there is a title.
 * 
 * @author Jan De Moerloose
 */
public class TopBar extends HLayout {

	/**
	 * Menu item titles.
	 */
	private static final String REJECTED = "Rejected, Level 0";

	private static final String STARTED = "Started";

	private static final String CHANGE_INCOMPLETE = "Changed/incomplete";

	private static final String FINISHED = "Finished";

	private static final String LOGOUT = "Logout";

	private static final String ABOUT = "About";

	private static final String ADMIN = "Admin";

	private HTMLFlow leftLabel;

	private ToolStripButton userButton;

	private EditEmailWindow emailWindow = new EditEmailWindow();

	private SystemReportWindow systemReportWindow = new SystemReportWindow();

	private AboutWindow aboutWindow = new AboutWindow();

	private ToolStripSeparator separator;

	private ToolStripMenuButton menuButton;

	/**
	 * Constructs a top bar.
	 */
	public TopBar() {
		super();
		setSize("100%", "44");
		setStyleName("header");

		leftLabel = new HTMLFlow(" ");
		leftLabel.setStyleName("headerText");
		leftLabel.setWidth100();
		addMember(leftLabel);

		LayoutSpacer spacer = new LayoutSpacer();
		addMember(spacer);

		ToolStrip headerBar = new ToolStrip();
		headerBar.setMembersMargin(2);
		headerBar.setSize("445", "44");
		headerBar.addFill();
		headerBar.setStyleName("headerRight");

		userButton = new ToolStripButton(UserContext.getInstance().getName());
		GwtCommandDispatcher.getInstance().addTokenChangedHandler(new TokenChangedHandler() {

			public void onTokenChanged(TokenChangedEvent event) {
				userButton.setTitle(UserContext.getInstance().getName());
				update();
			}
		});
		userButton.setIcon("[ISOMORPHIC]/images/user-icon.png");
		headerBar.addMember(userButton);

		separator = new ToolStripSeparator();
		separator.hide();
		headerBar.addMember(separator);

		menuButton = createAdminButton();
		menuButton.hide();
		headerBar.addMember(menuButton);

		headerBar.addSeparator();

		ToolStripButton logoutButton = new ToolStripButton(LOGOUT);
		logoutButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent clickEvent) {
				SsecAccess.logout();
			}
		});
		headerBar.addMember(logoutButton);

		headerBar.addSeparator();
		ToolStripButton aboutButton = new ToolStripButton(ABOUT);
		aboutButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent clickEvent) {
				aboutWindow.show();
			}
		});
		headerBar.addMember(aboutButton);
		addMember(headerBar);
	}

	/**
	 * Sets the title of the top bar.
	 * 
	 * @param title the new title
	 */
	public void setLeftTitle(@NotNull String title) {
		leftLabel.setContents(title);
	}

	public String getLeftTitle() {
		return leftLabel.getContents();
	}

	private ToolStripMenuButton createAdminButton() {
		Menu menu = new Menu();
		menu.setShowShadow(true);
		menu.setShadowDepth(3);

		MenuItem editEmails = new MenuItem("Edit email template...");
		Menu emailTemplates = new Menu();
		emailTemplates.setItems(new EmailItem(REJECTED, KtunaxaConstant.Email.LEVEL_0), new EmailItem(STARTED,
				KtunaxaConstant.Email.START), new EmailItem(CHANGE_INCOMPLETE, KtunaxaConstant.Email.CHANGE),
				new EmailItem(FINISHED, KtunaxaConstant.Email.RESULT));
		editEmails.setSubmenu(emailTemplates);

		MenuItem referral = new MenuItem("Referral");
		Menu referralActions = new Menu();
		MenuItem closeAction = new MenuItem("Close referral");
		closeAction.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

			public void onClick(MenuItemClickEvent menuItemClickEvent) {
				closeCurrentReferral();
			}
		});
		MenuItem resetAction = new MenuItem("Reset referral");
		resetAction.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

			public void onClick(MenuItemClickEvent menuItemClickEvent) {
				resetCurrentReferral();
			}
		});
		MenuItem deleteAction = new MenuItem("Delete referral");
		deleteAction.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

			public void onClick(MenuItemClickEvent menuItemClickEvent) {
				deleteCurrentReferral();
			}
		});
		MenuItem finishAction = new MenuItem("Finish referral");
		finishAction.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

			public void onClick(MenuItemClickEvent menuItemClickEvent) {
				finishCurrentReferral();
			}
		});
		referralActions.setItems(closeAction, resetAction, deleteAction, finishAction);
		referral.setSubmenu(referralActions);

		MenuItem systemReport = new MenuItem("System report");
		systemReport.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

			public void onClick(MenuItemClickEvent menuItemClickEvent) {
				systemReportWindow.show();
			}
		});

		menu.setItems(editEmails, referral, systemReport);

		return new ToolStripMenuButton(ADMIN, menu);
	}

	/**
	 * Updates the admin section of the tool strip.
	 */
	public void update() {
		if (UserContext.getInstance().isAdmin()) {
			separator.show();
			menuButton.show();
		} else {
			separator.hide();
			menuButton.hide();
		}
	}

	private void closeCurrentReferral() {
		final Feature referral = MapLayout.getInstance().getCurrentReferral();
		final List<String> selectedIds = MapLayout.getInstance().getSelectedReferrals();
		if (selectedIds.size() > 0) {
			boolean currentSelected = referral != null && selectedIds.contains(ReferralUtil.createId(referral));
			bulkOperate(selectedIds, CloseReferralRequest.COMMAND, currentSelected);
		} else if (null == referral) {
			SC.say("There is no current referral.");
		} else {
			SC.ask("Are you sure you want to close referral " + ReferralUtil.createId(referral) + " ?",
					new BooleanCallback() {

						public void execute(Boolean close) {
							if (close) {
								CloseReferralRequest request = new CloseReferralRequest();
								request.setReferralId(ReferralUtil.createId(referral));
								DateTimeFormat formatter = DateTimeFormat.getFormat(KtunaxaBpmConstant.DATE_FORMAT);
								request.setReason("Closed by " + UserContext.getInstance().getUser() + " at "
										+ formatter.format(new Date()) + ".");
								GwtCommand command = new GwtCommand(CloseReferralRequest.COMMAND);
								command.setCommandRequest(request);
								CommunicationHandler.get().execute(command,
										new AbstractCommandCallback<CommandResponse>() {

											public void execute(CommandResponse response) {
												MapLayout.getInstance().refreshReferral(true);
											}
										}, "Closing referral...");
							}
						}
					});
		}
	}

	private void resetCurrentReferral() {
		final Feature referral = MapLayout.getInstance().getCurrentReferral();
		final List<String> selectedIds = MapLayout.getInstance().getSelectedReferrals();
		if (selectedIds.size() > 0) {
			boolean currentSelected = referral != null && selectedIds.contains(ReferralUtil.createId(referral));
			bulkOperate(selectedIds, ResetReferralRequest.COMMAND, currentSelected);
		} else if (null == referral) {
			SC.say("There is no current referral.");
		} else {
			SC.ask("Are you sure you want to reset referral " + ReferralUtil.createId(referral) + " ?",
					new BooleanCallback() {

						public void execute(Boolean close) {
							if (close) {
								ResetReferralRequest request = new ResetReferralRequest();
								request.setReferralId(ReferralUtil.createId(referral));
								GwtCommand command = new GwtCommand(ResetReferralRequest.COMMAND);
								command.setCommandRequest(request);
								CommunicationHandler.get().execute(command,
										new AbstractCommandCallback<CommandResponse>() {

											public void execute(CommandResponse response) {
												MapLayout.getInstance().refreshReferral(true);
											}
										}, "Resetting referral...");
							}
						}
					});
		}

	}

	private void deleteCurrentReferral() {
		final Feature referral = MapLayout.getInstance().getCurrentReferral();
		final List<String> selectedIds = MapLayout.getInstance().getSelectedReferrals();
		if (selectedIds.size() > 0) {
			boolean currentSelected = referral != null && selectedIds.contains(ReferralUtil.createId(referral));
			bulkOperate(selectedIds, DeleteReferralRequest.COMMAND, currentSelected);
		} else if (null == referral) {
			SC.say("There is no current referral.");
		} else {
			SC.ask("Are you sure you want to delete referral " + ReferralUtil.createId(referral) + " ?",
					new BooleanCallback() {

						public void execute(Boolean close) {
							if (close) {
								DeleteReferralRequest request = new DeleteReferralRequest();
								request.setReferralId(ReferralUtil.createId(referral));
								GwtCommand command = new GwtCommand(DeleteReferralRequest.COMMAND);
								command.setCommandRequest(request);
								CommunicationHandler.get().execute(command,
										new AbstractCommandCallback<CommandResponse>() {

											public void execute(CommandResponse response) {
												MapLayout.getInstance().setReferralAndTask(null, null);
											}
										}, "Deleting referral...");
							}
						}
					});
		}
	}

	private void finishCurrentReferral() {
		final Feature referral = MapLayout.getInstance().getCurrentReferral();
		final List<String> selectedIds = MapLayout.getInstance().getSelectedReferrals();
		if (selectedIds.size() > 0) {
			boolean currentSelected = referral != null && selectedIds.contains(ReferralUtil.createId(referral));
			bulkOperate(selectedIds, FinishReferralRequest.COMMAND, currentSelected);
		} else if (null == referral) {
			SC.say("There is no current referral.");
		} else {
			SC.ask("Are you sure you want to finish referral " + ReferralUtil.createId(referral) + " ?",
					new BooleanCallback() {

						public void execute(Boolean close) {
							if (close) {
								FinishReferralRequest request = new FinishReferralRequest();
								request.setReferralId(ReferralUtil.createId(referral));
								GwtCommand command = new GwtCommand(FinishReferralRequest.COMMAND);
								command.setCommandRequest(request);
								CommunicationHandler.get().execute(command,
										new AbstractCommandCallback<CommandResponse>() {

											public void execute(CommandResponse response) {
												MapLayout.getInstance().refreshReferral(true);
											}
										}, "Finishing referral...");
							}
						}
					});
		}
	}

	private void bulkOperate(final List<String> selectedIds, final String commandName, final boolean currentSelected) {
		String question = null;
		final String reason;
		DateTimeFormat formatter = DateTimeFormat.getFormat(KtunaxaBpmConstant.DATE_FORMAT);
		if (CloseReferralRequest.COMMAND.equals(commandName)) {
			question = "Are you sure you want to close ";
			reason = "Closed by " + UserContext.getInstance().getUser() + " at " + formatter.format(new Date()) + ".";
		} else if (ResetReferralRequest.COMMAND.equals(commandName)) {
			question = "Are you sure you want to reset ";
			reason = "Reset by " + UserContext.getInstance().getUser() + " at " + formatter.format(new Date()) + ".";
		} else if (DeleteReferralRequest.COMMAND.equals(commandName)) {
			question = "Are you sure you want to delete ";
			reason = "Deleted by " + UserContext.getInstance().getUser() + " at " + formatter.format(new Date()) + ".";
		} else if (FinishReferralRequest.COMMAND.equals(commandName)) {
			question = "Are you sure you want to finish ";
			reason = "Finished by " + UserContext.getInstance().getUser() + " at " + formatter.format(new Date()) + ".";
		} else {
			reason = "";
		}
		question += selectedIds.size() > 1 ? "the " + selectedIds.size() + " selected referrals (" + selectedIds.get(0)
				+ "," + selectedIds.get(1) + ",...) ?" : "the selected referral " + selectedIds.get(0) + " ?";
		SC.ask(question, new BooleanCallback() {

			public void execute(Boolean close) {
				if (close) {
					BulkReferralRequest request = new BulkReferralRequest();
					request.setReferralIds(selectedIds);
					request.setReason(reason);
					request.setCommandName(commandName);
					GwtCommand command = new GwtCommand(BulkReferralRequest.COMMAND);
					command.setCommandRequest(request);
					GwtCommandDispatcher.getInstance().execute(command, new AbstractCommandCallback<CommandResponse>() {

						public void execute(CommandResponse response) {
							if (currentSelected) {
								if (commandName.equals(DeleteReferralRequest.COMMAND)) {
									MapLayout.getInstance().setReferralAndTask(null, null);
								} else {
									MapLayout.getInstance().refreshReferral(true);
								}
							}
							MapLayout.getInstance().refreshSearch();
						}
					});
				}
			}
		});

	}

	/**
	 * Internal class used for the creation of {@link MenuItem}s for editing of
	 * {@link org.ktunaxa.referral.server.domain.Template}s.
	 * 
	 * @author Emiel Ackermann
	 */
	private class EmailItem extends MenuItem {

		public EmailItem(final String title, final String notifier) {
			super(title);

			addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {

				public void onClick(MenuItemClickEvent event) {
					emailWindow.setEmailTemplate(notifier);
					emailWindow.show();
				}
			});
		}
	}
}
