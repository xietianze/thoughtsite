<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
<head>
  <title>Activate User!</title>
    <!-- Load the list js file. -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/users/activate.js"></script>
    <c:if test="${'success' eq viewStatus.status}">
    <c:set var="projectDetail" value="${viewStatus.data['project']}"/>
    <c:set var="project" value="${projectDetail['project']}"/>
      <script>
        var PROJECT_KEY = "<c:out value='${project.key}'/>";
      </script>
    </c:if>
</head>
<body>
  <div id="internal-pg-content">
    <!-- message-->
    <div class="ie-cent-win ie-text " id='activateLogin' style=''>You need to join/login before activating. Please 
    <a href='javascript:void(0);' onclick="ie.Login.showLoginPopup();" class="ie-nm-blu">Click here</a> to join/login.
    </div>
  </div>
</body>
</html>