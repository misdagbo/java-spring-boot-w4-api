/**
 * 
 */
package com.w4.api.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.w4.api.contracts.*;
import com.w4.api.engine.CoreEngine;
import com.w4.api.infrastructures.StringUtils;
import com.w4.api.infrastructures.Utility;
import com.w4.api.models.*;

import eu.w4.bpm.BPMException;
import eu.w4.bpm.service.BPMService;
import eu.w4.bpm.service.BPMServiceFactory;
import eu.w4.bpm.service.BPMSessionId;

/**
 * @author frederic
 *
 */
public class TaskBusiness {

	private static final Logger logger = LoggerFactory.getLogger(TaskBusiness.class);
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static TaskResponse createWorkcase(TaskRequest request) throws BPMException {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				hostSetting = Utility.getHostSetting(request.getProcess());
				if (hostSetting != null) {
					bpmService = BPMServiceFactory.getService(
							Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
					bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
							request.getSession().getPassword());
				}

				if (bpmSessionId != null) {
					request.setListItem(Utility.setTaskVariable(request.getTaskItem().getVariables()));

					// entering transactional mode
					bpmService.getSessionService().setAutoCommit(bpmSessionId, false);

					request.getSession().setBpmService(bpmService);
					request.getSession().setBpmSessionId(bpmSessionId);

					response = CoreEngine.createWorkcase(request);

					if (response.getHasError()) {
						throw new Exception(response.getMessage());
					}

					if (StringUtils.blank(response.getItem().getKey())) {
						request.getTaskItem().setIdDossier(response.getItem().getKey());
						request.getTaskItem().setIdTache((String) response.getItem().getValeur());

						// Test if go to nex step
						if (request.isNextStep()) {
							request.setCommentaire(new Commentaire());
							TaskResponse responseSubmitTask = CoreEngine.submitTask(request);

							if (responseSubmitTask.getHasError()) {
								throw new Exception(responseSubmitTask.getMessage());
							}
						}

						if (request.isReportCalled()) {
							ReportRequest paramsReport = new ReportRequest();
							paramsReport.setVariables(request.getTaskItem().getVariables());
							paramsReport.getVariables().put("idDossier", request.getTaskItem().getIdDossier());
							OkHttpClient client = new OkHttpClient();
							String json = new Gson().toJson(paramsReport);
							RequestBody body = RequestBody.create(JSON, json);
							Request requestReport = new Request.Builder().url(request.getUrlReport()).post(body)
									.build();
							Response responseReport = client.newCall(requestReport).execute();
							ReportResponse reportResponse = new Gson().fromJson(responseReport.body().string(),
									ReportResponse.class);

							if (reportResponse.getHasError()) {
								throw new Exception(reportResponse.getMessage());
							}

							if (request.getDocuments() != null) {
								if (reportResponse.getDocuments() != null) {
									request.getDocuments().addAll(reportResponse.getDocuments());
								}
							}
						}

						// attach document
						DocumentResponse responseDocument = CoreEngine.attacheFile(request);

						if (responseDocument.getHasError()) {
							throw new Exception(responseDocument.getMessage());
						}

						// commit transaction
						bpmService.getSessionService().commit(bpmSessionId);
					}
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
				// rollback if problem
				bpmService.getSessionService().rollback(bpmSessionId);
			} finally {
				try {
					// stop transactional mode
					bpmService.getSessionService().setAutoCommit(bpmSessionId, true);

					if (bpmSessionId != null) {
						bpmService.getSessionService().closeSession(bpmSessionId);
					}
				} catch (Exception ex) {
					response.setHasError(true);
					response.setMessage(ex.getMessage());
					logger.warn("EXCEPTION : " + ex.getMessage());
				}
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getWorkcase(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getTaskItem(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getTaskMyGoupLock(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getTaskMyGoupLock(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getMyTaskLock(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getMyTaskLock(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getMyTaskLockByActor(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getMyTaskLockByActor(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getMyTaskOffer(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getMyTaskOffer(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getWorkcaseDetails(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getWorkcaseDetails(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getAlarmTaskItem(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getAlarmTaskItem(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getTaskVariables(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getTaskVariables(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse submitTask(TaskRequest request) throws BPMException {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				hostSetting = Utility.getHostSetting(request.getProcess());
				if (hostSetting != null) {
					bpmService = BPMServiceFactory.getService(
							Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
					bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
							request.getSession().getPassword());
				}

				if (bpmSessionId != null) {
					request.setListItem(Utility.setTaskVariable(request.getTaskItem().getVariables()));

					// entering transactional mode
					bpmService.getSessionService().setAutoCommit(bpmSessionId, false);

					request.getSession().setBpmService(bpmService);
					request.getSession().setBpmSessionId(bpmSessionId);

					TaskResponse responseSubmitTask = CoreEngine.submitTask(request);

					if (responseSubmitTask.getHasError()) {
						throw new Exception(responseSubmitTask.getMessage());
					}

					if (request.isReportCalled()) {
						ReportRequest paramsReport = new ReportRequest();
						paramsReport.setVariables(request.getTaskItem().getVariables());
						paramsReport.getVariables().put("idDossier", request.getTaskItem().getIdDossier());
						OkHttpClient client = new OkHttpClient();
						String json = new Gson().toJson(paramsReport);
						RequestBody body = RequestBody.create(JSON, json);
						Request requestReport = new Request.Builder().url(request.getUrlReport()).post(body).build();
						Response responseReport = client.newCall(requestReport).execute();
						ReportResponse reportResponse = new Gson().fromJson(responseReport.body().string(),
								ReportResponse.class);

						if (reportResponse.getHasError()) {
							throw new Exception(reportResponse.getMessage());
						}

						if (request.getDocuments() != null) {
							if (reportResponse.getDocuments() != null) {
								request.getDocuments().addAll(reportResponse.getDocuments());
							}
						}
					}

					// attach document
					DocumentResponse responseDocument = CoreEngine.attacheFile(request);

					if (responseDocument.getHasError()) {
						throw new Exception(responseDocument.getMessage());
					}

					// commit transaction
					bpmService.getSessionService().commit(bpmSessionId);
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
				// rollback if problem
				bpmService.getSessionService().rollback(bpmSessionId);
			} finally {
				try {
					// stop transactional mode
					bpmService.getSessionService().setAutoCommit(bpmSessionId, true);

					if (bpmSessionId != null) {
						bpmService.getSessionService().closeSession(bpmSessionId);
					}
				} catch (Exception ex) {
					response.setHasError(true);
					response.setMessage(ex.getMessage());
					logger.warn("EXCEPTION : " + ex.getMessage());
				}
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse unlockTask(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.unlockTask(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse saveTask(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				request.setListItem(Utility.setTaskVariable(request.getTaskItem().getVariables()));
				response = CoreEngine.saveTask(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				response.setHasError(true);
				response.setMessage(e.getMessage());
				logger.warn("EXCEPTION : " + e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse modifyVariable(TaskRequest request) throws BPMException {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				hostSetting = Utility.getHostSetting(request.getProcess());
				if (hostSetting != null) {
					bpmService = BPMServiceFactory.getService(
							Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
					bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
							request.getSession().getPassword());
				}

				if (bpmSessionId != null) {
					request.setListItem(Utility.setTaskVariable(request.getTaskItem().getVariables()));

					// entering transactional mode
					bpmService.getSessionService().setAutoCommit(bpmSessionId, false);

					request.getSession().setBpmService(bpmService);
					request.getSession().setBpmSessionId(bpmSessionId);

					TaskResponse responseSubmitTask = CoreEngine.modifyVariable(request);

					if (responseSubmitTask.getHasError()) {
						throw new Exception(responseSubmitTask.getMessage());
					}

					// attach document
					DocumentResponse responseDocument = CoreEngine.attacheFile(request);

					if (responseDocument.getHasError()) {
						throw new Exception(responseDocument.getMessage());
					}

					// commit transaction
					bpmService.getSessionService().commit(bpmSessionId);
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
				// rollback if problem
				bpmService.getSessionService().rollback(bpmSessionId);
			} finally {
				try {
					// stop transactional mode
					bpmService.getSessionService().setAutoCommit(bpmSessionId, true);

					if (bpmSessionId != null) {
						bpmService.getSessionService().closeSession(bpmSessionId);
					}
				} catch (Exception ex) {
					response.setHasError(true);
					response.setMessage(ex.getMessage());
					logger.warn("EXCEPTION : " + ex.getMessage());
				}
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse lockTask(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.lockTask(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getWorkcaseVariables(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getWorkcaseVariables(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse searchWorkcase(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.searchWorkcase(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse checkTaskStatus(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.checkTaskStatus(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse reassignTask(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				response = CoreEngine.reassignTask(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getCountMyTaskLock(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getCountMyTaskLock(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getCountTaskOffer(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getCountTaskOffer(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static TaskResponse getAlarmTaskSupervision(TaskRequest request) {
		TaskResponse response = new TaskResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getAlarmTaskSupervision(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				logger.warn("EXCEPTION : " + e.getMessage());
				response.setHasError(true);
				response.setMessage(e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}
}
