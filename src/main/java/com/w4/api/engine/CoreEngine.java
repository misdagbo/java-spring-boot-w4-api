/**
 * 
 */
package com.w4.api.engine;

import java.util.*;
import java.util.stream.Collectors;

import javax.activation.MimetypesFileTypeMap;

import org.apache.tomcat.util.buf.UDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.w4.api.contracts.*;
import com.w4.api.infrastructures.*;
import com.w4.api.models.*;

import eu.w4.bpm.*;
import eu.w4.bpm.search.*;
import eu.w4.bpm.service.*;
import eu.w4.common.dataset.DataTable;
import eu.w4.data.*;
import eu.w4.data.document.*;
import eu.w4.data.document.service.DMDocumentService;
import eu.w4.data.service.DMSessionId;

/**
 * @author frederic
 *
 */
public class CoreEngine {

	private final static ResourceBundle resource = ResourceBundle.getBundle("application");
	private static final Logger logger = LoggerFactory.getLogger(CoreEngine.class);

	public static TaskResponse getTaskItem(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskFilter bpmTaskFilter = bpmTaskService.createTaskFilter();

				bpmTaskFilter.processIn(request.getProcess());

				// Login Actor
				if (StringUtils.blank(request.getTaskItem().getLoginActeur())) {
					bpmTaskFilter.assigneeIs(new BPMLogicalId(request.getTaskItem().getLoginActeur()), true);
				}

				// ID Dossier
				if (StringUtils.blank(request.getTaskItem().getIdDossier())) {
					bpmTaskFilter.workcaseIs(new BPMInternalId(request.getTaskItem().getIdDossier()));
				}

				// Libelle Tâche
				if (StringUtils.blank(request.getTaskItem().getEtape())) {
					bpmTaskFilter.onStep(new BPMLogicalId(request.getTaskItem().getEtape()));
				}

				// Date Création Task
				if (StringUtils.blank(request.getTaskItem().getDateCreateTask())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateCreateTask())) {
						Date date = DateUtils.strToDate(request.getTaskItem().getDateCreateTask());
						bpmTaskFilter.createdBetween(date, date);
					}
				}

				// Search values variables
				for (String key : request.getTaskItem().getVariables().keySet()) {
					Object value = request.getTaskItem().getVariables().get(key);
					if (StringUtils.blank((String) value)) {
						bpmTaskFilter.containsVariable(key, BPMFilterOperator.EQUAL, value);
					}
				}

				List<BPMTaskSort> sortList = new ArrayList<>();
				if (request.getOrderBy().equals(Global.ASC)) {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.ASC));
				} else {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.DESC));
				}

				// Etat task
				if (StringUtils.blank(request.getTaskItem().getEtat())) {
					if (request.getTaskItem().getEtat().equalsIgnoreCase(Global.W_STATE_RUN_CODE)) {
						bpmTaskFilter.stateIs(BPMFilterOperator.EQUAL, BPMState.RUNNING);
					}
					if (request.getTaskItem().getEtat().equalsIgnoreCase(Global.W_STATE_OFFER_CODE)) {
						bpmTaskFilter.stateIs(BPMFilterOperator.EQUAL, BPMState.OFFERED);
					}
					if (request.getTaskItem().getEtat().equalsIgnoreCase(Global.W_STATE_SAVE_CODE)) {
						bpmTaskFilter.stateIs(BPMFilterOperator.EQUAL, BPMState.SAVED);
					}
				} else {
					bpmTaskFilter.stateIsAlive();
				}

				// Size
				if (!request.getTakeAll()) {
					bpmTaskFilter.maxSizeIs(request.getLastIndex());
					DataTable dt = bpmTaskService.countTasks(bpmSessionId, bpmTaskFilter, null);
					if (dt.next()) {
						response.setCount((int) dt.getObject().getField(0));
					}
				}

				// Attach workcase details
				bpmTaskFilter.attachWorkcaseSnapshot();

				// Attach variables
				bpmTaskFilter.attachAllTaskVariables();

				List<BPMTaskSnapshot> result = bpmTaskService.searchTasks(bpmSessionId, bpmTaskFilter, sortList);

				for (BPMTaskSnapshot item : result) {
					if (StringUtils.blank(request.getTaskItem().getLoginActeur())) {
						TaskItem task = new TaskItem();
						final BPMVariableMap taskVariables = item.getAttachedTaskVariables();

						task.setVariables(Utility.setTaskVariable(taskVariables));
						task.setEtat(Utility.translateState(item.getState().name()));
						task.setDateCreateTask(DateUtils.dateToStringLong(item.getCreationDate()));
						task.setEtape(item.getStepId().getLogicalId());
						task.setIdTache(item.getId().getInternalId());
						final BPMWorkcaseSnapshot wc = item.getAttachedWorkcaseSnapshot();
						task.setDateCreateDossier(DateUtils.dateToStringLong(wc.getCreationDate()));
						task.setIdDossier(wc.getId().getInternalId());
						task.setEtatWorkcase(Utility.translateState(wc.getState().name()));
						final BPMActorService bpmActorService = bpmService.getActorService();
						final BPMActorSnapshot bpmInitiatorSnapshot = bpmActorService.getActor(bpmSessionId,
								wc.getInitiatorId());
						String fullName = (bpmInitiatorSnapshot.getLastName() == null)
								? (bpmInitiatorSnapshot.getFirstName() == null) ? null
										: bpmInitiatorSnapshot.getFirstName()
								: (bpmInitiatorSnapshot.getFirstName() == null) ? bpmInitiatorSnapshot.getLastName()
										: bpmInitiatorSnapshot.getFirstName() + Global.__SPACE
												+ bpmInitiatorSnapshot.getLastName();
						task.setInitiateur(fullName);
						final BPMActorSnapshot bpmActorSnapshot = bpmActorService.getActor(bpmSessionId,
								new BPMLogicalId(request.getTaskItem().getLoginActeur()));
						String fullNameActeur = (bpmActorSnapshot.getLastName() == null)
								? (bpmActorSnapshot.getFirstName() == null) ? null : bpmActorSnapshot.getFirstName()
								: (bpmActorSnapshot.getFirstName() == null) ? bpmActorSnapshot.getLastName()
										: bpmActorSnapshot.getFirstName() + Global.__SPACE
												+ bpmActorSnapshot.getLastName();
						task.setActeur(fullNameActeur);
						task.setLoginActeur(bpmActorSnapshot.getAuthenticationName());
						response.getListTaskItem().add(task);
					} else {
						if (item.getActorId() != null) {
							TaskItem task = new TaskItem();
							final BPMVariableMap taskVariables = item.getAttachedTaskVariables();

							task.setVariables(Utility.setTaskVariable(taskVariables));
							task.setEtat(Utility.translateState(item.getState().name()));
							task.setDateCreateTask(DateUtils.dateToStringLong(item.getCreationDate()));
							task.setEtape(item.getStepId().getLogicalId());
							task.setIdTache(item.getId().getInternalId());
							final BPMWorkcaseSnapshot wc = item.getAttachedWorkcaseSnapshot();
							task.setDateCreateDossier(DateUtils.dateToStringLong(wc.getCreationDate()));
							task.setIdDossier(wc.getId().getInternalId());
							task.setEtatWorkcase(Utility.translateState(wc.getState().name()));
							final BPMActorService bpmActorService = bpmService.getActorService();
							final BPMActorSnapshot bpmInitiatorSnapshot = bpmActorService.getActor(bpmSessionId,
									wc.getInitiatorId());
							String fullName = (bpmInitiatorSnapshot.getLastName() == null)
									? (bpmInitiatorSnapshot.getFirstName() == null) ? null
											: bpmInitiatorSnapshot.getFirstName()
									: (bpmInitiatorSnapshot.getFirstName() == null) ? bpmInitiatorSnapshot.getLastName()
											: bpmInitiatorSnapshot.getFirstName() + Global.__SPACE
													+ bpmInitiatorSnapshot.getLastName();
							task.setInitiateur(fullName);
							final BPMActorSnapshot bpmActorSnapshot = bpmActorService.getActor(bpmSessionId,
									item.getActorId());
							String fullNameActeur = (bpmActorSnapshot.getLastName() == null)
									? (bpmActorSnapshot.getFirstName() == null) ? null : bpmActorSnapshot.getFirstName()
									: (bpmActorSnapshot.getFirstName() == null) ? bpmActorSnapshot.getLastName()
											: bpmActorSnapshot.getFirstName() + Global.__SPACE
													+ bpmActorSnapshot.getLastName();
							task.setActeur(fullNameActeur);
							task.setLoginActeur(bpmActorSnapshot.getAuthenticationName());
							response.getListTaskItem().add(task);
						}
					}
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	public static RoleResponse getRole(RoleRequest request) {
		RoleResponse response = new RoleResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMRoleService bpmRoleService = bpmService.getRoleService();
				final BPMRoleFilter bpmRoleFilter = bpmRoleService.createRoleFilter();

				bpmRoleFilter.usedInProcess(new BPMLogicalId(request.getProcess()));

				// Libelle rôle
				if (StringUtils.blank(request.getLibelle())) {
					bpmRoleFilter.roleNameLike(request.getLibelle());
				}

				List<BPMRoleSnapshot> result = bpmRoleService.searchRoles(bpmSessionId, bpmRoleFilter, null);

				for (BPMRoleSnapshot item : result) {
					response.getListRoles().add(item.getLabel());
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	public static StepResponse getStep(StepRequest request) {
		StepResponse response = new StepResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMProcessService bpmProcessService = bpmService.getProcessService();
				final BPMProcessFilter bpmProcessFilter = bpmProcessService.createProcessFilter();

				bpmProcessFilter.processIs(new BPMLogicalId(request.getProcess()));

				List<BPMProcessSnapshot> result = bpmProcessService.searchProcesses(bpmSessionId, bpmProcessFilter,
						null);

				for (BPMProcessSnapshot item : result) {
					if (item.getRevisionNumber() == result.size()) {
						final BPMStepFilter bpmStepFilter = bpmProcessService.createStepFilter();
						bpmStepFilter.processIs(item.getId());
						bpmStepFilter.typeIs(BPMFilterOperator.EQUAL, BPMStepType.MANUAL);

						List<BPMStepSnapshot> resultStep = bpmProcessService.searchSteps(bpmSessionId, bpmStepFilter,
								null);
						for (BPMStepSnapshot step : resultStep) {
							if (StringUtils.blank(step.getLabel())) {
								response.getListSteps().add(step.getLabel());
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	public static TaskResponse getMyTaskLock(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskFilter bpmTaskFilter = bpmTaskService.createTaskFilter();

				bpmTaskFilter.processIn(request.getProcess());
				bpmTaskFilter.assigneeIs(new BPMLogicalId(bpmSessionId.getUserName()), false);

				// ID Dossier
				if (StringUtils.blank(request.getTaskItem().getIdDossier())) {
					bpmTaskFilter.workcaseIs(new BPMInternalId(request.getTaskItem().getIdDossier()));
				}

				// Libelle Tâche
				if (StringUtils.blank(request.getTaskItem().getEtape())) {
					bpmTaskFilter.onStep(new BPMLogicalId(request.getTaskItem().getEtape()));
				}

				// Date Création Task
				if (StringUtils.blank(request.getTaskItem().getDateCreateTask())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateCreateTask())) {
						Date date = DateUtils.strToDate(request.getTaskItem().getDateCreateTask());
						bpmTaskFilter.createdBetween(date, date);
					}
				}

				// Search values variables
				for (String key : request.getTaskItem().getVariables().keySet()) {
					Object value = request.getTaskItem().getVariables().get(key);
					if (StringUtils.blank((String) value)) {
						bpmTaskFilter.containsVariable(key, BPMFilterOperator.EQUAL, value);
					}
				}

				List<BPMTaskSort> sortList = new ArrayList<>();
				if (request.getOrderBy().equals(Global.ASC)) {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.ASC));
				} else {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.DESC));
				}

				// Etat task
				bpmTaskFilter.stateIsAlive();

				// Size
				if (!request.getTakeAll()) {
					bpmTaskFilter.maxSizeIs(request.getLastIndex());
					DataTable dt = bpmTaskService.countTasks(bpmSessionId, bpmTaskFilter, null);
					if (dt.next()) {
						response.setCount((int) dt.getObject().getField(0));
					}
				}

				// Attach workcase details
				bpmTaskFilter.attachWorkcaseSnapshot();

				// Attach variables
				bpmTaskFilter.attachAllTaskVariables();

				List<BPMTaskSnapshot> result = bpmTaskService.searchTasks(bpmSessionId, bpmTaskFilter, sortList);

				for (BPMTaskSnapshot item : result) {

					TaskItem task = new TaskItem();
					final BPMVariableMap taskVariables = item.getAttachedTaskVariables();

					task.setVariables(Utility.setTaskVariable(taskVariables));
					task.setEtat(Utility.translateState(item.getState().name()));
					task.setDateCreateTask(DateUtils.dateToStringLong(item.getCreationDate()));
					task.setEtape(item.getStepId().getLogicalId());
					task.setIdTache(item.getId().getInternalId());
					final BPMWorkcaseSnapshot wc = item.getAttachedWorkcaseSnapshot();
					task.setDateCreateDossier(DateUtils.dateToStringLong(wc.getCreationDate()));
					task.setIdDossier(wc.getId().getInternalId());
					task.setEtatWorkcase(Utility.translateState(wc.getState().name()));
					final BPMActorService bpmActorService = bpmService.getActorService();
					final BPMActorSnapshot bpmActorSnapshot = bpmActorService.getActor(bpmSessionId,
							wc.getInitiatorId());
					String fullName = (bpmActorSnapshot.getLastName() == null)
							? (bpmActorSnapshot.getFirstName() == null) ? null : bpmActorSnapshot.getFirstName()
							: (bpmActorSnapshot.getFirstName() == null) ? bpmActorSnapshot.getLastName()
									: bpmActorSnapshot.getFirstName() + Global.__SPACE + bpmActorSnapshot.getLastName();
					task.setInitiateur(fullName);

					if (StringUtils.blank(request.getTaskItem().getEtat())) {
						if (request.getTaskItem().getEtat().equalsIgnoreCase(Global.W_STATE_RUN_CODE)) {
							if (item.getState().name().equalsIgnoreCase(request.getTaskItem().getEtat())) {
								response.getListTaskItem().add(task);
							}
						} else {
							if (request.getTaskItem().getEtat().equalsIgnoreCase(Global.W_STATE_SAVE_CODE)) {
								if (item.getState().name().equalsIgnoreCase(request.getTaskItem().getEtat())) {
									response.getListTaskItem().add(task);
								}
							}
						}
					} else {
						response.getListTaskItem().add(task);
					}
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	public static TaskResponse getMyTaskLockByActor(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskFilter bpmTaskFilter = bpmTaskService.createTaskFilter();

				bpmTaskFilter.processIn(request.getProcess());
				bpmTaskFilter.assigneeIs(new BPMLogicalId(request.getTaskItem().getLoginActeur()), false);

				// ID Dossier
				if (StringUtils.blank(request.getTaskItem().getIdDossier())) {
					bpmTaskFilter.workcaseIs(new BPMInternalId(request.getTaskItem().getIdDossier()));
				}

				// Libelle Tâche
				if (StringUtils.blank(request.getTaskItem().getEtape())) {
					bpmTaskFilter.onStep(new BPMLogicalId(request.getTaskItem().getEtape()));
				}

				// Date Création Task
				if (StringUtils.blank(request.getTaskItem().getDateCreateTask())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateCreateTask())) {
						Date date = DateUtils.strToDate(request.getTaskItem().getDateCreateTask());
						bpmTaskFilter.createdBetween(date, date);
					}
				}

				// Search values variables
				for (String key : request.getTaskItem().getVariables().keySet()) {
					Object value = request.getTaskItem().getVariables().get(key);
					if (StringUtils.blank((String) value)) {
						bpmTaskFilter.containsVariable(key, BPMFilterOperator.EQUAL, value);
					}
				}

				List<BPMTaskSort> sortList = new ArrayList<>();
				if (request.getOrderBy().equals(Global.ASC)) {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.ASC));
				} else {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.DESC));
				}

				// Etat task
				bpmTaskFilter.stateIsAlive();

				// Size
				if (!request.getTakeAll()) {
					bpmTaskFilter.maxSizeIs(request.getLastIndex());
					DataTable dt = bpmTaskService.countTasks(bpmSessionId, bpmTaskFilter, null);
					if (dt.next()) {
						response.setCount((int) dt.getObject().getField(0));
					}
				}

				// Attach workcase details
				bpmTaskFilter.attachWorkcaseSnapshot();

				// Attach variables
				bpmTaskFilter.attachAllTaskVariables();

				List<BPMTaskSnapshot> result = bpmTaskService.searchTasks(bpmSessionId, bpmTaskFilter, sortList);

				for (BPMTaskSnapshot item : result) {

					TaskItem task = new TaskItem();
					final BPMVariableMap taskVariables = item.getAttachedTaskVariables();

					task.setVariables(Utility.setTaskVariable(taskVariables));
					task.setEtat(Utility.translateState(item.getState().name()));
					task.setDateCreateTask(DateUtils.dateToStringLong(item.getCreationDate()));
					task.setEtape(item.getStepId().getLogicalId());
					task.setIdTache(item.getId().getInternalId());
					final BPMWorkcaseSnapshot wc = item.getAttachedWorkcaseSnapshot();
					task.setDateCreateDossier(DateUtils.dateToStringLong(wc.getCreationDate()));
					task.setIdDossier(wc.getId().getInternalId());
					task.setEtatWorkcase(Utility.translateState(wc.getState().name()));
					final BPMActorService bpmActorService = bpmService.getActorService();
					final BPMActorSnapshot bpmActorSnapshot = bpmActorService.getActor(bpmSessionId,
							wc.getInitiatorId());
					String fullName = (bpmActorSnapshot.getLastName() == null)
							? (bpmActorSnapshot.getFirstName() == null) ? null : bpmActorSnapshot.getFirstName()
							: (bpmActorSnapshot.getFirstName() == null) ? bpmActorSnapshot.getLastName()
									: bpmActorSnapshot.getFirstName() + Global.__SPACE + bpmActorSnapshot.getLastName();
					task.setInitiateur(fullName);

					if (StringUtils.blank(request.getTaskItem().getEtat())) {
						if (request.getTaskItem().getEtat().equalsIgnoreCase(Global.W_STATE_RUN_CODE)) {
							if (item.getState().name().equalsIgnoreCase(request.getTaskItem().getEtat())) {
								response.getListTaskItem().add(task);
							}
						} else {
							if (request.getTaskItem().getEtat().equalsIgnoreCase(Global.W_STATE_SAVE_CODE)) {
								if (item.getState().name().equalsIgnoreCase(request.getTaskItem().getEtat())) {
									response.getListTaskItem().add(task);
								}
							}
						}
					} else {
						response.getListTaskItem().add(task);
					}
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	public static TaskResponse getTaskMyGoupLock(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskFilter bpmTaskFilter = bpmTaskService.createTaskFilter();

				bpmTaskFilter.processIn(request.getProcess());

				// ID Dossier
				if (StringUtils.blank(request.getTaskItem().getIdDossier())) {
					bpmTaskFilter.workcaseIs(new BPMInternalId(request.getTaskItem().getIdDossier()));
				}

				// Libelle Tâche
				if (StringUtils.blank(request.getTaskItem().getEtape())) {
					bpmTaskFilter.onStep(new BPMLogicalId(request.getTaskItem().getEtape()));
				}

				// Date Création Task
				if (StringUtils.blank(request.getTaskItem().getDateCreateTask())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateCreateTask())) {
						Date date = DateUtils.strToDate(request.getTaskItem().getDateCreateTask());
						bpmTaskFilter.createdBetween(date, date);
					}
				}

				// Search values variables
				for (String key : request.getTaskItem().getVariables().keySet()) {
					Object value = request.getTaskItem().getVariables().get(key);
					if (StringUtils.blank((String) value)) {
						bpmTaskFilter.containsVariable(key, BPMFilterOperator.EQUAL, value);
					}
				}

				List<BPMTaskSort> sortList = new ArrayList<>();
				if (request.getOrderBy().equals(Global.ASC)) {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.ASC));
				} else {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.DESC));
				}

				// Etat task
				bpmTaskFilter.stateIsAlive();

				// Size
				if (!request.getTakeAll()) {
					bpmTaskFilter.maxSizeIs(request.getLastIndex());
					DataTable dt = bpmTaskService.countTasks(bpmSessionId, bpmTaskFilter, null);
					if (dt.next()) {
						response.setCount((int) dt.getObject().getField(0));
					}
				}

				// Attach workcase details
				bpmTaskFilter.attachWorkcaseSnapshot();

				// Attach variables
				bpmTaskFilter.attachAllTaskVariables();

				// Attach actor
				bpmTaskFilter.attachActorSnapshot();

				List<BPMTaskSnapshot> result = bpmTaskService.searchTasks(bpmSessionId, bpmTaskFilter, sortList);

				for (BPMTaskSnapshot item : result) {
					boolean go = false;

					if (item.getState().name().equalsIgnoreCase("SAVED")
							|| item.getState().name().equalsIgnoreCase("RUNNING")) {

						final BPMActorSnapshot bpmActor = item.getAttachedActorSnapshot();
						if (bpmActor != null) {
							if (!bpmActor.getAuthenticationName().equals(bpmSessionId.getUserName())) {

								if (item.getRoleId() != null) {

									final BPMRoleService bpmRoleServiceTask = bpmService.getRoleService();
									final BPMRoleSnapshot bpmRole = bpmRoleServiceTask.getRole(bpmSessionId,
											item.getRoleId());

									final BPMRoleService bpmRoleService = bpmService.getRoleService();
									final BPMRoleFilter bpmRoleFilter = bpmRoleService.createRoleFilter();
									bpmRoleFilter.assignedTo(new BPMLogicalId(bpmSessionId.getUserName()));
									bpmRoleFilter.usedInProcess(new BPMLogicalId(request.getProcess()));
									final List<BPMRoleSnapshot> rolesActorConnected = bpmRoleService
											.searchRoles(bpmSessionId, bpmRoleFilter, null);

									for (BPMRoleSnapshot role : rolesActorConnected) {
										if (role.getLabel().equalsIgnoreCase(bpmRole.getLabel())) {
											go = true;
											break;
										}
									}

									if (go) {
										TaskItem task = new TaskItem();
										final BPMVariableMap taskVariables = item.getAttachedTaskVariables();

										task.setVariables(Utility.setTaskVariable(taskVariables));
										task.setEtat(Utility.translateState(item.getState().name()));
										task.setDateCreateTask(DateUtils.dateToStringLong(item.getCreationDate()));
										task.setEtape(item.getStepId().getLogicalId());
										task.setIdTache(item.getId().getInternalId());
										final BPMWorkcaseSnapshot wc = item.getAttachedWorkcaseSnapshot();
										task.setDateCreateDossier(DateUtils.dateToStringLong(wc.getCreationDate()));
										task.setIdDossier(wc.getId().getInternalId());
										task.setEtatWorkcase(Utility.translateState(wc.getState().name()));
										final BPMActorService bpmActorService = bpmService.getActorService();
										final BPMActorSnapshot bpmActorSnapshot = bpmActorService.getActor(bpmSessionId,
												wc.getInitiatorId());
										String fullName = (bpmActorSnapshot.getLastName() == null)
												? (bpmActorSnapshot.getFirstName() == null) ? null
														: bpmActorSnapshot.getFirstName()
												: (bpmActorSnapshot.getFirstName() == null)
														? bpmActorSnapshot.getLastName()
														: bpmActorSnapshot.getFirstName() + Global.__SPACE
																+ bpmActorSnapshot.getLastName();
										task.setInitiateur(fullName);
										String fullNameActeur = (bpmActor.getLastName() == null)
												? (bpmActor.getFirstName() == null) ? null : bpmActor.getFirstName()
												: (bpmActor.getFirstName() == null) ? bpmActor.getLastName()
														: bpmActor.getFirstName() + Global.__SPACE
																+ bpmActor.getLastName();
										task.setActeur(fullNameActeur);
										task.setLoginActeur(bpmActor.getAuthenticationName());

										if (StringUtils.blank(request.getTaskItem().getEtat())) {
											if (request.getTaskItem().getEtat()
													.equalsIgnoreCase(Global.W_STATE_RUN_CODE)) {
												if (item.getState().name()
														.equalsIgnoreCase(request.getTaskItem().getEtat())) {
													response.getListTaskItem().add(task);
												}
											} else {
												if (request.getTaskItem().getEtat()
														.equalsIgnoreCase(Global.W_STATE_SAVE_CODE)) {
													if (item.getState().name()
															.equalsIgnoreCase(request.getTaskItem().getEtat())) {
														response.getListTaskItem().add(task);
													}
												}
											}
										} else {
											response.getListTaskItem().add(task);
										}
									}
								}
							}
						}
					}
				}

				// group by acteur name
				Map<String, List<TaskItem>> groupByPriceMap = response.getListTaskItem().stream()
						.collect(Collectors.groupingBy(TaskItem::getActeur));

				response.setActorsGroup(groupByPriceMap);
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	public static TaskResponse getMyTaskOffer(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskFilter bpmTaskFilter = bpmTaskService.createTaskFilter();

				bpmTaskFilter.processIn(request.getProcess());
				bpmTaskFilter.inPoolOfActor(new BPMLogicalId(bpmSessionId.getUserName()));

				// ID Dossier
				if (StringUtils.blank(request.getTaskItem().getIdDossier())) {
					bpmTaskFilter.workcaseIs(new BPMInternalId(request.getTaskItem().getIdDossier()));
				}

				// Libelle Tâche
				if (StringUtils.blank(request.getTaskItem().getEtape())) {
					bpmTaskFilter.onStep(new BPMLogicalId(request.getTaskItem().getEtape()));
				}

				// Date Création Task
				if (StringUtils.blank(request.getTaskItem().getDateCreateTask())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateCreateTask())) {
						Date date = DateUtils.strToDate(request.getTaskItem().getDateCreateTask());
						bpmTaskFilter.createdBetween(date, date);
					}
				}

				// Search values variables
				for (String key : request.getTaskItem().getVariables().keySet()) {
					Object value = request.getTaskItem().getVariables().get(key);
					if (StringUtils.blank((String) value)) {
						bpmTaskFilter.containsVariable(key, BPMFilterOperator.EQUAL, value);
					}
				}

				List<BPMTaskSort> sortList = new ArrayList<>();
				if (request.getOrderBy().equals(Global.ASC)) {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.ASC));
				} else {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.DESC));
				}

				// Size
				if (!request.getTakeAll()) {
					bpmTaskFilter.maxSizeIs(request.getLastIndex());
					DataTable dt = bpmTaskService.countTasks(bpmSessionId, bpmTaskFilter, null);
					if (dt.next()) {
						response.setCount((int) dt.getObject().getField(0));
					}
				}

				// Etat task
				bpmTaskFilter.stateIsAlive();

				// Attach workcase details
				bpmTaskFilter.attachWorkcaseSnapshot();

				// Attach variables
				bpmTaskFilter.attachAllTaskVariables();

				List<BPMTaskSnapshot> result = bpmTaskService.searchTasks(bpmSessionId, bpmTaskFilter, sortList);

				for (BPMTaskSnapshot item : result) {

					TaskItem task = new TaskItem();
					final BPMVariableMap taskVariables = item.getAttachedTaskVariables();

					task.setVariables(Utility.setTaskVariable(taskVariables));
					task.setEtat(Utility.translateState(item.getState().name()));
					task.setDateCreateTask(DateUtils.dateToStringLong(item.getCreationDate()));
					task.setEtape(item.getStepId().getLogicalId());
					task.setIdTache(item.getId().getInternalId());
					final BPMWorkcaseSnapshot wc = item.getAttachedWorkcaseSnapshot();
					task.setDateCreateDossier(DateUtils.dateToStringLong(wc.getCreationDate()));
					task.setIdDossier(wc.getId().getInternalId());
					task.setEtatWorkcase(Utility.translateState(wc.getState().name()));
					final BPMActorService bpmActorService = bpmService.getActorService();
					final BPMActorSnapshot bpmActorSnapshot = bpmActorService.getActor(bpmSessionId,
							wc.getInitiatorId());
					String fullName = (bpmActorSnapshot.getLastName() == null)
							? (bpmActorSnapshot.getFirstName() == null) ? null : bpmActorSnapshot.getFirstName()
							: (bpmActorSnapshot.getFirstName() == null) ? bpmActorSnapshot.getLastName()
									: bpmActorSnapshot.getFirstName() + Global.__SPACE + bpmActorSnapshot.getLastName();
					task.setInitiateur(fullName);

					response.getListTaskItem().add(task);
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Get all task variables
	 *
	 * @param request
	 * @return TaskResponse
	 */
	public static TaskResponse searchWorkcaseByVariables(TaskRequest request) throws BPMException {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMWorkcaseService bpmWorkcaseService = bpmService.getWorkcaseService();
				final BPMWorkcaseFilter bpmWorkcaseFilter = bpmWorkcaseService.createWorkcaseFilter();

				bpmWorkcaseFilter.attachAllWorkcaseVariables();

				// Search values variables
				for (String key : request.getTaskItem().getVariables().keySet()) {
					Object value = request.getTaskItem().getVariables().get(key);
					if (StringUtils.blank((String) value)) {
						bpmWorkcaseFilter.containsVariable(key, BPMFilterOperator.EQUAL, value);
					}
				}

				List<BPMWorkcaseSnapshot> result = bpmWorkcaseService.searchWorkcases(bpmSessionId, bpmWorkcaseFilter,
						null);

				for (BPMWorkcaseSnapshot item : result) {
					final TaskItem task = new TaskItem();
					final BPMVariableMap taskVariables = item.getAttachedWorkcaseVariables();
					task.setVariables(Utility.setTaskVariable(taskVariables));
					response.getListTaskItem().add(task);
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Obtenir la liste des t√¢ches d'une corbeille
	 *
	 * @param request
	 * @return TaskResponse
	 */
	public static TaskResponse getAlarmTaskItem(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskFilter bpmTaskFilter = bpmTaskService.createTaskFilter();

				bpmTaskFilter.processIn(request.getProcess());
				bpmTaskFilter.stateIsAlive();
				bpmTaskFilter.assigneeIs(new BPMLogicalId(bpmSessionId.getUserName()), true);

				// ID Dossier
				if (StringUtils.blank(request.getTaskItem().getIdDossier())) {
					bpmTaskFilter.workcaseIs(new BPMInternalId(request.getTaskItem().getIdDossier()));
				}

				// Libelle Tâche
				if (StringUtils.blank(request.getTaskItem().getEtape())) {
					bpmTaskFilter.onStep(new BPMLogicalId(request.getTaskItem().getEtape()));
				}

				// Date Création Task
				if (StringUtils.blank(request.getTaskItem().getDateCreateTask())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateCreateTask())) {
						Date date = DateUtils.strToDate(request.getTaskItem().getDateCreateTask());
						bpmTaskFilter.createdBetween(date, date);
					}
				}

				// Search values variables
				for (String key : request.getTaskItem().getVariables().keySet()) {
					Object value = request.getTaskItem().getVariables().get(key);
					if (StringUtils.blank((String) value)) {
						bpmTaskFilter.containsVariable(key, BPMFilterOperator.EQUAL, value);
					}
				}

				List<BPMTaskSort> sortList = new ArrayList<>();
				if (request.getOrderBy().equals(Global.ASC)) {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.ASC));
				} else {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.DESC));
				}

				// Size
				if (!request.getTakeAll()) {
					bpmTaskFilter.maxSizeIs(request.getLastIndex());
					DataTable dt = bpmTaskService.countTasks(bpmSessionId, bpmTaskFilter, null);
					if (dt.next()) {
						response.setCount((int) dt.getObject().getField(0));
					}
				}

				// Attach workcase details
				bpmTaskFilter.attachWorkcaseSnapshot();

				// Attach variables
				bpmTaskFilter.attachAllTaskVariables();

				List<BPMTaskSnapshot> result = bpmTaskService.searchTasks(bpmSessionId, bpmTaskFilter, sortList);

				for (BPMTaskSnapshot item : result) {

					// Task Alarm
					Calendar cal = Calendar.getInstance();
					String alarmDateConverted = DateUtils.dateToString(cal.getTime());
					Date alarmDate = DateUtils.strToDate(alarmDateConverted);

					if (item.getAlarmDate() != null) {
						if (item.getAlarmDate().before(alarmDate)) {
							TaskItem task = new TaskItem();
							final BPMVariableMap taskVariables = item.getAttachedTaskVariables();

							task.setVariables(Utility.setTaskVariable(taskVariables));
							task.setEtat(Utility.translateState(item.getState().name()));
							task.setDateCreateTask(DateUtils.dateToStringLong(item.getCreationDate()));
							task.setEtape(item.getStepId().getLogicalId());
							task.setIdTache(item.getId().getInternalId());

							final BPMWorkcaseSnapshot wc = item.getAttachedWorkcaseSnapshot();
							task.setDateCreateDossier(DateUtils.dateToStringLong(wc.getCreationDate()));
							task.setIdDossier(wc.getId().getInternalId());
							task.setEtatWorkcase(Utility.translateState(wc.getState().name()));
							final BPMActorService bpmActorService = bpmService.getActorService();
							final BPMActorSnapshot bpmActorSnapshot = bpmActorService.getActor(bpmSessionId,
									wc.getInitiatorId());
							String fullName = (bpmActorSnapshot.getLastName() == null)
									? (bpmActorSnapshot.getFirstName() == null) ? null : bpmActorSnapshot.getFirstName()
									: (bpmActorSnapshot.getFirstName() == null) ? bpmActorSnapshot.getLastName()
											: bpmActorSnapshot.getFirstName() + Global.__SPACE
													+ bpmActorSnapshot.getLastName();
							task.setInitiateur(fullName);

							response.getListTaskItem().add(task);
						}
					}
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Obtenir la liste des t√¢ches d'une corbeille
	 *
	 * @param request
	 * @return TaskResponse
	 */
	public static TaskResponse getAlarmTaskSupervision(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskFilter bpmTaskFilter = bpmTaskService.createTaskFilter();

				bpmTaskFilter.processIn(request.getProcess());
				bpmTaskFilter.stateIsAlive();

				// ID Dossier
				if (StringUtils.blank(request.getTaskItem().getIdDossier())) {
					bpmTaskFilter.workcaseIs(new BPMInternalId(request.getTaskItem().getIdDossier()));
				}

				// Libelle Tâche
				if (StringUtils.blank(request.getTaskItem().getEtape())) {
					bpmTaskFilter.onStep(new BPMLogicalId(request.getTaskItem().getEtape()));
				}

				// Date Création Task
				if (StringUtils.blank(request.getTaskItem().getDateCreateTask())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateCreateTask())) {
						Date date = DateUtils.strToDate(request.getTaskItem().getDateCreateTask());
						bpmTaskFilter.createdBetween(date, date);
					}
				}

				// Search values variables
				for (String key : request.getTaskItem().getVariables().keySet()) {
					Object value = request.getTaskItem().getVariables().get(key);
					if (StringUtils.blank((String) value)) {
						bpmTaskFilter.containsVariable(key, BPMFilterOperator.EQUAL, value);
					}
				}

				List<BPMTaskSort> sortList = new ArrayList<>();
				if (request.getOrderBy().equals(Global.ASC)) {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.ASC));
				} else {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.DESC));
				}

				// Size
				if (!request.getTakeAll()) {
					bpmTaskFilter.maxSizeIs(request.getLastIndex());
					DataTable dt = bpmTaskService.countTasks(bpmSessionId, bpmTaskFilter, null);
					if (dt.next()) {
						response.setCount((int) dt.getObject().getField(0));
					}
				}

				// Attach workcase details
				bpmTaskFilter.attachWorkcaseSnapshot();

				// Attach variables
				bpmTaskFilter.attachAllTaskVariables();

				List<BPMTaskSnapshot> result = bpmTaskService.searchTasks(bpmSessionId, bpmTaskFilter, sortList);

				for (BPMTaskSnapshot item : result) {

					TaskItem task = new TaskItem();
					String color = "";
					final BPMVariableMap taskVariables = item.getAttachedTaskVariables();

					task.setVariables(Utility.setTaskVariable(taskVariables));
					task.setEtat(Utility.translateState(item.getState().name()));
					task.setDateCreateTask(DateUtils.dateToStringLong(item.getCreationDate()));
					task.setEtape(item.getStepId().getLogicalId());
					task.setIdTache(item.getId().getInternalId());

					String dateToString = DateUtils.dateToStringLong(new Date());
					String dateCreationTaskToString = DateUtils.dateToStringLong(item.getCreationDate());
					Date date = DateUtils.formatDate(dateToString, "dd/MM/yyyy HH:mm:ss");
					Date dateCreationTask = DateUtils.formatDate(dateCreationTaskToString, "dd/MM/yyyy HH:mm:ss");
					long diffDate = (date.getTime() >= dateCreationTask.getTime())
							? date.getTime() - dateCreationTask.getTime()
							: dateCreationTask.getTime() - date.getTime();
					long diffDays = diffDate / (24 * 60 * 60 * 1000);
					if (diffDays == 0) {
						long diffHours = diffDate / (60 * 60 * 1000) % 24;
						color = diffHours <= request.getAlarm() ? "green"
								: diffHours <= request.getDelai() ? "yellow" : "red";
						task.setColor(color);
					} else {
						if (diffDays > 0) {
							long dateInHours = diffDays * 24;
							color = dateInHours <= request.getAlarm() ? "green"
									: dateInHours <= request.getDelai() ? "yellow" : "red";
							task.setColor(color);
						}
					}
					final BPMWorkcaseSnapshot wc = item.getAttachedWorkcaseSnapshot();
					task.setDateCreateDossier(DateUtils.dateToStringLong(wc.getCreationDate()));
					task.setIdDossier(wc.getId().getInternalId());
					task.setEtatWorkcase(Utility.translateState(wc.getState().name()));
					final BPMActorService bpmActorService = bpmService.getActorService();
					final BPMActorSnapshot bpmActorSnapshot = bpmActorService.getActor(bpmSessionId,
							wc.getInitiatorId());
					String fullName = (bpmActorSnapshot.getLastName() == null)
							? (bpmActorSnapshot.getFirstName() == null) ? null : bpmActorSnapshot.getFirstName()
							: (bpmActorSnapshot.getFirstName() == null) ? bpmActorSnapshot.getLastName()
									: bpmActorSnapshot.getFirstName() + Global.__SPACE + bpmActorSnapshot.getLastName();
					task.setInitiateur(fullName);

					response.getListTaskItem().add(task);
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Obtenir la liste des t√¢ches d'une corbeille
	 *
	 * @param request
	 * @return TaskResponse
	 */
	public static TaskResponse getWorkcaseDetails(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskFilter bpmTaskFilter = bpmTaskService.createTaskFilter();

				bpmTaskFilter.workcaseIs(new BPMInternalId(request.getTaskItem().getIdDossier()));

				// Libelle Tâche
				if (StringUtils.blank(request.getTaskItem().getEtape())) {
					bpmTaskFilter.onStep(new BPMLogicalId(request.getTaskItem().getEtape()));
				}

				// Date Création Task
				if (StringUtils.blank(request.getTaskItem().getDateCreateTask())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateCreateTask())) {
						Date date = DateUtils.strToDate(request.getTaskItem().getDateCreateTask());
						bpmTaskFilter.createdBetween(date, date);
					}
				}

				// Date End Task
				if (StringUtils.blank(request.getTaskItem().getDateEndTache())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateEndTache())) {
						Date date = DateUtils.strToDate(request.getTaskItem().getDateEndTache());
						bpmTaskFilter.endedBetween(date, date);
					}
				}

				// Search values variables
				for (String key : request.getTaskItem().getVariables().keySet()) {
					Object value = request.getTaskItem().getVariables().get(key);
					if (StringUtils.blank((String) value)) {
						bpmTaskFilter.containsVariable(key, BPMFilterOperator.EQUAL, value);
					}
				}

				List<BPMTaskSort> sortList = new ArrayList<>();
				if (request.getOrderBy().equals(Global.ASC)) {
					sortList.add(
							bpmService.getTaskService().createTaskSort(BPMTaskSortBy.CREATION_DATE, BPMSortMode.ASC));
				} else {
					sortList.add(
							bpmService.getTaskService().createTaskSort(BPMTaskSortBy.CREATION_DATE, BPMSortMode.DESC));
				}

				// Size
				if (!request.getTakeAll()) {
					bpmTaskFilter.maxSizeIs(request.getLastIndex());
					DataTable dt = bpmTaskService.countTasks(bpmSessionId, bpmTaskFilter, null);
					if (dt.next()) {
						response.setCount((int) dt.getObject().getField(0));
					}
				}

				// Attach Workcase details
				bpmTaskFilter.attachWorkcaseSnapshot();

				// Attach variables
				bpmTaskFilter.attachAllTaskVariables();

				List<BPMTaskSnapshot> result = bpmTaskService.searchTasks(bpmSessionId, bpmTaskFilter, sortList);

				for (BPMTaskSnapshot item : result) {

					TaskItem task = new TaskItem();
					final BPMVariableMap taskVariables = item.getAttachedTaskVariables();

					task.setVariables(Utility.setTaskVariable(taskVariables));
					task.setEtat(Utility.translateState(item.getState().name()));
					task.setDateCreateTask(DateUtils.dateToStringLong(item.getCreationDate()));
					task.setEtape(item.getStepId().getLogicalId());
					task.setIdTache(item.getId().getInternalId());

					final BPMWorkcaseSnapshot wc = item.getAttachedWorkcaseSnapshot();
					task.setDateCreateDossier(DateUtils.dateToStringLong(wc.getCreationDate()));
					task.setIdDossier(wc.getId().getInternalId());
					task.setEtatWorkcase(Utility.translateState(wc.getState().name()));

					if (item.getActorId() != null) {
						final BPMActorService bpmActorService = bpmService.getActorService();
						final BPMActorSnapshot bpmActorSnapshot = bpmActorService.getActor(bpmSessionId,
								item.getActorId());
						String fullName = (bpmActorSnapshot.getLastName() == null)
								? (bpmActorSnapshot.getFirstName() == null) ? null : bpmActorSnapshot.getFirstName()
								: (bpmActorSnapshot.getFirstName() == null) ? bpmActorSnapshot.getLastName()
										: bpmActorSnapshot.getFirstName() + Global.__SPACE
												+ bpmActorSnapshot.getLastName();
						task.setActeur(fullName);
						task.setLoginActeur(bpmActorSnapshot.getAuthenticationName());
					}

					if (item.getState().equals(BPMState.DONE)) {
						String date = DateUtils.dateToStringLong(bpmTaskService.getEndDate(bpmSessionId, item.getId()));
						task.setDateEndTache(date);
					}

					response.getListTaskItem().add(task);
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	static public DMDocumentFolderSnapshot getFolderFromPath(DMDocumentService docService, DMSessionId sessionId,
			String source, String path) throws DMObjectNotFoundException {
		String[] folderNames = path.substring(1).split("/");
		DMDocumentFolderSnapshot res = null;
		DMDocumentFolderSnapshot currentFolder = docService.getRootFolder(sessionId, source);
		int currentLevel = 0;
		for (String folderName : folderNames) {
			List<DMDocumentItemSnapshot> folderChildren = docService.getChildren(sessionId, currentFolder);
			for (DMDocumentItemSnapshot folderChild : folderChildren) {
				if (folderChild.isFolder()) {
					if (folderName.equals(folderChild.getName())) {
						currentFolder = folderChild.getAsFolder();
						currentLevel++;
						break;
					}
				}
			}
		}
		if (currentFolder != null && currentLevel == folderNames.length) {
			res = currentFolder;
		} else {
			throw new DMObjectNotFoundException(path);
		}
		return res;
	}

	public static DocumentResponse createIfNotExistsFolderWithPath(DocumentRequest request)
			throws DMObjectNotFoundException, DMObjectAlreadyExistsException {
		DocumentResponse response = new DocumentResponse();

		try {
			if (request.getPath() == null || request.getPath().length() == 0 || "/".equals(request.getPath())) {
				response.setFolderSnapshot(request.getDocumentService()
						.getRootFolder(request.getSession().getBpmSessionId(), request.getSource()));
			} else {
				List<DMLocator> foundItems = null;
				try {
					foundItems = request.getDocumentService().search(request.getSession().getBpmSessionId(),
							request.getDocumentService().getRootFolder(request.getSession().getBpmSessionId(),
									request.getSource()),
							"SELECT * FROM cmis:folder where CONTAINS('PATH:\"" + request.getPath() + "\"')");
				} catch (Exception e) {
					response.setHasError(true);
					response.setMessage(e.getMessage());
					logger.warn("Exception : " + e.getMessage());
				}
				if (foundItems != null && foundItems.size() > 0) {
					response.setFolderSnapshot(request.getDocumentService()
							.getFolder(request.getSession().getBpmSessionId(), foundItems.get(0)));
				} else {
					String folderName = request.getPath().substring(request.getPath().lastIndexOf("/cm:") + 4);
					String parentPath = request.getPath().substring(0, request.getPath().lastIndexOf("/"));
					DocumentRequest requestDocument = new DocumentRequest(request.getDocumentService(),
							request.getSource(), parentPath);
					requestDocument.getSession().setBpmSessionId(request.getSession().getBpmSessionId());
					response = createIfNotExistsFolderWithPath(requestDocument);
					Map<String, Object> folderMetaData = new HashMap<>();
					folderMetaData.put("cmis:name", folderName);
					DMDocumentInfo docInfo = DMHelper.buildDMInfo(folderName, "cmis:folder", folderMetaData);
					DMLocator folderLocator = request.getDocumentService().createFolder(
							request.getSession().getBpmSessionId(), response.getFolderSnapshot(), docInfo);
					response.setFolderSnapshot(request.getDocumentService()
							.getFolder(request.getSession().getBpmSessionId(), folderLocator));
				}
			}
		} catch (DMObjectAlreadyExistsException | DMObjectNotFoundException e) {
			response.setHasError(true);
			response.setMessage(e.getMessage());
			logger.warn("Exception : " + e.getMessage());
		}
		return response;
	}

	/**
	 * Attach document to workcase
	 *
	 * @param request
	 * @return
	 */
	public static DocumentResponse attacheFile(TaskRequest request) throws Exception {
		DocumentResponse response = new DocumentResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;
		DMDocumentFolderSnapshot folderSnapshot = null;

		try {
			if (request.getSession().getBpmService() == null || request.getSession().getBpmSessionId() == null) {
				hostSetting = Utility.getHostSetting(request.getProcess());
				if (hostSetting != null) {
					bpmService = BPMServiceFactory.getService(
							Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
					bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
							request.getSession().getPassword());
				}
			} else {
				bpmService = request.getSession().getBpmService();
				bpmSessionId = request.getSession().getBpmSessionId();
			}

			if (bpmSessionId != null) {
				if (request.getDocuments() != null) {
					for (Document file : request.getDocuments()) {
						String filePath = "/tmp" + Global.__SEPARATOR_SLASH + file.getFileName();
						Utility.savePiece(file.getData(), filePath);

						final DMDocumentService bpmDocumentService = bpmService.getDocumentService();
						final BPMWorkcaseService bpmWorkcaseService = bpmService.getWorkcaseService();

						Map<String, Object> metadata = new HashMap<>();
						metadata.put("cmis:name", file.getFileName());

						DMDocumentInfo info = DMHelper.buildDMInfo(file.getFileName(), "cmis:document", metadata);

						// Récupération du contenu du fichier
						Fichier fileDownload = Utility.downloadFile(filePath);

						// On précise le type MIME
						DMDocumentContentSource source = new DMByteArrayContentSource(fileDownload.getBytes(),
								new MimetypesFileTypeMap().getContentType(fileDownload.getFile()));

						if (folderSnapshot == null) {
							DocumentRequest requestDocument = new DocumentRequest(bpmDocumentService,
									resource.getString("app.alfresco.src"),
									resource.getString("app.alfresco.attachment")
											+ request.getTaskItem().getIdDossier());
							requestDocument.getSession().setBpmSessionId(bpmSessionId);
							response = CoreEngine.createIfNotExistsFolderWithPath(requestDocument);

							if (response.getHasError()) {
								throw new Exception(response.getMessage());
							}

							final DMLocator createdLocator = bpmDocumentService.createDocument(bpmSessionId,
									response.getFolderSnapshot(), info, source);
							bpmWorkcaseService.attach(bpmSessionId,
									new BPMInternalId(request.getTaskItem().getIdDossier()), createdLocator);

							folderSnapshot = response.getFolderSnapshot();
						} else {
							final DMLocator createdLocator = bpmDocumentService.createDocument(bpmSessionId,
									folderSnapshot, info, source);
							bpmWorkcaseService.attach(bpmSessionId,
									new BPMInternalId(request.getTaskItem().getIdDossier()), createdLocator);
						}
					}
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			if (request.getSession().getBpmService() == null || request.getSession().getBpmSessionId() == null) {
				try {
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
		return response;
	}

	/**
	 * Chech task state (lock or unlock) for user
	 *
	 * @param request
	 * @return
	 */
	public static TaskResponse checkTaskStatus(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				response.setLocked(false);
				BPMTaskSnapshot task = bpmService.getTaskService().getTask(bpmSessionId,
						new BPMInternalId(request.getTaskItem().getIdTache()));

				if (task.getState().equals(BPMState.OFFERED) || task.getState().equals(BPMState.RUNNING)
						|| task.getState().equals(BPMState.SAVED)) {
					if (task.getState().equals(BPMState.OFFERED)) {
						if (bpmSessionId != null) {
							final BPMTaskService bpmTaskService = bpmService.getTaskService();
							bpmTaskService.lock(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdTache()));
						}
					}
					response.setLocked(true);
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;

	}

	/**
	 * Lock task
	 *
	 * @param request
	 * @return boolean
	 */
	public static TaskResponse lockTask(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				bpmTaskService.lock(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdTache()));
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * reassign task
	 *
	 * @param request
	 * @return boolean
	 */
	public static TaskResponse reassignTask(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession("w4adm", "w4adm");
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				bpmTaskService.reassignToActor(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdTache()),
						new BPMLogicalId(request.getTaskItem().getLoginActeur()), true);
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Unlock task
	 *
	 * @author ocit
	 * @param request
	 * @return boolean
	 */
	public static TaskResponse unlockTask(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				bpmTaskService.unlock(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdTache()));
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Submit task to another step
	 *
	 * @param request
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public static TaskResponse submitTask(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			if (request.getSession().getBpmService() == null || request.getSession().getBpmSessionId() == null) {
				hostSetting = Utility.getHostSetting(request.getProcess());
				if (hostSetting != null) {
					bpmService = BPMServiceFactory.getService(
							Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
					bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
							request.getSession().getPassword());
				}
			} else {
				bpmService = request.getSession().getBpmService();
				bpmSessionId = request.getSession().getBpmSessionId();
			}

			if (bpmSessionId != null) {
				if (StringUtils.blank(request.getTaskItem().getIdTache())) {
					final BPMTaskService bpmTaskService = bpmService.getTaskService();
					final BPMWorkcaseService bpmWorkcaseService = bpmService.getWorkcaseService();
					BPMVariableMap variables = BPMVariables.createVariableMap();

					for (Item item : request.getListItem()) {
						if (item.getValeur() instanceof String) {
							variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.STRING,
									(String) item.getValeur()));
						}

						if (item.getValeur() instanceof Integer) {
							variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.INTEGER,
									(int) item.getValeur()));
						}

						if (item.getValeur() instanceof Date) {
							variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.DATE,
									(Date) item.getValeur()));
						}

						if (item.getValeur() instanceof List<?>) {
							List<?> list = (List<?>) item.getValeur();
							if (list.size() > 0) {
								if (list.get(0) instanceof String) {
									variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.STRING_LIST,
											(List<String>) item.getValeur()));
								}
								if (list.get(0) instanceof Integer) {
									variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.INTEGER_LIST,
											(List<Integer>) item.getValeur()));
								}
								if (list.get(0) instanceof Date) {
									variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.DATE_LIST,
											(List<Date>) item.getValeur()));
								}
							}
						}
					}

					if (StringUtils.blank(request.getCommentaire().getTitle())
							&& StringUtils.blank(request.getCommentaire().getContent())) {

						bpmWorkcaseService.attach(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdDossier()),
								BPMComments.createComment(request.getCommentaire().getTitle(),
										request.getCommentaire().getContent()));
					}
					bpmTaskService.submit(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdTache()),
							variables);
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			if (request.getSession().getBpmService() == null || request.getSession().getBpmSessionId() == null) {
				try {
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
		return response;
	}

	/**
	 * Save task to work
	 *
	 * @param request
	 * @return boolean
	 */
	@SuppressWarnings("unchecked")
	public static TaskResponse saveTask(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				if (StringUtils.blank(request.getTaskItem().getIdTache())) {
					final BPMTaskService bpmTaskService = bpmService.getTaskService();
					final BPMWorkcaseService bpmWorkcaseService = bpmService.getWorkcaseService();
					final BPMVariableMap variables = BPMVariables.createVariableMap();

					for (Item item : request.getListItem()) {
						if (item.getValeur() instanceof String) {
							variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.STRING,
									(String) item.getValeur()));
						}

						if (item.getValeur() instanceof Integer) {
							variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.INTEGER,
									(int) item.getValeur()));
						}

						if (item.getValeur() instanceof Date) {
							variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.DATE,
									(Date) item.getValeur()));
						}

						if (item.getValeur() instanceof List<?>) {
							List<?> list = (List<?>) item.getValeur();
							if (list.size() > 0) {
								if (list.get(0) instanceof String) {
									variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.STRING_LIST,
											(List<String>) item.getValeur()));
								}
								if (list.get(0) instanceof Integer) {
									variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.INTEGER_LIST,
											(List<Integer>) item.getValeur()));
								}
								if (list.get(0) instanceof Date) {
									variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.DATE_LIST,
											(List<Date>) item.getValeur()));
								}
							}
						}
					}

					if (StringUtils.blank(request.getCommentaire().getContent())
							&& StringUtils.blank(request.getCommentaire().getTitle())) {
						try {
							bpmWorkcaseService.attach(bpmSessionId,
									new BPMInternalId(request.getTaskItem().getIdDossier()),
									BPMComments.createComment(request.getCommentaire().getTitle(),
											request.getCommentaire().getContent()));
						} catch (Exception ex) {
							response.setHasError(true);
							response.setMessage(ex.getMessage());
							logger.warn("EXCEPTION : " + ex.getMessage());
						}
					}
					bpmTaskService.save(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdTache()), variables);
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	public static TaskResponse searchWorkcase(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMWorkcaseService bpmWorkcaseService = bpmService.getWorkcaseService();
				final BPMWorkcaseFilter bpmWorkcaseFilter = bpmWorkcaseService.createWorkcaseFilter();
				bpmWorkcaseFilter.processNameLike(request.getProcess());
				bpmWorkcaseFilter.attachAllWorkcaseVariables();
				bpmWorkcaseFilter.attachInitiatorSnapshot();

				// ID Dossier
				if (StringUtils.blank(request.getTaskItem().getIdDossier())) {
					bpmWorkcaseFilter.workcaseIs(new BPMInternalId(request.getTaskItem().getIdDossier()));
				}

				// Initiator
				if (StringUtils.blank(request.getTaskItem().getActeur())) {
					bpmWorkcaseFilter.initiatorIs(new BPMLogicalId(request.getTaskItem().getActeur()));
				}

				// Date Création Workcase
				if (StringUtils.blank(request.getTaskItem().getDateCreateDossier())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateCreateDossier())) {
						Date date = DateUtils.strToDate(request.getTaskItem().getDateCreateDossier());
						bpmWorkcaseFilter.createdBetween(date, date);
					}
				}

				// Période search Workcase
				if (StringUtils.blank(request.getTaskItem().getDateDebut())
						&& StringUtils.blank(request.getTaskItem().getDateFin())) {
					if (DateUtils.validFormatDate(request.getTaskItem().getDateDebut())
							&& DateUtils.validFormatDate(request.getTaskItem().getDateFin())) {
						Date dateDebut = DateUtils.strToDate(request.getTaskItem().getDateDebut());
						Date dateFin = DateUtils.strToDate(request.getTaskItem().getDateFin());
						bpmWorkcaseFilter.createdBetween(dateDebut, dateFin);
					}
				}

				// Search values variables
				for (String key : request.getTaskItem().getVariables().keySet()) {
					Object value = request.getTaskItem().getVariables().get(key);
					if (StringUtils.blank((String) value)) {
						bpmWorkcaseFilter.containsVariable(key, BPMFilterOperator.EQUAL, value);
					}
				}

				bpmWorkcaseFilter.maxSizeIs(request.getLastIndex());

				List<BPMTaskSort> sortList = new ArrayList<>();
				if (request.getOrderBy().equals(Global.ASC)) {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.ASC));
				} else {
					sortList.add(bpmService.getTaskService().createTaskSort(BPMTaskSortBy.WORKCASE, BPMSortMode.DESC));
				}

				if (StringUtils.blank(request.getTaskItem().getEtatWorkcase())) {
					if (request.getTaskItem().getEtatWorkcase().equalsIgnoreCase(Global.W_STATE_RUN_CODE)) {
						bpmWorkcaseFilter.stateIs(BPMFilterOperator.EQUAL, BPMState.RUNNING);
					}
					if (request.getTaskItem().getEtatWorkcase().equalsIgnoreCase(Global.W_STATE_DONE_CODE)) {
						bpmWorkcaseFilter.stateIs(BPMFilterOperator.EQUAL, BPMState.DONE);
					}
					if (request.getTaskItem().getEtatWorkcase().equalsIgnoreCase(Global.W_STATE_CAN_CODE)) {
						bpmWorkcaseFilter.stateIs(BPMFilterOperator.EQUAL, BPMState.CANCELLED);
					}
				}

				List<BPMWorkcaseSnapshot> result = bpmWorkcaseService.searchWorkcases(bpmSessionId, bpmWorkcaseFilter,
						null);

				for (BPMWorkcaseSnapshot o : result) {

					TaskItem wc = new TaskItem();
					BPMVariableMap wcVariables = o.getAttachedWorkcaseVariables();

					wc.setVariables(Utility.setTaskVariable(wcVariables));
					wc.setIdDossier(o.getId().getInternalId());
					wc.setEtatWorkcase(Utility.translateState(o.getState().name()));
					wc.setDateCreateDossier(DateUtils.dateToStringLong(o.getCreationDate()));
					wc.setDateEndDossier(DateUtils.dateToStringLong(o.getEndDate()));

					final BPMActorSnapshot bpmActorSnapshot = o.getAttachedInitiatorSnapshot();
					String fullName = (bpmActorSnapshot.getLastName() == null)
							? (bpmActorSnapshot.getFirstName() == null) ? null : bpmActorSnapshot.getFirstName()
							: (bpmActorSnapshot.getFirstName() == null) ? bpmActorSnapshot.getLastName()
									: bpmActorSnapshot.getFirstName() + Global.__SPACE + bpmActorSnapshot.getLastName();
					wc.setInitiateur(fullName);

					response.getListTaskItem().add(wc);
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Get all task variables
	 *
	 * @param request
	 * @return TaskResponse
	 */
	public static TaskResponse getTaskVariables(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskAttachment bpmTaskAttachment = bpmTaskService.createTaskAttachment();
				bpmTaskAttachment.attachAllTaskVariables();

				final BPMTaskSnapshot bpmTaskSnapshot = bpmTaskService.getTask(bpmSessionId,
						new BPMInternalId(request.getTaskItem().getIdTache()), bpmTaskAttachment);

				final BPMVariableMap taskVariables = bpmTaskSnapshot.getAttachedTaskVariables();
				final Set<String> variablesName = taskVariables.keySet();

				for (final String variableName : variablesName) {
					response.getListItem().add(new Item(variableName, taskVariables.get(variableName).getValue()));
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Get all task variables
	 *
	 * @param request
	 * @return TaskResponse
	 */
	public static TaskResponse getWorkcaseVariables(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMWorkcaseService bpmWorkcaseService = bpmService.getWorkcaseService();
				final BPMWorkcaseAttachment bpmWorkcaseAttachment = bpmWorkcaseService.createWorkcaseAttachment();

				if (request.getListItem().size() > 0) {
					for (final Item key : request.getListItem()) {
						bpmWorkcaseAttachment.attachWorkcaseVariable(key.getKey());
					}
				} else {
					bpmWorkcaseAttachment.attachAllWorkcaseVariables();
				}

				final BPMWorkcaseSnapshot bpmWorkcaseSnapshot = bpmWorkcaseService.getWorkcase(bpmSessionId,
						new BPMInternalId(request.getTaskItem().getIdDossier()), bpmWorkcaseAttachment);

				final BPMVariableMap taskVariables = bpmWorkcaseSnapshot.getAttachedWorkcaseVariables();
				final Set<String> variablesName = taskVariables.keySet();

				for (final String variableName : variablesName) {
					response.getListItem().add(new Item(variableName, taskVariables.get(variableName).getValue()));
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Get all comments attached to workcase
	 *
	 * @param request
	 * @return CommentaireResponse
	 */
	public static CommentaireResponse getWorkcaseCommentaires(CommentaireRequest request) {
		CommentaireResponse response = new CommentaireResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMWorkcaseService bpmWorkcaseService = bpmService.getWorkcaseService();
				List<BPMComment> result = bpmWorkcaseService.getComments(bpmSessionId,
						new BPMInternalId(request.getTaskItem().getIdDossier()));

				if (result != null) {
					for (BPMComment o : result) {
						final Commentaire cm = new Commentaire();
						cm.setTitle(o.getTitle());
						cm.setContent(o.getContent());
						cm.setDateCreation(DateUtils.dateToStringLong(o.getCreationDate()));
						if (o.getAuthor() != null) {
							final BPMActorSnapshot bpmActor = bpmService.getActorService().getActor(bpmSessionId,
									o.getAuthor());
							String fullName = (bpmActor.getLastName() == null)
									? (bpmActor.getFirstName() == null) ? null : bpmActor.getFirstName()
									: (bpmActor.getFirstName() == null) ? bpmActor.getLastName()
											: bpmActor.getFirstName() + Global.__SPACE + bpmActor.getLastName();
							cm.setActeur(fullName);
						}
						response.getCommentaires().add(cm);
					}
					response.getCommentaires().sort(Comparator.comparing(Commentaire::getDateCreation).reversed());
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Get all documents attached to workcase
	 *
	 * @param request
	 * @return DocumentResponse
	 */
	public static DocumentResponse getWorkcaseDocuments(DocumentRequest request) {
		DocumentResponse response = new DocumentResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMWorkcaseService bpmWorkcaseService = bpmService.getWorkcaseService();
				List<DMLocator> result = bpmWorkcaseService.getDocuments(bpmSessionId,
						new BPMInternalId(request.getTaskItem().getIdDossier()));
				final DMDocumentService bpmDocumentService = bpmService.getDocumentService();

				if (result != null) {
					for (DMLocator o : result) {
						final Document dc = new Document();
						DMDocumentSnapshot doc = bpmDocumentService.getDocument(bpmSessionId, o);
						DMDocumentContent content = bpmDocumentService.getContent(bpmSessionId, doc);
						String mimeType = (String) doc.getMetaData().get("cmis:contentStreamMimeType");
						String base64 = Utility.convertInputStreamToFile(content.getContentPart(0).getInputStream(),
								doc.getName());
						dc.setData(base64);
						dc.setType(mimeType);
						dc.setNativeObjectId(o.getNativeObjectId());
						dc.setFileName(doc.getName());
						response.getDocuments().add(dc);
					}
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Create workcase to start process
	 *
	 * @param request
	 * @return Item
	 */
	@SuppressWarnings("unchecked")
	public static TaskResponse createWorkcase(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			if (request.getSession().getBpmService() == null || request.getSession().getBpmSessionId() == null) {
				hostSetting = Utility.getHostSetting(request.getProcess());
				if (hostSetting != null) {
					bpmService = BPMServiceFactory.getService(
							Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
					bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
							request.getSession().getPassword());
				}
			} else {
				bpmService = request.getSession().getBpmService();
				bpmSessionId = request.getSession().getBpmSessionId();
			}

			if (bpmSessionId != null) {
				final BPMProcessService bpmProcessService = bpmService.getProcessService();
				final BPMWorkcaseService bpmWorkcaseService = bpmService.getWorkcaseService();

				final BPMWorkcaseSnapshot bpmWorkcaseSnapshot = bpmProcessService.createWorkcase(bpmSessionId,
						new BPMLogicalId(request.getProcess()));

				BPMVariableMap variables = BPMVariables.createVariableMap();
				for (Item item : request.getListItem()) {
					if (item.getValeur() instanceof String) {
						variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.STRING,
								(String) item.getValeur()));
					}

					if (item.getValeur() instanceof Integer) {
						variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.INTEGER,
								(int) item.getValeur()));
					}

					if (item.getValeur() instanceof Date) {
						variables.put(
								BPMVariables.createVariable(item.getKey(), BPMDataType.DATE, (Date) item.getValeur()));
					}

					if (item.getValeur() instanceof List<?>) {
						List<?> list = (List<?>) item.getValeur();
						if (list.size() > 0) {
							if (list.get(0) instanceof String) {
								variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.STRING_LIST,
										(List<String>) item.getValeur()));
							}
							if (list.get(0) instanceof Integer) {
								variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.INTEGER_LIST,
										(List<Integer>) item.getValeur()));
							}
							if (list.get(0) instanceof Date) {
								variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.DATE_LIST,
										(List<Date>) item.getValeur()));
							}
						}
					}
				}

				final List<BPMTaskSnapshot> bpmTaskSnapshotList = bpmWorkcaseService.startGetNew(bpmSessionId,
						bpmWorkcaseSnapshot.getId(), variables);

				if (request.getCommentaire() != null) {
					bpmWorkcaseService.attach(bpmSessionId, bpmWorkcaseSnapshot.getId(), BPMComments
							.createComment(request.getCommentaire().getTitle(), request.getCommentaire().getContent()));
				}

				response.getItem().setKey(bpmWorkcaseSnapshot.getId().getInternalId());
				if (!bpmTaskSnapshotList.isEmpty()) {
					response.getItem().setValeur(bpmTaskSnapshotList.get(0).getId().getInternalId());
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			if (request.getSession().getBpmService() == null || request.getSession().getBpmSessionId() == null) {
				try {
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
		return response;
	}

	/**
	 * Modify Variable
	 *
	 * @param request
	 * @return Item
	 */
	@SuppressWarnings("unchecked")
	public static TaskResponse modifyVariable(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			if (request.getSession().getBpmService() == null || request.getSession().getBpmSessionId() == null) {
				hostSetting = Utility.getHostSetting(request.getProcess());
				if (hostSetting != null) {
					bpmService = BPMServiceFactory.getService(
							Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
					bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
							request.getSession().getPassword());
				}
			} else {
				bpmService = request.getSession().getBpmService();
				bpmSessionId = request.getSession().getBpmSessionId();
			}

			if (bpmSessionId != null) {
				final BPMWorkcaseService bpmWorkcaseService = bpmService.getWorkcaseService();
				final BPMTaskService bpmTaskService = bpmService.getTaskService();

				final BPMState state = bpmTaskService.getState(bpmSessionId,
						new BPMInternalId(request.getTaskItem().getIdTache()));

				BPMVariableMap variables = BPMVariables.createVariableMap();
				for (Item item : request.getListItem()) {
					if (item.getValeur() instanceof String) {
						variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.STRING,
								(String) item.getValeur()));
					}

					if (item.getValeur() instanceof Integer) {
						variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.INTEGER,
								(int) item.getValeur()));
					}

					if (item.getValeur() instanceof Date) {
						variables.put(
								BPMVariables.createVariable(item.getKey(), BPMDataType.DATE, (Date) item.getValeur()));
					}

					if (item.getValeur() instanceof List<?>) {
						List<?> list = (List<?>) item.getValeur();
						if (list.size() > 0) {
							if (list.get(0) instanceof String) {
								variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.STRING_LIST,
										(List<String>) item.getValeur()));
							}
							if (list.get(0) instanceof Integer) {
								variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.INTEGER_LIST,
										(List<Integer>) item.getValeur()));
							}
							if (list.get(0) instanceof Date) {
								variables.put(BPMVariables.createVariable(item.getKey(), BPMDataType.DATE_LIST,
										(List<Date>) item.getValeur()));
							}
						}
					}
				}

				bpmTaskService.save(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdTache()), variables);

				if (state == BPMState.OFFERED) {
					bpmTaskService.unlock(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdTache()));
				}

				if (state == BPMState.RUNNING) {
					bpmTaskService.lock(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdTache()));
				}

				if (StringUtils.blank(request.getCommentaire().getTitle())
						&& StringUtils.blank(request.getCommentaire().getContent())) {

					bpmWorkcaseService.attach(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdDossier()),
							BPMComments.createComment(request.getCommentaire().getTitle(),
									request.getCommentaire().getContent()));
				}

				bpmWorkcaseService.save(bpmSessionId, new BPMInternalId(request.getTaskItem().getIdDossier()),
						variables);
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			if (request.getSession().getBpmService() == null || request.getSession().getBpmSessionId() == null) {
				try {
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
		return response;
	}

	/**
	 * Create user BPMSessionID to call engine operations
	 *
	 * @param request
	 * @return SessionActeur
	 * @throws eu.w4.bpm.BPMException
	 */
	public static ActorResponse Login(ActorRequest request) {
		ActorResponse response = new ActorResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getActor().getLogin(),
						request.getActor().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMActorSnapshot bpmActor = bpmService.getActorService().getActor(bpmSessionId,
						new BPMLogicalId(bpmSessionId.getUserName()));
				final BPMRoleService bpmRoleService = bpmService.getRoleService();

				if (request.getAccessFct() != null) {
					for (final String unRole : request.getAccessFct()) {
						boolean isActor = bpmRoleService.isAssignedTo(bpmSessionId,
								new BPMLogicalId(request.getActor().getLogin()), new BPMLogicalId(unRole));

						response.getAccessFct().put(unRole, isActor);
					}
				}
				String fullNameActor = (bpmActor.getLastName() == null)
						? (bpmActor.getFirstName() == null) ? null : bpmActor.getFirstName()
						: (bpmActor.getFirstName() == null) ? bpmActor.getLastName()
								: bpmActor.getFirstName() + Global.__SPACE + bpmActor.getLastName();
				response.getActor().setNom(fullNameActor);
				response.getActor().setLogin(bpmActor.getAuthenticationName());
			}
		} catch (BPMInvalidSessionException ex) {
			response.setCodeError(ex.getNativeErrorCode());
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Create user BPMSessionID to call engine operations
	 *
	 * @param request
	 * @return SessionActeur
	 * @throws eu.w4.bpm.BPMException
	 */
	public static ActorResponse LoginProd(ActorRequest request) {
		ActorResponse response = new ActorResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;
		Map<String, String> credentials = new HashMap<>();

		try {
			credentials = Utility.uncryptCredentials(UDecoder.URLDecode(request.getCreadentials(), "UTF-8"));
			request.getActor().setLogin(credentials.get("login"));
			request.getActor().setPassword(credentials.get("password"));
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getActor().getLogin(),
						request.getActor().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMActorSnapshot bpmActor = bpmService.getActorService().getActor(bpmSessionId,
						new BPMLogicalId(bpmSessionId.getUserName()));
				final BPMRoleService bpmRoleService = bpmService.getRoleService();

				if (request.getAccessFct() != null) {
					for (final String unRole : request.getAccessFct()) {
						boolean isActor = bpmRoleService.isAssignedTo(bpmSessionId,
								new BPMLogicalId(request.getActor().getLogin()), new BPMLogicalId(unRole));

						response.getAccessFct().put(unRole, isActor);
					}
				}
				String fullNameActor = (bpmActor.getLastName() == null)
						? (bpmActor.getFirstName() == null) ? null : bpmActor.getFirstName()
						: (bpmActor.getFirstName() == null) ? bpmActor.getLastName()
								: bpmActor.getFirstName() + Global.__SPACE + bpmActor.getLastName();
				response.getActor().setNom(fullNameActor);
				response.getActor().setLogin(bpmActor.getAuthenticationName());
				response.getActor().setPassword(credentials.get("password"));
			}
		} catch (BPMInvalidSessionException ex) {
			response.setCodeError(ex.getNativeErrorCode());
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	/**
	 * Close user BPMSessionID
	 *
	 * @param request
	 * @return boolean {true if logout is correct}
	 */
	public static ActorResponse Logout(ActorRequest request) {
		ActorResponse response = new ActorResponse();

		try {
			if (request.getSession().getBpmSessionId() != null) {
				request.getSession().getBpmService().getSessionService()
						.closeSession(request.getSession().getBpmSessionId());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		}
		return response;
	}

	public static ActorResponse getListActorByRole(ActorRequest request) {
		ActorResponse response = new ActorResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMActorService bpmActorService = bpmService.getActorService();
				final BPMActorFilter bpmActorFilter = bpmActorService.createActorFilter();

				if (StringUtils.blank(request.getRole())) {
					bpmActorFilter.hasRole(new BPMLogicalId(request.getRole()));
				}

				List<BPMActorSnapshot> result = bpmActorService.searchActors(bpmSessionId, bpmActorFilter, null);
				for (BPMActorSnapshot o : result) {
					Actor actor = new Actor();
					String fullName = (o.getLastName() == null) ? (o.getFirstName() == null) ? null : o.getFirstName()
							: (o.getFirstName() == null) ? o.getLastName()
									: o.getFirstName() + Global.__SPACE + o.getLastName();
					actor.setId(o.getId().getInternalId());
					actor.setLogin(o.getAuthenticationName());
					actor.setNom(fullName);
					actor.setEmail(o.getEmailAddress());
					response.getListActor().add(actor);
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	public static TaskResponse getCountMyTaskLock(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskFilter bpmTaskFilter = bpmTaskService.createTaskFilter();

				bpmTaskFilter.processIn(request.getProcess());
				bpmTaskFilter.assigneeIs(new BPMLogicalId(bpmSessionId.getUserName()), false);

				// Etat task
				bpmTaskFilter.stateIsAlive();

				List<BPMTaskSnapshot> bpmRaskSnapshot = bpmTaskService.searchTasks(bpmSessionId, bpmTaskFilter, null);
				response.setCount(bpmRaskSnapshot.size());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}

	public static TaskResponse getCountTaskOffer(TaskRequest request) {
		TaskResponse response = new TaskResponse();
		BPMService bpmService = null;
		BPMSessionId bpmSessionId = null;
		HostSetting hostSetting = null;

		try {
			hostSetting = Utility.getHostSetting(request.getProcess());
			if (hostSetting != null) {
				bpmService = BPMServiceFactory.getService(
						Utility.params(hostSetting.getHost(), hostSetting.getPort(), hostSetting.getMode()));
				bpmSessionId = bpmService.getSessionService().openSession(request.getSession().getLogin(),
						request.getSession().getPassword());
			}

			if (bpmSessionId != null) {
				final BPMTaskService bpmTaskService = bpmService.getTaskService();
				final BPMTaskFilter bpmTaskFilter = bpmTaskService.createTaskFilter();

				bpmTaskFilter.processIn(request.getProcess());
				bpmTaskFilter.inPoolOfActor(new BPMLogicalId(bpmSessionId.getUserName()));

				// Etat task
				bpmTaskFilter.stateIsAlive();

				DataTable dt = bpmTaskService.countTasks(bpmSessionId, bpmTaskFilter, null);
				if (dt.next()) {
					response.setCount((int) dt.getObject().getField(0));
				}
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage(ex.getMessage());
			logger.warn("EXCEPTION : " + ex.getMessage());
		} finally {
			try {
				if (bpmSessionId != null) {
					bpmService.getSessionService().closeSession(bpmSessionId);
				}
			} catch (Exception ex) {
				response.setHasError(true);
				response.setMessage(ex.getMessage());
				logger.warn("EXCEPTION : " + ex.getMessage());
			}
		}
		return response;
	}
}
