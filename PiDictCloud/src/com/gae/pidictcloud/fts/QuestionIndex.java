package com.gae.pidictcloud.fts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

public class QuestionIndex {
	private static final Index INDEX = SearchServiceFactory.getSearchService()
		      .getIndex(IndexSpec.newBuilder().setName("question_index"));
	private static final Logger LOG = Logger.getLogger(
			QuestionIndex.class.getName());
	
	public static String IndexingDocument(Document doc)
	{
		try {
			LOG.info("Adding document:\n" + doc.toString());			
			INDEX.put(doc);						
			return "Indexing question OK";
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to add " + doc, e);
			return "Indexing question error " + e.getMessage();
		}
	}
	
	public static String Delete(String [] docIds)
	{
		try {
			LOG.info("Detete index" );			
			INDEX.delete(docIds);
			return "Detete question OK";
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to delete ", e);
			return "delete question error " + e.getMessage();
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
			return "Update indexing question OK";
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to update " ,e);
			return "Update indexing question error " + e.getMessage();
		}
	}
	
	public static List<QuestionEntity> FullTextSearch(String searchPhrase, String[] searchFields){
		List<QuestionEntity> found = new ArrayList<QuestionEntity>();
		try {
			String plural = "";
			if(searchPhrase.length()>0 && searchPhrase.charAt(0)=='"' && searchPhrase.charAt(searchPhrase.length()-1)=='"')
				plural = "~";
			else{
				if(searchPhrase.indexOf(' ')==-1 || searchFields.length==2){
					plural = "~";
					searchPhrase = "\""+searchPhrase+"\"";					
				}
			}
			String queryStr = "";
			int i = 1;
			for (String field : searchFields){
				if("1".equals(field)){
					queryStr += plural + searchPhrase;
					break;
				}else if("6".equals(field)){
					queryStr += PiDictEntity.question_property+":"+plural+searchPhrase+ (i==searchFields.length?"":" OR ");
				}else if("7".equals(field)){
					queryStr += PiDictEntity.answer_property+":"+plural+searchPhrase+(i==searchFields.length?"":" OR ");
				}	
				i++;
			}
			/*SortOptions sortOptions = SortOptions.newBuilder()
		            .addSortExpression(SortExpression.newBuilder()
		                .setExpression(PiDictEntity.createdOn_property)
		                .setDirection(SortExpression.SortDirection.DESCENDING)
		                .setDefaultValue(""))
		            .setLimit(1000)
		            .build();*/
			QueryOptions options = QueryOptions.newBuilder()
				    .setLimit(Constant.SEARCH_RESULT_LIMIT)				    
				    /*.setSortOptions(sortOptions)*/				    
				    .build();
			Query query = Query.newBuilder().setOptions(options).build(queryStr);
			LOG.info("Sending query " + query);
		    Results<ScoredDocument> results = INDEX.search(query);
		    for (ScoredDocument scoredDoc : results) {
		    	QuestionEntity qObj = new QuestionEntity();
		    	qObj.setKey(KeyFactory.stringToKey(scoredDoc.getOnlyField(PiDictEntity.entity_key_property).getText()));
		    	qObj.setQuestion(scoredDoc.getOnlyField(PiDictEntity.question_property).getText());
		    	qObj.setAnswer(scoredDoc.getOnlyField(PiDictEntity.answer_property).getText());		    	
		    	qObj.setCreatedOn(scoredDoc.getOnlyField(PiDictEntity.createdOn_property).getDate());
		    	found.add(qObj);
		    }
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to search " ,e);			
		}
		return found;
	}
}
