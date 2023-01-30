function getOrderUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content");
    var id = window.location.href.split("/").pop();

    return baseUrl + "/api/order/" + id;
}

var update_id;

function numberWithCommas(x) {
    return x.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
}

function updateOrderItem(event){
	$('#edit-order-modal').modal('toggle');


	var url = $("meta[name=baseUrl]").attr("content") + "/api/order/item/" + update_id;

	//Set the values to update
	var $form = $("#order-edit-form");
	var json = toJson($form);


	$.ajax({
	   url: url,
	   type: 'PUT',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		location.reload();
	   },
	   error: handleAjaxError
	});

	return false;
}

function displayEditOrderItem(id){

	var url = $("meta[name=baseUrl]").attr("content") + "/api/order/item/" + id;

    update_id = id;

	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayEditOrder(data);
	   },
	   error: handleAjaxError
	});
}

function displayEditOrder(data){

	$("#order-edit-form input[name=barcode]").val(data.barcode);
	$("#order-edit-form input[name=quantity]").val(data.quantity);
	$('#edit-order-modal').modal('toggle');
}


function displayOrders(data){
    var $tbody = $('#order-item-table').find('tbody');
    	$tbody.empty();
    	for(var i in data){
    		var e = data[i];
    		var dateUTC = new Date(e.date);
            var dateUTC = dateUTC.getTime()
            var dateIST = new Date(dateUTC);
            dateIST.setHours(dateIST.getHours() + 5);
            dateIST.setMinutes(dateIST.getMinutes() + 30);

            var buttonHtml = '<button style="background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" class="edit-btn" onclick="displayEditOrderItem(' + e.itemId + ')"><i class="fa fa-edit fa-lg"></i></button>'
    		var row ='<tr>'
    		+ '<td>' + e.id + '</td>'
    		+ '<td>' + e.barcode + '</td>'
    		+ '<td>' + e.productName + '</td>'
    		+ '<td>' + numberWithCommas(e.quantity) + '</td>'
    		+ '<td style="text-align:end">'  + numberWithCommas(e.total.toFixed(2)) + '</td>'
    		+ '<td>' + dateIST + '</td>'
    		+ '<td>' + buttonHtml + '</td>'
    		+ '</tr>';
            $tbody.append(row);
    	}




}



function getOrder(){
    var url = getOrderUrl();


    $.ajax({
        url:url,
        type:'GET',
        headers: {
                       	'Content-Type': 'application/json'
                       },
                	   success: function(data) {
                	   		displayOrders(data);

                	   },
        error:handleAjaxError

    })

}

function handleEditOrder(){

    var url = $("meta[name=baseUrl]").attr("content") + "/api/order/invoice/" + window.location.href.split("/").pop();

    $.ajax({
    url:url,
    type:'GET',
    headers: {
                               	'Content-Type': 'application/json'
                               },
                        	   success: function(data) {

                        	   		if(data.invoiceGenerated)
                        	   		{
                        	   		    let elements = document.querySelectorAll(".edit-btn");
                                        for(var i=0;i<elements.length;i++)
                                        {
                                            elements[i].setAttribute("disabled","disabled");
                                        }
                        	   		}

                        	   },
                error:handleAjaxError

    })


}


function generateInvoice(){
    var id = window.location.href.split("/").pop();
    var url = $("meta[name=baseUrl]").attr("content") + "/api/order/invoice/" + id;

    var req = new XMLHttpRequest();
        req.open("POST",url, true);
        req.responseType = "blob";

        req.onload = function (event) {
          var blob = req.response;
          $.notify("Invoice generated!", "success");

          var link=document.createElement('a');
          link.href=window.URL.createObjectURL(blob);
          link.download=`invoice${id}.pdf`;
          link.click();

          handleEditOrder();
        };

        req.send();

//    $.ajax({
//            url:url,
//            type:'GET',
//            headers: {
//                           	'Content-Type': 'application/json'
//                           },
//                    	   success: function(data) {
//                    	        $(".toast-body").html("Invoice Generated");
//                                $(".message").html("success");
//                                $(".toast").toast("show");
//
//
//
//
//
//                    	   },
//            error:handleAjaxError
//
//        }) ;
}



function init(){
     getOrder();
     handleEditOrder();
     $("#update-order-item").click(updateOrderItem);
     $("#invoice").click(generateInvoice);
        setTimeout(handleEditOrder, 100);



}



$(document).ready(init);