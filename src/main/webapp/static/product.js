
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

var update_id = 0;

//BUTTON ACTIONS
function addProduct(event){
	//Set the values to update
	var $form = $("#product-create-form");
	var json = toJson($form);


	var url = getProductUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        $('#create-product-modal').modal('toggle');
	        $.notify("Product has been added successfully","success");
	   		getProductList();  
	   },
	   error: function(response){

              	        handleAjaxError(response);
           }
	});

	return false;
}

function updateProduct(event){
	$('#edit-product-modal').modal('toggle');
	//Get the ID
	var url = getProductUrl() + "/" + update_id;

	//Set the values to update
	var $form = $("#product-edit-form");
	var json = toJson($form);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        $.notify("Product has been updated successfully","success");
	   		getProductList();   
	   },
	   error: handleAjaxError
	});

	return false;
}


function getProductList(){
	var url = getProductUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   console.log(data);
	   		displayProductList(data);  
	   },
	   error: handleAjaxError
	});
}

function deleteProduct(id){
    console.log(id);
	var url = getProductUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getProductList();  
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#productFile')[0].files[0];
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;
	
	var json = JSON.stringify(row);
	var url = getProductUrl();

	//Make ajax call
	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	   		uploadRows();  
	   },
	   error: function(response){
	   		row.error=response.responseText
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS

function displayProductList(data){
    var sno = 1;
	var $tbody = $('#product-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = '<button style="padding:0.5rem;border-radius:0.3rem;" title="Edit" onclick="displayEditProduct(' + e.id + ')"><i class="fa fa-edit fa-lg"></i></button>&nbsp;';

		var row = '<tr>'
		+ '<td>' + sno + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>' + e.brandCategory + '</td>'
		+ '<td>'  + e.mrp + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
        sno+=1;
	}
}

function displayEditProduct(id){

	var url = getProductUrl() + "/" + id;
	update_id = id;

	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayProduct(data);   
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#productFile');
	$file.val('');
	$('#productFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
	//Update counts	
	updateUploadDialog();
}

function updateUploadDialog(){
	$('#rowCount').html("" + fileData.length);
	$('#processCount').html("" + processCount);
	$('#errorCount').html("" + errorData.length);
}

function updateFileName(){
	var $file = $('#productFile');
	var fileName = $file.val();
	$('#productFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-product-modal').modal('toggle');
}

function displayProduct(data){

	$("#product-edit-form input[name=name]").val(data.name);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	$("#product-edit-form input[name=brandName]").val(data.brandName);
	$("#product-edit-form input[name=brandCategory]").val(data.brandCategory);
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$('#edit-product-modal').modal('toggle');
}

function createProduct(){
    $("#product-create-form input[name=name]").val("");
    	$("#product-create-form input[name=barcode]").val("");
    	$("#product-create-form input[name=brandName]").val("");
    	$("#product-create-form input[name=brandCategory]").val("");
    	$("#product-create-form input[name=mrp]").val("");
    	$('#create-product-modal').modal('toggle');
}

//INITIALIZATION CODE
function init(){
    $("#product-link").addClass('active');
	$('#add-product').click(addProduct);
	$('#update-product').click(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName);
    $('#create-product').click(createProduct);


}

$(document).ready(init);
$(document).ready(getProductList);