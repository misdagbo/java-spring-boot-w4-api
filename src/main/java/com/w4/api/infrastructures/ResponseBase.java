/**
 * 
 */
package com.w4.api.infrastructures;

import com.w4.api.models.Session;

/**
 * @author frederic
 *
 */
public class ResponseBase {
	private String message;
	private boolean hasError;
	private boolean hasCustomError;
	private int codeError;
	private int count;
	private Session session;
	/**
	 * 
	 */
	public ResponseBase() {
		super();
		// TODO Auto-generated constructor stub
		session = new Session();
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the hasError
	 */
	public boolean getHasError() {
		return hasError;
	}
	/**
	 * @param hasError the hasError to set
	 */
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
	/**
	 * @return the hasCustomError
	 */
	public boolean getHasCustomError() {
		return hasCustomError;
	}
	/**
	 * @param hasCustomError the hasCustomError to set
	 */
	public void setHasCustomError(boolean hasCustomError) {
		this.hasCustomError = hasCustomError;
	}
	/**
	 * @return the codeError
	 */
	public int getCodeError() {
		return codeError;
	}
	/**
	 * @param codeError the codeError to set
	 */
	public void setCodeError(int codeError) {
		this.codeError = codeError;
	}
	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}
	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}
}
