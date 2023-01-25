
function getUserUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/supervisor/user";
}

var updatedId;

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

function displayEditUser(id){

    updatedId = id;

    var url = getUserUrl() + "/" + updatedId;

    $.ajax({
    	   url: url,
    	   type: 'GET',
    	   success: function(data) {
    	   		 $("#edit-user-form input[name=email]").val(data.email);
                 $("edit-user-form input[name=role]").val(data.role);

                     $("#edit-user-modal").modal('toggle');
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

		var buttonHtml = '<button style="border:0;padding:0.5rem;background-color:transparent;border:0;border-radius:0.3rem;" title="Edit" onclick="displayEditUser(' + e.id + ')"><i class="fa fa-edit fa-lg"></i></button>&nbsp;&nbsp;&nbsp; '
		buttonHtml+='<button style="border:0;background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" title="Delete" onclick="deleteUser(' + e.id + ')"><i class="fa fa-trash fa-lg"></i></button>';

        var row = '<tr>'
		+ '<td>' + e.id + '</td>'
		+ '<td>' + e.email + '</td>'
		+ '<td>' + e.role + '</td>'
		+ '<td class="text-center">' + buttonHtml + '</td>'
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

function handleEditUser(){
    var url = getUserUrl() + "/" + updatedId;

    var $form = $("#edit-user-form");
    var json = toJson($form);
    console.log(json);

    $.ajax({
        	   url: url,
        	   type: 'POST',
        	   data:json,
        	   headers: {
                      	'Content-Type': 'application/json'
                      },
        	   success: function() {
                         $("#edit-user-modal").modal('toggle');
                         getUserList();
        	   },
        	   error: handleAjaxError
        	});

}

//INITIALIZATION CODE
function init(){
$("#admin-link").addClass('active');
	$('#add-user').click(addUser);
	$('#refresh-data').click(getUserList);
	$('#create-user').click(createUser);
	$('#edit-user').click(handleEditUser);


}

$(document).ready(init);
$(document).ready(getUserList);

