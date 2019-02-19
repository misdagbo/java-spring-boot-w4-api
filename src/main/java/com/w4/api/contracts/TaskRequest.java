/**
 * 
 */
package com.w4.api.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.w4.api.infrastructures.*;
import com.w4.api.models.*;

/**
 * @author frederic
 *
 */
@XmlRootElement
public class TaskRequest extends RequestBase {
	private Commentaire commentaire;
	private List<Document> documents;
	private List<Item> listItem;
	private TaskItem taskItem;
	private boolean nextStep;
	private int alarm;
	private int delai;
	private boolean reportCalled;
	private String urlReport;

	/**
	 * 
	 * 
	 */
	public TaskRequest() {
		super();
		// TODO Auto-generated constructor stub
		commentaire = new Commentaire();
		documents = new ArrayList<>();
		listItem = new ArrayList<>();
		taskItem = new TaskItem();
	}

	/**
	 * @return the commentaire
	 */
	public Commentaire getCommentaire() {
		return commentaire;
	}

	/**
	 * @param commentaire
	 *            the commentaire to set
	 */
	public void setCommentaire(Commentaire commentaire) {
		this.commentaire = commentaire;
	}

	/**
	 * @return the documents
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * @param documents
	 *            the documents to set
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
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

	/**
	 * @return the nextStep
	 */
	public boolean isNextStep() {
		return nextStep;
	}

	/**
	 * @param nextStep
	 *            the nextStep to set
	 */
	public void setNextStep(boolean nextStep) {
		this.nextStep = nextStep;
	}

	/**
	 * @return the alarm
	 */
	public int getAlarm() {
		return alarm;
	}

	/**
	 * @param alarm
	 *            the alarm to set
	 */
	public void setAlarm(int alarm) {
		this.alarm = alarm;
	}

	/**
	 * @return the delai
	 */
	public int getDelai() {
		return delai;
	}

	/**
	 * @param delai
	 *            the delai to set
	 */
	public void setDelai(int delai) {
		this.delai = delai;
	}

	/**
	 * @return the reportCalled
	 */
	public boolean isReportCalled() {
		return reportCalled;
	}

	/**
	 * @param reportCalled
	 *            the reportCalled to set
	 */
	public void setReportCalled(boolean reportCalled) {
		this.reportCalled = reportCalled;
	}

	/**
	 * @return the urlReport
	 */
	public String getUrlReport() {
		return urlReport;
	}

	/**
	 * @param urlReport
	 *            the urlReport to set
	 */
	public void setUrlReport(String urlReport) {
		this.urlReport = urlReport;
	}
}
