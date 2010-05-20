<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
  <title>Search</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.url.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/search/list.js"></script>
  <meta name="currentPage" content="search" />
</head>
<body>
  <div id="internal-pg-col-1">
	  <div id='search-ideas' style="background: #fff url();border:0px;">
	  </div>
	  <div id='search-projects' style="background: #fff url();border:0px;">
	  </div>
	  <!-- start pagination -->
	  <div style='clear:both'></div>
	  <div id= "pagination" class="ie-right ie-top-mar-20"> 
	  </div>
	  <!-- close pagination -->
	</div>  
	<div id="tag-cloud-sec" >
	  <div class="tag-cloud-heading">Narrow your Search  
	  </div>
	  <div id='category-filter'>
	  </div> 
	</div>
</body>
</html>