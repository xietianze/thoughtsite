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
 * file is responsible to handle all javascript tasks on Ideas Form on Admin Tab require
 * jquery 1.3.2
 */
// onload
google.setOnLoadCallback(function() {
    ie.progressStart();
 // Call to the method that renders all the ideas
    idea.loadIdeas(0);
});


idea = {
}

//Show all the Ideas.
idea.loadIdeas = function(offset) {
    var url = '';
    url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/ideas.json?startIndex=' + offset);
    $.get(
            url, 
            {}, 
            function (handle) {
                // render ideas
                if(handle != ''){
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                        $('#ideaData').html('');
                        var counter = 0;
                        for (i in handle.viewStatus.data.ideas) {
                        	counter++;
                        	if(counter > ie.config.RECORD_PER_PAGE_ADMIN)
                            break;
                            $('#ideaData').append(createHtml(handle.viewStatus.data.ideas[i]));
                        }
                        // Handles pagination
                        if(undefined != handle.viewStatus.data.paging)
                        {
                        	$('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'idea.loadIdeas'));
                        }
                        ie.progressEnd();
                    }
                    else {
                        $('#ideaData').html(handle.viewStatus.messages.ideas);
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

// HTML for rendering ideas
function createHtml(jsonData) {
    var output = '';
    //output += '<div id="IdeaData" style="width:100%;  class="ie-top-mar-25">';
    output += '		<div id="' + jsonData['idea']['key'] + '" class="ie-adm-lt-bar ie-clear">';
    output += '			<div class="ie-left ie-left-mar-5 ie-nm-blu">';
    output += 			ie.escapeHTML(jsonData['idea']['title']);
    output += '     	</div>';
    output += '    <div class="ie-right ie-left-mar-10 ie-right-mar-0">';
    output += '      <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/show/'+ jsonData['idea']['key']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View</a> |';
    output += '      <a href="javascript:void(0);" onclick="adminReasonDelete(\'' + jsonData['idea']['key'] + '\')" class="ie-nm-blu">Delete</a>';
    output += '  </div>';
   // output += '</div>';
    return output;
}

// It is called when admin deletes an Idea
function adminReasonDelete(ideaKey)
{
	var url = '';
	$('#dialog').html('<div class="hiddenInViewSource" style="padding-left: 70px; margin-top: 20px;">' 
					+	'<b>Enter reason for deleting: </b><textarea id="inputVal"></textarea><br/>'
					+	'</div>');
	$('#dialog').dialog('option', 'title', 'Input Reason!');
    $('#dialog').dialog('option', 'buttons', { "Submit": function() {
    	if($.trim($('#inputVal').val()) != null && $.trim($('#inputVal').val()) != '')
    	{
    	url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/delete/' + ideaKey);
    	$.post(
                url,
                {adminReason: $.trim($('#inputVal').val())},
                function (handle) {
                    if(ie.config.SUCCESS == handle.viewStatus.status) {
                    	for (i in handle.viewStatus.data.ideas) {
                            if(handle.viewStatus.data.ideas[i].key == ideaKey)
                        	{
                            	
                        	}
                            else
                            {
                            	var div = document.getElementById(ideaKey);
                           		var d = document.getElementById('ideaData');
                            	d.removeChild(div);
                            	break;
                            }
                        }
                    }
                    else {
                    	var div = document.getElementById(ideaKey);
                   		var d = document.getElementById('ideaData');
                    	d.removeChild(div);
                    	$('#errDisplay').html(handle.viewStatus.messages.ideas);
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
            $(this).dialog("close"); 
    	}
    	$(this).dialog("close");
    } });
    $('#dialog').dialog('option', 'modal', true);
    $('#dialog').dialog('open');
}
