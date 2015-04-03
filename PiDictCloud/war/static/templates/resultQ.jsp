<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gae.pidictcloud.entity.QuestionEntity" %>
<%@ page import="com.gae.pidictcloud.util.UtilFn" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
List<QuestionEntity> qResults =  (List<QuestionEntity>)request.getAttribute("qResults");
%>
<table id="resultQ" >
						<tr>
							<th>No</th>
							<th>Question</th>
							<th>Answer</th>
							<th>Date</th>
							<th></th>
						</tr>						
						<c:forEach items="${qResults}" var="qentity" varStatus="counter">  
						<%
							QuestionEntity qObj = (QuestionEntity)pageContext.getAttribute("qentity");						    
						    pageContext.setAttribute("key", KeyFactory.keyToString(qObj.getKey()),PageContext.PAGE_SCOPE);						    
						    pageContext.setAttribute("question", qObj.getQuestion(),PageContext.PAGE_SCOPE);						    
						    pageContext.setAttribute("answer", qObj.getAnswer(),PageContext.PAGE_SCOPE);						    
						    pageContext.setAttribute("isAnswered", qObj.isAnswered(),PageContext.PAGE_SCOPE);						    
						    pageContext.setAttribute("question_short", UtilFn.truncateWords(qObj.getQuestion(),3),PageContext.PAGE_SCOPE);
						    pageContext.setAttribute("answer_short", UtilFn.truncateWords(qObj.getAnswer(),3),PageContext.PAGE_SCOPE);
						    pageContext.setAttribute("date", UtilFn.getDateString(qObj.getCreatedOn(),"dd/MM/yyyy HH:mm"),PageContext.PAGE_SCOPE);
						  %>
						  <tr>
							<td><c:out value="${counter.count}"/></td>
							<td><c:out value="${question_short}"/></td>
							<td id="td${counter.count}"><c:out value="${answer_short}"/></td>
							<td><c:out value="${date}"/></td>
							<td><div class="arrow"></div></td>
						</tr>
						<tr>
							<td colspan="5">
								<h4>Question</h4>
								<ul>								
								<c:out value="${question}"/>
								 </ul>
								<span ondblclick="spanDbClick('span${counter.count}','hidden${counter.count}','td${counter.count}','1')">
								<h4 style="cursor:pointer">Answer</h4>
								<ul>
								<input type="hidden" value="${key}" id="hidden${counter.count}" />
								<span id="span${counter.count}" style="cursor:pointer">								
								<c:out value="${answer}"/>
								</span>								
								 </ul>
								 </span>
								 
							</td>
						</tr>
						</c:forEach> 
						<tr>
							<td colspan="5">
							&nbsp
							</td>
						</tr>
</table>