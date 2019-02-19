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
public class DocumentBusiness {

	private static final Logger logger = LoggerFactory.getLogger(DocumentBusiness.class);

	public static DocumentResponse getDocumentDossier(DocumentRequest request) {
		DocumentResponse response = new DocumentResponse();

		/* Logical Code */
		if (request != null) {
			try {
				String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
				request.getSession().setPassword(unCryptPassword);

				response = CoreEngine.getWorkcaseDocuments(request);

				if (response.getHasError()) {
					throw new Exception(response.getMessage());
				}
			} catch (Exception e) {
				response.setHasError(true);
				response.setMessage(e.getMessage());
				logger.warn("EXCEPTION " + e.getMessage());
			}
		}
		/* End Logical Code */

		return response;
	}

	public static DocumentResponse attachFile(TaskRequest request) {
		DocumentResponse response = new DocumentResponse();

		/* Logical Code */
		try {
			String unCryptPassword = Utility.decrypt(request.getSession().getPassword());
			request.getSession().setPassword(unCryptPassword);

			DocumentResponse responseDocument = CoreEngine.attacheFile(request);

			if (responseDocument.getHasError()) {
				throw new Exception(responseDocument.getMessage());
			}
		} catch (Exception e) {
			response.setHasError(true);
			response.setMessage(e.getMessage());
			logger.warn("EXCEPTION : " + e.getMessage());
		}
		/* End Logical Code */

		return response;
	}
}
