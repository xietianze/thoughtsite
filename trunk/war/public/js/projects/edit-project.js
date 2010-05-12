// Copyright 2009 Google Inc. All Rights Reserved.
/**
 * file is responsible to handle all javascript tasks on showIdeaForm require
 * jquery 1.3.2
 */
// onload set form focus validations
google.setOnLoadCallback(function() {
	// User require login before create project
		ie.Login.setLoggedinUserCallback('handleCreateProjectForm');
		// set description char limit to 3000 chars
	    $('#description').limit('3000','#charsLeftDescription');
	    // set project name char limit to 500 chars
	    $('#name').limit('150','#charsLeftName');
	    // set monetization char limit to 500 chars
	});



// handle save idea on the basis of user login as login is must for save idea
function handleCreateProjectForm(isUserLoggedIn) {
	if (isUserLoggedIn) {
		$('#btnCreateProj').unbind('click');
		$('#btnCreateProj')
				.bind(
						'click',
						function() {
							emailArray = document.getElementsByName('email');
							devNameArray = document.getElementsByName('devName');
							if ($.trim($('#name').val()) == '') {
								$('#errDisplay')
										.html('Please add project name');
								$('#errDisplay').show();
								return false;
							}
							//To do: Surabhi in Jquery
							var validDevName=true;
							for (i = 0; i < devNameArray.length; ++i) {
									if ((devNameArray[i].value == '' || devNameArray[i].value == 'name')
											&& ((emailArray[i].value == '') || (emailArray[i].value == 'emailId'))) {
			                            ie.showRecaptcha('postProject');
									} else if (!(devNameArray[i].value != '' && emailArray[i].value != '')) {
										$('#errDisplay')
												.html(
														'Please add both developers name and email id');
										$('#errDisplay').show();
										return false;
									} else if (emailArray[i].value != 'emailId' && !(isValidEmailAddress(emailArray[i].value))){
										$('#errDisplay')
										.html('Please enter valid email.');
										$('#errDisplay').show();
											return false
										}
							}
							ie.showRecaptcha('postProject');
						});
	} else {
		$('#btnCreateProj').unbind('click');
		$('#btnCreateProj').click(function() {
			ie.Login.showLoginPopup();
		});
	}
}

//Email Id validation
function isValidEmailAddress(emailAddress) {
	var pattern = new RegExp(/^(("[\w-\s]+")|([\w-]+(?:\.[\w-]+)*)|("[\w-\s]+")([\w-]+(?:\.[\w-]+)*))(@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$)|(@\[?((25[0-5]\.|2[0-4][0-9]\.|1[0-9]{2}\.|[0-9]{1,2}\.))((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){2}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\]?$)/i);
	return pattern.test(emailAddress);
	}
//Submit project form after captcha validation
function postProject(recaptchaChallengeField, recaptchaResponseField) {
    $('#recaptchaChallengeField').val(recaptchaChallengeField);
    $('#recaptchaResponseField').val(recaptchaResponseField);
    $('#projectForm').submit();
}
//Add new row to add more developers on create project screen
function addMore() {
	var developerList = document.getElementById('developerList');
	var numi = document.getElementById('theValue');
	var num = (document.getElementById('theValue').value - 1) + 2;
	numi.value = num;
	var newdiv = document.createElement('div');
	var divIdName = 'my' + num + 'Div';
	if(num >= 20)
	{
		$('#errDisplay')
		.html(
				'More than 20 developers cannot be added in 1 time.');
	}
	else
	{
		newdiv.setAttribute('id', divIdName);
		newdiv.innerHTML = '<input name="devName" onClick="blankDevName()" onSelect="blankDevName()" type="text" id="devName" value="name" size="30" maxlength="50" /> <input name="email" onClick="blankEmailId()" onSelect="blankEmailId()" type="text" id="email" value="emailId" size="30" maxlength="50" /> <input type="hidden" name="status" value="" /><input type="hidden" name="devKey" value="" />'
		developerList.appendChild(newdiv);
	}
}
//Blank text box to enter Developer name
function blankDevName() {
	var x=document.getElementsByName("devName");
	for (var i = 0; i < x.length; i++) {
		if(x[i].value == 'name')
			x[i].value = '';
	    }
}
//Blank text box to enter Developer email id
function blankEmailId() {
	var x=document.getElementsByName("email");
	for (var i = 0; i < x.length; i++) {
		if(x[i].value == 'emailId')
			x[i].value = '';
		}
}

