$(document).ready(init);

function init() {
	getDaySalesReport();
}

function displayDaySales(data) {
	let sno = 1;
	let $tbody = $('#display-daySales-table').find('tbody');
	$tbody.empty();
	for (let i in data) {
		let e = data[i];
		let dateUTC = new Date(e.date);
		dateUTC = dateUTC.getTime()
		let dateIST = new Date(dateUTC);
		dateIST.setHours(dateIST.getHours() + 5);
		dateIST.setMinutes(dateIST.getMinutes() + 30);
		let row = '<tr>'
			+ '<td>' + sno + '</td>'
			+ '<td>' + String(dateIST).slice(0, 25) + '</td>'
			+ '<td>' + e.orderCount + '</td>'
			+ '<td>' + e.itemCount + '</td>'
			+ '<td class="text-right">' + numberWithCommas(e.revenue.toFixed(2)) + '</td>'
			+ '</tr>';
		$tbody.append(row);
		sno += 1;
	}
}

function getDaySalesReport() {
	let url = $("meta[name=baseUrl]").attr("content") + "/api/report/daySales";
	$.ajax({
		url: url,
		type: 'GET',
		success: function (data) {
			displayDaySales(data);
		},
		error: handleAjaxError
	});
}


