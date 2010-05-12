/**
 * Handle idea detail page
 * @required jquery, ie.js, ie.login.js, jquery.limit.js
 * @author aksrivastava
 */

/**
 * Call on page load
 */
google.setOnLoadCallback(function() {
    ideaDisplay.setKey($('#key').val());
    if(null != $('#postComment') && undefined != $('#postComment'))
        $('#comment').limit('3000','#charsLeft');
    var i = document.getElementById('origSumm');
    if(i != null)
        $('#text').limit('3000','#chars');
    // call on user login
    ie.Login.setLoggedinUserCallback('ideaDisplay.handlePostCommentForm');

    // comment expand functionality 
    $('#expandLink').click(function(){
        $('#commentLayer').toggle();
        if($.trim($(this).html()) == '+') {
            $(this).html('-');
            ideaDisplay.expand($('#key').val());
        }
        else {
            $(this).html('+');
        }
    });
    $('#commentLayer').toggle();

    $('#btnAddOrigSumm').click(function(){
        ideaDisplay.addOrigSummary();
    });
    
    $('#btnPostComment').click(function(){
        ideaDisplay.postComment();
    });
    
    $('#commentLayer').toggle();
    ideaDisplay.expand();
})

/**
 * Call on page load
 */
var ideaDisplay = {
  key : ''
}

/**
 * handle comment form (show only when user is logged in)
 */
ideaDisplay.handlePostCommentForm = function(isLoggedIn) {
    if(isLoggedIn)
        $('#postComment').show();
    else
        $('#postComment').hide();
}

/**
 * set idea key 
 */
ideaDisplay.setKey = function(ideaKey) {
    this.key = ideaKey;
}
/**
 * get idea key 
 */
ideaDisplay.getKey = function() {
    return this.key;
}
/**
 * expand comments intermediate (load comments) 
 */
ideaDisplay.expand = function () {
    this.loadComments(0);
    ie.progressStart();
}

/**
 * ask for cptcha
 */
ideaDisplay.postComment = function() {
    if('' == $.trim($('#comment').val())) {
        $('#errComment').html('Please provide comments.');
        $('#errComment').show();
        $('#comment').focus();
    }
    else {
        ie.showRecaptcha('ideaDisplay.postCommentFinal');
    }
}

/**
 * post comment on idea
 * @param recaptchaChallengeField
 * @param recaptchaResponseField
 */
ideaDisplay.postCommentFinal = function(recaptchaChallengeField, recaptchaResponseField) {
    $.post(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'comments/post.json'),
                {text: $.trim($('#comment').val()), 
                ideaKey: this.getKey(), 
                recaptchaChallengeField: recaptchaChallengeField,
                recaptchaResponseField: recaptchaResponseField},
                function (handle) {
                    $('#errComment').html('');
                    if(ie.config.SUCCESS == handle.viewStatus.status) {
                        $('#errComment').html(handle.viewStatus.messages.comments);
                        $('#comment').val('');
                    }
                    else {
                        if(undefined != handle.viewStatus.messages.comments)
                            $('#errComment').html(handle.viewStatus.messages.comments);
                        if(undefined != handle.viewStatus.messages.captcha)
                        $('#errComment').append(handle.viewStatus.messages.captcha);
                        ie.progressEnd();
                    }
                    if('' != $('#errComment').html())
                        $('#errComment').show();
                    ideaDisplay.expand();
                }, 
                ie.config.RESPONSE_TYPE
    );
}

/**
 * Add summary for idea
 */
ideaDisplay.addOrigSummary = function() {
	var a = $.trim($('#text').val());
    $.post(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/addsummary/' + this.getKey()),
                {origSumm: $.trim($('#text').val())}, 
                function (handle) {
                    $('#errOrigSumm').html('');
                    if(ie.config.SUCCESS == handle.viewStatus.status) {
                        $('#errOrigSumm').html('Idea originator\'s summary added successfully.');
                        var div = document.getElementById('origSumm');
                    	var div2 = document.getElementById('main');
                    	div2.removeChild(div);
                    	var showDiv = document.createElement('div');
                    	showDiv.setAttribute('id','showOrigSumm');
                    	showDiv.innerHTML = '<strong>Idea Originator\'s Summary </strong><br/>' + ie.escapeHTML(a);
                    	showDiv.className = 'ie-top-mar-20';
                		div2.appendChild(showDiv);
                    }
                    else {
                        if(undefined != handle.viewStatus.messages.idea)
                            $('#errOrigSumm').html(handle.viewStatus.messages.idea);
                        ie.progressEnd();
                    }
                    if('' != $('#errOrigSumm').html())
                        $('#errOrigSumm').show();
                }, 
                ie.config.RESPONSE_TYPE
    );
}
/**
 * expand comments (load comments) 
 */
ideaDisplay.loadComments = function (offset) {
    $('#commentLayer').show();
    if(undefined == offset || '' == offset) {
        offset = 0;
    }
    $.get(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'comments/list/'+ this.getKey() + '.json?startIndex=' + offset), 
            {}, 
            function (handle) {
                // render most recent idea
                if(ie.config.SUCCESS == handle.viewStatus.status) {
                    $('#commentData').html('');
                    var counter = 0;
                    for (i in handle.viewStatus.data.comments) {
                        counter++;
                        if(counter > ie.config.RECORD_PER_PAGE)
                            break;
                        $('#commentData').append(ideaDisplay.createCommentHtml(handle.viewStatus.data.comments[i]));
                    }
                    if(undefined != handle.viewStatus.data.paging)
                        $('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'ideaDisplay.loadComments'));
                }
                else {
                    $('#commentData').html(handle.viewStatus.messages.comments);
                }
                ie.progressEnd();
            }, 
            ie.config.RESPONSE_TYPE
    );
}
/**
 * vote on idea 
 * @param ideaKey
 * @param isPositiveVote
 */
ideaDisplay.voteIdea = function(ideaKey, isPositiveVote) {
    if(!ie.config.IS_LOGGEDIN_USER) {
        ie.showError('Please login to vote on an idea.');
		return;
    }
    if('' == ideaKey) {
        return false;
    }
    var url = '';
    if(isPositiveVote) {
        url = '/ideas/voteIdea/' + ideaKey + '.json?isPositive=true';
    }
    else {
        url = '/ideas/voteIdea/' + ideaKey + '.json?isPositive=false';
    }
    ie.progressStart();
    $.get(
            url, 
            {}, 
            function (handle) {
                // render most recent idea
                if(ie.config.SUCCESS == handle.viewStatus.status) {
                    if(isPositiveVote) {
                        $('#pIdea').html(parseInt($('#pIdea').html()) + 1);
                    }
                    else {
                        $('#nIdea').html(parseInt($('#nIdea').html()) + 1);
                    }
                    ie.progressEnd();
                }
                else if(ie.config.ERROR == handle.viewStatus.status) {
                    ie.showError(handle.viewStatus.messages.vote);
                    ie.progressEnd();
                }
                else {
                    ie.progressEnd();
                    ie.globalError();
                }
            }, 
            ie.config.RESPONSE_TYPE
    );
    
}

/**
 * mark duplicate
 * @param ideaKey
 * @return void
 */
ideaDisplay.markDupl = function(ideaKey)
{
    if(!ie.config.IS_LOGGEDIN_USER) {
        ie.showError('Please login to mark duplicate an idea.');
		return;
    }
	var url = '';
	// get html to search duplicate idea
	$('#dialog').html('<div class="hiddenInViewSource jquery-dialog" style="padding-left: 50px; margin-top: 10px;">' 
					+	'<b>Enter matching title: </b><input id="inputVal" value=""/><br/>'
					+   '<div id="search-ideas"></div></div>'
					+   '<div style="clear:both"></div>'
					+   '<div id= "pagination" class="ie-right ie-top-mar-20">'
					+	'</div>');
	$('#dialog').dialog('option', 'title', 'Mark Duplicate');
    $('#dialog').dialog('option', 'buttons', { "Search": function() {
    	if($('#inputVal').val() != null && $('#inputVal').val() != '')
    	{
    	url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'search/ideasByTitle.json?startIndex='+ '0&keyword=' + $('#inputVal').val());
    	ie.progressStart();
        $.get(
                url, 
                {}, 
                function (handle) {
                    // render most recent idea
                    if(ie.config.SUCCESS == handle.viewStatus.status) {
                    	$('#search-ideas').html('');
                    	$('#pagination').html('');
                    	var counter = 0;
                    	for (i in handle.viewStatus.data.ideas) {
                            counter++;
                            if(counter > ie.config.RECORD_PER_PAGE)
                                break;
                            if(ideaKey != handle.viewStatus.data.ideas[i].key)
                            {
                            	$('#search-ideas').append(ideaDisplay.createIdeaHtml(handle.viewStatus.data.ideas[i], ideaKey));	
                            }
                            
                        }
                        if(undefined != handle.viewStatus.data.paging)
                            $('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'markDupl'));
                        ie.progressEnd();
                    }
                    else if(ie.config.ERROR == handle.viewStatus.status) {
                        ie.showError(handle.viewStatus.messages.search);
                        ie.progressEnd();
                    }
                    else {
                        ie.progressEnd();
                        ie.globalError();
                    }
                }, 
                ie.config.RESPONSE_TYPE
        	);
    	}
    	else
    	{
    		$('#dialog').html('<div style="padding-left: 50px; margin-top: 90px;">No title entered.</div>');
    	}
    } });
    $('#dialog').dialog('option', 'modal', true);
    $('#dialog').dialog('option', 'height', 500);
    $('#dialog').dialog('option', 'width', 500);
    $('#dialog').dialog('open');
}

/**
 * create idea html to mark duplicate
 * @param jsonData
 * @param ideaKey
 * @return
 */
ideaDisplay.createIdeaHtml = function(jsonData, ideaKey) {
    var output = '';
    output += '<div class="ie-right ie-text ie-search-detail">';
    output += '<h1 class="blu-heading ie-bottom-mar-5">';
    output += '<a href="javascript:void(0);" onclick="ideaDisplay.duplicate(\''+ ideaKey + '\', \'' +jsonData['key'] + '\');" class="ie-sm-blu">';
    output += ie.escapeHTML(jsonData['title']) + '</a></h1>';
    output += ie.escapeHTML(jsonData['description']) + '<br />';
   return output;
}

/**
 * create comment html
 * @param jsonData (json object of comment)
 * @return string
 */
ideaDisplay.createCommentHtml = function (jsonData) {
    var html = '';
    html += '<div class="ie-overview-desc ie-text">';
    html += '<img src="' + jsonData.user.thumbnailUrl + '" alt="" width="60" height="60" align="left" class="ie-right-mar-10 ie-bottom-mar-20 " />';
    html += ie.escapeHTML(jsonData.comment.commentTextAsString);
    if(jsonData.user) {
        html += '<br/><div class="ie-sm-lg ie-top-mar-10 ie-left" style=" width:auto;">Posted by: ';
        html += jsonData.user.displayName + '&nbsp; |&nbsp; ' + jsonData.comment.createdOn; 
        html += '</div>';
    }
    html += '<div class=" ie-top-mar-10 ie-right" style="width:auto;">';
    html += '<a href="javascript:void(0);" onclick="ideaDisplay.voteComment(\'' + jsonData.comment.key + '\', true)">';
    html += '<img border="0" src="' + ie.config.PUBLIC_URL + 'images/ic7.gif" alt="" width="20" height="20" />';
    html += '</a>';
    html += '+ <span id="pComment_' + jsonData.comment.key + '" class="ie-left-mar-5 ie-top-mar-10 ie-right-mar-10">' + jsonData.comment.totalPositiveVotes + '</span>';

    html += '<a href="javascript:void(0);" onclick="ideaDisplay.voteComment(\'' + jsonData.comment.key + '\', false)">';
    html += '<img border="0" src="' + ie.config.PUBLIC_URL + 'images/ic6.gif" alt="" width="20" height="20" />';
    html += '</a>';
    html += '- <span id="nComment_' + jsonData.comment.key + '" class="ie-left-mar-5 ie-top-mar-10 ie-right-mar-10">' + jsonData.comment.totalNegativeVotes + '</span>';
    
    html += '<img border="0" src="' + ie.config.PUBLIC_URL + 'images/ic5.gif" alt="" width="20" height="20" />';
    html += '&nbsp; <a href="javascript:void(0);" onclick="ie.reportAbouse(ie.config.IDEAS_COMMENT, \'' + jsonData.comment.key + '\')">Report abuse</a>';
    html += '</div>';
    html += '<div class="divider"> &nbsp;&nbsp;';
    html += '</div><div style="clear:both"></div>';
    html += '</div>';
    return html;
}

/**
 * vote on comment
 * @param commentKey
 * @param isPositiveVote (if true then positive vote else negative voting)
 * @return void
 */
ideaDisplay.voteComment = function (commentKey, isPositiveVote) {
    if(!ie.config.IS_LOGGEDIN_USER) {
        ie.showError('Please login to vote on an idea comment.');
		return;
    }
    if('' == commentKey) {
        return;
    }
    var url = '';
    if(isPositiveVote) {
        url = '/comments/vote/' + commentKey + '.json?isPositive=true';
    }
    else {
        url = '/comments/vote/' + commentKey + '.json?isPositive=false';
    }
    ie.progressStart();
    $.get(
            url, 
            {}, 
            function (handle) {
                // render most recent idea
                if(ie.config.SUCCESS == handle.viewStatus.status) {
                    if(isPositiveVote) {
                        $('#pComment_' + commentKey).html(parseInt($('#pComment_' + commentKey).html()) + 1);
                    }
                    else {
                        $('#nComment_' + commentKey).html(parseInt($('#nComment_' + commentKey).html()) + 1);
                    }
                    ie.progressEnd();
                }
                else if(ie.config.ERROR == handle.viewStatus.status) {
                    ie.showError(handle.viewStatus.messages.vote);
                    ie.progressEnd();
                }
                else {
                    ie.progressEnd();
                    ie.globalError();
                }
            }, 
            ie.config.RESPONSE_TYPE
    );
    
}

/**
 * vote on comment
 * @param commentKey
 * @param isPositiveVote (if true then positive vote else negative voting)
 * @return void
 */
ideaDisplay.duplicate = function (oIdeaKey, dIdeaKey) {
    ie.progressStart();
    $('#dialog').dialog("close");
    $('#dialog').dialog('option', 'buttons', { "Ok": function() { $(this).dialog("close"); } });
    $('#dialog').dialog('option', 'width', '250px');
    $('#dialog').dialog('option', 'height', '150px');
    // show message and send request to procss
    url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/flag/duplicate/'+ oIdeaKey + '/' + dIdeaKey);
    $.get(
            url, 
            {}, 
            function (handle) {
            	if(ie.config.SUCCESS == handle.viewStatus.status){
                	ie.showError('Idea successfully marked as duplicate.');
                    ie.progressEnd();
                }
                else if(ie.config.ERROR == handle.viewStatus.status) {
                    ie.showError(handle.viewStatus.messages.duplicate);
                    ie.progressEnd();
                }
                else {
                	ie.globalError();
                    ie.progressEnd();
                }
            }, 
            ie.config.RESPONSE_TYPE
    );
}