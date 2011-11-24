/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */
package org.ktunaxa.referral.client.referral;

import java.util.Date;
import java.util.HashMap;

import org.geomajas.command.CommandResponse;
import org.geomajas.command.dto.PersistTransactionRequest;
import org.geomajas.command.dto.PersistTransactionResponse;
import org.geomajas.gwt.client.command.CommandCallback;
import org.geomajas.gwt.client.command.GwtCommand;
import org.geomajas.gwt.client.command.GwtCommandDispatcher;
import org.geomajas.gwt.client.map.MapModel;
import org.geomajas.gwt.client.map.feature.Feature;
import org.geomajas.gwt.client.map.feature.FeatureTransaction;
import org.geomajas.gwt.client.map.layer.VectorLayer;
import org.geomajas.layer.feature.attribute.AssociationValue;
import org.geomajas.layer.feature.attribute.LongAttribute;
import org.geomajas.layer.feature.attribute.PrimitiveAttribute;
import org.geomajas.widget.utility.gwt.client.wizard.Wizard;
import org.geomajas.widget.utility.gwt.client.wizard.WizardWidget;
import org.ktunaxa.referral.client.gui.MapLayout;
import org.ktunaxa.referral.server.command.dto.CreateProcessRequest;
import org.ktunaxa.referral.server.service.KtunaxaConstant;

import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

/**
 * Wizard to create a new referral.
 * 
 * @author Jan De Moerloose
 */
public class ReferralCreationWizard extends Wizard<ReferralData> {

	private ReferralData data;

	private Runnable finishAction;

	public ReferralCreationWizard(Runnable finishAction) {
		super(new ReferralWizardView());
		this.finishAction = finishAction;
	}

	public void init() {
		addPage(new ReferralInfoPage());
		addPage(new AddGeometryPage());
		addPage(new ReferralConfirmPage());
		addPage(new AttachDocumentPage());
		start();
	}

	public void onCancel() {
		// start();
		finishAction.run();
	}

	public void onFinish() {
		// update referral, mark status as "in progress" and create business process
		SC.ask("Are you sure you want to finish the referral, creating the process ?", new BooleanCallback() {

			public void execute(Boolean value) {
				if (value != null && value) {
					getView().setLoading(true);
					VectorLayer layer = data.getLayer();

					Feature[] old;
					if (null == data.getFeature().getId()) {
						old = new Feature[0];
					} else {
						old = new Feature[] {data.getFeature()};
					}

					// KTU-257 update status to in-progress
					data.getFeature().setManyToOneAttribute(KtunaxaConstant.ATTRIBUTE_STATUS, new AssociationValue(
							new LongAttribute(2L), new HashMap<String, PrimitiveAttribute<?>>()));

					final FeatureTransaction ft = new FeatureTransaction(layer, old, new Feature[] {data.getFeature()});
					PersistTransactionRequest request = new PersistTransactionRequest();
					request.setFeatureTransaction(ft.toDto());
					final MapModel mapModel = layer.getMapModel();
					// assume layer crs
					request.setCrs(mapModel.getCrs());

					GwtCommand command = new GwtCommand(PersistTransactionRequest.COMMAND);
					command.setCommandRequest(request);

					GwtCommandDispatcher.getInstance().execute(command, new CommandCallback<CommandResponse>() {

						public void execute(CommandResponse response) {
							if (response instanceof PersistTransactionResponse) {
								PersistTransactionResponse ptr = (PersistTransactionResponse) response;
								mapModel.applyFeatureTransaction(new FeatureTransaction(ft.getLayer(), ptr
										.getFeatureTransaction()));
								Feature newFeature = new Feature(ptr.getFeatureTransaction().getNewFeatures()[0], ft
										.getLayer());
								createProcess(newFeature);
							}
						}
					});
				}
			}
		});
	}

	private void start() {
		data = new ReferralData(MapLayout.getInstance().getReferralLayer());
		start(data);
	}

	private void createProcess(Feature feature) {
		CreateProcessRequest request = new CreateProcessRequest();
		final String referralId = ReferralUtil.createId(feature);
		request.setReferralId(referralId);
		request.setDescription((String) feature.getAttributeValue(KtunaxaConstant.ATTRIBUTE_PROJECT));
		request.setEmail((String) feature.getAttributeValue(KtunaxaConstant.ATTRIBUTE_EMAIL));
		request.setEngagementLevel((Integer) feature
				.getAttributeValue(KtunaxaConstant.ATTRIBUTE_ENGAGEMENT_LEVEL_PROVINCE));
		request.setCompletionDeadline((Date) feature.getAttributeValue(KtunaxaConstant.ATTRIBUTE_RESPONSE_DEADLINE));

		GwtCommand command = new GwtCommand(CreateProcessRequest.COMMAND);
		command.setCommandRequest(request);

		GwtCommandDispatcher.getInstance().execute(command, new CommandCallback<CommandResponse>() {

			public void execute(CommandResponse response) {
				getView().setLoading(false);
				SC.ask("Referral " + referralId + " successfully created.<br /><br />" +
						" Create a new referral?", new BooleanCallback() {

					public void execute(Boolean value) {
						if (value != null && value) {
							start();
						} else {
							finishAction.run();
						}
					}
				});
			}
		});
	}

	/**
	 * View part of the wizard.
	 * 
	 * @author Jan De Moerloose
	 */
	public static class ReferralWizardView extends WizardWidget<ReferralData> {

		public ReferralWizardView() {
			super("Referral Creation Wizard",
					"Follow the steps below to create a new referral and add it to the system:");
		}

	}
}
