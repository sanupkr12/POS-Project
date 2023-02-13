const displaySalesTable =  $('#display-sales-table');

$(document).ready(init);

function init() {
    addCalendarValidation();
    displaySalesTable.hide();
    $("#download-sales").click(downloadSales);
    fillCategoryOption();
    $("#category").on('change', getBrandByCategory);
}
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
    let url = getCurrentUrl();
    makeAjaxCall(url,'POST',json,(response)=>{
         displaySalesList(response);
    },handleAjaxError);
    return false;
}
function displaySalesList(data) {
    if (data.length === 0) {
        $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify("No Results to display", "info");
        let $tbody = displaySalesTable.find('tbody');
        $tbody.empty();
        displaySalesTable.hide();
        return;
    }
    displaySalesTable.show();
    let $tbody = displaySalesTable.find('tbody');
    $tbody.empty();
    for (let i in data) {
        let e = data[i];
        let dateUTC = new Date(e.date);
        dateUTC = dateUTC.getTime()
        let dateIST = new Date(dateUTC);
        dateIST.setHours(dateIST.getHours() + 5);
        dateIST.setMinutes(dateIST.getMinutes() + 30);
        let row = `<tr>
            <td>${parseInt(+i+1)}</td>
            <td>${e.brand}</td>
            <td>${e.category}</td>
            <td>${numberWithCommas(e.quantity)}</td>
            <td style="text-align:end">${numberWithCommas(e.total.toFixed(2))}</td>
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
    let url = $("meta[name=baseUrl]").attr("content") + "/api/brand/category";
    makeAjaxCall(url,'GET',{},(data)=>{
      fillCategoryOptionUtil(data);
    },handleAjaxError);
}
function fillBrandOptionUtil(data) {
    let brandOption = $(".append-brand");
    for (let i = 0; i < data.length; i++) {
        brandOption.append(`<option value="${data[i]}">${data[i]}</option>`);
    }
    brandOption.append(`<option val="all">all</option>`);
}
function fillBrandOption() {
    let url = $("meta[name=baseUrl]").attr("content") + "/api/brand/list";
    makeAjaxCall(url,'GET',{},(data)=>{
      fillBrandOptionUtil(data);
    },handleAjaxError);
}
function getBrandByCategory(event) {
    let category = event.target.value;
    let url = $("meta[name=baseUrl]").attr("content") + "/api/brand/category/" + category;
    makeAjaxCall(url,'GET',{},(data)=>{
      let brandOption = $(".append-brand");
      brandOption[0].innerHTML = "";
      data.sort();
      brandOption.append(`<option selected>Select Brand</option>`);
      for (let i = 0; i < data.length; i++) {
          brandOption.append(`<option val="${data[i]}">${data[i]}</option>`);
      }
    },handleAjaxError);
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
    let url = $("meta[name=baseUrl]").attr("content") + "/api/brand/list/" + brand;
    makeAjaxCall(url,'GET',{},(data)=>{
      let categoryOption = $(".append-category");
      categoryOption[0].innerHTML = "";
      categoryOption.append(`<option selected>Select Category</option>`);
      for (let i = 0; i < data.length; i++) {
          categoryOption.append(`<option val="${data[i]}">${data[i]}</option>`);
      }
    },handleAjaxError);
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
