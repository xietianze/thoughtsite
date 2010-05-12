google.setOnLoadCallback(function() {
    ie.progressStart();
    
    $('#tagKeyword').focus(function () {
        if($('#tagKeyword').val() == 'Search Tag') {
            $('#tagKeyword').val('');
        }
    });
    $('#tagKeyword').blur(function () {
        if($('#tagKeyword').val() == '') {
            $('#tagKeyword').val('Search Tag');
        }
    });
    $('#searchTag').click(function(){
        if('' == $.trim($('#tagKeyword').val()) || 'Search Tag' == $.trim($('#tagKeyword').val())) {
            alert('Please provide search keyword.');
            return false;
        }
        var elem = $('.page_sel');
        elem.removeClass('page_sel');
        elem.addClass('page_no');
        tagList.setListType('search');
        loadTags($('#tagKeyword').val());
    });
    // initialize history
    $.historyInit(loadTags, "tag-list");
    // start with char A
    var isFirstElem = true;
    $('.page_no').each(function() {
        // url with [A-Z]1, 
        $(this).attr('href', '#'+$(this).html());
        // so that we can easily handle browser back
        $(this).attr('id', 'tag-'+$(this).html());
        // override click
        $(this).click(function(){
            // for history management
            var hash = this.href;
            hash = hash.replace(/^.*#/, '');
            // moves to a new page. 
            // pageload is called at once. 
            // hash don't contain "#", "?"
            tagList.setListType('list');
            $.historyLoad(hash);
            return false;
        });
        // need to load first elements tag
        if(isFirstElem) {
            $(this).removeClass('page_no');
            $(this).addClass('page_sel');
            isFirstElem = false;
            loadTags($(this).html());
        }
    });
});

var tagList = {};
tagList.setListType = function(listType) {
    this.type = listType;
}
tagList.getListType = function() {
    if(undefined == this.type)
        return 'list';
    return this.type;
}
function loadTags(keyword) {
    var url = '';
    if(tagList.getListType() == 'list') {
        url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'tags/list/'+ keyword + '.json');
        if(undefined == keyword || '' == keyword) {
            initial = 'A';
        }
        selectLink(keyword);
    }
    else {
        url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'tags/search?keyword='+ keyword);
    }
    $.get(
            url, 
            {}, 
            function (handle) {
                // render most recent idea
                if(ie.config.SUCCESS == handle.viewStatus.status) {
                    // TODO: Abhishek, Need to handle with Template :)
                    if(tagList.getListType() == 'search') {
                        var html = ie.TagCloud.render(
                                handle.viewStatus.data.tags, 
                                {"maxWeight":10,"minWeight":10}, 
                                {title : 'title', weightage : 'weightage', css: 'tag'},
                                {url:ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/list?type=tag&tag=')}
                            );
                    }
                    else {
                        var html = ie.TagCloud.render(
                            handle.viewStatus.data.tags, 
                            handle.viewStatus.data.weights, 
                            {title : 'title', weightage : 'weightage', css: 'tag'},
                            {url:ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'ideas/list?type=tag&tag=')}
                        );
                    }
                    $('#cloud-main').html(html);
                    ie.progressEnd();
                }
                else if(ie.config.ERROR == handle.viewStatus.status) {
                    $('#cloud-main').html(handle.viewStatus.messages.tags);
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

function selectLink(initial) {
    var selectedElem = $('.page_sel');
    $(selectedElem).removeClass('page_sel');
    $(selectedElem).addClass('page_no');
    $('#tag-' + initial).removeClass('page_no');
    $('#tag-' + initial).addClass('page_sel');
}