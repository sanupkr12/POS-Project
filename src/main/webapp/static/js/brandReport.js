const brandTable = $("#display-brand-table");

$(document).ready(init);

function init() {
    brandTable.hide();
    $("#download-brand").click(downloadBrand);
    fillCategoryOption();
    $("#category").on('change', getBrandByCategory);
}

function getCurrentUrl() {
    let baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/report/brand";
}

function downloadBrand(event) {
    event.preventDefault();
    let category = $("#category").val();
    let brand = $("#brand").val();
    if (category === "Select Category") {
        category = "";
    }
    if (brand === "Select Brand") {
        brand = "";
    }
    let details = {};
    details["category"] = category;
    details["brand"] = brand;
    let json = JSON.stringify(details);
    let url = getCurrentUrl();
    makeAjaxCall(url,'POST',json,(response)=> {
         displayBrandList(response);
    },handleAjaxError);
    return false;
}

function displayBrandList(data) {
    if (data.length === 0) {
        $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify("No Results to display", "info");
        brandTable.hide();
        return;
    }
    brandTable.show();
    let $tbody = brandTable.find('tbody');
    $tbody.empty();
    for (let i in data) {
        let e = data[i];
        let row = `<tr>
            <td>${parseInt(+i+1)}</td>
            <td>${e.name}</td>
            <td>${e.category}</td>
            </tr>`;
        $tbody.append(row);
    }
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

function fillCategoryOptionUtil(data) {
    let categoryOption = $(".append-category");
    categoryOption[0].innerHTML = "";
    data.sort();
    categoryOption.append(`<option selected>Select Category</option>`);
    categoryOption.append(`<option val="all">all</option>`);

    for (let i = 0; i < data.length; i++) {
        categoryOption.append(`<option val="${data[i]}">${data[i]}</option>`);
    }
}


function fillCategoryOption() {
    let url = $("meta[name=baseUrl]").attr("content") + "/api/brand/category"
    makeAjaxCall(url,'GET',{},(data)=> {
      fillCategoryOptionUtil(data);
    },handleAjaxError);
}

function fillBrandOptionUtil(data) {
    let brandOption = $(".append-brand");
    data.sort();
    brandOption[0].innerHTML = "";
    brandOption.append(`<option selected>Select Brand</option>`);
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



