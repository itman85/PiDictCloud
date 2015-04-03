package com.gae.pidictcloud.fts;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gae.pidictcloud.entity.PhraseEntity;
import com.gae.pidictcloud.entity.PiDictEntity;
import com.gae.pidictcloud.util.Constant;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;


public class PhraseIndex {
	
	private static final Index INDEX = SearchServiceFactory.getSearchService()
		      .getIndex(IndexSpec.newBuilder().setName("phrase_index"));
	private static final Logger LOG = Logger.getLogger(
			PhraseIndex.class.getName());
	
	public static String IndexingDocument(Document doc)
	{
		try {
			LOG.info("Adding document:\n" + doc.toString());
			INDEX.put(doc);			
			return "Indexing phrase OK";
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to add " + doc, e);
			return "Indexing phrase error " + e.getMessage();
		}
	}
	
	public static String Delete(String [] docIds)
	{
		try {
			LOG.info("Detete index" );			
			INDEX.delete(docIds);
			return "Detete phrase OK";
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to delete ", e);
			return "delete phrase error " + e.getMessage();
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
			return "Update indexing phrase OK";
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to update " ,e);
			return "Update indexing phrase error " + e.getMessage();
		}
	}
	
	public static String UpdateIndexingDocumentsForCategoryField(String categoryKey,String newCategory)
	{
		try {
			Query query = Query.newBuilder().build(PiDictEntity.category_key_property+":"+categoryKey);
			LOG.info("Sending query " + query);
			Results<ScoredDocument> results = INDEX.search(query);
			ArrayList<Document> listUpdatedDoc = new ArrayList<Document>();
			for (ScoredDocument scoredDoc : results) {
				 Document doc = Document.newBuilder()
					.setId(scoredDoc.getId())
					.addField(Field.newBuilder().setName(PiDictEntity.entity_key_property).setText(scoredDoc.getOnlyField(PiDictEntity.entity_key_property).getText()))
				    .addField(Field.newBuilder().setName(PiDictEntity.phrase_property).setText(scoredDoc.getOnlyField(PiDictEntity.phrase_property).getText()))
				    .addField(Field.newBuilder().setName(PiDictEntity.category_property).setText(newCategory))//update new category for this document
				    .addField(Field.newBuilder().setName(PiDictEntity.category_key_property).setText(scoredDoc.getOnlyField(PiDictEntity.category_key_property).getText()))
				    .addField(Field.newBuilder().setName(PiDictEntity.vimeaning_property).setText(scoredDoc.getOnlyField(PiDictEntity.vimeaning_property).getText()))
				    .addField(Field.newBuilder().setName(PiDictEntity.enmeaning_property).setText(scoredDoc.getOnlyField(PiDictEntity.enmeaning_property).getText()))
				    .addField(Field.newBuilder().setName(PiDictEntity.createdOn_property).setDate(scoredDoc.getOnlyField(PiDictEntity.createdOn_property).getDate()))
				    .build();
				 listUpdatedDoc.add(doc);				
			}
			if(listUpdatedDoc.size()>0)
				 INDEX.put(listUpdatedDoc);	
			
			return "Update indexing phrase OK";
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to update " ,e);
			return "Update indexing phrase error " + e.getMessage();
		}
	}
	
	public static List<PhraseEntity> FullTextSearch(String searchPhrase, String[] searchFields){
		List<PhraseEntity> found = new ArrayList<PhraseEntity>();
		try {
			String plural = "";
			if(searchPhrase.length()>0 && searchPhrase.charAt(0)=='"' && searchPhrase.charAt(searchPhrase.length()-1)=='"')
				plural = "~";
			else{
				if(searchPhrase.indexOf(' ')==-1 || (searchFields.length>1 && searchFields.length<4)){//search on multiple fields only match for the entire phrase
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
				}else if("2".equals(field)){
					queryStr += PiDictEntity.phrase_property+":"+plural+searchPhrase+ (i==searchFields.length?"":" OR ");
				}else if("3".equals(field)){
					queryStr += PiDictEntity.category_property+":"+plural+searchPhrase+(i==searchFields.length?"":" OR ");
				}else if("4".equals(field)){
					queryStr += PiDictEntity.enmeaning_property+":"+plural+searchPhrase+(i==searchFields.length?"":" OR ");
				}else if("5".equals(field)){
					queryStr += PiDictEntity.vimeaning_property+":"+plural+searchPhrase+(i==searchFields.length?"":" OR ");
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
		    	PhraseEntity pObj = new PhraseEntity();
		    	pObj.setKey(KeyFactory.stringToKey(scoredDoc.getOnlyField(PiDictEntity.entity_key_property).getText()));
		    	pObj.setPhrase(scoredDoc.getOnlyField(PiDictEntity.phrase_property).getText());		    	
		    	pObj.setCategory(scoredDoc.getOnlyField(PiDictEntity.category_property).getText());
		    	pObj.setEnmeaning(scoredDoc.getOnlyField(PiDictEntity.enmeaning_property).getText());
		    	pObj.setVimeaning(scoredDoc.getOnlyField(PiDictEntity.vimeaning_property).getText());
		    	pObj.setCreatedOn(scoredDoc.getOnlyField(PiDictEntity.createdOn_property).getDate());
		    	found.add(pObj);
		    }
		} catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Failed to search " ,e);			
		}
		return found;
	}
}
