<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gae.pidictcloud.entity.PhraseEntity" %>
<%@ page import="com.gae.pidictcloud.util.UtilFn" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
List<PhraseEntity> spResults =  (List<PhraseEntity>)request.getAttribute("spResults");
%>
<table id="result" >
						<tr>
							<th>No</th>
							<th>Phrase</th>
							<th>Category</th>
							<th>Vietnamese meaning</th>
							<th>English meaning</th>
							<th>Date</th>
							<th></th>
						</tr>						
						<c:forEach items="${spResults}" var="wpentity" varStatus="counter">  
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
							<td id="td3SW${counter.count}"><c:out value="${category_short}"/></td>
							<td id="td2SW${counter.count}"><c:out value="${vimean_short}"/></td>
							<td id="td1SW${counter.count}"><c:out value="${enmean_short}"/></td>
							<td><c:out value="${date}"/></td>
							<td><div class="arrow"></div></td>
						</tr>
						<tr>
							<td colspan="7">								
								<ul><c:out value="${phrase}"/></ul>
								<span ondblclick="spanDbClick('span3SW${counter.count}','hidden3SW${counter.count}','td3SW${counter.count}','55')">
								<h4 style="cursor:pointer">Category&nbsp;&nbsp;&nbsp;<a href="javascript:navigate_tabs('h','eighth-tab');"><span style="color:#CC0000;font-weight:bold">Use existed category?</span></a></h4>
								<ul>
								<input type="hidden" value="${key}" id="hidden3SW${counter.count}" />
								<span id="span3SW${counter.count}" style="cursor:pointer">								
								<c:out value="${category}"/>
								</span>
								</ul>
								</span>						
								<span ondblclick="spanDbClick('span1SW${counter.count}','hidden1SW${counter.count}','td1SW${counter.count}','22')">
								<h4  style="cursor:pointer">English meaning</h4>
								<ul>
								<input type="hidden" value="${key}" id="hidden1SW${counter.count}" />
								<span id="span1SW${counter.count}" style="cursor:pointer">								
								<c:out value="${enmean}"/>
								</span>
								</ul>
								</span>
								
								<span ondblclick="spanDbClick('span2SW${counter.count}','hidden2SW${counter.count}','td2SW${counter.count}','33')">
								<h4 style="cursor:pointer">VietNam meaning</h4>
								<ul>
								<input type="hidden" value="${key}" id="hidden2SW${counter.count}" />
								<span id="span2SW${counter.count}" style="cursor:pointer">
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