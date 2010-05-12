<html>
<head>
	<title>Ideas!</title>
    <!-- Load the home js file. -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.url.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/ideas/list.js"></script>
    <meta name="currentPage" content="idea" />
</head>
<body>
<div id="internal-pg-col-1">
  <div class="ie-overview-heading ie-top-mar-10 ie-bottom-mar-10" id='listHeading'>
    <div>Idea list</div>
    <div class="ie-right ie-right-mar-10 ie-top-neg-mar" id='sortIdeas'>
      Sort <img alt="" src="<%=request.getContextPath()%>/public/images/arr-up.gif" width="8" height="5" border="0"/>
      <div>
        <ul id="sort-dropdown">
            <li><a id='sortDate' href="#" class="ie-nm-blu">Submission date</a></li>
            <li><a id='sortVote' href="#" class="ie-nm-blu">Vote</a></li>
        </ul>
      </div>
    </div>
  </div>
  <div id='listData'>
  </div>
  <div id= "pagination" class="ie-right ie-top-mar-20"> 
  </div>
  <!-- close pagination -->  
</div>
<div id="tag-cloud-sec" >
  <div class="tag-cloud-heading">Tag Cloud 
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