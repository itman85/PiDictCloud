package com.gae.pidictcloud.entity;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;


public class PhraseEntity extends PiDictEntity{
	 
	 private String phrase;	 
	 private String category;	 
	 private String vimeaning;	 
	 private String enmeaning;
	 private String categoryKey;
	 	 
	public PhraseEntity(){}
	public PhraseEntity(Entity entity) {
		setCategory((String) entity.getProperty(category_property));
		setCategoryKey((String) entity.getProperty(category_key_property));
		setKey(entity.getKey());// default name of key
		setPhrase((String) entity.getProperty(phrase_property));
		setVimeaning((String) entity
				.getProperty(vimeaning_property));
		setEnmeaning((String) entity
				.getProperty(enmeaning_property));
		setCreatedOn((Date) entity.getProperty(createdOn_property));
		
	}
	public Entity createEntity()
	{
		Entity entity = new Entity(EntityFactory.PHRASE_ENTITY_KIND);
		entity.setProperty(phrase_property, phrase);
		entity.setProperty(category_property, category);
		entity.setProperty(category_key_property, categoryKey);
		entity.setProperty(vimeaning_property, vimeaning);
		entity.setProperty(enmeaning_property, enmeaning);
		entity.setProperty(createdOn_property, createdOn);		
		return entity;
	
	}
	
	public Document buildDocument(){
		Document doc = Document.newBuilder()
				.addField(Field.newBuilder().setName(entity_key_property).setText(KeyFactory.keyToString(key)))
			    .addField(Field.newBuilder().setName(phrase_property).setText(phrase))
			    .addField(Field.newBuilder().setName(category_property).setText(category))
			    .addField(Field.newBuilder().setName(category_key_property).setText(categoryKey))
			    .addField(Field.newBuilder().setName(vimeaning_property).setText(vimeaning))
			    .addField(Field.newBuilder().setName(enmeaning_property).setText(enmeaning))
			    .addField(Field.newBuilder().setName(createdOn_property).setDate(createdOn))
			    .build();
		return doc;
	}
	
	public Document updateDocument(Document doc){
		return Document.newBuilder()
				.setId(doc.getId())
				.addField(Field.newBuilder().setName(entity_key_property).setText(KeyFactory.keyToString(key)))
			    .addField(Field.newBuilder().setName(phrase_property).setText(phrase))
			    .addField(Field.newBuilder().setName(category_property).setText(category))
			    .addField(Field.newBuilder().setName(category_key_property).setText(categoryKey))
			    .addField(Field.newBuilder().setName(vimeaning_property).setText(vimeaning))
			    .addField(Field.newBuilder().setName(enmeaning_property).setText(enmeaning))
			    .addField(Field.newBuilder().setName(createdOn_property).setDate(createdOn))
			    .build();
	}
	public String getPhrase() {
		return phrase;
	}
	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getVimeaning() {
		return vimeaning;
	}
	public void setVimeaning(String vimeaning) {
		this.vimeaning = vimeaning;
	}
	public String getEnmeaning() {
		return enmeaning;
	}
	public void setEnmeaning(String enmeaning) {
		this.enmeaning = enmeaning;
	}
	public String getCategoryKey() {
		return categoryKey;
	}
	public void setCategoryKey(String categoryKey) {
		this.categoryKey = categoryKey;
	}
	
	
	 
}
