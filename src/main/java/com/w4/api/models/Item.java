/**
 * 
 */
package com.w4.api.models;

/**
 * @author frederic
 *
 */
public class Item {
    private String key;
    private Object valeur;
	/**
	 * 
	 */
	public Item() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param key
	 * @param valeur
	 */
	public Item(String key, Object valeur) {
		super();
		this.key = key;
		this.valeur = valeur;
	}
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the valeur
	 */
	public Object getValeur() {
		return valeur;
	}
	/**
	 * @param valeur the valeur to set
	 */
	public void setValeur(Object valeur) {
		this.valeur = valeur;
	}
}
