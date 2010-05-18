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
 * file is responsible to handle all javascript tasks on ShowProjectForm require
 * jquery 1.3.2
 */
// onload calls the method to load all the projects
google.setOnLoadCallback(function() {
    ie.progressStart();
    //Call to load all the projects
    project.loadProjects(0);
});


project = {
    
}

// Fetch all the projects and render through an HTML
project.loadProjects = function(offset, heading) {
    var url = '';
        url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'projects/get.json?startIndex=' + offset);
    $.get(
            url, 
            {}, 
            function (handle) {
                // render all the projects
                if(handle != ''){
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                        // TODO: Abhishek, Need to handle with Template :)
                        $('#listProject').html('');
                        var counter = 0;
                        for (i in handle.viewStatus.data.projects) {
                            counter++;
                            if(counter > ie.config.RECORD_PER_PAGE)
                                break;
                            $('#listProject').append(createHtml(handle.viewStatus.data.projects[i]));
                        }
                        // Handles Pagination
                        if(undefined != handle.viewStatus.data.paging)
                            $('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'project.loadProjects'));
                        ie.progressEnd();
                    }
                    else {
                        $('#listProject').html(handle.viewStatus.messages.projects);
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
// HTML to render all the projects data
function createHtml(jsonData) {
    var output = '';
    output += '<div style="width:100%; height:180px;" class="ie-top-mar-25">';
    output += '		<div class="ie-left-mar-20 ie-text ie-projects">';
    output += '			<h1 class="blu-heading">';
    output += '    		<a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'projects/show/'+ jsonData['key']) + '">';
    output += 			ie.escapeHTML(jsonData['name']) + '</a></h1><br />';
    output += '			<table width="98%" border="0" align="left" cellpadding="10%" cellspacing="10">';
    output += '				<tr>';
    output += '  				<td width="80px">';
    if(undefined != jsonData.logo && undefined != jsonData.logo.bytes && '' != jsonData.logo.bytes) {
        output += '					<img src= "/showImage/' + jsonData['key'] + '" height="60px" width="60px"/>';
    }
    else {
        output += '         		<img src= "' + ie.config.PUBLIC_URL + 'images/img.gif" height="60px" width="60px"/>';
    }
    output += '  				</td>';
    output += '  				<td>';
    output +=  						ie.escapeHTML(jsonData['descriptionAsString']) + '<br />';
    output += '						<strong>Last Updated: </strong>' + jsonData['updatedOn'] + '<br />';  
    output += '  				</td>';
    output += '				</tr>';
    output += '     </div>';
    output += '</div>';
    return output;
}
