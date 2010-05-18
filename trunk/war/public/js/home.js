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
 * file is responsible to handle all javascript tasks on home page
 * require jquery 1.3.2 
 */
// onload set form focus validations
google.setOnLoadCallback(function() {
    ie.Login.setLoggedinUserCallback('home.handleSaveIdeaForm');
	// categories required for the form
    ie.loadCategories('category', ''/*selected cat id*/);
    // on focus idea title
    $('#title').focus(function () {
  	    if($('#title').val() == 'Idea Title') {
  	        $('#title').val('');
  	    }
    });
    $('#title').blur(function () {
  	    if($('#title').val() == '') {
  	        $('#title').val('Idea Title');
  	    }
    });
    $('#description').focus(function () {
  	    if($('#description').val() == 'Description') {
  	        $('#description').val('');
  	    }
    });
    $('#description').blur(function () {
  	    if($('#description').val() == '') {
  	        $('#description').val('Description');
  	    }
    });
    $('#tags').focus(function () {
  	    if($('#tags').val() == 'Tags') {
  	        $('#tags').val('');
  	    }
    });
    $('#tags').blur(function () {
  	    if($('#tags').val() == '') {
  	        $('#tags').val('Tags');
  	    }
    });
    // set description char limit to 3000 chars
    $('#description').limit('3000','#charsLeft');
    
    // Put autocomplete functionality on Tags
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
            return row.title;
        },
        multiple: true,
        matchContains: false,
        highlight: false,
        formatted: false
    });
});


/**
 * handle save idea on the basis of user login as login is must for save idea 
 */
home.handleSaveIdeaForm = function (isUserLoggedIn) {
    if(isUserLoggedIn) {
        $('#btnSaveIdea').unbind('click');
        $('#btnSaveIdea').bind('click', function () {
            if($.trim($('#title').val()) == '' || $.trim($('#title').val()) == 'Idea Title') {
                $('#errDisplay').html('Please add Idea title');
                $('#errDisplay').show();
                return;
            }
            if($('#description').val() == 'Description') {
                $('#description').val('');
            }
            if($('#tags').val() == 'Tags') {
                $('#tags').val('');
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

/**
 * validate save idea form
 * @return boolean
 */
home.validateForm = function () {
    // only title field is required to save an idea
    if($.trim($('#title').val()) == '' || $.trim($('#title').val()) == 'Idea Title') {
        $('#errDisplay').html('Please add Idea title');
        $('#errDisplay').show();
        return false;
    }
    return true;
}
