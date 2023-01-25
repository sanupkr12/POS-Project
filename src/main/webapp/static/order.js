
function getOrderUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content");
    return baseUrl + "/api/order";
}


var index = 1;

function numberWithCommas(x) {
    return x.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
}


var orderForm = document.querySelector("#inputOrder");

function getElementFromString(string){
    let div = document.createElement("div");
    div.innerHTML=string;
    return div.firstChild;

}

function removeElement(ind){
    document.getElementById("index-" + ind).remove();
}






function addOrder(event){
    var orderItems = [];
    let orderList = document.querySelectorAll('.items');

    for(var i=0;i<orderList.length;i++)
    {
        let item = orderList[i];

        var barcode = item.querySelector(".new-barcode").innerText;
        var quantity =  parseInt(item.querySelector(".new-quantity").innerText);
        var price = parseInt(item.querySelector(".new-sellingPrice").innerText);

        var orderItem = {};
        orderItem["barcode"] = barcode;
        orderItem["quantity"] = quantity;
        orderItem["sellingPrice"] = price;

        orderItems.push(orderItem);
    }

    var json = JSON.stringify(orderItems);



    var url = getOrderUrl();

    $.ajax({
    	   url: url,
    	   type: 'POST',
    	   data: json,
    	   headers: {
           	'Content-Type': 'application/json'
           },
    	   success: function(response) {
    	        $('#create-order-modal').modal('toggle');

    	   		    $.notify("Order Placed Successfully","success");

                    getOrder();


    	   },
    	   error: function(response){

                  	        handleAjaxError(response);
                  	   }
    	});


    	return false;


}









function displayOrders(data){
    var $tbody = $('#order-table').find('tbody');
    	$tbody.empty();
    	for(var i in data){
    		var e = data[i];
    		var dateUTC = new Date(e.date);
            var dateUTC = dateUTC.getTime();
            var dateIST = new Date(dateUTC);
            dateIST.setHours(dateIST.getHours() + 5);
            dateIST.setMinutes(dateIST.getMinutes() + 30);
//
               var buttonHtml = '<a style="background-color:transparent;border:0;" title="Details"  href="http://localhost:9000/pos/ui/order/' + e.id + '"><img style="height:1.6rem;width:1.6rem;" src="' + $("meta[name=baseUrl]").attr("content") + "/static/info.png" + '"></a>'
    		var row ='<tr>'
    		+ '<td>' + e.id + '</td>'
    		+ '<td>' + dateIST + '</td>'
    		+ '<td style="text-align:end">'  + numberWithCommas(e.total) + '</td>'
    		+ '<td class="text-center">'  + buttonHtml + '</td>'
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


function createOrder(){
    $('#order-create-form input[name=barcode]').val("");
    $('#order-create-form input[name=quantity]').val("");
    $('#create-order-modal').modal('toggle');
}

function displayOrderItems(){
    var $tbody = $('#orderItemTable').find('tbody');
    	$tbody.empty();
    	for(var i=0;i<OrderItems.length;i++){
    		var e = data[i];
    		var buttonHtml = ' <button class="btn btn-outline-dark" onclick="displayEditInventory('+  "'" + e.barcode + "'" + ')">edit</button>&nbsp;';
    		buttonHtml += '<button class="btn btn-outline-danger" onclick="deleteInventory(' +  "'" + e.barcode + "'" + ')">delete</button>'

    		var row = '<tr>'
    		+ '<td>' + e.barcode + '</td>'
    		+ '<td>' + e.name + '</td>'
    		+ '<td>'  + e.quantity + '</td>'
    		+ '<td>' + buttonHtml + '</td>'
    		+ '</tr>';
            $tbody.append(row);
    	}
}

function removeFromModal(e){
    e.target.parentElement.parentElement.parentElement.parentElement.remove();
}


function addToOrderList(event){
    event.preventDefault();
    var barcode = $('#order-create-form input[name=barcode]').val();
    var quantity = $('#order-create-form input[name=quantity]').val();
    var sellingPrice = $('#order-create-form input[name=sellingPrice]').val();

    if(barcode==='' || quantity==='' || sellingPrice===0)
    {
       alert('Invalid Data Entry');
    }

    var ele = document.querySelector(`#barcode-${barcode}`);



    if(!ele)
    {

    var $tbody = $('#orderItemTable').find('tbody');

    var buttonHtml = '<button style="border:0;background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" title="Delete" onclick=removeFromModal(event)><i class="fa fa-trash fa-lg"></i></button>&nbsp;';
        		var row = '<tr class="items">'
        		+ '<td class="new-barcode">' + barcode + '</td>'
        		+ '<td class="new-quantity" id="barcode-' + barcode + '">' + quantity + '</td>'
        		+ '<td class="new-sellingPrice">' + sellingPrice + '</td>'
        		+ '<td>' + buttonHtml + '</td>'
        		+ '</tr>';
    $tbody.append(row);


    }
    else{

        var prevQuantity = parseInt(ele.innerText);
        ele.innerHTML = prevQuantity + parseInt(quantity);
    }

     $('#order-create-form input[name=barcode]').val("");
        $('#order-create-form input[name=quantity]').val("");
        $('#order-create-form input[name=sellingPrice]').val("");

}

function init(){
$("#order-link").addClass('active');
        $("#add-order").click(addOrder);


        getOrder();
        $('#create-order').click(createOrder);
        $('#addToOrderList').click(addToOrderList);


}


$(document).ready(init);