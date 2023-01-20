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
        	            displaySalesList(response);
        	   },
        	   error: handleAjaxError

    });
    return false;
}


function displaySalesList(data){
    var sno = 1;
	var $tbody = $('#display-sales-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
		var buttonHtml = ' <button class="btn btn-outline-dark" onclick="displayEditInventory('+  "'" + e.barcode + "'" + ')">edit</button>&nbsp;';
        var dateUTC = new Date(e.date);
                    var dateUTC = dateUTC.getTime()
                    var dateIST = new Date(dateUTC);
                    dateIST.setHours(dateIST.getHours() + 5);
                    dateIST.setMinutes(dateIST.getMinutes() + 30);

		var row = '<tr>'
		+ '<td>' + sno + '</td>'
		+ '<td>' + e.productName + '</td>'
		+ '<td>' + e.barcode + '</td>'
		+ '<td>'  + e.quantity + '</td>'
		+ '<td>'  + e.total + '</td>'
		+ '<td>'  + dateIST + '</td>'
		+ '</tr>';
        $tbody.append(row);
        sno+=1;
	}
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

            fillBrandOptionUtil(data);
        }
    })
}


function getBrandByCategory(event){
    var category = event.target.value;


    var brand = $("#brand").val();

    if(category==='Select Category' && brand==='Select Brand')
        {
            fillBrandOption();
            fillCategoryOption();
            return;
        }

    if(brand!=='Select Brand')
    {
        return;
    }

    $.ajax({
         url:$("meta[name=baseUrl]").attr("content") + "/api/brand/category/" + category,
                type:'GET',
                headers:{
                    'Content-type':'application/json'

                },
                success:function(data){
                    var brandOption = $(".append-brand");
                    brandOption[0].innerHTML = "";

                    brandOption.append(`<option selected>Select Brand</option>`);

                         for(var i=0;i<data.length;i++)
                         {
                            brandOption.append(`<option val="${data[i]}">${data[i]}</option>`);
                         }
                }

    })

}


function getCategoryByBrand(event){

    var brand = event.target.value;

    var category = $("#category").val();

    if(category==='Select Category' && brand==='Select Brand')
        {
            fillBrandOption();
            fillCategoryOption();
            return;
        }

    if(category!=='Select Category')
    {
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



function init(){
    $("#download-sales").click(downloadSales);
    fillCategoryOption();
    fillBrandOption();
    $("#category").on('change',getBrandByCategory);
        $("#brand").on('change',getCategoryByBrand);


}





$(document).ready(init);