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
