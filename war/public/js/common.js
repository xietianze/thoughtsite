google.setOnLoadCallback(function() {
    ie.Login.setLoggedinUserCallback('handleUserProfileTab');
    ie.Login.setLoggedinUserCallback('handleAdminTab');
    setTimeout('ie.Login.socialLogin()', 1000);
    $('#keyword').focus(function () {
        if($('#keyword').val() == 'Enter Search Keyword') {
            $('#keyword').val('');
        }
    });
    $('#keyword').blur(function () {
        if($('#keyword').val() == '') {
            $('#keyword').val('Enter Search Keyword');
        }
    });
    $('#dialog').dialog({
        autoOpen: false,
        modal: true
    });
    
    $("#tab-menu").tabs();
});

function validateSearch() {
	if('Enter Search Keyword' == $.trim($('#keyword').val())) {
        $('#keyword').val('');
    }
    return true;
    
}
function handleUserProfileTab(isLoggedIn) {
    if(isLoggedIn) {
        $('#userProfile').show();
    }
    else {
        $('#userProfile').hide();
    }
}

function handleAdminTab(isLoggedIn, role) {
    if(role != ie.config.ADMIN_ROLE)
        return;
    if(isLoggedIn) {
        $('#admin').show();
    }
    else {
        $('#admin').hide();
    }
}

// theme for re captcha
var RecaptchaOptions = {
   theme : 'clean'
};
