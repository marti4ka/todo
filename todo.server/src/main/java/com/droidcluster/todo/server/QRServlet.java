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
		byte[] qr = QRCode.from(UUID.randomUUID().toString()).to(ImageType.PNG)
				.stream().toByteArray();

		resp.setContentLength(qr.length);
		resp.setContentType("image/png");
		resp.getOutputStream().write(qr);
		resp.getOutputStream().flush();
	}
}
