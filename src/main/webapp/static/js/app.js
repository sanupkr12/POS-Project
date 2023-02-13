$(document).ready(init);

function init() {
    numberValidation();
}

function numberValidation() {
    let ele = document.querySelectorAll('.num-input');
    if (ele.length === 0) {
        return;
    }
    else {
        for (let i = 0; i < ele.length; i++) {
            ele[i].onkeypress = function (evt) {
                if (evt.which != 8 && evt.which != 0 && evt.which != 46 && evt.which < 48 || evt.which > 57) {
                    evt.preventDefault();
                }
            };
        }
    }
}
function numberWithCommas(x) {
    return x.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
}
//HELPER METHOD
function toJson($form) {
    let serialized = $form.serializeArray();
    console.log(serialized);
    let s = '';
    let data = {};
    for (s in serialized) {
        data[serialized[s]['name']] = serialized[s]['value']
    }
    let json = JSON.stringify(data);
    return json;
}
function handleSuccessNotification(message) {
    $('.notifyjs-wrapper').trigger('notify-hide');
    $.notify(message, 'success');
}
function handleErrorNotification(message) {
    $('.notifyjs-wrapper').trigger('notify-hide');
    $.notify.defaults({ clickToHide: true, autoHide: false });
    $.notify(message + " ❌️", 'error');
}
function handleAjaxError(response) {
    response = JSON.parse(response.responseText);
    handleErrorNotification(response.message);

}
function readFileData(file, callback) {
    let config = {
        header: true,
        delimiter: "\t",
        skipEmptyLines: "greedy",
        complete: function (results) {
            callback(results);
        }
    }
    Papa.parse(file, config);
}
function writeFileData(arr) {
    let config = {
        quoteChar: '',
        escapeChar: '',
        delimiter: "\t"
    };
    let data = Papa.unparse(arr, config);
    let blob = new Blob([data], { type: 'text/tsv;charset=utf-8;' });
    let fileUrl = null;
    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'download.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    let tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'download.tsv');
    tempLink.click();
    tempLink.remove();
}

function makeAjaxCall(url,type,data,success,error){
    if(type==='GET' || type==='DELETE')
    {
        $.ajax({
            url: url,
            type: type,
            headers: {
                'Content-Type': 'application/json'
            },
            success: success,
            error: error
        });
    }
    else{
       $.ajax({
                url: url,
                type: type,
                data: data,
                headers: {
                    'Content-Type': 'application/json'
                },
                success: success,
                error: error
            });
    }
}









