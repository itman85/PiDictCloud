package com.gae.pidictcloud;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gae.pidictcloud.entity.CategoryEntity;
import com.gae.pidictcloud.entity.EntityFactory;
import com.gae.pidictcloud.entity.PhraseEntity;
import com.gae.pidictcloud.entity.PiDictEntity;
import com.gae.pidictcloud.entity.QuestionEntity;
import com.gae.pidictcloud.fts.CategoryIndex;
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
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@SuppressWarnings("serial")
public class IndexingServlet extends HttpServlet{
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		if (req.getServletPath().equals(
				Constant.INDEXING_DOCUMENT_SERVLETPATH)) {
			try {			
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
				Key key = KeyFactory.stringToKey(req.getParameter("key"));
				Entity entity = datastore.get(key);
				String kindEntity = req.getParameter("kind");
				String action = req.getParameter("action");
				if(EntityFactory.PHRASE_ENTITY_KIND.equals(kindEntity))
				{
					PhraseEntity pObj = new PhraseEntity(entity);				
					if("add".equals(action))
						PhraseIndex.IndexingDocument(pObj.buildDocument());
					else if("update".equals(action))
						PhraseIndex.UpdateIndexingDocument(pObj);					
					
				}else if(EntityFactory.QUESTION_ENTITY_KIND.equals(kindEntity))
				{
					QuestionEntity qObj = new QuestionEntity(entity);							
					if("add".equals(action))
						QuestionIndex.IndexingDocument(qObj.buildDocument());
					else if("update".equals(action))
						QuestionIndex.UpdateIndexingDocument(qObj);
				}else if(EntityFactory.CATEGORY_ENTITY_KIND.equals(kindEntity))
				{
					CategoryEntity cObj = new CategoryEntity(entity);				
					if("add".equals(action))
						CategoryIndex.IndexingDocument(cObj.buildDocument());
					else if("update".equals(action))
						CategoryIndex.UpdateIndexingDocument(cObj);
					
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (req.getServletPath().equals(
				Constant.UPDATE_INDEXING_DOCUMENT_SERVLETPATH)) {
			try {	
				DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();				
				String kindEntity = req.getParameter("kind");
				String action = req.getParameter("action");
				if(EntityFactory.PHRASE_ENTITY_KIND.equals(kindEntity))
				{
					if("update_category".equals(action)){
						String cateKey = req.getParameter("category_key");
						String newCate = req.getParameter("new_category");
						Filter EqualFilter =
								  new FilterPredicate(PiDictEntity.category_key_property,
								                      FilterOperator.EQUAL,
								                      cateKey);
						Query q = new Query(EntityFactory.PHRASE_ENTITY_KIND).setFilter(EqualFilter);
						PreparedQuery pq = datastore.prepare(q);
						ArrayList<Entity> arrUpdatedEntities = new ArrayList<Entity>() ;
						for (Entity updatedEntity : pq.asIterable()) {
							updatedEntity.setProperty(PiDictEntity.category_property, newCate);	
							arrUpdatedEntities.add(updatedEntity);						 
						}
						if(arrUpdatedEntities.size()>0){
							TransactionOptions options = TransactionOptions.Builder.withXG(true);
							Transaction txn = datastore.beginTransaction(options);
							datastore.put(txn,arrUpdatedEntities);
							PhraseIndex.UpdateIndexingDocumentsForCategoryField(cateKey, newCate);
							txn.commit();
						}
						
					}
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {
		doGet(req, resp);
	}
}
