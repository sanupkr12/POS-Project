// FILE UPLOAD METHODS
let fileData = [];
let errorData = [];
let processCount = 0;
let updatedId;

$(document).ready(init);
//INITIALIZATION CODE
function init() {
	$("#brand-link").addClass('active');
	$('#brand-edit-form').submit(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
	$('#BrandFile').on('change', updateFileName);
	$('#add-brand').click(createBrand);
	$('#brand-form').submit(addBrand);
	$('#brandFile').on('change', function () {
		let fileName = $(this).val();
		fileName = fileName.split("\\").pop();
		$('#brandFileName').html(fileName);
	});
	getBrandList();
}

function getBrandUrl() {
	let baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}
function getBrandList() {
	let url = getBrandUrl();
	makeAjaxCall(url,'GET',{},(data)=>{
        displayBrandList(data);
    },
    handleAjaxError);
}

//UI DISPLAY METHODS
function displayBrandList(data) {
	let $tbody = $('#Brand-table').find('tbody');
	$tbody.empty();
	let j = 1;
	for (let i in data) {
		let e = data[i];
		let buttonHtml = ' <button style="background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;"  title="Edit"  onclick="displayEditBrand(' + e.id + ')"><i class="fa fa-edit fa-lg"></i></button>&nbsp;';
		let row = '<tr>'
			+ '<td>' + j + '</td>'
			+ '<td>' + e.name + '</td>'
			+ '<td>' + e.category + '</td>'
			+ '<td class="text-center supervisor-only">' + buttonHtml + '</td>'
			+ '</tr>';
		$tbody.append(row);
		j += 1;
	}
	if ($("meta[name=role]").attr("content") === 'operator') {
		$(".supervisor-only").hide();
	}
}

//BUTTON ACTIONS
function addBrand(event) {
	event.preventDefault();
	//Set the values to update
	let $form = $("#brand-form");
	let json = toJson($form);
	let url = getBrandUrl();
	makeAjaxCall(url,'POST',json,(response)=> {
        $('#create-brand-modal').modal('toggle');
        handleSuccessNotification("Brand has been added successfully");
        getBrandList();
    },(response)=> {
        handleAjaxError(response);
    });
	return false;
}

function updateBrand(event) {
	event.preventDefault();
	$('#edit-Brand-modal').modal('toggle');
	//Get the ID
	let url = getBrandUrl() + "/" + updatedId;
	//Set the values to update
	let $form = $("#brand-edit-form");
	let json = toJson($form);
	makeAjaxCall(url,'PUT',json,(response)=> {
        $('#edit-brand-modal').modal('toggle');
        handleSuccessNotification("Brand Edited Successfully");
        getBrandList();
    },handleAjaxError);
	return false;
}

function deleteBrand(id) {
	let url = getBrandUrl() + "/" + id;
	makeAjaxCall(url,'DELETE',{},(data)=> {
        getBrandList();
    },handleAjaxError);
}

function processData() {
	let file = $('#brandFile')[0].files[0];
	if (!file) {
		handleErrorNotification("No file detected");
		return;
	}
	if (file.type != 'text/tab-separated-values') {
		handleErrorNotification("Wrong file type");
		return;
	}
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results) {
	fileData = results.data;
	if (fileData.length > 5000) {
		handleErrorNotification("Number of entry greater than 5000");
		return;
	}
	let fields = results.meta.fields;
	if (fields.length != 2) {
		let row = {};
		row.error = "Incorrect fields provided";
		errorData.push(row);
		$("#download-errors").show();
		return;
	}
	else {
		if (fields[0] != 'name' || fields[1] != 'category') {
			let row = {};
			row.error = "Incorrect headers provided";
			errorData.push(row);
			$("#download-errors").show();
			return;
		}
	}
	if (fileData.length === 0) {
		handleErrorNotification("Empty tsv");
		return;
	}
	uploadRows();
}

function uploadRows() {
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if (processCount == fileData.length) {
		getBrandList();
		if (errorData.length === 0) {
			$.notify("Brand File uploaded successfully", "success");
			$('#upload-brand-modal').modal('toggle');
			return;
		}
		$("#download-errors").show();
		return;
	}
	//Process next row
	let row = fileData[processCount];
	processCount++;
	let json = JSON.stringify(row);
	let url = getBrandUrl();
	//Make ajax call
	makeAjaxCall(url,'POST',json,
	(response)=> {
	uploadRows();
	},
	(response)=> {
        let data = JSON.parse(response.responseText);
        row.error = data["message"];
        errorData.push(row);
        uploadRows();
    });
}

function downloadErrors() {
	writeFileData(errorData);
}

function displayEditBrand(id) {
	let url = getBrandUrl() + "/" + id;
	updatedId = id;
	makeAjaxCall(url,'GET',{},(data) =>{
        displayBrand(data);
    },
    handleAjaxError
	);
}

function resetUploadDialog() {
	//Reset file name
	let $file = $('#BrandFile');
	$file.val('');
	$('#BrandFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts	
	updateUploadDialog();
}

function updateUploadDialog() {
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName() {
	let $file = $('#BrandFile');
	let fileName = $file.val();
	fileName = fileName.split("\\").pop();
	$('#BrandFileName').html(fileName);
}

function displayUploadData() {
	resetUploadDialog();
	$('#download-errors').hide();
	$('#upload-brand-modal').modal('toggle');
}

function displayBrand(data) {
	$("#brand-edit-form input[name=name]").val(data.name);
	$("#brand-edit-form input[name=category]").val(data.category);
	$('#edit-brand-modal').modal('toggle');
}

function createBrand() {
	$("#brand-form input[name=name]").val('');
	$("#brand-form input[name=category]").val('');
	$('#create-brand-modal').modal('toggle');
}



