/**
 * 
 */
package com.w4.api.models;

/**
 * @author frederic
 *
 */
public class Document {
	private String type;
    private String fileName;
    private String nativeObjectId;
    private String data;
	/**
	 * 
	 */
	public Document() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the nativeObjectId
	 */
	public String getNativeObjectId() {
		return nativeObjectId;
	}
	/**
	 * @param nativeObjectId the nativeObjectId to set
	 */
	public void setNativeObjectId(String nativeObjectId) {
		this.nativeObjectId = nativeObjectId;
	}
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
}
