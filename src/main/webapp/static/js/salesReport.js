function init() {
    addCalendarValidation();
    $('#display-sales-table').hide();
    $("#download-sales").click(downloadSales);
    fillCategoryOption();
    $("#category").on('change', getBrandByCategory);
}
$(document).ready(init);
function getCurrentUrl() {
    let baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/report/sales";
}
function downloadSales(event) {
    event.preventDefault();
    let category = $("#category").val();
    let brand = $("#brand").val();
    if (category == "Select Category") {
        category = "";
    }
    if (brand == "Select Brand") {
        brand = "";
    }
    let startDate = $("#start-date").val();
    let endDate = $("#end-date").val();
    if (Date.parse(startDate) > Date.parse(endDate)) {
        handleErrorNotification("Start Date Cannot be greater than End Date");
        return;
    }
    let details = {};
    details["category"] = category;
    details["brand"] = brand;
    details["startDate"] = startDate;
    details["endDate"] = endDate;
    let json = JSON.stringify(details);
    $.ajax({
        url: getCurrentUrl(),
        type: "POST",
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function (response) {
            displaySalesList(response);
        },
        error: handleAjaxError
    });
    return false;
}
function displaySalesList(data) {
    if (data.length === 0) {
        $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify("No Results to display", "info");
        let $tbody = $('#display-sales-table').find('tbody');
        $tbody.empty();
        $('#display-sales-table').hide();
        return;
    }
    $('#display-sales-table').show();
    let sno = 1;
    let $tbody = $('#display-sales-table').find('tbody');
    $tbody.empty();
    for (let i in data) {
        let e = data[i];
        let buttonHtml = ' <button class="btn btn-outline-dark" onclick="displayEditInventory(' + "'" + e.barcode + "'" + ')">edit</button>&nbsp;';
        let dateUTC = new Date(e.date);
        dateUTC = dateUTC.getTime()
        let dateIST = new Date(dateUTC);
        dateIST.setHours(dateIST.getHours() + 5);
        dateIST.setMinutes(dateIST.getMinutes() + 30);
        let row = '<tr>'
            + '<td>' + sno + '</td>'
            + '<td>' + e.brand + '</td>'
            + '<td>' + e.category + '</td>'
            + '<td>' + numberWithCommas(e.quantity) + '</td>'
            + '<td style="text-align:end">' + numberWithCommas(e.total.toFixed(2)) + '</td>'
            + '</tr>';
        $tbody.append(row);
        sno += 1;
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
            brandOption[0].innerHTML = "";
            data.sort();
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
    if (category != 'Select Category') {
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
function addCalendarValidation() {
    let dtToday = new Date();
    let month = dtToday.getMonth() + 1;
    let day = dtToday.getDate();
    let year = dtToday.getFullYear();
    if (month < 10)
        month = '0' + month.toString();
    if (day < 10)
        day = '0' + day.toString();
    let maxDate = year + '-' + month + '-' + day;
    $('#start-date').attr('max', maxDate);
    $('#end-date').attr('max', maxDate);
}
