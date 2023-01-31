function getCurrentUrl(){
    	var baseUrl = $("meta[name=baseUrl]").attr("content")
    	return baseUrl + "/api/report/sales";

}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
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

    if(Date.parse(startDate)>Date.parse(endDate))
    {
        handleErrorNotification("Start Date Cannot be greater than End Date");
        return;
    }



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

    if(data.length===0)
    {
        $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify("No Results to display","info");
        var $tbody = $('#display-sales-table').find('tbody');
        $tbody.empty();
        $('#display-sales-table').hide();
        return;
    }

    $('#display-sales-table').show();
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
		+ '<td>' + e.brand + '</td>'
		+ '<td>' + e.category + '</td>'
		+ '<td>'  + numberWithCommas(e.quantity) + '</td>'
		+ '<td style="text-align:end">'  + numberWithCommas(e.total.toFixed(2)) + '</td>'
		+ '</tr>';
        $tbody.append(row);
        sno+=1;
	}
}

function fillCategoryOptionUtil(data){
     var categoryOption = $(".append-category");
     data.sort();
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
//    var brand = $("#brand").val();
//    if(category==='Select Category' && brand==='Select Brand')
//    if(category==='Select Category')
//    {
//        fillBrandOption();
//        fillCategoryOption();
//        return;
//    }

//    if(brand!='Select Brand')
//    {
//
//        return;
//    }

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


function getCategoryByBrand(event){
    var brand = event.target.value;
    var category = $("#category").val();

    if(category==='Select Category' && brand==='Select Brand')
    {
            fillBrandOption();
            fillCategoryOption();
            return;
    }


    if(category!='Select Category')
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
function addCalendarValidation(){
    var dtToday = new Date();

    var month = dtToday.getMonth() + 1;
    var day = dtToday.getDate();
    var year = dtToday.getFullYear();

    if(month < 10)
        month = '0' + month.toString();
    if(day < 10)
        day = '0' + day.toString();

    var maxDate = year + '-' + month + '-' + day;

    $('#start-date').attr('max', maxDate);
    $('#end-date').attr('max', maxDate);
}


function init(){
    addCalendarValidation();
    $('#display-sales-table').hide();
    $("#download-sales").click(downloadSales);
    fillCategoryOption();
//    fillBrandOption();
    $("#category").on('change',getBrandByCategory);
//    $("#brand").on('change',getCategoryByBrand);

}





$(document).ready(init);