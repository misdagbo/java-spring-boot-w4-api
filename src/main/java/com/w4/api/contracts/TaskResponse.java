/**
 * 
 */
package com.w4.api.contracts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.w4.api.infrastructures.*;
import com.w4.api.models.*;

/**
 * @author frederic
 *
 */
@XmlRootElement
public class TaskResponse extends ResponseBase {
	private List<TaskItem> listTaskItem;
	private List<Item> listItem;
	private Map<String, List<TaskItem>> actorsGroup;
	private Item item;
	private boolean locked;

	/**
	 * 
	 */
	public TaskResponse() {
		super();
		// TODO Auto-generated constructor stub
		listTaskItem = new ArrayList<>();
		listItem = new ArrayList<>();
		item = new Item();
	}

	/**
	 * @return the listTaskItem
	 */
	public List<TaskItem> getListTaskItem() {
		return listTaskItem;
	}

	/**
	 * @param listTaskItem
	 *            the listTaskItem to set
	 */
	public void setListTaskItem(List<TaskItem> listTaskItem) {
		this.listTaskItem = listTaskItem;
	}

	/**
	 * @return the listItem
	 */
	public List<Item> getListItem() {
		return listItem;
	}

	/**
	 * @param listItem
	 *            the listItem to set
	 */
	public void setListItem(List<Item> listItem) {
		this.listItem = listItem;
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * @param item
	 *            the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked
	 *            the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * @return the actorsGroup
	 */
	public Map<String, List<TaskItem>> getActorsGroup() {
		return actorsGroup;
	}

	/**
	 * @param actorsGroup
	 *            the actorsGroup to set
	 */
	public void setActorsGroup(Map<String, List<TaskItem>> actorsGroup) {
		this.actorsGroup = actorsGroup;
	}
}
