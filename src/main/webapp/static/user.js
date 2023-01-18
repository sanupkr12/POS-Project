
function getUserUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/admin/user";
}

//BUTTON ACTIONS
function addUser(event){
	//Set the values to update
	var $form = $("#user-create-form");
	var json = toJson($form);
	var url = getUserUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        $('#user-create-modal').modal('toggle');
	   		getUserList();    
	   },
	   error: handleAjaxError
	});

	return false;
}

function getUserList(){
	var url = getUserUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {

	   		displayUserList(data);

	   },
	   error: handleAjaxError
	});
}

function deleteUser(id){
	var url = getUserUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getUserList();    
	   },
	   error: handleAjaxError
	});
}

//UI DISPLAY METHODS

function displayUserList(data){
	var $tbody = $('#user-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];

		var buttonHtml = '<button style="padding:0.5rem;border-radius:0.3rem;" class="fa fa-edit fa-lg" onclick="displayEditUser(' + e.id + ')"></button>&nbsp;&nbsp;&nbsp; '
		buttonHtml+='<button style="padding:0.5rem;border-radius:0.3rem;" class="fa fa-trash" onclick="deleteUser(' + e.id + ')"></button>';

        var row = '<tr>'
		+ '<td>' + e.id + '</td>'
		+ '<td>' + e.email + '</td>'
		+ '<td>' + e.role + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function createUser(){
    $('#user-create-form input[name=email]').val('');
    $('#user-create-form input[name=password]').val('');
    $('#user-create-form input[name=role]').val('');
    $('#create-user-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){
$("#admin-link").addClass('active');
	$('#add-user').click(addUser);
	$('#refresh-data').click(getUserList);
	$('#create-user').click(createUser);
}

$(document).ready(init);
$(document).ready(getUserList);

