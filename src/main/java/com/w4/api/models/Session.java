/**
 * 
 */
package com.w4.api.models;

import eu.w4.bpm.service.BPMService;
import eu.w4.bpm.service.BPMSessionId;

/**
 * @author frederic
 *
 */
public class Session {
	private BPMService bpmService;
	private BPMSessionId bpmSessionId;
	private String login;
	private String password;
	private boolean status;
	private String loginTime;

	/**
	 * 
	 */
	public Session() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param login
	 * @param password
	 */
	public Session(String login, String password) {
		super();
		this.login = login;
		this.password = password;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login
	 *            the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @return the loginTime
	 */
	public String getLoginTime() {
		return loginTime;
	}

	/**
	 * @param loginTime
	 *            the loginTime to set
	 */
	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}

	/**
	 * @return the bpmService
	 */
	public BPMService getBpmService() {
		return bpmService;
	}

	/**
	 * @param bpmService
	 *            the bpmService to set
	 */
	public void setBpmService(BPMService bpmService) {
		this.bpmService = bpmService;
	}

	/**
	 * @return the bpmSessionId
	 */
	public BPMSessionId getBpmSessionId() {
		return bpmSessionId;
	}

	/**
	 * @param bpmSessionId
	 *            the bpmSessionId to set
	 */
	public void setBpmSessionId(BPMSessionId bpmSessionId) {
		this.bpmSessionId = bpmSessionId;
	}
}
