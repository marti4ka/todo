package com.droidcluster.todo.server;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class HttpSessionCollector implements HttpSessionListener {
	private static final Map<String, String> items = new HashMap<String, String>();
	private static final Map<String, String> wsIds = new HashMap<String, String>();

	@Override
	public void sessionCreated(HttpSessionEvent event) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		items.remove(event.getSession()
				.getAttribute(NotesServlet.SESSION_ID));
	}

	public static String get(String sessionId) {
		return items.get(sessionId);
	}

	public static String putItems(String sessionId, String json) {
		return items.put(sessionId, json);
	}

	public static String getWsId(String itemsId) {
		return wsIds.get(itemsId);
	}

	public static void putWsId(String itemsId, String wsId) {
		wsIds.put(itemsId, wsId);
	}

}