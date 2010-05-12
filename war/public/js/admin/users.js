// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * file is responsible to handle all javascript tasks on Users Form on Admin Tab require
 * jquery 1.3.2
 */

// onload set the user screen
google.setOnLoadCallback(function() {
    ie.progressStart();
 // Call to the method that renders all the ideas
    user.loadUsers(0);
});


user = {
}

//Show all the Users.
user.loadUsers = function(offset) {
    var url = '';
    url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/users.json?startIndex=' + offset);
    $.get(
            url, 
            {}, 
            function (handle) {
                // render ideas
                if(handle != ''){
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                        $('#userData').html('');
                        var counter = 0;
                        for (i in handle.viewStatus.data.users) {
                        	counter++;
                        	if(counter > ie.config.RECORD_PER_PAGE_ADMIN)
                            break;
                            $('#userData').append(createHtml(handle.viewStatus.data.users[i]));
                        }
                        // Handles pagination
                        if(undefined != handle.viewStatus.data.paging)
                        {
                        	$('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'user.loadUsers'));
                        }
                        ie.progressEnd();
                    }
                    else {
                        $('#userData').html(handle.viewStatus.messages.users);
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

//HTML for rendering users
function createHtml(jsonData) {
    var output = '';
    output += '		<div id="' + jsonData['userKey'] + '" class="ie-adm-lt-bar ie-clear">';
    output += '			<div class="ie-left ie-left-mar-5 ie-nm-blu">';
    output += 			ie.escapeHTML(jsonData['displayName']);
    output += '     	</div>';
    output += '    		<div class="ie-right ie-left-mar-10 ie-right-mar-0">';
    output += '      		<a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'users/profile/'+ jsonData['userKey']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View</a> ';
    output += '  		</div>';
    output += '      	<div id="banDiv' + jsonData['userKey'] + '" class="ie-right ie-left-mar-10 ie-right-mar-0">';
    if(ie.config.STATUS_ACTIVE == jsonData['status'] && ie.config.ADMIN_ROLE != jsonData['roleName']) {
    output += '      		<a href="javascript:void(0);" onclick="adminReasonBan(\'' + jsonData['userKey'] + '\')" class="ie-nm-blu">Ban</a> |';
    }
    output += '			</div>';
    output += '      	<div id="activateDiv' + jsonData['userKey'] + '" class="ie-right ie-left-mar-10 ie-right-mar-0">';
    if(ie.config.STATUS_BANNED == jsonData['status']) {
    output += '      		<a href="javascript:void(0);" onclick="adminReasonActivate(\'' + jsonData['userKey'] + '\')" class="ie-nm-blu">Activate</a> |';
    }
    output += '			</div>';
    output += '		</div>';
    return output;
}

//Called when a user is banned
function adminReasonBan(userKey)
{
	var url = '';
	// Opens a dialog box that takes the reason for banning a user
	$('#dialog').html('<div class="hiddenInViewSource" style="padding-left: 70px; margin-top: 20px;">' 
					+	'<b>Enter reason to ban a user: </b><textarea id="inputVal"></textarea><br/>'
					+	'</div>');
	$('#dialog').dialog('option', 'title', 'Input Reason!');
    $('#dialog').dialog('option', 'buttons', { "Submit": function() {
    	if($.trim($('#inputVal').val()) != null && $.trim($('#inputVal').val()) != '')
    	{
    	url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/banUser/' + userKey);
    	$.post(
                url,
                {adminReason: $.trim($('#inputVal').val())}, //saved reason for ban
                function (handle) {
                    if(ie.config.SUCCESS == handle.viewStatus.status) {
                    		if(handle.viewStatus.data.user.userKey == userKey && handle.viewStatus.data.user.status == "banned")
                        	{
                    			var main = document.getElementById(userKey);
                        		var banDiv = document.getElementById('banDiv' + userKey);
                        		var activateDiv = document.getElementById('activateDiv' + userKey);
                        		main.removeChild(banDiv);
                        		if(activateDiv != null)
                        		main.removeChild(activateDiv);
                        		var activateDiv = document.createElement('div');
                        		activateDiv.setAttribute('id','activateDiv' + userKey);
                        		activateDiv.innerHTML = '<a href="javascript:void(0);" onclick="adminReasonActivate(' + '\'' + userKey + '\'' + ')"  class="ie-nm-blu">Activate</a> |';
                        		activateDiv.className = 'ie-right ie-left-mar-10 ie-right-mar-0';
                        		main.appendChild(activateDiv);
                        	}
                    }
                    else {
                    	$('#errDisplay').html(handle.viewStatus.messages.user);
                        ie.progressEnd();
                    }
                }, 
                ie.config.RESPONSE_TYPE
        );
    	$(this).dialog("close");
    	}
    	else
    	{
            alert('Please provide reason for action.');
    		$('#dialog').dialog('option', 'buttons', { "Ok": function() { $(this).dialog("close"); } });
    	}
    	$(this).dialog("close");
    } });
    $('#dialog').dialog('option', 'modal', true);
    $('#dialog').dialog('open');
}
//Called when a user is activated
function adminReasonActivate(userKey)
{
	var url = '';
//	Opens a dialog box that takes the reason for activating a user
	$('#dialog').html('<div class="hiddenInViewSource" style="padding-left: 70px; margin-top: 20px;">' 
					+	'<b>Enter reason for activating: </b><textarea id="inputVal"></textarea><br/>'
					+	'</div>');
	$('#dialog').dialog('option', 'title', 'Input Reason!');
    $('#dialog').dialog('option', 'buttons', { "Submit": function() {
    	if($.trim($('#inputVal').val()) != null && $.trim($('#inputVal').val()) != '')
    	{
    	url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/activateUser/' + userKey);
    	$.post(
                url,
                {adminReason: $.trim($('#inputVal').val())},// saved reason for activation
                function (handle) {
                    if(ie.config.SUCCESS == handle.viewStatus.status) {
                		if(handle.viewStatus.data.user.userKey == userKey && handle.viewStatus.data.user.status == "active")
                    	{
                			var main = document.getElementById(userKey);
                    		var activateDiv = document.getElementById('activateDiv' + userKey);
                    		main.removeChild(activateDiv);
                    		var banDiv = document.getElementById('banDiv' + userKey);
                    		if(banDiv != null)
                    		{
                    			main.removeChild(banDiv);
                    		}
                    		var banDiv = document.createElement('div');
                    		banDiv.setAttribute('id','banDiv' + userKey);
                    		banDiv.innerHTML = '<a href="javascript:void(0);" onclick="adminReasonBan(' + '\'' + userKey + '\'' + ')"  class="ie-nm-blu">Ban</a> |';
                    		banDiv.className = 'ie-right ie-left-mar-10 ie-right-mar-0';
                    		main.appendChild(banDiv);
                    	}
                }
                    else {
                    	$('#errDisplay').html(handle.viewStatus.messages.user);
                        ie.progressEnd();
                    }
                }, 
                ie.config.RESPONSE_TYPE
        );
    	}
    	else
    	{
            alert('Please provide reason for action.');
    		$('#dialog').dialog('option', 'buttons', { "Ok": function() { $(this).dialog("close"); } });
    	}
    	 $(this).dialog("close"); 
    } });
    $('#dialog').dialog('option', 'modal', true);
    $('#dialog').dialog('open');
}



