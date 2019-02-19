/**
 * 
 */
package com.w4.api.models;

/**
 * @author frederic
 *
 */
public class Commentaire {
    private String title;
    private String dateCreation;
    private String content;
    private String acteur;
	/**
	 * 
	 */
	public Commentaire() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the dateCreation
	 */
	public String getDateCreation() {
		return dateCreation;
	}
	/**
	 * @param dateCreation the dateCreation to set
	 */
	public void setDateCreation(String dateCreation) {
		this.dateCreation = dateCreation;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the acteur
	 */
	public String getActeur() {
		return acteur;
	}
	/**
	 * @param acteur the acteur to set
	 */
	public void setActeur(String acteur) {
		this.acteur = acteur;
	}
}
