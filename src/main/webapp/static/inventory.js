
function getInventoryUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
}

//BUTTON ACTIONS
function addInventory(event){
    event.preventDefault();
	//Set the values to update
	var $form = $("#inventory-create-form");
	var json = toJson($form);


	var url = getInventoryUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        $("#create-inventory-modal").modal('toggle');
	        $.notify("Inventory has been updated successfully","success");
	   		getInventoryList();
	   },
	   error: function(response){

              	        handleAjaxError(response);
              	   }
	});

	return false;
}

function updateInventory(event){
    event.preventDefault();
	$('#edit-inventory-modal').modal('toggle');
	//Get the ID
	var barcode = $("#inventory-edit-form input[name=barcode]").val();

	var url = getInventoryUrl() + "/";

	//Set the values to update
	var $form = $("#inventory-edit-form");
	var json = toJson($form);


	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	        $.notify("Inventory has been updated successfully","success");
	   		getInventoryList();
	   },
	   error: handleAjaxError
	});

	return false;
}


function getInventoryList(){
	var url = getInventoryUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	        console.log(data);
	   		displayInventoryList(data);
	   },
	   error: handleAjaxError
	});
}

function deleteInventory(id){
	var url = getInventoryUrl() + "/" + id;

	$.ajax({
	   url: url,
	   type: 'DELETE',
	   success: function(data) {
	   		getInventoryList();
	   },
	   error: handleAjaxError
	});
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;


function processData(){
	var file = $('#inventoryFile')[0].files[0];

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

	if(fields.length!=2)
	{
	    var row = {};
	    row.error = "Incorrect fields provided";
	    errorData.push(row);
	    $("#download-errors").show();
	    return;
	}
	else{
	    if(fields[0]!='barcode' || fields[1]!='quantity')
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
	 getInventoryList();
		if(errorData.length==0)
        {
            $.notify("Inventory File uploaded successfully","success");
            	        $('#upload-inventory-modal').modal('toggle');

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
	var url = getInventoryUrl();

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

function displayInventoryList(data){
    var sno = 1;
	var $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = ' <button style="background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" title="Edit" onclick="displayEditInventory('+  "'" + e.barcode + "'" + ')"><i class="fa fa-edit fa-lg"></i></button>&nbsp;';


		var row = '<tr>'
		+ '<td>' + sno + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>'  + numberWithCommas(e.quantity) + '</td>'
		+ '<td class="text-center supervisor-only">' + buttonHtml + '</td>'
		+ '</tr>';
        $tbody.append(row);
        sno+=1;
	}

	if($("meta[name=role]").attr("content") === 'operator')
    	{
    	   $(".supervisor-only").hide();
    	}
}

function displayEditInventory(barcode){
	var url = getInventoryUrl() + "/" + barcode;
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayInventory(data);
	   },
	   error: handleAjaxError
	});
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
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
	var $file = $('#inventoryFile');
	var fileName = $file.val();
	$('#inventoryFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog();
 	$('#download-errors').hide();
	$('#upload-inventory-modal').modal('toggle');
}

function displayInventory(data){
	$("#inventory-edit-form input[name=barcode]").val(data.barcode);
	$("#inventory-edit-form input[name=quantity]").val(data.quantity);
	$('#edit-inventory-modal').modal('toggle');
}

function createInventory(){
    $("#inventory-create-form input[name=barcode]").val("");
    $("#inventory-create-form input[name=quantity]").val(0);
    $("#create-inventory-modal").modal('toggle');
}




//INITIALIZATION CODE
function init(){
$("#inventory-link").addClass('active');
	$('#inventory-create-form').submit(addInventory);
	$('#inventory-edit-form').submit(updateInventory);
	$('#refresh-data').click(getInventoryList);
	$('#upload-data').click(displayUploadData);
	$('#process-data').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#employeeFile').on('change', updateFileName);
    $("#create-inventory").click(createInventory);
    $('#download-errors').hide();
    $('#inventoryFile').on('change',function(){
          var fileName = $(this).val();
          $("#inventoryFileName").html(fileName);
      })
}

$(document).ready(init);
$(document).ready(getInventoryList);