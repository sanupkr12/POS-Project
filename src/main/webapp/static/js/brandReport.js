$(document).ready(init);

function init() {
    $("#display-brand-table").hide();
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
    $.ajax({
        url: getCurrentUrl(),
        type: "POST",
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function (response) {
            displayBrandList(response);
        },
        error: handleAjaxError

    });
    return false;
}

function displayBrandList(data) {
    if (data.length === 0) {
        $('.notifyjs-wrapper').trigger('notify-hide');
        $.notify("No Results to display", "info");
        $("#display-brand-table").hide();
        return;
    }
    $("#display-brand-table").show();
    let $tbody = $('#display-brand-table').find('tbody');
    $tbody.empty();
    let j = 1;
    for (let i in data) {
        let e = data[i];
        let row = '<tr>'
            + '<td>' + j + '</td>'
            + '<td>' + e.name + '</td>'
            + '<td>' + e.category + '</td>'
            + '</tr>';
        $tbody.append(row);
        j += 1;
    }
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
    data.sort();
    brandOption[0].innerHTML = "";
    brandOption.append(`<option selected>Select Brand</option>`);
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

