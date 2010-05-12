<html>
<head>
	<title>Tags!</title>
    <!-- Load the home js file. -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.history.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/tags/list.js"></script>
    <meta name="currentPage" content="tag" />
</head>
<body>
<div id="internal-pg-content">
    <!-- start pagination -->
   <div class="ie-clear" >
     <form onsubmit="return false;">
       <input name="tagKeyword" type="text" class="ie-text ie-top" id="tagKeyword" value="Search Tag" size="15" align="top" />
       <a href="javascript:void(0)" id="searchTag"><img style="padding-top:4px" src="<%=request.getContextPath()%>/public/images/go.gif" border="0"/></a>
     </form>
   </div>
  <div id="pagination" class="ie-left ie-width-90 ie-text"> 
    A-Z:
    <a href="#" class="page_no">A</a>
    <a href="#" class="page_no">B</a>
    <a href="#" class="page_no">C</a>
    <a href="#" class="page_no">D</a>
    <a href="#" class="page_no">E</a>
    <a href="#" class="page_no">F</a>
    <a href="#" class="page_no">G</a>
    <a href="#" class="page_no">H</a>
    <a href="#" class="page_no">I</a>
    <a href="#" class="page_no">J</a>
    <a href="#" class="page_no">K</a>
    <a href="#" class="page_no">L</a>
    <a href="#" class="page_no">M</a>
    <a href="#" class="page_no">N</a>
    <a href="#" class="page_no">O</a>
    <a href="#" class="page_no">P</a>
    <a href="#" class="page_no">Q</a>
    <a href="#" class="page_no">R</a>
    <a href="#" class="page_no">S</a>
    <a href="#" class="page_no">T</a>
    <a href="#" class="page_no">U</a>
    <a href="#" class="page_no">V</a>
    <a href="#" class="page_no">W</a>
    <a href="#" class="page_no">X</a>
    <a href="#" class="page_no">Y</a>
    <a href="#" class="page_no">Z</a>
  </div>
      <!-- close pagination -->
  <div id="cloud-main" class="ie-clear">
  </div>
</div>
</body>
</html>