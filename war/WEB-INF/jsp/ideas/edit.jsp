<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<html>
<head>
  <title>Save idea!</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/lib/jquery.limit.js"></script>
    <script type='text/javascript' src='<%=request.getContextPath()%>/public/js/lib/jquery.autocomplete.js'></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/public/js/ideas/edit-idea.js"></script>
    <script type="text/javascript" src="http://api.recaptcha.net/js/recaptcha_ajax.js"></script>
    <link href="<%=request.getContextPath()%>/public/css/lib/jquery.autocomplete.css" type="text/css" rel="stylesheet" />
    <meta name="currentPage" content="save" />
</head>
<body>
  <div id="submit-idea-pg-content">
    <form action="/ideas/publish" method="POST" id='ideaForm'>
      
      <table width="98%" border="0" align="left" cellpadding="2" cellspacing="1" class="ie-submit-idea-tb" id="sub-my-idea">
        <tr>
          <td width="23%"></td>
          <td colspan="5">
            <div class="ie-left-mar-10 ie-top-mar-5 ie-rd" id="errDisplay">
              <c:if test="${'error' eq viewStatus.status}">
                <!-- Print all messages -->
                <ul><c:forEach items="${viewStatus.messages}" var="msgEntry">
                  <c:choose>
                    <c:when test="${'duplicate' eq msgEntry.key}">
                      <!-- Print links for duplicate errors -->
                      <div><c:out value="${viewStatus.messages['duplicate']}" /></div>
                      <%-- Iterate over  duplicate ideas list--%>
                      <c:forEach items="${viewStatus.data['duplicateIdeas']}" var="duplicateIdea">
                      <c:url value="/ideas/show/${duplicateIdea.key}" var="ideaUrl"/>
                      <li><a href="<c:out value='${ideaUrl}'/>" class="ie-nm-blu" target="blank"><c:out value="${duplicateIdea.title}"/></a></li>
                      </c:forEach>
                    </c:when>
                    <c:otherwise>
                      <!-- Print other messages -->
                      <fmt:bundle basename="ui">
                      <li><fmt:message key="${msgEntry.key}"/>: <c:out value="${msgEntry.value}"/></li>
                      </fmt:bundle>
                    </c:otherwise>
                  </c:choose>  
                </c:forEach></ul>
              </c:if>  
            </div>
          </td>
        </tr>
        <c:set var="idea" value="${viewStatus.data['idea']}"/>
        <tr>
          <td width="23%" class="ie-td-lbg" >Idea Name<em>*</em></td>
          <td colspan="5" class="ie-td-lbg" ><input name="title" type="text" id="title" size="70" maxlength="150" value="<c:out value='${idea.title}'/>"></td>
        </tr>
        <tr>
          <td class="ie-td-lbg">Description<em>*</em></td>
          <td colspan="5" class="ie-td-lbg">
            <textarea name="description" cols="110" rows="7"  id="description" ><c:out value='${idea.description}'/></textarea>
            <br />
            <span class="ie-left-mar-3" id="charsLeftDescription"></span> chars left.
          </td>
        </tr> 
        <tr>
          <td class="ie-td-lbg">Target Audience </td>
          <td colspan="3" class="ie-td-lbg">
            <textarea name="targetAudience" cols="110" rows="4"  id="targetAudience"><c:out value='${idea.targetAudience}'/></textarea>
            <br />
            <span class="ie-left-mar-3" id="charsLeftTargetAudience"></span> chars left.
          </td>
        </tr>
        <tr>
          <td class="ie-td-lbg">Competition</td>
          <td colspan="3" class="ie-td-lbg">
            <textarea name="competition" cols="110" rows="4"  id="competition"><c:out value='${idea.competition}'/></textarea>
            <br />
            <span class="ie-left-mar-3" id="charsLeftCompetition"></span> chars left.
          </td>
        </tr>
        <tr>
          <td class="ie-td-lbg">How to Monetize </td>
          <td colspan="3" class="ie-td-lbg">
            <textarea name="monetization" cols="110" rows="4"  id="monetization"><c:out value='${idea.monetization}'/></textarea>
            <br />
            <span class="ie-left-mar-3" id="charsLeftMonetization"></span> chars left.
          </td>
        </tr>
        <tr>
          <td class="ie-td-lbg">Tags</td>
          <td class="ie-td-lbg">
          <input name="tags" type="text" id="tags" size="70" maxlength="150" value="<c:out value='${idea.tags}'/>"/></td>
        </tr>
        <tr>
          <td class="ie-td-lbg">Category</td>
          <td colspan="5" class="ie-td-lbg"><select name="ideaCategoryKey" id="category">
            <option>Category</option>
          </select>
          <input type="hidden" value="<c:out value='${idea.ideaCategoryKey}'/>" id="selectedCategory" name="selectedCategory"/>
          </td>
        </tr>
        <tr>
          <td width="23%" class="ie-td-lbg">Projects visibility<em>*</em><span class="red"></span></td>
          <td colspan="5" class="ie-td-lbg"><input type="checkbox" name="ideaRightsGivenUp" id="ideaRightsGivenUp" <c:if test="${idea.ideaRightsGivenUp}">checked</c:if>  />
            Idea rights belong to the originator of the idea<br/>
            <input type="checkbox" name="ipGivenUp" id="ipGivenUp" <c:if test="${idea.ipGivenUp}">checked</c:if>/>
            Originator is giving up IP for their idea</td>
        </tr>
        <tr>
          <td class="ie-td-lbg">&nbsp;</td>
          <td colspan="5" class="ie-td-lbg">&nbsp;</td>
        </tr>
        <tr>
          <td width="23%" class="ie-td-lbg">&nbsp;</td>
          <td colspan="5" class="ie-td-lbg">
            <div align="left">
              <input type="hidden" name="isDuplicate" id="isDuplicate" value="<c:out value='${isDuplicate}' default="false"/>"/>
              <input type="hidden" name="recaptchaChallengeField" id="recaptchaChallengeField"/>
              <input type="hidden" name="recaptchaResponseField" id="recaptchaResponseField"/>
              <input type="button" name="button" id="btnSaveIdea" value="Save draft" class="ie-button" 
              style="padding-left:20px padding-right:20px;" onclick="saveIdea()" />
              <input type="hidden" value="<c:out value='${idea.key}' default=''/>" id="key" name="key"/>
              <input type="hidden" value="<c:out value='${idea.status}' default=''/>" id="status" name="status"/>
              
              &nbsp;
              <%--Set publish label according to duplicate flag --%>
              <c:set var="publishLabel" value="Publish"></c:set>
              <c:if test="${true eq isDuplicate}">
                <c:set var="publishLabel" value="Publish Anyway"></c:set>
              </c:if>
              <input type="button" name="button2" id="btnPublishIdea" value="<c:out value='${publishLabel}'/>" class="ie-button" style="padding-left:20px; 
              padding-right:20px;" />
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