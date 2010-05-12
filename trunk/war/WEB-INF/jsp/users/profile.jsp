<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@page import="com.google.ie.web.controller.WebConstants"%>
<html>
<head>
	<title>User profile!</title>
    <!-- Load the home js file. -->
    
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.url.js"></script>
    <meta name="currentPage" content="" />
</head>
<body>
 <div id="internal-pg-content">
 	<c:if test="${'error' eq viewStatus.status}">
      <div class="ie-left-mar-10 ie-top-mar-5 ie-rd" id="errDisplay"><c:out value="${viewStatus.messages['error']}"/></div>
    </c:if>
    <c:if test="${'success' eq viewStatus.status}">
    <c:set var="user" value="${viewStatus.data['user']}"/>
         <!-- User Profile 1 -->
         <div class="ie-profile-desc ie-text" id="mid-wrapper" >
           <div class="ie-left" ><strong><c:out value="${user.displayName}"/>'s profile</strong><br /><br />
           </div>
           <div>
	           <br /><br />
	           Name:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strong><c:out value="${user.displayName}"/></strong><br /><br /> 
	           Status:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${user.status}"/> <br /><br />
	           Reputation Points:&nbsp;&nbsp;<c:out value="${user.reputationPoints}"/> <br /><br />
	           Joined on:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${user.createdOn}"/><br /><br />
           </div>
         </div>
         <!-- start blank div -->
         <div class="ie-clear ie-last-blank-space" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
         </div>
   	  	<!-- close blank div -->
   	  </c:if>
  </div>
</body>
</html>