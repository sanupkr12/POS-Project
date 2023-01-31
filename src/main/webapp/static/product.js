
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

    var $form = $("#product-create-form");
	//Set the values to update
	var name = $("#product-create-form input[name='name']").val();
	var barcode = $("#product-create-form input[name='barcode']").val();
	var mrp = $("#product-create-form input[name='mrp']").val();
	var brandName = $("#inputBrandName").val();
	var brandCategory = $("#inputCategory").val();

	if(brandName==='Select Brand' || brandCategory==='Select Category')
	{
	    handleErrorNotification("Invalid Brand or Category");
	    return;
	}

	var product = {};

	product["name"] = name;
	product["barcode"] = barcode;
	product["mrp"] = mrp;
	product["brandName"] = brandName;
	product["brandCategory"] = brandCategory;

	var json = JSON.stringify(product);

    console.log(json);

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
	        handleSuccessNotification("Product has been added successfully");
	   		getProductList();  
	   },
	   error: function(response){

              	        handleAjaxError(response);
           }
	});

	return false;
}

function updateProduct(event){
	//Get the ID
	var url = getProductUrl() + "/" + update_id;

        //Set the values to update
        var $form = $("#product-edit-form");

    	//Set the values to update
    	var name = $("#product-edit-form input[name='name']").val();
    	var barcode = $("#product-edit-form input[name='barcode']").val();
    	var mrp = $("#product-edit-form input[name='mrp']").val();
    	var brandName = $("#editInputBrandName").val();
    	var brandCategory = $("#editInputCategory").val();

    	if(brandName==='Select Brand' || brandCategory==='Select Category')
    	{
    	    handleErrorNotification("Invalid Brand or Category");
    	    return;
    	}

    	var product = {};

    	product["name"] = name;
    	product["barcode"] = barcode;
    	product["mrp"] = mrp;
    	product["brandName"] = brandName;
    	product["brandCategory"] = brandCategory;

    	var json = JSON.stringify(product);


	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },	   
	   success: function(response) {

	        handleSuccessNotification("Product Updated Successfully");
	        $('#edit-product-modal').modal('toggle');
	   		getProductList();   
	   },
	   error: function(response){

	        handleAjaxError(response);
	   }
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
            handleSuccessNotification("Brand File uploaded successfully");
            $('#upload-product-modal').modal('toggle');

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
	fileName = fileName.split("\\").pop();
	$('#productFileName').html(fileName);
}

function fillBrandOption(){


    $.ajax({
        url:$("meta[name=baseUrl]").attr("content") + "/api/brand/list",
        type:'GET',
        headers:{
            'Content-type':'application/json'

        },
        success:function(data){

            fillBrandOptionUtil(data);
        }
    })
}

function fillBrandOptionUtil(data){
    var brandOption = $(".append-brand");

    brandOption[0].innerHTML = "";

    brandOption.append(`<option selected>Select Brand</option>`);

    for (var i=0;i<data.length;i++)
    {
        brandOption.append(`<option value="${data[i]}">${data[i]}</option>`);
    }


}

function fillEditBrandOption(){


    $.ajax({
        url:$("meta[name=baseUrl]").attr("content") + "/api/brand/list",
        type:'GET',
        headers:{
            'Content-type':'application/json'

        },
        success:function(data){

            fillEditBrandOptionUtil(data);
        }
    })
}

function fillEditBrandOptionUtil(data){
    var brandOption = $("#editInputBrandName");

    brandOption[0].innerHTML = "";
    data.sort();
    brandOption.append(`<option selected>Select Brand</option>`);

    for (var i=0;i<data.length;i++)
    {
        brandOption.append(`<option value="${data[i]}">${data[i]}</option>`);
    }


}

function displayCategory(event){

    var brand = event.target.value;

    if(brand==='Select Brand')
    {
        var categoryOption = $(".append-category");
        categoryOption[0].innerHTML = "";
        categoryOption.append(`<option selected>Select Category</option>`);
        return;
    }


    $.ajax({
             url:$("meta[name=baseUrl]").attr("content") + "/api/brand/list/" + brand,
                    type:'GET',
                    headers:{
                        'Content-type':'application/json'

                    },
                    success:function(data){
                        var categoryOption = $(".append-category");

                        categoryOption[0].innerHTML = "";

                        categoryOption.append(`<option selected>Select Category</option>`);
                         for(var i=0;i<data.length;i++)
                         {
                            categoryOption.append(`<option val="${data[i]}">${data[i]}</option>`);
                         }
                    }

        })

}

function displayEditCategory(event){

    var brand = event.target.value;

    if(brand==='Select Brand')
    {
        var categoryOption = $("#editInputCategory");
        categoryOption[0].innerHTML = "";
        categoryOption.append(`<option selected>Select Category</option>`);
        return;
    }


    $.ajax({
             url:$("meta[name=baseUrl]").attr("content") + "/api/brand/list/" + brand,
                    type:'GET',
                    headers:{
                        'Content-type':'application/json'

                    },
                    success:function(data){
                        var categoryOption = $("#editInputCategory");
                        data.sort();
                        categoryOption[0].innerHTML = "";

                        categoryOption.append(`<option selected>Select Category</option>`);
                         for(var i=0;i<data.length;i++)
                         {
                            categoryOption.append(`<option val="${data[i]}">${data[i]}</option>`);
                         }
                    }

        })

}


function displayUploadData(){
 	resetUploadDialog();
 	$('#download-errors').hide();
	$('#upload-product-modal').modal('toggle');
}

function displayProduct(data){

	$("#product-edit-form input[name=name]").val(data.name);
	$("#product-edit-form input[name=barcode]").val(data.barcode);
	fillEditBrandOption();
	$("#product-edit-form input[name=mrp]").val(data.mrp);
	$('#edit-product-modal').modal('toggle');
}

function createProduct(){
        $("#product-create-form input[name=name]").val("");
    	$("#product-create-form input[name=barcode]").val("");
    	fillBrandOption();
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
    $("#inputBrandName").on('change',displayCategory);
    $("#editInputBrandName").on('change',displayEditCategory);
}

$(document).ready(init);
$(document).ready(getProductList);