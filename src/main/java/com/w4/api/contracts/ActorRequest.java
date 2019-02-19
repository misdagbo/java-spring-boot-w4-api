/**
 * 
 */
package com.w4.api.contracts;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.w4.api.infrastructures.RequestBase;
import com.w4.api.models.*;

/**
 * @author frederic
 *
 */
@XmlRootElement
public class ActorRequest extends RequestBase {
	private Actor actor;
	private String role;
	private List<String> accessFct;
	private String creadentials;

	/**
	 * 
	 */
	public ActorRequest() {
		super();
		// TODO Auto-generated constructor stub
		actor = new Actor();
	}

	/**
	 * @return the actor
	 */
	public Actor getActor() {
		return actor;
	}

	/**
	 * @param actor
	 *            the actor to set
	 */
	public void setActor(Actor actor) {
		this.actor = actor;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	public List<String> getAccessFct() {
		return accessFct;
	}

	public void setAccessFct(List<String> accessFct) {
		this.accessFct = accessFct;
	}

	public String getCreadentials() {
		return creadentials;
	}

	public void setCreadentials(String creadentials) {
		this.creadentials = creadentials;
	}
}
