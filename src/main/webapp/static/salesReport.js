function getCurrentUrl(){
    	var baseUrl = $("meta[name=baseUrl]").attr("content")
    	return baseUrl + "/api/report/sales";

}


function downloadSales(event){
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

    var startDate = $("#start-date").val();
    var endDate = $("#end-date").val();





    var details = {};

    details["category"] = category;
    details["brand"] = brand;
    details["startDate"] = startDate;
    details["endDate"] = endDate;


    var json = JSON.stringify(details);



    $.ajax({
        url:getCurrentUrl(),
        type:"POST",
        data:json,
        	   headers: {
               	'Content-Type': 'application/json'
               },
        	   success: function(response) {
        	            alert("Sales Report successfully downloaded");
        	   },
        	   error: handleAjaxError

    });
    return false;
}

function fillCategoryOptionUtil(data){
     var categoryOption = $(".append-category");
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
            console.log(data);
            fillCategoryOptionUtil(data);
        }
    })
}

function fillBrandOptionUtil(data){
    var brandOption = $(".append-brand");

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
            console.log(data);
            fillBrandOptionUtil(data);
        }
    })
}


function init(){
    $("#download-sales").click(downloadSales);
    fillCategoryOption();
    fillBrandOption();


}





$(document).ready(init);