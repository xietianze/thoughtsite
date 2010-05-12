<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
  <decorator:usePage id="thePage" />
  <% 
    // TODO: Sachneet "Change to JSTL tags"
    String pageName = thePage.getProperty("meta.currentPage");
    if(null != pageName) {
  %>
  
    <div id="top-menu"> 
      <div class="ie-top-menu-main"><decorator:getProperty property='meta.menu' />
        <div><a class="<%if (pageName.equals("home")) {%>ie-menu-items-selected<% } else {%>ie-menu-items<%} %>" href="/">Home</a></div>
        <div><a class="<%if (pageName.equals("idea")) {%>ie-menu-items-selected<% } else {%>ie-menu-items<%} %>" href="/ideas/list">Idea</a></div>
        <div><a class="<%if (pageName.equals("save")) {%>ie-menu-items-selected<% } else {%>ie-menu-items<%} %>" href="/ideas/showForm">Submit My Idea</a></div>
        <div><a class="<%if (pageName.equals("tag")) {%>ie-menu-items-selected<% } else {%>ie-menu-items<%} %>" href="/tags/list">Tags</a></div>
        <div><a class="<%if (pageName.equals("project")) {%>ie-menu-items-selected<% } else {%>ie-menu-items<%} %>" href="/projects/list">Projects</a></div>
        <div id='userProfile' style='display:none'><a class="<%if (pageName.equals("user")) {%>ie-menu-items-selected<% } else {%>ie-menu-items<%} %>" href="/users/ideas">User Profile</a></div>
        <%if (pageName.equals("search")) {%>
        <div class="ie-menu-items-selected">Search Result</div>
        <% } %>
        <div id='admin' <c:if test="${'admin' != user.roleName}">style='display:none'</c:if>><a class="<%if (pageName.equals("admin")) {%>ie-menu-items-selected<% } else {%>ie-menu-items<%} %>" href="/admin/action">Admin</a></div>
      </div> 
      
      <%if (pageName.equals("user")) {%>
        <div id="tab-menu">
          <ul>
            <li><a href="#user-profile"> My Profile</a></li>
            <li><a href="#user-ideas"> My Ideas</a></li>
            <li><a href="#user-projects"> My Projects</a></li>
          </ul>
        </div>
      <% } %>
      <%if (pageName.equals("search")) {%>
        <div id="tab-menu">
          <ul>
            <li><a href="#search-ideas">Ideas</a></li>
            <li><a href="#search-projects">Projects</a></li>
          </ul>
        </div>
      <% } %>
          <%if (pageName.equals("admin")) {%>
      <div id="ie-adm-tab-menu">
        <div style="width:20px; height: 10px; float:left; background-color:#fff; background-image:url(<%=request.getContextPath()%>/images/tab-bg.gif)" > &nbsp;</div> 
        <div class="ie-adm-tabmenu-spacer"> &nbsp;</div>
        <div class="ie-adm-tabmenu-items-selected"> Action Items</div> 
        <div class="ie-adm-tabmenu-spacer"> &nbsp;</div> 
        <div><a class="ie-adm-tabmenu-items" href="<%=request.getContextPath()%>/ideas/list"> Ideas</a></div>
        <div class="ie-adm-tabmenu-spacer"> &nbsp;</div>
        <div><a class="ie-adm-tabmenu-items" href="<%=request.getContextPath()%>/users/list"> Users</a></div>  
        <div class="ie-adm-tabmenu-spacer"> &nbsp; </div>
        <div><a class="ie-adm-tabmenu-items" href="<%=request.getContextPath()%>/projects/list"> Projects</a></div> 
        <div class="ie-adm-tabmenu-spacer"> &nbsp; </div>
        <div class="ie-adm-tabmenu-items"> Add Custom Gadget</div> 
      </div>
      <% } %>
    <% } %>
    </div>
