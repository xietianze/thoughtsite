// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * file is responsible to handle all javascript tasks on showIdeaForm
 * require jquery 1.3.2 
 */
// onload set form focus validations
google.setOnLoadCallback(function() {
    // User require login before save idea
    ie.Login.setLoggedinUserCallback('handleSaveIdeaForm');
    ie.Login.setLoggedinUserCallback('handlePublishIdeaForm');
    // categories required for the form
    ie.loadCategories('category', $('#selectedCategory').val());

    // set description char limit to 3000 chars
    $('#description').limit('3000','#charsLeftDescription');
    // set targetAudience char limit to 500 chars
    $('#targetAudience').limit('500','#charsLeftTargetAudience');
    // set monetization char limit to 500 chars
    $('#monetization').limit('500','#charsLeftMonetization');
    // set competition char limit to 500 chars
    $('#competition').limit('500','#charsLeftCompetition');
    
    $("#tags").autocomplete(ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'tags/suggest/'), {
        width: 200,
        dataType: "json",
        parse: function(handle) {
            if(handle.viewStatus.status == ie.config.SUCCESS) {
                return $.map(handle.viewStatus.data.tags, function(row) {
                    return {
                        data: row,
                        value: row.key,
                        result: row.title
                    }
                });
            }
            else {
                return;
            }
        },
        formatItem: function(row) {
            // don't show the current month in the list of values (for whatever reason)
            return row.title;
        },
        multiple: true,
        matchContains: false,
        highlight: false,
        formatted: false
    });
});


// handle save idea post
function saveIdea() {
    // only title field is required to save an idea
    if($.trim($('#title').val()) == '' || $.trim($('#title').val()) == 'Idea Title') {
        $('#errDisplay').html('Please add Idea title');
        $('#errDisplay').show();
        return false;
    }
    ie.progressStart();
   
    // create post data
    var postdata = {
        'title' : $.trim($('#title').val()),
        'description' : $.trim($('#description').val()),
        'targetAudience' : $.trim($('#targetAudience').val()),
        'monetization' : $.trim($('#monetization').val()),
        'competition' : $.trim($('#competition').val()),
        'tags' : $.trim($('#tags').val()),
        'ideaCategoryKey' : $('#category').val(),
        'ideaRightsGivenUp' : $('#ideaRightsGivenUp').is(':checked'),
        'ipGivenUp' :$('#ipGivenUp').is(':checked') 
    };
    if('' != $.trim($('#key').val())) {
        postdata['key'] = $.trim($('#key').val());
    }
    // post data
    $.post(ie.buildUrl(ie.config.REQUEST_FRIEND_CONNECT, 'ideas/save.json'),
           postdata, 
           saveIdeaResponse, 
           ie.config.RESPONSE_TYPE
    );
}

function saveIdeaResponse (handle) {
	//TODO(Abhishek) :Please check whether the code below is required or need to be modified. Use view status instead
    if(handle['viewStatus'] != undefined) {
        if(handle['viewStatus']['status'] == 'success') {
            $('#key').val(handle.viewStatus.data.idea.key);
            $('#errDisplay').html(handle['viewStatus']['messages']['globalMessage']);
            $('#errDisplay').html('Your idea has been saved');
            $('#errDisplay').show();
            $('html, body').animate({scrollTop: '0px'}, 500);
            ie.progressEnd();
            return;
        }
        else if(handle['viewStatus']['status'] == 'globalError') {
            $('#errDisplay').html(handle['viewStatus']['messages']['globalMessage']);
            ie.progressEnd();
            return;
        }
        else if(handle['viewStatus']['status'] == 'error') {
            var errorHTML = '';
            if(handle['viewStatus']['messages']['title'] != undefined) {
                errorHTML += handle['viewStatus']['messages']['title'] + '<br/>';
            }
            if(handle['viewStatus']['messages']['description'] != undefined) {
                errorHTML += handle['viewStatus']['messages']['description'] + '<br/>';
            }
            if(handle['viewStatus']['messages']['targetAudience'] != undefined) {
                errorHTML += handle['viewStatus']['messages']['targetAudience'] + '<br/>';
            }
            if(handle['viewStatus']['messages']['monetization'] != undefined) {
                errorHTML += handle['viewStatus']['messages']['monetization'] + '<br/>';
            }
            if(handle['viewStatus']['messages']['competition'] != undefined) {
                errorHTML += handle['viewStatus']['messages']['competition'] + '<br/>';
            }
            if(handle['viewStatus']['messages']['tags'] != undefined) {
                errorHTML += handle['viewStatus']['messages']['tags'] + '<br/>';
            }
            if(handle['viewStatus']['messages']['category'] != undefined) {
                errorHTML += handle['viewStatus']['messages']['category'] + '<br/>';
            }
            if(handle['viewStatus']['messages']['ideaRightsGivenUp'] != undefined) {
                errorHTML += handle['viewStatus']['messages']['ideaRightsGivenUp'] + '<br/>';
            }
            if(handle['viewStatus']['messages']['ipGivenUp'] != undefined) {
                errorHTML += handle['viewStatus']['messages']['ipGivenUp'] + '<br/>';
            }
            $('#errDisplay').html(errorHTML);
            ie.progressEnd();
            return;
        }
    }
    else {
        $('#errDisplay').html(ie.SERVER_ERROR);
        ie.progressEnd();
        return;
    }
}

//handle save idea on the basis of user login as login is must for save idea 
function handleSaveIdeaForm (isUserLoggedIn) {
    if(isUserLoggedIn) {
        $('#btnSaveIdea').unbind('click');
        $('#btnSaveIdea').bind('click', function () {
            if($.trim($('#title').val()) == '' || $.trim($('#title').val()) == 'Idea Title') {
                $('#errDisplay').html('Please add Idea title');
                $('#errDisplay').show();
                return false;
            }
            $('#submitIdeaForm').submit();
        });
    }
    else {
        $('#btnSaveIdea').unbind('click');
        $('#btnSaveIdea').click(function () {
            ie.Login.showLoginPopup();
        });
    }
}

//handle save idea on the basis of user login as login is must for save idea 
function handlePublishIdeaForm (isUserLoggedIn) {
	
    if(isUserLoggedIn) {
        $('#btnPublishIdea').unbind('click');
        $('#btnPublishIdea').bind('click', function () {
            if($.trim($('#title').val()) == '' || $.trim($('#title').val()) == '') {
                $('#errDisplay').html('Please add Idea title');
                $('#errDisplay').show();
                return false;
            }
            if($.trim($('#description').val()) == '' || $.trim($('#description').val()) == '') {
                $('#errDisplay').html('Please add Idea description');
                $('#errDisplay').show();
                return false;
            }
            if(!($('#ipGivenUp').is(':checked') && $('#ideaRightsGivenUp').is(':checked'))) {
                $('#errDisplay').html('Please select project visibility');
                $('#errDisplay').show();
                return false;
            }
            ie.showRecaptcha('publishIdea');
        });
    }
    else {
        $('#btnPublishIdea').unbind('click');
        $('#btnPublishIdea').click(function () {
            ie.Login.showLoginPopup();
        });
    }
}

function publishIdea (recaptchaChallengeField, recaptchaResponseField) {
    $('#recaptchaChallengeField').val(recaptchaChallengeField);
    $('#recaptchaResponseField').val(recaptchaResponseField);
    $('#ideaForm').submit();
}
