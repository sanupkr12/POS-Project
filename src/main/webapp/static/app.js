
//HELPER METHOD
function toJson($form){
    var serialized = $form.serializeArray();
    console.log(serialized);
    var s = '';
    var data = {};
    for(s in serialized){
        data[serialized[s]['name']] = serialized[s]['value']
    }
    var json = JSON.stringify(data);
    return json;
}

function handleSuccessNotification(message){
    $('.notifyjs-wrapper').trigger('notify-hide');
    $.notify(message,'success');
}

function handleErrorNotification(message){

    $('.notifyjs-wrapper').trigger('notify-hide');
    $.notify.defaults( {clickToHide:true,autoHide:false} );
    $.notify(message + " ❌️",'error');

}

function handleAjaxError(response){
	var response = JSON.parse(response.responseText);
	handleErrorNotification(response.message);

}

function readFileData(file, callback){
	var config = {
		header: true,
		delimiter: "\t",
		skipEmptyLines: "greedy",
		complete: function(results) {
			callback(results);
	  	}	
	}
	Papa.parse(file, config);
}


function writeFileData(arr){
	var config = {
		quoteChar: '',
		escapeChar: '',
		delimiter: "\t"
	};
	
	var data = Papa.unparse(arr, config);
    var blob = new Blob([data], {type: 'text/tsv;charset=utf-8;'});
    var fileUrl =  null;

    if (navigator.msSaveBlob) {
        fileUrl = navigator.msSaveBlob(blob, 'download.tsv');
    } else {
        fileUrl = window.URL.createObjectURL(blob);
    }
    var tempLink = document.createElement('a');
    tempLink.href = fileUrl;
    tempLink.setAttribute('download', 'download.tsv');
    tempLink.click(); 
}

function numberValidation(){
    var ele = document.querySelectorAll('.num-input');
        if(ele.length===0)
        {
            return;
        }
        else{
            for(var i=0;i<ele.length;i++)
            {
                ele[i].onkeypress = function (evt) {
                    if (evt.which != 8 && evt.which != 0 && evt.which!=46 && evt.which < 48 || evt.which > 57)
                    {
                        evt.preventDefault();
                    }
                 };
            }
        }
}


function init(){
    numberValidation();
}

$(document).ready(init);


