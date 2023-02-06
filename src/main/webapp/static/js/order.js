$(document).ready(init);
function init() {
    $("#order-link").addClass('active');
    $("#add-order").click(addOrder);
    getOrder();
    $('#create-order').click(createOrder);
    $('#addToOrderList').click(addToOrderList);
}
function getOrderUrl() {
    let baseUrl = $("meta[name=baseUrl]").attr("content");
    return baseUrl + "/api/order";
}
let index = 1;
let orderForm = document.querySelector("#inputOrder");
function getElementFromString(string) {
    let div = document.createElement("div");
    div.innerHTML = string;
    return div.firstChild;
}
function removeElement(ind) {
    document.getElementById("index-" + ind).remove();
}
function addOrder(event) {
    let orderItems = [];
    let orderList = document.querySelectorAll('.items');
    if (orderList.length === 0) {
        handleErrorNotification("order cannot be empty");
        return;
    }
    for (let i = 0; i < orderList.length; i++) {
        let item = orderList[i];
        let barcode = item.querySelector(".new-barcode").innerText;
        let quantity = parseInt(item.querySelector(".new-quantity").value);
        let price = parseInt(item.querySelector(".new-sellingPrice").value);
        if (quantity <= 0) {
            handleErrorNotification("Quantity cannot be negative");
            return;
        }
        if (price <= 0) {
            handleErrorNotification("Price cannot be negative");
            return;
        }
        let orderItem = {};
        orderItem["barcode"] = barcode;
        orderItem["quantity"] = quantity;
        orderItem["sellingPrice"] = price;
        orderItems.push(orderItem);
    }
    let json = JSON.stringify(orderItems);
    let url = getOrderUrl();
    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function (response) {
            $('#create-order-modal').modal('toggle');
            handleSuccessNotification("Order Placed Successfully");
            getOrder();
        },
        error: function (response) {
            handleAjaxError(response);
        }
    });
    return false;
}
function displayOrders(data) {
    let $tbody = $('#order-table').find('tbody');
    $tbody.empty();
    for (let i in data) {
        let e = data[i];
        let dateUTC = new Date(e.date);
        dateUTC = dateUTC.getTime();
        let dateIST = new Date(dateUTC);
        dateIST.setHours(dateIST.getHours() + 5);
        dateIST.setMinutes(dateIST.getMinutes() + 30);
        let buttonHtml = '<a style="background-color:transparent;border:0;" title="Details"  href="http://localhost:9000/pos/ui/order/' + e.id + '"><img style="height:1.6rem;width:1.6rem;" src="' + $("meta[name=baseUrl]").attr("content") + "/static/images/info.png" + '"></a>'
        let row = '<tr>'
            + '<td>' + e.id + '</td>'
            + '<td>' + String(dateIST).slice(0, 25) + '</td>'
            + '<td style="text-align:end">' + numberWithCommas(e.total.toFixed(2)) + '</td>'
            + '<td class="text-center">' + buttonHtml + '</td>'
            + '</tr>';
        $tbody.append(row);
    }
}
function getOrder() {
    let url = getOrderUrl();
    $.ajax({
        url: url,
        type: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        success: function (data) {
            displayOrders(data);
        },
        error: handleAjaxError
    })
}
function createOrder() {
    $('#order-create-form input[name=barcode]').val("");
    $('#order-create-form input[name=quantity]').val("");
    $('#create-order-modal').modal('toggle');
}
function displayOrderItems() {
    let $tbody = $('#orderItemTable').find('tbody');
    $tbody.empty();
    for (let i = 0; i < OrderItems.length; i++) {
        let e = data[i];
        let buttonHtml = ' <button class="btn btn-outline-dark" onclick="displayEditInventory(' + "'" + e.barcode + "'" + ')">edit</button>&nbsp;';
        buttonHtml += '<button class="btn btn-outline-danger" onclick="deleteInventory(' + "'" + e.barcode + "'" + ')">delete</button>'
        let row = '<tr>'
            + '<td>' + e.barcode + '</td>'
            + '<td>' + e.name + '</td>'
            + '<td>' + e.quantity + '</td>'
            + '<td>' + buttonHtml + '</td>'
            + '</tr>';
        $tbody.append(row);
    }
}
function removeFromModal(e) {
    e.target.parentElement.parentElement.parentElement.parentElement.remove();
}
function addToOrderList(event) {
    event.preventDefault();
    let barcode = $('#order-create-form input[name=barcode]').val();
    let quantity = $('#order-create-form input[name=quantity]').val();
    let sellingPrice = $('#order-create-form input[name=sellingPrice]').val();
    if (barcode === '' || quantity === '' || quantity <= 0 || sellingPrice === '' || sellingPrice <= 0) {
        handleErrorNotification('Invalid Data Entry');
        return;
    }
    let ele = document.querySelector(`#barcode-${barcode}`);
    if (!ele) {
        let $tbody = $('#orderItemTable').find('tbody');
        let buttonHtml = '<button style="border:0;background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" title="Delete" onclick=removeFromModal(event)><i class="fa fa-trash fa-lg"></i></button>&nbsp;';
        let row = '<tr class="items">'
            + '<td class="new-barcode">' + barcode + '</td>'
            + '<td><input type="number"  class="w-50 new-quantity" step="1" min="1" value="' + quantity + '" id="barcode-' + barcode + '"></td>'
            + '<td ><input type="number" step="0.01" min="1" class="w-50 new-sellingPrice" value="' + sellingPrice + '"></td>'
            + '<td>' + buttonHtml + '</td>'
            + '</tr>';
        $tbody.append(row);
    }
    else {
        let prevQuantity = parseInt(ele.value);
        ele.value = prevQuantity + parseInt(quantity);
    }
    $('#order-create-form input[name=barcode]').val("");
    $('#order-create-form input[name=quantity]').val("");
    $('#order-create-form input[name=sellingPrice]').val("");
}
