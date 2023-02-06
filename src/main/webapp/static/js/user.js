$(document).ready(init);
//INITIALIZATION CODE
function init() {
	$('#add-user').click(addUser);
	$('#refresh-data').click(getUserList);
	$('#create-user').click(createUser);
	getUserList();
}
function getUserUrl() {
	let baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/supervisor/user";
}
//BUTTON ACTIONS
function addUser(event) {
	//Set the values to update
	let $form = $("#user-create-form");
	let json = toJson($form);
	console.log(json);
	let url = getUserUrl();
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function (response) {
			$('#create-user-modal').modal('toggle');
			getUserList();
		},
		error: handleAjaxError
	});
	return false;
}
function getUserList() {
	let url = getUserUrl();
	$.ajax({
		url: url,
		type: 'GET',
		success: function (data) {
			displayUserList(data);
		},
		error: handleAjaxError
	});
}
function deleteUser(id) {
	let confirmation = confirm("Are You Sure You Want to delete?");
	if (confirmation) {
		let url = getUserUrl() + "/" + id;
		$.ajax({
			url: url,
			type: 'DELETE',
			success: function (data) {
				getUserList();
			},
			error: handleAjaxError
		});
	}
	else {
		return;
	}
}
//UI DISPLAY METHODS
function displayUserList(data) {
	let $tbody = $('#user-table').find('tbody');
	$tbody.empty();
	for (let i in data) {
		let e = data[i];
		let buttonHtml = '<button style="border:0;background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" title="Delete" onclick="deleteUser(' + e.id + ')"><i class="fa fa-trash fa-lg"></i></button>';
		let row = '<tr>'
			+ '<td>' + parseInt(i+1) + '</td>'
			+ '<td>' + e.email + '</td>'
			+ '<td>' + e.role + '</td>'
			+ '<td class="text-center">' + buttonHtml + '</td>'
			+ '</tr>';
		$tbody.append(row);
	}
}
function createUser() {
	$('#user-create-form input[name=email]').val('');
	$('#user-create-form input[name=password]').val('');
	$('#user-create-form input[name=role]').val('');
	$('#create-user-modal').modal('toggle');
}
