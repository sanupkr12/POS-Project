
function getProductUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/product";
}

var update_id = 0;

function numberWithCommas(x) {
    return x.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
}

//BUTTON ACTIONS
function addProduct(event){
    event.preventDefault();
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
    event.preventDefault();
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

	if(!file)
    {
        handleErrorNotification("No file detected");
        return;
    }

	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	var fields = results.meta.fields;

    if(fields.length!=5)
    {
        var row = {};
        row.error = "Incorrect fields provided";
        errorData.push(row);
        $("#download-errors").show();
        return;
    }
    else{
        if(fields[0]!='name' || fields[1]!='brandName' || fields[2]!='brandCategory' || fields[3]!='barcode' || fields[4]!='mrp')
        {
            var row = {};
            row.error = "Incorrect headers provided";
            errorData.push(row);
            $("#download-errors").show();
            return;
        }
    }

	uploadRows();
}

function uploadRows(){
	//Update progress
	updateUploadDialog();
	//If everything processed then return
	if(processCount==fileData.length){
	    getProductList();
		if(errorData.length==0)
        {
            $.notify("Brand File uploaded successfully","success");
            $('#upload-product-modal').modal('toggle');

            return;
        }
        $("#download-errors").show();
        return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;

	if(row.__parsed_extra != null) {
        row.error="Extra Fields exist.";
        errorData.push(row);
        uploadRows();
        return;
    }
	
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
		var buttonHtml = '<button style="background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" title="Edit" onclick="displayEditProduct(' + e.id + ')"><i class="fa fa-edit fa-lg"></i></button>&nbsp;';

		var row = '<tr>'
		+ '<td>' + sno + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.brandName + '</td>'
		+ '<td>' + e.brandCategory + '</td>'
		+ '<td style="text-align:end">'  + numberWithCommas(e.mrp.toFixed(2)) + '</td>'
		+ '<td class="text-center supervisor-only">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
        sno+=1;
	}
    console.log($("meta[name=role]").attr("content"));
	if($("meta[name=role]").attr("content") === 'operator')
        	{
        	    $(".supervisor-only").hide();
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
 	$('#download-errors').hide();
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
	$('#product-create-form').submit(addProduct);
	$('#product-edit-form').submit(updateProduct);
	$('#refresh-data').click(getProductList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#productFile').on('change', updateFileName);
    $('#create-product').click(createProduct);
    $('#download-errors').hide();

}

$(document).ready(init);
$(document).ready(getProductList);