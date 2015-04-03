package com.gae.pidictcloud.entity;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;

public class CategoryEntity extends PiDictEntity{

	 private String category_name;
	 
	 public CategoryEntity(){}
	 
	public CategoryEntity(Entity entity) {
		setCategory_name((String) entity.getProperty(category_name_property));
		setKey(entity.getKey());// default name of key
		setCreatedOn((Date) entity.getProperty(createdOn_property));
	}
	
	@Override
	public Entity createEntity() {
		// TODO Auto-generated method stub
		Entity entity = new Entity(EntityFactory.CATEGORY_ENTITY_KIND);
		entity.setProperty(category_name_property, category_name);		
		entity.setProperty(createdOn_property, createdOn);		
		return entity;
	}

	@Override
	public Document buildDocument() {
		// TODO Auto-generated method stub
		Document doc = Document.newBuilder()
				.addField(Field.newBuilder().setName(entity_key_property).setText(KeyFactory.keyToString(key)))
			    .addField(Field.newBuilder().setName(category_name_property).setText(category_name))			    
			    .addField(Field.newBuilder().setName(createdOn_property).setDate(createdOn))
			    .build();
		return doc;
	}

	@Override
	public Document updateDocument(Document doc) {
		// TODO Auto-generated method stub
		return Document.newBuilder()
				.setId(doc.getId())
				.addField(Field.newBuilder().setName(entity_key_property).setText(KeyFactory.keyToString(key)))
			    .addField(Field.newBuilder().setName(category_name_property).setText(category_name))			    
			    .addField(Field.newBuilder().setName(createdOn_property).setDate(createdOn))
			    .build();
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

}
