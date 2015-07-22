package br.com.aasp.poc.gae;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class RecorteServlet extends HttpServlet {

	private static final long serialVersionUID = -4638246491309277347L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String texto = req.getParameter("texto");
		String id = UUID.randomUUID().toString();

		Entity entity = new Entity("Recorte", id);
		entity.setProperty("texto", texto);

		AsyncDatastoreService asyncDS = DatastoreServiceFactory.getAsyncDatastoreService();
		asyncDS.put(entity);

		resp.setContentType("text/json");
		resp.getWriter().printf("{\"id\": \"%s\"}", id);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] parts = getURIWithoutContextPath(req).split("/");

		if (parts.length != 3) {
			resp.setStatus(400);
			resp.setContentType("text/json");
			resp.getWriter().printf("{\"error\": \"id required\"}");
			return;
		}

		Key key = KeyFactory.createKey("Recorte", parts[2]);
		DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

		try {
			Entity entity = datastoreService.get(key);
			resp.setContentType("text/json");
			resp.getWriter().printf("{\"texto\": \"%s\"}", entity.getProperty("texto"));
		} catch (EntityNotFoundException e) {
			resp.setStatus(404);
			resp.setContentType("text/json");
			resp.getWriter().printf("{\"error\": \"invalid id}");
		}

	}

	public static String getURIWithoutContextPath(HttpServletRequest req) {
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();

		if (contextPath != null && !contextPath.isEmpty()) {
			uri = uri.replace(contextPath, "");
		}

		return uri;
	}

}
