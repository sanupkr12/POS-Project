let update_id;

//Global Variables
const orderItemTable = $('#order-item-table');
const editOrderModal = $('#edit-order-modal');
const $editOrderForm = $('#order-edit-form');

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

function updateOrderItem(event) {
    editOrderModal.modal('toggle');
    let url = $("meta[name=baseUrl]").attr("content") + "/api/order/item/" + update_id;
    //Set the values to update
    let json = toJson($editOrderForm);
    makeAjaxCall(url,'PUT',json,(response)=>{
        location.reload();
    },handleAjaxError);
    return false;
}
function displayEditOrderItem(id) {
    let url = $("meta[name=baseUrl]").attr("content") + "/api/order/item/" + id;
    update_id = id;
    makeAjaxCall(url,'GET',{},(data)=>{
      displayEditOrder(data);
    },handleAjaxError);
}
function displayEditOrder(data) {
    $editOrderForm.find("input[name=barcode]").val(data.barcode);
    $editOrderForm.find("input[name=quantity]").val(data.quantity);
    editOrderModal.modal('toggle');
}
function displayOrders(data) {
    let $tbody = orderItemTable.find('tbody');
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
        let row = `<tr>
            <td>${e.barcode} </td>
            <td>${e.productName} </td>
            <td>${numberWithCommas(e.quantity)} </td>
            <td style="text-align:end">${numberWithCommas(e.total.toFixed(2))} </td>
            <td>${String(dateIST).slice(0, 25)} </td>
            <td>${buttonHtml}</td>
            </tr>`;
        $tbody.append(row);
    }
}
function getOrder() {
    let url = getOrderUrl();
    makeAjaxCall(url,'GET',{},(data)=>{
       displayOrders(data);
    },handleAjaxError);
}
function handleEditOrder() {
    let url = $("meta[name=baseUrl]").attr("content") + "/api/order/invoice/" + window.location.href.split("/").pop();
    makeAjaxCall(url,'GET',{},(data)=>{
      if (data.invoiceGenerated) {
          let elements = document.querySelectorAll(".edit-btn");
          for (let i = 0; i < elements.length; i++) {
              elements[i].setAttribute("disabled", "disabled");
          }
      }
    },handleAjaxError);
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
