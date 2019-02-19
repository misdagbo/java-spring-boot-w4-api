/**
 * 
 */
package com.w4.api.contracts;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.w4.api.infrastructures.RequestBase;
import com.w4.api.models.*;

import eu.w4.data.document.DMDocumentFolderSnapshot;
import eu.w4.data.document.service.DMDocumentService;

/**
 * @author frederic
 *
 */
@XmlRootElement
public class DocumentRequest extends RequestBase {

	private TaskItem taskItem;
	private List<Document> documents;
	private DMDocumentService documentService;
	private String source;
	private String path;
	private DMDocumentFolderSnapshot folderSnapshot;

	/**
	 * 
	 */
	public DocumentRequest() {
		super();
		// TODO Auto-generated constructor stub
		taskItem = new TaskItem();
		documents = new ArrayList<>();
	}

	/**
	 * @param session
	 * @param documentService
	 * @param source
	 * @param path
	 */
	public DocumentRequest(DMDocumentService documentService, String source, String path) {
		super();
		this.documentService = documentService;
		this.source = source;
		this.path = path;
	}

	/**
	 * @return the item
	 */
	public TaskItem getItem() {
		return taskItem;
	}

	/**
	 * @param item
	 *            the item to set
	 */
	public void setItem(TaskItem item) {
		this.taskItem = item;
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
	 * @return the documentService
	 */
	public DMDocumentService getDocumentService() {
		return documentService;
	}

	/**
	 * @param documentService
	 *            the documentService to set
	 */
	public void setDocumentService(DMDocumentService documentService) {
		this.documentService = documentService;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path
	 *            the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public TaskItem getTaskItem() {
		return taskItem;
	}

	public void setTaskItem(TaskItem taskItem) {
		this.taskItem = taskItem;
	}

	/**
	 * @return the folderSnapshot
	 */
	public DMDocumentFolderSnapshot getFolderSnapshot() {
		return folderSnapshot;
	}

	/**
	 * @param folderSnapshot the folderSnapshot to set
	 */
	public void setFolderSnapshot(DMDocumentFolderSnapshot folderSnapshot) {
		this.folderSnapshot = folderSnapshot;
	}
}
