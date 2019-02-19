/**
 * 
 */
package com.w4.api.contracts;

import eu.w4.data.document.DMDocumentFolderSnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.w4.api.infrastructures.ResponseBase;
import com.w4.api.models.*;

/**
 * @author frederic
 *
 */
@XmlRootElement
public class DocumentResponse extends ResponseBase {

	private List<Document> documents;
	private DMDocumentFolderSnapshot folderSnapshot;
	/**
	 * 
	 */
	public DocumentResponse() {
		super();
		// TODO Auto-generated constructor stub
		documents = new ArrayList<>();
	}
	/**
	 * @return the documents
	 */
	public List<Document> getDocuments() {
		return documents;
	}
	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
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
