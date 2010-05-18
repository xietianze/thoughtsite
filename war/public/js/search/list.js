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

google.setOnLoadCallback(function() {
    ie.progressStart();
    idea.setKeyword($.url.param("keyword"));
    $('#keyword').val($.url.param("keyword"));
    loadCategories();
    var $tabs = $('#tab-menu').tabs();
    $tabs.tabs('option', 'selected', 0); // => 0
    loadIdeas(0);

    $('#tab-menu').bind('tabsselect', function(event, ui) {
        switch(ui.panel.id){
        case 'search-ideas':
            loadIdeas(0);
            break;
        case 'search-projects':
            loadProjects(0);
            break;
        default :
            loadIdeas(0);
            break;
        }
    });
});

var idea = {
  keyword : '',// for search
  categoryKey : ''   
};

idea.setKeyword = function (keyword) {
    this.keyword = keyword;
}
idea.getKeyword = function () {
    return this.keyword;
}
idea.setCategoryKey = function (key) {
    this.categoryKey = key;
}
idea.getCategoryKey = function () {
    return this.categoryKey;
}

function loadIdeas(offset) {
    $('#tag-cloud-sec').show();
    var url = '';
    if('' == idea.getCategoryKey()) {
        url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'search/ideas.json?startIndex='+ offset + '&keyword=' + idea.getKeyword())
    }
    else {
        url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'search/ideas.json?startIndex='+ offset + '&categoryKey=' + idea.getCategoryKey() + '&keyword=' + idea.getKeyword())
    }
    $.get(
            url, 
            {}, 
            function (handle) {
                // render most recent idea
                if(handle != ''){
                    $('#search-ideas').html('');
                    $('#pagination').html('');
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                        // TODO: Abhishek, Need to handle with Template :)
                        $('#search-ideas').html('');
                        $('#pagination').html('');
                        for (i in handle.viewStatus.data.ideas) {
                            $('#search-ideas').append(createIdeaHtml(handle.viewStatus.data.ideas[i]));
                        }
                        if(undefined != handle.viewStatus.data.paging)
                            $('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'loadIdeas'));
                        ie.progressEnd();
                    }
                    else {
                        $('#search-ideas').html(handle.viewStatus.messages.ideas);
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

function loadProjects(offset) {
    $('#tag-cloud-sec').hide();
    $.get(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'search/projects.json?startIndex='+ offset + '&keyword=' + idea.getKeyword()), 
            {}, 
            function (handle) {
                // render most recent idea
                $('#search-ideas').html('');
                $('#pagination').html('');
                if(handle != ''){
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                        // TODO: Abhishek, Need to handle with Template :)
                        $('#search-projects').html('');
                        for (i in handle.viewStatus.data.projects) {
                            $('#search-projects').append(createProjectHtml(handle.viewStatus.data.projects[i]));
                        }
                        if(undefined != handle.viewStatus.data.paging)
                            $('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'loadProjects'));
                        //$('#pagination').html(ie.Paging.getHTMLByPage(60,6));
                        ie.progressEnd();
                    }
                    else {
                        $('#search-projects').html(handle.viewStatus.messages.projects);
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


function createProjectHtml(jsonData) {
    var output = '';
    output += '<div class="ie-right ie-text ie-search-detail">';
    output += '<h1 class="blu-heading ie-bottom-mar-5">' + ie.escapeHTML(jsonData.project.name) + '</h1>';
    output += ie.escapeHTML(jsonData.project.description) + '<br />';
    output += '<div class="ie-right"><a href="'+ ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'projects/show/'+ jsonData['project']['key']) +'" class="ie-sm-blu">view</a></div>';
    output += '</div>';
    if(undefined != jsonData.comments) {
        for(var i=0; i < jsonData.comments.length; i ++)
            output += createCommentHtml(jsonData.comments[i], jsonData['project']['key'], ie.escapeHTML(jsonData.project.name), 'project');
    }
   return output;
}

function createIdeaHtml(jsonData) {
    var output = '';
    output += '<div class="ie-right ie-text ie-search-detail">';
    output += '<h1 class="blu-heading ie-bottom-mar-5">' + ie.escapeHTML(jsonData.idea.title) + '</h1>';
    output += ie.escapeHTML(jsonData.idea.description) + '<br />';
    output += '<div class="ie-right"><a href="'+ ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/show/'+ jsonData['idea']['key']) +'" class="ie-sm-blu">view</a></div>';
    output += '</div>';
    if(undefined != jsonData.comments) {
        for(var i=0; i < jsonData.comments.length; i ++)
            output += createCommentHtml(jsonData.comments[i], jsonData['idea']['key'], ie.escapeHTML(jsonData.idea.title), 'idea');
    }
   return output;
}

function createCommentHtml(jsonData, key, title, type) {
    var output = '';
    output += '<div class="ie-comment-width ie-right" ><div class="ie-right ie-text ie-comment-detail">';
    output += '<h2 class="blu-heading">'+ ie.escapeHTML(title) + '</h2>';
    output += ie.escapeHTML(jsonData.text) + '<br/>'; 
    if('idea' == type) {
        output += '<div class="ie-right"><a href="'+ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/show/'+ key)+'" class="ie-sm-blu">View</a></div>';
    }
    else {
        output += '<div class="ie-right"><a href="'+ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'projects/show/'+ key)+'" class="ie-sm-blu">View</a></div>';
    }
    output += '</div></div>';
    return output;
}

function loadCategories () {
    $.get(
            ie.buildUrl(ie.config.REQUEST_FRIEND_CONNECT,
                    'ideas/categories.json'),
            {},
            function(handle) {
                if (handle.viewStatus.status == ie.config.SUCCESS) {
                    var html = '<ul class="ie-search-list">';
                    for(i in handle.viewStatus.data.categories) {
                        html += '<li class="ie-search-list">';
                        html += '<a href="javascript:void(0);" onclick="filter(\'';
                        html += handle.viewStatus.data.categories[i].key;
                        html += '\')" class="ie-search-list-blu">';
                        html += handle.viewStatus.data.categories[i].name;
                        html += '</a></li>';
                    }
                    $('#category-filter').html(html);
                    
                } else {
                    ie.globalError();
                }
            },
            ie.config.RESPONSE_TYPE
        );
}

function filter (categoryKey) {
    idea.setCategoryKey(categoryKey);
    loadIdeas(0);
}
