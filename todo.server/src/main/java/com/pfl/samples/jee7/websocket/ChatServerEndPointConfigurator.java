/**
 * ChatServerEndPointConfigurator.java
 * http://programmingforliving.com
 */
package com.pfl.samples.jee7.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

/**
 * ChatServerEndPointConfigurator
 * @author Jiji_Sasidharan
 */
public class ChatServerEndPointConfigurator extends Configurator {

	private ChatServerEndPoint chatServer = new ChatServerEndPoint();

	@Override
	public <T> T getEndpointInstance(Class<T> endpointClass)
			throws InstantiationException {
//		System.out
//				.println("ChatServerEndPointConfigurator.getEndpointInstance()");
		return (T)chatServer;
	}
	
	@Override
	public void modifyHandshake(ServerEndpointConfig sec,
			HandshakeRequest request, HandshakeResponse response) {
//		System.out.println("ChatServerEndPointConfigurator.modifyHandshake()");
		HttpSession httpSession = (HttpSession)request.getHttpSession();
//		System.out.println(httpSession);
        sec.getUserProperties().put(HttpSession.class.getName(),httpSession);
//        System.out.println("ChatServerEndPointConfigurator.modifyHandshake()/end");
	}
}