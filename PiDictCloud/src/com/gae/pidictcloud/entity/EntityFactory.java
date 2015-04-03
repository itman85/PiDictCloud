package com.gae.pidictcloud.entity;

public class EntityFactory {
	public static final String PHRASE_ENTITY_KIND = "PhraseEntity";
	public static final String QUESTION_ENTITY_KIND = "QuestionEntity";
	public static final String CATEGORY_ENTITY_KIND = "CategoryEntity";
	
	public PiDictEntity doEntityFactory(String entity_kind){
		if(PHRASE_ENTITY_KIND.equals(entity_kind))
			return new PhraseEntity();
		else if(QUESTION_ENTITY_KIND.equals(entity_kind))
			return new QuestionEntity();
		else if(CATEGORY_ENTITY_KIND.equals(entity_kind))
			return new CategoryEntity();
		
		return null;
		
	}
}
