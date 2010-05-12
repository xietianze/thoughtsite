// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * Library for ideaexchange
 * 
 * @author Abhishek
 */
var ie = {};


/**
 * Idea exchange lib config
 */
ie.config = {
    reCaptchPublicKey : '6LdxTQsAAAAAAJmfpbUQDeqoggNWP2HA-rkZ_xhR',
    REQUEST_FRIEND_CONNECT : 'friendconnect',
    REQUEST_IDEA_EXCHANGE : 'root',
    baseUrl : '/',
    PUBLIC_URL : '/public/',
    RESPONSE_TYPE : 'json',
    SUCCESS : 'success',
    ERROR : 'error',
    STATUS_ACTIVE : 'active',
    STATUS_BANNED : 'banned',
    STATUS_SAVED : 'Saved',
    STATUS_PUBLISHED : 'Published',
    STATUS_OBJECTIONABLE : 'Objectionable',
    STATUS_DUPLICATE : 'Duplicate',
    IDEAS : 'ideas',
    IDEAS_COMMENT : 'comments',
    PROJECTS_COMMENT : 'projectComments',
    STATUS_DUPLICATE : 'Duplicate',
    ENTITY_IDEA : 'Idea',
    ENTITY_IDEA_COMMENT : 'IdeaComment',
    ENTITY_PROJECT_COMMENT : 'ProjectComment',
    RECORD_PER_PAGE : 10,
    RECORD_PER_PAGE_ADMIN : 50,
    IS_LOGGEDIN_USER : false,
    ADMIN_ROLE : 'admin',
    ANONYMOUS_ROLE : 'anonymous'
};

/**
 * Global messages
 */
ie.config.message = {
    SERVER_ERROR : 'Server is heavily loaded, Please try again later.',
    NO_RECORDS_FOUND : 'We did not find any records for your request.'
}

/**
 * redirect to home location
 */
ie.sendToHome = function () {
    location.href = ie.config.baseUrl;
}

/**
 * show captcha
 */
ie.showRecaptcha = function (callback) {
    $('#dialog').html('<div id="reCaptcha"></div>');
    $('#dialog').dialog('option', 'title', 'Are you human?');
    $('#dialog').dialog('option', 'buttons', { "Ok": function() { 
                                                        eval(callback + '("' + $('#recaptcha_challenge_field').val() + '", "' + $('#recaptcha_response_field').val() + '")');
                                                        $(this).dialog("close"); 
                                                     } 
                                             }
    );
    $('#dialog').dialog('option', 'modal', true);
    $('#dialog').dialog('option', 'width', 450);
    $('#dialog').dialog('open');
    
    Recaptcha.create(ie.config.reCaptchPublicKey, 'reCaptcha', {
        theme: "clean", 
        callback: Recaptcha.focus_response_field});
}

/**
 * show progress bar
 */
ie.progressStart = function() {
    var element = $("<div/>").show().addClass('progress').css("position",
            "absolute").attr("id", "ideaexchange_progress").appendTo(
            document.body);
    element.html('Loading...');
};

/**
 * Remove loading box
 */
ie.progressEnd = function() {
    $('#ideaexchange_progress').remove();
};

/**
 * Show global server error
 */
ie.showError = function(message) {
    $('#dialog').html(message);
    $('#dialog').dialog('option', 'title', 'Alert!');
    $('#dialog').dialog('option', 'buttons', { "Ok": function() { $(this).dialog("close"); } });
    $('#dialog').dialog('option', 'modal', true);
    $('#dialog').dialog('open');
};
/**
 * Show global server error
 */
ie.globalError = function() {
    var errorHTML = '<div class="global-error">' + this.config.message.SERVER_ERROR + '</div>';
    $('#dialog').html(errorHTML);
    $('#dialog').dialog('option', 'title', 'Alert!');
    $('#dialog').dialog('option', 'buttons', { "Ok": function() { $(this).dialog("close"); } });
    $('#dialog').dialog('option', 'modal', true);
    $('#dialog').dialog('open');
};

/**
 * build URL for AJAX requests TODO: modify it to make more generic
 */
ie.buildUrl = function(module, url) {
    if (module == this.config.REQUEST_FRIEND_CONNECT) {
        return this.Login.url + url
    } else if (module == this.config.REQUEST_IDEA_EXCHANGE) {
        return this.config.baseUrl + url;
    }
};

/**
 * load categories in select box
 */
ie.loadCategories = function(itemId, selectedItem) {
    $.get(
        this.buildUrl(this.config.REQUEST_FRIEND_CONNECT,
                'ideas/categories.json'),
        {},
        function(handle) {
            if (handle.viewStatus.status == ie.config.SUCCESS) {
                var template = '<option value=\"@key@\">@name@</option>';
                var html = ie.Template.renderByToken(
                                handle.viewStatus.data.categories,
                                template);
                $("select#" + itemId).html('<option value="-1" selected>Select category</option>' + html);
                $("select#" + itemId).val(selectedItem);
            } else {
                ie.globalError();
            }
        },
        this.config.RESPONSE_TYPE
    );
};



/**
 * report abuse to idea or comment
 */
ie.reportAbouse = function(type, key) {
    if(!this.config.IS_LOGGEDIN_USER) {
        ie.showError('Please login to report abuse.');
    }
    $.post(
            this.buildUrl(this.config.REQUEST_IDEA_EXCHANGE, type + '/flag/abuse/' + key + '.json'),
            {},
            function (handle) {
                if(handle.viewStatus.status == ie.config.SUCCESS) {
                    ie.showError(handle.viewStatus.messages.flag);
                }
                else {
                    ie.showError(handle.viewStatus.messages.flag);
                }
            }, 
            this.config.RESPONSE_TYPE
    );
}

/**
 * escape html (html to special chars)
 */
ie.escapeHTML = function(str) {
    var div = document.createElement('div');
    var text = document.createTextNode(str);
    div.appendChild(text);
    return div.innerHTML;
}


/**
 * validate email address
 */
ie.isValidEmailAddress = function(emailAddress) {
    var pattern = new RegExp(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
    return pattern.test(emailAddress);
}