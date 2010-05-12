// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * file is responsible to handle all javascript tasks on Projects Form on Admin Tab require
 * jquery 1.3.2
 */
// onload
google.setOnLoadCallback(function() {
    ie.progressStart();
    // Call to the method that renders all the projects
    project.loadProjects(0);
});


project = {
}

//Show all the Porjects.
project.loadProjects = function(offset) {
    var url = '';
    url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/projects.json?startIndex=' + offset);
    $.get(
            url, 
            {}, 
            function (handle) {
                // render projects
                if(handle != ''){
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                        // TODO: Abhishek, Need to handle with Template :)
                        $('#listData').html('');
                        var counter = 0;
                        for (i in handle.viewStatus.data.projects) {
                        	counter++;
                        	if(counter > ie.config.RECORD_PER_PAGE_ADMIN)
                            break;
                            $('#listData').append(createHtml(handle.viewStatus.data.projects[i]));
                        }
//                        Handles pagination
                        if(undefined != handle.viewStatus.data.paging)
                        {
                        	$('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'project.loadProjects'));
                        }
                        ie.progressEnd();
                    }
                    else {
                        $('#listData').html(handle.viewStatus.messages.projects);
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

// HTML that renders the page
function createHtml(jsonData) {
    var output = '';
    output += '<div id="ProjectData" style="width:100%;  class="ie-top-mar-25">';
    output += '<div id="' + jsonData['key'] + '" class="ie-adm-lt-bar ie-clear">';
    output += '			<div class="ie-left ie-left-mar-5 ie-nm-blu">';
    output += 			ie.escapeHTML(jsonData['name']);
    output += '     	</div>';
    output += '    <div class="ie-right ie-left-mar-10 ie-right-mar-0">';
    output += '      <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'projects/show/'+ jsonData['key']) + ' " onclick="window.open(this.href,\'window_name\',\'options\'); return false;" class="ie-nm-blu">View</a> |';
    output += '      <a href="javascript:void(0);" onclick="adminReasonDelete(\'' + jsonData['key'] + '\')" class="ie-nm-blu">Delete</a>';
    output += '  </div>';
    output += '</div>';
    output += '</div>';
    return output;
}

function adminReasonDelete(projectKey)
{
	var url = '';
	$('#dialog').html('<div class="hiddenInViewSource" style="padding-left: 70px; margin-top: 20px;">' 
					+	'<b>Enter reason for deleting: </b><textarea id="inputVal"></textarea><br/>'
					+	'</div>');
	$('#dialog').dialog('option', 'title', 'Input Reason!');
    $('#dialog').dialog('option', 'buttons', { "Submit": function() {
    	if($.trim($('#inputVal').val()) != null && $.trim($('#inputVal').val()) != '')
    	{
    	url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'admin/deleteProject/' + projectKey);
    	$.post(
                url,
                {adminReason: $.trim($('#inputVal').val())},
                function (handle) {
                    if(ie.config.SUCCESS == handle.viewStatus.status) {
                    	project.loadProjects(0);
                    }
                    else {
                    	var div = document.getElementById(projectKey);
                   		var d = document.getElementById('ProjectData');
                    	d.removeChild(div);
                    	$('#errDisplay').html(handle.viewStatus.messages.projects);
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
