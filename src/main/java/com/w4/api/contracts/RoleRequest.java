/**
 * 
 */
package com.w4.api.contracts;

import javax.xml.bind.annotation.XmlRootElement;

import com.w4.api.infrastructures.RequestBase;

/**
 * @author frederic
 *
 */
@XmlRootElement
public class RoleRequest extends RequestBase {
	private String libelle;
	
	/**
	 * 
	 */
	public RoleRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the libelle
	 */
	public String getLibelle() {
		return libelle;
	}

	/**
	 * @param libelle the libelle to set
	 */
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
}
