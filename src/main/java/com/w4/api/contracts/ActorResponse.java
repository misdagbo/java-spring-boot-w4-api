/**
 * 
 */
package com.w4.api.contracts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.w4.api.infrastructures.*;
import com.w4.api.models.Actor;

/**
 * @author frederic
 *
 */
@XmlRootElement
public class ActorResponse extends ResponseBase {
	private Actor actor;
	private List<Actor> listActor;
	private Map<String, Boolean> accessFct;

	/**
	 * 
	 */
	public ActorResponse() {
		super();
		// TODO Auto-generated constructor stub
		actor = new Actor();
		listActor = new ArrayList<>();
		accessFct = new HashMap<>();
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
	 * @return the listActor
	 */
	public List<Actor> getListActor() {
		return listActor;
	}

	/**
	 * @param listActor
	 *            the listActor to set
	 */
	public void setListActor(List<Actor> listActor) {
		this.listActor = listActor;
	}

	public Map<String, Boolean> getAccessFct() {
		return accessFct;
	}

	public void setAccessFct(Map<String, Boolean> accessFct) {
		this.accessFct = accessFct;
	}
}
