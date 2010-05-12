<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD XHTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>India incubator program - <decorator:title default="Welcome!" /></title>
    <meta http-equiv=Content-Type content="text/html; charset=iso-8859-1"/>
     <!-- for dev-iip 
     <meta name="google-site-verification" content="Ef3KCtSi-DqVSVq-Z3bf_ZaLrLuSw6cWrdbNwAcAGCo" />-->
    <!-- for qa-iip 
    <meta name="google-site-verification" content="Ef3KCtSi-DqVSVq-Z3bf_ZaLrLuSw6cWrdbNwAcAGCo" /> -->
     <!-- for staging-iip -->
    <meta name="google-site-verification" content="Ef3KCtSi-DqVSVq-Z3bf_ZaLrLuSw6cWrdbNwAcAGCo" /> 
    
    
    <link media="screen, tv, projection, print" href="<%=request.getContextPath()%>/public/css/lib/jquery-ui.css" type="text/css" rel="stylesheet" />
    <link media="screen, tv, projection, print" href="<%=request.getContextPath()%>/public/css/common.css" type="text/css" rel="stylesheet" />
    <link media="screen, tv, projection, print" href="<%=request.getContextPath()%>/public/css/commom-id.css" type="text/css" rel="stylesheet" />

    <!-- Load the Google AJAX API Loader -->
    <script type="text/javascript" src="http://www.google.com/jsapi"></script>

    <!-- Load the Google Friend Connect javascript library and jquery library. -->
    <script type="text/javascript">
      google.load("jquery", "1.3.2");
    </script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery-ui.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.cookie.js"></script>
    <!-- Load the common js library. -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/ie.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/ie.login.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/ie.paging.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/ie.tagcloud.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/ie.template.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/common.js"></script>
    <decorator:head />
    <c:if test="${ null ne sessionScope.loginError}">
	    <script> alert('<c:out value="${sessionScope.loginError}" />')</script>
	    <c:remove var="loginError" scope="session" />
    </c:if>
  </head>
  <body>
    <div id="container" >
      <div>
        <%@ include file="/WEB-INF/includes/header.jsp"%>
        <%@ include file="/WEB-INF/includes/navigation.jsp"%>
        <div id="wrapper">
          <decorator:body/>
        </div>
        <%@ include file="/WEB-INF/includes/footer.jsp"%>
      </div>
    </div>
    <!--  to show dialog boxes -->
    <div id="dialog" title="Dialog Title" style="display: none;">
    </div>
  </body>
</html>
    