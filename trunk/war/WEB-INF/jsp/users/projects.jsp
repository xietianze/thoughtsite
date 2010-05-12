<html>
<head>
	<title>My Projects!</title>
    <!-- Load the home js file. -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.url.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/users/list.js"></script>
    <meta name="currentPage" content="user" />
</head>
<body>
  <!-- <div class="ie-overview-heading ie-top-mar-3 ie-right-mar-10" id='listHeading'>Project list</div> -->
  <div id='listProject'>
  </div>
  <div id= "pagination" class="ie-right ie-top-mar-20"> 
  </div>
  <!-- close pagination -->  
</body>
</html>