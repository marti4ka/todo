package com.droidcluster.todo.server;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class HttpSessionCollector implements HttpSessionListener {
	private static final Map<String, String> sessions = new HashMap<String, String>();

	@Override
	public void sessionCreated(HttpSessionEvent event) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		sessions.remove(event.getSession()
				.getAttribute(NotesServlet.SESSION_ID));
	}

	public static String get(String sessionId) {
		return sessions.get(sessionId);
	}

	public static String put(String sessionId, String json) {
		return sessions.put(sessionId, json);
	}

}