package com.gae.pidictcloud.entity;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.search.Document;

public abstract  class PiDictEntity {
	public static final String phrase_property = "phrase";
	public static final String category_property = "category";
	public static final String vimeaning_property = "vimeaning";
	public static final String enmeaning_property = "enmeaning";
	public static final String createdOn_property = "createdOn";
	public static final String category_key_property = "categoryKey";
	
	public static final String question_property = "question";
	public static final String answer_property = "answer";
	public static final String isAnswered_property = "isAnswered";
	
	public static final String entity_key_property = "entity_key";//in document
	
	public static final String category_name_property = "category_name";
	
	protected Key key;
	protected Date createdOn;
	
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	public abstract  Entity createEntity();
	public abstract  Document buildDocument();
	public abstract  Document updateDocument(Document doc);
	
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
}
