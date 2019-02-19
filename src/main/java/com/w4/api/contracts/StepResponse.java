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
public class StepResponse extends ResponseBase {
	private List<String> listSteps;
	
	/**
	 * 
	 */
	public StepResponse() {
		super();
		// TODO Auto-generated constructor stub
		listSteps = new ArrayList<>();
	}

	/**
	 * @return the listSteps
	 */
	public List<String> getListSteps() {
		return listSteps;
	}

	/**
	 * @param listSteps the listSteps to set
	 */
	public void setListSteps(List<String> listSteps) {
		this.listSteps = listSteps;
	}
}
