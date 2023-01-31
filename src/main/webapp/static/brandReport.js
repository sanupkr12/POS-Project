function init(){
    $("#display-brand-table").hide();
    $("#download-brand").click(downloadBrand);
    fillCategoryOption();
    $("#category").on('change',getBrandByCategory);

}

$(document).ready(init);

function getCurrentUrl(){
    	var baseUrl = $("meta[name=baseUrl]").attr("content")
    	return baseUrl + "/api/report/brand";
}

function downloadBrand(event){
    event.preventDefault();
    var category = $("#category").val();
    var brand = $("#brand").val();

    if(category=="Select Category")
    {
        category = "";
    }

    if(brand=="Select Brand")
    {
        brand = "";
    }


    var details = {};

    details["category"] = category;
    details["brand"] = brand;
    var json = JSON.stringify(details);

    $.ajax({
        url:getCurrentUrl(),
        type:"POST",
        data:json,
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	            displayBrandList(response);

        	   },
        	   error: handleAjaxError

    });
    return false;
}

function displayBrandList(data){

    if(data.length===0)
    {
        $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify("No Results to display","info");
        $("#display-brand-table").hide();
        return;
    }

    $("#display-brand-table").show();
	var $tbody = $('#display-brand-table').find('tbody');
	$tbody.empty();

	var j=1;
	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td>' + j + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>'  + e.category + '</td>'
		+ '</tr>';
        $tbody.append(row);
        j+=1;
	}
}

function fillCategoryOptionUtil(data){
     var categoryOption = $(".append-category");
     categoryOption[0].innerHTML = "";
     data.sort();
     categoryOption.append(`<option selected>Select Category</option>`);

     for(var i=0;i<data.length;i++)
     {
        categoryOption.append(`<option val="${data[i]}">${data[i]}</option>`);
     }

     categoryOption.append(`<option val="all">all</option>`);

}


function fillCategoryOption(){

    $.ajax({
        url:$("meta[name=baseUrl]").attr("content") + "/api/brand/category",
        type:'GET',
        headers:{
            'Content-type':'application/json'
        },
        success:function(data){
            fillCategoryOptionUtil(data);
        }
    })
}

function fillBrandOptionUtil(data){
    var brandOption = $(".append-brand");
    data.sort();
    brandOption[0].innerHTML = "";
    brandOption.append(`<option selected>Select Brand</option>`);
    for (var i=0;i<data.length;i++)
    {
        brandOption.append(`<option value="${data[i]}">${data[i]}</option>`);
    }
    brandOption.append(`<option val="all">all</option>`);
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

function getBrandByCategory(event){
    var category = event.target.value;
    $.ajax({
         url:$("meta[name=baseUrl]").attr("content") + "/api/brand/category/" + category,
         type:'GET',
         headers:{
            'Content-type':'application/json'
                },
        success:function(data){
            var brandOption = $(".append-brand");
            brandOption[0].innerHTML = "";
            data.sort();
            brandOption.append(`<option selected>Select Brand</option>`);
                 for(var i=0;i<data.length;i++)
                 {
                    brandOption.append(`<option val="${data[i]}">${data[i]}</option>`);
                 }
        }
    })
}

