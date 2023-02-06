$(document).ready(init);
function init() {
    getOrder();
    handleEditOrder();
    $("#update-order-item").click(updateOrderItem);
    $("#invoice").click(generateInvoice);
    setTimeout(handleEditOrder, 100);
}
function getOrderUrl() {
    let baseUrl = $("meta[name=baseUrl]").attr("content");
    let id = window.location.href.split("/").pop();
    return baseUrl + "/api/order/" + id;
}
let update_id;
function updateOrderItem(event) {
    $('#edit-order-modal').modal('toggle');
    let url = $("meta[name=baseUrl]").attr("content") + "/api/order/item/" + update_id;
    //Set the values to update
    let $form = $("#order-edit-form");
    let json = toJson($form);
    $.ajax({
        url: url,
        type: 'PUT',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function (response) {
            location.reload();
        },
        error: handleAjaxError
    });
    return false;
}
function displayEditOrderItem(id) {
    let url = $("meta[name=baseUrl]").attr("content") + "/api/order/item/" + id;
    update_id = id;
    $.ajax({
        url: url,
        type: 'GET',
        success: function (data) {
            displayEditOrder(data);
        },
        error: handleAjaxError
    });
}
function displayEditOrder(data) {
    $("#order-edit-form input[name=barcode]").val(data.barcode);
    $("#order-edit-form input[name=quantity]").val(data.quantity);
    $('#edit-order-modal').modal('toggle');
}
function displayOrders(data) {
    let $tbody = $('#order-item-table').find('tbody');
    $tbody.empty();
    if (data.length === 0) {
        return;
    }
    $("#orderId")[0].innerText = data[0].id;
    for (let i in data) {
        let e = data[i];
        let dateUTC = new Date(e.date);
        dateUTC = dateUTC.getTime()
        let dateIST = new Date(dateUTC);
        dateIST.setHours(dateIST.getHours() + 5);
        dateIST.setMinutes(dateIST.getMinutes() + 30);
        let buttonHtml = '<button style="background-color:transparent;border:0;padding:0.5rem;border-radius:0.3rem;" class="edit-btn" onclick="displayEditOrderItem(' + e.itemId + ')"><i class="fa fa-edit fa-lg"></i></button>'
        let row = '<tr>'
            + '<td>' + e.barcode + '</td>'
            + '<td>' + e.productName + '</td>'
            + '<td>' + numberWithCommas(e.quantity) + '</td>'
            + '<td style="text-align:end">' + numberWithCommas(e.total.toFixed(2)) + '</td>'
            + '<td>' + String(dateIST).slice(0, 25) + '</td>'
            + '<td>' + buttonHtml + '</td>'
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
function handleEditOrder() {
    let url = $("meta[name=baseUrl]").attr("content") + "/api/order/invoice/" + window.location.href.split("/").pop();
    $.ajax({
        url: url,
        type: 'GET',
        headers: {
            'Content-Type': 'application/json'
        },
        success: function (data) {
            if (data.invoiceGenerated) {
                let elements = document.querySelectorAll(".edit-btn");
                for (let i = 0; i < elements.length; i++) {
                    elements[i].setAttribute("disabled", "disabled");
                }
            }
        },
        error: handleAjaxError
    })
}
function generateInvoice() {
    let id = window.location.href.split("/").pop();
    let url = $("meta[name=baseUrl]").attr("content") + "/api/order/invoice/" + id;
    let req = new XMLHttpRequest();
    req.open("POST", url, true);
    req.responseType = "blob";
    req.onload = function (event) {
        let blob = req.response;
        $.notify("Invoice generated!", "success");
        let link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = `invoice${id}.pdf`;
        link.click();
        handleEditOrder();
    };
    req.send();
}
