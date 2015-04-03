<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.gae.pidictcloud.entity.CategoryEntity" %>
<%@ page import="com.gae.pidictcloud.util.UtilFn" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
List<CategoryEntity> cResults =  (List<CategoryEntity>)request.getAttribute("scResults");
%>
<table id="resultC" >
						<tr>
							<th>No</th>
							<th>Category</th>							
							<th>Date</th>
							<th></th>
						</tr>						
						<c:forEach items="${scResults}" var="centity" varStatus="counter">  
						<%
						CategoryEntity cObj = (CategoryEntity)pageContext.getAttribute("centity");						    
						    pageContext.setAttribute("key", KeyFactory.keyToString(cObj.getKey()),PageContext.PAGE_SCOPE);
						    
						    pageContext.setAttribute("category", cObj.getCategory_name(),PageContext.PAGE_SCOPE);						  					    
						    
						    pageContext.setAttribute("category_short", UtilFn.truncateWords(cObj.getCategory_name(),3),PageContext.PAGE_SCOPE);
						   
						    pageContext.setAttribute("date", UtilFn.getDateString(cObj.getCreatedOn(),"dd/MM/yyyy HH:mm"),PageContext.PAGE_SCOPE);
						  %>
						  <tr>
							<td><c:out value="${counter.count}"/></td>							
							<td id="td${counter.count}"><c:out value="${category_short}"/></td>
							<td><c:out value="${date}"/></td>
							<td><div class="arrow"></div></td>
						</tr>
						<tr>
							<td colspan="5">
								<textarea readonly rows="3" cols="50" ><c:out value="${category}#${key}"/></textarea>					
								<span ondblclick="spanDbClick('span${counter.count}','hidden${counter.count}','td${counter.count}','4')">
								<h4 style="cursor:pointer">Category name</h4>
								<ul>
								<input type="hidden" value="${key}" id="hidden${counter.count}" />
								<span id="span${counter.count}" style="cursor:pointer">								
								<c:out value="${category}"/>
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