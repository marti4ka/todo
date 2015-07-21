package com.droidcluster.todo.server;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class NotesServlet extends HttpServlet {
	public static final String SESSION_ID = "node_uuid";
	private static final String UTF_8 = "UTF-8";
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		if (pathInfo == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"missing notes id");
			return;
		}

		String uuid = pathInfo.substring(1);
		Object inHttpSession = req.getSession().getAttribute(SESSION_ID);
		if ("undefined".equals(uuid)) {
			if(inHttpSession instanceof String) {
				uuid = (String) inHttpSession;
			} else {
				uuid = UUID.randomUUID().toString();
				req.getSession().setAttribute(SESSION_ID, uuid);
			}
		} else {
			if (inHttpSession instanceof String && !uuid.equals(inHttpSession)) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"wrond notes id");
				return;
			}
		}

		String result = HttpSessionCollector.get(uuid);
		if (result == null) {
			result = "[]";
		}
		resp.setContentLength(result.length());
		resp.setContentType("application/json");
		resp.setHeader("notes-id", uuid);
		resp.getOutputStream().write(result.getBytes(UTF_8));
		resp.getOutputStream().flush();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String uuid = req.getPathInfo().substring(1);
		String json = IOUtils.toString(req.getInputStream(), UTF_8);
		HttpSessionCollector.put(uuid, json);
	}
}
