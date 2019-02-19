/**
 * 
 */
package com.w4.api.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.w4.api.infrastructures.*;

/**
 * @author frederic
 *
 */
@XmlRootElement
public class RoleResponse extends ResponseBase {
	private List<String> listRoles;
	
	/**
	 * 
	 */
	public RoleResponse() {
		super();
		// TODO Auto-generated constructor stub
		listRoles = new ArrayList<>();
	}

	/**
	 * @return the listRoles
	 */
	public List<String> getListRoles() {
		return listRoles;
	}

	/**
	 * @param listRoles the listRoles to set
	 */
	public void setListRoles(List<String> listRoles) {
		this.listRoles = listRoles;
	}
}
