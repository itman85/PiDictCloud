package com.gae.pidictcloud;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gae.pidictcloud.entity.CategoryEntity;
import com.gae.pidictcloud.entity.EntityFactory;
import com.gae.pidictcloud.entity.PiDictEntity;
import com.gae.pidictcloud.fts.CategoryIndex;
import com.gae.pidictcloud.util.Constant;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@SuppressWarnings("serial")
public class CategoryServlet extends HttpServlet{
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
					Constant.UPDATE_CATEGORY_SERVLETPATH)) {

				try {
					Key key = KeyFactory.stringToKey(req.getParameter("key"));
					Entity entity = datastore.get(key);
					String newCate = URLDecoder.decode(req.getParameter("cate_name"),"UTF-8").trim();
					if (!"".equals(newCate)) {
						entity.setProperty(PiDictEntity.category_name_property,	newCate);
						
						Transaction txn = datastore.beginTransaction();
						datastore.put(entity);
						Queue queue = QueueFactory.getDefaultQueue();
						queue.add(TaskOptions.Builder.withUrl(Constant.INDEXING_DOCUMENT_SERVLETPATH).param("key", KeyFactory.keyToString(key))
								.param("kind", EntityFactory.CATEGORY_ENTITY_KIND)
								.param("action","update"));
						queue.add(TaskOptions.Builder.withUrl(Constant.UPDATE_INDEXING_DOCUMENT_SERVLETPATH).param("category_key", KeyFactory.keyToString(key))
								.param("new_category", newCate)
								.param("kind", EntityFactory.PHRASE_ENTITY_KIND)
								.param("action","update_category"));
						 
						txn.commit();
						
					}

				} catch (EntityNotFoundException ex) {
					// TODO Auto-generated catch block
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				}
			}else if(req.getServletPath().equals(Constant.SEARCH_CATEGORY_SERVLETPATH)){
				try {
					String searchCate = URLDecoder.decode(
							req.getParameter("keyword"), "UTF-8").trim();
					
					List<CategoryEntity> results = CategoryIndex.FullTextSearch(
							searchCate);
					req.setAttribute("scResults", results);
					req.getRequestDispatcher("/static/templates/resultC.jsp")
							.forward(req, resp);
				} catch (ServletException ex) {
					// TODO Auto-generated catch block
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				}
			}else  if (req.getServletPath().equals(
					Constant.SUBMIT_CATEGORY_SERVLETPATH)) {
				try {
					EntityFactory ef = new EntityFactory();
					CategoryEntity cEntity = (CategoryEntity) ef.doEntityFactory(EntityFactory.CATEGORY_ENTITY_KIND);
					cEntity.setCategory_name(URLDecoder.decode(
							req.getParameter("content_input"), "UTF-8").trim());
					cEntity.setCreatedOn(new Date());
					if(!"".equals(cEntity.getCategory_name())){
						Transaction txn = datastore.beginTransaction();
						Key cKey = datastore.put(txn,cEntity.createEntity());						
						Queue queue = QueueFactory.getDefaultQueue();	
						queue.add(TaskOptions.Builder.withUrl(Constant.INDEXING_DOCUMENT_SERVLETPATH).param("key", KeyFactory.keyToString(cKey))
								.param("kind", EntityFactory.CATEGORY_ENTITY_KIND)
								.param("action","add"));
						txn.commit();
						resp.setContentType("text/plain");	
						resp.setCharacterEncoding("utf-8");
						resp.getWriter().println(cEntity.getCategory_name()+"#"+KeyFactory.keyToString(cKey));
					}else{
						resp.setContentType("text/plain");						
						resp.getWriter().println("Category name must not be empty!");						
					}
				}catch (Exception ex) {
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println("Exception " + ex.getMessage());
				} finally {
					
				}
			}
		} else {
			resp.setContentType("text/html");
			resp.setCharacterEncoding("utf-8");
			resp.getWriter().println(Constant.ACCESS_DENIED);
		}
	}
}
