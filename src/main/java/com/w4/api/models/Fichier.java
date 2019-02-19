/**
 * 
 */
package com.w4.api.models;

import java.io.File;

/**
 * @author frederic
 *
 */
public class Fichier {

    private File file;
    private byte[] bytes;
	/**
	 * 
	 */
	public Fichier() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	/**
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}
	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
