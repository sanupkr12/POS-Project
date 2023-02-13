$(document).ready(init);

function init() {
	$("#report-link").addClass('active');
}
function getCurrentUrl() {
	let baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/report";
}
