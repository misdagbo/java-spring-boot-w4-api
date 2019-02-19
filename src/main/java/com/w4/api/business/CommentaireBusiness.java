/**
 * 
 */
package com.w4.api.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.w4.api.contracts.*;
import com.w4.api.engine.CoreEngine;
import com.w4.api.infrastructures.Utility;

/**
 * @author frederic
 *
 */
public class CommentaireBusiness {

	private static final Logger logger = LoggerFactory.getLogger(CommentaireBusiness.class);

	public static CommentaireResponse getCommentaireDossier(CommentaireRequest request) {
		CommentaireResponse response = new CommentaireResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getWorkcaseCommentaires(request);

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
