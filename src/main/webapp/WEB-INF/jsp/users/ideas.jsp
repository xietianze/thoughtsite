<html>
<head>
	<title>My Ideas!</title>
    <!-- Load the list js file. -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/users/list.js"></script>
    <meta name="currentPage" content="user" />
</head>
<body>
<div id="internal-pg-col-1">
  <div id='user-profile' style="background: #fff url();border:0px;">
  </div>
  <div id='user-ideas' style="background: #fff url();border:0px;">
  </div>
  <div id='user-projects' style="background: #fff url();border:0px;">
  </div>
  <!-- start pagination -->
  <div id= "pagination" class="ie-right ie-top-mar-20"> 
  </div>
  <!-- close pagination -->  
</div>
<div id="tag-cloud-sec" >
  <div class="tag-cloud-heading">My Tag Cloud 
  </div>
  <div >
    <div id="cloud" class="ie-center">
    </div>
    <div class="ie-right ie-bottom-mar-10 ie-top-mar-20 ie-right-mar-10"> 
      <a href="<%=request.getContextPath()%>/tags/list" class="ie-nm-blu"> See all tags</a>                
    </div>
  </div>
</div>
</body>
</html>