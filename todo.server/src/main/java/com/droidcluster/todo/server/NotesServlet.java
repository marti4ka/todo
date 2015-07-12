package com.droidcluster.todo.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NotesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String result = "[ { \"text\" : \"learn angular.js\", \"done\" : true }, { \"text\" : \"build an ANGULAR app\", \"done\" : false } ]";
		resp.getOutputStream().write(result.getBytes());
		resp.getOutputStream().flush();

		// TODO Auto-generated method stub
	}
}
