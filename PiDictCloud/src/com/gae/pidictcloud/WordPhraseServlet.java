package com.gae.pidictcloud;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gae.pidictcloud.entity.CategoryEntity;
import com.gae.pidictcloud.entity.EntityFactory;
import com.gae.pidictcloud.entity.PhraseEntity;
import com.gae.pidictcloud.entity.PiDictEntity;
import com.gae.pidictcloud.fts.CategoryIndex;
import com.gae.pidictcloud.fts.PhraseIndex;
import com.gae.pidictcloud.util.Constant;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;


@SuppressWarnings("serial")
public class WordPhraseServlet  extends HttpServlet {
	
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
			//EntityManager em = EMF.get().createEntityManager();
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			if (req.getServletPath().equals(
					Constant.SUBMIT_WORD_PHRASE_SERVLETPATH)) {				
				try {
					EntityFactory ef = new EntityFactory();
					PhraseEntity pEntity = (PhraseEntity) ef.doEntityFactory(EntityFactory.PHRASE_ENTITY_KIND);
					pEntity.setPhrase(URLDecoder.decode(
							req.getParameter("word_input"), "UTF-8").trim());
					
					String category = URLDecoder.decode(
							req.getParameter("category_input"), "UTF-8").trim();					
				
					pEntity.setEnmeaning(URLDecoder.decode(
							req.getParameter("en_meant_input"), "UTF-8").trim());
					pEntity.setVimeaning(URLDecoder.decode(
							req.getParameter("vi_meant_input"), "UTF-8").trim());

					pEntity.setCreatedOn(new Date());
					
					TransactionOptions options = TransactionOptions.Builder.withXG(true);
					Transaction txn = datastore.beginTransaction(options);
					Queue queue = QueueFactory.getDefaultQueue();
					if(!"".equals(category)){
						if(category.indexOf('#')!=-1){
							String[] cateKey = category.split("#");
							pEntity.setCategory(cateKey[0]);
							pEntity.setCategoryKey(cateKey[1]);						
						}else{
							CategoryEntity cEntity = (CategoryEntity) ef.doEntityFactory(EntityFactory.CATEGORY_ENTITY_KIND);
							cEntity.setCategory_name(category);
							cEntity.setCreatedOn(new Date());
							Key cKey = datastore.put(txn,cEntity.createEntity());						
							pEntity.setCategory(category);
							pEntity.setCategoryKey(KeyFactory.keyToString(cKey));	
							queue.add(TaskOptions.Builder.withUrl(Constant.INDEXING_DOCUMENT_SERVLETPATH).param("key", pEntity.getCategoryKey())
									.param("kind", EntityFactory.CATEGORY_ENTITY_KIND)
									.param("action","add"));
						}
					}
					Key key = datastore.put(txn,pEntity.createEntity());					
					
					queue.add(TaskOptions.Builder.withUrl(Constant.INDEXING_DOCUMENT_SERVLETPATH).param("key", KeyFactory.keyToString(key))
							.param("kind", EntityFactory.PHRASE_ENTITY_KIND)
							.param("action","add"));
					
					
					txn.commit();
					//em.persist(pEntity);
					//resp.sendRedirect(Constant.ROOT_SERVLETPATH);
					resp.setContentType("text/plain");					
					resp.getWriter().println("Submit successfully");	
				} catch (Exception ex) {
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				} finally {
					//em.close();
				}
			}else if (req.getServletPath().equals(
					Constant.SUBMIT_WORD_PHRASE_FROM_EXTENSION_SERVLETPATH)) {	
				JSONObject resJsonObj = new JSONObject();//this json obj for response 
				JSONArray listOKPhraseIds = new JSONArray();
				String resStatus = ""; 
				try {
					EntityFactory ef = new EntityFactory();
					JSONArray arrJsonObjs = new JSONArray(req.getParameter("data"));					
					TransactionOptions options = TransactionOptions.Builder.withXG(true);
					
					Queue queue = QueueFactory.getDefaultQueue();
					
					for(int i=0;i<arrJsonObjs.length();i++)
					{
						//begin transaction
						Transaction txn = datastore.beginTransaction(options);
						
						JSONObject jsonObj = arrJsonObjs.getJSONObject(i);
					    
					    PhraseEntity pEntity = (PhraseEntity) ef.doEntityFactory(EntityFactory.PHRASE_ENTITY_KIND);
						pEntity.setPhrase(jsonObj.getString("phrase").trim());						
						String category = jsonObj.getString("category").trim();					
						pEntity.setEnmeaning(jsonObj.getString("enmeaning").trim());
						pEntity.setVimeaning(jsonObj.getString("vimeaning").trim());

						pEntity.setCreatedOn(new Date());
						if(!"".equals(category)){
							if(category.indexOf('#')!=-1){
								String[] cateKey = category.split("#");
								pEntity.setCategory(cateKey[0]);
								pEntity.setCategoryKey(cateKey[1]);						
							}else{
								CategoryEntity cEntity = (CategoryEntity) ef.doEntityFactory(EntityFactory.CATEGORY_ENTITY_KIND);
								cEntity.setCategory_name(category);
								cEntity.setCreatedOn(new Date());
								Key cKey = datastore.put(txn,cEntity.createEntity());						
								pEntity.setCategory(category);
								pEntity.setCategoryKey(KeyFactory.keyToString(cKey));	
								queue.add(TaskOptions.Builder.withUrl(Constant.INDEXING_DOCUMENT_SERVLETPATH).param("key", pEntity.getCategoryKey())
										.param("kind", EntityFactory.CATEGORY_ENTITY_KIND)
										.param("action","add"));
							}
						}
						
						Key key = datastore.put(txn,pEntity.createEntity());					
						
						//task is only enqueued—and guaranteed to be enqueued—if the transaction is committed successfully. 
						queue.add(TaskOptions.Builder.withUrl(Constant.INDEXING_DOCUMENT_SERVLETPATH).param("key", KeyFactory.keyToString(key))
								.param("kind", EntityFactory.PHRASE_ENTITY_KIND)
								.param("action","add"));
						
						//commit transaction
						txn.commit();
						//add id of phrase that store into cloud successfully.
						listOKPhraseIds.put(jsonObj.getString("clientid").trim());
					}					 														
					
				} catch (Exception ex) {									
					resStatus = ex.getMessage();					
				} finally {
					try {
						resJsonObj.put("resultlist", listOKPhraseIds);
						if(listOKPhraseIds.length()>0){
							if(!"".equals(resStatus))
								resStatus = "Store "+ listOKPhraseIds.length() + " phrase(s) successfully!" + "\n" + resStatus;
							else
								resStatus = "Store "+ listOKPhraseIds.length() + " phrase(s) successfully!";
						}
						resJsonObj.put("message", resStatus);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(resJsonObj.toString());//send response with json string to client					
				}
			}else if (req.getServletPath().equals(
					Constant.HISTORY_WORD_PHRASE_FROM_EXTENSION_SERVLETPATH)) {	
				JSONObject resJsonObj = new JSONObject();//this json obj for response				
				JSONArray listHisJsonObjs = new JSONArray();
				String resStatus = "";
				try{
					SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
					
					Date startDate = dateformat.parse(URLDecoder.decode(
								req.getParameter("startdate"), "UTF-8").trim());
					Date endDate = dateformat.parse(URLDecoder.decode(
							req.getParameter("enddate"), "UTF-8").trim());				

					Filter MinFilter =
					  new FilterPredicate(PiDictEntity.createdOn_property,
					                      FilterOperator.GREATER_THAN_OR_EQUAL,
					                      startDate);

					Filter MaxFilter =
					  new FilterPredicate(PiDictEntity.createdOn_property,
					                      FilterOperator.LESS_THAN_OR_EQUAL,
					                      endDate);
					Filter RangeFilter =
							  CompositeFilterOperator.and(MinFilter, MaxFilter);
					Query q = new Query(EntityFactory.PHRASE_ENTITY_KIND)
									.setFilter(RangeFilter)
									.addSort(PiDictEntity.createdOn_property, SortDirection.DESCENDING);;
					PreparedQuery pq = datastore.prepare(q);					
					for (Entity result : pq.asIterable()) {
						PhraseEntity pObj = new PhraseEntity(result);	
						JSONObject hisJsonObj = new JSONObject();
						hisJsonObj.put("phrase", pObj.getPhrase());
						hisJsonObj.put("category", pObj.getCategory());
						hisJsonObj.put("vimeaning", pObj.getVimeaning());
						hisJsonObj.put("enmeaning", pObj.getEnmeaning());
						hisJsonObj.put("createdon",new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a Z").format(pObj.getCreatedOn()));
						//add this json obj into list of json objs
						listHisJsonObjs.put(hisJsonObj);
					}					
					resStatus = "Received "+ listHisJsonObjs.length() + " phrase(s) successfully!"; 
				}catch(Exception ex)
				{
					listHisJsonObjs = new JSONArray();
					resStatus = ex.getMessage();
				}
				finally {					
					try {
						resJsonObj.put("resultlist", listHisJsonObjs);
						resJsonObj.put("message", resStatus);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(resJsonObj.toString());//send response with json string to client						
				}
				
			}else if (req.getServletPath().equals(
					Constant.SEARCH_WORD_PHRASE_FROM_EXTENSION_SERVLETPATH)) {
				JSONObject resJsonObj = new JSONObject();//this json obj for response				
				JSONArray listSearchJsonObjs = new JSONArray();
				String resStatus = "";
				try {
					String searchPhrase = URLDecoder.decode(
							req.getParameter("keyword"), "UTF-8").trim();
					String category = URLDecoder.decode(
							req.getParameter("category"), "UTF-8");
					String[] searchFields = category.split("_");
					List<PhraseEntity> results = PhraseIndex.FullTextSearch(
							searchPhrase, searchFields);
					for(PhraseEntity pObj : results)
					{
						JSONObject hisJsonObj = new JSONObject();
						hisJsonObj.put("phrase", pObj.getPhrase());
						hisJsonObj.put("category", pObj.getCategory());
						hisJsonObj.put("vimeaning", pObj.getVimeaning());
						hisJsonObj.put("enmeaning", pObj.getEnmeaning());
						hisJsonObj.put("createdon",new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a Z").format(pObj.getCreatedOn()));
						//add this json obj into list of json objs
						listSearchJsonObjs.put(hisJsonObj);
					}
					resStatus = "Received "+ listSearchJsonObjs.length() + " phrase(s) successfully!"; 
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					listSearchJsonObjs = new JSONArray();
					resStatus = ex.getMessage();
				}finally {					
					try {
						resJsonObj.put("resultlist", listSearchJsonObjs);
						resJsonObj.put("message", resStatus);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(resJsonObj.toString());//send response with json string to client						
				}
				
			}else if(req.getServletPath().equals(Constant.HISTORY_WORD_PHRASE_SERVLETPATH)){
				try{
					SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
					
					Date startDate = dateformat.parse(URLDecoder.decode(
								req.getParameter("startdate"), "UTF-8").trim());
					Date endDate = dateformat.parse(URLDecoder.decode(
							req.getParameter("enddate"), "UTF-8").trim());				

					Filter MinFilter =
					  new FilterPredicate(PiDictEntity.createdOn_property,
					                      FilterOperator.GREATER_THAN_OR_EQUAL,
					                      startDate);

					Filter MaxFilter =
					  new FilterPredicate(PiDictEntity.createdOn_property,
					                      FilterOperator.LESS_THAN_OR_EQUAL,
					                      endDate);
					Filter RangeFilter =
							  CompositeFilterOperator.and(MinFilter, MaxFilter);
					Query q = new Query(EntityFactory.PHRASE_ENTITY_KIND)
								 .setFilter(RangeFilter)
							     .addSort(PiDictEntity.createdOn_property, SortDirection.DESCENDING);
					PreparedQuery pq = datastore.prepare(q);
					ArrayList<PhraseEntity> results = new ArrayList<PhraseEntity>() ;
					for (Entity result : pq.asIterable()) {
						PhraseEntity pObj = new PhraseEntity(result);						
						results.add(pObj);						 
						}
					
					 /*TypedQuery<PhraseEntity> query =
						      em.createNamedQuery(PhraseEntity.HISTORY_QUERY_NAME, PhraseEntity.class);
							
					 query.setParameter("startdate", startDate,TemporalType.DATE);
					 query.setParameter("enddate", endDate,TemporalType.DATE);
					List<PhraseEntity> results = (List<PhraseEntity>) query.getResultList();
					*/
					////pass this results to jsp page
					req.setAttribute("wpResults", results);					
					req.getRequestDispatcher("/static/templates/resultW.jsp").forward(req, resp);
					
				}catch(Exception ex)
				{
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				}
			}else if(req.getServletPath().equals(Constant.UPDATE_WORD_PHRASE_SERVLETPATH)){
				
				try {
					Key key = KeyFactory.stringToKey(req.getParameter("key"));
					Entity entity = datastore.get(key);
					
					TransactionOptions options = TransactionOptions.Builder.withXG(true);
					Transaction txn = datastore.beginTransaction(options);
					Queue queue = QueueFactory.getDefaultQueue();
					
					if(!"".equals(req.getParameter("vi_meant").trim()))
						entity.setProperty(PiDictEntity.vimeaning_property, URLDecoder.decode(
								req.getParameter("vi_meant"), "UTF-8").trim());
					if(!"".equals(req.getParameter("en_meant").trim()))
						entity.setProperty(PiDictEntity.enmeaning_property, URLDecoder.decode(
								req.getParameter("en_meant"), "UTF-8").trim());
					if(!"".equals(req.getParameter("category").trim())){
						String category =  URLDecoder.decode(req.getParameter("category"), "UTF-8").trim();
						if(category.indexOf('#')!=-1){
							String[] cateKey = category.split("#");
							entity.setProperty(PiDictEntity.category_property, cateKey[0]);
							entity.setProperty(PiDictEntity.category_key_property, cateKey[1]);												
						}else{
							EntityFactory ef = new EntityFactory();
							CategoryEntity cEntity = (CategoryEntity) ef.doEntityFactory(EntityFactory.CATEGORY_ENTITY_KIND);
							cEntity.setCategory_name(category);
							cEntity.setCreatedOn(new Date());
							Key cKey = datastore.put(txn,cEntity.createEntity());	
							entity.setProperty(PiDictEntity.category_property, category);
							entity.setProperty(PiDictEntity.category_key_property, KeyFactory.keyToString(cKey));							
							queue.add(TaskOptions.Builder.withUrl(Constant.INDEXING_DOCUMENT_SERVLETPATH).param("key", KeyFactory.keyToString(cKey))
									.param("kind", EntityFactory.CATEGORY_ENTITY_KIND)
									.param("action","add"));
						}						
					}	
					
					datastore.put(txn,entity);					
					queue.add(TaskOptions.Builder.withUrl(Constant.INDEXING_DOCUMENT_SERVLETPATH).param("key", KeyFactory.keyToString(key))
							.param("kind", EntityFactory.PHRASE_ENTITY_KIND)
							.param("action","update"));
					
					txn.commit();
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				}
				
				
			}else if(req.getServletPath().equals(Constant.SEARCH_WORD_PHRASE_SERVLETPATH)){
				try {
					String searchPhrase = URLDecoder.decode(
							req.getParameter("keyword"), "UTF-8").trim();
					String category = URLDecoder.decode(
							req.getParameter("category"), "UTF-8");
					String[] searchFields = category.split("_");
					List<PhraseEntity> results = PhraseIndex.FullTextSearch(
							searchPhrase, searchFields);
					req.setAttribute("spResults", results);
					req.getRequestDispatcher("/static/templates/result.jsp")
							.forward(req, resp);
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(String.format(Constant.EXCEPTION,"Exception " + ex.getMessage()));
				}
			}
		}else {
			if (req.getServletPath().equals(
					Constant.SUBMIT_WORD_PHRASE_FROM_EXTENSION_SERVLETPATH)||
				req.getServletPath().equals(
					Constant.HISTORY_WORD_PHRASE_FROM_EXTENSION_SERVLETPATH)||
				req.getServletPath().equals(
					Constant.SEARCH_WORD_PHRASE_FROM_EXTENSION_SERVLETPATH)) {
				resp.setContentType("text/html");
				resp.setCharacterEncoding("utf-8");
				resp.getWriter().println("{\"message\":\"Access denied\",\"resultlist\":[]}");//send response with json string to client		
			}else{
				resp.setContentType("text/html");
				resp.setCharacterEncoding("utf-8");
				resp.getWriter().println(Constant.ACCESS_DENIED);				
				
			}
		}
	 }
	
}
