<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
  <title>Create Project!</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.limit.js"></script>
    <script type='text/javascript' src='<%=request.getContextPath()%>/public/js/lib/jquery.autocomplete.js'></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/projects/edit-project.js"></script>
    <script type="text/javascript" src="http://api.recaptcha.net/js/recaptcha_ajax.js"></script>
    <link href="<%=request.getContextPath()%>/public/css/lib/jquery.autocomplete.css" type="text/css" rel="stylesheet" />
    <meta name="currentPage" content="project" />
</head>
<body>
  <div id="submit-idea-pg-content">
    <form action="/projects/create" enctype="multipart/form-data" method="POST" id='projectForm' >
      <c:if test="${not empty ideaKey}">
        <input type="hidden" value="<c:out value='${ideaKey}'/>" id="ideaKey" name="ideaKey"/>        
      </c:if>
      <c:if test="${not empty projectKey}">
        <input type="hidden" value="<c:out value='${projectKey}'/>" id="projectKey" name="projectKey"/>        
      </c:if>
      <table width="98%" border="0" align="left" cellpadding="2" cellspacing="1" class="ie-submit-idea-tb" id="sub-my-idea">
        <tr>
          <td width="23%"></td>
          <td colspan="5">
            <div class="ie-left-mar-10 ie-top-mar-5 ie-rd" id="errDisplay">

            <ul><c:forEach items="${viewStatus.messages}" var="msgEntry">
                      <!-- Print other messages -->
                      <fmt:bundle basename="ui">
                      <li><c:out value="${msgEntry.value}"/></li>
                      </fmt:bundle>
                </c:forEach></ul>
            </div>
          </td>
         
        </tr>
         <c:set var="projectDetail" value="${viewStatus.data['project']}"></c:set>
         <c:set var="ideaTitle" value="${viewStatus.data['ideaTitle']}"></c:set>
         <c:set var="project" value="${projectDetail['project']}"/>  
        	<div class="ie-left-mar-5">
        	<c:if test="${true eq editProject}">
               <h3 class="blu-heading">Update your Project</h3>
      		</c:if>
      		<c:if test="${empty editProject}">
              <h3 class="blu-heading">Create your Project</h3>
            </c:if>
                    	
            <div class="ie-text ie-left-mar-5"><c:out value='${ideaTitle}'/>
            </div>
        </div>
        <tr>
          <td width="23%" class="ie-td-lbg" >Project Name<em>*</em></td>
          <td colspan="5" class="ie-td-lbg" ><input name="name" type="text" id="name" size="70" maxlength="150" value="<c:out value='${project.name}'/>"/>
          <br />
          <span class="ie-left-mar-3" id="charsLeftName"></span> chars left.
          </td>
        </tr>
        <tr>
          <td class="ie-td-lbg">Description</td>
          <td colspan="5" class="ie-td-lbg">
            <textarea name="description" cols="110" rows="7"  id="description" ><c:out value='${project.description}'/></textarea>
            <br />
            <span class="ie-left-mar-3" id="charsLeftDescription"></span> chars left.
          </td>
        </tr> 
        <tr>
          <td class="ie-td-lbg">Upload Logo</td>
          <td colspan="5" class="ie-td-lbg">
            <input type="file" name="logoFile" id="fileField"  value="<c:out value='${project.logo}'/>"/>
            <input type="hidden" name="recaptchaChallengeField" id="recaptchaChallengeField"/>
            <input type="hidden" name="recaptchaResponseField" id="recaptchaResponseField"/>
		  	<br />
          </td>
        </tr>
        <tr>
          <td class="ie-td-lbg">Add Developers</td>
          <td colspan="5" class="ie-td-lbg">
			<div  id="developerList">
				<c:if test="${not empty projectDetail.developers}">
			    <c:forEach items="${projectDetail.developers}" var="developer" >				
				<input name="devName" type="text"  value="<c:out value='${developer.name}'/>" readonly="readonly" size="30" maxlength="50" />
				<input name="email" type="text"  value="<c:out value='${developer.emailId}'/>" readonly="readonly" size="30" maxlength="50" />
				<input type="hidden" name="status" value="<c:out value='${developer.status}'/>" />
				<input type="hidden" name="devKey" value="<c:out value='${developer.key}'/>"/>
				<br></br>
			   </c:forEach>		
			   </c:if>	  
			  
		   		<input type="hidden" value="0" id="theValue" />
				<input name="devName" onClick="blankDevName()" onSelect="blankDevName()" type="text" id="devName" value="name" size="30" maxlength="50" />
				<input name="email" onClick="blankEmailId()" onSelect="blankEmailId()" type="text" id="email" value="emailId" size="30" maxlength="50" />	
				<input type="hidden" name="status" value="" />	
				<input type="hidden" name="devKey" value="" /> <a href="#javascript:void(0)" onclick="addMore()" class="ie-nm-blu">Add more</a>	
			</div>
                 
		  	<br />
          </td>
        </tr>
        <tr>
          <td class="ie-td-lbg">&nbsp;</td>
          <td colspan="5" class="ie-td-lbg">&nbsp;</td>
        </tr>
        <tr>
          <td width="23%" class="ie-td-lbg">&nbsp;</td>
          <td colspan="5" class="ie-td-lbg">
            <div align="left">
            <c:if test="${true eq editProject}">
               <input type="button" name="button" id="btnCreateProj" value="Update Project" class="ie-button" 
              style="padding-left:20px; padding-right:20px;"/>
      		</c:if>
      		<c:if test="${empty editProject}">
              <input type="button" name="button" id="btnCreateProj" value="Create Project" class="ie-button" 
              style="padding-left:20px; padding-right:20px;"/>
            </c:if>
            </div></td>
        </tr>
        <tr>
          <td class="ie-td-lbg">&nbsp;</td>
          <td colspan="5" class="ie-td-lbg">&nbsp;</td>
        </tr>
        
      </table> 
    </form>
  </div>
</body>
</html>