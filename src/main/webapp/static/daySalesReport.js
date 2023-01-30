
function numberWithCommas(x) {
    return x.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ",");
}

function displayDaySales(data){
    var sno = 1;
	var $tbody = $('#display-daySales-table').find('tbody');
	$tbody.empty();
	for(var i in data){
		var e = data[i];
        var dateUTC = new Date(e.date);
                            var dateUTC = dateUTC.getTime()
                            var dateIST = new Date(dateUTC);
                            dateIST.setHours(dateIST.getHours() + 5);
                            dateIST.setMinutes(dateIST.getMinutes() + 30);

		var row = '<tr>'
		+ '<td>' + sno + '</td>'
		+ '<td>' + String(dateIST).slice(0,25) + '</td>'
		+ '<td>' + e.orderCount + '</td>'
		+ '<td>'  + e.itemCount + '</td>'
		+ '<td class="text-right">' + numberWithCommas(e.revenue.toFixed(2)) + '</td>'
		+ '</tr>';
        $tbody.append(row);
        sno+=1;
	}
}

function getDaySalesReport(){
	var url = $("meta[name=baseUrl]").attr("content") + "/api/report/daySales";
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayDaySales(data);
	   },
	   error: handleAjaxError
	});
}


function init(){
    getDaySalesReport();
}


$(document).ready(init);