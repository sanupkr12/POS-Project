function getOrderUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content");
    var id = window.location.href.split("/").pop();

    return baseUrl + "/api/order/" + id;
}

var update_id;

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

            var buttonHtml = '<button class="btn btn-outline-danger edit-btn" onclick="displayEditOrderItem(' + e.itemId + ')">Edit</button>'
    		var row ='<tr>'
    		+ '<td>' + e.id + '</td>'
    		+ '<td>' + e.barcode + '</td>'
    		+ '<td>' + e.productName + '</td>'
    		+ '<td>' + e.quantity + '</td>'
    		+ '<td>'  + e.total + '</td>'
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
    var url = $("meta[name=baseUrl]").attr("content") + "/api/order/invoice/" + window.location.href.split("/").pop();

    $.ajax({
            url:url,
            type:'PUT',
            headers: {
                           	'Content-Type': 'application/json'
                           },
                    	   success: function(data) {
                    	        alert("Invoice Generated");
                    	   		handleEditOrder();

                    	   },
            error:handleAjaxError

        }) ;
}

function init(){
     getOrder();
     $("#update-order-item").click(updateOrderItem);
     $("#invoice").click(generateInvoice);
        setTimeout(handleEditOrder, 100);



}



$(document).ready(init);