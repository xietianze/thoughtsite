// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * file is responsible to handle all javascript tasks on home page
 * require jquery 1.3.2 
 */
// onload set form focus validations
google.setOnLoadCallback(function() {
    ie.Login.setLoggedinUserCallback('activate');
});

function activate (isUserLoggedIn) {
    if(isUserLoggedIn) {
       location.href = ie.buildUrl(ie.config.REQUEST_IDEA_EXCHANGE, 'projects/show/' + PROJECT_KEY);
    }
    else {
        $('#activateLogin').show();
    }
}