// FILE UPLOAD METHODS
let fileData = [];
let errorData = [];
let processCount = 0;

$(document).ready(init);
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
          let fileName = $(this).val();
          fileName = fileName.split("\\").pop();
          $("#inventoryFileName").html(fileName);
      });
	 getInventoryList();
}
function getInventoryUrl(){
	let baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/inventory";
}
//BUTTON ACTIONS
function addInventory(event){
    event.preventDefault();
	//Set the values to update
	let $form = $("#inventory-create-form");
	let json = toJson($form);
	let url = getInventoryUrl();
	makeAjaxCall(url,'POST',json,(response)=>{
        $("#create-inventory-modal").modal('toggle');
        handleSuccessNotification("Inventory has been updated successfully");
        getInventoryList();
    },(response)=>{
        handleAjaxError(response);
    })
	return false;
}
function updateInventory(event){
    event.preventDefault();
	$('#edit-inventory-modal').modal('toggle');
	//Get the ID
	let barcode = $("#inventory-edit-form input[name=barcode]").val();
	let url = getInventoryUrl() + "/";
	//Set the values to update
	let $form = $("#inventory-edit-form");
	let json = toJson($form);
	makeAjaxCall(url,'PUT',json,(response)=>{
        handleSuccessNotification("Inventory has been updated successfully");
        getInventoryList();
     },handleAjaxError);
	return false;
}
function getInventoryList(){
	let url = getInventoryUrl();
	makeAjaxCall(url,'GET',{},(data)=> {
        displayInventoryList(data);
    },handleAjaxError)
}
function deleteInventory(id){
	let url = getInventoryUrl() + "/" + id;
	makeAjaxCall(url,'DELETE',{},(data)=> {
        getInventoryList();
    },handleAjaxError);
}

function processData(){
	let file = $('#inventoryFile')[0].files[0];
	if(file.type!='text/tab-separated-values')
    {
        handleErrorNotification("Wrong file type");
        return;
    }
	if(!file)
    {
        handleErrorNotification("No file detected");
        return;
    }
	readFileData(file, readFileDataCallback);
}
function readFileDataCallback(results){
	fileData = results.data;
	if(fileData.length>5000)
    {
        handleErrorNotification("Number of entry greater than 5000");
        return;
    }
	let fields = results.meta.fields;
	if(fields.length!=2)
	{
	    let row = {};
	    row.error = "Incorrect number of headers provided";
	    errorData.push(row);
	    $("#download-errors").show();
	    return;
	}
	else{
	    if(fields[0]!='barcode' || fields[1]!='quantity')
	    {
	        let row = {};
            row.error = "Incorrect headers provided";
            errorData.push(row);
            $("#download-errors").show();
            return;
	    }
	}
	if(fileData.length===0)
    {
        handleErrorNotification("Empty tsv");
        return;
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
            handleSuccessNotification("Inventory File uploaded successfully");
            $('#upload-inventory-modal').modal('toggle');
            return;
        }
        $("#download-errors").show();
        return;
	}
	//Process next row
	let row = fileData[processCount];
	processCount++;
	let json = JSON.stringify(row);
	let url = getInventoryUrl();
	//Make ajax call
	makeAjaxCall(url,'POST',json,(response) =>{
        uploadRows();
    },(response)=>{
        row.error=response.responseText
        errorData.push(row);
        uploadRows();
     });
}
function downloadErrors(){
	writeFileData(errorData);
}
//UI DISPLAY METHODS
function displayInventoryList(data){
    let sno = 1;
	let $tbody = $('#inventory-table').find('tbody');
	$tbody.empty();
	for(let i in data){
		let e = data[i];
		let buttonHtml = ' <button style="background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" title="Edit" onclick="displayEditInventory('+  "'" + e.barcode + "'" + ')"><i class="fa fa-edit fa-lg"></i></button>&nbsp;';
		let row = '<tr>'
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
	let url = getInventoryUrl() + "/" + barcode;
	makeAjaxCall(url,'GET',{},(data)=> {
        displayInventory(data);
    },handleAjaxError);
}

function resetUploadDialog(){
	//Reset file name
	let $file = $('#inventoryFile');
	$file.val('');
	$('#inventoryFileName').html("Choose File");
	//Reset letious counts
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
	let $file = $('#inventoryFile');
	let fileName = $file.val();
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
