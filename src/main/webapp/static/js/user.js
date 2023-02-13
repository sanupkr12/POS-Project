const createUserModal = $('#create-user-modal');
const $createUserForm = $('#user-create-form');
const userTable = $("#user-table");

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
	let json = toJson($createUserForm);
	let url = getUserUrl();
	makeAjaxCall(url,'POST',json,(response)=>{
        createUserModal.modal('toggle');
        getUserList();
    },handleAjaxError);
	return false;
}
function getUserList() {
	let url = getUserUrl();
	makeAjaxCall(url,'GET',{},(data)=>{
        displayUserList(data);
    },handleAjaxError);
}
function deleteUser(id) {
	let confirmation = confirm("Are You Sure You Want to delete?");
	if (confirmation) {
		let url = getUserUrl() + "/" + id;
		makeAjaxCall(url,'DELETE',{},(data)=>{
            getUserList();
        },handleAjaxError);
	}
	else {
		return;
	}
}
//UI DISPLAY METHODS
function displayUserList(data) {
	let $tbody = userTable.find('tbody');
	$tbody.empty();
	for (let i in data) {
		let e = data[i];
		let buttonHtml = `<button style="border:0;background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" title="Delete" onclick="deleteUser(${e.id})"><i class="fa fa-trash fa-lg"></i></button>`;
		let row = '<tr>'
			+ '<td>' + parseInt(+i+1) + '</td>'
			+ '<td>' + e.email + '</td>'
			+ '<td>' + e.role + '</td>'
			+ '<td class="text-center">' + buttonHtml + '</td>'
			+ '</tr>';
		$tbody.append(row);
	}
}
function createUser() {
	$createUserForm.find('input[name=email]').val('');
	$createUserForm.find('input[name=password]').val('');
	createUserModal.modal('toggle');
}
