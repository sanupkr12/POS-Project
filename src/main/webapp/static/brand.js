
function getBrandUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/brand";
}

var updatedId;
//BUTTON ACTIONS
function addBrand(event){
    event.preventDefault();
	//Set the values to update
	var $form = $("#brand-form");
	var json = toJson($form);


	var url = getBrandUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        $('#create-brand-modal').modal('toggle');
	        handleSuccessNotification("Brand has been added successfully");
	   		getBrandList();  
	   },
	   error: function(response){

	        handleAjaxError(response);
	   }
	});

	return false;
}

function updateBrand(event){
    event.preventDefault();
	$('#edit-Brand-modal').modal('toggle');
	//Get the ID
	var url = getBrandUrl() + "/" + updatedId;

	//Set the values to update
	var $form = $("#brand-edit-form");
	var json = toJson($form);
	console.log(json);

	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {
	        $('#edit-brand-modal').modal('toggle');

	        handleSuccessNotification("Brand Edited Successfully");
	   		getBrandList();   
	   },
	   error: handleAjaxError
	});

	return false;
}


function getBrandList(){
	var url = getBrandUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrandList(data);  
	   },
	   error: handleAjaxError
	});
}

function deleteBrand(id){
	var url = getBrandUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getBrandList();  
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){

	var file = $('#brandFile')[0].files[0];

	if(!file)
    {
        handleErrorNotification("No file detected");
        return;
    }

	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){

	fileData = results.data;

	if(fileData.length===0)
	{
	    handleErrorNotification("Empty tsv");
        return;
	}

	if(fileData.length>5000)
	{
	    handleErrorNotification("Number of entry greater than 5000");
	    return;
	}

	var fields = results.meta.fields;

	if(fields.length!=2)
	{
	    var row = {};
	    row.error = "Incorrect fields provided";
	    errorData.push(row);
	    $("#download-errors").show();
	    return;
	}
	else{
	    if(fields[0]!='name' || fields[1]!='category')
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
	    getBrandList();

	    if(errorData.length===0)
	    {
	        $.notify("Brand File uploaded successfully","success");
	        $('#upload-brand-modal').modal('toggle');
	        return;
	    }


	    $("#download-errors").show();
		return;
	}
	
	//Process next row
	var row = fileData[processCount];
	processCount++;


//	if(row.__parsed_extra != null) {
//        row.error="Extra Fields exist.";
//        errorData.push(row);
//        uploadRows();
//        return;
//    }
	
	var json = JSON.stringify(row);

	var url = getBrandUrl();
	console.log(json);


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
            var data = JSON.parse(response.responseText);
	   		row.error=data["message"];
	   		errorData.push(row);
	   		uploadRows();
	   }
	});

}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS

function displayBrandList(data){
	var $tbody = $('#Brand-table').find('tbody');
	$tbody.empty();
	var j=1;
	for(var i in data){
		var e = data[i];
		var buttonHtml = ' <button style="background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;"  title="Edit"  onclick="displayEditBrand(' + e.id + ')"><i class="fa fa-edit fa-lg"></i></button>&nbsp;';

		var row = '<tr>'
		+ '<td>' + j + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '<td class="text-center supervisor-only">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
        j+=1;
	}

	if($("meta[name=role]").attr("content") === 'operator')
	{
	    $(".supervisor-only").hide();
	}

}

function displayEditBrand(id){
	var url = getBrandUrl() + "/" + id;
	updatedId = id;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayBrand(data);   
	   },
	   error: handleAjaxError
	});	
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#BrandFile');
	$file.val('');
	$('#BrandFileName').html("Choose File");
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
	var $file = $('#BrandFile');
	var fileName = $file.val();
	fileName = fileName.split("\\").pop();
	$('#BrandFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog();
 	$('#download-errors').hide();
	$('#upload-brand-modal').modal('toggle');
}

function displayBrand(data){
	$("#brand-edit-form input[name=name]").val(data.name);
	$("#brand-edit-form input[name=age]").val(data.age);
	$("#brand-edit-form input[name=id]").val(data.id);
	$('#edit-brand-modal').modal('toggle');
}

function createBrand(){
    $("#brand-form input[name=name]").val('');
    $("#brand-form input[name=category]").val('');
    $('#create-brand-modal').modal('toggle');
}


//INITIALIZATION CODE
function init(){
    $("#brand-link").addClass('active');
	$('#brand-edit-form').submit(updateBrand);
	$('#refresh-data').click(getBrandList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#BrandFile').on('change', updateFileName);
    $('#add-brand').click(createBrand);
    $('#brand-form').submit(addBrand);
    $('#brandFile').on('change',function(){
        var fileName = $(this).val();
        fileName = fileName.split("\\").pop();
        $('#brandFileName').html(fileName);
    });



}

$(document).ready(init);
$(document).ready(getBrandList);

