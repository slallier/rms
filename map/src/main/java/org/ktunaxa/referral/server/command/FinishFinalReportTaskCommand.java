/*
 * This is part of the Ktunaxa referral system.
 *
 * Copyright 2011 Ktunaxa Nation Council, http://www.ktunaxa.org/, Canada.
 */

package org.ktunaxa.referral.server.command;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.geomajas.command.Command;
import org.geomajas.command.CommandDispatcher;
import org.geomajas.command.CommandResponse;
import org.geomajas.command.SuccessCommandResponse;
import org.geomajas.geometry.Crs;
import org.geomajas.global.ExceptionCode;
import org.geomajas.global.GeomajasException;
import org.geomajas.layer.VectorLayerService;
import org.geomajas.layer.feature.Attribute;
import org.geomajas.layer.feature.InternalFeature;
import org.geomajas.layer.feature.attribute.AssociationValue;
import org.geomajas.layer.feature.attribute.LongAttribute;
import org.geomajas.layer.feature.attribute.OneToManyAttribute;
import org.geomajas.layer.feature.attribute.PrimitiveAttribute;
import org.geomajas.plugin.reporting.command.dto.PrepareReportingRequest;
import org.geomajas.plugin.reporting.command.dto.PrepareReportingResponse;
import org.geomajas.security.SecurityContext;
import org.geomajas.service.DispatcherUrlService;
import org.geomajas.service.FilterService;
import org.geomajas.service.GeoService;
import org.ktunaxa.referral.client.form.FinalReportClickHandler;
import org.ktunaxa.referral.client.referral.ReferralUtil;
import org.ktunaxa.referral.server.command.dto.FinishFinalReportTaskRequest;
import org.ktunaxa.referral.server.command.dto.FinishFinalReportTaskResponse;
import org.ktunaxa.referral.server.command.dto.FinishTaskRequest;
import org.ktunaxa.referral.server.command.dto.SendEmailRequest;
import org.ktunaxa.referral.server.service.CmisService;
import org.ktunaxa.referral.server.service.KtunaxaConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command to finish the final report task. This will create the report, save it as attachment in the CMS and send the
 * final report e-mail to the proponent, including the final report and other documents which need to be included.
 *
 * @author Joachim Van der Auwera
 */
@Component
@Transactional(rollbackFor = { Exception.class })
public class FinishFinalReportTaskCommand
		implements Command<FinishFinalReportTaskRequest, FinishFinalReportTaskResponse> {

	private final Logger log = LoggerFactory.getLogger(FinishFinalReportTaskCommand.class);

	public static final String MIME_PDF = "application/pdf";

	@Autowired
	private CommandDispatcher commandDispatcher;

	@Autowired
	private SecurityContext securityContext;

	@Autowired
	private VectorLayerService vectorLayerService;

	@Autowired
	private DispatcherUrlService dispatcherUrlService;

	@Autowired
	private CmisService cmisService;

	@Autowired
	private FilterService filterService;

	@Autowired
	private GeoService geoService;

	public FinishFinalReportTaskResponse getEmptyCommandResponse() {
		return new FinishFinalReportTaskResponse();
	}

	public void execute(FinishFinalReportTaskRequest request, FinishFinalReportTaskResponse response) throws Exception {
		String referralId = request.getReferralId();
		if (null == referralId) {
			throw new GeomajasException(ExceptionCode.PARAMETER_MISSING, "referralId");
		}

		// build report and save as in referral
		CommandResponse prepareReportResponse =
				commandDispatcher.execute(PrepareReportingRequest.COMMAND, request.getPrepareReportingRequest(),
						securityContext.getToken(), null);
		if (prepareReportResponse.isError()) {
			response.getErrors().addAll(prepareReportResponse.getErrors());
			response.getErrorMessages().addAll(prepareReportResponse.getErrorMessages());
			response.getExceptions().addAll(prepareReportResponse.getExceptions());
			throw new GeomajasException(ExceptionCode.UNEXPECTED_PROBLEM, "Could not prepare report.");
		}
		OneToManyAttribute newDocuments = null;
		if (prepareReportResponse instanceof PrepareReportingResponse) {
			PrepareReportingResponse prr = (PrepareReportingResponse) prepareReportResponse;
			String url = dispatcherUrlService.getDispatcherUrl() + prr.getRelativeUrl().
					replace(PrepareReportingResponse.REPORT_NAME, FinalReportClickHandler.REPORT_NAME).
					replace(PrepareReportingResponse.FORMAT, FinalReportClickHandler.FORMAT);
			log.debug("Report URL {}", url);

			// download report and upload as document in Alfresco
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			String filename = referralId.replace('/', '_') + "-finalReport.pdf";
			Document cmisDocument = cmisService.create(filename, MIME_PDF, httpResponse.getEntity().getContent(),
					ReferralUtil.getYear(referralId), referralId);
			log.debug("Uploaded in Alfresco");
			if (!httpGet.isAborted()) {
				httpGet.abort(); // assure low level resources are released
			}
			log.debug("Aborted input for safety");

			// add document in referral
			Crs crs = geoService.getCrs2(KtunaxaConstant.LAYER_CRS);
			List<InternalFeature> features = vectorLayerService.getFeatures(KtunaxaConstant.LAYER_REFERRAL_SERVER_ID,
					crs,
					filterService.parseFilter(ReferralUtil.createFilter(referralId)), null,
					VectorLayerService.FEATURE_INCLUDE_ATTRIBUTES);
			InternalFeature orgReferral = features.get(0);
			log.debug("Got referral {}", referralId);
			InternalFeature referral = orgReferral.clone();
			Map<String, Attribute> attributes = referral.getAttributes();
			List<InternalFeature> newFeatures = new ArrayList<InternalFeature>();
			newFeatures.add(referral);
			OneToManyAttribute orgDocuments = (OneToManyAttribute) attributes.get(KtunaxaConstant.ATTRIBUTE_DOCUMENTS);
			List<AssociationValue> documents = new ArrayList<AssociationValue>(orgDocuments.getValue());
			AssociationValue finalReportDocument = new AssociationValue();
			finalReportDocument.setStringAttribute(KtunaxaConstant.ATTRIBUTE_DOCUMENT_ADDED_BY,
					securityContext.getUserId());
			finalReportDocument.setDateAttribute(KtunaxaConstant.ATTRIBUTE_DOCUMENT_ADDITION_DATE, new Date());
			finalReportDocument.setStringAttribute(KtunaxaConstant.ATTRIBUTE_DOCUMENT_DESCRIPTION, "final report");
			finalReportDocument.setStringAttribute(KtunaxaConstant.ATTRIBUTE_DOCUMENT_DISPLAY_URL,
					cmisService.getDisplayUrl(cmisDocument));
			finalReportDocument.setStringAttribute(KtunaxaConstant.ATTRIBUTE_DOCUMENT_DOWNLOAD_URL,
					cmisService.getDownloadUrl(cmisDocument));
			finalReportDocument.setStringAttribute(KtunaxaConstant.ATTRIBUTE_DOCUMENT_ID, filename);
			finalReportDocument.setStringAttribute(KtunaxaConstant.ATTRIBUTE_DOCUMENT_TITLE, filename);
			finalReportDocument.setBooleanAttribute(KtunaxaConstant.ATTRIBUTE_DOCUMENT_INCLUDE_IN_REPORT, true);
			finalReportDocument.setBooleanAttribute(KtunaxaConstant.ATTRIBUTE_DOCUMENT_CONFIDENTIAL, false);
			finalReportDocument.setManyToOneAttribute(KtunaxaConstant.ATTRIBUTE_DOCUMENT_TYPE,
					new AssociationValue(new LongAttribute(1L), // official response
							new HashMap<String, PrimitiveAttribute<?>>()));
			documents.add(finalReportDocument);
			newDocuments = new OneToManyAttribute(documents);
			attributes.put(KtunaxaConstant.ATTRIBUTE_DOCUMENTS, newDocuments);
			log.debug("Going to add document in referral");
			vectorLayerService.saveOrUpdate(KtunaxaConstant.LAYER_REFERRAL_SERVER_ID, crs, features, newFeatures);
			log.debug("Document added in referral");
		}

		// send e-mail with final report
		if (request.isSendMail()) {
			if (null == newDocuments) {
				throw new GeomajasException(ExceptionCode.UNEXPECTED_PROBLEM, "No documents in referral after update.");
			}
			List<String> attachments = new ArrayList<String>();
			for (AssociationValue doc : newDocuments.getValue()) {
				log.debug("include " + doc.getAttributeValue(KtunaxaConstant.ATTRIBUTE_DOCUMENT_INCLUDE_IN_REPORT));
				if ((Boolean) doc.getAttributeValue(KtunaxaConstant.ATTRIBUTE_DOCUMENT_INCLUDE_IN_REPORT)) {
					String url = (String) doc.getAttributeValue(KtunaxaConstant.ATTRIBUTE_DOCUMENT_DOWNLOAD_URL);
					log.debug("Add attachment {}", url);
					attachments.add(url);
				}
			}
			log.debug("e-mail attachments {}", attachments);
			request.setAttachmentUrls(attachments);
			log.debug("Going to send final report e-mail.");
			CommandResponse emailResponse = commandDispatcher.execute(SendEmailRequest.COMMAND, request,
					securityContext.getToken(), null);
			if (emailResponse.isError() || !(emailResponse instanceof SuccessCommandResponse) ||
					!((SuccessCommandResponse) emailResponse).isSuccess()) {
				response.getErrors().addAll(emailResponse.getErrors());
				response.getErrorMessages().addAll(emailResponse.getErrorMessages());
				response.getExceptions().addAll(emailResponse.getExceptions());
				throw new GeomajasException(ExceptionCode.UNEXPECTED_PROBLEM, "Could not send e-mail.");
			}
			log.debug("Final report e-mail sent.");
		}

		// finish task
		FinishTaskRequest finishTaskRequest = new FinishTaskRequest();
		finishTaskRequest.setTaskId(request.getTaskId());
		finishTaskRequest.setVariables(request.getVariables());
		CommandResponse finishResponse =
				commandDispatcher.execute(FinishTaskRequest.COMMAND, request, securityContext.getToken(), null );
		if (finishResponse.isError() || !(finishResponse instanceof SuccessCommandResponse) ||
					!((SuccessCommandResponse) finishResponse).isSuccess()) {
			response.getErrors().addAll(finishResponse.getErrors());
			response.getErrorMessages().addAll(finishResponse.getErrorMessages());
			response.getExceptions().addAll(finishResponse.getExceptions());
			throw new GeomajasException(ExceptionCode.UNEXPECTED_PROBLEM, "Could not finish task.");
		}

		response.setSuccess(true);
	}
}