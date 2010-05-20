<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@page import="com.google.ie.web.controller.WebConstants"%>
<html>
<head>
  <title>Project Detail</title>
    <!-- Load the list js file. -->
    <script type='text/javascript' src='<%=request.getContextPath()%>/public/js/lib/jquery.limit.js'></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/projects/show.js"></script>
    <script type="text/javascript" src="http://api.recaptcha.net/js/recaptcha_ajax.js"></script>
    <meta name="currentPage" content="project" />
</head>
<body>
  <div id="internal-pg-content">
    <c:if test="${'error' eq viewStatus.status}">
      <div class="ie-left-mar-10 ie-rd" id="errDisplay"><c:out value="${viewStatus.messages['error']}"/></div>
    </c:if>
    <c:if test="${'success' eq viewStatus.status}">
    <c:set var="projectDetail" value="${viewStatus.data['project']}"/>
    <div class="ie-overview-main">
	    <c:set var="project" value="${projectDetail['project']}"/>
	    <c:if test="${'Deleted' eq project.status}">
        <span class="global-error">The project you want to view is deleted by site Administrator.</span><br /><br />
	    </c:if>
      <c:if test="${'Deleted' != project.status}">
	    	<c:set var="user" value="${projectDetail.user}"></c:set>
    		<c:set var="creation_date" value="${project.createdOn}" />
    		
              <div class="ie-overview-desc ie-text">
              <!-- discreption -->
                <div class="ie-bottom-mar-10">
	                <span class="ie-left-mar-5 ie-top-mar-10 ie-right-mar-10"><h2><strong class="blu-heading"><c:out value="${project.name}"/></strong></h2></span>	                
	                <span class="ie-top-mar-10 ie-right-mar-10" id='editIdea'  <c:if test="${false eq projectDetail.projectEditable}">style='display:none'</c:if>><a href="<c:out value='/projects/editProject/${project.key}'/>" class="ie-nm-blu" >Edit Project</a></span>
	                
	            </div>
	            <div class="ie-bottom-mar-10">
	            <c:if test="${not empty project.ideaKey}">
	            	Idea: <a href="<c:out value='/ideas/show/${project.ideaKey}'/>"  class="ie-nm-blu"><c:out value="${projectDetail.ideaTitle}"/> </a><br />
	            </c:if>
	            <c:if test="${empty project.ideaKey}">
	            	<span class="global-error">The idea related to this project was either deleted or marked objectionable by site Administrator.</span><br /><br />
	            </c:if>
	                <!-- Owner:   <a href="<c:out value='/users/profile/${user.userKey}'/>" class="ie-nm-blu"><c:out value="${user.displayName}"/></a><br />
	                 -->
	                Created on: <fmt:formatDate value="${creation_date}" type="date" timeStyle="full" 
                  					dateStyle="long" timeZone="IST" pattern="MMMM dd, yyyy z"/> 
	                 <!--<c:out value="${project.createdOn}"/>--><br />
	                  <br />
	                  <strong>Description</strong><br />              
	                   <c:out value="${project.description}"/><br /><br />
	                  <strong>Developers (<c:out value="${projectDetail.developerCount}"/>)</strong><br />
	                 <c:if test="${not empty projectDetail.developers}">
	                 <c:set var="count" value="${projectDetail.developerCount}"></c:set>
	                 <c:set var="counter" value="0"></c:set>
			   		 <c:forEach items="${projectDetail.developers}" var="developer" >
			   		 <c:set var="counter" value="${counter + 1}"></c:set>
			   		 <c:if test="${not empty developer.userKey}">
			   		  <a href="<c:out value='/users/profile/${developer.userKey}'/>" class="ie-nm-blu"><c:out value="${developer.name}"/></a>
			   		 </c:if>
			   		 <c:if test="${empty developer.userKey}">
			   		  <c:out value="${developer.name}"/>
			   		 </c:if>	
			   		 <c:if test="${count gt counter}">
			   		 ,
			   		 </c:if>		   		 			   		 
	                  </c:forEach>
	                  </c:if>
                 </div>
                  <br />
               </div> 
            </div>
    <!-- close comment section -->
    <div class="ie-overview-main" style='display:none' id='postComment'>
      <div class="ie-overview-heading ie-top-mar-10">Post Your Comments</div>
      <!-- comment -->
      <div class="ie-left-mar-10 ie-top-mar-5 ie-rd" id="errComment"></div>
      <div class="ie-overview-desc ie-text">
        <ul class="ie-hm">
          <li class="ie-hm">Comment:</li>
          <li class="ie-hm">
            <textarea name="comment" cols="130" rows="5"  id="comment" ></textarea>
          </li>
          <li class="ie-hm">
            <div id="charsLeft"></div> chars left.
          </li>
          <li class="ie-hm">
            <input type="button" name="btnPostComment" id="btnPostComment" value="Post" class="ie-button" />
          </li>
        </ul>
      
        <div class="divider"> &nbsp;&nbsp;
        </div>
      </div>
      <!-- close-->
    </div>
    <!--  block 5 -->
    <div class="ie-overview-main">
      <div class="ie-overview-heading ie-top-mar-10  ie-bottom-mar-10"> 
        <div class="ie-left">Comments</div>
        <div class="ie-right ie-right-mar-10"> <a href="javascript:void(0);" id='expandLink' class="link-plus">-</a> </div>
      </div>
      <!-- comment -->
      <div id="commentLayer" style="display:none">
        <div id="commentData"></div>
        <!-- comment -->
        <div id= "pagination" class="ie-right"></div>
        <div style="clear:both"></div>
      </div>
      <!-- close-->
    </div>
    <!-- start pagination -->
    <!-- close pagination -->
    <br />
    </c:if>
	</c:if>
  </div>
  
  <input type="hidden" value="<c:out value='${idea.key}'/>" id="ideaKey">
  <input type="hidden" value="<c:out value='${project.key}'/>" id="projectKey">
</body>
</html>