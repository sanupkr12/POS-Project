
function getOrderUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content");
    return baseUrl + "/api/order";
}


var index = 1;


var orderForm = document.querySelector("#inputOrder");

function getElementFromString(string){
    let div = document.createElement("div");
    div.innerHTML=string;
    return div.firstChild;

}

function removeElement(ind){
    document.getElementById("index-" + ind).remove();
}


document.querySelector("#add-btn").addEventListener('click',function(e){
        e.preventDefault();
        var formHtml = `<div class="row" id=${"index-"+index} style="margin:2rem 0rem">
                                <div class="col-sm-3 form-group" style="padding-left:0rem">
                                <label for=${"barcode-" + index} class="form-label">Barcode</label>
                                <input type="text" name="barcode" id=${"barcode-" + index} class="form-control" placeholder="Enter Barcode">
                                </div>
                                <div class="col-sm-3 form-group" >
                                        <label for=${"barcode-" + index} class="form-label">Quantity</label>
                                        <input type="number" name="quantity" id=${"quantity-" + index} class="form-control" placeholder="Enter Quantity">
                                        </div>
                                        <div class="col-sm-3 form-group" >
                                                <label for=${"barcode-" + index} class="form-label">Selling Price</label>
                                                <input type="number" name="sellingPrice" id=${"price-" + index} class="form-control" placeholder="Enter Selling Price">
                                                </div>
                                        <div class="col-sm-3">
                                                                                       <button id="remove-btn" onclick=${"removeElement(" + index + ")"} type="button" class="btn btn-warning" style="margin-top:1.5rem;"> &nbsp; - &nbsp; </button></div>
                                </div>
                                               `;

        orderForm.appendChild(getElementFromString(formHtml));
        index+=1;
 });





function addOrder(event){
    var orderItems = [];

    for(var i=1;i<index;i++)
    {

        var barcode = $(`${"#barcode-" + i}`).val();
        var quantity = $(`${"#quantity-" + i}`).val();
        var price = $(`${"#price-" + i}`).val();

        if(!barcode)
        {
            continue;
        }
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
    	   		alert("Order Successfully placed");
    	   		location.reload();
    	   },
    	   error: handleAjaxError
    	});


    	return false;


}









function displayOrders(data){
    var $tbody = $('#order-table').find('tbody');
    	$tbody.empty();
    	for(var i in data){
    		var e = data[i];
    		var dateUTC = new Date(e.date);
            var dateUTC = dateUTC.getTime()
            var dateIST = new Date(dateUTC);
            dateIST.setHours(dateIST.getHours() + 5);
            dateIST.setMinutes(dateIST.getMinutes() + 30);
//            var buttonHtml = '<button onclick="getOrderItem(' + e.id + ')">Details</button>'
               var buttonHtml = '<a href="http://localhost:9000/pos/ui/order/' + e.id + '">Details</a>'
    		var row ='<tr>'
    		+ '<td>' + e.id + '</td>'
    		+ '<td>' + dateIST + '</td>'
    		+ '<td>'  + e.total + '</td>'
    		+ '<td>'  + buttonHtml + '</td>'
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

function init(){
        $("#add-order").click(addOrder);
        var formHtml = `<div class="row" id=${"index-"+index} style="margin:2rem 0rem;">
        <div class="col-sm-3 form-group" style="padding-left:0rem">
        <label for=${"barcode-" + index} class="form-label">Barcode</label>
        <input type="text" name="barcode" id=${"barcode-" + index} class="form-control" placeholder="Enter Barcode">
        </div>
        <div class="col-sm-3 form-group" >
                <label for=${"barcode-" + index} class="form-label">Quantity</label>
                <input type="number" name="quantity" id=${"quantity-" + index} class="form-control" placeholder="Enter Quantity">
                </div>
                <div class="col-sm-3 form-group">
                        <label for=${"barcode-" + index} class="form-label">Selling Price</label>
                        <input type="number" name="sellingPrice" id=${"price-" + index} class="form-control" placeholder="Enter Selling Price">
                        </div>
        </div>`;
        orderForm.appendChild(getElementFromString(formHtml));
        index+=1;
        getOrder();


}


$(document).ready(init);