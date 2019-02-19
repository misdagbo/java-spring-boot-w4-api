package com.w4.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.w4.api.business.*;
import com.w4.api.contracts.*;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "/workflowBackend")
public class Service {

	private static final Logger logger = LoggerFactory.getLogger(Service.class);

	@RequestMapping(value = "/signInActor", method = RequestMethod.POST, produces = { "application/json" }, consumes = {
			"application/json" })
	public ActorResponse signInActor(@RequestBody ActorRequest request) {
		logger.info("...........................begin method signInActor............................");

		ActorResponse response = new ActorResponse();
		try {
			response = ActorBusiness.signInActor(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());

			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method signInActor............................");
		return response;
	}

	@RequestMapping(value = "/signInActorProd", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public ActorResponse signInActorProd(@RequestBody ActorRequest request) {
		logger.info("...........................begin method signInActorProd............................");

		ActorResponse response = new ActorResponse();
		try {
			response = ActorBusiness.signInActorProd(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());

			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method signInActorProd............................");
		return response;
	}

	@RequestMapping(value = "/logoutActor", method = RequestMethod.POST, produces = { "application/json" }, consumes = {
			"application/json" })
	public ActorResponse logoutActor(@RequestBody ActorRequest request) {
		logger.info("...........................begin method logoutActor............................");
		ActorResponse response = new ActorResponse();
		try {
			response = ActorBusiness.logoutActor(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());

			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method logoutActor............................");
		return response;
	}

	@RequestMapping(value = "/createWorkcase", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse createWorkcase(@RequestBody TaskRequest request) {
		logger.info("...........................begin method createWorkcase............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.createWorkcase(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method createWorkcase............................");
		return response;
	}

	@RequestMapping(value = "/getWorkcase", method = RequestMethod.POST, produces = { "application/json" }, consumes = {
			"application/json" })
	public TaskResponse getWorkcase(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getWorkcase............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getWorkcase(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getWorkcase............................");
		return response;
	}

	@RequestMapping(value = "/getCommentaireDossier", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public CommentaireResponse getCommentaireDossier(@RequestBody CommentaireRequest request) {
		logger.info("...........................begin method getCommentaireDossier............................");
		CommentaireResponse response = new CommentaireResponse();
		try {
			response = CommentaireBusiness.getCommentaireDossier(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getCommentaireDossier............................");
		return response;
	}

	@RequestMapping(value = "/getDocumentDossier", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public DocumentResponse getDocumentDossier(@RequestBody DocumentRequest request) {
		logger.info("...........................begin method getDocumentDossier............................");
		DocumentResponse response = new DocumentResponse();
		try {
			response = DocumentBusiness.getDocumentDossier(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getDocumentDossier............................");
		return response;
	}

	@RequestMapping(value = "/unlockTask", method = RequestMethod.POST, produces = { "application/json" }, consumes = {
			"application/json" })
	public TaskResponse unlockTask(@RequestBody TaskRequest request) {
		logger.info("...........................begin method unlockTask............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.unlockTask(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method unlockTask............................");
		return response;
	}

	@RequestMapping(value = "/checkTaskStatus", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse checkTaskStatus(@RequestBody TaskRequest request) {
		logger.info("...........................begin method checkTaskStatus............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.checkTaskStatus(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method checkTaskStatus............................");
		return response;
	}

	@RequestMapping(value = "/lockTask", method = RequestMethod.POST, produces = { "application/json" }, consumes = {
			"application/json" })
	public TaskResponse lockTask(@RequestBody TaskRequest request) {
		logger.info("...........................begin method lockTask............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.lockTask(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method lockTask............................");
		return response;
	}

	@RequestMapping(value = "/getTaskVariables", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getTaskVariables(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getTaskVariables............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getTaskVariables(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getTaskVariables............................");
		return response;
	}

	@RequestMapping(value = "/saveTask", method = RequestMethod.POST, produces = { "application/json" }, consumes = {
			"application/json" })
	public TaskResponse saveTask(@RequestBody TaskRequest request) {
		logger.info("...........................begin method saveTask............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.saveTask(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method saveTask............................");
		return response;
	}

	@RequestMapping(value = "/submitTask", method = RequestMethod.POST, produces = { "application/json" }, consumes = {
			"application/json" })
	public TaskResponse submitTask(@RequestBody TaskRequest request) {
		logger.info("...........................begin method submitTask............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.submitTask(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method submitTask............................");
		return response;
	}

	@RequestMapping(value = "/getWorkcaseDetails", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getWorkcaseDetails(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getWorkcaseDetails............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getWorkcaseDetails(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getWorkcaseDetails............................");
		return response;
	}

	@RequestMapping(value = "/getAlarmTaskItem", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getAlarmTaskItem(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getAlarmTaskItem............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getAlarmTaskItem(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getAlarmTaskItem............................");
		return response;
	}

	@RequestMapping(value = "/getWorkcaseVariables", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getWorkcaseVariables(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getWorkcaseVariables............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getWorkcaseVariables(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getWorkcaseVariables............................");
		return response;
	}

	@RequestMapping(value = "/searchWorkcase", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse searchWorkcase(@RequestBody TaskRequest request) {
		logger.info("...........................begin method searchWorkcase............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.searchWorkcase(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method searchWorkcase............................");
		return response;
	}

	@RequestMapping(value = "/getListActorByRole", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public ActorResponse getListActorByRole(@RequestBody ActorRequest request) {
		logger.info("...........................begin method getListActorByRole............................");
		ActorResponse response = new ActorResponse();
		try {
			response = ActorBusiness.getListActorByRole(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getListActorByRole............................");
		return response;
	}

	@RequestMapping(value = "/getTaskMyGoupLock", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getTaskMyGoupLock(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getTaskMyGoupLock............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getTaskMyGoupLock(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getTaskMyGoupLock............................");
		return response;
	}

	@RequestMapping(value = "/getMyTaskLock", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getMyTaskLock(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getMyTaskLock............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getMyTaskLock(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getMyTaskLock............................");
		return response;
	}

	@RequestMapping(value = "/getMyTaskLockByActor", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getMyTaskLockByActor(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getMyTaskLockByActor............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getMyTaskLockByActor(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getMyTaskLockByActor............................");
		return response;
	}

	@RequestMapping(value = "/getMyTaskOffer", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getMyTaskOffer(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getMyTaskOffer............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getMyTaskOffer(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getMyTaskOffer............................");
		return response;
	}

	@RequestMapping(value = "/attachFile", method = RequestMethod.POST, produces = { "application/json" }, consumes = {
			"application/json" })
	public DocumentResponse attachFile(@RequestBody TaskRequest request) {
		logger.info("...........................begin method attachFile............................");
		DocumentResponse response = new DocumentResponse();
		try {
			response = DocumentBusiness.attachFile(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method attachFile............................");
		return response;
	}

	@RequestMapping(value = "/reassignTask", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse reassignTask(@RequestBody TaskRequest request) {
		logger.info("...........................begin method reassignTask............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.reassignTask(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method reassignTask............................");
		return response;
	}

	@RequestMapping(value = "/getRole", method = RequestMethod.POST, produces = { "application/json" }, consumes = {
			"application/json" })
	public RoleResponse getRole(@RequestBody RoleRequest request) {
		logger.info("...........................begin method getRole............................");
		RoleResponse response = new RoleResponse();
		try {
			response = CommonBusiness.getRole(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getRole............................");
		return response;
	}

	@RequestMapping(value = "/getStep", method = RequestMethod.POST, produces = { "application/json" }, consumes = {
			"application/json" })
	public StepResponse getStep(@RequestBody StepRequest request) {
		logger.info("...........................begin method getStep............................");
		StepResponse response = new StepResponse();
		try {
			response = CommonBusiness.getStep(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getStep............................");
		return response;
	}

	@RequestMapping(value = "/getCountMyTaskLock", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getCountMyTaskLock(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getCountMyTaskLock............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getCountMyTaskLock(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getCountMyTaskLock............................");
		return response;
	}

	@RequestMapping(value = "/getCountTaskOffer", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getCountTaskOffer(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getCountTaskOffer............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getCountTaskOffer(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getCountTaskOffer............................");
		return response;
	}

	@RequestMapping(value = "/modifyVariable", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse modifyVariable(@RequestBody TaskRequest request) {
		logger.info("...........................begin method modifyVariable............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.modifyVariable(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method modifyVariable............................");
		return response;
	}

	@RequestMapping(value = "/getAlarmTaskSupervision", method = RequestMethod.POST, produces = {
			"application/json" }, consumes = { "application/json" })
	public TaskResponse getAlarmTaskSupervision(@RequestBody TaskRequest request) {
		logger.info("...........................begin method getAlarmTaskSupervision............................");
		TaskResponse response = new TaskResponse();
		try {
			response = TaskBusiness.getAlarmTaskSupervision(request);

			if (response.getHasError()) {
				throw new Exception(response.getMessage());
			}
		} catch (Exception ex) {
			response.setHasError(true);
			response.setMessage((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
			logger.warn((ex.getMessage() == null || ex.getMessage().isEmpty())
					? "La transaction a été annulée suite à une erreur survenue" : ex.getMessage());
		}
		logger.info("...........................end method getAlarmTaskSupervision............................");
		return response;
	}
}