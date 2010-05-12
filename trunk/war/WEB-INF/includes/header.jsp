      <div id="header" > 
        <div id="logo">
  			  <a href="/" border="0">
  			     <img border="0" src="<%=request.getContextPath()%>/public/images/thoughtsite-logo.gif" alt="" width="158" height="70" class="ie-top ie-top-mar-10 ie-bottom-mar-5"/>
  			  </a>
			  </div>
          <!-- search form --> 
          <div id="search-form"> 
            <form action="<%=request.getContextPath()%>/search/ideas" method="get" onsubmit="return validateSearch();">
              <ul class="ie-hm">
                <li class="ie-hm" id="signin">
                  
                </li>
                <li class="ie-hm ie-top-mar-15">
                  <input name="offset" type="hidden" value="0" />
                  <input name="type" type="hidden" value="search" />
                  <input name="keyword" type="text" class="ie-text ie-top" id="keyword" value="Enter Search Keyword" size="20" align="top" > 
                  <input type="image" src="<%=request.getContextPath()%>/public/images/go.gif" name="go" border="0" style="border:none"  >
                </li>
              </ul>
            </form>
          </div> 
          <!-- end search form --> 
        </div>