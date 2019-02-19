/**
 * 
 */
package com.w4.api.models;

import java.util.HashMap;
import java.util.Map;

/**
 * @author frederic
 *
 */
public class TaskItem {
	private String idTache;
	private String idDossier;
	private String procedure;
	private String etape;
	private String initiateur;
	private String dateCreateDossier;
	private String responsable;
	private String dateEndDossier;
	private String dateCreateTask;
	private String dateEndTache;
	private String etat;
	private String etatWorkcase;
	private String fileActivity;
	private String acteur;
	private String loginActeur;
	private String dateDebut;
	private String dateFin;
	private String role;
	private String color;
	private Map<String, Object> variables;

	/**
	 * 
	 */
	public TaskItem() {
		super();
		// TODO Auto-generated constructor stub
		variables = new HashMap<String, Object>();
	}

	/**
	 * @return the idTache
	 */
	public String getIdTache() {
		return idTache;
	}

	/**
	 * @param idTache
	 *            the idTache to set
	 */
	public void setIdTache(String idTache) {
		this.idTache = idTache;
	}

	/**
	 * @return the idDossier
	 */
	public String getIdDossier() {
		return idDossier;
	}

	/**
	 * @param idDossier
	 *            the idDossier to set
	 */
	public void setIdDossier(String idDossier) {
		this.idDossier = idDossier;
	}

	/**
	 * @return the procedure
	 */
	public String getProcedure() {
		return procedure;
	}

	/**
	 * @param procedure
	 *            the procedure to set
	 */
	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	/**
	 * @return the etape
	 */
	public String getEtape() {
		return etape;
	}

	/**
	 * @param etape
	 *            the etape to set
	 */
	public void setEtape(String etape) {
		this.etape = etape;
	}

	/**
	 * @return the initiateur
	 */
	public String getInitiateur() {
		return initiateur;
	}

	/**
	 * @param initiateur
	 *            the initiateur to set
	 */
	public void setInitiateur(String initiateur) {
		this.initiateur = initiateur;
	}

	/**
	 * @return the dateCreateDossier
	 */
	public String getDateCreateDossier() {
		return dateCreateDossier;
	}

	/**
	 * @param dateCreateDossier
	 *            the dateCreateDossier to set
	 */
	public void setDateCreateDossier(String dateCreateDossier) {
		this.dateCreateDossier = dateCreateDossier;
	}

	/**
	 * @return the dateCreateTask
	 */
	public String getDateCreateTask() {
		return dateCreateTask;
	}

	/**
	 * @param dateCreateTask
	 *            the dateCreateTask to set
	 */
	public void setDateCreateTask(String dateCreateTask) {
		this.dateCreateTask = dateCreateTask;
	}

	/**
	 * @return the dateEndTache
	 */
	public String getDateEndTache() {
		return dateEndTache;
	}

	/**
	 * @param dateEndTache
	 *            the dateEndTache to set
	 */
	public void setDateEndTache(String dateEndTache) {
		this.dateEndTache = dateEndTache;
	}

	/**
	 * @return the etat
	 */
	public String getEtat() {
		return etat;
	}

	/**
	 * @param etat
	 *            the etat to set
	 */
	public void setEtat(String etat) {
		this.etat = etat;
	}

	/**
	 * @return the etatWorkcase
	 */
	public String getEtatWorkcase() {
		return etatWorkcase;
	}

	/**
	 * @param etatWorkcase
	 *            the etatWorkcase to set
	 */
	public void setEtatWorkcase(String etatWorkcase) {
		this.etatWorkcase = etatWorkcase;
	}

	/**
	 * @return the fileActivity
	 */
	public String getFileActivity() {
		return fileActivity;
	}

	/**
	 * @param fileActivity
	 *            the fileActivity to set
	 */
	public void setFileActivity(String fileActivity) {
		this.fileActivity = fileActivity;
	}

	/**
	 * @return the acteur
	 */
	public String getActeur() {
		return acteur;
	}

	/**
	 * @param acteur
	 *            the acteur to set
	 */
	public void setActeur(String acteur) {
		this.acteur = acteur;
	}

	/**
	 * @return the variables
	 */
	public Map<String, Object> getVariables() {
		return variables;
	}

	/**
	 * @param variables
	 *            the variables to set
	 */
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	/**
	 * @return the dateDebut
	 */
	public String getDateDebut() {
		return dateDebut;
	}

	/**
	 * @param dateDebut the dateDebut to set
	 */
	public void setDateDebut(String dateDebut) {
		this.dateDebut = dateDebut;
	}

	/**
	 * @return the dateFin
	 */
	public String getDateFin() {
		return dateFin;
	}

	/**
	 * @param dateFin the dateFin to set
	 */
	public void setDateFin(String dateFin) {
		this.dateFin = dateFin;
	}

	/**
	 * @return the loginActeur
	 */
	public String getLoginActeur() {
		return loginActeur;
	}

	/**
	 * @param loginActeur the loginActeur to set
	 */
	public void setLoginActeur(String loginActeur) {
		this.loginActeur = loginActeur;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * @return the dateEndDossier
	 */
	public String getDateEndDossier() {
		return dateEndDossier;
	}

	/**
	 * @param dateEndDossier the dateEndDossier to set
	 */
	public void setDateEndDossier(String dateEndDossier) {
		this.dateEndDossier = dateEndDossier;
	}

	/**
	 * @return the responsable
	 */
	public String getResponsable() {
		return responsable;
	}

	/**
	 * @param responsable the responsable to set
	 */
	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
}
