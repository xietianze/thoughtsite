<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
<head>
	<title>Welcome!</title>
    <!-- Load the home js file. -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.limit.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/home.js"></script>
	<script type='text/javascript' src='<%=request.getContextPath()%>/public/js/lib/jquery.autocomplete.js'></script>
    <link href="<%=request.getContextPath()%>/public/css/lib/jquery.autocomplete.css" type="text/css" rel="stylesheet" />
    <meta name="currentPage" content="home" />
</head>
<body>
  <!-- start body left content  -->
  <div class="ie-top-mar-15" >
    <div id="content"> 
      <strong>Lorem ipsum dolor sit amet, consectetur </strong><br>
      Namdd sds  mauris est, iaculis a laoreet ut, porttitor non nulla. Nam mauris est, iaculis a laoreet ut, porttitor non nulla. 
      <ul>
        <li>adipiscing elit.Nam mauris est, iaculis </li>
        <li>porttitor non nulla. adipiscing elit.Praesent </li>
      </ul><br>
      
      <div align="left">
        <table width="390" border="0" align="left" cellpadding="0" cellspacing="0">
          <tr>
            <td width="49"><img src="<%=request.getContextPath()%>/public/images/ic1.gif" border="0" alt="Share your idea"></td>
            <td width="128" align="center" valign="middle">Share <br>your idea</td>
            <td width="10" rowspan="3" align="center" class="ie-ver-line"><!-- N/A --></td>
            <td width="53"><!-- N/A --></td>
            <td width="49"><img src="<%=request.getContextPath()%>/public/images/ic4.gif" border="0" alt="Select an Idea"></td>
            <td width="90" align="center" valign="middle">Select an Idea</td>
          </tr>
          <tr>
            <td><img src="<%=request.getContextPath()%>/public/images/arrow-dwn.gif" border="0" alt="Vote, Comment"></td>
            <td><!-- N/A --></td>
            <td><!-- N/A --></td>
            <td><img src="<%=request.getContextPath()%>/public/images/arrow-dwn.gif" border="0" alt="#"></td>
            <td><!-- N/A --></td>
          </tr>
          <tr>
            <td><img src="<%=request.getContextPath()%>/public/images/ic2.gif"  border="0" alt="#"></td>
            <td align="center" valign="middle">Vote, Comment <br>
             &amp; Discuss</td>
            <td><!-- N/A --></td>
            <td>
              <img src="<%=request.getContextPath()%>/public/images/ic3.gif" border="0" alt="Build an Internet Company">
            </td>
            <td align="center" valign="middle">Build an <br>
             Internet Company
            </td>
          </tr>
        </table><br>
      </div>
    </div>
  </div>
  <!-- end body left content  -->
  <!-- start right idea form -->
  <div id="submit-idea-sec" >
    <!-- start form  -->
    <div class="ie-text" id="submit-idea-form"  align="center" >
      <form action="<%=request.getContextPath()%>/ideas/save" method="post" id="submitIdeaForm" onsubmit="return home.validateForm();">
        <!-- form heading -->
        <div class="ie-form-title-hm" >Submit Your Form
        </div>
        <!-- Start form -->
        <div >
          <ul class="ie-hm">
            <li class="ie-hm">
              <div class="ie-left-mar-10 ie-top-mar-5 ie-rd" id="errDisplay" style="display:none">
              </div>
            </li>
            <li class="ie-hm">
              <input name="title" id="title" type="text" maxlength="500" class="ie-left-mar-10 ie-size-93 " value="Idea Title" alt="Idea Title"/>
            </li>
            <li class="ie-hm">
              <textarea id="description" name="description" cols="40" rows="7" class="ie-left-mar-10 ie-size-92" alt="Description">Description</textarea>
            </li>
            <li class="ie-hm"><span class="ie-left-mar-10" id="charsLeft"></span> chars left.</li>
            <li class="ie-hm">
              <input name="tags" id="tags" type="text" class="ie-left-mar-10 ie-size-62" value="Tags" alt="Tags" autocomplete="off" maxlength="150">
              <select name="ideaCategoryKey" id="category" class="ie-size-30" alt="Category">
                <option>Category</option>
              </select>
            </li>
            <li class="ie-hm ie-center ie-bottom-mar-10 ie-top-mar-10"><input type="button" name="button" id="btnSaveIdea" value="Save Idea" class="ie-button" onclick=""> 
            </li>
            <li class="ie-hm ie-center ie-bottom-mar-10"><a href="#" class="ie-sm-blu">Terms &amp; condtions</a> </li>
            <li class="ie-hm">&nbsp; </li>
          </ul>
        </div>
        <!-- end form fields -->
      </form>
    </div>
  <!-- end form -->
  </div> 
  <!-- end right idea form  -->
</div>
<!-- end body content  --> 
<!-- start bottom popular links -->
<div class="ie-clear"  >
<div  align="center"> 
  <div id="bottom-links">
     <!-- start first column  -->
    <div class="ie-clear" id="bottom-wrapper" >
      <div id= "bt-colA" > 
        <div class="ie-top-pad-10 ie-left-pad-10 ie-right-pad-10 ie-bottom-mar-10" >
          <div class="ie-bottom-link-heading " >Most Popular Ideas
          </div> 
          <div class="ie-bottom-link-content">
            <c:forEach items="${popularIdeas}" var="popularIdea">
            <strong>
              <a class="ie-lg" href="<%=request.getContextPath()%>/ideas/show/<c:out value='${popularIdea.key}'/>">
                <c:out value='${popularIdea.title}'></c:out>
              </a>
            </strong>
            <br />
            <br />
            <c:out value='${popularIdea.description}'></c:out>
            <div align="right" ><a href="<%=request.getContextPath()%>/ideas/show/<c:out value='${popularIdea.key}'/>" class="ie-sm-lg">more..</a></div>
            <br />
            </c:forEach>
          </div>
        </div>
      </div>
      <!-- col B -->
      <div id= "bt-colB"> 
        <div class="ie-top-pad-10 ie-left-pad-9 ie-right-pad-9 ie-bottom-mar-10 bt-colB-int">
          <div class="ie-bottom-link-heading " >Most Recent Ideas
          </div> 
          <div class="ie-bottom-link-content">
            <c:forEach items="${recentIdeas}" var="recentIdea">
              <strong>
              <a class="ie-lg" href="<%=request.getContextPath()%>/ideas/show/<c:out value='${recentIdea.key}'/>">
                <c:out value='${recentIdea.title}'></c:out>
              </a>
              </strong>
            <br /><br />
            <c:out value='${recentIdea.description}'/>
             <div align="right" ><a href="<%=request.getContextPath()%>/ideas/show/<c:out value='${recentIdea.key}'/>" class="ie-sm-lg">more..</a> </div>
            <br />  
            </c:forEach>
          </div>
        </div>
      </div>
      <!-- col C -->
      <div id= "bt-colC">  
        <div class="ie-top-pad-10 ie-left-pad-9 ie-right-pad-10 ie-bottom-mar-10">
           <div class="ie-bottom-link-heading " >Recently Picked Up Ideas
          </div> 
          <div class="ie-bottom-link-content">
            <c:forEach items="${recentlyPickedIdeas}" var="projectIdea">
              <strong>
              <a class="ie-lg" href="<%=request.getContextPath()%>/ideas/show/<c:out value='${projectIdea.key}'/>">
                <c:out value='${projectIdea.title}'></c:out>
              </a>
              </strong>
            <br /><br />
            <c:out value='${projectIdea.description}'/>
             <div align="right" ><a href="<%=request.getContextPath()%>/ideas/show/<c:out value='${projectIdea.key}'/>" class="ie-sm-lg">more..</a> </div>
            <br />  
            </c:forEach>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>