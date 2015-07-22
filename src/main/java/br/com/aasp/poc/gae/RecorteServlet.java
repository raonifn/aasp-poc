package br.com.aasp.poc.gae;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;

import br.com.queronatura.api.util.EntityUtil;

public class RecorteServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String texto = req.getParameter("texto");
		String id = UUID.randomUUID().toString();

		Entity entity = new Entity("Recorte", id);
		super.doPut(req, resp);
	}

}
