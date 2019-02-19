/**
 * 
 */
package com.w4.api.infrastructures;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author frederic
 *
 */
public class SessionUtils {

	public static HttpServletRequest getRequest() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes();
		return attributes.getRequest();
	}

	public static HttpSession getSession() {
		return getSession(true);
	}

	public static HttpSession getSession(boolean create) {
		HttpSession session = getRequest().getSession(create);
		session.setMaxInactiveInterval(-1);
		return session;
	}

	public static String getSessionId() {
		return getSession().getId();
	}

	public static ServletContext getServletContext() {
		return getSession().getServletContext();
	}
}
