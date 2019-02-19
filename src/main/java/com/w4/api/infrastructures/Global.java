/**
 * 
 */
package com.w4.api.infrastructures;

/**
 * @author frederic
 *
 */
public class Global {
	
	public static final String ALGORITHM_AES = "AES";
    public static final String ALGORITHM_AES_ECB_NOPADDING = "AES/ECB/NoPadding";
    public static final String ALGORITHM_AES_ECB_PKCS5 = "AES/ECB/PKCS5Padding";
    public static final String ALGORITHM_AES_ECB_PKCS7 = "AES/ECB/PKCS7Padding";
    public static final String ALGORITHM_AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";
    public static final String ALGORITHM_AES_CBC_PKCS7 = "AES/CBC/PKCS7Padding";
    public static final String BIT = "128"; 

	public static String WORKCASE = "workcase";
	public static String VARIABLE = "variable";
	public static Integer MAX_SIZE = 10;
	public static String ASTERIX = "*";
	public static String WORKCASE_NULL = "0";

	// Declare des separators
	public final static String __SEPARATOR_ = "_";
	public final static String __SEPARATOR_SLASH = "/";
	public final static String __SPACE = " ";
	public final static String _WF_SEPARATOR = "====";

	// Declare workcase status code for search
	public static String W_STATE_RUN_CODE = "4";
	public static String W_STATE_DONE_CODE = "501";
	public static String W_STATE_CAN_CODE = "502";
	public static String W_STATE_OFFER_CODE = "OFFER";
	public static String W_STATE_SAVE_CODE = "SAVE";

	// Code error W4
	public static int INVALID_AUTHENTIFICATION_CODE = 2101;

	// Order
	public static String ASC = "ASC";
	public static String DESC = "DESC";
}
