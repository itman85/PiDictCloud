package com.gae.pidictcloud.entity;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;

public class QuestionEntity extends PiDictEntity{	  
	private String question;	
	private String answer;	
	private boolean isAnswered;	
	
	public QuestionEntity(){}
	public QuestionEntity(Entity entity){
		setQuestion((String) entity
				.getProperty(PiDictEntity.question_property));
		setAnswer((String) entity
				.getProperty(PiDictEntity.answer_property));
		setAnswered((Boolean) entity
				.getProperty(PiDictEntity.isAnswered_property));
		setKey(entity.getKey());// default name of key
		setCreatedOn((Date) entity
				.getProperty(PiDictEntity.createdOn_property));		
	}
	
	public Entity createEntity()
	{
		Entity entity = new Entity(EntityFactory.QUESTION_ENTITY_KIND);
		entity.setProperty(question_property, question);
		entity.setProperty(answer_property, answer);
		entity.setProperty(isAnswered_property, isAnswered);		
		entity.setProperty(createdOn_property, createdOn);
		return entity;
	
	}	
	
	public Document buildDocument(){
		Document doc = Document.newBuilder()
				.addField(Field.newBuilder().setName(entity_key_property).setText(KeyFactory.keyToString(key)))
			    .addField(Field.newBuilder().setName(question_property).setText(question))
			    .addField(Field.newBuilder().setName(answer_property).setText(answer))			    
			    .addField(Field.newBuilder().setName(createdOn_property).setDate(createdOn))
			    .build();
		return doc;
	}
	
	public Document updateDocument(Document doc){
		return Document.newBuilder()
		 	.setId(doc.getId())
			.addField(Field.newBuilder().setName(entity_key_property).setText(KeyFactory.keyToString(key)))
		    .addField(Field.newBuilder().setName(question_property).setText(question))
		    .addField(Field.newBuilder().setName(answer_property).setText(answer))			    
		    .addField(Field.newBuilder().setName(createdOn_property).setDate(createdOn))
		    .build();
	}

	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public boolean isAnswered() {
		return isAnswered;
	}
	public void setAnswered(boolean isAnswered) {
		this.isAnswered = isAnswered;
	}
	
	 
}
