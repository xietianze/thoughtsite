<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
<head>
  <title>Project Listing!</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.limit.js"></script>
    <script type='text/javascript' src='<%=request.getContextPath()%>/public/js/lib/jquery.autocomplete.js'></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/admin/projects.js"></script>
    <link href="<%=request.getContextPath()%>/public/css/lib/jquery.autocomplete.css" type="text/css" rel="stylesheet" />
    <meta name="currentPage" content="admin" />
</head>
<body>
  <div id="ie-adm-tab-menu">
        <div style="width:20px; height: 10px; float:left; background-color:#fff; background-image:url(<%=request.getContextPath()%>/images/tab-bg.gif)" > &nbsp;</div> 
        <div class="ie-adm-tabmenu-spacer"> &nbsp;</div>
        <div><a class="ie-adm-tabmenu-items" href="/admin/action"> Action Items</a></div> 
        <div class="ie-adm-tabmenu-spacer"> &nbsp;</div> 
        <div><a class="ie-adm-tabmenu-items-selected" href="/admin/ideas"> Ideas</a></div>
        <div class="ie-adm-tabmenu-spacer"> &nbsp;</div>
        <div><a class="ie-adm-tabmenu-items" href="<%=request.getContextPath()%>/admin/users"> Users</a></div>  
        <div class="ie-adm-tabmenu-spacer"> &nbsp; </div>
        <div><a class="ie-adm-tabmenu-items" href="/admin/projects"> Projects</a></div> 
  </div>
  <div id="internal-pg-content">
    <c:if test="${'error' eq viewStatus.status}">
      <div class="ie-left-mar-10 ie-top-mar-5 ie-rd" id="errDisplay"><c:out value="${viewStatus.messages['error']}"/></div>
    </c:if>
    <c:if test="${'success' eq viewStatus.status}">
    <c:set var="projects" value="${viewStatus.data['projects']}"/>
	  <div id='listData'>
  		</div>
	  <div id= "pagination" class="ie-right ie-top-mar-20"> 
  	  </div>
  		<!-- close pagination -->  
    </c:if>
  </div>
      
    <br />
</body>
</html>