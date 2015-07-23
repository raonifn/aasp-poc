package br.com.aasp.poc.gae;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutException;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;

public class RecorteServlet extends HttpServlet {

	private static final long serialVersionUID = -4638246491309277347L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String value = req.getParameter("value");
		PrintWriter out = resp.getWriter();

		if (value == null) {
			resp.setStatus(400);
			resp.setContentType("text/json");
			out.printf("{\"error\": \"value required\"}");
			return;
		}

		Results<ScoredDocument> result = find("recorte", value);
		resp.setContentType("text/json");

		resp.getWriter();
		out.println("[");
		int i = 0;
		for (ScoredDocument scored : result) {
			if (i++ > 0) {
				out.println(",");
			}
			out.printf("{\"score\": \"%d\", \"id\": \"%s\", \"texto\": \"%s\"}]", scored.getRank(), scored.getId(),
					scored.getFields("texto"));
		}
		out.println("]");
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

		Document doc = Document.newBuilder().setId(id).addField(Field.newBuilder().setName("texto").setText(texto))
				.build();
		indexADocument("recorte", doc);

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

	public String getURIWithoutContextPath(HttpServletRequest req) {
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();

		if (contextPath != null && !contextPath.isEmpty()) {
			uri = uri.replace(contextPath, "");
		}

		return uri;
	}

	public void indexADocument(String indexName, Document document) {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

		try {
			index.put(document);
		} catch (PutException e) {
			if (StatusCode.TRANSIENT_ERROR.equals(e.getOperationResult().getCode())) {
				index.put(document);
			}
		}
	}

	public Results<ScoredDocument> find(String indexName, String value) {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(indexName).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

		Results<ScoredDocument> result = index.search(value);
		return result;
	}

}
