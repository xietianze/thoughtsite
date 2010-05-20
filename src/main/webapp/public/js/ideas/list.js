google.setOnLoadCallback(function() {
    ie.progressStart();
    loadTagCloud();
    if($.url.param("type") && '' !=$.trim($.url.param("type"))) {
        switch($.url.param("type")){
        case idea.DEFAULT_TYPE :
            idea.setBrowseType(idea.DEFAULT_TYPE);
            break;
        case idea.SEARCH_TYPE :
            idea.setBrowseType(idea.SEARCH_TYPE);
            idea.setKeyword($.url.param("keyword"));
            break;
        case idea.TAG_TYPE :
            idea.setBrowseType(idea.TAG_TYPE);
            idea.browseByTag($.url.param("tag"));
            break;
        case idea.USER_TYPE :
            idea.setBrowseType(idea.USER_TYPE);
            break;
        default :
            idea.setBrowseType(idea.DEFAULT_TYPE);
        break;
        }
    }
    $("#sortIdeas").click( function() {
        $("ul#sort-dropdown").toggle(); return false;
    });
    idea.loadIdeas(0);
});

function handleVotingForm(isLoggedIn) {
    if(isLoggedIn) {
    }
    else {
    }
}

idea = {
    DEFAULT_TYPE : 'list',
    SEARCH_TYPE : 'search',
    TAG_TYPE : 'tag',
    USER_TYPE : 'user',
    DEFAULT_SORT_ORDER : 'publishDate',
    DATE_SORT_ORDER : 'publishDate',
    VOTE_SORT_ORDER : 'totalVotes',
    SORT_ORDER : 'publishDate',
    keyword : ''// for search
    
}
idea.setSortOrder = function (type) {
    if(undefined == type)
        this.SORT_ORDER = this.DEFAULT_SORT_ORDER;
    else
        this.SORT_ORDER = type;
}
idea.setKeyword = function (term) {
    this.keyword = term;
}
idea.setBrowseType = function (browseType) {
    this.browseBy = browseType;
}
idea.getBrowseType = function () {
    if(undefined == this.browseBy) {
        return this.DEFAULT_TYPE;
    }
    else {
        return this.browseBy;
    }
}
idea.browseByTag = function(tag) {
    this.setBrowseType(this.TAG_TYPE);
    this.tagName = tag;
    idea.loadIdeas(0);
}

idea.loadIdeas = function(offset, heading) {
    var url = '';
    if(this.getBrowseType() == this.DEFAULT_TYPE) {
        url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/get.json?startIndex=' + offset);
    }
    else if(this.getBrowseType() == this.TAG_TYPE){
        //url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/byTag/' + this.tagName + '/' + offset + '.json');
    	url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/byTag/' + this.tagName + '.json?startIndex=' + offset);
        $('#listHeading').html('Ideas by tag "'+ this.tagName +'"');
    }
    else if(this.getBrowseType() == this.SEARCH_TYPE){
        url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/search?keyword=' + this.keyword + '&offset');
        $('#listHeading').html('Search "'+ this.keyword +'"');
    }
    else if(this.getBrowseType() == this.USER_TYPE){
    }
    url = url + '&orderBy=' + this.SORT_ORDER;
    $.get(
            url, 
            {}, 
            function (handle) {
                // render most recent idea
                if(handle != ''){
                    if(handle.viewStatus.status == ie.config.SUCCESS) {
                        // TODO: Abhishek, Need to handle with Template :)
                        $('#listData').html('');
                        var counter = 0;
                        for (i in handle.viewStatus.data.ideas) {
                            counter++;
                            if(counter > ie.config.RECORD_PER_PAGE)
                                break;
                            $('#listData').append(createHtml(handle.viewStatus.data.ideas[i]));
                        }
                        if(undefined != handle.viewStatus.data.paging)
                            $('#pagination').html(ie.Paging.getHTML(handle.viewStatus.data.paging, 'idea.loadIdeas'));
                        if(counter > 1) {
                            $('#sortDate').click(function () {
                                idea.sort('date');
                            });
                            $('#sortVote').click(function () {
                                idea.sort('vote');
                            });
                        }
                        ie.progressEnd();
                    }
                    else {
                        $('#listData').html(handle.viewStatus.messages.ideas);
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

idea.sort = function (type) {
    if('vote' == type) {
        this.setSortOrder(this.VOTE_SORT_ORDER);
        this.loadIdeas(0);
    }
    else {
        this.setSortOrder(this.DATE_SORT_ORDER);
        this.loadIdeas(0);
        // by default sort on date
    }
}

function createHtml(jsonData) {
    var output = '';
    output += '<div style="width:100%; height:180px;"  >';
    if(ie.config.STATUS_PUBLISHED == jsonData['idea']['status']) {
        output += '        <div class="ie-left ie-votes">';
        output += '<div class="ie-top-mar-10 ie-center"><strong> Votes</strong>';
        output += '    </div>';
        output += '    <div class="ie-top-mar-10 ie-left">';
        output += '      <a href="#vote" onclick="voteIdea(\'' + jsonData['idea']['key'] + '\', true)"><img src="' + ie.config.PUBLIC_URL + 'images/hand-up.gif" alt="" width="25" height="30" hspace="10" border="0" /></a><strong>+ ';
        output += '<span id="pIdea' + jsonData['idea']['key'] + '">' + jsonData['idea']['totalPositiveVotes']+'</span></strong>';
        output += '    </div>';
        output += '    <div class="ie-clear ie-top-mar-5 ie-left" style="width: 85%">';
        output += '      <a href="#vote" onclick="voteIdea(\'' + jsonData['idea']['key'] + '\', false)"><img src="' + ie.config.PUBLIC_URL + 'images/hand-dwn.gif" alt="" width="25" height="30" hspace="10" border="0" /></a><strong>- ';
        output += '<span id="nIdea' + jsonData['idea']['key'] + '">' + jsonData['idea']['totalNegativeVotes']+'</span></strong>'; 
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
        output += '<div class="ie-top-mar-10 ie-center"><strong> Unpublished Idea</strong>';
        output += '    </div>';
        output += '  </div>';
    }
    output += '  <div class="ie-right ie-text ie-detail">';
    output += '    <h1 class="blu-heading">';
    if(ie.config.STATUS_SAVED == jsonData['idea']['status']) {
        output += '    <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/edit/'+ jsonData['idea']['key']) + '">';
    }
    else {
        output += '    <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/show/'+ jsonData['idea']['key']) + '">';
    }
    output += ie.escapeHTML(jsonData['idea']['title']) + '</a></h1><br />';
    output += ie.escapeHTML(jsonData['idea']['description']) + '<br/>';
    output += '    <div class="ie-left ie-detail-info1">';
    output += '      <strong>Tags:</strong>';
    if(undefined != jsonData.tags) {
        for(data in jsonData.tags) {
            output += '      <a href="javascript:void(0)" onclick="idea.browseByTag(\'' + jsonData.tags[data]['title'] + '\');" class="ie-nm-blu">' + jsonData['tags'][data]['title'] + '</a>';
        }
    }
    output += '    </div>';
    if(undefined != jsonData.user) {
        output += '    <div class="ie-right ie-detail-info2">';
        output += '      <strong>Last Updated: </strong>'+ jsonData['idea']['lastUpdated']+'<br />';
        output += '      <a href="' + ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'users/profile/'+ jsonData.user.userKey) + '" class="ie-nm-blu">' + jsonData.user.displayName + '</a><br />';
        output += '      <strong>' + jsonData.user.reputationPoints + ' Points</strong><br />';
        if(undefined != jsonData.user.userAwards)
            output += '      <strong>' + jsonData.user.userAwards + ' Awards</strong></div>';
        output += '  </div>';
    }
    output += '</div>';
    return output;
}

function voteIdea(ideaKey, isPositiveVote) {
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
                        $('#pIdea' + ideaKey).html(parseInt($('#pIdea' + ideaKey).html()) + 1);
                    }
                    else {
                        $('#nIdea' + ideaKey).html(parseInt($('#nIdea' + ideaKey).html()) + 1);
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

function loadTagCloud() {
    $.get(
            ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'tags/tagcloud' + '.json'), 
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
                    $('#cloud').html(handle.viewStatus.messages.tags);
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