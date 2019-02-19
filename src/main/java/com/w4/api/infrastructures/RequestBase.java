/**
 * 
 */
package com.w4.api.infrastructures;

import com.w4.api.models.Session;

/**
 * @author frederic
 *
 */
public class RequestBase {
	private int internalId;
	private int startIndex;
	private int lastIndex;
	private boolean takeAll;
	private String orderBy;
	private String operatorToUse;
	private String host;
	private String port;
	private String mode;
	private String process;
	private Session session;
	/**
	 * 
	 */
	public RequestBase() {
		super();
		// TODO Auto-generated constructor stub
		session = new Session();
	}
	/**
	 * @return the internalId
	 */
	public int getInternalId() {
		return internalId;
	}
	/**
	 * @param internalId the internalId to set
	 */
	public void setInternalId(int internalId) {
		this.internalId = internalId;
	}
	/**
	 * @return the startIndex
	 */
	public int getStartIndex() {
		return startIndex;
	}
	/**
	 * @param startIndex the startIndex to set
	 */
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	/**
	 * @return the lastIndex
	 */
	public int getLastIndex() {
		return lastIndex;
	}
	/**
	 * @param lastIndex the lastIndex to set
	 */
	public void setLastIndex(int lastIndex) {
		this.lastIndex = lastIndex;
	}
	/**
	 * @return the takeAll
	 */
	public boolean getTakeAll() {
		return takeAll;
	}
	/**
	 * @param takeAll the takeAll to set
	 */
	public void setTakeAll(boolean takeAll) {
		this.takeAll = takeAll;
	}
	/**
	 * @return the orderBy
	 */
	public String getOrderBy() {
		return orderBy;
	}
	/**
	 * @param orderBy the orderBy to set
	 */
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	/**
	 * @return the operatorToUse
	 */
	public String getOperatorToUse() {
		return operatorToUse;
	}
	/**
	 * @param operatorToUse the operatorToUse to set
	 */
	public void setOperatorToUse(String operatorToUse) {
		this.operatorToUse = operatorToUse;
	}
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}
	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	/**
	 * @return the process
	 */
	public String getProcess() {
		return process;
	}
	/**
	 * @param process the process to set
	 */
	public void setProcess(String process) {
		this.process = process;
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
