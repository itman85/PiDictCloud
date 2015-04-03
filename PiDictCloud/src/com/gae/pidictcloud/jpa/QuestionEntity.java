package com.gae.pidictcloud.jpa;
import com.google.appengine.api.datastore.Key;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class QuestionEntity {
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Key key;
	 
	 private String question;
	 private String answer;
	 private boolean isAnswer;
	 private Date createdOn;
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
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
	public boolean isAnswer() {
		return isAnswer;
	}
	public void setAnswer(boolean isAnswer) {
		this.isAnswer = isAnswer;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	 
}
