/* Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS.
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

/**
 * file is responsible to handle all javascript tasks on ShowProjectDetailForm require
 * jquery 1.3.2
 */
// onload set form focus validations
google.setOnLoadCallback(function() {
	//Set project key
    projectDisplay.setProjectKey($('#projectKey').val());
    // set comment char limit to 3000 chars
    $('#comment').limit('3000','#charsLeft');
    //User require login before create project
    ie.Login.setLoggedinUserCallback('handleProjectPostCommentForm');
    //Expanded link when login
    $('#expandLink').click(function(){
        $('#commentLayer').toggle();
        if($.trim($(this).html()) == '+') {
            $(this).html('-');
            projectDisplay.expand($('#projectKey').val());
        }
        else {
            $(this).html('+');
        }
    });
    //Post comment when Post button is clicked
    $('#btnPostComment').click(function(){
    	projectDisplay.postComment();
    });
    
    $('#commentLayer').toggle();
    projectDisplay.expand();
})
//    User require login before create project
function handleProjectPostCommentForm(isLoggedIn) {
    if(isLoggedIn) {
        $('#postComment').show();
    }
    else {
        $('#postComment').hide();
    }
}

var projectDisplay = {
		projectKey : ''
}

projectDisplay.setProjectKey = function(projectKey) {
    this.projectKey = projectKey;
}
projectDisplay.getProjectKey = function() {
    return this.projectKey;
}
projectDisplay.expand = function () {
    this.loadComments(0);
    ie.progressStart();
}
//Post comment when Post button is clicked
projectDisplay.postComment = function() {
    if('' == $.trim($('#comment').val())) {
        $('#errComment').html('Please provide comments.');
        $('#postComment').focus();
    }
    else {
        ie.showRecaptcha('projectDisplay.postCommentFinal');
    }
}
//Submit the comment entered
projectDisplay.postCommentFinal = function(recaptchaChallengeField, recaptchaResponseField) {
    $.post(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'projectComments/postProjectComments.json'),
                {text: $.trim($('#comment').val()), 
                projectKey: this.getProjectKey(), 
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
                    projectDisplay.expand();
                }, 
                ie.config.RESPONSE_TYPE
    );
}
//Render all the comments posted for a project
projectDisplay.loadComments = function (offset) {
    if(undefined == offset || '' == offset) {
        offset = 0;
    }
    $.get(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'projectComments/listProjectComments/'+ this.projectKey + '.json?startIndex=' + offset), 
            {}, 
            function (handle) {
                // render all the comments
                if(ie.config.SUCCESS == handle.viewStatus.status) {
                    $('#commentData').html('');
                    var counter = 0;
                    for (i in handle.viewStatus.data.comments) {
                        counter++;
                        if(counter > ie.config.RECORD_PER_PAGE)
                            break;
                        $('#commentData').append(projectDisplay.createHtml(handle.viewStatus.data.comments[i]));
                    }
                    $('#commentLayer').show();
                 // Handles Pagination
                    if(undefined != handle.viewStatus.data.paging)
                        $('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'projectDisplay.loadComments'));
                    ie.progressEnd();
                }
                else {//Show error message when not success
                	$('#commentData').html(handle.viewStatus.messages.comments);
                    ie.progressEnd();
                }
            }, 
            ie.config.RESPONSE_TYPE
    );
}
//HTML to render the page loaded with comments
projectDisplay.createHtml = function (jsonData) {
    var html = '';
    html += '<div class="ie-overview-desc ie-text">';
    if(jsonData.user) {
        html += '<img src="' + jsonData.user.thumbnailUrl + '" alt="" width="60" height="60" align="left" class="ie-right-mar-10 ie-bottom-mar-20 " />';
    }
    html += ie.escapeHTML(jsonData.comment.commentTextAsString);
    if(jsonData.user) {
        html += '<br/><div class="ie-sm-lg ie-top-mar-10 ie-left" style=" width:auto;">Posted by: ';
        html += jsonData.user.displayName + '&nbsp; |&nbsp; ' + jsonData.comment.createdOn; 
        html += '</div>';
    }
    html += '<div class=" ie-top-mar-10 ie-right" style="width:auto;">';
    html += '<img border="0" src="' + ie.config.PUBLIC_URL + 'images/ic5.gif" alt="" width="20" height="20" />';
    html += '&nbsp; <a href="javascript:void(0);" onclick="ie.reportAbouse(ie.config.PROJECTS_COMMENT, \'' + jsonData.comment.key + '\')">Report abuse</a>';
    html += '</div>';
    html += '<div class="divider"> &nbsp;&nbsp;';
    html += '</div><div style="clear:both"></div>';
    html += '</div>';
    return html;
}

//Alert to show that a comment is reported as Abuse.
function showAlert() {
    $('#dialog').html('<div style="padding-left: 40px; margin-top: 45px;">This Comment is reported as Abuse.</div>');
    $('#dialog').dialog('option', 'title', 'Alert!');
    $('#dialog').dialog('option', 'modal', true);
    $('#dialog').dialog('open');
};

