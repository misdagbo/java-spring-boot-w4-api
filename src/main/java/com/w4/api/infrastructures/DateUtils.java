/**
 * 
 */
package com.w4.api.infrastructures;

import java.text.*;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author frederic
 *
 */
public class DateUtils {

	/**
	 * Formatter la date en chaine de caracteres
	 *
	 * @param d
	 *            : String ex : dd/MM/yyyy
	 * @return
	 */
	
	private static final Logger logger = LogManager.getLogger("DateUtils");
	
	public static String dateToString(Date d) {

		if (d == null) {
			return null;
		} else {
			SimpleDateFormat std = new SimpleDateFormat("dd/MM/yyyy");
			return std.format(d);
		}

	}

	/**
	 * Formatter la date en chaine de caracteres
	 *
	 * @param d
	 *            : String ex : dd/MM/yyyy HH:mm:ss
	 * @return
	 */
	public static String dateToStringLong(Date d) {

		if (d == null) {
			return null;
		} else {
			SimpleDateFormat std = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			return std.format(d);
		}
	}

	/**
	 *
	 * @param s
	 * @return
	 */
	public static Date strToDate(String s) {
		if (blank(s)) {
			return null;
		} else {
			SimpleDateFormat std = new SimpleDateFormat("dd/MM/yyyy");
			try {
				return std.parse(s);
			} catch (ParseException e) {
				logger.warn("EXCEPTION ParseException : "+e.getMessage());
				return null;
			}
		}
	}

	public static Date strToDateLong(String s) {
		if (blank(s)) {
			return null;
		} else {
			SimpleDateFormat std = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			try {
				return std.parse(s);
			} catch (ParseException e) {
				logger.warn("EXCEPTION ParseException : "+e.getMessage());
				return null;
			}
		}
	}

	public static long strToDateNumeric(String s) {
		if (blank(s)) {
			return 0;
		} else {
			SimpleDateFormat std = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			try {
				return std.parse(s).getTime();
			} catch (ParseException e) {
				logger.warn("EXCEPTION ParseException : "+e.getMessage());
				return 0;
			}
		}
	}

	/**
	 *
	 * @param s
	 * @return
	 * @throws ParseException
	 */
	public static java.sql.Date getSQLShort(String s) throws ParseException {
		if (blank(s)) {
			return null;
		} else {
			SimpleDateFormat std = new SimpleDateFormat("dd/MM/yyyy");
			return new java.sql.Date(std.parse(s).getTime());
		}
	}

	private static boolean blank(String s) {
		if (s == null || s.equals("")) {
			return true;
		} else if (s.matches("^\\s+$")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean validFormatDate(String stringDate) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		try {
			format.parse(stringDate);
			return true;
		} catch (ParseException e) {
			logger.warn("EXCEPTION ParseException : "+e.getMessage());
			return false;
		}
	}
	
	public static Date formatDate(String date, String format) {
		if (blank(date)) {
			return null;
		} else {
			SimpleDateFormat std = new SimpleDateFormat(format);
			try {
				return std.parse(date);
			} catch (ParseException e) {
				logger.warn("EXCEPTION ParseException : " + e.getMessage());
				return null;
			}
		}
	}
}
