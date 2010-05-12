<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@page import="com.google.ie.web.controller.WebConstants"%>
<html>
<head>
  <title>Idea Detail</title>
    <!-- Load the list js file. -->
    <script type='text/javascript' src='<%=request.getContextPath()%>/public/js/lib/jquery.limit.js'></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/ideas/show.js"></script>
    <script type="text/javascript" src="http://api.recaptcha.net/js/recaptcha_ajax.js"></script>
    <meta name="currentPage" content="idea" />
</head>
<body>
  <div id="internal-pg-content">
    <c:if test="${'error' eq viewStatus.status}">
      <div class="ie-left-mar-10 ie-top-mar-5 ie-rd" id="errDisplay"><c:out value="${viewStatus.messages['error']}"/></div>
    </c:if>
    <c:if test="${'success' eq viewStatus.status}">
    <c:set var="ideaDetail" value="${viewStatus.data['ideaDetail']}"/>
    <c:set var="origIdea" value="${viewStatus.data['origIdea']}"></c:set>
    <div class="ie-overview-main">
      <c:set var="idea" value="${ideaDetail['idea']}"/>
      <div class="ie-overview-heading">Idea Detail</div>
      <div id="main" class="ie-overview-desc ie-text">
        <span class="ie-right">Created on <fmt:formatDate value="${idea.publishDate}" type="date" pattern="MMMM dd, yyyy z" timeZone="IST"/></span>
        <c:set var="user" value="${ideaDetail.user}"></c:set>
        <h2 class="ie-nm-blu"><strong><c:out value="${idea.title}"/></strong></h2> 
        <img src="<c:out value="${user.thumbnailUrl}"/>" alt="" width="45" height="45" hspace="2" />
        <span class="ie-left-mar-5"><c:out value="${user.displayName}"/></span><br/>
        <c:if test="${'Duplicate' ne idea.status}">
        <div class="ie-top-mar-10 ie-bottom-mar-10 ">
          <!-- Positive Votes -->
          <a href="javascript:void(0);" onclick="ideaDisplay.voteIdea('<c:out value="${idea.key}"/>', true)">
            <img border="0" src="<%=request.getContextPath()%>/public/images/ic7.gif" alt="" width="20" height="20" />
          </a>
           + <span id='pIdea' class="ie-left-mar-5 ie-top-mar-10 ie-right-mar-10"><c:out value="${idea.totalPositiveVotes}"/></span>
          <!-- Negative Votes-->
          <a href="javascript:void(0);" onclick="ideaDisplay.voteIdea('<c:out value="${idea.key}"/>', false)">
            <img border="0" src="<%=request.getContextPath()%>/public/images/ic6.gif" alt="" width="20" height="20" />
          </a>
           - <span id='nIdea' class="ie-left-mar-5 ie-top-mar-10 ie-right-mar-10"><c:out value="${idea.totalNegativeVotes}"/></span>
          <!-- Report abuse -->          
          <img src="<%=request.getContextPath()%>/public/images/ic5.gif" alt="" width="20" height="20" border="0" />
          <span class="ie-left-mar-5 ie-top-mar-10 ie-right-mar-10"><a href="javascript:void(0);"  onclick="ie.reportAbouse(ie.config.IDEAS, '<c:out value='${idea.key}'/>')" class="ie-nm-blu">Report abuse</a></span>
          <!-- Report duplicate -->
          <a href="#" class="ie-nm-blu">
          <img src="<%=request.getContextPath()%>/public/images/ic8.gif" alt="" width="20" height="20" border="0" /></a>
          <span class="ie-left-mar-5 ie-top-mar-10 ie-right-mar-10"><a href="javascript:void(0);" onclick="ideaDisplay.markDupl('<c:out value="${idea.key}"/>')" class="ie-nm-blu">Report duplicate</a></span> 
          <span class="ie-left-mar-5 ie-top-mar-10 ie-right-mar-10"><a href="<c:out value='/projects/showForm/${idea.key}'/>" class="ie-nm-blu">Create Project</a></span>
        </div>
        </c:if>
        <c:if test="${not empty ideaDetail.tags}">
          <div class=" ie-bottom-mar-10">
            <strong>Tags: </strong>
            <c:forEach items="${ideaDetail.tags}" var="tag" varStatus="status">
              <c:url value="/ideas/list" var="tagUrl">
                <c:param name="type" value="tag"/>
                <c:param name="tag" value="${tag.title}"/>
              </c:url>
              <a href="<c:out value='${tagUrl}'/>" class="ie-nm-blu"><c:out value="${tag.title}"/></a><c:if test="${not status.last}">, </c:if>
            </c:forEach>              
          </div>
        </c:if>
        <!-- Check for projects here -->
        <c:if test="${false}">
          <div class="ie-bottom-mar-10">
            <strong>Projects: </strong>
            <c:forEach items="${ideaDetail.projects}" var="project" varStatus="status">
              <c:url value="/projects/list" var="projectUrl">
              </c:url>
              <a href="<c:out value='projectUrl'/>" class="ie-nm-blu"><c:out value="${project.name}"/></a><c:if test="${not status.last}">, </c:if>
            </c:forEach>               
          </div>
        </c:if>
        <c:if test="${not empty idea.description}">
          <div class="ie-top-mar-20"><strong>About idea </strong><br/><c:out value="${idea.description}"/></div>
        </c:if>
        <c:if test="${not empty idea.targetAudience}">
          <div class="ie-top-mar-20"><strong>Target Audience</strong><br/><c:out value="${idea.targetAudience}"/></div>
        </c:if>
        <c:if test="${not empty idea.competition}">
          <div class="ie-top-mar-20"><strong>Competition </strong><br/><c:out value="${idea.competition}"/></div>
        </c:if>
        <c:if test="${not empty idea.monetization}">
          <div class="ie-top-mar-20"><strong>How to Monetize</strong><br/><c:out value="${idea.monetization}"/></div>
        </c:if>
        <c:if test="${not empty idea.ideaSummary}">
          <div id="showOrigSumm" class="ie-top-mar-20"><strong>Idea Originator's Summary </strong><br/><c:out value="${idea.ideaSummary}"/></div>
        </c:if>
        <c:if test="${'Duplicate' eq idea.status}">
        	<div class="ie-top-mar-20"><strong>Duplicate of</strong><br/><a href="<c:out value='/ideas/show/${idea.originalIdeaKey}'/>" class="ie-nm-blu"><c:out value="${origIdea.title}"/></a></div>
        </c:if>
        <!-- Idea originator summary -->  
        <c:if test="${('Duplicate' ne idea.status) and (empty idea.ideaSummary) and (idea.creatorKey == sessionScope['user'].userKey)}">
          <div class="ie-left-mar-10 ie-top-mar-5 ie-rd global-error" id="errOrigSumm" style='display:none'></div>
	        <div id='origSumm'>
	        	<div class="ie-top-mar-20"><strong>Add Idea Originator's Summary:</strong><br/></div>
	            <textarea name="text" cols="130" rows="5"  id="text" ></textarea><br/>
	            <span id="chars"></span> characters left.<br/>
	            <input type="button" name="btnAddOrigSumm" id="btnAddOrigSumm" value="Add" class="ie-button" />
	        </div>
	       </c:if>
      </div>
    </div>
  
    
    <c:if test="${'Duplicate' ne idea.status}">
    <!-- close comment section -->
    <div class="ie-overview-main" style='display:none' id='postComment'>
      <div class="ie-overview-heading ie-top-mar-10">Post Your Comments</div>
      <!-- comment -->
      <div class="ie-left-mar-10 ie-top-mar-5 ie-rd global-error" id="errComment" style='display:none'></div>
      <div class="ie-overview-desc ie-text">
        <ul class="ie-hm">
          <li class="ie-hm">Comment:</li>
          <li class="ie-hm">
            <textarea name="comment" cols="130" rows="5"  id="comment" ></textarea>
          </li>
          <li class="ie-hm">
            <span id="charsLeft"></span> characters left.
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
    </c:if>
    <!--  block 5 -->
    <div class="ie-overview-main">
      <div class="ie-overview-heading ie-top-mar-10  ie-bottom-mar-10"> 
        <div class="ie-left">Comments</div>
        <div class="ie-right ie-right-mar-10"> <a href="javascript:void(0);" id='expandLink' class="link-plus">-</a> </div>
      </div>
      <!-- comment -->
      <div id="commentLayer" style="display:none">
        <div id="commentData">No records found.</div>
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
  </div>
  
  <input type="hidden" value="<c:out value='${idea.key}'/>" id="key">
</body>
</html>