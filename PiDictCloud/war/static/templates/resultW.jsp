<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gae.pidictcloud.entity.PhraseEntity" %>
<%@ page import="com.gae.pidictcloud.util.UtilFn" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
List<PhraseEntity> wpResults =  (List<PhraseEntity>)request.getAttribute("wpResults");
%>
<table id="resultW" >
						<tr>
							<th>No</th>
							<th>Phrase</th>
							<th>Category</th>
							<th>Vietnamese meaning</th>
							<th>English meaning</th>
							<th>Date</th>
							<th></th>
						</tr>						
						<c:forEach items="${wpResults}" var="wpentity" varStatus="counter">  
						<%
							PhraseEntity wpObj = (PhraseEntity)pageContext.getAttribute("wpentity");						    
						    pageContext.setAttribute("key", KeyFactory.keyToString(wpObj.getKey()),PageContext.PAGE_SCOPE);						    
						    pageContext.setAttribute("phrase", wpObj.getPhrase(),PageContext.PAGE_SCOPE);						   
						    pageContext.setAttribute("category", wpObj.getCategory(),PageContext.PAGE_SCOPE);						    
						    pageContext.setAttribute("vimean", wpObj.getVimeaning(),PageContext.PAGE_SCOPE);						    
						    pageContext.setAttribute("enmean", wpObj.getEnmeaning(),PageContext.PAGE_SCOPE);
						    pageContext.setAttribute("phrase_short", UtilFn.truncateWords(wpObj.getPhrase(),5),PageContext.PAGE_SCOPE);
						    pageContext.setAttribute("category_short", UtilFn.truncateWords(wpObj.getCategory(),5),PageContext.PAGE_SCOPE);
						    pageContext.setAttribute("vimean_short", UtilFn.truncateWords(wpObj.getVimeaning(),3),PageContext.PAGE_SCOPE);
						    pageContext.setAttribute("enmean_short", UtilFn.truncateWords(wpObj.getEnmeaning(),3),PageContext.PAGE_SCOPE);
						    pageContext.setAttribute("date", UtilFn.getDateString(wpObj.getCreatedOn(),"dd/MM/yyyy HH:mm"),PageContext.PAGE_SCOPE);
						  %>
						<tr>
							<td><c:out value="${counter.count}"/></td>
							<td><c:out value="${phrase_short}"/></td>
							<td id="td3W${counter.count}"><c:out value="${category_short}"/></td>
							<td id="td2W${counter.count}"><c:out value="${vimean_short}"/></td>
							<td id="td1W${counter.count}"><c:out value="${enmean_short}"/></td>
							<td><c:out value="${date}"/></td>
							<td><div class="arrow"></div></td>
						</tr>
						<tr>
							<td colspan="7">								
								<ul><c:out value="${phrase}"/></ul>
								<span ondblclick="spanDbClick('span3W${counter.count}','hidden3W${counter.count}','td3W${counter.count}','5')">
								<h4 style="cursor:pointer">Category&nbsp;&nbsp;&nbsp;<a href="javascript:navigate_tabs('h','eighth-tab');"><span style="color:#CC0000;font-weight:bold">Use existed category?</span></a></h4>
								<ul>
								<input type="hidden" value="${key}" id="hidden3W${counter.count}" />
								<span id="span3W${counter.count}" style="cursor:pointer">								
								<c:out value="${category}"/>
								</span>
								</ul>
								</span>						
								<span ondblclick="spanDbClick('span1W${counter.count}','hidden1W${counter.count}','td1W${counter.count}','2')">
								<h4 style="cursor:pointer">English meaning</h4>
								<ul>
								<input type="hidden" value="${key}" id="hidden1W${counter.count}" />
								<span id="span1W${counter.count}" style="cursor:pointer">								
								<c:out value="${enmean}"/>
								</span>
								</ul>
								</span>
								
								<span ondblclick="spanDbClick('span2W${counter.count}','hidden2W${counter.count}','td2W${counter.count}','3')">
								<h4 style="cursor:pointer">VietNam meaning</h4>
								<ul>
								<input type="hidden" value="${key}" id="hidden2W${counter.count}" />
								<span id="span2W${counter.count}" style="cursor:pointer">
								<c:out value="${vimean}"/>
								</span>
								 </ul>
								 </span>
							</td>
						</tr>
						</c:forEach> 
						<tr>
							<td colspan="7">
							&nbsp
							</td>
						</tr>
</table>