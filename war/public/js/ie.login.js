/**
 * Idea exchange friend connect library
 */
ie.Login = {
    loginCallback : {},
    logoutCallback : {},
    USER_ROLE : 'anonymous',
    IS_AUTHENTICATED_USER : 'is_authenticated_user',
    url : '/'
};

ie.Login.showLoginPopup = function () {
	// get html to search duplicate idea
	$('#dialog').html('<form id="loginForm" method="post" action="/login"><div class="hiddenInViewSource jquery-dialog" style="padding-left: 50px; margin-top: 10px;">' 
			+	'<b>OpenID URL: </b><input name="openid" id="openid" value=""/><br/>'
			+	'</div></form>');
	$('#dialog').dialog('option', 'title', 'Login to system!');
    $('#dialog').dialog('option', 'buttons', { "Login": function() {
    	if('' == $.trim($('#openid').val())) {
    		alert('Please provide openid or provider name.')
    		return false;
    	}
    	$('#loginForm').submit();
    }});
    $('#dialog').dialog('option', 'modal', true);
    $('#dialog').dialog('option', 'height', 100);
    $('#dialog').dialog('option', 'width', 200);
    $('#dialog').dialog('open');
}
/**
 * prepare HTML for Sign in
 */
ie.Login.getSigninHtml = function() {
    return '<a href="javascript:void(0);" onclick="ie.Login.showLoginPopup();" class="ie-link-gray ie-right-mar-10">Sign In</a>';
};

/**
 * prepare HTML for Signed in user
 */
ie.Login.getSignedUserHtml = function() {
    ie.config.IS_LOGGEDIN_USER = true;
    var html = '<span class="ie-link-gray-b ie-left-mar-10 ie-right-mar-10">Welcome ' + $
            .cookie('viewer_display_name') + ' </span>';
    return html += '|<a href="#" class="ie-link-gray ie-left-mar-10" onclick="ie.Login.processLogout()">Sign Out</a>';
};

/**
 * start idea exchange with login
 */
ie.Login.socialLogin = function() {
    if(this.is_loggedin) {
        return;
    }
    if ($('#signin') == undefined) {
        // No need of login block
        return;
    }
    // check wheather user is already logged in to the system
    if ($.cookie(this.IS_AUTHENTICATED_USER) == null && $.cookie('viewer_display_name') != null) {
        this.need2wayServerLogin();
        this.needLoggedinUser(false);
    }
    else {
        if ($.cookie('viewer_display_name') == null) {
            // if session not exist make request to create one
            $('#signin').html(this.getSigninHtml());
            $.cookie('viewer_display_name', null, { expires: -1, path: '/'});
            this.needLoggedinUser(false);
        } else {
            // Create html to display the user's name, and a sign-out link.
            $('#signin').html(this.getSignedUserHtml());
            this.needLoggedinUser(true);
        }
    }
};

/**
 * Email is required so get html to provide input field for the same
 */
ie.Login.getEmailRequireHtml = function () {
   opHtml = '';
   opHtml += '<p>Please provide your valid email id, This mail id will be used for further communication with you.</p>';
   opHtml += '<div class="error" id="mailError" style="display:none">Please provide your valid email id</div>';
   opHtml += 'Email: <input type="text" name="userEmail" id="userEmail" />';
   return opHtml; 
}
/**
 * On successful login via GFC we need to update info on our server
 */
ie.Login.need2wayServerLogin = function() {
    var url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE,
            'users/authenticate.json');
    $.get( url, 
           {}, 
           function(handle) {
            if (handle.viewStatus.status == ie.config.SUCCESS) {
                    // user able to login
                $('#signin').html(ie.Login.getSignedUserHtml());
                ie.config.IS_LOGGEDIN_USER = true;
                ie.Login.USER_ROLE = handle.viewStatus.data.user.roleName;
                ie.Login.needLoggedinUser(true);
                $.cookie(ie.Login.IS_AUTHENTICATED_USER, true, { path: '/'});              
            }
            else if (handle.viewStatus.status == ie.config.ERROR && undefined != handle.viewStatus.messages.emailRequired) {
                // email id required 
                $('#dialog').html(ie.Login.getEmailRequireHtml());
                $('#dialog').dialog('option', 'title', 'Email required!');
                $('#dialog').dialog('option', 'buttons', { "Ok": function() {
                                                                    if(!ie.isValidEmailAddress($('#userEmail').val())) {
                                                                        $('#mailError').show();
                                                                        return;
                                                                    }
                                                                    // email validation check
                                                                    eval('ie.Login.addEmail("' + $('#userEmail').val() + '")');
                                                                    $(this).dialog("close"); 
                                                                 } 
                                                         }
                );
                $('#dialog').dialog('option', 'modal', true);
                $('#dialog').dialog('option', 'width', 450);
                $('#dialog').dialog('open');
                
            }
            else {
                // server error
                ie.Login.needLoggedinUser(false);
                ie.Login.processLogoutIntermediate();
                $('#signin').html(ie.Login.getSigninHtml());
                ie.showError(handle.viewStatus.messages.globalMessage);
            }
           }, ie.config.RESPONSE_TYPE);
};

ie.Login.addEmail = function (email) {
    // if mail is valid and record is saved then return else delete cookie 
    // $.cookie('viewer_display_name', null, { expires: -1, path: '/'});
    var url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE,
    'users/register-mail/' + email + '.json');
    $.get( url, 
       {}, 
       function(handle) {
        if (handle.viewStatus.status == ie.config.SUCCESS) {
                // user able to login
            $('#signin').html(ie.Login.getSignedUserHtml());
            ie.config.IS_LOGGEDIN_USER = true;
            ie.Login.needLoggedinUser(true);
            $.cookie(ie.Login.IS_AUTHENTICATED_USER, true, { path: '/'});
        }
        else {
            // server error
            $.cookie('viewer_display_name', null, { expires: -1, path: '/'});
            ie.Login.needLoggedinUser(false);
            $('#signin').html(ie.Login.getSigninHtml());
            ie.globalError();
        }
       }, ie.config.RESPONSE_TYPE);
}

/**
 * Setter for call back after login
 */
ie.Login.setLoggedinUserCallback = function(callback) {
    if (callback != undefined) {
        this.loginCallback[callback] = callback;
    }
};
/**
 * Setter for call back after login
 */
ie.Login.setLogoutUserCallback = function(callback) {
    if (callback != undefined) {
        this.logoutCallback[callback] = callback;
    }
};

/**
 * process call back set by setLoggedinUserCallback after login
 */
ie.Login.needLoggedinUser = function(isUserLoggedin) {
    if (this.loginCallback != undefined) {
        for ( var callback in this.loginCallback) {
            eval(this.loginCallback[callback] + '(' + isUserLoggedin + ', \'' + this.USER_ROLE + '\')');
        }
    }
};

/**
 * process user logout
 */
ie.Login.processLogout = function () {
	this.processLogoutIntermediate(true);
}

/**
 * process user logout
 */
ie.Login.processLogoutIntermediate = function (isRedirect) {
    // send request on server to handle session reset
    var url = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE,
    'users/logoff.json');
    $.get(url,
          function(handle) {
            ie.config.IS_LOGGEDIN_USER = false;
            ie.Login.USER_ROLE = ie.config.ANONYMOUS_ROLE;
            if (ie.Login.logoutCallback != undefined) {
                for ( var callback in ie.Login.logoutCallback) {
                    eval(ie.Login.logoutCallback[callback] + '()');
                }
            }
            if(isRedirect)
            	ie.sendToHome();
          });
}
