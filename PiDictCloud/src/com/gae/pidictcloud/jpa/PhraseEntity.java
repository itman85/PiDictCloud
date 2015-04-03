package com.gae.pidictcloud.jpa;

import com.google.appengine.api.datastore.Key;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({    
    @NamedQuery(name="PhraseEntity.getHistoryByStartEndDate",
                query="SELECT P FROM PhraseEntity P WHERE P.createdOn >= :startdate AND P.createdOn <= :enddate"),
}) 
public class PhraseEntity {
	 public static final String HISTORY_QUERY_NAME = "PhraseEntity.getHistoryByStartEndDate";
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Key key;
	 
	 @Basic
	 private String phrase;
	 @Basic
	 private String category;
	 @Basic
	 private String vimeaning;
	 @Basic
	 private String enmeaning;
	 @Basic
	 private Date createdOn;
	 
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
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
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	 
}
