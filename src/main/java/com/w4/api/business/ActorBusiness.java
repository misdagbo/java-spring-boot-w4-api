/**
 * 
 */
package com.w4.api.business;

import com.w4.api.contracts.*;
import com.w4.api.engine.CoreEngine;
import com.w4.api.infrastructures.Utility;
import com.w4.api.models.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author frederic
 *
 */
public class ActorBusiness {

	private static final Logger logger = LoggerFactory.getLogger(ActorBusiness.class);

	public static ActorResponse signInActor(ActorRequest request) {
		ActorResponse response = new ActorResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String cryptPassword = request.getActor().getPassword();
				String unCryptPassword = Utility.decrypt(cryptPassword);
				request.getActor().setPassword(unCryptPassword);

				response = CoreEngine.Login(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}

				response.setSession(new Session(request.getActor().getLogin(), cryptPassword));

			} catch (Exception e) {
				response.setHasError(true);
				response.setMessage(e.getMessage());
				logger.warn("EXCEPTION : " + e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static ActorResponse signInActorProd(ActorRequest request) {
		ActorResponse response = new ActorResponse();

		/* Logical Code */
		if (request != null) {
			try {
				response = CoreEngine.LoginProd(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}

				String password = Utility.encrypt(response.getActor().getPassword());
				response.setSession(new Session(response.getActor().getLogin(), password));
				response.getActor().setPassword(null);
			} catch (Exception e) {
				response.setHasError(true);
				response.setMessage(e.getMessage());
				logger.warn("EXCEPTION : " + e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static ActorResponse logoutActor(ActorRequest request) {
		ActorResponse response = new ActorResponse();

		/* Logical Code */
		if (request != null) {
			try {
				response = CoreEngine.Logout(request);

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

	public static ActorResponse getListActorByRole(ActorRequest request) {
		ActorResponse response = new ActorResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getListActorByRole(request);

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
}
