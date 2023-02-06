// FILE UPLOAD METHODS
let fileData = [];
let errorData = [];
let processCount = 0;

$(document).ready(init);
//INITIALIZATION CODE
function init() {
	$("#product-link").addClass('active');
	$('#product-create-form').submit(addProduct);
	$('#product-edit-form').submit(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
	$('#productFile').on('change', updateFileName);
	$('#create-product').click(createProduct);
	$('#download-errors').hide();
	$("#inputBrandName").on('change', displayCategory);
	$("#editInputBrandName").on('change', displayEditCategory);
	getProductList();
}
function getProductUrl() {
	let baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}
let update_id = 0;
//BUTTON ACTIONS
function addProduct(event) {
	event.preventDefault();
	let $form = $("#product-create-form");
	//Set the values to update
	let name = $("#product-create-form input[name='name']").val();
	let barcode = $("#product-create-form input[name='barcode']").val();
	let mrp = $("#product-create-form input[name='mrp']").val();
	let brandName = $("#inputBrandName").val();
	let brandCategory = $("#inputCategory").val();
	if (brandName === 'Select Brand' || brandCategory === 'Select Category') {
		handleErrorNotification("Invalid Brand or Category");
		return;
	}
	let product = {};
	product["name"] = name;
	product["barcode"] = barcode;
	product["mrp"] = mrp;
	product["brandName"] = brandName;
	product["brandCategory"] = brandCategory;
	let json = JSON.stringify(product);
	let url = getProductUrl();
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function (response) {
			$('#create-product-modal').modal('toggle');
			handleSuccessNotification("Product has been added successfully");
			getProductList();
		},
		error: function (response) {
			handleAjaxError(response);
		}
	});
	return false;
}
function updateProduct(event) {
	//Get the ID
	let url = getProductUrl() + "/" + update_id;
	//Set the values to update
	let $form = $("#product-edit-form");
	//Set the values to update
	let name = $("#product-edit-form input[name='name']").val();
	let barcode = $("#product-edit-form input[name='barcode']").val();
	let mrp = $("#product-edit-form input[name='mrp']").val();
	let brandName = $("#editInputBrandName").val();
	let brandCategory = $("#editInputCategory").val();
	if (brandName === 'Select Brand' || brandCategory === 'Select Category') {
		handleErrorNotification("Invalid Brand or Category");
		return;
	}
	let product = {};
	product["name"] = name;
	product["barcode"] = barcode;
	product["mrp"] = mrp;
	product["brandName"] = brandName;
	product["brandCategory"] = brandCategory;
	let json = JSON.stringify(product);
	$.ajax({
		url: url,
		type: 'PUT',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function (response) {
			handleSuccessNotification("Product Updated Successfully");
			$('#edit-product-modal').modal('toggle');
			getProductList();
		},
		error: function (response) {
			handleAjaxError(response);
		}
	});
	return false;
}
function getProductList() {
	let url = getProductUrl();
	$.ajax({
		url: url,
		type: 'GET',
		success: function (data) {
			displayProductList(data);
		},
		error: handleAjaxError
	});
}
function deleteProduct(id) {
	let url = getProductUrl() + "/" + id;
	$.ajax({
		url: url,
		type: 'DELETE',
		success: function (data) {
			getProductList();
		},
		error: handleAjaxError
	});
}
function processData() {
	let file = $('#productFile')[0].files[0];
	if (file.type != 'text/tab-separated-values') {
		handleErrorNotification("Wrong file type");
		return;
	}
	if (!file) {
		handleErrorNotification("No file detected");
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
	if (fields.length != 5) {
		let row = {};
		row.error = "Incorrect number of headers provided";
		errorData.push(row);
		$("#download-errors").show();
		return;
	}
	else {
		if (fields[0] != 'name' || fields[1] != 'brandName' || fields[2] != 'brandCategory' || fields[3] != 'barcode' || fields[4] != 'mrp') {
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
		getProductList();
		if (errorData.length == 0) {
			handleSuccessNotification("Brand File uploaded successfully");
			$('#upload-product-modal').modal('toggle');
			return;
		}
		$("#download-errors").show();
		return;
	}
	//Process next row
	let row = fileData[processCount];
	processCount++;
	let json = JSON.stringify(row);
	let url = getProductUrl();
	//Make ajax call
	$.ajax({
		url: url,
		type: 'POST',
		data: json,
		headers: {
			'Content-Type': 'application/json'
		},
		success: function (response) {
			uploadRows();
		},
		error: function (response) {
			row.error = response.responseText
			errorData.push(row);
			uploadRows();
		}
	});
}
function downloadErrors() {
	writeFileData(errorData);
}
//UI DISPLAY METHODS
function displayProductList(data) {
	let sno = 1;
	let $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for (let i in data) {
		let e = data[i];
		let buttonHtml = '<button style="background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" title="Edit" onclick="displayEditProduct(' + e.id + ')"><i class="fa fa-edit fa-lg"></i></button>&nbsp;';
		let row = '<tr>'
			+ '<td>' + sno + '</td>'
			+ '<td>' + e.name + '</td>'
			+ '<td>' + e.barcode + '</td>'
			+ '<td>' + e.brandName + '</td>'
			+ '<td>' + e.brandCategory + '</td>'
			+ '<td style="text-align:end">' + numberWithCommas(e.mrp.toFixed(2)) + '</td>'
			+ '<td class="text-center supervisor-only">' + buttonHtml + '</td>'
			+ '</tr>';
		$tbody.append(row);
		sno += 1;
	}
	if ($("meta[name=role]").attr("content") === 'operator') {
		$(".supervisor-only").hide();
	}
}
function displayEditProduct(id) {
	let url = getProductUrl() + "/" + id;
	update_id = id;
	$.ajax({
		url: url,
		type: 'GET',
		success: function (data) {
			displayProduct(data);
		},
		error: handleAjaxError
	});
}
function resetUploadDialog() {
	//Reset file name
	let $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
	//Reset letious counts
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
	let $file = $('#productFile');
	let fileName = $file.val();
	fileName = fileName.split("\\").pop();
	$('#productFileName').html(fileName);
}
function fillBrandOption() {
	$.ajax({
		url: $("meta[name=baseUrl]").attr("content") + "/api/brand/list",
		type: 'GET',
		headers: {
			'Content-type': 'application/json'
		},
		success: function (data) {
			fillBrandOptionUtil(data);
		}
	})
}
function fillBrandOptionUtil(data) {
	let brandOption = $(".append-brand");
	brandOption[0].innerHTML = "";
	brandOption.append(`<option selected>Select Brand</option>`);
	for (let i = 0; i < data.length; i++) {
		brandOption.append(`<option value="${data[i]}">${data[i]}</option>`);
	}
}
function fillEditBrandOption(val) {
	$.ajax({
		url: $("meta[name=baseUrl]").attr("content") + "/api/brand/list",
		type: 'GET',
		headers: {
			'Content-type': 'application/json'
		},
		success: function (data) {
			fillEditBrandOptionUtil(data, val);
		}
	})
}
function fillEditBrandOptionUtil(data, val) {
	let brandOption = $("#editInputBrandName");
	brandOption[0].innerHTML = "";
	data.sort();
	for (let i = 0; i < data.length; i++) {
		if (data[i] === val.brandName) {
			brandOption.append(`<option selected>${val.brandName}</option>`);
		}
		else {
			brandOption.append(`<option value="${data[i]}">${data[i]}</option>`);
		}
	}
	$.ajax({
		url: $("meta[name=baseUrl]").attr("content") + "/api/brand/list/" + val.brandName,
		type: 'GET',
		headers: {
			'Content-type': 'application/json'
		},
		success: function (data) {
			let categoryOption = $("#editInputCategory");
			data.sort();
			categoryOption[0].innerHTML = "";
			for (let i = 0; i < data.length; i++) {
				if (data[i] === val.brandCategory) {
					categoryOption.append(`<option selected>${val.brandCategory}</option>`);
				}
				else {
					categoryOption.append(`<option val="${data[i]}">${data[i]}</option>`);
				}
			}
		}
	})
}
function displayCategory(event) {
	let brand = event.target.value;
	if (brand === 'Select Brand') {
		let categoryOption = $(".append-category");
		categoryOption[0].innerHTML = "";
		categoryOption.append(`<option selected>Select Category</option>`);
		return;
	}
	$.ajax({
		url: $("meta[name=baseUrl]").attr("content") + "/api/brand/list/" + brand,
		type: 'GET',
		headers: {
			'Content-type': 'application/json'
		},
		success: function (data) {
			let categoryOption = $(".append-category");
			categoryOption[0].innerHTML = "";
			categoryOption.append(`<option selected>Select Category</option>`);
			for (let i = 0; i < data.length; i++) {
				categoryOption.append(`<option val="${data[i]}">${data[i]}</option>`);
			}
		}
	})
}
function displayEditCategory(event) {
	let brand = event.target.value;
	if (brand === 'Select Brand') {
		let categoryOption = $("#editInputCategory");
		categoryOption[0].innerHTML = "";
		categoryOption.append(`<option selected>Select Category</option>`);
		return;
	}
	$.ajax({
		url: $("meta[name=baseUrl]").attr("content") + "/api/brand/list/" + brand,
		type: 'GET',
		headers: {
			'Content-type': 'application/json'
		},
		success: function (data) {
			let categoryOption = $("#editInputCategory");
			data.sort();
			categoryOption[0].innerHTML = "";
			categoryOption.append(`<option selected>Select Category</option>`);
			for (let i = 0; i < data.length; i++) {
				categoryOption.append(`<option val="${data[i]}">${data[i]}</option>`);
			}
		}
	})
}
function displayUploadData() {
	resetUploadDialog();
	$('#download-errors').hide();
	$('#upload-product-modal').modal('toggle');
}
function displayProduct(data) {
	$("#product-edit-form input[name=name]").val(data.name);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	fillEditBrandOption(data);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$('#edit-product-modal').modal('toggle');
}
function createProduct() {
	$("#product-create-form input[name=name]").val("");
	$("#product-create-form input[name=barcode]").val("");
	fillBrandOption();
	$("#product-create-form input[name=mrp]").val("");
	$('#create-product-modal').modal('toggle');
}
