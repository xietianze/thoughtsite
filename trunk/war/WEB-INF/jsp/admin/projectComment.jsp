<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
	<title>Welcome!</title>
    <!-- Load the home js file. -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.limit.js"></script>
	<script type='text/javascript' src='<%=request.getContextPath()%>/public/js/lib/jquery.autocomplete.js'></script>
    <link href="<%=request.getContextPath()%>/public/css/lib/jquery.autocomplete.css" type="text/css" rel="stylesheet" />
</head>
<body>
  <!-- start body content  -->
      <div class="ie-overview-heading ie-top-mar-10">Comments
      </div>
      <!-- comment -->
      <div id="commentLayer">
	      <c:if test="${'error' eq viewStatus.status}">
	      	<div class="ie-left-mar-10 ie-rd" id="errDisplay"><c:out value="${viewStatus.messages['error']}"/></div>
	      </c:if>
	      <c:if test="${'success' eq viewStatus.status}">
	      	<c:set var="projectDetail" value="${viewStatus.data['project']}"/>
	      	<c:set var="user" value="${projectDetail.user}"></c:set>
	      	<c:set var="comment" value="${viewStatus.data['projComment']}"/>
	        <div id="commentData">
		        <div class="ie-overview-desc ie-text">
		        	<c:out value="${comment.text}"/>
		        	<br/><div class="ie-sm-lg ie-top-mar-10 ie-left" style=" width:auto;">Posted by:
		        	<c:out value="${user.displayName}"/> | 
		        	<fmt:formatDate value="${comment.createdOn}" type="date" timeStyle="full" 
                  					dateStyle="long" timeZone="IST" pattern="MMMM dd, yyyy z"/>
		        	</div>
	        	</div> <br/><br/>
	        <!-- comment -->
	        	<div style="clear:both"></div>
	      	</div>
	      <!-- close-->
	      </c:if>
    </div>
  <!-- end body content  -->
</body>
</html>