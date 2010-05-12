google.setOnLoadCallback(function() {
    ie.progressStart();
    loadTagCloud();    // start with 0 offset
    var $tabs = $('#tab-menu').tabs();
    $tabs.tabs('option', 'selected', 0); // => 0
    loadUserProfile();
    $('#tab-menu').bind('tabsselect', function(event, ui) {
        switch(ui.panel.id){
        case 'user-profile':
        	loadUserProfile();
            break;
        case 'user-ideas':
            loadIdeas(0);
            break;
        case 'user-projects':
            loadProjects(0);
            break;
        default :
            loadUserProfile();
            break;
        }
    });
});

function loadUserProfile(){
    $.get(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'users/profile.json'), 
            {}, 
            function (handle) {
                // render user profile
                if(handle != ''){
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                    	$('#user-profile').html('');
                    	$('#pagination').html('');
                    	$('#user-profile').append(createProfileHtml(handle.viewStatus.data.user));
                    	ie.progressEnd();
                    }
                    else {
                        $('#user-profile').html(handle.viewStatus.messages.user);
                    }
                }
                else {
                    ie.globalError();
                }
                ie.progressEnd();
            }, 
            ie.config.RESPONSE_TYPE
    );
}

function loadIdeas(offset) {
    $.get(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'users/ideas.json?startIndex='+ offset + ''), 
            {}, 
            function (handle) {
                // render most recent idea
                if(handle != ''){
                	$('#user-ideas').html('');
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                        // TODO: Abhishek, Need to handle with Template :)
                        $('#user-ideas').html('');
                        $('#pagination').html('');
                        var counter = 0;
                        for (i in handle.viewStatus.data.ideas) {
                            counter++;
                            if(counter > ie.config.RECORD_PER_PAGE)
                                break;
                            $('#user-ideas').append(createIdeaHtml(handle.viewStatus.data.ideas[i]));
                        }
                        $('#pagination').html('');
                        if(undefined != handle.viewStatus.data.paging)
                            $('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'loadIdeas'));
                        ie.progressEnd();
                    }
                    else {
                        $('#user-ideas').html(handle.viewStatus.messages.ideas);
                    }
                }
                else {
                    ie.globalError();
                }
                ie.progressEnd();
            }, 
            ie.config.RESPONSE_TYPE
    );
}

function loadIdeasAfterDeletion(ideaKey) {
    $.get(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/delete/' + ideaKey), 
            {}, 
            function(handle) {
            	loadIdeas(0);
            }, 
            ie.config.RESPONSE_TYPE
    );
}

function loadProjects(offset) {
    $.get(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'users/projects.json?startIndex='+ offset + ''), 
            {}, 
            function (handle) {
                // render most recent idea
                if(handle != ''){
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                        // TODO: Abhishek, Need to handle with Template :)
                        $('#user-projects').html('');
                        var counter =0;
                        for (i in handle.viewStatus.data.projects) {
                            counter++;
                            if(counter > ie.config.RECORD_PER_PAGE)
                                break;
                            $('#user-projects').append(createProjectHtml(handle.viewStatus.data.projects[i]));
                        }
                        $('#pagination').html('');
                        if(undefined != handle.viewStatus.data.paging)
                            $('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'loadProjects'));
                        ie.progressEnd();
                    }
                    else {
                        $('#user-projects').html(handle.viewStatus.messages.projects);
                    }
                }
                else {
                    ie.globalError();
                }
                ie.progressEnd();
            }, 
            ie.config.RESPONSE_TYPE
    );
}

function createProfileHtml(jsonData) {
	var output = '';
	output += '<div class="ie-profile-desc ie-text" id="mid-wrapper" >';
//	output += '<div class="ie-left" ><br /><br /><br />';
//	output += '<img src="' + jsonData['thumbnailUrl'] + '" alt="" width="100" height="100" align="left" class="ie-right-mar-10 ie-bottom-mar-20 " /></div>';
	output += '<div class="ie-left ie-right-mar-10 ie-left-mar-10"><br /><br />';
	output += 'Name:<br /><br /> Email-id:<br /><br />Status: <br /><br />Reputation Points: <br /><br />Joined on:';
	output += '</div>';
	output += '<div class="ie-left ie-right-mar-10 ie-left-mar-20" ><br /><br />';
	output += '<strong>' + jsonData['displayName'] + '</strong><br /><br />'; 
	if(undefined == jsonData['emailId']) {
		output += '<span class="ie-nm-blu"> </span><br /><br />';  
    }
    else {
    	output += '<span class="ie-nm-blu">' + jsonData['emailId'] + '</span><br /><br />';
    }
	
	output += jsonData['status'] + '<br /><br />';
	output += jsonData['reputationPoints'] + '<br /><br />';
	output += jsonData['createdOn'] + '<br /><br />';
	output += '</div>';
	output += '</div>';
	return output;
}

function createProjectHtml(jsonData) {
    var output = '';
    output += '<div style="width:100%;  height:180px;" class="ie-top-mar-25">';
    output += '     <div class="ie-left-mar-20 ie-text ie-projects">';
    output += '         <h1 class="blu-heading">';
    output += '         <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'projects/show/'+ jsonData['project']['key']) + '">';
    output +=           ie.escapeHTML(jsonData['project']['name']) + '</a></h1><br />';
    output += '<table width="98%" border="0" align="left" cellpadding="2" cellspacing="1">';
    output += '<tr>';
    output += '  <td width="80px">';
    if(undefined == jsonData['project']['logo']) {
        output += '         <img src= "' + ie.config.PUBLIC_URL +'/images/img.gif" height="60px" width="60px"/>';  
    }
    else {
        output += '         <img src= "/showImage/' + jsonData['project']['key'] + '" height="60px" width="60px"/>';  
    }
    output += '  </td>';
    output += '  <td valign="top">';
    output +=           ie.escapeHTML(jsonData['project']['description']) + '<br />';
    output += '         <strong>Last Updated: </strong>' + jsonData['project']['updatedOn'] + '<br />';  
    output += '  </td>';
    output += '</tr>';
    output += '     </div>';
    output += '</div>';
    return output;
}

function createIdeaHtml(jsonData) {
    var output = '';
    output += '<div style="width:100%; height:180px;"  >';
    if(ie.config.STATUS_PUBLISHED == jsonData['idea']['status']) {
        output += '        <div class="ie-left ie-votes">';
        output += '<div class="ie-top-mar-10 ie-center"><strong> Votes</strong>';
        output += '    </div>';
        output += '    <div class="ie-top-mar-10 ie-left">';
        output += '      <img src="' + ie.config.PUBLIC_URL + 'images/hand-up.gif" alt="" width="25" height="30" hspace="10" border="0" /><strong>+ ';
        output += jsonData['idea']['totalPositiveVotes']+'</strong>';
        output += '    </div>';
        output += '    <div class="ie-clear ie-top-mar-5 ie-left" style="width: 85%">';
        output += '      <img src="' + ie.config.PUBLIC_URL + 'images/hand-dwn.gif" alt="" width="25" height="30" hspace="10" border="0" /><strong>- ';
        output += jsonData['idea']['totalNegativeVotes']+'</strong>'; 
        output += '    </div>';
        output += '  </div>';
    }
    else if(ie.config.STATUS_OBJECTIONABLE == jsonData['idea']['status']) {
        output += '        <div class="ie-left ie-votes">';
        output += '<div class="ie-top-mar-10 ie-center"><strong> Objectionable Idea</strong>';
        output += '    </div>';
        output += '  </div>';
    }
    else if(ie.config.STATUS_DUPLICATE == jsonData['idea']['status']) {
        output += '        <div class="ie-left ie-votes">';
        output += '<div class="ie-top-mar-10 ie-center"><strong> Duplicate Idea</strong>';
        output += '    </div>';
        output += '  </div>';
    }
    else {
        output += '        <div class="ie-left ie-votes">';
        output += '<div class="ie-top-mar-10 ie-center"><strong> Saved Idea</strong>';
        output += '    </div>';
        output += '  </div>';
    }
    output += '  <div class="ie-right ie-text ie-detail">';
    output += '    <h1 class="blu-heading">';
    if(ie.config.STATUS_SAVED == jsonData['idea']['status']) {
        output += '    <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/edit/'+ jsonData['idea']['key']) + '">';
    }
    else if(ie.config.STATUS_OBJECTIONABLE == jsonData['idea']['status']) {
        output += '    <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/edit/'+ jsonData['idea']['key']) + '">';
    }
    else {
        output += '    <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/show/'+ jsonData['idea']['key']) + '">';
    }
    output += ie.escapeHTML(jsonData['idea']['title']) + '</a></h1><br />';
    output += ie.escapeHTML(jsonData['idea']['description']) + '<br/>';
    output += '    <div class="ie-left ie-detail-info1">';
    output += '      <strong>Tags:</strong>';
    if(undefined != jsonData['tags']) {
        for(data in jsonData['tags']) {
            output += '      <a href="javascript:void(0)" onclick="idea.browseByTag(\'' + jsonData['tags'][data]['title'] + '\');" class="ie-nm-blu">' + jsonData['tags'][data]['title'] + '</a>';
        }
    }
    output += '    </div>';
    output += '    <div class="ie-right ie-left-mar-10 ie-right-mar-0">';
    if(ie.config.STATUS_SAVED == jsonData['idea']['status']) {
        output += '      <a href="javascript:void(0);" onclick="loadIdeasAfterDeletion(\'' + jsonData['idea']['key'] + '\')" class="ie-nm-blu">Delete</a>';
    }
    output += '    </div>';
    if(undefined != jsonData['user']) {
        output += '    <div class="ie-right ie-detail-info2">';
        output += '      <strong>Last Updated: </strong>'+jsonData['idea']['lastUpdated']+'<br />';
        output += '      <a href="#" class="ie-nm-blu">' + jsonData['user']['name'] + '</a><br />';
        output += '      <strong>' + jsonData['user']['userPoints'] + ' Points</strong><br />';
        output += '      <strong>' + jsonData['user']['userAwards'] + ' Awards</strong></div>';
        output += '  </div>';
    }
    output += '</div>';
    return output;
}
function loadTagCloud() {
    $.get(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'tags/my' + '.json'), 
            {}, 
            function (handle) {
                // render most recent idea
                if(ie.config.SUCCESS == handle.viewStatus.status) {
                    // TODO: Abhishek, Need to handle with Template :)
                    var html = ie.TagCloud.render(handle.viewStatus.data.tags, 
                            handle.viewStatus.data.weights, 
                            {title : 'title', weightage : 'weightage', css: 'tag'},
                            {url:ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/list?type=tag&tag=')}
                            );
                    $('#cloud').html(html);
                    ie.progressEnd();
                }
                else if(ie.config.ERROR == handle.viewStatus.status) {
                    $('#cloud').html(handle.viewStatus.messages.tag);
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