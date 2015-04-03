package com.gae.pidictcloud;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gae.pidictcloud.entity.EntityFactory;
import com.gae.pidictcloud.entity.PhraseEntity;
import com.gae.pidictcloud.entity.PiDictEntity;
import com.gae.pidictcloud.entity.QuestionEntity;
import com.gae.pidictcloud.fts.PhraseIndex;
import com.gae.pidictcloud.fts.QuestionIndex;
import com.gae.pidictcloud.util.Constant;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@SuppressWarnings("serial")
public class QuestionServlet extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().println("Hi! this is PiDictCloud.");
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String accessCode = req.getParameter("code");
		if (Constant.ACCESS_CODE.equals(accessCode)) {
			// EntityManager em = EMF.get().createEntityManager();
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
			if (req.getServletPath().equals(
					Constant.SUBMIT_QUESTION_SERVLETPATH)) {
				try {
					EntityFactory ef = new EntityFactory();
					QuestionEntity qEntity = (QuestionEntity) ef.doEntityFactory(EntityFactory.QUESTION_ENTITY_KIND);
					qEntity.setQuestion(URLDecoder.decode(
							req.getParameter("content_input"), "UTF-8").trim());
					qEntity.setAnswer(URLDecoder.decode(
							req.getParameter("answer_input"), "UTF-8").trim());
					if("".equals(qEntity.getAnswer()))
						qEntity.setAnswered(false);
					else
						qEntity.setAnswered(true);
					qEntity.setCreatedOn(new Date());
					
					if(!"".equals(qEntity.getQuestion())){
						Transaction txn = datastore.beginTransaction();
						Key key = datastore.put(qEntity.createEntity());
						Queue queue = QueueFactory.getDefaultQueue();
						queue.add(TaskOptions.Builder.withUrl(Constant.INDEXING_DOCUMENT_SERVLETPATH).param("key", KeyFactory.keyToString(key))
								.param("kind", EntityFactory.QUESTION_ENTITY_KIND)
								.param("action","add"));
						 
						txn.commit();
					}
					// em.persist(pEntity);
					resp.setContentType("text/plain");					
					resp.getWriter().println("Submit successfully");	
				} catch (Exception ex) {
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				} finally {
					// em.close();
				}
			} else if (req.getServletPath().equals(
					Constant.HISTORY_QUESTION_SERVLETPATH)) {
				try {
					SimpleDateFormat dateformat = new SimpleDateFormat(
							"yyyy-MM-dd");

					Date startDate = dateformat.parse(URLDecoder.decode(
							req.getParameter("startdate"), "UTF-8").trim());
					Date endDate = dateformat.parse(URLDecoder.decode(
							req.getParameter("enddate"), "UTF-8").trim());

					String filter = URLDecoder.decode(
							req.getParameter("filter"), "UTF-8");

					Filter MinFilter = new FilterPredicate(
							PiDictEntity.createdOn_property,
							FilterOperator.GREATER_THAN_OR_EQUAL, startDate);

					Filter MaxFilter = new FilterPredicate(
							PiDictEntity.createdOn_property,
							FilterOperator.LESS_THAN_OR_EQUAL, endDate);
					Filter CompositeFilter = CompositeFilterOperator.and(
							MinFilter, MaxFilter);
					if ("yes".equals(filter)) {
						Filter AnswerFilter = new FilterPredicate(
								PiDictEntity.isAnswered_property,
								FilterOperator.EQUAL, true);
						CompositeFilter = CompositeFilterOperator.and(
								MinFilter, MaxFilter, AnswerFilter);
					} else if ("no".equals(filter)) {
						Filter AnswerFilter = new FilterPredicate(
								PiDictEntity.isAnswered_property,
								FilterOperator.EQUAL, false);
						CompositeFilter = CompositeFilterOperator.and(
								MinFilter, MaxFilter, AnswerFilter);
					}

					Query q = new Query(EntityFactory.QUESTION_ENTITY_KIND)
							.setFilter(CompositeFilter)
							.addSort(PiDictEntity.createdOn_property, SortDirection.DESCENDING);
					PreparedQuery pq = datastore.prepare(q);
					ArrayList<QuestionEntity> results = new ArrayList<QuestionEntity>();
					for (Entity result : pq.asIterable()) {
						QuestionEntity qObj = new QuestionEntity(result);
						results.add(qObj);
					}

					// //pass this results to jsp page
					req.setAttribute("qResults", results);

					req.getRequestDispatcher("/static/templates/resultQ.jsp")
							.forward(req, resp);

				} catch (Exception ex) {
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				}
			} else if (req.getServletPath().equals(
					Constant.UPDATE_QUESTION_SERVLETPATH)) {

				try {
					Key key = KeyFactory.stringToKey(req.getParameter("key"));
					Entity entity = datastore.get(key);

					if (!"".equals(req.getParameter("answer"))) {
						entity.setProperty(PiDictEntity.answer_property,
								URLDecoder.decode(req.getParameter("answer"),
										"UTF-8"));
						entity.setProperty(PiDictEntity.isAnswered_property,
								true);
						Transaction txn = datastore.beginTransaction();
						datastore.put(entity);
						Queue queue = QueueFactory.getDefaultQueue();
						queue.add(TaskOptions.Builder.withUrl(Constant.INDEXING_DOCUMENT_SERVLETPATH).param("key", KeyFactory.keyToString(key))
								.param("kind", EntityFactory.QUESTION_ENTITY_KIND)
								.param("action","update"));
						 
						txn.commit();
						
					}

				} catch (Exception ex) {
					// TODO Auto-generated catch block
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				}
			}else if(req.getServletPath().equals(Constant.SEARCH_QUESTION_SERVLETPATH)){
				try {
					String searchPhrase = URLDecoder.decode(
							req.getParameter("keyword"), "UTF-8");
					String category = URLDecoder.decode(
							req.getParameter("category"), "UTF-8");
					String[] searchFields = category.split("_");
					List<QuestionEntity> results = QuestionIndex.FullTextSearch(
							searchPhrase, searchFields);
					req.setAttribute("sqResults", results);
					req.getRequestDispatcher("/static/templates/result2.jsp")
							.forward(req, resp);
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				}
			}
		} else {
			resp.setContentType("text/html");
			resp.setCharacterEncoding("utf-8");
			resp.getWriter().println(Constant.ACCESS_DENIED);
		}
	}
	
	
}
