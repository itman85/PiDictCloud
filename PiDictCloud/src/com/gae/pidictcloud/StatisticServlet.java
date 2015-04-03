package com.gae.pidictcloud;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gae.pidictcloud.entity.EntityFactory;
import com.gae.pidictcloud.entity.PhraseEntity;
import com.gae.pidictcloud.entity.PiDictEntity;
import com.gae.pidictcloud.fts.CategoryIndex;
import com.gae.pidictcloud.fts.PhraseIndex;
import com.gae.pidictcloud.fts.QuestionIndex;
import com.gae.pidictcloud.util.Constant;
import com.gae.pidictcloud.util.UtilFn;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.RawValue;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class StatisticServlet extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {		
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String accessCode = req.getParameter("code");
		if (Constant.ACCESS_CODE.equals(accessCode)) {
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			if(req.getServletPath().equals(Constant.GET_STATISTIC_PHRASES_SERVLETPATH)){
				JSONObject resJObj = new JSONObject();//this json obj for response				
				JSONArray listPhraseJObjs = new JSONArray();
				String resStatus = "a";
				try {
					SimpleDateFormat dateformat = new SimpleDateFormat(
							"yyyy-MM-dd");
					String strStartDate = URLDecoder.decode(
							req.getParameter("startdate"), "UTF-8").trim();
					String strEndDate = URLDecoder.decode(
							req.getParameter("enddate"), "UTF-8").trim();
					Date startDate = null;
					Date endDate = null;
					Filter dateFilter =null;
					Filter maxFilter = null;
					Filter minFilter = null;
					if(!"0".equals(strStartDate))
					{
						startDate = dateformat.parse(strStartDate);
						minFilter = new FilterPredicate(
								PiDictEntity.createdOn_property,
								FilterOperator.GREATER_THAN_OR_EQUAL, startDate);
					}					
					if(!"1".equals(strEndDate))
					{
						endDate = dateformat.parse(strEndDate);
						maxFilter = new FilterPredicate(
								PiDictEntity.createdOn_property,
								FilterOperator.LESS_THAN_OR_EQUAL, endDate);
					}
					
					if(minFilter !=null && maxFilter!=null){
						dateFilter = CompositeFilterOperator.and(minFilter,	maxFilter);
					}else if(minFilter !=null){
						dateFilter = minFilter;
					}else if(maxFilter !=null){
						dateFilter = maxFilter;
					}
					
					Query q = new Query(EntityFactory.PHRASE_ENTITY_KIND)
							.addProjection(new PropertyProjection(PiDictEntity.createdOn_property, Date.class))
							.addProjection(new PropertyProjection(PiDictEntity.phrase_property, String.class))
							.setFilter(dateFilter);
							
					PreparedQuery pq = datastore.prepare(q);					
					TreeMap<String, ArrayList<String>> tmDatePhrase = new TreeMap<String, ArrayList<String>>();	
					String dateTemp;
					for (Entity result : pq.asIterable()) {
						Date date = (Date) result.getProperty(PiDictEntity.createdOn_property);
						String phrase = (String) result.getProperty(PiDictEntity.phrase_property);
						dateTemp = UtilFn.getDateString(date, "yyyy/MM/dd");
						if(tmDatePhrase.containsKey(dateTemp))
						{
							tmDatePhrase.get(dateTemp).add(UtilFn.truncateWords(phrase, 10));
						}else{
							ArrayList<String> arrPhrases = new ArrayList<String>();
							arrPhrases.add(UtilFn.truncateWords(phrase, 10));
							tmDatePhrase.put(dateTemp, arrPhrases);
						}						
					}
					
					for (Map.Entry<String, ArrayList<String>> entry : tmDatePhrase.entrySet()) {
						
						JSONObject phraseJsonObj = new JSONObject();
						phraseJsonObj.put("phraseslist", entry.getValue());
						phraseJsonObj.put("date", entry.getKey());
						listPhraseJObjs.put(phraseJsonObj);						
					}					
					if(tmDatePhrase.size()==0)
						resStatus = "Not found any phrases in this period of time!";					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					listPhraseJObjs = new JSONArray();
					resStatus = e.getMessage();
				}finally{
					try {
						resJObj.put("phrasedata", listPhraseJObjs);
						resJObj.put("message", resStatus);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					resp.setContentType("text/html");
					resp.setCharacterEncoding("utf-8");
					resp.getWriter().println(resJObj.toString());//send response with json string to client						
				}
			}			
		}else {
			resp.setContentType("text/html");
			resp.setCharacterEncoding("utf-8");
			resp.getWriter().println(Constant.ACCESS_DENIED);
		}
	}

}
