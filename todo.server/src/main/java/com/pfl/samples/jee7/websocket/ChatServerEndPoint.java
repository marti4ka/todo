/**
 * ChatServerEndPoint.java
 * http://programmingforliving.com
 */
package com.pfl.samples.jee7.websocket;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.droidcluster.todo.server.HttpSessionCollector;
import com.droidcluster.todo.server.NotesServlet;

/**
 * ChatServer
 * 
 * @author Jiji_Sasidharan
 */
@ServerEndpoint(value = "/chat", configurator = ChatServerEndPointConfigurator.class)
public class ChatServerEndPoint {

	public static final String WS_ID = "WS_ID";
	private static Map<String, Session> sessions = new HashMap<>();

	/**
	 * Callback hook for Connection open events. This method will be invoked
	 * when a client requests for a WebSocket connection. <br/>
	 * http://stackoverflow.com/questions/17936440/accessing-httpsession-from-
	 * httpservletrequest-in-a-web-socket-serverendpoint
	 * 
	 * @param userSession
	 *            the userSession which is opened.
	 */
	@OnOpen
	public void onOpen(Session userSession) {
		HttpSession httpSession = (HttpSession) userSession.getUserProperties()
				.get(HttpSession.class.getName());
		String wsId = userSession.getId();
		String itemsId = (String) httpSession
				.getAttribute(NotesServlet.SESSION_ID);
		HttpSessionCollector.putWsId(itemsId, wsId);
		sessions.put(wsId, userSession);
		httpSession.setAttribute(WS_ID, wsId);
//		System.out.println("ChatServerEndPoint.onOpen()");
//		System.out.println("itemsId=" + itemsId + ", wsId=" + wsId);
	}

	/**
	 * Callback hook for Connection close events. This method will be invoked
	 * when a client closes a WebSocket connection.
	 * 
	 * @param userSession
	 *            the userSession which is opened.
	 */
	@OnClose
	public void onClose(Session userSession) {
//		System.out.println("ChatServerEndPoint.onClose()");
	}

	/**
	 * Callback hook for Message Events. This method will be invoked when a
	 * client send a message.
	 * 
	 * @param message
	 *            The text message
	 * @param userSession
	 *            The session of the client
	 */
	@OnMessage
	public void onMessage(String message, Session userSession) {
//		System.out.println(message);
	}

	public static Session getWsSession(String wsId) {
		return sessions.get(wsId);
	}
}