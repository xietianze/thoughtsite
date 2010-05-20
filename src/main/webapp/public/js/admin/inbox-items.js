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
 * file is responsible to handle all javascript tasks on AdminRequestsForm require
 * jquery 1.3.2
 */
// onload 
google.setOnLoadCallback(function() {
	// Call to the method that renders all the pending admin requests. 
	 inboxDisplay.loadItems(0);
	})

var inboxDisplay = {
  
}
//Show all the admin requests pending.
inboxDisplay.loadItems = function(offset, heading) {
    var url = '';
        url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/action.json?startIndex=' + offset);
    $.get(
            url, //Fetch all the pending admin requests
            {}, 
            function (handle) {
                // render all the pending admin requests
                if(handle != ''){
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                    	$('#inboxData').html('');
                    	var counter = 0;
                        for (i in handle.viewStatus.data.adminRequests) {
                        	counter++;
                        	if(counter > ie.config.RECORD_PER_PAGE_ADMIN)
                            break;
                            $('#inboxData').append(createInboxItemHtml(handle.viewStatus.data.adminRequests[i]));
                        }
                        //paging 
                        if(undefined != handle.viewStatus.data.paging)
                        {
                        	$('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'inboxDisplay.loadItems'));
                        }
                            
                        ie.progressEnd();
                    }
                    else { //show - no records found or Error  message in case of failure
                    	$('#inboxData').html(handle.viewStatus.messages.adminRequests);
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
//HTML - Admin Requests
function createInboxItemHtml(jsonData) {
    var output = '';
    output += '		<div id="' + jsonData['key'] + "All" + '" class="ie-adm-lt-bar ie-clear">';
    output += '			<div class="ie-left ie-left-mar-5 ie-nm-blu">';
    if(ie.config.STATUS_OBJECTIONABLE == jsonData['requestType']) {
    	if(ie.config.ENTITY_IDEA == jsonData['entityType']) {
    			output += '       <span class="ie-lb-comment">Flagged Idea</span>';
    			output += '       <a href="#" class="ie-nm-blu">' + ie.escapeHTML(jsonData['entityTitle']) + '</a>';
    	}
    }
    if(ie.config.STATUS_DUPLICATE == jsonData['requestType']) {
        output += '        <span class="ie-lb-dup">Duplicate</span>';
        output += '       <a href="#" class="ie-nm-blu">' + ie.escapeHTML(jsonData['entityTitle']) + '</a>';
    }
    if(ie.config.STATUS_OBJECTIONABLE == jsonData['requestType']) {
    	if(ie.config.ENTITY_IDEA_COMMENT == jsonData['entityType']) {
    			output += '       <span class="ie-lb-banned">Flagged Idea Comment</span>';
    			output += '       <a href="#" class="ie-nm-blu">' + ie.escapeHTML(jsonData['entityTitle']) + '</a>';
    		}
    	}
    if(ie.config.STATUS_OBJECTIONABLE == jsonData['requestType']) {
    	if(ie.config.ENTITY_PROJECT_COMMENT == jsonData['entityType']) {
    			output += '       <span class="ie-lb-active">Flagged Project Comment</span>';
    			output += '       <a href="#" class="ie-nm-blu">' + ie.escapeHTML(jsonData['entityTitle']) + '</a>';
    		}
    	}
    output += '  </div>';
    output += '		<div class="ie-right ie-left-mar-10 ie-right-mar-0">';
    if(ie.config.STATUS_DUPLICATE == jsonData['requestType']) {
    	if(undefined != jsonData.otherInfo)
        output += '        <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/show/'+ jsonData.otherInfo[0].split('~~')[1]) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View Original</a> |';
        output += '        <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/show/'+ jsonData['entityKey']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View Duplicate</a> |';
    }
    else if(ie.config.ENTITY_IDEA == jsonData['entityType']) {
        output += '        <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/show/'+ jsonData['entityKey']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View</a> |';
    }
    else if(ie.config.ENTITY_IDEA_COMMENT == jsonData['entityType'])
    {
    	output += '        <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/viewIdea/'+ jsonData['entityKey']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View</a> |';
    }
    else if(ie.config.ENTITY_PROJECT_COMMENT == jsonData['entityType'])
    {
    	output += '        <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/viewProject/'+ jsonData['entityKey']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View</a> |';
    }
    output += '      <a href="javascript:void(0);" onclick="adminReasonApprove(\'' + jsonData['key'] + '\',  \'' + jsonData['entityType'] + '\')" class="ie-nm-blu">Approve</a>  |';
    output += '      <a href="javascript:void(0);" onclick="adminReasonDeny(\'' + jsonData['key'] + '\',  \'' + jsonData['entityType'] + '\')" class="ie-nm-blu">Deny</a>';
    output += '  </div>';
    output += '  </div>';
    return output;
}
// It is called when Admin denies a request
function adminReasonDeny(itemKey, type)
{
	var url = '';
	$('#dialog').html('<div class="hiddenInViewSource" style="padding-left: 70px; margin-top: 20px;">' 
					+	'<b>Enter reason for denying: </b><textarea id="inputVal"></textarea><br/>'
					+	'</div>');
	$('#dialog').dialog('option', 'title', 'Input Reason!');
    $('#dialog').dialog('option', 'buttons', { "Submit": function() {
    	if($.trim($('#inputVal').val()) != null && $.trim($('#inputVal').val()) != '')
    	{
    	url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/deny/' + itemKey);
    	$.post(
                url,
                {adminReason: $.trim($('#inputVal').val())},
                function (handle) {
                    if(ie.config.SUCCESS == handle.viewStatus.status) {
                    	for (i in handle.viewStatus.data.adminRequests) {
                            if(handle.viewStatus.data.adminRequests[i].key == itemKey)
                        	{
                            	
                        	}
                            else
                            {
                            	//To do: Surabhi in Jquery
                            	var div = document.getElementById(itemKey + "All");
                            	var div2 = document.getElementById(itemKey + type);
                            	if(type == 'ProjectComment')
                            	{
                            		var i = document.getElementById('inboxData');
                            	}
                            	else if(type == 'IdeaComment')
                            	{
                            		var i = document.getElementById('inboxData');
                            	}
                            	else if(type == 'Idea')
                            	{
                            		var i = document.getElementById('inboxData');
                            	}
                            	else if(type == 'Duplicate')
                            	{
                            		var i = document.getElementById('inboxData');
                            	}
                            	//Remove the one from the list which is denied
                            	i.removeChild(div);
                            	break;
                            }
                        }
                    }
                    else {
                    	var div = document.getElementById(itemKey + "All");
                    	var div2 = document.getElementById(itemKey + type);
                    	if(type == 'ProjectComment')
                    	{
                    		var i = document.getElementById('inboxData');
                    	}
                    	else if(type == 'IdeaComment')
                    	{
                    		var i = document.getElementById('inboxData');
                    	}
                    	else if(type == 'Idea')
                    	{
                    		var i = document.getElementById('inboxData');
                    	}
                    	else if(type == 'Duplicate')
                    	{
                    		var i = document.getElementById('inboxData');
                    	}
//                    	Remove the one from the list which is denied
                    	i.removeChild(div);
                    	$('#errDisplay').html(handle.viewStatus.messages.adminRequests);
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
//It is called when Admin approves a request
function adminReasonApprove(itemKey, type)
{
	var url = '';
	$('#dialog').html('<div class="hiddenInViewSource" style="padding-left: 70px; margin-top: 20px;">' 
					+	'<b>Enter reason for Approving: </b><textarea id="inputVal"></textarea><br/>'
					+	'</div>');
	$('#dialog').dialog('option', 'title', 'Input Reason!');
    $('#dialog').dialog('option', 'buttons', { "Submit": function() {
    	if($.trim($('#inputVal').val()) != null && $.trim($('#inputVal').val()) != '')
    	{
    	url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/approve/' + itemKey);
    	$.post(
                url,
                {adminReason: $.trim($('#inputVal').val())},
                function (handle) {
                    if(ie.config.SUCCESS == handle.viewStatus.status) {
                    	for (i in handle.viewStatus.data.adminRequests) {
                            if(handle.viewStatus.data.adminRequests[i].key == itemKey)
                        	{
                            	
                        	}
                            else
                            {
                            	var div = document.getElementById(itemKey + "All");
                            	var div2 = document.getElementById(itemKey + type);
                            	if(type == 'ProjectComment')
                            	{
                            		var d = document.getElementById('flagProjectCommentData');
                            		var i = document.getElementById('inboxData');
                            	}
                            	else if(type == 'IdeaComment')
                            	{
                            		var d = document.getElementById('flagIdeaCommentData');
                            		var i = document.getElementById('inboxData');
                            	}
                            	else if(type == 'Idea')
                            	{
                            		var d = document.getElementById('flagIdeaData');
                            		var i = document.getElementById('inboxData');
                            	}
                            	else if(type == 'Duplicate')
                            	{
                            		var d = document.getElementById('duplicateData');
                            		var i = document.getElementById('inboxData');
                            	}
                            	i.removeChild(div);
                            	d.removeChild(div2);
                            	break;
                            }
                        }
                    }
                    else {
                    	var div = document.getElementById(itemKey + "All");
                    	var div2 = document.getElementById(itemKey + type);
                    	if(type == 'ProjectComment')
                    	{
                    		var d = document.getElementById('flagProjectCommentData');
                    		var i = document.getElementById('inboxData');
                    	}
                    	else if(type == 'IdeaComment')
                    	{
                    		var d = document.getElementById('flagIdeaCommentData');
                    		var i = document.getElementById('inboxData');
                    	}
                    	else if(type == 'Idea')
                    	{
                    		var d = document.getElementById('flagIdeaData');
                    		var i = document.getElementById('inboxData');
                    	}
                    	else if(type == 'Duplicate')
                    	{
                    		var d = document.getElementById('duplicateData');
                    		var i = document.getElementById('inboxData');
                    	}
                    	i.removeChild(div);
                    	d.removeChild(div2);
                    	$('#errDisplay').html(handle.viewStatus.messages.adminRequests);
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

//Enhancements - Filter:

//function createFlagProjectCommentDataHtml(jsonData) {
//var output = '';
//
//if(ie.config.STATUS_OBJECTIONABLE == jsonData['requestType']) {
//	if(ie.config.ENTITY_PROJECT_COMMENT == jsonData['entityType']) {
//	output += '		<div id="' + jsonData['key'] + jsonData['entityType'] + '" class="ie-adm-lt-bar ie-clear">';
//	output += '			<div class="ie-left ie-left-mar-5 ">';
//  output += '        		<span class="ie-lb-active">Flagged Project Comment</span>';
//  output += '       		<a href="#" class="ie-nm-blu">' + ie.escapeHTML(jsonData['entityTitle']) + '</a>';
//  output += '  		</div>';
//  output += '			<div class="ie-right ie-left-mar-10 ie-right-mar-0">';
//  output += '        <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/viewProject/'+ jsonData['entityKey']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View</a> |';
//  output += '      		<a href="javascript:void(0);" onclick="adminReasonApprove(\'' + jsonData['key'] + '\',  \'' + jsonData['entityType'] + '\')" class="ie-nm-blu">Approve</a>  |';
//  output += '      		<a href="javascript:void(0);" onclick="adminReasonDeny(\'' + jsonData['key'] + '\',  \'' + jsonData['entityType'] + '\')" class="ie-nm-blu">Deny</a>';
//  output += '  		</div>';
//  output += '  	</div>';
//	}
//}
//return output;
//}
//
//function createFlagIdeaCommentDataHtml(jsonData) {
//var output = '';
//
//if(ie.config.STATUS_OBJECTIONABLE == jsonData['requestType']) {
//	if(ie.config.ENTITY_IDEA_COMMENT == jsonData['entityType']) {
//	output += '		<div id="' + jsonData['key'] + jsonData['entityType'] + '" class="ie-adm-lt-bar ie-clear">';
//	output += '			<div class="ie-left ie-left-mar-5 ">';
//  output += '        		<span class="ie-lb-banned">Flagged Idea Comment</span>';
//  output += '       		<a href="#" class="ie-nm-blu">' + ie.escapeHTML(jsonData['entityTitle']) + '</a>';
//  output += '  		</div>';
//  output += '			<div class="ie-right ie-left-mar-10 ie-right-mar-0">';
//  output += '        <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/viewIdea/'+ jsonData['entityKey']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View</a> |';
//  output += '      		<a href="javascript:void(0);" onclick="adminReasonApprove(\'' + jsonData['key'] + '\',  \'' + jsonData['entityType'] + '\')" class="ie-nm-blu">Approve</a>  |';
//  output += '      		<a href="javascript:void(0);" onclick="adminReasonDeny(\'' + jsonData['key'] + '\',  \'' + jsonData['entityType'] + '\')" class="ie-nm-blu">Deny</a>';
//  output += '  		</div>';
//  output += '  	</div>';
//	}
//}
//return output;
//}
//
//function createFlagIdeaDataHtml(jsonData) {
//var output = '';
//
//if(ie.config.STATUS_OBJECTIONABLE == jsonData['requestType']) {
//	if(ie.config.ENTITY_IDEA == jsonData['entityType']) {
//	output += '		<div id="' + jsonData['key'] + jsonData['entityType'] + '" class="ie-adm-lt-bar ie-clear">';
//	output += '			<div class="ie-left ie-left-mar-5 ">';
//  output += '        		<span class="ie-lb-comment">Flagged Idea</span>';
//  output += '       		<a href="#" class="ie-nm-blu">' + ie.escapeHTML(jsonData['entityTitle']) + '</a>';
//  output += '  		</div>';
//  output += '			<div class="ie-right ie-left-mar-10 ie-right-mar-0">';
//  output += '        		<a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/show/'+ jsonData['entityKey']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View</a> |';
//  output += '      		<a href="javascript:void(0);" onclick="adminReasonApprove(\'' + jsonData['key'] + '\',  \'' + jsonData['entityType'] + '\')" class="ie-nm-blu">Approve</a>  |';
//  output += '      		<a href="javascript:void(0);" onclick="adminReasonDeny(\'' + jsonData['key'] + '\',  \'' + jsonData['entityType'] + '\')" class="ie-nm-blu">Deny</a>';
//  output += '  		</div>';
//  output += '  	</div>';
//	}
//}
//return output;
//}
//
//function createDuplicateDataHtml(jsonData) {
//var output = '';
//
//if(ie.config.STATUS_DUPLICATE == jsonData['requestType']) {
//	output += '		<div id="' + jsonData['key'] + jsonData['entityType'] + '" class="ie-adm-lt-bar ie-clear">';
//	output += '			<div class="ie-left ie-left-mar-5 ">';
//  output += '        		<span class="ie-lb-dup">Duplicate</span>';
//  output += '       		<a href="#" class="ie-nm-blu">' + ie.escapeHTML(jsonData['entityTitle']) + '</a>';
//  output += '  		</div>';
//  output += '			<div class="ie-right ie-left-mar-10 ie-right-mar-0">';
//  output += '        		<a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/show/'+ jsonData['entityKey']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View</a> |';
//  output += '      		<a href="javascript:void(0);" onclick="adminReasonApprove(\'' + jsonData['key'] + '\',  \'' + jsonData['entityType'] + '\')" class="ie-nm-blu">Approve</a>  |';
//  output += '      		<a href="javascript:void(0);" onclick="adminReasonDeny(\'' + jsonData['key'] + '\',  \'' + jsonData['entityType'] + '\')" class="ie-nm-blu">Deny</a>';
//  output += '  		</div>';
//  output += '  	</div>';
//}
//return output;
//}
//function setFilter(val)
//{
//	if(val == 'Select')
//	{
//		$('#inboxData').show();
//		$('#duplicateData').hide();
//		$('#flagIdeaData').hide();
//		$('#flagIdeaCommentData').hide();
//		$('#flagProjectCommentData').hide();
//	}
//	else if(val == 'Duplicate')
//	{
//		$('#duplicateData').show();
//		$('#flagIdeaData').hide();
//		$('#flagIdeaCommentData').hide();
//		$('#flagProjectCommentData').hide();
//		$('#inboxData').hide();
//	}
//	else if(val == 'Flagged Idea')
//	{
//		$('#duplicateData').hide();
//		$('#flagIdeaData').show();
//		$('#flagIdeaCommentData').hide();
//		$('#flagProjectCommentData').hide();
//		$('#inboxData').hide();
//	}
//	else if(val == 'Flagged Idea Comment')
//	{
//		$('#duplicateData').hide();
//		$('#flagIdeaData').hide();
//		$('#flagIdeaCommentData').show();
//		$('#flagProjectCommentData').hide();
//		$('#inboxData').hide();
//	}
//	else if(val == 'Flagged Project Comment')
//	{
//		$('#duplicateData').hide();
//		$('#flagIdeaData').hide();
//		$('#flagIdeaCommentData').hide();
//		$('#flagProjectCommentData').show();
//		$('#inboxData').hide();
//	}
//}