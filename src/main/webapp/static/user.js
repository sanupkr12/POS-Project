
function getUserUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/supervisor/user";
}


//BUTTON ACTIONS
function addUser(event){
	//Set the values to update
	var $form = $("#user-create-form");
	var json = toJson($form);
	console.log(json);
	var url = getUserUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        $('#create-user-modal').modal('toggle');
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
    var confirmation = confirm("Are You Sure You Want to delete?");

    if(confirmation)
    {
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
    else{
        return;
    }

}


//UI DISPLAY METHODS

function displayUserList(data){
	var $tbody = $('#user-table').find('tbody');
	$tbody.empty();
	var sno = 1;
	for(var i in data){
		var e = data[i];

		var buttonHtml = '<button style="border:0;background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" title="Delete" onclick="deleteUser(' + e.id + ')"><i class="fa fa-trash fa-lg"></i></button>';

        var row = '<tr>'
		+ '<td>' + sno + '</td>'
		+ '<td>' + e.email + '</td>'
		+ '<td>' + e.role + '</td>'
		+ '<td class="text-center">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
        sno+=1;
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
	$('#add-user').click(addUser);
	$('#refresh-data').click(getUserList);
	$('#create-user').click(createUser);


}

$(document).ready(init);
$(document).ready(getUserList);

