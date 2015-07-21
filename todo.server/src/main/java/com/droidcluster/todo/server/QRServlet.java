package com.droidcluster.todo.server;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class QRServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String uuid;
		Object attribute = req.getSession().getAttribute(NotesServlet.SESSION_ID);
		if(!(attribute instanceof String) || "undefined".equals(attribute)) {
			uuid = UUID.randomUUID().toString();
			req.getSession().setAttribute(NotesServlet.SESSION_ID, uuid);
		} else {
			uuid = (String) attribute;
		}
		
		int pathLength = req.getServletPath().length();
		String url = req.getRequestURL().toString();
		url = url.substring(0, url.length() - pathLength);
		url = url + "/notes/" + uuid;
//		resp.getOutputStream().write(url.getBytes());

		byte[] qr = QRCode.from(url).to(ImageType.PNG).withSize(250, 250)
				.stream().toByteArray();
		resp.setContentLength(qr.length);
		resp.setContentType("image/png");
		resp.setHeader("notes-id", uuid);
		resp.getOutputStream().write(qr);
		resp.getOutputStream().flush();
	}
}
