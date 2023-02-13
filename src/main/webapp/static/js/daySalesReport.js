const daySalesTable = $('#display-daySales-table');

$(document).ready(init);

function init() {
	getDaySalesReport();
}

function displayDaySales(data) {
	let $tbody = daySalesTable.find('tbody');
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
			<td>${String(dateIST).slice(0, 25)}</td>
			<td>${e.orderCount}</td>
			<td>${e.itemCount}</td>
			<td class="text-right">${numberWithCommas(e.revenue.toFixed(2))}</td>
			</tr>`;
		$tbody.append(row);
	}
}

function getDaySalesReport() {
	let url = $("meta[name=baseUrl]").attr("content") + "/api/report/daySales";
	makeAjaxCall(url,'GET',{},(data)=>{
        displayDaySales(data);
    },handleAjaxError)
}


