/**
 * 
 */
package com.w4.api.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.w4.api.contracts.RoleRequest;
import com.w4.api.contracts.RoleResponse;
import com.w4.api.contracts.StepRequest;
import com.w4.api.contracts.StepResponse;
import com.w4.api.engine.CoreEngine;
import com.w4.api.infrastructures.Utility;

/**
 * @author frederic
 *
 */
public class CommonBusiness {

	private static final Logger logger = LoggerFactory.getLogger(CommonBusiness.class);

	public static RoleResponse getRole(RoleRequest request) {
		RoleResponse response = new RoleResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getRole(request);

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

	public static StepResponse getStep(StepRequest request) {
		StepResponse response = new StepResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getStep(request);

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
