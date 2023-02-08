const inventoryTable = $("#display-inventory-table");

$(document).ready(init);

function init() {
    inventoryTable.hide();
    $("#download-inventory").click(downloadInventory);
    fillCategoryOption();
    $("#category").on('change', getBrandByCategory);
}
function getCurrentUrl() {
    let baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/report/inventory";
}
function numberWithCommas(x) {
    return x.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
}
function downloadInventory(event) {
    event.preventDefault();
    let category = $("#category").val();
    let brand = $("#brand").val();
    if (category == "Select Category") {
        category = "";
    }
    if (brand == "Select Brand") {
        brand = "";
    }
    let details = {};
    details["category"] = category;
    details["brand"] = brand;
    let json = JSON.stringify(details);
    let url = getCurrentUrl();
    makeAjaxCall(url,'POST',json,(response)=>{
         displayInventoryList(response);
     },handleAjaxError);
    return false;
}
function displayInventoryList(data) {
    if (data.length === 0) {
        $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify("No Results to display", "info");
        inventoryTable.hide();
        return; 
    }
    inventoryTable.show();
    let $tbody = inventoryTable.find('tbody');
    $tbody.empty();
    for (let i in data) {
        let e = data[i];
        let buttonHtml = ' <button class="btn btn-outline-dark" onclick="displayEditInventory(' + "'" + e.barcode + "'" + ')">edit</button>&nbsp;';
        let row = `<tr>
            <td>${parseInt(+i+1)}</td>
            <td>${e.name}</td>
            <td>${e.barcode}</td>
            <td>${numberWithCommas(e.quantity)}</td>
            </tr>`;
        $tbody.append(row);
    }
}
function fillCategoryOptionUtil(data) {
    let categoryOption = $(".append-category");
    data.sort();
    categoryOption.append(`<option val="all">all</option>`);
    for (let i = 0; i < data.length; i++) {
        categoryOption.append(`<option val="${data[i]}">${data[i]}</option>`);
    }
}
function fillCategoryOption() {
    $.ajax({
        url: $("meta[name=baseUrl]").attr("content") + "/api/brand/category",
        type: 'GET',
        headers: {
            'Content-type': 'application/json'
        },
        success: function (data) {
            fillCategoryOptionUtil(data);
        }
    })
}
function fillBrandOptionUtil(data) {
    let brandOption = $(".append-brand");
    for (let i = 0; i < data.length; i++) {
        brandOption.append(`<option value="${data[i]}">${data[i]}</option>`);
    }
    brandOption.append(`<option val="all">all</option>`);
}
function fillBrandOption() {
    $.ajax({
        url: $("meta[name=baseUrl]").attr("content") + "/api/brand/list",
        type: 'GET',
        headers: {
            'Content-type': 'application/json'
        },
        success: function (data) {
            fillBrandOptionUtil(data);
        }
    })
}
function getBrandByCategory(event) {
    let category = event.target.value;
    $.ajax({
        url: $("meta[name=baseUrl]").attr("content") + "/api/brand/category/" + category,
        type: 'GET',
        headers: {
            'Content-type': 'application/json'
        },
        success: function (data) {
            let brandOption = $(".append-brand");
            data.sort();
            brandOption[0].innerHTML = "";
            brandOption.append(`<option selected>Select Brand</option>`);
            for (let i = 0; i < data.length; i++) {
                brandOption.append(`<option val="${data[i]}">${data[i]}</option>`);
            }
        }
    })
}
function getCategoryByBrand(event) {
    let brand = event.target.value;
    let category = $("#category").val();
    if (category === 'Select Category' && brand === 'Select Brand') {
        fillBrandOption();
        fillCategoryOption();
        return;
    }
    if (category !== 'Select Category') {
        return;
    }
    $.ajax({
        url: $("meta[name=baseUrl]").attr("content") + "/api/brand/list/" + brand,
        type: 'GET',
        headers: {
            'Content-type': 'application/json'
        },
        success: function (data) {
            let categoryOption = $(".append-category");
            categoryOption[0].innerHTML = "";
            categoryOption.append(`<option selected>Select Category</option>`);
            for (let i = 0; i < data.length; i++) {
                categoryOption.append(`<option val="${data[i]}">${data[i]}</option>`);
            }
        }
    })
}
