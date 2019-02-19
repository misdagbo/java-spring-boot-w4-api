/**
 * 
 */
package com.w4.api.contracts;

import javax.xml.bind.annotation.XmlRootElement;

import com.w4.api.infrastructures.RequestBase;
import com.w4.api.models.*;

/**
 * @author frederic
 *
 */
@XmlRootElement
public class CommentaireRequest extends RequestBase {

	private TaskItem taskItem;

	/**
	 * 
	 */
	public CommentaireRequest() {
		super();
		// TODO Auto-generated constructor stub
		taskItem = new TaskItem();
	}

	/**
	 * @return the taskItem
	 */
	public TaskItem getTaskItem() {
		return taskItem;
	}

	/**
	 * @param taskItem
	 *            the taskItem to set
	 */
	public void setTaskItem(TaskItem taskItem) {
		this.taskItem = taskItem;
	}
}
