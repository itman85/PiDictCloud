package com.gae.pidictcloud.fts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gae.pidictcloud.entity.CategoryEntity;
import com.gae.pidictcloud.entity.PiDictEntity;
import com.gae.pidictcloud.entity.QuestionEntity;
import com.gae.pidictcloud.util.Constant;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;

public class CategoryIndex {
	private static final Index INDEX = SearchServiceFactory.getSearchService()
		      .getIndex(IndexSpec.newBuilder().setName("category_index"));
	private static final Logger LOG = Logger.getLogger(
			CategoryIndex.class.getName());
	
	public static String IndexingDocument(Document doc)
	{
		try {
			LOG.info("Adding document:\n" + doc.toString());
			INDEX.put(doc);			
			return "Indexing category OK";
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to add " + doc, e);
			return "Indexing category error " + e.getMessage();
		}
	}
	
	public static String Delete(String [] docIds)
	{
		try {
			LOG.info("Detete index" );
			
			INDEX.delete(docIds);
			return "Detete category OK";
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to delete ", e);
			return "delete category error " + e.getMessage();
		}
	}
	
	public static String UpdateIndexingDocument(PiDictEntity piEntity)
	{
		try {
			Query query = Query.newBuilder()
					.setOptions(QueryOptions.newBuilder().setLimit(1).
					build()).build(PiDictEntity.entity_key_property+":"+KeyFactory.keyToString(piEntity.getKey()));
			LOG.info("Sending query " + query);
			Results<ScoredDocument> results = INDEX.search(query);
			 for (ScoredDocument scoredDoc : results) {
				 Document doc = piEntity.updateDocument(scoredDoc);
				 INDEX.put(doc);	
			 }
			return "Update indexing category OK";
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to update " ,e);
			return "Update indexing category error " + e.getMessage();
		}
	}
	
	public static List<CategoryEntity> FullTextSearch(String searchCate){
		List<CategoryEntity> found = new ArrayList<CategoryEntity>();
		try {		
				
			String plural = "";
			String queryStr = "";
			if("".equals(searchCate)){
				queryStr = "NOT " +  PiDictEntity.category_name_property+" = \"\"";
			}else {
				if((searchCate.charAt(0)=='"' && searchCate.charAt(searchCate.length()-1)=='"'))
					plural = "~";
				else{
					if(searchCate.indexOf(' ')==-1){
						plural = "~";
						searchCate = "\""+searchCate+"\"";					
					}
				}
				queryStr = PiDictEntity.category_name_property+":"+plural+searchCate;
			}
			/*SortOptions sortOptions = SortOptions.newBuilder()
		            .addSortExpression(SortExpression.newBuilder()
		                .setExpression(PiDictEntity.createdOn_property)
		                .setDirection(SortExpression.SortDirection.DESCENDING)
		                .setDefaultValue(""))
		            .setLimit(1000)
		            .build();*/
			QueryOptions options = QueryOptions.newBuilder()
				    .setLimit(1000)//max category is 1k				    
				    /*.setSortOptions(sortOptions)*/				    
				    .build();
			Query query = Query.newBuilder().setOptions(options).build(queryStr);
			LOG.info("Sending query " + query);
		    Results<ScoredDocument> results = INDEX.search(query);
		    for (ScoredDocument scoredDoc : results) {
		    	CategoryEntity cObj = new CategoryEntity();
		    	cObj.setKey(KeyFactory.stringToKey(scoredDoc.getOnlyField(PiDictEntity.entity_key_property).getText()));
		    	cObj.setCategory_name(scoredDoc.getOnlyField(PiDictEntity.category_name_property).getText());		    	   	
		    	cObj.setCreatedOn(scoredDoc.getOnlyField(PiDictEntity.createdOn_property).getDate());
		    	found.add(cObj);
		    }
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to search " ,e);			
		}
		return found;
	}
}
